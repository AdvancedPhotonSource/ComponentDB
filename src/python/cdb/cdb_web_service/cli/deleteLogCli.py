#!/usr/bin/env python

"""
Copyright (c) UChicago Argonne, LLC. All rights reserved.
See LICENSE file.
"""

from cdb.cdb_web_service.api.logRestApi import LogRestApi
from cdbWebServiceCli import CdbWebServiceCli
from cdb.common.exceptions.invalidRequest import InvalidRequest

class DeleteLogCli(CdbWebServiceCli):
    def __init__(self):
        CdbWebServiceCli.__init__(self)
        self.addOption('', '--log-id', dest='logId', help='Id of the log entry to add attachment for')

    def checkArgs(self):
        if self.options.logId is None:
            raise InvalidRequest('Log ID must be provided.')

    def getLogId(self):
        return self.options.logId

    def runCommand(self):
        self.parseArgs(usage="""
    cdb-update-log --log-id=LOG_ID

Description:
    Removes a log entry.
        """)
        self.checkArgs()
        api = LogRestApi(self.getUsername(), self.getPassword(), self.getServiceHost(), self.getServicePort(), self.getServiceProtocol())

        cdbObject = api.deleteLogEntry(self.getLogId())

        print cdbObject.getDisplayString(self.getDisplayKeys(), self.getDisplayFormat())

#######################################################################
# Run command.
def runCommand():
    cli = DeleteLogCli()
    cli.run()

if __name__ == '__main__':
    runCommand()
