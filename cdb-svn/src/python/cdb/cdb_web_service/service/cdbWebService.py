#!/usr/bin/env python

#
# CDB Web Service
#

####################################################################

from cdb.common.service.cdbWebServiceBase import CdbWebServiceBase
from cdb.common.utility.cdbModuleManager import CdbModuleManager
from cdb.common.utility.configurationManager import ConfigurationManager
from cdb.common.impl.authorizationManager import AuthorizationManager
from cdb.cdb_web_service.service.cdbWebServiceRouteMapper import CdbWebServiceRouteMapper

####################################################################

class CdbWebService(CdbWebServiceBase):
 
    def __init__(self):
        CdbWebServiceBase.__init__(self, CdbWebServiceRouteMapper)

    def initCdbModules(self):
        self.logger.debug('Initializing cdb modules')

        # For testing purposes only, use NoOp authorization principal retriever
        #self.logger.debug('Using NoOp Authorization Principal Retriever')
        #from cdb.common.impl.noOpAuthorizationPrincipalRetriever import NoOpAuthorizationPrincipalRetriever
        #principalRetriever = NoOpAuthorizationPrincipalRetriever()
        #AuthorizationManager.getInstance().addAuthorizationPrincipalRetriever(principalRetriever)

        # Add modules that will be started.
        moduleManager = CdbModuleManager.getInstance()
        self.logger.debug('Initialized cdb modules')

    def getDefaultServerHost(self):
        return ConfigurationManager.getInstance().getServiceHost()

    def getDefaultServerPort(self):
        return ConfigurationManager.getInstance().getServicePort()

####################################################################
# Run service

if __name__ == '__main__':
    service = CdbWebService();
    service.run()
