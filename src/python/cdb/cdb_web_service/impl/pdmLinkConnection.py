#!/usr/bin/env python

'''
*********************************
Script file gathers various information from Windchill PDMLink repository
Generates links for both ISMS and PDMLink
Generates a history of drawings 

Requires an installation of suds located on: 
    https://fedorahosted.org/suds/
    
All Classes available in Windchill:
    http://windchill-dev.aps.anl.gov/Windchill/infoengine/jsp/tools/doc/index.jsp


***********************************TESTING NOTES**********************************
(1/6/2015 - Could be innitialized in this way):
pdmConn = PDMLink('edp','PakTai8','http://windchill-vm.aps.anl.gov/Windchill', 'https://icmsdocs.aps.anl.gov/docs/idcplg?IdcService=DISPLAY_URL&dDocName=','&dRevLabel=')

pdmConn.getLinks('D14100201-113160.ASM')
pdmConn.getLinks('u2210203-101600.drw')
pdmConn.getLinks('U1340201-102000.DRW')
pdmConn.getLinks('U221020202-104210.DRW')
***********************************************************************************

*********************************
'''

import ssl
from suds.client import Client
from suds.xsd.doctor import Import
from suds.xsd.doctor import ImportDoctor

from cdb.common.objects.cdbObject import CdbObject
from cdb.common.utility.errorChecker import ErrorChecker
from cdb.common.exceptions.objectNotFound import ObjectNotFound


# decorator used to disable ssl for functions requestion info from Windchill
# decorator to decorate functions that have input of (self, drawingNumber, [etc]) due to error checking
def useUnverifiedSslContext(func):
    # store the default ssl context
    defaultSslContextontext = ssl._create_default_https_context

    def newFunc(*args, **kwargs):
        # throws an exception upon invalid extension
        ErrorChecker.pdmLinkDrawingValidExtension(args[1])
        # Disable ssl checking
        ssl._create_default_https_context = ssl._create_unverified_context
        # perform call to windchill
        val = func(*args, **kwargs)
        # revert back to original SSL settings
        ssl._create_default_https_context = defaultSslContextontext
        return val

    return newFunc


