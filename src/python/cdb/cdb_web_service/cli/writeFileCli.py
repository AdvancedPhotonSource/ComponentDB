#!/usr/bin/env python

"""
Copyright (c) UChicago Argonne, LLC. All rights reserved.
See LICENSE file.
"""


from cdbWebServiceSessionCli import CdbWebServiceSessionCli
from cdb.cdb_web_service.api.fileSystemRestApi import FileSystemRestApi
from cdb.common.exceptions.invalidRequest import InvalidRequest

class WriteFileCli(CdbWebServiceSessionCli):
    def __init__(self):
        CdbWebServiceSessionCli.__init__(self)
        self.addOption('', '--path', dest='path', help='File path.')
        self.addOption('', '--content', dest='content', help='File content.')

    def checkPath(self):
        if self.options.path is None:
            raise InvalidRequest('Missing path.')

    def getPath(self):
        return self.options.path

    def getContent(self):
        if self.options.content is None:
            return ''
        return self.options.content

    def runCommand(self):
        self.parseArgs(usage="""
    cdb-write-file --path=PATH --content=CONTENT

Description:
    Writes file.
        """)
        self.checkPath()
        api = FileSystemRestApi(self.getUsername(), self.getPassword(), self.getServiceHost(), self.getServicePort(), self.getServiceProtocol())
        print api.writeFile(self.getPath(), self.getContent())

#######################################################################
# Run command.
if __name__ == '__main__':
    cli = WriteFileCli()
    cli.run()
