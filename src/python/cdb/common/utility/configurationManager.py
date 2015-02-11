#!/usr/bin/env python

# 
# Configuration manager singleton.
#

#######################################################################

import os
import socket
import UserDict
import pwd

from cdb.common.constants import cdbServiceConstants
from cdb.common.exceptions.configurationError import ConfigurationError

#######################################################################

# Defaults.

DEFAULT_CDB_ROOT_DIR = '/opt/cdb'
DEFAULT_CDB_RUN_DIR = '%s'                      # requires run dir
DEFAULT_CDB_CONFIG_FILE = '%s/etc/cdb.conf'     # requires run dir

DEFAULT_CDB_LOG_FILE = '%s/var/log/cdb.log'    # requires run dir
DEFAULT_CDB_LOG_CONFIG_FILE = '%s/etc/cdb.log.conf'     # requires run dir
DEFAULT_CDB_CONSOLE_LOG_LEVEL = 'critical'
DEFAULT_CDB_FILE_LOG_LEVEL = 'info'
#DEFAULT_CDB_LOG_RECORD_FORMAT = '%(asctime)s,%(msecs)03d [%(levelname)s] %(module)s:%(lineno)d %(user)s@%(host)s %(name)s (%(process)d): %(message)s'
#DEFAULT_CDB_LOG_RECORD_FORMAT = '%(asctime)s,%(msecs)03d %(levelname)s %(module)s:%(lineno)d %(process)d:  %(message)s'
DEFAULT_CDB_LOG_RECORD_FORMAT = '%(asctime)s,%(msecs)03d %(levelname)s %(process)d:  %(message)s'
DEFAULT_CDB_LOG_DATE_FORMAT = '%Y-%m-%d %H:%M:%S'

DEFAULT_CDB_CHERRYPY_LOG_LEVEL = 'ERROR'
DEFAULT_CDB_CHERRYPY_LOG_FILE = '%s/var/log/cherrypy.error'     # requires run dir
DEFAULT_CDB_CHERRYPY_ACCESS_FILE = '%s/var/log/cherrypy.access' # requires run dir

DEFAULT_CDB_SERVICE_PORT = 10232           # 10CDB
DEFAULT_CDB_SERVICE_HOST = '0.0.0.0'
DEFAULT_CDB_SERVICE_PROTOCOL = cdbServiceConstants.CDB_SERVICE_PROTOCOL_HTTP
DEFAULT_CDB_SERVICE_USERNAME = ''
DEFAULT_CDB_SERVICE_PASSWORD = ''

DEFAULT_CDB_DB = 'mysql'
DEFAULT_CDB_DB_HOST = 'localhost'
DEFAULT_CDB_DB_PORT = 20232                # 20CDB
DEFAULT_CDB_DB_PASSWORD = ''
DEFAULT_CDB_DB_USER = ''
DEFAULT_CDB_DB_SCHEMA = 'cdb'
DEFAULT_CDB_DB_PASSWORD_FILE = '%s/lib/mysql/etc/db.passwd' # requires root dir

DEFAULT_CDB_CONTEXT_ROOT = '/cdb'

# Session cache file
DEFAULT_CDB_SESSION_CACHE_FILE = None

# Enforce session credentials.
DEFAULT_CDB_REQUIRE_SESSION_CREDENTIALS = False

# SSL variables
DEFAULT_CDB_SSL_CA_CERT_FILE = None
DEFAULT_CDB_SSL_CERT_FILE = None
DEFAULT_CDB_SSL_KEY_FILE = None

