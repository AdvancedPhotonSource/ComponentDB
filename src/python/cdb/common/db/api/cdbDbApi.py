#!/usr/bin/env python

from cdb.common.exceptions.cdbException import CdbException
from cdb.common.exceptions.internalError import InternalError
from cdb.common.utility.loggingManager import LoggingManager
from cdb.common.db.impl.dbManager import DbManager

class CdbDbApi:
    """ Base DB API class. """
    def __init__(self):
        self.logger = LoggingManager.getInstance().getLogger(self.__class__.__name__)
        self.dbManager = DbManager.getInstance()

    def getLogger(self):
        return self.logger

    def executeQuery(self, query):
        connection = None
        try:
            connection = self.dbManager.acquireConnection()
            try:
                return connection.execute(query)
            except CdbException, ex:
                raise
            except Exception, ex:
                self.logger.exception('%s' % ex)
                raise
        finally:
            self.dbManager.releaseConnection(connection)


    def loadRelation(self, dbObject, relationName):
        if not relationName in dir(dbObject):
            raise InternalError('Relation %s not valid for class %s'
                % (relationName, dbObject.__class__.__name__))
        o = None
        exec 'o = dbObject.%s' % (relationName)
        return o

    def loadRelations(self, dbObject, optionDict):
        for k in optionDict.keys():
            # The optionDict contains key-value pairs of relation name
            # and a boolean to indicate whether to load that relation
            if not optionDict[k]:
                continue

            try:
                self.loadRelation(dbObject, k)
            except InternalError, ex:
                self.logger.error(ex)

# Testing.
if __name__ == '__main__':
    api = CdbDbApi()


