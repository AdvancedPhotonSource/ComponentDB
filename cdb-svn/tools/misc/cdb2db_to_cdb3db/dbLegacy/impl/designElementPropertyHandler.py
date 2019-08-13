#!/usr/bin/env python

from sqlalchemy import and_
from sqlalchemy.orm.exc import NoResultFound

from dbLegacy.entities.designElementProperty import DesignElementProperty
from cdb.common.exceptions.objectNotFound import ObjectNotFound
from dbLegacy.impl.cdbDbEntityHandler import CdbDbEntityHandler

class DesignElementPropertyHandler(CdbDbEntityHandler):

    def __init__(self):
        CdbDbEntityHandler.__init__(self)

    def findDesignElementProperty(self, session, designElementId, propertyValueId):
        try:
            dbDesignElementProperty = session.query(DesignElementProperty).filter(and_(DesignElementProperty.design_element_id==designElementId, DesignElementProperty.property_value_id==propertyValueId)).one()
            return dbDesignElementProperty
        except NoResultFound, ex:
            raise ObjectNotFound('Property for design element id %s with value id %s does not exist.' % (designElementId, propertyValueId))

    def addDesignElementProperty(self, session, dbDesignElement, dbPropertyValue):
        dbDesignElementProperty = DesignElementProperty()
        dbDesignElementProperty.designElement = dbDesignElement
        dbDesignElementProperty.propertyValue = dbPropertyValue
        session.add(dbDesignElementProperty)
        session.flush()
        return dbDesignElementProperty


