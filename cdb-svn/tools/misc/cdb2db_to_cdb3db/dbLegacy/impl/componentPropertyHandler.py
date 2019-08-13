#!/usr/bin/env python

from sqlalchemy import and_
from sqlalchemy.orm.exc import NoResultFound

from dbLegacy.entities.componentProperty import ComponentProperty
from cdb.common.exceptions.objectNotFound import ObjectNotFound
from dbLegacy.impl.cdbDbEntityHandler import CdbDbEntityHandler

class ComponentPropertyHandler(CdbDbEntityHandler):

    def __init__(self):
        CdbDbEntityHandler.__init__(self)

    def findComponentProperty(self, session, componentId, propertyValueId):
        try:
            dbComponentProperty = session.query(ComponentProperty).filter(and_(ComponentProperty.component_id==componentId, ComponentProperty.property_value_id==propertyValueId)).one()
            return dbComponentProperty
        except NoResultFound, ex:
            raise ObjectNotFound('Property for component id %s with value id %s does not exist.' % (componentId, propertyValueId))

    def addComponentProperty(self, session, dbComponent, dbPropertyValue):
        dbComponentProperty = ComponentProperty()
        dbComponentProperty.component = dbComponent
        dbComponentProperty.propertyValue = dbPropertyValue
        session.add(dbComponentProperty)
        session.flush()
        return dbComponentProperty


