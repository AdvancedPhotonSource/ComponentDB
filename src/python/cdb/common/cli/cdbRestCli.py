#!/usr/bin/env python

"""
Copyright (c) UChicago Argonne, LLC. All rights reserved.
See LICENSE file.
"""


from cdb.common.cli.cdbCli import CdbCli
from cdb.common.utility.configurationManager import ConfigurationManager

class CdbRestCli(CdbCli):
    """ Base cdb REST cli class. """

    def __init__(self, validArgCount=0):
        CdbCli.__init__(self, validArgCount)
        self.serviceHost = None
        self.servicePort = None

        serviceGroup = 'Service Options'
        self.addOptionGroup(serviceGroup, None)
        # These will be set via env variables
        self.addOptionToGroup(serviceGroup, '', '--service-host', dest='serviceHost', default=self.getDefaultServiceHost(), help='Service host (default: %s, can be set via CDB_SERVICE_HOST environment variable).' % self.getDefaultServiceHost())
        self.addOptionToGroup(serviceGroup, '', '--service-port', dest='servicePort', default=self.getDefaultServicePort(), help='Service port (default: %s, can be set via CDB_SERVICE_PORT environment variable).' % self.getDefaultServicePort())
        self.addOptionToGroup(serviceGroup, '', '--service-protocol', dest='serviceProtocol', default=self.getDefaultServiceProtocol(), help='Service protocol (default: %s, can be set via CDB_SERVICE_PROTOCOL environment variable).' % self.getDefaultServiceProtocol())

        # SSL options, disabled for now.
        #self.addOptionToGroup(commonGroup, '', '--ssl-key', dest='sslKeyFile', help='SSL key file (needed if service requires peer verification, can be set via CDB_SSL_KEY_FILE environment variable).')
        #self.addOptionToGroup(commonGroup, '', '--ssl-cert', dest='sslCertFile', help='SSL certificate file (needed if service requires peer verification, can be set via CDB_SSL_CERT_FILE environment variable).')
        #self.addOptionToGroup(commonGroup, '', '--ssl-ca-cert', dest='sslCaCertFile', help='SSL CA certificate file (needed if client requires peer verification, can be set via CDB_SSL_CA_CERT_FILE environment variable).')
        
    def getDefaultServiceHost(self):
        return ConfigurationManager.getInstance().getServiceHost()

    def getDefaultServicePort(self):
        return ConfigurationManager.getInstance().getServicePort()

    def getDefaultServiceProtocol(self):
        return ConfigurationManager.getInstance().getServiceProtocol()

    def parseArgs(self, usage=None):
        CdbCli.parseArgs(self, usage)
        configManager = ConfigurationManager.getInstance()
        self.serviceHost = self.options.serviceHost
        configManager.setServiceHost(self.serviceHost)
        self.servicePort = self.options.servicePort
        configManager.setServicePort(self.servicePort)
        self.serviceProtocol = self.options.serviceProtocol
        configManager.setServiceProtocol(self.serviceProtocol)

        # SSL options, comment out for now
        #self.sslCaCertFile = self.options.sslCaCertFile
        #if self.sslCaCertFile:
        #    configManager.setSslCaCertFile(self.sslCaCertFile)
        #self.sslCertFile = self.options.sslCertFile
        #if self.sslCertFile:
        #    configManager.setSslCertFile(self.sslCertFile)
        #self.sslKeyFile = self.options.sslKeyFile
        #if self.sslKeyFile:
        #    configManager.setSslKeyFile(self._sslKeyFile)

