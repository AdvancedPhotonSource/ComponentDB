#!/usr/bin/env python

"""
Copyright (c) UChicago Argonne, LLC. All rights reserved.
See LICENSE file.
"""
from cdb.cdb_web_service.api.itemRestApi import ItemRestApi
from cdbWebServiceCli import CdbWebServiceCli
from cdb.common.exceptions.invalidRequest import InvalidRequest

class getItemLogs(CdbWebServiceCli):
    def __init__(self):
        CdbWebServiceCli.__init__(self)
        self.addOption('', '--qr-id', dest='qrId', help='QrId of item to get log entries for')

    def checkArgs(self):
        if self.options.qrId is None:
            raise InvalidRequest('Item qrId must be provided.')

    def getQrId(self):
        return self.options.qrId

    def runCommand(self):
        self.parseArgs(usage="""
    cdb-get-item-logs --qr-id=QRID

Description:
    Gets the log entries for an item with a qrId.
        """)
        self.checkArgs()
        api = ItemRestApi(self.getUsername(), self.getPassword(), self.getServiceHost(), self.getServicePort(), self.getServiceProtocol())

        logs = api.getLogEntriesForItemWithQrId(self.getQrId())
        for log in logs:
            print log.getDisplayString(self.getDisplayKeys(), self.getDisplayFormat())

#######################################################################
# Run command.
def runCommand():
    cli = getItemLogs()
    cli.run()

if __name__ == '__main__':
    runCommand()
