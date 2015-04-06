#!/usr/bin/env python

#
# Base web service class.
#

####################################################################
import sys
import os
import cherrypy
from cherrypy.process import plugins
from cherrypy import server

from cdb.common.constants import cdbStatus
from cdb.common.utility.configurationManager import ConfigurationManager
from cdb.common.utility.loggingManager import LoggingManager
from cdb.common.utility.cdbModuleManager import CdbModuleManager
from cdb.common.exceptions.configurationError import ConfigurationError

####################################################################

class CdbRestWebServiceBase:

    DEFAULT_N_SERVER_REQUEST_THREADS = 10
    DEFAULT_SERVER_SOCKET_TIMEOUT = 30
    CONFIG_SECTION_NAME = 'WebService'
    CONFIG_OPTION_NAME_LIST = [ 'serviceHost', 'servicePort', 
        'sslCertFile', 'sslKeyFile', 'sslCaCertFile' ]

    class SignalHandler:
        def __init__(self, signal, oldSignalHandler):
            self.signal = signal
            self.oldSignalHandler = oldSignalHandler
            self.logger = LoggingManager.getInstance().getLogger(self.__class__.__name__)

        def signalHandler(self):
            self.logger.debug('%s signal handler called' % self.signal)
            CdbModuleManager.getInstance().stopModules()
            self.oldSignalHandler()

    def __init__(self, routeMapper):
        self.configurationManager = ConfigurationManager.getInstance()
        self.routeMapper = routeMapper
        self.options = None
        self.args = None
        self.logger = None

    def prepareOptions(self):
        from optparse import OptionParser
        p = OptionParser()
        p.add_option('-d', '--daemon', action="store_true",
            dest='daemonFlag', default=False, 
            help="Run as a daemon.")
        p.add_option('-p', '--pid-file',
            dest='pidFile', default=None,
            help="Store process id in the given file.")
        p.add_option('', '--config-file',
            dest='configFile', default=None,
            help="Service configuration file.")
        p.add_option('-P', '--port',
            dest='servicePort', default=None,
            help="Service port.")
        p.add_option('-H', '--host',
            dest='serviceHost', default=None,
            help="Service host.")
        p.add_option('-C', '--ssl-ca-cert', 
            dest='sslCaCertFile', default=None,
            help='SSL CA certificate path (used for client SSL certificate verification). Requires --ssl-key and --ssl-cert.')
        p.add_option('-c', '--ssl-cert', 
            dest='sslCertFile', default=None,
            help='SSL certificate path. SSL operation requires both --ssl-key and --ssl-cert. Client SSL certificate verification also requires --ssl-ca-cert.')
        p.add_option('-k', '--ssl-key', 
            dest='sslKeyFile', default=None, 
            help='SSL key path. SSL operation requires both --ssl-key and --ssl-cert. Client SSL certificate verification also requires --ssl-ca-cert.')
        p.add_option('', '--n-server-threads', 
            dest='nServerThreads', default=CdbRestWebServiceBase.DEFAULT_N_SERVER_REQUEST_THREADS,
            help='Number of service request handler threads (defaut: %s).' % CdbRestWebServiceBase.DEFAULT_N_SERVER_REQUEST_THREADS)
        return p

    def initCdbModules(self):
        return None

    def getDefaultServerHost(self):
        return None

    def getDefaultServerPort(self):
        return None

    # Instantiate modified signal handler that stops cdb modules first, 
    # and then does the default action.
    def modifySignalHandlers(self, engine):
        pluginsSignalHandler = plugins.SignalHandler(engine)
        handlers = pluginsSignalHandler.handlers

        # Add handler for interrupt
        handlers['SIGINT'] = engine.exit

        # Modify all signal handlers
        for signal in handlers.keys():
            self.logger.debug('Modifying signal: %s' % signal)
            oldSignalHandler = handlers[signal]
            self.logger.debug('Old signal handler: %s' % oldSignalHandler)
            signalHandler = CdbRestWebServiceBase.SignalHandler(signal, oldSignalHandler)
            self.logger.debug('Setting signal handler to: %s' % signalHandler.signalHandler)
            handlers[signal] = signalHandler.signalHandler
            pluginsSignalHandler.subscribe()

    def initServerLog(self):
        cherrypyLogLevel = self.configurationManager.getCherrypyLogLevel()
        cherrypy.log.error_log.setLevel(cherrypyLogLevel)
        cherrypy.log.error_file = self.configurationManager.getCherrypyLogFile()
        cherrypy.log.error_log.propagate = False
        cherrypy.log.access_log.setLevel(cherrypyLogLevel)
        cherrypy.log.access_file = self.configurationManager.getCherrypyAccessFile()
        cherrypy.log.access_log.propagate = False

    def updateServerConfig(self):
        serviceHost = self.configurationManager.getServiceHost()
        servicePort = int(self.configurationManager.getServicePort())
        nServerThreads = int(self.options.nServerThreads)
        configDict = {
            'server.socket_host' : serviceHost,
            'server.socket_port' : servicePort,
            'server.thread_pool' : nServerThreads,
            'log.screen' : (self.options.daemonFlag != True),
        }
        cherrypy.config.update(configDict)

    def readConfigFile(self, configFile):
        configFile = self.options.configFile
        if not configFile:
            configFile = self.configurationManager.getConfigFile()
        else:
            self.configurationManager.setConfigFile(configFile)

        if not os.path.exists(configFile):
            raise ConfigurationError('Configuration file %s does not exist.' % configFile)
        # Read file and set config options
        self.configurationManager.setOptionsFromConfigFile(CdbRestWebServiceBase.CONFIG_SECTION_NAME, CdbRestWebServiceBase.CONFIG_OPTION_NAME_LIST, configFile)

    def readCommandLineOptions(self):
        # This method should be called after reading config file
        # in case some options are overridden
        if self.options.sslCaCertFile != None:
            self.configurationManager.setSslCaCertFile(self.options.sslCaCertFile)
        if self.options.sslCertFile != None:
            self.configurationManager.setSslCertFile(self.options.sslCertFile)

        if self.options.sslKeyFile != None:
            self.configurationManager.setSslKeyFile(self.options.sslKeyFile)

        if self.options.serviceHost != None:
            self.configurationManager.setServiceHost(self.options.serviceHost)

        if self.options.servicePort != None:
            self.configurationManager.setServicePort(self.options.servicePort)

    def prepareServer(self):
        try:
            optionParser = self.prepareOptions()
            (self.options, self.args) = optionParser.parse_args()
      
            # Read config file and override with command line options
            self.readConfigFile(self.options.configFile)
            self.readCommandLineOptions()
             
            # Turn off console log for daemon mode.
            self.logger = LoggingManager.getInstance().getLogger(self.__class__.__name__)
            if self.options.daemonFlag:
                LoggingManager.getInstance().setConsoleLogLevel('CRITICAL')

            dispatch = self.routeMapper.setupRoutes()
            self.logger.debug('Using route dispatch: %s' % dispatch)

            config = {
                '/' : {
                    'request.dispatch' : dispatch,
                },
            }
                   
            # No root controller as we provided our own.
            cherrypy.tree.mount(root=None, config=config)
            self.initServerLog()
            self.updateServerConfig()

            self.logger.info('Using host %s' % self.configurationManager.getServiceHost())
            self.logger.info('Using port %s' % self.configurationManager.getServicePort())
            self.logger.debug('Using %s request handler threads' % self.options.nServerThreads)
        except Exception, ex:
            if self.logger is not None:
                self.logger.exception(ex)
            else:
                import traceback
                print '\n%s' % sys.exc_info()[1]
                traceback.print_exc(file=sys.stderr)
            sys.exit(cdbStatus.CDB_ERROR)

    # Run server.
    def __runServer(self):
        self.logger.info('Starting service')
        engine = cherrypy.engine

        # Set up Deamonization
        if self.options.daemonFlag:
            plugins.Daemonizer(engine).subscribe()
        self.logger.debug('Daemon mode: %s' % self.options.daemonFlag)

        if self.options.pidFile != None:
            plugins.PIDFile(engine, self.options.pidFile).subscribe()
        self.logger.debug('Using PID file: %s' % self.options.pidFile)

        sslCertFile = self.configurationManager.getSslCertFile()
        sslKeyFile = self.configurationManager.getSslKeyFile()
        sslCaCertFile = self.configurationManager.getSslCaCertFile()
        if sslCertFile != None and sslKeyFile != None:
            server.ssl_ca_certificate = None
            if sslCaCertFile != None:
                server.ssl_ca_certificate = self.options.sslCaCertFile
                self.logger.info('Using SSL CA cert file: %s' % sslCaCertFile)
            server.ssl_certificate = sslCertFile
            self.logger.info('Using SSL cert file: %s' % sslCertFile)

            server.ssl_private_key = sslKeyFile
            self.logger.info('Using SSL key file: %s' % sslKeyFile)

        server.ssl_module = 'builtin'
        #server.ssl_module = 'pyopenssl'

        # Increase timeout to prevent early SSL connection terminations
        server.socket_timeout = CdbRestWebServiceBase.DEFAULT_SERVER_SOCKET_TIMEOUT
        
        # Setup the signal handler to stop the application while running.
        if hasattr(engine, 'signal_handler'):
            engine.signal_handler.subscribe()
        if hasattr(engine, 'console_control_handler'):
            engine.console_control_handler.subscribe()
            self.modifySignalHandlers(engine)

        # Turn off autoreloader.
        self.logger.debug('Turning off autoreloader')
        engine.autoreload.unsubscribe()

        # Start the engine.
        try:
            self.logger.debug('Starting engine')
            engine.start()

            # Prepare cdb services.
            # Doing this before engine starts may cause issues with existing timers.
            self.logger.debug('Starting modules')
            self.initCdbModules()
            CdbModuleManager.getInstance().startModules()
        except Exception, ex:
            self.logger.exception('Service exiting: %s' % ex)
            CdbModuleManager.getInstance().stopModules()
            return cdbStatus.CDB_ERROR
        self.logger.info('Service ready')
        engine.block()
        CdbModuleManager.getInstance().stopModules()
        self.logger.info('Service done')
        return cdbStatus.CDB_OK

    # Run server instance.
    def run(self):
        self.prepareServer()
        sys.exit(self.__runServer())

####################################################################
# Testing

if __name__ == '__main__':
    pass
