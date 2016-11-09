#!/usr/bin/env python

"""
Copyright (c) UChicago Argonne, LLC. All rights reserved.
See LICENSE file.
"""


#
# CDB Object class.
#

#######################################################################

import UserDict
import UserList
import types
import json
import datetime

from cdb.common.exceptions.invalidArgument import InvalidArgument
from cdb.common.utility import loggingManager

class CdbObject(UserDict.UserDict):
    """ Base cdb object class. """
    ALL_KEYS = '__all__'
    DEFAULT_KEYS = '__default__'

    DICT_DISPLAY_FORMAT = 'dict'
    TEXT_DISPLAY_FORMAT = 'text'
    JSON_DISPLAY_FORMAT = 'json'

    DEFAULT_KEY_LIST = [ 'id', 'name' ]

    def __init__(self, dict={}):
        if isinstance(dict, types.DictType): 
            UserDict.UserDict.__init__(self, dict)
        elif isinstance(dict, UserDict.UserDict):
            UserDict.UserDict.__init__(self, dict.data)
        else:
            raise InvalidArgument('CdbObject instance must be initialized using dictionary.')
        self.logger = None

    def getLogger(self):
        if not self.logger:
            self.logger = loggingManager.getLogger(self._class__.__name__)
        return self.logger

    @classmethod
    def getFromDict(cls, dict):
        inst = cls()
        for key in dict.keys():
            inst[key] = dict[key]
        return inst

    def getRepKeyList(self, keyList):
        if keyList is None:
            return self.DEFAULT_KEY_LIST
        elif type(keyList) == types.ListType:
            if not len(keyList):
                return self.DEFAULT_KEY_LIST
            else:
                return keyList
        elif type(keyList) == types.StringType:
            if keyList == CdbObject.ALL_KEYS:
                return self.data.keys()
            elif keyList == CdbObject.DEFAULT_KEYS:
                return self.DEFAULT_KEY_LIST
            else:
                # Assume keys are separated by comma
                return keyList.split(',')
        else: 
            # Unknown key list parameter.
            raise InvalidArgument('Key list parameter must be one of: None, string "%s", string "%s", string containing comma-separated keys, or list of strings.' (CdbObject.ALL_KEYS, CdbObject.DEFAULT_KEYS))
            

    def getDictRep(self, keyList=None):
        # Dict representation is dict
        dictRep = {}
        displayKeyList = self.getRepKeyList(keyList)
        for key in displayKeyList:
            value = self.get(key)
            if isinstance(value, CdbObject):
                dictRep[key] = value.getDictRep('__all__')
            elif type(value) == types.ListType:
                itemList = []
                for item in value:
                    if isinstance(item, CdbObject):
                        itemList.append(item.getDictRep('__all__'))
                    else:
                        itemList.append(item)
                dictRep[key] = itemList
            else:
                if value is not None:
                    if isinstance(value, datetime.datetime):
                        dictRep[key] = str(value)
                    else:
                        dictRep[key] = value
        return dictRep

    def getTextRep(self, keyList=None):
        display = ''
        displayKeyList = self.getRepKeyList(keyList)
        for key in displayKeyList:
            value = self.get(key)
            if isinstance(value, CdbObject):
                display = display + '%s={ %s} ' % (key, value.getTextRep())
            elif isinstance(value, types.ListType):
                display = display + '%s=[ ' % key
                for item in value:
                    if isinstance(item, CdbObject):
                        display = display + '{ %s}, ' % (item)
                    else:
                        display = display + ' %s, ' % (item)
                display = display + '] '
            else:
                if value is not None:
                    display = display + '%s=%s ' % (key, value)
        return display

    def getJsonRep(self, keyList=None):
        dictRep = self.getDictRep(keyList)
        return json.dumps(dictRep)

    def getFullJsonRep(self):
        dictRep = self.getDictRep(CdbObject.ALL_KEYS)
        return json.dumps(dictRep)

    @classmethod 
    def fromJsonString(cls, jsonString):
        return cls.getFromDict(json.loads(jsonString))

    def getDisplayString(self, displayKeyList=[], displayFormat=TEXT_DISPLAY_FORMAT):
        """ Get display string. """
        if displayFormat == CdbObject.DICT_DISPLAY_FORMAT:
            return self.getDictRep(displayKeyList)
        elif displayFormat == CdbObject.TEXT_DISPLAY_FORMAT:
            return self.getTextRep(displayKeyList)
        elif displayFormat == CdbObject.JSON_DISPLAY_FORMAT:
            return self.getJsonRep(displayKeyList)
        raise InvalidArgument('Unrecognized display displayFormat: %s.' (displayFormat))


#######################################################################
# Testing.

if __name__ == '__main__':
    x = {'name' : 'XYZ', 'one':1, 'two':2 }
    o = CdbObject(x)
    print 'CDB Object: ', o
    print 'Type of CDB object: ', type(o)
    print 'JSON Rep:  ', o.getJsonRep()
    print 'Type of JSON rep: ', type(o.getJsonRep())
    j = '{"name" : "XYZ", "one":1, "two":2 }'
    print 'String: ', j
    x2 = CdbObject.fromJsonString(j)
    print 'CDB Object 2: ', x2
    print 'Type of CDB object 2: ', type(x2)
    print x2.getDisplayString(displayKeyList='__all__')


