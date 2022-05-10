#!/usr/bin/env python

from multiprocessing.spawn import prepare
import os
from configparser import ConfigParser


class ConfigurationManager:
    CDB_INSTALL_DIR_ENV_NAME = 'CDB_INSTALL_DIR'
    CDB_CLI_CONFIG_FILE_ENV_NAME = 'CDB_CLI_CONFIG_FILE'
    CDB_PROD_CONFIG_SUBDIR = '%s/etc/cdb.conf'
    CONFIG_WEB_PORTAL_SECTION_NAME = 'WebPortal'
    CONFIG_PORTAL_VALUE_NAME = 'portalWebAddress'

    SESSION_COOKIE_SECTION_NAME = 'SessionInfo'
    SESSION_COOKIE_VALUE_NAME = 'sessionCookieFilePath'
    SESSION_USERNAME = 'sessionUsername'
    SESSION_PASSWORD = 'sessionPassword'

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

        if self.CDB_CLI_CONFIG_FILE_ENV_NAME in os.environ:
            config_path = os.environ[self.CDB_CLI_CONFIG_FILE_ENV_NAME]
            if os.path.exists(config_path):
                self.configuration = ConfigParser()
                self.configuration.read(config_path)
            else:
                raise Exception("Path (%s) was not found using the enviornment variable '%s'. Please clear (`unset %s`) variable or specify an appropriate path." 
                    % (config_path, self.CDB_CLI_CONFIG_FILE_ENV_NAME, self.CDB_CLI_CONFIG_FILE_ENV_NAME))
        else:
            home_dot_dir_config = os.path.expanduser("~/.cdb/cdb.conf")
            current_dir_config = os.path.expanduser("./cdb.conf")

            config_paths = [home_dot_dir_config,
                            current_dir_config]

            try:
                install_path = os.environ[self.CDB_INSTALL_DIR_ENV_NAME]
                configuration_file_path = self.CDB_PROD_CONFIG_SUBDIR % install_path
                config_paths.append(configuration_file_path)
            except:
                pass

            self.prepare_configuration(config_paths)

    def prepare_configuration(self, config_paths):
        '''
        Read or generate configuration
        '''

        config_file_exists = [os.path.exists(config_file) for config_file in config_paths]        

        if any(config_file_exists):
            self.configuration = ConfigParser()
            self.configuration.read(config_paths)
        else:
            print("Greetings!!!,  you need a minimal cdb.conf file to run these scripts.")
            print("The file can be in one of the following locations and it helps specify ")
            print("CDB Server locations and the location of the session key file that caches")
            print("access credentials for CDB")
            print("")

            for i, path in enumerate(config_paths):
                print("%i: %s" % (i, os.path.dirname(path)))

            config_file_dir_selection = input("Enter the directory to store cdb.conf & other required files [0]: ") or 0
            config_filename = config_paths[int(config_file_dir_selection)]

            print("To get you started, you will need to enter the address of the default ")
            print("CDB Server.  You can add more server definitions by editing the cdb.conf file ")
            print("and following the <name> = <url> syntax ")
            print("Hit [Return] to accept defaults ")
            prompt_string = "Enter the default CDB Server Address [https://cdb.aps.anl.gov/cdb]: "
            cdb_server = input(prompt_string) or "https://cdb.aps.anl.gov/cdb"
            session_file = os.path.dirname(config_filename) + "/cdb_api_session"

            self.configuration = ConfigParser()
            self.configuration.optionxform = str
            self.configuration[self.CONFIG_WEB_PORTAL_SECTION_NAME] = {self.CONFIG_PORTAL_VALUE_NAME: cdb_server}
            self.configuration[self.SESSION_COOKIE_SECTION_NAME] = {self.SESSION_COOKIE_VALUE_NAME: session_file}
            try:
                config_directory = os.path.dirname(config_filename)
                if not os.path.exists(config_directory):
                    os.makedirs(config_directory)
                with open(config_filename, "w") as configfile:
                    self.configuration.write(configfile)
            except Exception as e:
                print(" Sorry, can't save your cdb.conf file, try again")
                print(str(e))        

    def get_portal_address(self, portal_key=CONFIG_PORTAL_VALUE_NAME):
        web_portal_section = self.configuration[self.CONFIG_WEB_PORTAL_SECTION_NAME]
        try:
            portal_value = web_portal_section[portal_key]
        except:
            print("Sorry ", portal_key, " wasn't provided in your cdb.conf ")
            return (None)
        return (portal_value)

    def get_session_file_path(self):
        session_cookie_section = self.configuration[self.SESSION_COOKIE_SECTION_NAME]
        session_cookie_file = os.path.expanduser(session_cookie_section[self.SESSION_COOKIE_VALUE_NAME])
        return session_cookie_file

    def get_session_credentials(self):
        try:
            session_cookie_section = self.configuration[self.SESSION_COOKIE_SECTION_NAME]
            session_username = session_cookie_section[self.SESSION_USERNAME]
            session_password = session_cookie_section[self.SESSION_PASSWORD]
            return (session_username, session_password)
        except Exception as e:
            return (None, None)

if __name__ == "__main__":
    configMan = ConfigurationManager()
    print("Default Portal Address Is:", configMan.get_portal_address())
    print("Session File Path Is:", configMan.get_session_file_path())
    print("Default Portal Address Is:", configMan.get_portal_address("NotListed"))
    print("Session Password is ", configMan.get_session_credentials())
    print("Outstanding, it all works")
