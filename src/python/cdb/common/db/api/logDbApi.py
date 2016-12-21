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
    def verifyUserCreatedLogEntry(self, userId, logId, **kwargs):
        """
        Checks whether a user created the log entry.

        :param userId:
        :param logId:
        :raises InvalidSession: whenever a user has not created the log.
        :param kwargs:
        :return: True when user created the log
        """
        session = kwargs['session']
        return self.logHandler.verifyUserCreatedLogEntry(session, userId, logId=logId)

    @CdbDbApi.executeQuery
    def getLogById(self, logId, **kwargs):
        """
        Finds a log entry by its id

        :param logId:
        :param kwargs:
        :return:
        """
        session = kwargs['session']
        dbLog = self.logHandler.findLogById(session, logId)
        return dbLog.getCdbObject()

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
    def addLogAttachment(self, logId, attachmentName, attachmentTag, attachmentDescription, userAddingId, **kwargs):
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
        dbLogAttachment = self.logHandler.addLogAttachment(session, logId, attachmentName, attachmentTag,
                                                           attachmentDescription, userAddingId)
        return dbLogAttachment.getCdbObject()

    @CdbDbApi.executeQuery
    def getLogEntriesForItemElementId(self, itemElementId, logLevelName=None, **kwargs):
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

    @CdbDbApi.executeTransaction
    def updateLogEntry(self, logId, enteredByUserId, text=None, effectiveFromDateTime=None,
                       effectiveToDateTime=None, logTopicName=None, **kwargs):
        """
        Update a log entry given its id.

        :param logId:
        :param text:
        :param enteredByUserId:
        :param effectiveFromDateTime:
        :param effectiveToDateTime:
        :param logTopicName:
        :param kwargs:
        :return: Updated log entry.
        """
        session = kwargs['session']
        dbUpdatedLog = self.logHandler.updateLog(session, logId, enteredByUserId, text, effectiveFromDateTime,
                                                 effectiveToDateTime, logTopicName)
        return dbUpdatedLog.getCdbObject()

    @CdbDbApi.executeTransaction
    def deleteLogEntry(self, logId, userId, **kwargs):
        """
        Removes a log entry with given id.

        :param logId:
        :param userId:
        :param kwargs:
        :raises An exception when the delete fails.
        """
        session = kwargs['session']
        self.logHandler.deleteLog(session, logId, userId)

#######################################################################
# Testing.
if __name__ == '__main__':
    api = LogDbApi()

    print api.getLogAttachments()
    print api.getLogs()

    print api.addLogAttachment(1, 'hello world', 'hi', 'desc')
