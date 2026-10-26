import unittest
from cdbApi.exceptions import OpenApiException
from test.cdb_test_base import CdbTestBase


class UserTest(CdbTestBase):

    def test_user_route(self):
        users = self.userApi.get_all1()
        self.assertNotEqual(users, None)

        username = users[0].username
        user_by_username = self.userApi.get_user_by_username(username)

        self.assertEqual(user_by_username.username, username)


if __name__ == "__main__":
    unittest.main()
