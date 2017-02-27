#!/usr/bin/env python

"""
Copyright (c) UChicago Argonne, LLC. All rights reserved.
See LICENSE file.
"""

from cdb.common.utility.sslUtility import SslUtility
import json

'''
***Class that communicates with ICMS and retrieves revisions and url of a drawing
'''

from suds.client import Client
from suds.sudsobject import asdict

class Icms:

    ICMS_DOC_PATH = 'docs/idcplg?IdcService=DISPLAY_URL&dDocName='
    ICMS_REVISION_PARAM = '&dRevLabel='
    ICMS_SEARCH_WSDL_PATH = 'docs/idcplg?IdcService=DISPLAY_URL&dDocName=SEARCH'
    ICMS_INFO_WSDL_PATH = "docs/idcplg?IdcService=DISPLAY_URL&dDocName=DOCINFO"

    def __init__(self, icmsUser, icmsPass, icmsUrl):
        self.icmsUrl = icmsUrl
        self.icmsBaseUrl = '%s/%s' % (icmsUrl, self.ICMS_DOC_PATH)
        self.icmsUrlRevs = self.ICMS_REVISION_PARAM

        self.__createIcmsClients(icmsUser, icmsPass)

    @SslUtility.useUnverifiedSslContext
    def __createIcmsClients(self, icmsUser, icmsPass):
        # No need for this suds client, wsdl class is not used.
        # icmsSearchWsdlUrl = '%s/%s' % (self.icmsUrl, self.ICMS_SEARCH_WSDL_PATH)
        # self.icmsSearchClass = Client(icmsSearchWsdlUrl, username=icmsUser, password=icmsPass)

        icmsInfoWsdlUrl = '%s/%s' % (self.icmsUrl, self.ICMS_INFO_WSDL_PATH)
        icmsSearchWsdlUrl = '%s/%s' % (self.icmsUrl, self.ICMS_SEARCH_WSDL_PATH)
        self.icmsInfoClass = Client(icmsInfoWsdlUrl, username=icmsUser, password=icmsPass)
        self.icmsSearchClass = Client(icmsSearchWsdlUrl, username=icmsUser, password=icmsPass)

    @SslUtility.useUnverifiedSslContext
    def performIcmsQuickSearch(self, keyword):
        result = self.icmsSearchClass.service.QuickSearch(keyword)
        json = self.___toJson(result)

        return json

    @SslUtility.useUnverifiedSslContext
    def getDocInfo(self, keyword):
        docInfo = self.icmsInfoClass.service.DocInfoByName(keyword)
        json = self.___toJson(docInfo)

        return json

    @SslUtility.useUnverifiedSslContext
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

    def ___toJson(self, sudsResponse):
        return json.dumps(self.__convertSudsToDict(sudsResponse))

    def __convertSudsToDict(self, sudsResponse):
        out = {}
        for k, v in asdict(sudsResponse).iteritems():
            if hasattr(v, '__keylist__'):
                out[k] = self.__convertSudsToDict(v)
            elif isinstance(v, list):
                out[k] = []
                for item in v:
                    if hasattr(item, '__keylist__'):
                        out[k].append(self.__convertSudsToDict(item))
                    else:
                        out[k].append(item)
            else:
                out[k] = v
        return out
    
if __name__ == '__main__':
    icmsConnection = Icms()
    print icmsConnection.performIcmsQuickSearch('quadrupole')
    print icmsConnection.getDocInfo('D14100201-113160.ASM')
    print icmsConnection.getIcmsRevisions('D14100201-113160.ASM') 
    print icmsConnection.getIcmsRevisions('u2210203-101600.drw')
    print icmsConnection.getIcmsRevisions('U1340201-102000.DRW')
    print icmsConnection.getIcmsRevisions('U221020202-104210.DRW') 
