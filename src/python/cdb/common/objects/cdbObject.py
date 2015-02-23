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
        for (key,obj) in self.data.items():
            if isinstance(obj, CdbObject):
                dictRep[key] = obj.getDictRep()
            else:
                if obj is not None:
                    dictRep[key] = obj
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


