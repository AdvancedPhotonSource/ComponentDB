#!/usr/bin/env python

"""
Copyright (c) UChicago Argonne, LLC. All rights reserved.
See LICENSE file.
"""


import os

class OsUtility:

    @classmethod
    def createDir(cls, path, mode=None):
        """ Create directory if it does not exist already. """
        if not os.path.isdir(path):
            os.makedirs(path)
            if mode is not None:
                os.chmod(path, mode)

    @classmethod
    def removeLink(cls, path):
        """ Remove link on a given path. """
        if not os.path.islink(path):
            return
        os.remove(path)

    @classmethod
    def removeFile(cls, path):
        """ Remove file on a given path. """
        if not os.path.isfile(path):
            return
        os.remove(path)

    @classmethod
    def removeAndIgnoreErrors(cls, path):
        """ Remove file on a given path and ignore any errors. """
        try:
            os.remove(path)
        except Exception, ex:
            pass

    @classmethod
    def removeDir(cls, path):
        """ Remove dir on a given path, even if it is not empty. """
        if not os.path.isdir(path):
            return
        files=os.listdir(path)
        for f in files:
            fullpath=os.path.join(path, f)
            if os.path.islink(fullpath) or not os.path.isdir(fullpath):
                os.remove(fullpath)
            else:
                removeDir(fullpath)
        os.rmdir(path)
        
    @classmethod
    def chownPath(cls, path, uid, gid):
        """ Change owner on a given path recursively.  """
        if os.path.isfile(path):
            os.chown(path, uid, gid)
            return 
        elif os.path.islink(path):
            os.lchown(path, uid, gid)
            return 
        elif os.path.isdir(path):
            files=os.listdir(path)
            for f in files:
                fullpath=os.path.join(path, f)
                chownPath(fullpath, uid, gid)
            os.chown(path, uid, gid)

    @classmethod
    def chownPathByUserName(cls, path, userName):
        """ Change owner on a given path recursively.  """
        import pwd
        user = pwd.getpwnam(userName)
        chownPath(path, user.pw_uid, user.pw_gid)

    @classmethod
    def findFiles(cls, dirPath, fileList=None):
        """ List files in a given directory. Return list of absolute paths.
            Do not follow symbolic links.
        """
        fList = fileList
        if not fList:
            fList = []
        if os.path.isdir(dirPath):
            files = os.listdir(dirPath)
            for f in files:
                fullPath = os.path.join(dirPath, f)
                if os.path.isfile(fullPath):
                    fList.append(fullPath)
                elif os.path.isdir(fullPath):
                    fList = findFiles(fullPath, fList)
        return fList

    @classmethod
    def importNameFromFile(cls, name, filePath):
        """ Import specified name from file. """
        import sys
        import os.path
        dirName = os.path.dirname(filePath)
        moduleName = os.path.basename(filePath).replace('.py', '')
        sys.path = [dirName] + sys.path
        cmd = 'from %s import %s as tmpObject' % (moduleName, name)
        exec cmd
        del sys.path[0]
        return tmpObject

    @classmethod
    def getUserHomeDir(cls):
        """ Get current user home directory. """
        from os.path import expanduser
        home = expanduser('~')
        return home

#######################################################################
# Testing.

if __name__ == '__main__':
    pass
