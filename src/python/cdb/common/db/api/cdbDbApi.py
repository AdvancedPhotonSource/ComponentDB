#!/usr/bin/env python

"""
Copyright (c) UChicago Argonne, LLC. All rights reserved.
See LICENSE file.
"""


from cdb.common.exceptions.cdbException import CdbException
from cdb.common.exceptions.internalError import InternalError
from cdb.common.utility.loggingManager import LoggingManager
from cdb.common.db.impl.dbManager import DbManager

class CdbDbApi:
    """
    Base DB API class.
    """
    def __init__(self):
        self.logger = LoggingManager.getInstance().getLogger(self.__class__.__name__)
        self.dbManager = DbManager.getInstance()
        self.adminGroupName = None

    def setAdminGroupName(self, adminGroupName):
        self.adminGroupName = adminGroupName

    @classmethod
    def executeQuery(cls, func):
        """
        Decorator for read-only DB queries

        Should never be used if data is being changed.
        This decorator allows queries to be performed on db.

        When exceptions occur, logs and re-raises CdbExceptions.

        :param func: function that performs a query on the database.
        :raises CdbException: specific cdb exception
        :return: function with kwargs or session used for the communication with db.
        """
        def query(*args, **kwargs):
            try:
                dbManager = DbManager.getInstance()
                session = dbManager.openSession()
                kwargs['session'] = session
                try:
                    return func(*args, **kwargs)
                except CdbException, ex:
                    raise
                except Exception, ex:
                    cls.getLogger().exception('%s' % ex)
                    raise CdbException(exception=ex)
            finally:
                dbManager.closeSession(session)
        return query

    @classmethod
    def executeTransaction(cls, func):
        """
        Decorator for all DB transactions

        Should always be used whenever data is being changed.
        This decorator ensures that upon exception, everything is reverted to its previous state.

        When exceptions occur, logs and re-raises CdbExceptions.

        :param func: function that performs a query on the database.
        :raises CdbException: specific cdb exception
        :return: function with kwargs or session used for the communication with db.
        """
        def transaction(*args, **kwargs):
            try:
                dbManager = DbManager.getInstance()
                session = dbManager.openSession()
                kwargs['session'] = session
                try:
                    result = func(*args, **kwargs)
                    session.commit()
                    return result
                except CdbException, ex:
                    session.rollback()
                    raise
                except Exception, ex:
                    session.rollback()
                    cls.getLogger().exception('%s' % ex)
                    raise CdbException(exception=ex)
            finally:
                dbManager.closeSession(session)
        return transaction

    @classmethod
    def getLogger(cls):
        """
        Get an instance of a logger for the class.

        :return: logger instance.
        """
        logger = LoggingManager.getInstance().getLogger(cls.__name__)
        return logger

    @classmethod
    def executeConnectionQuery(cls, query):
        """
        Allows execution of simple SQL based query on the CDB db.

        :param query: (String) SQL query.
        :return: query results list.
        """
        connection = None
        try:
            connection = DbManager.getInstance().acquireConnection()
            try:
                return connection.execute(query)
            except CdbException, ex:
                raise
            except Exception, ex:
                cls.getLogger().exception('%s' % ex)
                raise
        finally:
            DbManager.getInstance().releaseConnection(connection)

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

    def toCdbObjectList(self, dbEntityList):
        """
        Converts a sqlAlchemy db entity list to cdb object list.

        :param dbEntityList: cdbDbEntity result list from sqlalchemy
        :return: cdbObjectList: list of type cdbObject
        """
        cdbObjectList = []
        for dbEntity in dbEntityList:
            cdbObjectList.append(dbEntity.getCdbObject())
        return cdbObjectList

#######################################################################
# Testing.
if __name__ == '__main__':
    api = CdbDbApi()


