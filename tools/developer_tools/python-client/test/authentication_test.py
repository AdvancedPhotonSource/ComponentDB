import unittest
from cdbApi.exceptions import OpenApiException
from test.cdb_test_base import CdbTestBase


class AuthenticationTest(CdbTestBase):

    def test_login_as_admin(self):
        """
        Test logging in as an admin user.
        """
        try:
            self.loginAsAdmin()
            self.factory.testAuthenticated()
        except OpenApiException as ex:
            self.fail(f"Admin login failed with exception: {ex}")

    def test_login_as_user(self):
        """
        Test logging in as a standard test user.
        """
        try:
            self.loginAsUser()
            self.factory.testAuthenticated()
        except OpenApiException as ex:
            self.fail(f"User login failed with exception: {ex}")

    def test_logout(self):
        """
        Test logging out after logging in.
        """
        self.loginAsAdmin()
        self.factory.logOutUser()

        failed = False

        try:
            self.factory.testAuthenticated()
        except OpenApiException as ex:
            failed = True

        self.loggedIn = False
        self.assertTrue(failed, "The user was not logged out. Auth function passes.")

    def test_invalid_login(self):
        """
        Test logging in with invalid credentials.
        """
        invalid_username = "invalidUser"
        invalid_password = "invalidPassword"
        failed = False

        try:
            self.factory.authenticateUser(invalid_username, invalid_password)
        except OpenApiException:
            failed = True

        self.assertTrue(failed, msg="Invalid login should raise an exception.")


if __name__ == "__main__":
    unittest.main()
