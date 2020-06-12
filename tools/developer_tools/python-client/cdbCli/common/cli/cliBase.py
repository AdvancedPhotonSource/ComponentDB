import os

from click._compat import raw_input

from getpass import getpass

from CdbApiFactory import CdbApiFactory
from cdbApi import ApiException
from cdbCli.common.utility.configurationManager import ConfigurationManager


class CliBase:

    api_factory = None

    def __init__(self):
        self.config = ConfigurationManager.get_instance()

    def _get_api_factory(self):
        if CliBase.api_factory is None:
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

    def require_api(self):
        apiFactory = self._get_api_factory()
        return apiFactory

    def require_authenticated_api(self):
        factory = self.require_api()
        factory.getItemApi()

        try:
            token = self.get_session_token()
            if token is not None:
                factory.setAuthenticateToken(token)
            factory.testAuthenticated()
        except ApiException as ex:
            # Need to prompt for credentials
            print("The command requires authentication.")
            username = raw_input("Username: ")
            password = getpass("Password: ")
            try:
                factory.authenticateUser(username, password)
                token = factory.getAuthenticateToken()
                self.set_session_token(token)
            except ApiException as ex:
                raise ex
                print("Authentication failed.")

        return factory

    @staticmethod
    def print_cdb_obj(cdb_object):
        cdb_object = str(cdb_object) + '\n'
        print(cdb_object)

    # TODO Add a print list cdb of cdb object
