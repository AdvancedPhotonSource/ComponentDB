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
from cdb.common.objects.image import Image
from cdb.common.utility.loggingManager import LoggingManager
from cdb.common.utility.sslUtility import SslUtility 
from cdb.common.exceptions.objectNotFound import ObjectNotFound
from cdb.common.exceptions.externalServiceError import ExternalServiceError
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

        #initalize icmsClass
        self.icmsConnection = Icms(self.ICMS_USER, self.ICMS_PASS, self.icmsUrl)

    def __cleanPDMLinkDrawingList(self, drawingList):
        """
        PDM Link has an inconsistency when retrieving multiple results.
        Some drawings have a result per revision while others do not.
        The results need to be narrowed down to only the unique drawings.

        :param drawingList: (PDMLink Drawing Object List) original list of drawings from PDMLink
        :return: new list of unique drawings
        """
        newList = []
        prevName = ''

        for drawing in drawingList:
            # hasMore property is currently not used in this web service
            if(str(drawing.properties[0].name) == 'hasMore'):
                continue

            # Add non-repeated values
            currentName = drawing.properties[0].value
            if prevName != currentName:
                prevName = currentName
                newList.append(drawing)

        return newList

    def __searchWithoutExtension(self, namePattern, maxResults=100):
        """
        Some drawings cannot be found when full name is given
        Function strips the extension if possible searches for drawing
        More results need to be fetched without extension max is defaulted to 100 for single drawing searches

        :param namePattern: (str) Pattern to search PDMLink
        :param maxResults: (int) specify number of drawing results
        :return: List of drawings if modified(there was an extension to remove) search was performed
        :raises ExternalServiceError: Could occur during the PDM Link search
        """
        # Check if extension exists
        splitNamePattern = namePattern.split('.')
        # No extension to remove
        # No need to reattempt to search
        if splitNamePattern.__len__() < 2:
            return []

        # Part of name before a '.'
        newName = splitNamePattern[0]

        return self.findPdmLinkDrawings(newName, startResults=0, maxResults=maxResults)

    @SslUtility.useUnverifiedSslContext
    def __getDrawing(self, pdmLinkDrawingObject):
        """
        Get complete drawing object including drawing information and revision information from PDMLink and ICMS

        :param pdmLinkDrawingObject: (PDMLink Drawing Object) One drawing result from PDMLink
        :return: CDBObject of type PdmLinkDrawing
        """
        if pdmLinkDrawingObject.ufid is None:
            return None
        self.logger.debug('Retrieving iteration history for UFID: %s' % pdmLinkDrawingObject.ufid)
        iterationHistory = self.windchillWs.service.GetIterationHistory(
            [pdmLinkDrawingObject.ufid],
            ['versionInfo.identifier.versionId',
            'iterationInfo.identifier.iterationId',
            'state.state'])
        revisionList = []
        for iteration in iterationHistory:
            ufid = iteration.ufid
            propertyMap = self.getPdmLinkObjectPropertyMap(iteration)
            versionId = propertyMap.get('versionInfo.identifier.versionId')
            iterationId = propertyMap.get('iterationInfo.identifier.iterationId')
            state = propertyMap.get('state.state')
            revisionList.append(PdmLinkDrawingRevision({
                'version' : versionId,
                'iteration' : iterationId,
                'state' : state,
                'ufid' : ufid
            }))
        propertyMap = self.getPdmLinkObjectPropertyMap(pdmLinkDrawingObject)
        name = propertyMap.get('name')
        oid = propertyMap.get('oid')

        # retrieve the revisions from ICMS
        icmsRevisions = self.getIcmsRevisions(name)

        # retrieve the details about a drawing
        reqDetails = ["RESP_ENG", "DRAFTER", "WBS_DESCRIPTION", "TITLE1", "TITLE2", "TITLE3", "TITLE4", "TITLE5"]
        drawingDetailsRaw = self.windchillWs.service.Fetch([ufid], reqDetails)
        drawingDetails = self.getPdmLinkObjectPropertyMap(drawingDetailsRaw[0])

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
        drawingInfo = {}
        drawingInfo["respEng"] = drawingDetails.get("RESP_ENG")
        drawingInfo["drafter"] = drawingDetails.get("DRAFTER")
        drawingInfo["wbsDescription"] = drawingDetails.get("WBS_DESCRIPTION")
        drawingInfo["title1"] = drawingDetails.get("TITLE1")
        drawingInfo["title2"] = drawingDetails.get("TITLE2")
        drawingInfo["title3"] = drawingDetails.get("TITLE3")
        drawingInfo["title4"] = drawingDetails.get("TITLE4")
        drawingInfo["title5"] = drawingDetails.get("TITLE5")
        drawingInfo['name'] = name
        drawingInfo['windchillUrl'] = actionUrl
        drawingInfo['revisionList'] = revisionList

        return PdmLinkDrawing(drawingInfo)

    @SslUtility.useUnverifiedSslContext
    def findPdmLinkDrawings(self, drawingNamePattern, startResults=0, maxResults=1):
        """
        Finds PDMLink drawing object(s) by name pattern

        :param drawingNamePattern: (str) The keyword/pattern by which to search PDM Link for drawings
        :param startResults: (int) Results could be fetched incrementally from PDM Link. Specifies start of fetching
        :param maxResults: (int) Specifies how many results should be fetched from PDM Link
        :return: A cleaned up list of PDM Link drawings resulting from the search
        :raises ExternalServiceError: Could occur during the PDM Link search
        """
        self.logger.debug('Looking for name pattern: %s ' % drawingNamePattern)
        self.__createWindchillClients()
        self.searchArgs['START'] = startResults
        self.searchArgs['MAX'] = maxResults
        self.searchArgs['keyword'] = drawingNamePattern

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
            drawingList = self.__searchWithoutExtension(drawingNamePattern)

        # Group and clean up results
        drawingList = self.__cleanPDMLinkDrawingList(drawingList)

        return drawingList

    def findPdmLinkDrawing(self, drawingName):
        """
        Find PDMLink drawing object by name.

        :param drawingName: (str) The full name/number of the PDMLink drawing
        :return: PDMLink Drawing
        :raises ExternalServiceError: Could occur during the PDM Link search
        :raises ObjectNotFound: The requested drawing was not found
        :raises InvalidArgument: The drawing name is not complete
        """
        def findNameInList(name):
            for drawing in drawingList:
                curDrawingName = drawing.properties[0].value
                if curDrawingName.lower() == drawingName.lower():
                    return drawing
            return None

        # Getting a single PDM Link requires the full name with extension.
        PdmLinkDrawing.checkDrawingName(drawingName)
        drawingList = self.findPdmLinkDrawings(drawingName)

        # Find the requested drawing from results
        reqDrawing = findNameInList(drawingName)

        # List did not contain requested drawing
        if not reqDrawing:
            # Try searching without extension
            drawingList = self.__searchWithoutExtension(drawingName)
            reqDrawing = findNameInList(drawingName)
            if not reqDrawing:
                raise ObjectNotFound('PDMLink drawing %s could not be found.' % drawingName)

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

    def getIcmsRevisions(self, name):
        """
        initialize ICMS Client, if needed and get revisions

        :param name: (str) Name or number as referred to in PDM Link or Contend ID as referred to in ICMS
        :return: revisions and pdf url for those revisions
        """
        self.__createIcmsClient()
        return self.icmsConnection.getIcmsRevisions(name)

    def getDrawing(self, drawingName):
        """
        Find PDMLink drawing and get extra info such as revision, url and metadata.

        :param drawingName: (str) Drawing name or drawing number
        :return: CDBObject of type PdmLinkDrawing
        :raises ExternalServiceError: Could occur during the PDM Link search
        :raises ObjectNotFound: The requested drawing was not found
        :raises InvalidArgument: The drawing name is not complete
        """
        pdmLinkDrawingObject = self.findPdmLinkDrawing(drawingName)
        return self.__getDrawing(pdmLinkDrawingObject)

    def getDrawings(self, drawingNamePattern):
        """
        Find multiple drawings in PDMLink and get extra info for each one.

        * wildcards:
            * "*" - fill in any character(s) where the * exists
            * "?" - fill in exactly one character in the place of ?

        :param drawingNamePattern: (str) Keyword used to search for the list of drawings
        :return: A list CDBObjects from the function getDrawings
        :raises ExternalServiceError: Could occur during the PDM Link search
        """
        pdmLinkDrawingObjectList = self.findPdmLinkDrawings(drawingNamePattern, startResults=0, maxResults=9999)
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
            searchResultsMap.append(PdmLinkSearchResult(self.getPdmLinkObjectPropertyMap(pdmLinkDrawing)))
        return searchResultsMap

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
        if (len(listContentResults) > 2):
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


