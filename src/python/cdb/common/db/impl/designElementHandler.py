#!/usr/bin/env python

import datetime
from sqlalchemy import and_
from sqlalchemy.orm.exc import NoResultFound

from cdb.common.exceptions.objectAlreadyExists import ObjectAlreadyExists
from cdb.common.exceptions.objectNotFound import ObjectNotFound
from cdb.common.db.entities.component import Component
from cdb.common.db.entities.design import Design
from cdb.common.db.entities.designElement import DesignElement
from cdb.common.db.entities.designElementProperty import DesignElementProperty
from cdb.common.db.entities.propertyValue import PropertyValue
from cdb.common.db.entities.location import Location
from cdb.common.db.entities.entityInfo import EntityInfo
from cdb.common.db.impl.cdbDbEntityHandler import CdbDbEntityHandler
from designHandler import DesignHandler
from componentHandler import ComponentHandler
from locationHandler import LocationHandler
from entityInfoHandler import EntityInfoHandler
from propertyValueHandler import PropertyValueHandler

class DesignElementHandler(CdbDbEntityHandler):

    def __init__(self):
        CdbDbEntityHandler.__init__(self)
        self.designHandler = DesignHandler()
        self.componentHandler = ComponentHandler()
        self.locationHandler = LocationHandler()
        self.entityInfoHandler = EntityInfoHandler()
        self.propertyValueHandler = PropertyValueHandler()

    def findDesignElementById(self, session, id):
        try:
            dbDesignElement = session.query(DesignElement).filter(DesignElement.id==id).one()
            return dbDesignElement
        except NoResultFound, ex:
            raise ObjectNotFound('Design element id %s does not exist.' % (id))

    def getDesignElements(self, session, parentDesignId):
        self.logger.debug('Retrieving element list for design %s' % parentDesignId)
        dbParentDesign = self.designHandler.getDesignById(session, parentDesignId)
        dbDesignElements = session.query(DesignElement).filter(DesignElement.parent_design_id==parentDesignId).all()
        return dbDesignElements

    def getDesignElementById(self, session, id):
        self.logger.debug('Retrieving design element id %s' % id)
        dbDesignElement = self.findDbDesignElementById(session, id)
        dbDesignElementProperties = session.query(PropertyValue).join(DesignElementProperty).filter(and_(DesignElementProperty.design_element_id==dbDesignElement.id, DesignElementProperty.property_value_id==PropertyValue.id)).all()
        dbDesignElement.propertyValueList = dbDesignElementProperties
        return dbDesignElement

    def getDesignElementByName(self, session, parentDesignId, name):
        try:
            self.logger.debug('Retrieving design element name %s that belongs to design id %s' % (name, parentDesignId))
            dbParentDesign = self.designHandler.getDesignById(session, parentDesignId)
            dbDesignElement = session.query(DesignElement).filter(and_(DesignElement.name==name, DesignElement.parent_design_id==parentDesignId)).one()
            dbDesignElementProperties = session.query(PropertyValue).join(DesignElementProperty).filter(and_(DesignElementProperty.design_element_id==dbDesignElement.id, DesignElementProperty.property_value_id==PropertyValue.id)).all()
            dbDesignElement.propertyValueList = dbDesignElementProperties
            return dbDesignElement
        except NoResultFound, ex:
            raise ObjectNotFound('Design element %s that belongs to design id %s does not exist.' % (name, parentDesignId))

    def addDesignElement(self, session, name, parentDesignId, childDesignId, componentId, locationId, createdByUserId, ownerUserId, ownerGroupId, isGroupWriteable, sortOrder, description):
        try:
            dbParentDesign = self.designHandler.getDesignById(session, parentDesignId)
            dbDesignElement = session.query(DesignElement).filter(and_(DesignElement.name==name, DesignElement.parent_design_id==parentDesignId)).one()
            raise ObjectAlreadyExists('Design element %s that belongs to design id %s already exists.' % (name, parentDesignId))
        except NoResultFound, ex:
            # OK.
            pass

        # Create entity info
        dbEntityInfo = self.entityInfoHandler.createEntityInfo(session, createdByUserId, ownerUserId, ownerGroupId, isGroupWriteable)

        # Create design element
        dbDesignElement = DesignElement(name=name, parent_design_id=dbParentDesign.id, sort_order=sortOrder, description=description)
        dbDesignElement.entityInfo = dbEntityInfo
        if childDesignId is not None:
            dbChildDesign = self.designHandler.findDesignById(session, childDesignId)
            dbDesignElement.child_design_id = dbChildDesign.id
        if componentId is not None:
            dbComponent = self.componentHandler.findComponentById(session, componentId)
            dbDesignElement.component_id = dbComponent.id
        if locationId is not None:
            dbLocation = self.locationHandler.findLocationById(session, locationId)
            dbDesignElement.location_id = dbLocation.id
        session.add(dbDesignElement)
        session.flush()
        self.logger.debug('Inserted design element id %s' % dbDesignElement.id)
        return dbDesignElement

    def addDesignElementProperty(self, session, designElementId, propertyTypeName, tag, value, units, description, enteredByUserId):
        dbDesignElement = self.findDesignElementById(session, designElementId)
        dbPropertyValue = self.propertyValueHandler.createPropertyValue(session, propertyTypeName, tag, value, units, description, enteredByUserId)
        dbDesignElementProperty = DesignElementProperty()
        dbDesignElementProperty.designElement = dbDesignElement 
        dbDesignElementProperty.propertyValue = dbPropertyValue
        session.add(dbDesignElementProperty)
        session.flush()
        return dbDesignElementProperty




