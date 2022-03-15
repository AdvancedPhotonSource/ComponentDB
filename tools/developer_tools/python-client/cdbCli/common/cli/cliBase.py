#!/usr/bin/env python
import os

from click import prompt,echo

from getpass import getpass

from CdbApiFactory import CdbApiFactory
from cdbApi import ApiException
from cdbCli.common.utility.configurationManager import ConfigurationManager
try:
    from rich import print
except:
    pass


class CliBase:

    api_factory = None

    def __init__(self,portal_key=None):
        self.config = ConfigurationManager.get_instance()
        if portal_key:
            self._get_api_factory(portal_key)
            
    def _get_api_factory(self,portal_key=None):
        if CliBase.api_factory is None:
            if portal_key:
                portal_URL = self.config.get_portal_address(portal_key)
            else:
                portal_URL = self.config.get_portal_address()
            CliBase.api_factory = CdbApiFactory(portal_URL)
        return CliBase.api_factory

    def get_session_token(self):
        session_file_path = self.config.get_session_file_path()
        if os.path.exists(session_file_path):
            session_file = open(session_file_path, 'r')
            return session_file.read()
        return None

    def set_session_token(self, session_token):
        session_file_path = self.config.get_session_file_path()
        directory = os.path.dirname(session_file_path)
        if not os.path.exists(directory):
            os.makedirs(directory)

        session_file = open(session_file_path, 'w')
        session_file.write(session_token)

    def require_api(self,portal_key=None):
        apiFactory = self._get_api_factory(portal_key)
        return apiFactory

    def require_authenticated_api(self,
                                  prompt_string="The command requires authentication.",
                                  portal_key=None):
        factory = self.require_api(portal_key)
        factory.getItemApi()

        try:
            token = self.get_session_token()
            if token is not None:
                factory.setAuthenticateToken(token)
            factory.testAuthenticated()
            return(factory)
        except ApiException as ex:
            pass
        # The session key doesn't work.  We now 
        # See if the username and password are in the Configuration data
        username,password = self.config.get_session_credentials()
        if (username != None) and (password != None):
            try:
                factory.authenticateUser(username, password)
                token = factory.getAuthenticateToken()
                self.set_session_token(token)
                return(factory)
            except ApiException as ex:
                exObj = factory.parseApiException(ex)
                echo("Local configured password failed: %s" % exObj.simple_name)

        # Need to prompt for credentials
        echo(prompt_string)
        username = prompt("Username")
        password = getpass("Password: ")
        try:
            factory.authenticateUser(username, password)
            token = factory.getAuthenticateToken()
            self.set_session_token(token)
            return factory
        except ApiException as ex:
            exObj = factory.parseApiException(ex)
            raise Exception("%s - %s" % (exObj.simple_name, exObj.message))

    @staticmethod
    def print_cdb_obj(cdb_object):
        cdb_object = str(cdb_object) + '\n'
        echo(cdb_object)

    # TODO Add a print list cdb of cdb object

if __name__ == "__main__":
    cli = CliBase()
    factory = cli.require_authenticated_api()
