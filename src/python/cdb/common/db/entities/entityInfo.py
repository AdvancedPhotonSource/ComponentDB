#!/usr/bin/env python

from sqlalchemy import Column
from sqlalchemy import Integer
from sqlalchemy import ForeignKey
from sqlalchemy.ext.declarative import declarative_base

from cdb.common.db.entities.cdbDbEntity import CdbDbEntity
from cdb.common.objects import entityInfo

#class EntityInfo(declarative_base(), CdbDbEntity):
class EntityInfo(CdbDbEntity):

    #__tablename__ = 'entity_info'
    #id = Column(Integer, primary_key=True)
    #owner_user_id = Column(Integer, ForeignKey('user_info.id'))

    mappedColumnDict = { 
        'owner_user_id' : 'ownerUserId',
        'owner_user_group_id' : 'ownerUserGroupId',
        'created_on_date_time' : 'createdOnDateTime',
        'created_by_user_id' : 'createdByUserId',
        'last_modified_by_user_id' : 'lastModifiedByUserId',
        'last_modified_on_date_time' : 'lastModifiedOnDateTime',
        'obsoleted_by_user_id' : 'obsoletedByUserId',
        'obsoleted_on_date_time' : 'obsoletedOnDateTime',
        'is_group_writeable' : 'isGroupWriteable',
    }
    cdbObjectClass = entityInfo.EntityInfo

    def __init__(self, **kwargs):
        CdbDbEntity.__init__(self, **kwargs)


    def loadRelationships(self):
        self.ownerUserInfo

