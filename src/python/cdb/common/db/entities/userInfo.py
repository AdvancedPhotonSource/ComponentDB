#!/usr/bin/env python

from sqlalchemy.ext.declarative import declarative_base
from sqlalchemy import Column
from sqlalchemy import Integer
from sqlalchemy import String

from cdb.common.db.entities.cdbDbEntity import CdbDbEntity
from cdb.common.objects import userInfo

#class UserInfo(declarative_base(), CdbDbEntity):
class UserInfo(CdbDbEntity):

    #__tablename__ = 'user_info'
    #id = Column(Integer, primary_key=True)
    #username = Column(String)
    #first_name = Column(String)
    #middle_name = Column(String)
    #last_name = Column(String)
    #email = Column(String)
    #password = Column(String)
    #description = Column(String)

    mappedColumnDict = { 
        'first_name' : 'firstName',
        'middle_name' : 'middleName',
        'last_name' : 'lastName',
    }
    cdbObjectClass = userInfo.UserInfo

    def __init__(self, **kwargs):
        CdbDbEntity.__init__(self, **kwargs)