class ConfigurationManager(UserDict.UserDict):
    """ 
    Singleton class used for keeping system configuration data. The class
    initializes its data using predefined defaults, or from certain 
    environment variables.

    Usage:
        from cdb.common.utility import configurationManager
        cm = configurationManager.getInstance()
        cm.setConsoleLogLevel('info')
        level = cm.getConsoleLogLevel()
        cm['myKey'] = 'myValue'
        value = cm.get('myKey')
    """

    # Get singleton instance.
    @classmethod
    def getInstance(cls):
        """ Get configuration manager singleton instance. """
        from cdb.common.utility.configurationManager import ConfigurationManager
        try:
            cm = ConfigurationManager()
        except ConfigurationManager, ex:
            cm = ex
        return cm

    # Singleton.
    __instance = None

    def __init__(self):
        if ConfigurationManager.__instance:
            raise ConfigurationManager.__instance
        ConfigurationManager.__instance = self
        UserDict.UserDict.__init__(self)
        self['user'] = pwd.getpwuid(os.getuid())[0]
        self['host'] = socket.gethostname()

        self['defaultRootDir'] = DEFAULT_CDB_ROOT_DIR
        self.__setFromEnvVar('rootDir', 'CDB_ROOT_DIR')
        self['defaultRunDir'] = DEFAULT_CDB_RUN_DIR % self.getRootDir()
        self.__setFromEnvVar('runDir', 'CDB_RUN_DIR')

        self['defaultConfigFile'] = DEFAULT_CDB_CONFIG_FILE % self.getRunDir()
        self['defaultLogFile'] = DEFAULT_CDB_LOG_FILE % self.getRunDir()
        self['defaultLogConfigFile'] = DEFAULT_CDB_LOG_CONFIG_FILE % self.getRunDir()

        self['defaultConsoleLogLevel'] = DEFAULT_CDB_CONSOLE_LOG_LEVEL
        self['defaultFileLogLevel'] = DEFAULT_CDB_FILE_LOG_LEVEL
        self['defaultLogRecordFormat'] = DEFAULT_CDB_LOG_RECORD_FORMAT
        self['defaultLogDateFormat'] = DEFAULT_CDB_LOG_DATE_FORMAT

        self['defaultCherrypyLogLevel'] = DEFAULT_CDB_CHERRYPY_LOG_LEVEL
        self['defaultCherrypyLogFile'] = DEFAULT_CDB_CHERRYPY_LOG_FILE % self.getRunDir()
        self['defaultCherrypyAccessFile'] = DEFAULT_CDB_CHERRYPY_ACCESS_FILE % self.getRunDir()

        self['defaultServicePort'] = DEFAULT_CDB_SERVICE_PORT
        self['defaultServiceHost'] = DEFAULT_CDB_SERVICE_HOST
        self['defaultServiceProtocol'] = DEFAULT_CDB_SERVICE_PROTOCOL
        self['defaultServiceUsername'] = DEFAULT_CDB_SERVICE_USERNAME  
        self['defaultServicePassword'] = DEFAULT_CDB_SERVICE_PASSWORD
        self['defaultDb'] = DEFAULT_CDB_DB
        self['defaultDbHost'] = DEFAULT_CDB_DB_HOST
        self['defaultDbPort'] = DEFAULT_CDB_DB_PORT
        self['defaultDbPassword'] = DEFAULT_CDB_DB_PASSWORD
        self['defaultDbPasswordFile'] = DEFAULT_CDB_DB_PASSWORD_FILE % self.getRunDir()
        self['defaultDbUser'] = DEFAULT_CDB_DB_USER
        self['defaultDbSchema'] = DEFAULT_CDB_DB_SCHEMA

        self['defaultContextRoot'] = DEFAULT_CDB_CONTEXT_ROOT

        self['defaultSessionCacheFile'] = DEFAULT_CDB_SESSION_CACHE_FILE
        self['defaultRequireSessionCredentials'] = DEFAULT_CDB_REQUIRE_SESSION_CREDENTIALS

        self['defaultSslCaCertFile'] = DEFAULT_CDB_SSL_CA_CERT_FILE
        self['defaultSslCertFile'] = DEFAULT_CDB_SSL_CERT_FILE
        self['defaultSslKeyFile'] = DEFAULT_CDB_SSL_KEY_FILE
                                                                                        # Settings that might come from environment variables.
        self.__setFromEnvVar('logFile', 'CDB_LOG_FILE')
        self.__setFromEnvVar('logConfigFile', 'CDB_LOG_CONFIG_FILE')
        self.__setFromEnvVar('consoleLogLevel', 'CDB_CONSOLE_LOG_LEVEL')
        self.__setFromEnvVar('fileLogLevel', 'CDB_FILE_LOG_LEVEL')
        self.__setFromEnvVar('logRecordFormat', 'CDB_LOG_RECORD_FORMAT')
        self.__setFromEnvVar('logDateFormat', 'CDB_LOG_DATE_FORMAT')

        self.__setFromEnvVar('cherrypyLogLevel', 'CDB_CHERRYPY_LOG_LEVEL')
        self.__setFromEnvVar('cherrypyLogFile', 'CDB_CHERRYPY_LOG_FILE')
        self.__setFromEnvVar('cherrypyAccessFile', 'CDB_CHERRYPY_ACCESS_FILE')

        self.__setFromEnvVar('serviceProtocol', 'CDB_SERVICE_PROTOCOL')
        self.__setFromEnvVar('serviceHost', 'CDB_SERVICE_HOST')
        self.__setFromEnvVar('servicePort', 'CDB_SERVICE_PORT')
        self.__setFromEnvVar('serviceUsername', 'CDB_SERVICE_USERNAME')
        self.__setFromEnvVar('servicePassword', 'CDB_SERVICE_PASSWORD')

        self.__setFromEnvVar('contextRoot', 'CDB_CONTEXT_ROOT')

        self.__setFromEnvVar('sessionCacheFile', 'CDB_SESSION_CACHE_FILE')

        self.__setFromEnvVar('sslCaCertFile', 'CDB_SSL_CA_CERT_FILE')
        self.__setFromEnvVar('sslCertFile', 'CDB_SSL_CERT_FILE')
        self.__setFromEnvVar('sslKeyFile', 'CDB_SSL_KEY_FILE')

        self.__setFromEnvVar('configFile', 'CDB_CONFIG_FILE')
        self.__setFromEnvVar('dbPasswordFile', 'CDB_DB_PASSWORD_FILE')

        # Settings that might come from file.
        self.__setFromVarFile('dbPassword', self.getDbPasswordFile())

    # This function will ignore errors if environment variable is not set.
    def __setFromEnvVar(self, key, envVar):
        """ 
        Set value for the specified key from a given environment variable.
        This function ignores errors for env. variables that are not set.
        """
        try:
            self[key] = os.environ[envVar]
        except:
            pass

    # This function will ignore errors if variable file is not present.
    def __setFromVarFile(self, key, varFile):
        """ 
        Set value for the specified key from a given file. The first line  
        in the file is variable value.
        This function ignores errors.
        """
        try:
            v = open(varFile, 'r').readline()
            self[key] = v.lstrip().rstrip()
        except Exception, ex:
            pass

    def __getKeyValue(self, key, default='__internal__'):
        """
        Get value for a given key.
        Keys will be of the form 'logFile', and the default keys have
        the form 'defaultLogFile'.
        """
        defaultKey = "default" + key[0].upper() + key[1:]
        defaultValue = self.get(defaultKey, None)
        if default != '__internal__':
            defaultValue = default
        return self.get(key, defaultValue)

    def getHost(self):
        return self['host']
    
    def getUser(self):
        return self['user']

    def getDefaultRootDir(self):
        return self['defaultRootDir']

    def setRootDir(self, rootDir):
        self['rootDir'] = rootDir

    def getRootDir(self, default='__internal__'):
        return self.__getKeyValue('rootDir', default)

    def getDefaultRunDir(self):
        return self['defaultRunDir']

    def setRunDir(self, runDir):
        self['runDir'] = runDir

    def getRunDir(self, default='__internal__'):
        return self.__getKeyValue('runDir', default)

    def getDefaultLogFile(self):
        return self['defaultLogFile']

    def setLogFile(self, logFile):
        self['logFile'] = logFile

    def getLogFile(self, default='__internal__'):
        return self.__getKeyValue('logFile', default)

    def hasLogFile(self):
        return self.has_key('logFile')

    def getDefaultLogConfigFile(self):
        return self['defaultLogConfigFile']

    def setLogConfigFile(self, logConfigFile):
        self['logConfigFile'] = logConfigFile

    def getLogConfigFile(self, default='__internal__'):
        return self.__getKeyValue('logConfigFile', default)

    def hasLogConfigFile(self):
        return self.has_key('logConfigFile')

    def getDefaultConsoleLogLevel(self):
        return self['defaultConsoleLogLevel']

    def setConsoleLogLevel(self, level):
        self['consoleLogLevel'] = level 

    def getConsoleLogLevel(self, default='__internal__'):
        return self.__getKeyValue('consoleLogLevel', default)

    def hasConsoleLogLevel(self):
        return self.has_key('consoleLogLevel')

    def getDefaultFileLogLevel(self):
        return self['defaultFileLogLevel']

    def setFileLogLevel(self, level):
        self['fileLogLevel'] = level 

    def getFileLogLevel(self, default='__internal__'):
        return self.__getKeyValue('fileLogLevel', default)

    def hasFileLogLevel(self):
        return self.has_key('fileLogLevel')

    def getDefaultLogRecordFormat(self):
        return self['defaultLogRecordFormat']

    def setLogRecordFormat(self, format):
        self['logRecordFormat'] = format

    def getLogRecordFormat(self, default='__internal__'):
        return self.__getKeyValue('logRecordFormat', default)

    def hasLogRecordFormat(self):
        return self.has_key('logRecordFormat')

    def getDefaultLogDateFormat(self):
        return self['defaultLogDateFormat']

    def setLogDateFormat(self, format):
        self['logDateFormat'] = format

    def getLogDateFormat(self, default='__internal__'):
        return self.__getKeyValue('logDateFormat', default)

    def hasLogDateFormat(self):
        return self.has_key('logDateFormat')

    def getDefaultCherrypyLogLevel(self):
        return self['defaultCherrypyLogLevel']

    def setCherrypyLogLevel(self, level):
        self['cherrypyLogLevel'] = level 

    def getCherrypyLogLevel(self, default='__internal__'):
        return self.__getKeyValue('cherrypyLogLevel', default)

    def hasCherrypyLogLevel(self):
        return self.has_key('cherrypyLogLevel')

    def getDefaultCherrypyLogCherrypy(self):
        return self['defaultCherrypyLogFile']

    def setCherrypyLogFile(self, cherrypyLogFile):
        self['cherrypyLogFile'] = cherrypyLogFile

    def getCherrypyLogFile(self, default='__internal__'):
        return self.__getKeyValue('cherrypyLogFile', default)

    def hasCherrypyLogFile(self):
        return self.has_key('cherrypyLogFile')

    def getDefaultCherrypyAccessFile(self):
        return self['defaultCherrypyAccessFile']

    def setCherrypyAccessFile(self, cherrypyAccessFile):
        self['cherrypyAccessFile'] = cherrypyAccessFile

    def getCherrypyAccessFile(self, default='__internal__'):
        return self.__getKeyValue('cherrypyAccessFile', default)

    def hasCherrypyAccessFile(self):
        return self.has_key('cherrypyAccessFile')

    def isDbAvailable(self):
        if os.access(self.getDbPasswordFile(), os.R_OK):
            return True
        return False

    def getDefaultServiceProtocol(self):
        return self['defaultServiceProtocol']

    def setServiceProtocol(self, serviceProtocol):
        self['serviceProtocol'] = serviceProtocol

    def getServiceProtocol(self, default='__internal__'):
        return self.__getKeyValue('serviceProtocol', default)

    def hasServiceProtocol(self):
        return self.has_key('serviceProtocol')

    def getDefaultServicePort(self):
        return self['defaultServicePort']

    def setServicePort(self, servicePort):
        self['servicePort'] = servicePort 

    def getServicePort(self, default='__internal__'):
        return int(self.__getKeyValue('servicePort', default))

    def hasServicePort(self):
        return self.has_key('servicePort')

    def getDefaultServiceHost(self):
        return self['defaultServiceHost']

    def setServiceHost(self, serviceHost):
        self['serviceHost'] = serviceHost

    def getServiceHost(self, default='__internal__'):
        return self.__getKeyValue('serviceHost', default)

    def hasServiceHost(self):
        return self.has_key('serviceHost')

    def getDefaultServiceUsername(self):
        return self['defaultServiceUsername']

    def setServiceUsername(self, serviceUsername):
        self['serviceUsername'] = serviceUsername 

    def getServiceUsername(self, default='__internal__'):
        return self.__getKeyValue('serviceUsername', default)

    def hasServiceUsername(self):
        return self.has_key('serviceUsername')

    def getDefaultServicePassword(self):
        return self['defaultServicePassword']

    def setServicePassword(self, servicePassword):
        self['servicePassword'] = servicePassword 

    def getServicePassword(self, default='__internal__'):
        return self.__getKeyValue('servicePassword', default)

    def hasServicePassword(self):
        return self.has_key('servicePassword')

    def getDefaultDb(self):
        return self['defaultDb']

    def setDb(self, db):
        self['db'] = db

    def getDb(self, default='__internal__'):
        return self.__getKeyValue('db', default) 

    def hasDb(self):
        return self.has_key('db')

    def getDefaultDbHost(self):
        return self['defaultDbHost']

    def setDbHost(self, dbHost):
        self['dbHost'] = dbHost

    def getDbHost(self, default='__internal__'):
        return self.__getKeyValue('dbHost', default) 

    def hasDbHost(self):
        return self.has_key('dbHost')

    def getDefaultDbPort(self):
        return self['defaultDbPort']

    def setDbPort(self, dbPort):
        self['dbPort'] = dbPort

    def getDbPort(self, default='__internal__'):
        return self.__getKeyValue('dbPort', default) 

    def hasDbPort(self):
        return self.has_key('dbPort')

    def getDefaultDbPassword(self):
        return self['defaultDbPassword']

    def setDbPassword(self, dbPassword):
        self['dbPassword'] = dbPassword

    def getDbPassword(self, default='__internal__'):
        return self.__getKeyValue('dbPassword', default) 

    def hasDbPassword(self):
        return self.has_key('dbPassword')

    def getDefaultDbPasswordFile(self):
        return self['defaultDbPasswordFile']

    def getDbPasswordFile(self, default='__internal__'):
        return self.__getKeyValue('dbPasswordFile', default) 

    def setDbPasswordFile(self, f):
        self['dbPasswordFile'] = f

    def hasDbPasswordFile(self):
        return self.has_key('dbPasswordFile')

    def getDefaultDbUser(self):
        return self['defaultDbUser']

    def getDbUser(self, default='__internal__'):
        return self.__getKeyValue('dbUser', default) 

    def setDbUser(self, u):
        self['dbUser'] = u

    def hasDbUser(self):
        return self.has_key('dbUser')

    def getDbSchema(self):
        return self['dbSchema']

    def getDefaultConfigFile(self):
        return self['defaultConfigFile']

    def setConfigFile(self, configFile):
        self['configFile'] = configFile

    def getConfigFile(self, default='__internal__'):
        return self.__getKeyValue('configFile', default)

    def hasConfigFile(self):
        return self.has_key('configFile')

    def getDefaultContextRoot(self):
        return self['defaultContextRoot']

    def setContextRoot(self, contextRoot):
        self['contextRoot'] = contextRoot

    def getContextRoot(self, default='__internal__'):
        return self.__getKeyValue('contextRoot', default)

    def hasContextRoot(self):
        return self.has_key('contextRoot')

    def getDefaultSessionCacheFile(self):
        return self['defaultSessionCacheFile']

    def setSessionCacheFile(self, sessionCacheFile):
        self['sessionCacheFile'] = sessionCacheFile

    def getSessionCacheFile(self, default='__internal__'):
        return self.__getKeyValue('sessionCacheFile', default)

    def hasSessionCacheFile(self):
        return self.has_key('sessionCacheFile')

    def getDefaultRequireSessionCredentials(self):
        return self['defaultRequireSessionCredentials']

    def setRequireSessionCredentials(self, requireSessionCredentials):
        self['requireSessionCredentials'] = requireSessionCredentials

    def getRequireSessionCredentials(self, default='__internal__'):
        return self.__getKeyValue('requireSessionCredentials', default)

    def hasRequireSessionCredentials(self):
        return self.has_key('requireSessionCredentials')

    def getDefaultSslCaCertFile(self):
        return self['defaultSslCaCertFile']

    def setSslCaCertFile(self, sslCaCertFile):
        self['sslCaCertFile'] = sslCaCertFile

    def getSslCaCertFile(self, default='__internal__'):
        return self.__getKeyValue('sslCaCertFile', default)

    def hasSslCaCertFile(self):
        return self.has_key('sslCaCertFile')

    def getDefaultSslCertFile(self):
        return self['defaultSslCertFile']

    def setSslCertFile(self, sslCertFile):
        self['sslCertFile'] = sslCertFile

    def getSslCertFile(self, default='__internal__'):
        return self.__getKeyValue('sslCertFile', default)

    def hasSslCertFile(self):
        return self.has_key('sslCertFile')

    def getDefaultSslKeyFile(self):
        return self['defaultSslKeyFile']

    def setSslKeyFile(self, sslKeyFile):
        self['sslKeyFile'] = sslKeyFile

    def getSslKeyFile(self, default='__internal__'):
        return self.__getKeyValue('sslKeyFile', default)

    def hasSslKeyFile(self):
        return self.has_key('sslKeyFile')

    def getDefaultUsername(self):
        return self['defaultUsername']

    def setUsername(self, username):
        self['username'] = username

    def getUsername(self, default='__internal__'):
        return self.__getKeyValue('username', default)

    def hasUsername(self):
        return self.has_key('username')

    def getDefaultPassword(self):
        return self['defaultPassword']

    def setPassword(self, password):
        self['password'] = password

    def getPassword(self, default='__internal__'):
        return self.__getKeyValue('password', default)

    def hasPassword(self):
        return self.has_key('password')

#######################################################################
# Testing.

if __name__ == '__main__':
    cm = ConfigurationManager.getInstance()
    print cm
