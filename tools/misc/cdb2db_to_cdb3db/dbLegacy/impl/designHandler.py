#!/usr/bin/env python

import datetime
from sqlalchemy import and_
from sqlalchemy.orm.exc import NoResultFound

from cdb.common.exceptions.objectAlreadyExists import ObjectAlreadyExists
from cdb.common.exceptions.objectNotFound import ObjectNotFound
from dbLegacy.entities.design import Design
from dbLegacy.entities.designElement import DesignElement
from dbLegacy.entities.designProperty import DesignProperty
from dbLegacy.entities.propertyValue import PropertyValue
from dbLegacy.entities.entityInfo import EntityInfo
from dbLegacy.impl.cdbDbEntityHandler import CdbDbEntityHandler
from entityInfoHandler import EntityInfoHandler

class DesignHandler(CdbDbEntityHandler):

    def __init__(self):
        CdbDbEntityHandler.__init__(self)
        self.entityInfoHandler = EntityInfoHandler()

    def getDesigns(self, session):
        self.logger.debug('Retrieving design list')
        dbDesigns = session.query(Design).all()
        return dbDesigns

    def findDesignById(self, session, id):
        try:
            dbDesign = session.query(Design).filter(Design.id==id).one()
            return dbDesign
        except NoResultFound, ex:
            raise ObjectNotFound('Design id %s does not exist.' % (id))

    def findDesignByName(self, session, name):
        try:
            dbDesign = session.query(Design).filter(Design.name==name).one()
            return dbDesign
        except NoResultFound, ex:
            raise ObjectNotFound('Design %s does not exist.' % (name))

    # This method will not throw exception if both id and name are none
    def findDesignByIdOrName(self, session, id, name):
        if id is None and name is None:
            return None
        if id is not None:
            return self.findDesignById(session, id)
        return self.findDesignByName(session, name)

    # This method will not throw exception if both id and name are none
    def findDesignIdByIdOrName(self, session, id, name):
        dbDesign = self.findDesignByIdOrName(session, id, name)
        if dbDesign is None:
            return None
        return dbDesign.id

    def getDesignById(self, session, id):
        self.logger.debug('Retrieving design id %s' % id)
        dbDesign = self.findDesignById(session, id)
        dbDesignProperties = session.query(PropertyValue).join(DesignProperty).filter(and_(DesignProperty.design_id==dbDesign.id, DesignProperty.property_value_id==PropertyValue.id)).all()
        dbDesign.propertyValueList = dbDesignProperties
        dbDesignElements = session.query(DesignElement).filter(DesignElement.parent_design_id==dbDesign.id).all()
        dbDesign.designElementList = dbDesignElements
        return dbDesign

    def getDesignByName(self, session, name):
        self.logger.debug('Retrieving design name %s' % name)
        dbDesign = self.findDesignByName(session, name)
        dbDesignProperties = session.query(PropertyValue).join(DesignProperty).filter(and_(DesignProperty.design_id==dbDesign.id, DesignProperty.property_value_id==PropertyValue.id)).all()
        dbDesign.propertyValueList = dbDesignProperties
        dbDesignElements = session.query(DesignElement).filter(DesignElement.parent_design_id==dbDesign.id).all()
        dbDesign.designElementList = dbDesignElements
        return dbDesign

    def addDesign(self, session, name, createdByUserId, ownerUserId, ownerGroupId, isGroupWriteable, description=None):
        try:
            dbDesign = session.query(Design).filter(Design.name==name).one()
            raise ObjectAlreadyExists('Design %s already exists.' % name)
        except NoResultFound, ex:
            # OK.
            pass

        # Create entity info
        dbEntityInfo = self.entityInfoHandler.createEntityInfo(session, createdByUserId, ownerUserId, ownerGroupId, isGroupWriteable)

        # Create design
        dbDesign = Design(name=name, description=description)
        dbDesign.entityInfo = dbEntityInfo
        session.add(dbDesign)
        session.flush()
        self.logger.debug('Inserted design id %s' % dbDesign.id)
        return dbDesign

    def addDesignProperty(self, session, dbDesign, dbPropertyValue):
        dbDesignProperty = DesignProperty()
        dbDesignProperty.design = dbDesign
        dbDesignProperty.propertyValue = dbPropertyValue
        session.add(dbDesignProperty)
        session.flush()
        return dbDesignProperty

