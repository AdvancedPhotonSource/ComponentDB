#!/usr/bin/env python

import os
from configparser import ConfigParser


class ConfigurationManager:
    CDB_INSTALL_DIR_ENV_NAME = 'CDB_INSTALL_DIR'
    CDB_PROD_CONFIG_SUBDIR = '%s/etc/cdb.conf'
    CONFIG_WEB_PORTAL_SECTION_NAME = 'WebPortal'
    CONFIG_PORTAL_VALUE_NAME = 'portalWebAddress'

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

        install_path = os.environ[self.CDB_INSTALL_DIR_ENV_NAME]
        configuration_file_path = self.CDB_PROD_CONFIG_SUBDIR % install_path

        self.configuration = ConfigParser()
        self.configuration.read(configuration_file_path)

    def get_portal_address(self):
        web_portal_section = self.configuration[self.CONFIG_WEB_PORTAL_SECTION_NAME]
        return web_portal_section[self.CONFIG_PORTAL_VALUE_NAME]

    def get_session_file_path(self):
        return os.path.expanduser(self.SESSION_COOKIE_FILE_PATH)
