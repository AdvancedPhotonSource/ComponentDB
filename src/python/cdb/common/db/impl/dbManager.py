#!/usr/bin/env python

"""
Copyright (c) UChicago Argonne, LLC. All rights reserved.
See LICENSE file.
"""


import threading
import os.path
import sqlalchemy 
from sqlalchemy.orm import sessionmaker
from sqlalchemy.orm import mapper
from sqlalchemy.orm import relationship

from cdb.common.exceptions.commandFailed import CommandFailed
from cdb.common.exceptions.configurationError import ConfigurationError
from cdb.common.utility.loggingManager import LoggingManager
from cdb.common.utility.configurationManager import ConfigurationManager
from cdb.common.db.entities import cdbDbEntityMap


class DbManager:
    """ Singleton class for db management. """

    DB_CONNECTION_POOL_SIZE = 10
    DB_CONNECTION_POOL_MAX_OVERFLOW = 2
    DB_CONNECTION_POOL_RECYCYLE_TIME = 600
    DB_CONNECTION_POOL_TIMEOUT = 60
    DB_CONNECTION_LOGGING_FLAG = False

    CONFIG_SECTION_NAME = 'DbManager'
    CONFIG_OPTION_NAME_LIST = [ 'dbSchema', 'dbUser', 'dbPasswordFile', 'dbHost', 'dbPort' ]

    # Singleton.
    __lock = threading.RLock()
    __instance = None

    @classmethod
    def getInstance(cls):
        from cdb.common.db.impl.dbManager import DbManager
        try:
            mgr = DbManager()
        except DbManager, ex:
            mgr = ex
        return mgr 

    def __init__(self):
        DbManager.__lock.acquire()
        try:
            if DbManager.__instance is not None:
                raise DbManager.__instance
            DbManager.__instance = self
            self.lock = threading.RLock()
            self.logger = LoggingManager.getInstance().getLogger(self.__class__.__name__)
            cm = ConfigurationManager.getInstance()
            cm.setOptionsFromConfigFile(DbManager.CONFIG_SECTION_NAME, DbManager.CONFIG_OPTION_NAME_LIST)

            dbUser = cm.getDbUser()
            self.logger.debug('Using DB user: %s' % dbUser)
            dbSchema = cm.getDbSchema()
            dbPasswordFile = cm.getDbPasswordFile()
            self.logger.debug('Using DB password file: %s' % dbPasswordFile)
            dbPassword = open(dbPasswordFile, 'r').readline().strip()
            dbPort = cm.getDbPort()
            dbHost = cm.getDbHost()
            db = cm.getDb()
            self.logger.debug('DB schema: %s' % dbSchema)
            self.logger.debug('DB password file: %s' % dbPasswordFile)
            engineUrl = '%s://%s:%s@%s:%s/%s' % (db, dbUser, dbPassword, dbHost, dbPort, dbSchema)
            #self.logger.debug('Using engine URL: %s' % engineUrl)
            self.engine = sqlalchemy.create_engine(engineUrl, 
                pool_size=DbManager.DB_CONNECTION_POOL_SIZE, 
                max_overflow=DbManager.DB_CONNECTION_POOL_MAX_OVERFLOW, 
                pool_recycle=DbManager.DB_CONNECTION_POOL_RECYCYLE_TIME, 
                echo=DbManager.DB_CONNECTION_LOGGING_FLAG, 
                pool_timeout=DbManager.DB_CONNECTION_POOL_TIMEOUT)
            self.metadata = sqlalchemy.MetaData(engineUrl)

            self.logger.debug('Mapping DB tables')
            for (dbTableName, (dbEntityClass, dbRelationDict)) in cdbDbEntityMap.CDB_DB_ENTITY_MAP.items():
                self.mapTable(dbEntityClass, dbTableName, dbRelationDict)
            self.logger.debug('Initialized SQLalchemy engines')

        finally:
            DbManager.__lock.release()

    def getLogger(self):
        return self.logger

    def initTable(self, tableClass, tableName):
        """ Initialize DB table. """
        self.lock.acquire()
        try:
            tbl = sqlalchemy.Table(tableName, self.metadata, autoload=True)
            tableClass.columns = tbl.columns
            return tbl
        finally:
            self.lock.release()

    def mapTable(self, tableClass, tableName, relationDict):
        """ Map DB table to a given class. """
        self.lock.acquire()
        try:
            table = sqlalchemy.Table(tableName, self.metadata, autoload=True)
            tableClass.columns = table.columns

            # Build relations from specified foreign key columns and other properties.
            tableRelations = {}
            for (name, propertyDict) in relationDict.items():
                lazy = propertyDict.get('lazy')
                parentEntity = propertyDict.get('parentEntity')
                foreignKeyColumns = propertyDict.get('foreignKeyColumns', [])
                if len(foreignKeyColumns):
                    fkList = []
                    for fk in foreignKeyColumns:
                        fkList.append(table.columns.get(fk))
                    tableRelations[name] = relationship(parentEntity, foreign_keys=fkList, lazy=lazy)
                else:
                    tableRelations[name] = relationship(parentEntity, lazy=lazy)

            mapper(tableClass, table, tableRelations)
            return table
        finally:
            self.lock.release()

    def getMetadataTable(self, table):
        return self.metadata.tables[table]

    def openSession(self):
        """ Open db session. """
        self.lock.acquire()
        try:
            Session = sessionmaker(bind=self.engine)
            return Session()
        finally:
            self.lock.release()

    def closeSession(self, session):
        """ Close db session. """
        self.lock.acquire()
        try:
            session.close()
        finally:
            self.lock.release()

    def acquireConnection(self):
        """ Get db connection. """
        self.lock.acquire()
        try:
            return self.engine.connect()
        finally:
            self.lock.release()
                                                                        
    def releaseConnection(self, connection):
        """ Release db connection. """
        self.lock.acquire()
        try:
            if connection:
                connection.close()
        finally:
            self.lock.release()

#######################################################################
# Testing.
if __name__ == '__main__':
    ConfigurationManager.getInstance().setConsoleLogLevel('debug')
    mgr = DbManager.getInstance()
    mgr.acquireConnection()
    print 'Got connection'


