#!/usr/bin/env python

"""
Copyright (c) UChicago Argonne, LLC. All rights reserved.
See LICENSE file.
"""


#
# Logging manager singleton.
#

#######################################################################

import re
import sys
import os.path
import logging
from cdb.common.utility.configurationManager import ConfigurationManager
from cdb.common.exceptions.configurationError import ConfigurationError

#######################################################################


class LoggingManager:
    """ 
    The log manager class is initialized via a configuration file 
    that may have the following sections:

    ConsoleLogging      # Used for output on the screen
    FileLogging         # Used for logging into a file

    Each section in the configuration file should have the following 
    keys:
        handler     # Indicates which handler class to use 
        level       # Indicates logging level
        format      # Indicates format for log messages
        dateFormat  # Indicates date format used for log messages

    Given below is an example of a valid configuration file:

    [ConsoleLogging]
        handler=ConsoleLoggingHandler(sys.stdout,)
        level=info
        format=[%(levelname)s] %(message)s 
        dateFormat=%m/%d/%y %H:%M:%S
    [FileLogging]
        handler=TimedRotatingFileLoggingHandler('/tmp/cdb.log')
        level=debug
        format=%(asctime)s,%(msecs)d [%(levelname)s] %(module)s:%(lineno)d %(user)s@%(host)s %(name)s (%(process)d): %(message)s
        dateFormat=%m/%d/%y %H:%M:%S
    """

    # Get singleton instance.
    @classmethod
    def getInstance(cls):
        from cdb.common.utility.loggingManager import LoggingManager
        try:
            lm = LoggingManager()
        except LoggingManager, ex:
            lm = ex
        return lm

    # Singleton.
    __instance = None

    def __init__(self):
        if LoggingManager.__instance:
             raise LoggingManager.__instance
        LoggingManager.__instance = self
        self.consoleHandler = None
        self.fileHandlerList = []
        self.maxIntLevel = logging.CRITICAL
        self.minIntLevel = logging.NOTSET
        self.levelRegExList = []
        self.logger = logging.getLogger(self.__class__.__name__)
        self.initFlag = False

    def setMinLogLevel(self, minLogLevel=logging.INFO):
        self.minIntLevel = minLogLevel

    def parseLevelRegEx(self, levelRegExList):
        """ Parse expressions of the form <regex>=<log level>. """
        lines = levelRegExList.split('\n')
        for line in lines:
            try:
                 (regex, level) = line.rsplit('=', 1)
                 pattern = re.compile(regex)
                 tuple = (pattern, logging.getLevelName(level.upper()))
                 self.levelRegExList.append(tuple)
            except Exception, ex:
                 self.logger.error('Parser error in log configuration file: %s' % line)
                 self.logger.exception(ex)

    # Get Log Level based on a string representation
    def getIntLogLevel(self, levelStr):
        level = logging.getLevelName(levelStr)
        # Level should be an integer
        try:
            return int(level) 
        except ValueError, ex:
            raise ConfigurationError('"%s" is not valid log level' % levelStr)

    # Configure log handlers.
    def configureHandlers(self):
        """ Configure log handlers from the config file. """
        cm = ConfigurationManager.getInstance()
        configFile = cm.getConfigFile()
        configParser = cm.getConfigParserFromConfigFile(configFile)
        configSections = cm.getConfigSectionsFromConfigParser(configParser)

        # Console handler.
        defaults = { 
            'level' : cm.getConsoleLogLevel(),
            'format' : cm.getLogRecordFormat(),
            'dateFormat' : cm.getLogDateFormat(),
            'handler' : 'ConsoleLoggingHandler(sys.stdout,)'
        }
        consoleHandler = self.__configureHandler(configParser, 'ConsoleLogging', defaults)

        if consoleHandler != None:
            self.consoleHandler = consoleHandler

        # File logging. 
        # Do not configure if log directory does not exist.
        defaults['handler'] = None
        defaults['level'] = cm.getFileLogLevel() 
        if not os.path.exists(configFile):
            # No config file, we'll configure default.
            defaultLogFile = cm.getLogFile()
            defaultLogDir = os.path.dirname(defaultLogFile)
            if os.path.exists(defaultLogDir):
                handler = 'TimedRotatingFileLoggingHandler("%s")' % defaultLogFile 
                defaults['handler'] = handler
                fileHandler = self.__configureHandler(configParser, 'FileLogging', defaults)
                if fileHandler != None:
                    self.fileHandlerList.append(fileHandler)

        else:
            # Parse all file loggers present in the config file
            for configSection in configSections:
                if configSection.startswith('FileLogging'):
                     fileHandler = self.__configureHandler(configParser, configSection, defaults)
                     if fileHandler != None:
                         self.fileHandlerList.append(fileHandler)

        # Add handlers to the root logger.  Use logging class here
        # to make sure we can have a logger when we parse the 
        # logger expressions
        rootLogger = logging.getLogger('')
        for handler in [self.consoleHandler] + self.fileHandlerList:
            rootLogger.addHandler(handler)

        # Get a logger factory based on our current config 
        self.configureLoggers(configParser, defaultLevel='debug')

    def configureLoggers(self, configParser, defaultLevel='error'):
        rootLogLevel = 'error'
        levelRegEx = '^.*$=%s' % (defaultLevel)
        if configParser is not None and configParser.has_section('LoggerLevels'):
            rootLogLevel = ConfigurationManager.getOptionFromConfigParser(configParser, 'LoggerLevels', 'root', rootLogLevel)
            levelRegEx = ConfigurationManager.getOptionFromConfigParser(configParser, 'LoggerLevels', 'levelregex', levelRegEx)

        rootLevelInt = logging.getLevelName(rootLogLevel.upper())
        logging.getLogger('').root.setLevel(rootLevelInt)
        logging.getLogger('').debug('Set root logger to %s' % rootLevelInt)

        if not levelRegEx:
            return

        # Parse expressions of the form <regex>=<log-level>. """
        lines = levelRegEx.split('\n')
        for line in lines:
            try:
                # Use the right split so we can have '='s in the regex
                (regex, level) = line.rsplit('=', 1)
                pattern = re.compile(regex)
                tuple = (pattern, logging.getLevelName(level.upper()))
                self.levelRegExList.append(tuple)
            except Exception, ex:
                # Do not fail
                self.logger.error('Parser error in log configuration file: %s' % line)
                self.logger.exception(ex)

    # Configure particular handler with given defaults.
    def __configureHandler(self, configParser, configSection, defaults):
        """ Configure specified handler with a given defaults. """
        handlerOption = defaults['handler']
        try:
            if configParser is not None:
                handlerOption = configParser.get(configSection, 'handler')
        except Exception, ex:
            pass

        # If handlerOption is empty, handler cannot be instantiated.
        handler = None
        if handlerOption != None:
            # Handler argument format: MyHandler(arg1, arg2, ...)
            # Module will be in lowercase letters, but the class
            # should be capitalized.
            handlerName = re.sub('\(.*', '', handlerOption)
            moduleName = handlerName[0].lower() + handlerName[1:]
            try:
                exec 'from cdb.common.utility import %s' % (moduleName)
                exec 'handler = %s.%s' % (moduleName, handlerOption)
            except IOError, ex:
                errNo, errMsg = ex
                import errno

                # If the exception raised is an I/O permissions error, ignore
                # it and disable this log handler.  This allows non-root users
                # to use the (system-wide) default log configuration
                if errNo != errno.EACCES:
                    raise
                handler = None 
            except Exception, ex:
                raise ConfigurationError(exception=ex)

        # Only request setting from the config file if it was
        # not set via environment variable, or programmatically.
        cm = ConfigurationManager.getInstance()
        if handler != None:
            try:
                level = cm.getOptionFromConfigParser(configParser, configSection, 'level', defaults['level'])
                intLevel = self.getIntLogLevel(level.upper())
                handler.setLevel(intLevel)

                format = cm.getOptionFromConfigParser(configParser, configSection, 'format', defaults['format'])
                dateFormat = cm.getOptionFromConfigParser(configParser, configSection, 'dateFormat', defaults['dateFormat'])

                handler.setFormatter(logging.Formatter(format, dateFormat))
            except Exception, ex:
                raise ConfigurationError(exception=ex)

            # Apply filters to handler
            filter = None
            try:
                filter = configParser.get(configSection, 'filter')
                if filter:
                    handler.addFilter(logging.Filter(filter))
            except Exception, ex:
                pass
        return handler

    def getLogger(self, name='defaultLogger'):
        if not self.initFlag:
            self.initFlag = True
            self.configureHandlers()
        logger = logging.getLogger(name)
        logger.setLevel(self.getLevel(name))
        return logger

    def getLevel(self, name):
        # Match from the known regex list.
        level = logging.NOTSET

        # The last regex is most important.
        for e in reversed(self.levelRegExList):
            (pattern, level) = e

            # If we return not None it is a match
            if not None == pattern.match(name):
                break

        if level > self.maxIntLevel:
            level = self.maxIntLevel
        if level < self.minIntLevel:
            level = self.minIntLevel
        return level

    def setConsoleLogLevel(self, level):
        try:
            # We need to override the logger levels and the handler
            intLevel = self.getIntLogLevel(level.upper())
            self.consoleHandler.setLevel(intLevel)
            self.maxIntLevel = intLevel
            self.logger.setLevel(intLevel)
        except Exception, ex:
            raise ConfigurationError(exception=ex)

    def setFileLogLevel(self, level):
        try:
            # We need to override the logger levels and the handler
            intLevel = self.getIntLogLevel(level.upper())
            for handler in self.fileHandlerList:
                handler.setLevel(intLevel)
            self.maxIntLevel = intLevel
            self.logger.setLevel(intLevel)
        except Exception, ex:
            raise ConfigurationError(exception=ex)

#######################################################################
# Testing.

if __name__ == '__main__':
    lm = LoggingManager.getInstance()
    logger = lm.getLogger('Main')
    logger.error('Error In Main')
    logger.debug('Debug In Main')
    logger.warn('Warn In Main')
    logger.info('Info In Main')
    logger = lm.getLogger('Main')
    logger.info('Info In Main')
    logger = lm.getLogger('')
    logger.info('Info using root logger')
    logger = lm.getLogger('Main.2')
    logger.info('Info in Main.2')
    lm.setConsoleLogLevel('info')
    logger.debug('You should not see this message')
    lm.setConsoleLogLevel('debug')
    logger.debug('Debug in Main.2')
