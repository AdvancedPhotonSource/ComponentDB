#!/usr/bin/env python

"""
Copyright (c) UChicago Argonne, LLC. All rights reserved.
See LICENSE file.
"""
from cdb.cdb_web_service.api.itemRestApi import ItemRestApi
from cdb.cdb_web_service.api.logRestApi import LogRestApi
from cdbWebServiceCli import CdbWebServiceCli
from cdb.common.exceptions.invalidRequest import InvalidRequest

class addItemLogAttachment(CdbWebServiceCli):
    def __init__(self):
        CdbWebServiceCli.__init__(self)
        self.addOption('', '--log-id', dest='logId', help='Id of the log entry to add attachment for')
        self.addOption('', '--attachment', dest='attachment', help='Attachment to add to the log entry.')
        self.addOption('', '--attachment-description', dest='attachmentDescription', help='Attachment description')

    def checkArgs(self):
        if self.options.logId is None:
            raise InvalidRequest('Log ID must be provided.')
        if self.options.attachment is None:
            raise InvalidRequest('Attachment must be provided.')

    def getLogId(self):
        return self.options.logId

    def getAttachment(self):
        return self.options.attachment

    def getAttachmentDescription(self):
        return self.options.attachmentDescription

    def runCommand(self):
        self.parseArgs(usage="""
    cdb-add-log-attachment --log-id=LOG_ID
        --attachment=ATTACHMENT
        --attachment-description=ATTACHMENT_DESCRIPTION

Description:
    Adds an attachment to a log.
        """)
        self.checkArgs()
        api = LogRestApi(self.getUsername(), self.getPassword(), self.getServiceHost(), self.getServicePort(), self.getServiceProtocol())

        logAttachment = api.addLogAttachment(self.getLogId(), self.getAttachment(), self.getAttachmentDescription())

        print logAttachment.getDisplayString(self.getDisplayKeys(), self.getDisplayFormat())

#######################################################################
# Run command.
def runCommand():
    cli = addItemLogAttachment()
    cli.run()

if __name__ == '__main__':
    runCommand()
