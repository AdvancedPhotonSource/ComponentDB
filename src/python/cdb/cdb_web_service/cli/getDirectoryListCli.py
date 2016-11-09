#!/usr/bin/env python

"""
Copyright (c) UChicago Argonne, LLC. All rights reserved.
See LICENSE file.
"""


from cdbWebServiceCli import CdbWebServiceCli
from cdb.cdb_web_service.api.fileSystemRestApi import FileSystemRestApi
from cdb.common.exceptions.invalidRequest import InvalidRequest

class GetDirectoryListCli(CdbWebServiceCli):
    def __init__(self):
        CdbWebServiceCli.__init__(self)
        self.addOption('', '--path', dest='path', help='Directory path.')

    def checkPath(self):
        if self.options.path is None:
            raise InvalidRequest('Missing path.')

    def getPath(self):
        return self.options.path

    def runCommand(self):
        self.parseArgs(usage="""
    cdb-get-directory-list --path=PATH

Description:
    Retrieves directory listing.
        """)
        self.checkPath()
        api = FileSystemRestApi(self.getUsername(), self.getPassword(), self.getServiceHost(), self.getServicePort(), self.getServiceProtocol())
        print api.getDirectoryList(self.getPath())

#######################################################################
# Run command.
if __name__ == '__main__':
    cli = GetDirectoryListCli()
    cli.run()
