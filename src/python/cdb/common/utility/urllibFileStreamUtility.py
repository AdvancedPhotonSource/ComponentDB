#!/usr/bin/env python

"""
Copyright (c) UChicago Argonne, LLC. All rights reserved.
See LICENSE file.
"""

import io
import os


class _UrllibFileIO(io.FileIO):
    def __init__(self, name, mode='rb', closefd=True):
        io.FileIO.__init__(self, name, mode, closefd)

        self.__size = statinfo = os.stat(name).st_size

    def __len__(self):
        return self.__size


class UrlLibFileStreamUtility:

    @classmethod
    def getStreamDataObject(cls, filePath, mode='rb'):
        return _UrllibFileIO(filePath, mode)

    @classmethod
    def isStreamDataObject(cls, dataObject):
        return isinstance(dataObject, _UrllibFileIO)
