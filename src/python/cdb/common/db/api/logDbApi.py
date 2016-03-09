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


#######################################################################
# Testing.
if __name__ == '__main__':
    api = LogDbApi()

    print api.getLogAttachments()
    print api.getLogs()



