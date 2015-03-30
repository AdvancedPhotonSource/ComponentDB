#!/usr/bin/env python

import datetime
from sqlalchemy import and_
from sqlalchemy.orm.exc import NoResultFound

from cdb.common.exceptions.objectAlreadyExists import ObjectAlreadyExists
from cdb.common.exceptions.objectNotFound import ObjectNotFound
from cdb.common.db.entities.component import Component
from cdb.common.db.entities.design import Design
from cdb.common.db.entities.designElement import DesignElement
from cdb.common.db.entities.location import Location
from cdb.common.db.entities.entityInfo import EntityInfo
from cdb.common.db.impl.cdbDbEntityHandler import CdbDbEntityHandler
from designHandler import DesignHandler
from componentHandler import ComponentHandler
from locationHandler import LocationHandler
from entityInfoHandler import EntityInfoHandler

class DesignElementHandler(CdbDbEntityHandler):

    def __init__(self):
        CdbDbEntityHandler.__init__(self)
        self.designHandler = DesignHandler()
        self.componentHandler = ComponentHandler()
        self.locationHandler = LocationHandler()
        self.entityInfoHandler = EntityInfoHandler()

    def getDesignElements(self, session, parentDesignId):
        self.logger.debug('Retrieving element list for design %s' % parentDesignId)
        dbParentDesign = self.designHandler.getDesignById(session, parentDesignId)
        dbDesignElements = session.query(DesignElement).filter(DesignElement.parent_design_id==parentDesignId).all()
        return dbDesignElements

    def getDesignElementById(self, session, id):
        try:
            self.logger.debug('Retrieving design element id %s' % id)
            dbDesignElement = session.query(DesignElement).filter(DesignElement.id==id).one()
            return dbDesignElement
        except NoResultFound, ex:
            raise ObjectNotFound('Design element id %s does not exist.' % (id))

    def getDesignElementByName(self, session, parentDesignId, name):
        try:
            self.logger.debug('Retrieving design element name %s that belongs to design id %s' % (name, parentDesignId))
            dbParentDesign = self.designHandler.getDesignById(session, parentDesignId)
            dbDesignElement = session.query(DesignElement).filter(and_(DesignElement.name==name, DesignElement.parent_design_id==parentDesignId)).one()
            return dbDesignElement
        except NoResultFound, ex:
            raise ObjectNotFound('Design element %s that belongs to design id %s does not exist.' % (name, parentDesignId))

    def addDesignElement(self, session, name, parentDesignId, childDesignId, componentId, locationId, createdByUserId, ownerUserId, ownerGroupId, isGroupWriteable, sortOrder=None, description=None):
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
        dbDesignElement = DesignElement(name=name, sort_order=sortOrder, description=description)
        dbDesignElement.parentDesign = dbParentDesign
        dbDesignElement.entityInfo = dbEntityInfo
        if childDesignId is not None:
            dbChildDesign = self.designHandler.getDesignById(session, childDesignId)
            dbDesignElement.childDesign = dbChildDesign
        if componentId is not None:
            dbComponent = self.componentHandler.getComponentById(session, componentId)
            dbDesignElement.component = dbComponent
        if locationId is not None:
            dbLocation = self.locationHandler.getLocationById(session, locationId)
            dbDesignElement.location = dbLocation
        session.add(dbDesignElement)
        session.flush()
        self.logger.debug('Inserted design element id %s' % dbDesignElement.id)
        return dbDesignElement

