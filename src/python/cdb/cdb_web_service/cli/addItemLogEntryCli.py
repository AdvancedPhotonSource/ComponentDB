#!/usr/bin/env python

"""
Copyright (c) UChicago Argonne, LLC. All rights reserved.
See LICENSE file.
"""
from cdb.cdb_web_service.api.itemRestApi import ItemRestApi
from cdbWebServiceSessionCli import CdbWebServiceSessionCli
from cdb.common.exceptions.invalidRequest import InvalidRequest

class addItemLogEntry(CdbWebServiceSessionCli):
    def __init__(self):
        CdbWebServiceSessionCli.__init__(self)
        self.addOption('', '--qr-id', dest='qrId', help='QrId of item to add log entry for')
        self.addOption('', '--item-id', dest='itemId', help='Item id of an item to add log entry for')
        self.addOption('', '--log-entry', dest='logEntry', help='Log entry text to add to the item with qrId specified.')
        self.addOption('', '--attachment', dest='attachment', help='Attachment to add along with the log entry.')

    def checkArgs(self):
        if self.options.qrId is None and self.options.itemId is None:
            raise InvalidRequest('Item qrId or Id must be provided.')
        if self.options.logEntry is None:
            raise InvalidRequest('Log Entry must be provided.')

    def getQrId(self):
        return self.options.qrId

    def getItemId(self):
        return self.options.itemId

    def getLogEntry(self):
        return self.options.logEntry

    def getAttachment(self):
        return self.options.attachment

    def runCommand(self):
        self.parseArgs(usage="""
    cdb-add-item-log-entry --qr-id=QRID|--item-id=ITEMID
        --log-entry=LOGENTRY
        --attachment=ATTACHMENT

Description:
    Adds a log entry to an item with a qrId or item Id.
        """)
        self.checkArgs()
        api = ItemRestApi(self.getUsername(), self.getPassword(), self.getServiceHost(), self.getServicePort(), self.getServiceProtocol())
        if self.getItemId() is not None:
            log = api.addLogEntryToItemWithItemId(self.getItemId(), self.getLogEntry(), self.getAttachment())
        else:
            log = api.addLogEntryToItemWithQrId(self.getQrId(), self.getLogEntry(), self.getAttachment())

        print log.getDisplayString(self.getDisplayKeys(), self.getDisplayFormat())

#######################################################################
# Run command.
def runCommand():
    cli = addItemLogEntry()
    cli.run()

if __name__ == '__main__':
    runCommand()
