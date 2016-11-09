#!/usr/bin/env python

"""
Copyright (c) UChicago Argonne, LLC. All rights reserved.
See LICENSE file.
"""


import random
import string
import crypt
#import md5
import hashlib
import base64

class CryptUtility:

    CRYPT_TYPE = 6  # SHA-512 (man crypt)
    SALT_CHARACTERS = string.lowercase + string.uppercase + string.digits
    SALT_DELIMITER = '$'
    SALT_LENGTH_IN_BYTES = 4

    PBKDF2_ENCRYPTION = 'sha1' # use SHA-1 for compatibility with java
    PBKDF2_KEY_LENGTH_IN_BYTES = 24
    PBKDF2_ITERATIONS = 1003

    @classmethod
    def getRandomWord(cls, length):
        return ''.join(random.choice(CryptUtility.SALT_CHARACTERS) for i in range(length))

    @classmethod
    def cryptPassword(cls, password):
        """ Return crypted password. """
        #calculator = md5.md5()
        #calculator.update(salt)
        #md5Salt = calculator.hexdigest()
        #return crypt.crypt(cleartext, md5Salt)

        salt = CryptUtility.getRandomWord(CryptUtility.SALT_LENGTH_IN_BYTES)
        salt = '%s%s%s%s%s'.format(CryptUtility.SALT_DELIMITER, CryptUtility.CRYPT_TYPE, CryptUtility.SALT_DELIMITER, salt, CryptUtility.SALT_DELIMITER)
        return crypt.crypt(password, salt)

    @classmethod
    def verifyPassword(cls, password, cryptedPassword):
        """ Verify crypted password. """
        return cryptedPassword == crypt.crypt(password, cryptedPassword)

    @classmethod
    def cryptPasswordWithPbkdf2(cls, password):
        """ Crypt password with pbkdf2 package and encode with b64. """
        salt = CryptUtility.getRandomWord(CryptUtility.SALT_LENGTH_IN_BYTES)
        return cls.saltAndCryptPasswordWithPbkdf2(password, salt)

    @classmethod
    def saltAndCryptPasswordWithPbkdf2(cls, password, salt):
        """ Crypt salted password with pbkdf2 package and encode with b64. """
        cryptedPassword = hashlib.pbkdf2_hmac(
            CryptUtility.PBKDF2_ENCRYPTION, 
            password, salt, 
            CryptUtility.PBKDF2_ITERATIONS, 
            CryptUtility.PBKDF2_KEY_LENGTH_IN_BYTES)
        encodedPassword = base64.b64encode(cryptedPassword)
        return '%s%s%s' % (salt, CryptUtility.SALT_DELIMITER, encodedPassword)

    @classmethod
    def verifyPasswordWithPbkdf2(cls, password, cryptedPassword):
        """ Verify crypted password. """
        # Get salt
        salt = '%s' % cryptedPassword.split(CryptUtility.SALT_DELIMITER)[0]
        # Verify crypted password
        return cryptedPassword == cls.saltAndCryptPasswordWithPbkdf2(password, salt)

#######################################################################
# Testing.

if __name__ == '__main__':
    import sys
    #password = "cdb"
    password = sys.argv[1]
    print 'Clear text: ', password
    #cryptedPassword = CryptUtility.cryptPassword(password)
    #print 'Crypted: ', cryptedPassword
    #isVerified = CryptUtility.verifyPassword(password, cryptedPassword)
    #print 'Verify: ', isVerified

    cryptedPassword = CryptUtility.cryptPasswordWithPbkdf2(password)
    print 'Crypted: ', cryptedPassword
    isVerified = CryptUtility.verifyPasswordWithPbkdf2(password, cryptedPassword)
    print 'Verify: ', isVerified

