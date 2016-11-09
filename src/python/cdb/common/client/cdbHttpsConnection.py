#!/usr/bin/env python

"""
Copyright (c) UChicago Argonne, LLC. All rights reserved.
See LICENSE file.
"""


import socket
import httplib
import ssl

from cdb.common.utility.configurationManager import ConfigurationManager

class CdbHttpsConnection(httplib.HTTPSConnection):

    def __init__(self, hostPort, timeout):
        cm = ConfigurationManager.getInstance()
        args = hostPort.split(':')
        host = args[0]
        if len(args) > 1:
            port = int(args[1])
        else:
            port = cm.getServicePort()
        keyFile = cm.getSslKeyFile()
        certFile = cm.getSslCertFile()
        caCertFile = cm.getSslCaCertFile()
        certChain = None
        strict = True
        httplib.HTTPSConnection.__init__(self, host, port, keyFile, certFile, strict, timeout)
        context = self.getContext(keyFile, certFile, caCertFile, certChain)
        sock = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
        sock.setsockopt(socket.SOL_SOCKET, socket.SO_REUSEADDR, 1)
        self.sock = context.wrap_socket(sock)
        self.connect()

    def connect(self):
        self.sock.connect((self.host,self.port))

    def getContext(self, keyFile, certFile, caCertFile=None, certChain=None):
        """Return SSL Context from self attributes."""
        #context = ssl.SSLContext(ssl.PROTOCOL_SSLv23)
        context = ssl.SSLContext(ssl.PROTOCOL_TLSv1_2)
        if caCertFile is not None:
            context.verify_mode = ssl.CERT_REQUIRED
            context.load_verify_locations(caCertFile)
        else:
            context.verify_mode = ssl.CERT_NONE
        if certFile is not None and keyFile is not None:
            context.load_cert_chain(certFile, keyFile)
        if certChain:
            context.load_verify_locations(certChain)
        return context

#######################################################################
# Testing.

if __name__ == '__main__':
    pass

