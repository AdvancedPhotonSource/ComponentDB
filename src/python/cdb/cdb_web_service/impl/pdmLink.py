#!/usr/bin/env python

"""
*********************************
Class that retrieves drawing information from Windchill PDMLink repository.

Available Windchill classes:
    http://windchill-dev.aps.anl.gov/Windchill/infoengine/jsp/tools/doc/index.jsp

********************************TESTING NOTES**********************************
pdmLink = PdmLink('edp','PakTai8','http://windchill-vm.aps.anl.gov/Windchill', 'https://icmsdocs.aps.anl.gov')

pdmLink.getDrawing('D14100201-113160.ASM')
pdmLink.getDrawing('u2210203-101600.drw')
pdmLink.getDrawing('U1340201-102000.DRW')
pdmLink.getDrawing('U221020202-104210.DRW')
*******************************************************************************
"""

from suds.client import Client
from suds.xsd.doctor import Import
from suds.xsd.doctor import ImportDoctor
from suds import WebFault

from cdb.common.objects.pdmLinkDrawing import PdmLinkDrawing 
from cdb.common.objects.pdmLinkDrawingRevision import PdmLinkDrawingRevision
from cdb.common.objects.pdmLinkSearchResult import PdmLinkSearchResult
from cdb.common.objects.pdmLinkComponent import PdmLinkComponent
from cdb.common.db.api.componentDbApi import ComponentDbApi
from cdb.common.objects.image import Image
from cdb.common.utility.loggingManager import LoggingManager
from cdb.common.utility.sslUtility import SslUtility
from cdb.common.exceptions.objectNotFound import ObjectNotFound
from cdb.common.exceptions.externalServiceError import ExternalServiceError
from cdb.common.exceptions.invalidRequest import InvalidRequest
from cdb.common.exceptions.invalidArgument import InvalidArgument
from icms import Icms

