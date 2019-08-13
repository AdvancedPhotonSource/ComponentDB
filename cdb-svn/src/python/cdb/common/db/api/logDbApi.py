#!/usr/bin/env python

from cdb.common.db.api.cdbDbApi import CdbDbApi
from cdb.common.db.impl.logHandler import LogHandler


class LogDbApi(CdbDbApi):

    def __init__(self):
        CdbDbApi.__init__(self)
        self.logHandler = LogHandler()

    @CdbDbApi.executeQuery
    def getLogAttachments(self, **kwargs):
        session = kwargs['session']
        dbLogAttachments = self.logHandler.getLogAttachments(session)
        return self.toCdbObjectList(dbLogAttachments)

    @CdbDbApi.executeQuery
    def getAttachments(self, **kwargs):
        session = kwargs['session']
        dbAttachments = self.logHandler.getAttachments(session)
        return self.toCdbObjectList(dbAttachments)

    @CdbDbApi.executeQuery
    def getLogs(self, **kwargs):
        session = kwargs['session']
        dbLogs = self.logHandler.getLogs(session)
        return self.toCdbObjectList(dbLogs)

    @CdbDbApi.executeTransaction
    def addLogTopic(self, name, description, **kwargs):
        session = kwargs['session']
        dbLogTopic = self.logHandler.addLogTopic(session, name, description)
        return dbLogTopic.getCdbObject()

    @CdbDbApi.executeQuery
    def getLogTopics(self, **kwargs):
        session = kwargs['session']
        dbLogTopics = self.logHandler.getLogTopics(session)
        return self.toCdbObjectList(dbLogTopics)

    @CdbDbApi.executeTransaction
    def addLogAttachment(self, logId, attachmentName, attachmentTag, attachmentDescription, **kwargs):
        session = kwargs['session']
        dbLogAttachment = self.logHandler.addLogAttachment(session, logId, attachmentName, attachmentTag, attachmentDescription)
        return dbLogAttachment.getCdbObject()

    @CdbDbApi.executeQuery
    def getLogEntriesForItemElementId(self, itemElementId, logLevelName = None, **kwargs):
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