#######################################################################
# Testing.
if __name__ == '__main__':
    pdmLink = PdmLink('edp', 'PakTai8', 'http://windchill-vm.aps.anl.gov/Windchill', 'https://icmsdocs.aps.anl.gov')

    def getDrawingFromPDMLink(drawingName):
        print 'Get Drawing: ', drawingName
        drawing = pdmLink.getDrawing(drawingName)
        print drawing.getDisplayString(PdmLinkDrawing.DEFAULT_KEY_LIST)
        print '~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~'

    def getDrawingsFromPDMLink(drawingPattern):
        print 'Get Drawings: ', drawingPattern
        drawings = pdmLink.getDrawings(drawingPattern)
        for drawing in drawings:
            print drawing.getDisplayString(PdmLinkDrawing.DEFAULT_KEY_LIST)
        print '~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~'

    def searchDrawingFromPDMLink(drawingPattern):
        print 'Search Drawing: ', drawingPattern
        results = pdmLink.getDrawingSearchResults(drawingPattern)
        for result in results:
            print result.getDisplayString(PdmLinkSearchResult.DEFAULT_KEY_LIST)
        print '~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~'

    def getThumbnailImage(drawingName):
        print 'Get Thumbnail and Image for: ', drawingName
        drawing = pdmLink.getDrawing(drawingName)
        print pdmLink.getDrawingImage(drawing['revisionList'][1]['ufid']).getDisplayString(Image.DEFAULT_KEY_LIST)
        print pdmLink.getDrawingThumbnail(drawing['revisionList'][1]['ufid']).getDisplayString(Image.DEFAULT_KEY_LIST)
        print '~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~'


    # Getting a drawing from PDMLink
    getDrawingFromPDMLink('D14100201-113160.asm')
    # Get drawings that aren't found unless extension is removed
    getDrawingFromPDMLink('pole_top_bolt_on.drw')
    getDrawingFromPDMLink('pole_top_bolt_on.prt')

    raise Exception
    # Search for drawings
    # Use wild cards: ?, *
    searchDrawingFromPDMLink('U221020205-12212?.DRW')
    searchDrawingFromPDMLink('AG1250E-QA_*.prt')
    searchDrawingFromPDMLink('D14100201-110*')
    # Search for drawings that aren't searchable unless extension is removed
    searchDrawingFromPDMLink('pole_top_bolt_on.drw')

    # Fetch information about multiple drawings from PDMLink
    getDrawingsFromPDMLink('AG1250E-QA_*.prt')

    # Search for Thumbnails and Images
    getThumbnailImage('D14100201-113160.asm')

    # Handling drawings where ICMS and PDMLink do not have matching revisions
    getDrawingFromPDMLink('41050401-500000.drw')

    # Causes an external service error exception
    # print pdmLink.getDrawings('stillwell ben')