class PdmLink:

    WINDCHILL_WEBPARTS_URL = '/servlet/SimpleTaskDispatcher?CLASS=com.ptc.windchill.webparts'
    WINDCHILL_WS_URL = '/servlet/SimpleTaskDispatcher?CLASS=com.ptc.windchill.ws'
    SOAP_ENCODING_SCHEMA_URL = 'http://schemas.xmlsoap.org/soap/encoding/'
    ICMS_USER = 'cdb_soap'
    ICMS_PASS = 'soap$4cdb'


    def __init__(self, username, password, windchillUrl, icmsUrl):
        self.username = username
        self.password = password
        self.windchillUrl = windchillUrl 
        self.icmsUrl = icmsUrl
        self.windchillWs = None
        self.windchillWebparts = None
        self.icmsConnection = None
        self.componentDbApi = None
        self.logger = LoggingManager.getInstance().getLogger(self.__class__.__name__)

        # searchArgs are used in search functions of Windchill webparts class
        # Only keyword needs to be changed when searching for a node. 
        # Example: webparts.service.WindchillSearch(**searchArgs)
        self.searchArgs = {
            'keyword': 'notSet', 
            'START': 0, 'MAX': 1, 
            'SearchTypes': 'WCTYPE|wt.epm.EPMDocument|gov.anl.aps.DefaultEPMDocument',
            'AttributeFilter': '',
            'SortBy': 'name', 
            'SortDirection': 'ASC',
            'clientLocale': 'en-US'
        }

    def __createWindchillClients(self):
        """
        Initialize windchill PDM Link Webparts and ws soap classes
        """
        if self.windchillWs != None and self.windchillWebparts != None:
            return

        # WSDL URLS
        fullWebpartsUrl = self.windchillUrl + self.WINDCHILL_WEBPARTS_URL
        fullWsUrl = self.windchillUrl + self.WINDCHILL_WS_URL

        # Create Client objects 
        imp = Import(self.SOAP_ENCODING_SCHEMA_URL)  # Required for suds 

        self.windchillWebparts = Client(fullWebpartsUrl, plugins=[ImportDoctor(imp)], username=self.username, password=self.password)
        self.windchillWs = Client(fullWsUrl, plugins=[ImportDoctor(imp)], username=self.username, password=self.password)

    def __createIcmsClient(self):
        """
        Initialize ICMS soap class
        """
        if(self.icmsConnection != None):
            return

        # initialize icmsClass
        self.icmsConnection = Icms(self.ICMS_USER, self.ICMS_PASS, self.icmsUrl)

    def __createComponentDbApi(self):
        """
        Initialize db API for components
        """
        if(self.componentDbApi != None):
            return

        # initialize db api
        self.componentDbApi = ComponentDbApi()

    def __cleanPDMLinkDrawingList(self, drawingList):
        """
        PDM Link has an inconsistency when retrieving multiple results.
        Some drawings have a result per revision while others do not.
        The results need to be narrowed down to only the unique drawings.

        :param drawingList: (PDMLink Drawing Object List) original list of drawings from PDMLink
        :return: new list of unique drawings
        """

        verInx = 6
        itrInx = 5
        newList = []
        revList = []

        def addLatestRev(curDrawing=None):
            def getKeyAtIndex(revision, index, key):
                ver = revision.properties[index]
                if ver.name == key:
                    return ver.value
                else:
                    revisionMap = self.getPdmLinkObjectPropertyMap(revision)
                    if key in revisionMap:
                        return revisionMap[key]
                    else:
                        return 0

            def getVersion(revision):
                return getKeyAtIndex(revision, verInx, 'versionInfo.identifier.versionId')

            def getIteration(revision):
                return getKeyAtIndex(revision, itrInx, 'versionInfo.identifier.versionId')

            maxRev = 0
            maxRevIndex = 0
            if revList.__len__() > 0:
                for i in range(0, revList.__len__()):
                    curRev = int(str(getVersion(revList[i])) + str(getIteration(revList[i])))
                    if curRev > maxRev:
                        maxRev = curRev
                        maxRevIndex = i
                newList.append(revList[maxRevIndex])
            elif curDrawing is not None:
                newList.append(curDrawing)

        # Drawing list is empty
        if drawingList.__len__() == 0:
            return newList
        if str(drawingList[0].properties[0].name) == 'hasMore':
            return newList

        prevNumber = drawingList[0].properties[1].value
        revList.append(drawingList[0])

        for drawing in drawingList[1:]:
            # hasMore property is currently not used in this web service
            if str(drawing.properties[0].name) == 'hasMore':
                addLatestRev()
                continue

            # Add non-repeated values
            currentNumber = drawing.properties[1].value
            if prevNumber == currentNumber:
                revList.append(drawing)
            elif prevNumber != currentNumber:
                prevNumber = currentNumber
                addLatestRev(drawing)

                # empty the temp list with multiples of drawing
                del revList[:]
                revList.append(drawing)

        return newList

    def __searchWithoutExtension(self, searchPattern, maxResults=100):
        """
        Some drawings cannot be found when full name is given
        Function strips the extension if possible searches for drawing
        More results need to be fetched without extension max is defaulted to 100 for single drawing searches

        :param searchPattern: (str) Pattern to search PDMLink
        :param maxResults: (int) specify number of drawing results
        :return: List of drawings if modified(there was an extension to remove) search was performed
        :raises ExternalServiceError: Could occur during the PDM Link search
        """

        splitNumber = searchPattern.split('.')
        # No extension to remove
        # No need to reattempt to search
        if splitNumber[-1] == '???':
            return []

        # Part of number before a '.'
        newSearchPattern = splitNumber[0] + '.???'

        return self.findPdmLinkDrawings(newSearchPattern, startResults=0, maxResults=maxResults)

    @SslUtility.useUnverifiedSslContext
    def __getDrawing(self, pdmLinkDrawingObject, ufid=None, oid=None):
        """
        Get complete drawing object including drawing information and revision information from PDMLink and ICMS

        :param pdmLinkDrawingObject: (PDMLink Drawing Object) One drawing result from PDMLink
        :return: CDBObject of type PdmLinkDrawing
        """
        if pdmLinkDrawingObject is not None:
            if (pdmLinkDrawingObject.ufid is None) and (ufid is None):
                return None
        if pdmLinkDrawingObject is None and ufid is None:
            return None

        if ufid is None:
            ufid = pdmLinkDrawingObject.ufid

        self.logger.debug('Retrieving iteration history for UFID: %s' % ufid)
        iterationHistory = self.windchillWs.service.GetIterationHistory(
            [ufid],
            ['versionInfo.identifier.versionId',
            'iterationInfo.identifier.iterationId',
            'state.state',
            'thePersistInfo.createStamp'])

        revisionList = []
        for iteration in iterationHistory:
            iterationUfid = iteration.ufid
            propertyMap = self.getPdmLinkObjectPropertyMap(iteration)
            versionId = propertyMap.get('versionInfo.identifier.versionId')
            iterationId = propertyMap.get('iterationInfo.identifier.iterationId')
            state = propertyMap.get('state.state')
            dateCreated = propertyMap.get('thePersistInfo.createStamp')
            revisionList.append(PdmLinkDrawingRevision({
                'version' : versionId,
                'iteration' : iterationId,
                'state' : state,
                'ufid' : iterationUfid,
                'dateCreated' : dateCreated
            }))

        if(oid is None):
            propertyMap = self.getPdmLinkObjectPropertyMap(pdmLinkDrawingObject)
            oid = propertyMap.get('oid')

        latestUfid = iterationHistory[0].ufid
        drawingInfo = self.__getDrawingDetails(latestUfid)
        number = drawingInfo['number']

        # retrieve the revisions from ICMS
        icmsRevisions = self.getIcmsRevisions(number)

        # Create a windchill url for the drawing
        response = self.windchillWebparts.service.GetActionUrl('object.view')
        actionUrl = str(response[0].value)
        actionUrl = actionUrl.replace('INSERTOID', oid)

        # Add ICMS link to revisions that have an ICMS link
        for pdmRevision in revisionList:
            for icmsRevision in icmsRevisions:
                if (pdmRevision['version'] == icmsRevision['revision']['version']) and (pdmRevision['iteration'] == icmsRevision['revision']['iteration']):
                    pdmRevision['icmsUrl'] = icmsRevision['url']
                    icmsRevision['done'] = True

        # Add ICMS revisions that were not matched with the PDM Link revisions
        for revision in icmsRevisions:
            if 'done' in revision:
                pass
            else:
                newRev = {}
                newRev['state'] = 'ICMS Mismatch'
                newRev['icmsUrl'] = revision['url']
                newRev['version'] = revision['revision']['version']
                newRev['iteration'] = revision['revision']['iteration']
                revisionList.append(newRev)

        # Populate a drawing information object
        drawingInfo['windchillUrl'] = actionUrl
        drawingInfo['revisionList'] = revisionList

        return PdmLinkDrawing(drawingInfo)

    def __getDrawingDetails(self, ufid):
        """
        Get detailed metadata about a drawing

        :param ufid: unique identifier of a drawing revision
        :return: dictionary with drawing details.
        """
        # retrieve the details about a drawing for the latest revision
        reqDetails = ["RESP_ENG", "DRAFTER", "WBS_DESCRIPTION", "TITLE1", "TITLE2", "TITLE3", "TITLE4", "TITLE5", "number","name"]
        drawingDetailsRaw = self.windchillWs.service.Fetch([ufid], reqDetails)
        drawingDetails = self.getPdmLinkObjectPropertyMap(drawingDetailsRaw[0])

        drawingInfo = {}
        drawingInfo["respEng"] = drawingDetails.get("RESP_ENG")
        drawingInfo["drafter"] = drawingDetails.get("DRAFTER")
        drawingInfo["wbsDescription"] = drawingDetails.get("WBS_DESCRIPTION")
        drawingInfo["title1"] = drawingDetails.get("TITLE1")
        drawingInfo["title2"] = drawingDetails.get("TITLE2")
        drawingInfo["title3"] = drawingDetails.get("TITLE3")
        drawingInfo["title4"] = drawingDetails.get("TITLE4")
        drawingInfo["title5"] = drawingDetails.get("TITLE5")
        drawingInfo['name'] = drawingDetails.get('name')
        drawingInfo['number'] = drawingDetails.get('number')

        return drawingInfo

    @SslUtility.useUnverifiedSslContext
    def __getDrawingDetailsWithoutSSL(self, ufid):
        return self.__getDrawingDetails(ufid)

    def __findRelatedDrawings(self, drawingNumberBase):
        """
        See getRelatedDrawingSearchResults
        """
        if drawingNumberBase is None:
            raise InvalidRequest('drawingNumber or drawing number base must be provided')

        # Check if something with extension was given
        splitPattern = drawingNumberBase.split('.')
        if splitPattern.__len__() > 1:
            # raises InvalidArgument
            PdmLinkDrawing.checkDrawingNumber(drawingNumberBase)
            drawingNumberBase = splitPattern[0]

        searchPattern = drawingNumberBase + '.???'

        searchResults = self.getDrawingSearchResults(searchPattern)

        relatedDrawings = []

        for searchResult in searchResults:
            if searchResult['number'].split('.')[0].lower() == drawingNumberBase.lower():
                relatedDrawings.append(searchResult)

        if relatedDrawings.__len__() == 0:
            raise ObjectNotFound("Related PDMLink drawings for '" + drawingNumberBase + "' could not be found")

        return relatedDrawings

    def __generateComponentInfo(self, drawingNumber):
        """
        see generateComponentInfo
        """
        if drawingNumber is None:
            raise InvalidRequest('drawingNumber must be provided')

        drawingDetails = None
        ufid = None
        # Component Info
        pdmComponentName = None
        pdmPropertyValues = []

        # Search using the drawing name provided
        PdmLinkDrawing.checkDrawingNumber(drawingNumber)
        drawingNumberBase = str(drawingNumber).split('.')[0]
        searchResults = self.__findRelatedDrawings(drawingNumberBase)

        # Generate list of PDM Link properties and component Name
        # Drawing is searchable only by UFID provided
        for searchResult in searchResults:
            pdmPropertyValues.append(searchResult['number'])
            resultExt = searchResult['number'].split('.')[-1]
            if str(resultExt).lower() == 'drw':
                # set UFID for getting drawing metadata
                ufid = searchResult['ufid']
                # Set pdmComponentName
                pdmComponentName = str(searchResult['number']).split('.')[0]

        if pdmComponentName is None:
            pdmComponentName = drawingNumberBase

        if ufid is None:
            # DRW was not found use titles of the first search result
            ufid = searchResults[0]['ufid']


        componentInfo = {}
        componentInfo['name'] = pdmComponentName
        componentInfo['drawingNumber'] = drawingNumber
        componentInfo['pdmPropertyValues'] = pdmPropertyValues

        # Generate component type suggestions
        self.__createComponentDbApi()

        # Get a list of available types
        componentTypes = self.componentDbApi.getComponentTypes()

        # Get drawing details for keywords
        if drawingDetails is None:
            drawingDetails = self.__getDrawingDetailsWithoutSSL(ufid)

        # Add WBS description
        componentInfo['wbsDescription'] = None
        wbsDescription = drawingDetails['wbsDescription']
        if wbsDescription is not None:
            if wbsDescription != '' and wbsDescription != '-':
                wbsSplitArray = wbsDescription.split('.')
                cdbWbsFormat = wbsSplitArray[0]
                del wbsSplitArray[0]
                for letter in wbsSplitArray:
                    if letter.isdigit():
                        cdbWbsFormat += ".%02d" % int(letter)
                componentInfo['wbsDescription'] = cdbWbsFormat


        # Generate a keyword list from drawings titles
        keywordList = []
        cdbDescription = ''
        for i in range(1, 6):
            title = str(drawingDetails['title'+str(i)])
            cdbDescription += title+"; "
            tmpKeywordList = title.split(' ')
            for keyword in tmpKeywordList:
                if keyword != '-':
                    keywordList.append(keyword)

        # Remove the last semicolon and set cdbDescription
        componentInfo['cdbDescription'] = cdbDescription[0:-2]

        # Key is id of a component type and value is commonality of a component type based on keywords.
        stats = {}
        suggestedTypeList = []
        for componentType in componentTypes:
            componentTypeName = str(componentType['name']).lower()
            for keyword in keywordList:
                keyword = keyword.lower()
                if keyword + ' ' in componentTypeName \
                        or ' ' + keyword in componentTypeName \
                        or componentTypeName.startswith(keyword):
                    curID = str(componentType['id'])
                    if curID not in stats.keys():
                        stats[curID] = 1
                    else:
                        stats[curID] += 1

        # Sort dictionary keys by value
        sortedIds = sorted(stats, key=stats.get)

        # Add the suggested types from most to least popular
        for i in range(sortedIds.__len__() -1, -1, -1):
            for componentType in componentTypes:
                if int(componentType['id']) == int(sortedIds[i]):
                    suggestedTypeList.append(componentType)
                    continue

        componentInfo['suggestedComponentTypes'] = suggestedTypeList

        return PdmLinkComponent(componentInfo)

    @SslUtility.useUnverifiedSslContext
    def findPdmLinkDrawings(self, searchPattern, startResults=0, maxResults=1):
        """
        Finds PDMLink drawing object(s) by name pattern

        :param searchPattern: (str) The keyword/pattern by which to search PDM Link for drawings
        :param startResults: (int) Results could be fetched incrementally from PDM Link. Specifies start of fetching
        :param maxResults: (int) Specifies how many results should be fetched from PDM Link
        :return: A cleaned up list of PDM Link drawings resulting from the search
        :raises ExternalServiceError: Could occur during the PDM Link search
        """
        self.logger.debug('Looking for name pattern: %s ' % searchPattern)
        self.__createWindchillClients()
        self.searchArgs['START'] = startResults
        self.searchArgs['MAX'] = maxResults
        self.searchArgs['keyword'] = searchPattern

        # This call actually returns list of objects, and the last one will
        # be of the form
        # This last object is removed in the cleanPDMLinkDrawingList function below
        # (com.ptc.windchill.ws.GenericBusinessObject){
        #   ufid = None
        #   typeIdentifier = "com.ptc.object"
        #   properties[] = 
        #     (com.ptc.windchill.ws.Property){
        #       name = "hasMore"
        #       value = "true"
        #     },
        # }

        # PDMLink server could throw its own exceptions when searching for drawings
        try:
            drawingList = self.windchillWebparts.service.WindchillSearch(**self.searchArgs)
        except WebFault as e:
            raise ExternalServiceError('PDM Link: ' + e.fault.faultstring)

        # The search results came back empty, try to remove extension
        if drawingList[0].ufid is None:
            drawingList = self.__searchWithoutExtension(searchPattern)

        # Group and clean up results
        drawingList = self.__cleanPDMLinkDrawingList(drawingList)

        return drawingList

    def findPdmLinkDrawing(self, drawingNumber):
        """
        Find PDMLink drawing object by name.

        :param drawingNumber: (str) The full number of the PDMLink drawing
        :return: PDMLink Drawing
        :raises ExternalServiceError: Could occur during the PDM Link search
        :raises ObjectNotFound: The requested drawing was not found
        :raises InvalidArgument: The drawing name is not complete
        """
        def findNameInList():
            for drawing in drawingList:
                curDrawingNumber = drawing.properties[1].value
                if curDrawingNumber.lower() == drawingNumber.lower():
                    return drawing
            return None

        # Getting a single PDM Link requires the full name with extension.
        PdmLinkDrawing.checkDrawingNumber(drawingNumber)
        drawingList = self.findPdmLinkDrawings(drawingNumber)

        # Find the requested drawing from results
        reqDrawing = findNameInList()

        # List did not contain requested drawing
        if not reqDrawing:
            # Try searching without extension
            drawingList = self.__searchWithoutExtension(drawingNumber)
            reqDrawing = findNameInList()
            if not reqDrawing:
                raise ObjectNotFound('PDMLink drawing %s could not be found.' % drawingNumber)

        return reqDrawing

    @classmethod
    def getPdmLinkObjectPropertyMap(self, pdmObject):
        """
        Get property map for a given pdm object

        :param pdmObject: (PDM Link Drawing Object) A single drawing result from PDM Link
        :return: propertyMap extracted from pdmObject
        """
        propertyMap = {}
        for property in pdmObject.properties:
            propertyMap[property.name] = property.value
        return propertyMap

    def getIcmsRevisions(self, drawingNumber):
        """
        initialize ICMS Client, if needed and get revisions

        :param drawingNumber: (str) Name or number as referred to in PDM Link or Contend ID as referred to in ICMS
        :return: revisions and pdf url for those revisions
        """
        self.__createIcmsClient()
        return self.icmsConnection.getIcmsRevisions(drawingNumber)

    def getDrawing(self, drawingNumber):
        """
        Find PDMLink drawing and get extra info such as revision, url and metadata.

        :param drawingNumber: (str) Drawing name or drawing number
        :return: CDBObject of type PdmLinkDrawing
        :raises ExternalServiceError: Could occur during the PDM Link search
        :raises ObjectNotFound: The requested drawing was not found
        :raises InvalidArgument: The drawing name is not complete
        """
        pdmLinkDrawingObject = self.findPdmLinkDrawing(drawingNumber)
        return self.__getDrawing(pdmLinkDrawingObject)

    def getDrawings(self, searchPattern):
        """
        Find multiple drawings in PDMLink and get extra info for each one.

        * wildcards:
            * "*" - fill in any character(s) where the * exists
            * "?" - fill in exactly one character in the place of ?

        :param searchPattern: (str) Keyword used to search for the list of drawings
        :return: A list CDBObjects from the function getDrawings
        :raises ExternalServiceError: Could occur during the PDM Link search
        """
        pdmLinkDrawingObjectList = self.findPdmLinkDrawings(searchPattern, startResults=0, maxResults=9999)
        drawingList = []
        for pdmLinkDrawingObject in pdmLinkDrawingObjectList:
            drawing = self.__getDrawing(pdmLinkDrawingObject)
            if drawing is not None:
                drawingList.append(drawing)
        return drawingList

    def getDrawingSearchResults(self, searchPattern):
        """
        Search PDM Link for drawing names/numbers

        * wildcards:
            * "*" - fill in any character(s) where the * exists
            * "?" - fill in exactly one character in the place of ?

        :param searchPattern: (str) Keyword used to search for pdmLink drawings
        :return: A list of CDBObject of type PdmLinkSearchResult
        :raises ExternalServiceError: Could occur during the PDM Link search
        """
        pdmLinkDrawingObjectList = self.findPdmLinkDrawings(searchPattern, startResults=0, maxResults=9999)

        searchResultsMap = []
        for pdmLinkDrawing in pdmLinkDrawingObjectList:
            propertyMap = PdmLinkSearchResult(self.getPdmLinkObjectPropertyMap(pdmLinkDrawing))
            propertyMap['ufid'] = pdmLinkDrawing.ufid
            propertyMap['modifyStamp'] = propertyMap['thePersistInfo.modifyStamp']
            del propertyMap['thePersistInfo.modifyStamp']
            searchResultsMap.append(propertyMap)
        return searchResultsMap

    def completeDrawingInformation(self, ufid, oid):
        """
        Completes the drawing details by providing only ufid and oid of the drawing that is a result of the search.

        :param ufid: (str) drawing search result ufid
        :param odi: (str) drawing search result oid
        :return: CDBObject of type PdmLinkDrawing
        """
        self.__createWindchillClients()
        return self.__getDrawing(None, ufid, oid)

    @SslUtility.useUnverifiedSslContext
    def getDrawingImage(self, ufid):
        """
        Get a one time use authenticated link to a drawing image.

        :param ufid: (str) UFID of a particular revision of a PDMLink drawing.
        :return: CDBObject of type Image
        :raises: ObjectNotFound: if an image cannot be retrieved for a particular revision.
        """
        self.__createWindchillClients()
        listContentResults = self.windchillWs.service.ListContent(ufid)
        if len(listContentResults) > 2:
            for attrib in listContentResults[2]['properties']:
                if attrib.name == 'urlLocation':
                    return Image({'imageUrl' : attrib.value})
        else:
            raise ObjectNotFound('Image for the requested ufid was not found.')

    @SslUtility.useUnverifiedSslContext
    def getDrawingThumbnail(self, ufid):
        """
        Get a one time use authenticated link to a drawing thumbnail.

        :param ufid: (str) UFID of a particular revision of a PDMLink drawing.
        :return: CDBObject of type Image
        :raises: ObjectNotFound: if a thumbnail cannot be retrieved for a particular revision.
        """
        self.__createWindchillClients()
        listContentResults = self.windchillWs.service.ListContent(ufid)
        if (len(listContentResults) > 2):
            for attrib in listContentResults[1]['properties']:
                if attrib.name == 'urlLocation' :
                    return Image({'thumbnailUrl' : attrib.value})
        else:
            raise ObjectNotFound('Thumbnail for the requested ufid was not found.')

    def generateComponentInfo(self, drawingNumber=None, ufid=None):
        """
        Generates information that would be helpful/used when creating a component using a PDM Link drawing number:
        Name:
            Component Name
        PdmLink Properties:
            List of related drawings
        Suggested Type:
            No concept of component types exist in PDMLink, Generates a list of suggested component types based on metadata entered by engineer.
        WBS information:
            Extracted from PdmLink metadata

        :param drawingNumber: The full name of the drawing
        :param ufid: optional currently not implemented for REST API
        :return: pdmLinkComponentObject with all the information gathered.
        :raises ObjectNotFound: if a drawing cannot be found
        :raises InvalidArguments: if a drawing number is not complete
        :raises InvalidRequest: if required parameters aren't provided
        """
        return self.__generateComponentInfo(drawingNumber)

    def createComponent(self, drawingNumber, createdByUserId, componentTypeId, description, ownerUserId, ownerGroupId, isGroupWriteable, componentTypeName):
        """
        Uses the generateComponentInfo to gather information required to create a component.
        Uses default componentTypeid if componentTypeId or componentTypeName is not provided
        Sets properties for all related PDMLink drawings
        Sets wbs property if possible and value is allowed.

        :param drawingNumber: (str) Pdm Link drawing number
        :param createdByUserId: (int)User id of user that is creating the component
        :param componentTypeId: (int) id of the type of component is being added
        :param description: (str) description of the component
        :param ownerUserId: (int) User id of user that owns the component
        :param ownerGroupId: (int) Group id of group that owns the component
        :param isGroupWriteable: (boolean) Could group modify the component
        :param componentTypeName: (str) user could provide component type name instead of id which will be searched for.
        :return: ComponentObject with the resulting component after it has been added
        :raises ObjectNotFound: if a drawing cannot be found, if a component type name provided cannot be found
        :raises InvalidArguments: if a drawing number is not complete
        :raises InvalidRequest: if required parameters aren't provided
        """
        # use default values for adding properties
        def addProperty(componentId, propertyId, propertyValue):
            self.componentDbApi.addComponentPropertyByTypeId(componentId, propertyId, tag='',
                                                                     value=propertyValue, units=None, description=None,
                                                                     enteredByUserId=createdByUserId, isDynamic=False,
                                                                     isUserWriteable=False)

        if (componentTypeId is None or componentTypeId == '' ) and (componentTypeName is None or componentTypeName == ''):
            raise InvalidRequest('componentTypeId or componentTypeName must be provided to create a component.')

        # Throws invalidArguments if user does not provide complete drawingNumber
        # Throws InvalidRequest if no number is provided
        componentInfo = self.__generateComponentInfo(drawingNumber)

        # Check if description was entered
        if description is None:
            description = componentInfo['cdbDescription']

        # It is already created in the above function
        self.__createComponentDbApi()

        # Figure component type id if one was provided textually or use default
        if componentTypeId is None and componentTypeName is not None:
            # Raises ObjectNotFound
            componentTypeId = self.componentDbApi.getComponentTypeByName(componentTypeName)['id']
        if componentTypeId is None:
            componentTypeId = componentInfo['suggestedComponentTypes'][0]['id']

        # Add the new component
        newComponent = self.componentDbApi.addComponent(componentInfo['name'], componentTypeId, createdByUserId, ownerUserId,
                                                        ownerGroupId, isGroupWriteable, description)

        componentId = newComponent['id']

        # Add PDMLink properties
        # Get id of pdmLink property
        pdmPropertyTypeId = self.componentDbApi.getPropertyTypeByName(PdmLinkComponent.PROPERTY_TYPE_PDM_NAME)['id']
        pdmPropertyValues = componentInfo['pdmPropertyValues']
        for pdmPropertyValue in pdmPropertyValues:
            addProperty(componentId, pdmPropertyTypeId, pdmPropertyValue)

        # Attempt to add wbs description
        wbsDescription = componentInfo['wbsDescription']
        if wbsDescription is not None:
            wbsPropertyTypeId = self.componentDbApi.getPropertyTypeByName(PdmLinkComponent.PROPERTY_TYPE_WBS_NAME)['id']
            try:
                addProperty(componentId, wbsPropertyTypeId, wbsDescription)
            except InvalidArgument as invalidArgsEx:
                self.logger.debug('Could not add WBS information for drawing: %s' % str(invalidArgsEx.getErrorMessage()))

        return self.componentDbApi.getComponentById(componentId)

    def getRelatedDrawingSearchResults(self, drawingNumberBase):
        """
        Searched PDMLink for drawings related to each other. For example a DRW and PRT for the same part.

        :param drawingNumberBase: A drawing number with or without extension
        :return: A list of CDBObject of type PdmLinkSearchResult
        :raises ObjectNotFound: if no related drawings were found
        :raises InvalidArgument: if drawing number with extension is invalid
        :raises InvalidRequest: if no drawing number is provided
        """
        return self.__findRelatedDrawings(drawingNumberBase)
