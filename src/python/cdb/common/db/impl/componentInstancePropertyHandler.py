#!/usr/bin/env python

from sqlalchemy import and_
from sqlalchemy.orm.exc import NoResultFound

from cdb.common.db.entities.componentInstanceProperty import ComponentInstanceProperty
from cdb.common.exceptions.objectNotFound import ObjectNotFound
from cdb.common.db.impl.cdbDbEntityHandler import CdbDbEntityHandler

class ComponentInstancePropertyHandler(CdbDbEntityHandler):

    def __init__(self):
        CdbDbEntityHandler.__init__(self)

    def findComponentInstanceProperty(self, session, componentInstanceId, propertyValueId):
        try:
            dbComponentInstanceProperty = session.query(ComponentInstanceProperty).filter(and_(ComponentInstanceProperty.component_instance_id==componentInstanceId, ComponentInstanceProperty.property_value_id==propertyValueId)).one()
            return dbComponentInstanceProperty
        except NoResultFound, ex:
            raise ObjectNotFound('Property for component instance id %s with value id %s does not exist.' % (componentInstanceId, propertyValueId))

    def addComponentInstanceProperty(self, session, dbComponentInstance, dbPropertyValue):
        dbComponentInstanceProperty = ComponentInstanceProperty()
        dbComponentInstanceProperty.componentInstance = dbComponentInstance
        dbComponentInstanceProperty.propertyValue = dbPropertyValue
        session.add(dbComponentInstanceProperty)
        session.flush()
        return dbComponentInstanceProperty


