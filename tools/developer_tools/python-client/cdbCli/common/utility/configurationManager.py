#!/usr/bin/env python

import os
from configparser import ConfigParser


class ConfigurationManager:
    CDB_INSTALL_DIR_ENV_NAME = 'CDB_INSTALL_DIR'
    CDB_PROD_CONFIG_SUBDIR = '%s/etc/cdb.conf'
    CONFIG_WEB_PORTAL_SECTION_NAME = 'WebPortal'
    CONFIG_PORTAL_VALUE_NAME = 'portalWebAddress'

    SESSION_COOKIE_SECTION_NAME = 'SessionInfo'
    SESSION_COOKIE_VALUE_NAME = 'sessionCookieFilePath'
    SESSION_COOKIE_FILE_PATH = '~/.cdb/api_session'

    # Singleton.
    __instance = None

    @classmethod
    def get_instance(cls):
        """ Get configuration manager singleton instance. """
        if ConfigurationManager.__instance is not None:
            return ConfigurationManager.__instance
        else:
            return ConfigurationManager()

    def __init__(self):
        if ConfigurationManager.__instance:
            raise ConfigurationManager.__instance
        ConfigurationManager.__instance = self

        try:
            install_path = os.environ[self.CDB_INSTALL_DIR_ENV_NAME]
        except:
            install_path = "/"
        configuration_file_path = self.CDB_PROD_CONFIG_SUBDIR % install_path

        home_dot_dir_config = os.path.expanduser("~/.cdb/cdb.conf")
        current_dir_config = os.path.expanduser("./cdb.conf")
        
        allowed_files = [configuration_file_path,
                         home_dot_dir_config,
                         current_dir_config]
        
        self.configuration = ConfigParser()
        self.configuration.read(allowed_files)

    def get_portal_address(self):
        web_portal_section = self.configuration[self.CONFIG_WEB_PORTAL_SECTION_NAME]
        return web_portal_section[self.CONFIG_PORTAL_VALUE_NAME]

    def get_session_file_path(self):
        session_cookie_section = self.configuration[self.SESSION_COOKIE_SECTION_NAME]
        session_cookie_file = os.path.expanduser(session_cookie_section[self.SESSION_COOKIE_VALUE_NAME])
        return(session_cookie_file)
