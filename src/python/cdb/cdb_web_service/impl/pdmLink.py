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

from cdb.common.objects.pdmLinkDrawing import PdmLinkDrawing 
from cdb.common.objects.pdmLinkDrawingRevision import PdmLinkDrawingRevision
from cdb.common.objects.image import Image
from cdb.common.utility.loggingManager import LoggingManager
from cdb.common.utility.sslUtility import SslUtility 
from cdb.common.exceptions.objectNotFound import ObjectNotFound
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
        if(self.icmsConnection != None):
            return

        #initalize icmsClass
        self.icmsConnection = Icms(self.ICMS_USER, self.ICMS_PASS, self.icmsUrl)

    # Find PDMLink drawing objects by name pattern
    @SslUtility.useUnverifiedSslContext
    def findPdmLinkDrawings(self, drawingNamePattern):
        self.logger.debug('Looking for name pattern: %s ' % drawingNamePattern)
        self.__createWindchillClients()
        self.searchArgs['keyword'] = drawingNamePattern
        # This call actually returns list of objects, and the last one will
        # be of the form
        # (com.ptc.windchill.ws.GenericBusinessObject){
        #   ufid = None
        #   typeIdentifier = "com.ptc.object"
        #   properties[] = 
        #     (com.ptc.windchill.ws.Property){
        #       name = "hasMore"
        #       value = "true"
        #     },
        # }
        drawingList = self.windchillWebparts.service.WindchillSearch(**self.searchArgs)
        return drawingList 

    # Find PDMLink drawing object by name. 
    def findPdmLinkDrawing(self, drawingName):
        PdmLinkDrawing.checkDrawingName(drawingName)
        drawingList = self.findPdmLinkDrawings(drawingName)
        if not drawingList:
            raise ObjectNotFound('PDMLink drawing %s could not be found.' % drawingName)
        drawing = drawingList[0]
        firstProperty = drawing.properties[0]
        # Object is not found if the first property of the first object
        # in the list satisfies condition below
        if firstProperty.name == "hasMore" and firstProperty.value == "false":
            raise ObjectNotFound('PDMLink drawing %s could not be found.' % drawingName)
        return drawing

    # Get property map for a given pdm object
    @classmethod
    def getPdmLinkObjectPropertyMap(self, pdmObject):
        propertyMap = {}
        for property in pdmObject.properties:
            propertyMap[property.name] = property.value
        return propertyMap

    # initialize icms Client, if needed and get revisions
    def getIcmsRevisions(self, name):
        self.__createIcmsClient()
        return self.icmsConnection.getIcmsRevisions(name)


    # Get complete drawing object
    @SslUtility.useUnverifiedSslContext
    def __getDrawing(self, pdmLinkDrawingObject):
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


        response = self.windchillWebparts.service.GetActionUrl('object.view')
        actionUrl = str(response[0].value)
        actionUrl = actionUrl.replace('INSERTOID', oid)

        # Add ICMS link to revisions that have an ICMS link
        for pdmRevision in revisionList:
            for icmsRevision in icmsRevisions:
                if (pdmRevision['version'] == icmsRevision['revision']['version']) and (pdmRevision['iteration'] == icmsRevision['revision']['iteration']):
                        pdmRevision['icmsUrl'] = icmsRevision['url']

        # Populate a drawing information object
        drawingInfo = {}
        for detail in reqDetails:
            drawingInfo[detail] = drawingDetails.get(detail)

        drawingInfo['name'] = name
        drawingInfo['windchillUrl'] = actionUrl
        drawingInfo['revisionList'] = revisionList

        return PdmLinkDrawing(drawingInfo)

    # Get drawing 
    def getDrawing(self, drawingName):
        pdmLinkDrawingObject = self.findPdmLinkDrawing(drawingName)
        return self.__getDrawing(pdmLinkDrawingObject)

    # Get drawings for a given name pattern
    def getDrawings(self, drawingNamePattern):
        pdmLinkDrawingObjectList = self.findPdmLinkDrawings(drawingNamePattern)
        drawingList = []
        for pdmLinkDrawingObject in pdmLinkDrawingObjectList:
            drawing = self.__getDrawing(pdmLinkDrawingObject)
            if drawing is not None:
                drawingList.append(drawing)
        return drawingList

    # Return a one time use authenticated link to a drawing image.
    @SslUtility.useUnverifiedSslContext
    def getDrawingImage(self, ufid):
        self.__createWindchillClients()
        listContentResults = self.windchillWs.service.ListContent(ufid)
        if (len(listContentResults) > 2):
            for attrib in listContentResults[2]['properties']:
                if attrib.name == 'urlLocation':
                    return Image({'imageUrl' : attrib.value})
        else:
            raise ObjectNotFound('Image for the requested ufid was not found.')

    # Return a one time use authenticated link to a drawing thumbnail.
    @SslUtility.useUnverifiedSslContext
    def getDrawingThumbnail(self, ufid):
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
    drawingName = 'D14100201-113160.asm'

    print 'Drawing: ', drawingName
    drawing = pdmLink.getDrawing(drawingName)
    print drawing

    drawingName2 = 'U221020202-104210.DRW'

    print 'Drawing2: ', drawingName2
    drawing2 = pdmLink.getDrawing(drawingName2)

    print drawing2



    print ''
    print 'Thumbnails'
    print drawing['revisionList'][1]
    print pdmLink.getDrawingImage(drawing['revisionList'][1]['ufid'])
    print pdmLink.getDrawingThumbnail(drawing['revisionList'][1]['ufid'])
    print ''
    #print 'Drawings'
    #drawings = pdmLink.getDrawings('D14100201-110*')
    #print drawings

