from unittest import TestCase

from cdb.common.utility.cryptUtility import CryptUtility

class TestCryptUtility(TestCase):
    def test_getRandomWord(self):
        cryptUtility = CryptUtility()
        random = cryptUtility.getRandomWord(45)
        self.assertEqual(45, random.__len__(), "Random word generated is not correct length.")

    def test_cryptPasswordAndVerify(self):
        cryptUtility = CryptUtility()
        password = "Hello World"
        cryptedPassword = cryptUtility.cryptPassword(password)
        self.assertTrue(cryptUtility.verifyPassword(password, cryptedPassword), "Failed to decrpt/encrypt password")
