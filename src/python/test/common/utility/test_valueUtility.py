from unittest import TestCase

from cdb.common.utility.valueUtility import ValueUtility


class TestValueUtility(TestCase):
    def test_toBoolean(self):
        self.assertEqual(False, ValueUtility.toBoolean('false'))
        self.assertEqual(True, ValueUtility.toBoolean('True'))
        self.assertEqual(True, ValueUtility.toBoolean(True))

