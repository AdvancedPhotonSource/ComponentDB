#!/usr/bin/env python

"""
Copyright (c) UChicago Argonne, LLC. All rights reserved.
See LICENSE file.
"""


'''
***Class that communicates with ICMS and retrieves revisions and url of a drawing
'''

from suds.client import Client

class Icms:

    ICMS_DOC_URL = '/docs/idcplg?IdcService=DISPLAY_URL&dDocName='
    ICMS_REVISION_PARAM = '&dRevLabel='
    ICMS_SEARCH_WSDL = 'http://oradev.aps.anl.gov/gen-doc/soap_search11g-0813.wsdl'
    ICMS_INFO_WSDL = "http://oradev.aps.anl.gov/gen-doc/soap_docinfo-0821.wsdl"

    def __init__(self, icmsUser, icmsPass, icmsUrl):

        #No need for this suds client, wsdl class is not used.
        #self.icmsSearchClass = Client(self.ICMS_SEARCH_WSDL, username=icmsUser, password=icmsPass)
        self.icmsInfoClass = Client(self.ICMS_INFO_WSDL, username=icmsUser, password=icmsPass)
        
        self.icmsBaseUrl = icmsUrl + self.ICMS_DOC_URL
        self.icmsUrlRevs = self.ICMS_REVISION_PARAM
        
    
    def getIcmsRevisions(self, keyword):
        docSearch = self.icmsInfoClass.service.DocInfoByName(keyword)
        result = []
        if(docSearch['StatusInfo']['statusCode'] != 0):
            print docSearch['StatusInfo']['statusMessage']
        else:
            for revision in docSearch['Revisions']:
                pdmLinkStyleRevs = self.convertRevLabel(revision['dRevLabel'])
                result.append({'url':
                               self.icmsBaseUrl + revision['dDocName'] + self.icmsUrlRevs + revision['dRevLabel'],
                               'revision': pdmLinkStyleRevs
                               })
        return result
    
    def convertRevLabel(self, icmsRevLabel):
        result = {} 
        result['version'] = str(icmsRevLabel[0:2])
        result['iteration'] = str(int(icmsRevLabel[2:]))
        return result
    
if __name__ == '__main__':
    icmsConnection = Icms()
    print icmsConnection.getIcmsRevisions('D14100201-113160.ASM') 
    print icmsConnection.getIcmsRevisions('u2210203-101600.drw')
    print icmsConnection.getIcmsRevisions('U1340201-102000.DRW')
    print icmsConnection.getIcmsRevisions('U221020202-104210.DRW') 
