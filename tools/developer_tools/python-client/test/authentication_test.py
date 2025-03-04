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
        self.loggedIn = False

        with self.assertRaises(OpenApiException) as context:
            self.factory.testAuthenticated()
        self.assertIsNotNone(
            context.exception, "The user was not logged out. Auth function passes."
        )

    def test_invalid_login(self):
        """
        Test logging in with invalid credentials.
        """
        invalid_username = "invalidUser"
        invalid_password = "invalidPassword"

        with self.assertRaises(OpenApiException) as context:
            self.factory.authenticateUser(invalid_username, invalid_password)

        self.assertIsNotNone(
            context.exception, msg="Invalid login should raise an exception."
        )


if __name__ == "__main__":
    unittest.main()
