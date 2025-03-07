import unittest
from cdbApi.exceptions import OpenApiException
from test.cdb_test_base import CdbTestBase


class LogTest(CdbTestBase):

    def test_log_api(self):
        self.loginAsUser()
        fail = False
        try:
            result = self.logApi.get_successful_login_log()
        except OpenApiException as ex:
            fail = True
        self.assertEqual(fail, True)

        self.loginAsAdmin()
        result = self.logApi.get_successful_login_log()
        self.assertNotEqual(result, None)


if __name__ == "__main__":
    unittest.main()
