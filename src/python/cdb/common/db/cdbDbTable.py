#!/usr/bin/env python

from cdb.common.exceptions.dbError import DbError
from cdb.common.utility.loggingManager import LoggingManager

class CdbDbTable(object):
    """ Base Cdb DB table wrapper class. """
    columns = []
    mappedColumnDict = {}

    def __init__(self, **kwargs):
        self.logger = LoggingManager.getInstance().getLogger(self.__class__.__name__)
        for col, value in kwargs.items():
            if col not in self.columns:
                raise DbError('Column %s not found in table %s' % (col, self.__class__.__name__))
            else:
                setattr(self, col, value)

     def getLogger(self):
         return self.logger

     def mapColumn(self, dbName, objectName):
         if self.__dict__.has_key(dbName):
             self.__dict__[objectName] = self.__dict__.get(dbName)

     def mapColumns(self):
         for (dbName, objectName) in self.mappedColumnDict.items():
             self.mapColumn(dbName, objectName)
     
     def __repr__(self):
         s = '%s(' % self.__class__.__name__
         addComma = False
         for col in self.columns:
             colName = col.__str__().split('.')[-1]
             if not addComma:
                 addComma = True
             else:
                 s += ','
             exec 'value = self.%s' % colName
             if value is not None:
                 s += '%s=%s' % (colName, value)
             s += ')'
         return s

     def loadRelations(self):
         """ Load all relations object knows about. """
         pass

     def getCdbObject(self):
         """ 
         Convert the raw database object (self, a dict) into a 
         CDB db object.
         """
         return None