class PDMLink:
    def __init__(self, pdmUserName, pdmPass, windchillLink, icmsLink, icmsRev):
        # Location of windchill classes on Windchill. 
        # Usage windchillLink + ClassLink | https://windchill-vm.aps.anl.gov/Windchill + clasLink
        windchillWebpartsClassLink = '/servlet/SimpleTaskDispatcher?CLASS=com.ptc.windchill.webparts'
        windchillWsClassLink = '/servlet/SimpleTaskDispatcher?CLASS=com.ptc.windchill.ws'
        # required for Suds, Soap client for python.
        soapEncodingXML = 'http://schemas.xmlsoap.org/soap/encoding/'

        # WSDL URLS
        fullWebpartsClassLink = windchillLink + windchillWebpartsClassLink
        fullWsClassLink = windchillLink + windchillWsClassLink

        #       ***Innitialize variables used across class***

        #Static ISMS links
        self.icmsLink = icmsLink
        self.icmsRev = icmsRev

        #Create Client objects 
        imp = Import(soapEncodingXML)  #Required for suds to corretly use wsdls
        self.windchillWebpartsClass = Client(fullWebpartsClassLink, plugins=[ImportDoctor(imp)], username=pdmUserName,
                                             password=pdmPass)
        self.windchillWsClass = Client(fullWsClassLink, plugins=[ImportDoctor(imp)], username=pdmUserName,
                                       password=pdmPass)

        #Create searchArgs used in the search functions of Windchill webparts class
        #Usage -- only the kewords needs to be changed when searching for a node. | webpartsClass.service.WindchillSearch(**searchArgs)
        searchTypes = "WCTYPE|wt.epm.EPMDocument|gov.anl.aps.DefaultEPMDocument"
        self.searchArgs = {'keyword': 'notSet', 'START': 0, 'MAX': 1, 'SearchTypes': searchTypes, 'AttributeFilter': '',
                           'SortBy': 'name', 'SortDirection': 'ASC', 'clientLocale': 'en-US'}
        self.currDrw = 'notSet'

    #function meant to be used internally - finds node by its id. contains the requierd UFID    
    def findDrawing(self, keyword, forceReload=False):
        #No need to grab the object from the server again
        if (self.currDrw != keyword or forceReload):
            self.searchArgs['keyword'] = keyword
            self.curObj = self.windchillWebpartsClass.service.WindchillSearch(**self.searchArgs)
            self.currDrw = keyword

    #fetches and returns the state, version, iteration, and ufid for each revision
    def getRevisions(self, drawingNum):
        self.findDrawing(drawingNum)
        history = self.windchillWsClass.service.GetIterationHistory([self.curObj[0].ufid],
                                                                    ['versionInfo.identifier.versionId',
                                                                     'iterationInfo.identifier.iterationId',
                                                                     'state.state'])
        tmp = []
        for index in range(len(history)):
            ufid = history[index].ufid
            for property in history[index].properties:
                if property.name == 'versionInfo.identifier.versionId':
                    ver = property.value
                elif property.name == 'iterationInfo.identifier.iterationId':
                    iter = property.value
                elif property.name == 'state.state':
                    state = property.value
            tmp.append({'version': ver, 'iteration': iter, 'state': state, 'ufid': ufid})
        return tmp

    #Returns a string verion of the above function (used for cherrypy)
    def getRevisionsStr(self, drawingNum):
        return str(self.getRevisions(drawingNum))

    #returns a one time use authenticated link to an image of a drawing numbers ufid if one exists
    @useUnverifiedSslContext
    def getThumbnail(self, ufid):
        listContentResults = self.windchillWsClass.service.ListContent(ufid)
        if (len(listContentResults) > 2):
            for attrib in listContentResults[2]['properties']:
                if attrib.name == 'urlLocation':
                    return CdbObject({'thumbnailUrl': attrib.value})
        else:
            raise ObjectNotFound('A thumbnail for the requested ufid was not found. Some drawings/revisions do not have thumbnails that could be retreived.')

    #returns a small image of the revision given the ufid of drawing number
    def getSmallThumbnail(self, ufid):
        listContentResults = self.windchillWsClass.service.ListContent(ufid)
        if (len(listContentResults) > 2):
            for attrib in listContentResults[1]['properties']:
                if attrib.name == 'urlLocation':
                    return str(attrib.value)
        else:
            return str(False)

    #fetches the name of the drawingNum -- should be the same however if partially entered the first result will be shown.
    def getName(self, drawingNum):
        self.findDrawing(drawingNum)
        for property in self.curObj[0].properties:
            if (property.name == 'name'):
                return property.value

    #returns everything getRevisions as well as the links to pdmlink and ICMS
    @useUnverifiedSslContext
    def getLinksRevs(self, drawingNum):
        # function below getRevisions - uptates the self.currObj no need to call findDrawing
        revs = self.getRevisions(drawingNum)
        for property in self.curObj[0].properties:
            if (property.name == 'oid'):
                oid = property.value
            elif (property.name == 'name'):
                name = property.value
        response = self.windchillWebpartsClass.service.GetActionUrl('object.view')
        actionUrl = str(response[0].value)
        actionUrl = actionUrl.replace("INSERTOID", oid)

        links = [];
        for index in range(len(revs)):
            if (revs[index]['state'] == 'RELEASED'):
                links.append(CdbObject({"UFID": str(revs[index]['ufid']), "state": revs[index]['state'], "icmsUrl": str(
                    self.icmsLink + name + self.icmsRev + revs[index]['version'] + str(revs[index]['iteration']).rjust(
                        3, '0')),
                                        "version": revs[index]['version'], "iteration": revs[index]['iteration']}))
            else:
                links.append(CdbObject({"UFID": str(revs[index]['ufid']), "state": revs[index]['state'],
                                        "version": revs[index]['version'], "iteration": revs[index]['iteration']}))

        return CdbObject({"name": self.getName(drawingNum), "windchillUrl": str(actionUrl), "revisions": links})

#######################################################################
# Testing.

if __name__ == '__main__':
    pdmConn = PDMLink('edp', 'PakTai8', 'http://windchill-vm.aps.anl.gov/Windchill',
                      'https://icmsdocs.aps.anl.gov/docs/idcplg?IdcService=DISPLAY_URL&dDocName=', '&dRevLabel=')
    print 'find drawing:'
    print pdmConn.getLinksRevs('D14100201-113160.ASM')
    print ''
    LinksRevs = pdmConn.getLinksRevs('D14100201-113160')
    print LinksRevs
    print ''
    pdmConn.getThumbnail(LinksRevs[1]['UFID'])
    

    


    
        
