#!/usr/bin/env python

"""
Copyright (c) UChicago Argonne, LLC. All rights reserved.
See LICENSE file.
"""


import threading
import time
from collections import deque

class ObjectCache:
    """
    Cache objects identified by id. Objects are removed from cache
    based on the last accessed algorithm.
    """

    # How much larger than object cache should time stamp deq be
    # allowed to grow.
    DEFAULT_TIME_STAMP_DEQ_SIZE_FACTOR = 2
    # Cache info expiration time.
    DEFAULT_OBJECT_LIFETIME = 60 # seconds

    def __init__(self, cacheSize, objectLifetime=DEFAULT_OBJECT_LIFETIME):
        self.lock = threading.RLock()
        self.objectMap = {} # id/object map
        self.timeStampDeq = deque() # timeStamp deq
        self.cacheSize = cacheSize
        self.objectLifetime = objectLifetime
        self.deqSize = ObjectCache.DEFAULT_TIME_STAMP_DEQ_SIZE_FACTOR*cacheSize

    def setCacheSize(self, cacheSize):
        self.cacheSize = cacheSize

    def setObjectLifetime(self, objectLifetime):
        self.objectLifetime = objectLifetime

    def __purgeOne(self):
        # Get rid of one cached item based on the last accessed algorithm.
        while True:
            deqEntry = self.timeStampDeq.popleft()
            oldId = deqEntry[0]
            cachedEntry = self.objectMap.get(oldId)
            if cachedEntry is not None:
                # Timestamp entry is valid.
                if cachedEntry == deqEntry:
                    # Found an old item, get rid of it from the cache.
                    del self.objectMap[oldId]
                    break
        # Done.
        return

    def __purgeTimeStampDeq(self):
        # Get rid of stale entries.
        timeStampDeq = deque()
        while len(self.timeStampDeq):
            deqEntry = self.timeStampDeq.popleft()
            id = deqEntry[0]
            cachedEntry = self.objectMap.get(id)
            if cachedEntry is not None:
                # Timestamp entry is valid.
                if cachedEntry == deqEntry:
                    # Found current item, keep it.
                    timeStampDeq.append(deqEntry)
        # Done.
        self.timeStampDeq = timeStampDeq 
        return

    def put(self, id, item, objectLifetime=None):
        updateTime = time.time()
        expirationTime = updateTime + self.objectLifetime
        if objectLifetime is not None:
            expirationTime = updateTime + objectLifetime
        entry = (id, item, updateTime, expirationTime)
        self.lock.acquire()
        try:
            self.objectMap[id] = entry
            self.timeStampDeq.append(entry)
            if len(self.objectMap) > self.cacheSize:
                self.__purgeOne()
            if len(self.timeStampDeq) > self.deqSize:
                self.__purgeTimeStampDeq()
        
        finally:
            self.lock.release()

    def get(self, id):
        return self.objectMap.get(id)

    def remove(self, id):
        self.lock.acquire()
        try:
            item = self.objectMap.get(id)
            if item is not None:
                del self.objectMap[id]
            return item
        finally:
            self.lock.release()

    def isEmpty(self):
        return len(self.objectMap) == 0

    def size(self):
        return len(self.objectMap) 

    def __str__(self):
        return '%s' % self.timeStampDeq

#######################################################################
# Testing.
if __name__ == '__main__':
    c = ObjectCache(3)

    class Item:
        def __init__(self, id):
            self.id = id
        def getId(self):
            return self.id
        def __str__(self):
            return '%s' % self.id

    class Item2:
        def __init__(self, name):
            self.name = name 
        def getName(self):
            return self.name
        def __str__(self):
            return '%s' % self.name

    for i in range(0,5):
        item = Item(i)
        c.put(i, item)
        print 'Added item: ', item
        print 'Cache: ', c
        time.sleep(1)

    for j in range(0,3):
        item = Item(2)
        c.put(2, item)
        print 'Updated item: ', item
        print 'Cache: ', c
        time.sleep(1)

    item = c.remove(2)
    print 'Deleted item 2: ', item
    print 'Cache: ', c
    time.sleep(1)
    item = c.get(2)
    print 'Got item 2: ', item
    print 'Cache: ', c
    print
    time.sleep(1)

    print
    c = ObjectCache(3)
    c.put('sv', Item2('sv')) 
    print c
    i = c.get('sv')
    print i
    print 'Done'

