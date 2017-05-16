#!/usr/bin/env python

"""
Copyright (c) UChicago Argonne, LLC. All rights reserved.
See LICENSE file.
"""

from cdb.cdb_web_service.api.logRestApi import LogRestApi
from cdbWebServiceCli import CdbWebServiceCli
from cdb.common.exceptions.invalidRequest import InvalidRequest

class UpdateLogCli(CdbWebServiceCli):
    def __init__(self):
        CdbWebServiceCli.__init__(self)
        self.addOption('', '--log-id', dest='logId', help='Id of the log entry to add attachment for')
        self.addOption('', '--text', dest='text', help='Text conatined in the log entry')
        self.addOption('', '--log-topic-name', dest='logTopicName', help='name of the log topic associated with log')
        self.addOption('', '--effective-from-date-time', dest='effectiveFromDateTime', help='date the log is effective from')
        self.addOption('', '--effective-to-date-time', dest='effectiveToDateTime', help='date the log is effective to')

    def checkArgs(self):
        if self.options.logId is None:
            raise InvalidRequest('Log ID must be provided.')

    def getLogId(self):
        return self.options.logId

    def getLogText(self):
        return self.options.text

    def getLogTopicName(self):
        return self.options.logTopicName

    def getEffectiveFromDate(self):
        return self.options.effectiveFromDateTime

    def getEffectiveToDate(self):
        return self.options.effectiveToDateTime

    def runCommand(self):
        self.parseArgs(usage="""
    cdb-update-log --log-id=LOG_ID
        --text=TEXT
        --log-topic-name=LOG_TOPIC_NAME
        --effective-from-date-time=EFFECTIVE_FROM_DATE_TIME
        --effective-to-date-time=EFFECTIVE_TO_DATE_TIME

Description:
    Updates attributes of a log entry.
        """)
        self.checkArgs()
        api = LogRestApi(self.getUsername(), self.getPassword(), self.getServiceHost(), self.getServicePort(), self.getServiceProtocol())

        log = api.updateLogEntry(self.getLogId(), self.getLogText(), self.getEffectiveFromDate(), self.getEffectiveToDate(), self.getLogTopicName())

        print log.getDisplayString(self.getDisplayKeys(), self.getDisplayFormat())

#######################################################################
# Run command.
def runCommand():
    cli = UpdateLogCli()
    cli.run()

if __name__ == '__main__':
    runCommand()