#######################################################################
# Testing.
if __name__ == '__main__':
    pdmLink = PdmLink('edp', 'PakTai8', 'http://windchill-vm.aps.anl.gov/Windchill', 'https://icmsdocs.aps.anl.gov')

    def getDrawingFromPDMLink(drawingNumber):
        print 'Get Drawing: ', drawingNumber
        drawing = pdmLink.getDrawing(drawingNumber)
        print drawing.getDisplayString(PdmLinkDrawing.DEFAULT_KEY_LIST)
        print '~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~'

    def getDrawingsFromPDMLink(searchPattern):
        print 'Get Drawings: ', searchPattern
        drawings = pdmLink.getDrawings(searchPattern)
        for drawing in drawings:
            print drawing.getDisplayString(PdmLinkDrawing.DEFAULT_KEY_LIST)
        print '~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~'

    def searchDrawingFromPDMLink(searchPattern):
        print 'Search Drawing: ', searchPattern
        results = pdmLink.getDrawingSearchResults(searchPattern)
        for result in results:
            print result.getDisplayString(PdmLinkSearchResult.DEFAULT_KEY_LIST)
        print '~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~'
        return results

    def getThumbnailImage(drawingNumber):
        print 'Get Thumbnail and Image for: ', drawingNumber
        drawing = pdmLink.getDrawing(drawingNumber)
        print pdmLink.getDrawingImage(drawing['revisionList'][1]['ufid']).getDisplayString(Image.DEFAULT_KEY_LIST)
        print pdmLink.getDrawingThumbnail(drawing['revisionList'][1]['ufid']).getDisplayString(Image.DEFAULT_KEY_LIST)
        print '~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~'

    # Complete results of a search result
    results = searchDrawingFromPDMLink('pole Top')
    oid = results[0]['oid']
    ufid = results[0]['ufid']
    print pdmLink.completeDrawingInformation(ufid, oid)

    # Getting a drawing from PDMLink
    getDrawingFromPDMLink('D14100201-113160.asm')
    # Get drawings that aren't found unless extension is removed
    getDrawingFromPDMLink('pole_top_bolt_on.drw')
    getDrawingFromPDMLink('pole_top_bolt_on.prt')

    # Search for drawings
    # Use wild cards: ?, *
    searchDrawingFromPDMLink('U221020205-12212?.DRW')
    searchDrawingFromPDMLink('AG1250E-QA_*.prt')
    searchDrawingFromPDMLink('D14100201-110*')
    
    # Fetch information about multiple drawings from PDMLink
    getDrawingsFromPDMLink('AG1250E-QA_*.prt')

    # Search for Thumbnails and Images
    getThumbnailImage('D14100201-113160.asm')

    # Handling drawings where ICMS and PDMLink do not have matching revisions
    getDrawingFromPDMLink('41050401-500000.drw')
    getDrawingFromPDMLink('31020301-100009.drw')

    # Causes an external service error exception
    # print pdmLink.getDrawings('stillwell ben')

    # Tests below alter the database remove quit to run them.
    quit()
    # Create component from pdmLink Drawing
    pdmLink.createComponent(createdByUserId=26, ownerUserId=26, ownerGroupId=4, isGroupWriteable=True,
                            description='PDM Link Utility Test', drawingNumber='D14100201-113160.asm', componentTypeId=None, componentTypeName='Absorber')