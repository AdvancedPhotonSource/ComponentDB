#!/usr/bin/env python

"""
Copyright (c) UChicago Argonne, LLC. All rights reserved.
See LICENSE file.
"""


import os
import urllib

from cdb.common.utility.encoder import Encoder
from cdb.common.exceptions.cdbException import CdbException
from cdb.common.api.cdbRestApi import CdbRestApi


class FileSystemRestApi(CdbRestApi):
    
    def __init__(self, username=None, password=None, host=None, port=None, protocol=None):
        CdbRestApi.__init__(self, username, password, host, port, protocol)

    def getDirectoryList(self, path):
        try:
            directoryName = os.path.basename(path)
            parentDirectory = os.path.dirname(path)
            url = '%s/directories/%s?parentDirectory=%s' % (self.getContextRoot(), directoryName, parentDirectory)
            responseDict = self.sendRequest(url=url, method='GET')
            return responseDict
        except CdbException, ex:
            raise
        except Exception, ex:
            self.getLogger().exception('%s' % ex)
            raise CdbException(exception=ex)

    def writeFile(self, path, content):
        try:
            fileName = os.path.basename(path)
            parentDirectory = os.path.dirname(path)
            encodedFileContent = Encoder.encode(content)
            url = '%s/files/%s?parentDirectory=%s&encodedFileContent=%s' % (self.getContextRoot(), fileName, parentDirectory, encodedFileContent)
            responseDict = self.sendSessionRequest(url=url, method='POST')
            return responseDict
        except CdbException, ex:
            raise
        except Exception, ex:
            self.getLogger().exception('%s' % ex)
            raise CdbException(exception=ex)

#######################################################################
# Testing.

if __name__ == '__main__':
    #api = FileSystemRestApi('sveseli', 'sveseli', 'zagreb.svdev.net', 10232, 'https')
    api = FileSystemRestApi('sveseli', 'sveseli', 'zagreb.svdev.net', 10232, 'http')
    print api.getDirectoryList('/home/sveseli')
    print api.writeFile('/tmp/xyz', 'Hi there, qweqweqsad \dsdd')



