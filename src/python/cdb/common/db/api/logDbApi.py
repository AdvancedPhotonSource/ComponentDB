#!/usr/bin/env python

"""
Copyright (c) UChicago Argonne, LLC. All rights reserved.
See LICENSE file.
"""


from cdb.common.db.api.cdbDbApi import CdbDbApi
from cdb.common.db.impl.logHandler import LogHandler


class LogDbApi(CdbDbApi):

    def __init__(self):
        CdbDbApi.__init__(self)
        self.logHandler = LogHandler()

    @CdbDbApi.executeQuery
    def getLogAttachments(self, **kwargs):
        """
        Get all log attachment records.

        :param kwargs:
        :return: CdbObject list of resulting record.
        """
        session = kwargs['session']
        dbLogAttachments = self.logHandler.getLogAttachments(session)
        return self.toCdbObjectList(dbLogAttachments)

    @CdbDbApi.executeQuery
    def getLogs(self, **kwargs):
        """
        Get all log records.

        :param kwargs:
        :return: CdbObject list of resulting record.
        """
        session = kwargs['session']
        dbLogs = self.logHandler.getLogs(session)
        return self.toCdbObjectList(dbLogs)

    @CdbDbApi.executeTransaction
    def addLogTopic(self, name, description, **kwargs):
        """
        Add a new log topic.

        :param name:
        :param description:
        :param kwargs:
        :return: (CdbObject) newly added record.
        """
        session = kwargs['session']
        dbLogTopic = self.logHandler.addLogTopic(session, name, description)
        return dbLogTopic.getCdbObject()

    @CdbDbApi.executeQuery
    def getLogTopics(self, **kwargs):
        """
        Get all log topics.

        :param kwargs:
        :return: CdbObject list of resulting record.
        """
        session = kwargs['session']
        dbLogTopics = self.logHandler.getLogTopics(session)
        return self.toCdbObjectList(dbLogTopics)

    @CdbDbApi.executeTransaction
    def addLogAttachment(self, logId, attachmentName, attachmentTag, attachmentDescription, **kwargs):
        """
        Add a new log attachment record.

        Should typically be performed from the portal since unique names are generated.

        :param logId:
        :param attachmentName:
        :param attachmentTag:
        :param attachmentDescription:
        :param kwargs:
        :return: (CdbObject) newly added record.
        """
        session = kwargs['session']
        dbLogAttachment = self.logHandler.addLogAttachment(session, logId, attachmentName, attachmentTag, attachmentDescription)
        return dbLogAttachment.getCdbObject()

    @CdbDbApi.executeQuery
    def getLogEntriesForItemElementId(self, itemElementId, logLevelName = None, **kwargs):
        """
        Get all logs for a particular item element id.

        NOTE: logs of a particular item are in their 'self element'.

        :param itemElementId:
        :param logLevelName:
        :param kwargs:
        :return: (CdbObject) resulting record.
        """
        session = kwargs['session']
        dbLogs = self.logHandler.getLogEntriesForItemElementId(session, itemElementId, logLevelName)
        return self.toCdbObjectList(dbLogs)




#######################################################################
# Testing.
if __name__ == '__main__':
    api = LogDbApi()

    print api.getLogAttachments()
    print api.getLogs()

    print api.addLogAttachment(1, 'hello world', 'hi', 'desc')



