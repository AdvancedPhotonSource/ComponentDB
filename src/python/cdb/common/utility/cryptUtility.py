#!/usr/bin/env python

import random
import string
import crypt
#import md5

class CryptUtility:

    SALT_LENGTH = 2
    SALT_CHARACTERS = string.lowercase + string.uppercase + string.digits

    @classmethod
    def getRandomWord(cls, length):
        return ''.join(random.choice(CryptUtility.SALT_CHARACTERS) for i in range(length))

    @classmethod
    def cryptPassword(cls, cleartext):
        """ Return crypted password. """
        #calculator = md5.md5()
        #calculator.update(salt)
        #md5Salt = calculator.hexdigest()
        #return crypt.crypt(cleartext, md5Salt)
        salt = CryptUtility.getRandomWord(CryptUtility.SALT_LENGTH)
        return crypt.crypt(cleartext, salt)

    @classmethod
    def verifyPassword(cls, password, cryptedPassword):
        """ Verify crypted password. """
        return cryptedPassword == crypt.crypt(password, cryptedPassword)

#######################################################################
# Testing.

if __name__ == '__main__':
    password = "adrianIsTuntu"
    print 'Clear text: ', password
    cryptedPassword = CryptUtility.cryptPassword(password)
    print 'Crypted: ', cryptedPassword
    isVerified = CryptUtility.verifyPassword(password, cryptedPassword)
    print 'Verify: ', isVerified

