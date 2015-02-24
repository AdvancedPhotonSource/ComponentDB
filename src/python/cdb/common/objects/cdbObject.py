#!/usr/bin/env python

#
# CDB Object class.
#

#######################################################################

import UserDict
import UserList
import types
import json

from cdb.common.exceptions.invalidArgument import InvalidArgument
from cdb.common.utility import loggingManager

class CdbObject(UserDict.UserDict):
    """ Base cdb object class. """
    displayKeyList = [ 'id', 'name' ]

    def __init__(self, dict={}):
        if isinstance(dict, types.DictType): 
            UserDict.UserDict.__init__(self, dict)
        elif isinstance(dict, UserDict.UserDict):
            UserDict.UserDict.__init__(self, dict.data)
        else:
            raise InvalidArgument('CdbObject instance must be initialized using dictionary.')
        self.jsonPreprocessKeyList = []
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

    def getDictRep(self):
        # Dict representation is dict
        dictRep = {}
        for (key,value) in self.data.items():
            if isinstance(value, CdbObject):
                dictRep[key] = value.getDictRep()
            elif type(value) == types.ListType:
                itemList = []
                for item in value:
                    if isinstance(item, CdbObject):
                        itemList.append(item.getDictRep())
                    else:
                        itemList.append(item)
                dictRep[key] = itemList
            else:
                if value is not None:
                    dictRep[key] = value
        return dictRep

    def getDictJsonPreprocessedRep(self):
        dictRep = self.getDictRep()
        # Convert designated keys into string values.
        for key in self.jsonPreprocessKeyList:
            value = dictRep.get(key)
            if value is not None:
                dictRep[key] = '%s' % value
        return dictRep

    def getJsonRep(self):
        dictRep = self.getDictJsonPreprocessedRep()
        return json.dumps(dictRep)

    @classmethod 
    def fromJsonString(cls, jsonString):
        return cls.getFromDict(json.loads(jsonString))

    def display(self, keyList=[]):
        displayKeyList = keyList
        if not keyList:
            displayKeyList = self.displayKeyList
        display = ''
        for key in displayKeyList:
            if self.has_key(key):
                value = self.get(key)
                if isinstance(value, CdbObject):
                    display = display + '%s={ %s} ' % (key, value.display())
                elif isinstance(value, types.ListType):
                    display = display + '%s=[ ' % key
                    for item in value:
                        if isinstance(item, CdbObject):
                            display = display + '{ %s}, ' % (item)
                        else:
                            display = display + ' %s, ' % (item)
                    display = display + '] '
                else:
                    display = display + '%s=%s ' % (key, value)
        return display

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
    print x2.display()


