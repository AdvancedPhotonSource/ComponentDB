#!/usr/bin/env python

from sqlalchemy.orm.exc import NoResultFound
from cdb.common.exceptions.objectNotFound import ObjectNotFound
from cdb.common.exceptions.objectAlreadyExists import ObjectAlreadyExists

from cdb.common.utility.loggingManager import LoggingManager

class CdbDbEntityHandler:
    def __init__(self):
        self.logger = LoggingManager.getInstance().getLogger(self.__class__.__name__)

    def getLogger(self):
        return self.logger

    @classmethod
    def toIntegerFromBoolean(cls, value):
        if value:
            return 1
        else:
            return 0

    def _getEntityDisplayName(self, entityDbObject):
        if hasattr(entityDbObject, 'entityDisplayName'):
            return entityDbObject.entityDisplayName
        else:
            return entityDbObject.__name__.lower()

    def _getAllDbObjects(self, session, entityDbObject):
        entityDisplayName = self._getEntityDisplayName(entityDbObject)
        self.logger.debug('Retrieving %s list' % entityDisplayName)
        dbObjects = session.query(entityDbObject).all()
        return dbObjects

    def _findDbObjByName(self, session, entityDbObject, name):
        entityDisplayName = self._getEntityDisplayName(entityDbObject)
        try:
            dbObj = session.query(entityDbObject).filter(entityDbObject.name==name).one()
            return dbObj
        except NoResultFound, ex:
            raise ObjectNotFound('No %s with name %s exist.' % (entityDisplayName, name))

    def _findDbObjById(self, session, entityDbObject, id):
        entityDisplayName = self._getEntityDisplayName(entityDbObject)
        try:
            dbObj = session.query(entityDbObject).filter(entityDbObject.id==id).one()
            return dbObj
        except NoResultFound, ex:
            raise ObjectNotFound('No %s with id %s exists.' % (entityDisplayName, id))

    def _prepareAddUniqueNameObj(self, session, entityDbObject, name):
        entityDisplayName = self._getEntityDisplayName(entityDbObject)
        self.logger.debug('Adding %s %s' % (entityDisplayName, name))
        try:
            session.query(entityDbObject).filter(entityDbObject.name==name).one()
            raise ObjectAlreadyExists('%s %s already exists.' % (entityDisplayName, name))
        except NoResultFound, ex:
            # ok
            pass

    def _addSimpleNameDescriptionTable(self, session, entityDbObject, name, description):
        self._prepareAddUniqueNameObj(session, entityDbObject, name)
        entityDisplayName = self._getEntityDisplayName(entityDbObject)

        # Create Entity Db Object
        dbObject = entityDbObject(name=name)
        if description:
            dbObject.description = description

        session.add(dbObject)
        session.flush()
        self.logger.debug('Inserted %s id %s' % (entityDisplayName, dbObject.id))
        return dbObject
