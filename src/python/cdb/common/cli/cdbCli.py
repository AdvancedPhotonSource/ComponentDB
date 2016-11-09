#!/usr/bin/env python

"""
Copyright (c) UChicago Argonne, LLC. All rights reserved.
See LICENSE file.
"""


import sys
import os
import os.path
import stat
from optparse import OptionGroup

import cdb
from cdb.common.utility.loggingManager import LoggingManager
from cdb.common.utility.configurationManager import ConfigurationManager
from cdb.common.utility.osUtility import OsUtility
from cdb.common.objects.cdbObject import CdbObject
from cdb.common.exceptions.cdbException import CdbException
from cdb.common.exceptions.invalidRequest import InvalidRequest
from cdb.common.exceptions.invalidArgument import InvalidArgument
from cdb.common.exceptions.internalError import InternalError
from cdb.common.cli.cdbOptionParser import CdbOptionParser
from cdb.common.constants import cdbStatus

class CdbCli(object):
    """ Base cdb command line interface class. """
    DEFAULT_SESSION_CACHE_FILE = OsUtility.getUserHomeDir() + '/.cdb/.session.cache'
    ANY_NUMBER_OF_POSITIONAL_ARGS = 10000000

    def __init__(self, validArgCount=0):
        self.logger = LoggingManager.getInstance().getLogger(self.__class__.__name__)
        # Do not log into a file for CLIs
        LoggingManager.getInstance().setFileLogLevel('CRITICAL') 
        LoggingManager.getInstance().setConsoleLogLevel('CRITICAL') 
        self.parser = CdbOptionParser()
        self.options = {}
        self.args = []
        self.validArgCount = validArgCount
        self.optionGroupDict = {}

        commonGroup = 'Common Options'
        self.addOptionGroup(commonGroup, None)
        self.addOptionToGroup(commonGroup, '-h', '--help', action='help', help='Show this help message and exit.')
        self.addOptionToGroup(commonGroup, '-?', '',       action='help', help='Show this help message and exit.')
        self.addOptionToGroup(commonGroup, '-v', '--version', action='store_true', dest='version', default=False, help='Print version and exit.')
        self.addOptionToGroup(commonGroup, '-d', '--debug', dest='consoleLogLevel', help='Set debug level (valid values: CRITICAL, ERROR, WARNING, INFO, DEBUG). Console log level can also be set via CDB_CONSOLE_LOG_LEVEL environment variable,')
        self.addOptionToGroup(commonGroup, '', '--display-format', dest='displayFormat', default=CdbObject.TEXT_DISPLAY_FORMAT, help='Display format for output objects. Possible options are: %s, %s, and %s (default: %s).' % (CdbObject.TEXT_DISPLAY_FORMAT, CdbObject.DICT_DISPLAY_FORMAT, CdbObject.JSON_DISPLAY_FORMAT, CdbObject.TEXT_DISPLAY_FORMAT)) 
        self.addOptionToGroup(commonGroup, '', '--display-keys', dest='displayKeys', default=CdbObject.DEFAULT_KEYS, help='List of output object keys to display. Possible options are: %s, %s, and string containing comma-separated keys (default: %s, represents class default keys).' % (CdbObject.DEFAULT_KEYS, CdbObject.ALL_KEYS, CdbObject.DEFAULT_KEYS)) 

    def getDefaultServiceHost(self):
        return ConfigurationManager.getInstance().getServiceHost()

    def getDefaultServicePort(self):
        return ConfigurationManager.getInstance().getServicePort()

    def getDefaultServiceProtocol(self):
        return ConfigurationManager.getInstance().getServiceProtocol()

    def getUsername(self):
        return None

    def getPassword(self):
        return None

    def getDisplayFormat(self):
        return self.options.displayFormat

    def getDisplayKeys(self):
        return self.options.displayKeys

    def getLogger(self):
        return self.logger

    def getParser(self):
        return self.parser

    def addOption(self, *args, **kwargs):
        self.parser.add_option(*args, **kwargs)

    def addOptionToGroup(self, groupName, *args, **kwargs):
        """ Add group option. Group must be created using addOptionGroup(). """
        group = self.optionGroupDict.get(groupName)
        group.add_option(*args, **kwargs)

    def addOptionGroup(self, groupName, desc):
        group = OptionGroup(self.parser, groupName, desc)
        self.parser.add_option_group(group)
        self.optionGroupDict[groupName] = group

    def processArgs(self):
        pass

    def parseArgs(self, usage=None):
        if usage:
            self.parser.usage = usage
        try:
            (self.options, self.args) = self.parser.parse_args()
            self.processArgs()
        except SystemExit, rc:
            sys.stdout.flush()
            sys.stderr.flush()
            sys.exit(int(str(rc)))

        if self.validArgCount < len(self.args):
            # Positional args are not enabled and we have some
            msg = 'Invalid positional argument(s):'
            for arg in self.args[self.validArgCount:]:
                msg += ' ' + arg
            msg += ' (This command allows %s positional arguments.)' % self.validArgCount
            raise InvalidArgument(msg)

        optDict = self.options.__dict__
        if optDict.get('version'):
            print 'CDB Software Version: %s' % (cdb.__version__)
            sys.exit(0)

        # Logging level. First try from command line, then from env variable.
        consoleLogLevel = optDict.get('consoleLogLevel', None)
        if consoleLogLevel:
            LoggingManager.getInstance().setConsoleLogLevel(consoleLogLevel)
        else:
            consoleLogLevel = ConfigurationManager.getInstance().getConsoleLogLevelFromEnvVar()
            if consoleLogLevel:
                LoggingManager.getInstance().setConsoleLogLevel(consoleLogLevel)


        # Check session cache.
        configManager = ConfigurationManager.getInstance()
        try:
            self.checkSessionCache()
        except Exception, ex:
            self.logger.warn('Disabling session cache: %s' % ex)
            configManager.setSessionCacheFile(None)

        return (self.options, self.args)

    def checkSessionCache(self):
        configManager = ConfigurationManager.getInstance()
        sessionCacheFile = configManager.getSessionCacheFile()
        if sessionCacheFile is None:
            sessionCacheFile = CdbCli.DEFAULT_SESSION_CACHE_FILE 
        sessionCacheFile = sessionCacheFile.strip()
        if len(sessionCacheFile):
            sessionCacheDir = os.path.dirname(sessionCacheFile)
            OsUtility.createDir(sessionCacheDir, stat.S_IRUSR|stat.S_IWUSR|stat.S_IXUSR)
            configManager.setSessionCacheFile(sessionCacheFile)
            configManager.setRequireSessionCredentials(True)

    def usage(self, s=None):
        """ Print help provided by optparse. """
        if s:
            print >>sys.stderr, 'Error: ', s, '\n'
        self.parser.print_help()
        sys.exit(cdbStatus.CDB_ERROR)

    def getOptions(self):
        return self.options

    def getNArgs(self):
        """ Returns the number of command line arguments. """
        return len(self.args)

    def getArgs(self):
        """ Returns the command line argument list. """
        return self.args

    def splitArgsIntoDict(self, keyValueDelimiter=':'):
        """ Returns the command line argument list as dictionary of key/value
        pairs. Each argument is split using specified delimiter. """
        argDict = {}
        for a in self.args:
            sList = a.split(keyValueDelimiter)
            key = sList[0]
            value = ''
            if len(sList) > 1:
                value = keyValueDelimiter.join(sList[1:])
            argDict[key] = value
        return argDict

    def getArg(self, i):
        """ Returns the i-th command line argument. """
        return self.args[i]

    def getServiceHost(self):
        #return self.serviceHost
        return ConfigurationManager.getInstance().getServiceHost()

    def getServicePort(self):
        #return self.servicePort
        return ConfigurationManager.getInstance().getServicePort()

    def getServiceProtocol(self):
        #return self.serviceProtocol
        return ConfigurationManager.getInstance().getServiceProtocol()

    def getSslCaCertFile(self):
        return self.sslCaCertFile

    def getSslCertFile(self):
        return self.sslCertFile

    def getSslKeyFile(self):
        return self.sslKeyFile

    def displayCdbObject(self, cdbObject):
        if isinstance(cdbObject, cdbObject):
            return '%s' % cdbObject.getJsonRep()
        else:
            return '%s' % cdbObject

    def runCommand(self):
        """ This method must be implemented by the derived class. """
        raise InternalError('Method runCommand() must be overriden in the derived class.')

    def run(self):
        """ This method invokes runCommand() and handles any exceptions. """
        try:
            self.runCommand()
        except CdbException, ex:
            self.logger.exception('%s' % ex)
            print '%s' % ex.getErrorMessage()
            raise SystemExit(ex.getErrorCode())
        except SystemExit, ex:
            raise
        except Exception, ex:
            self.logger.exception('%s' % ex)
            print >>sys.stderr, '%s' % ex
            raise SystemExit(cdbStatus.CDB_ERROR)

    def getId(self):
        id = self.options.id
        if id == None:
            raise InvalidRequest('Missing id.')
        return id

    def displayCdbObject(self, cdbObject):
        optDict = self._options.__dict__
        if isinstance(cdbObject, CdbObject):
            if optDict.get('dict'):
                 return '%s' % cdbObject
            else:
                 return cdbObject.display()
        else:
            return '%s' % cdbObject

#######################################################################
# Testing

if __name__ == '__main__':
    cli = CdbCli(3)
    cli.addOption("-f", "--file", dest="filename", help="write report to FILE", metavar="FILE")
    cli.addOption("-q", "--quiet", action="store_false", dest="verbose", default=True, help="don't print log messages to stdout")
    (options, args) = cli.parseArgs()
    print 'After parse:'
    print 'OPTIONS: ', options
    print 'ARGS: ', args
    print 'From CLI'
    print 'OPTIONS: ', cli.getOptions()
    print 'ARGS: ', cli.getArgs()
    print
    print 'FILENAME'
    print 'options.filename', options.filename
    print 'cli.getOptions().filename', cli.getOptions().filename
    o = cli.getOptions()
    print 'o.filename', o.filename
    print 'cli.getArgs()', cli.getArgs()
    print 'len(cli.getArgs())', len(cli.getArgs())

    for a in cli.getArgs():
        print 'arg', a

    first_arg = cli.getArg(0)
    print 'first_arg', first_arg

    second_arg = cli.getArg(1)
    print 'second_arg', second_arg

    try:
        third_arg = cli.getArg(2)
        print 'third_arg', third_arg
    except:
        print 'no third arg'
