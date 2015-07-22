#!/usr/bin/env python

from sqlalchemy import and_
from sqlalchemy.orm.exc import NoResultFound

from cdb.common.db.entities.designProperty import DesignProperty
from cdb.common.exceptions.objectNotFound import ObjectNotFound
from cdb.common.db.impl.cdbDbEntityHandler import CdbDbEntityHandler

class DesignPropertyHandler(CdbDbEntityHandler):

    def __init__(self):
        CdbDbEntityHandler.__init__(self)

    def findDesignProperty(self, session, designId, propertyValueId):
        try:
            dbDesignProperty = session.query(DesignProperty).filter(and_(DesignProperty.design_id==designId, DesignProperty.property_value_id==propertyValueId)).one()
            return dbDesignProperty
        except NoResultFound, ex:
            raise ObjectNotFound('Property for design id %s with value id %s does not exist.' % (designId, propertyValueId))

    def addDesignProperty(self, session, dbDesign, dbPropertyValue):
        dbDesignProperty = DesignProperty()
        dbDesignProperty.design = dbDesign
        dbDesignProperty.propertyValue = dbPropertyValue
        session.add(dbDesignProperty)
        session.flush()
        return dbDesignProperty


