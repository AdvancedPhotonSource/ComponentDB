#!/usr/bin/env python

from cdb.common.exceptions.invalidRequest import InvalidRequest
from cdb.common.utility.cryptUtility import CryptUtility
from cdb.common.cli.cdbDbCli import CdbDbCli
from dbLegacy.api.userDbApi import UserDbApi

class AddUserCli(CdbDbCli):
    def __init__(self):
        CdbDbCli.__init__(self)
        self.addOption('', '--username', dest='username', help='User username.')
        self.addOption('', '--first-name', dest='firstName', help='User first name.')
        self.addOption('', '--last-name', dest='lastName', help='User last name.')
        self.addOption('', '--middle-name', dest='middleName', help='User middle name.')
        self.addOption('', '--email', dest='email', help='User email.')
        self.addOption('', '--description', dest='description', help='User description.')
        self.addOption('', '--password', dest='password', help='User password. If both password and password file options are used, the password option takes precedence.')
        self.addOption('', '--password-file', dest='passwordFile', help='File containing user password in plain text. If both password and password file options are used, the password option takes precedence.')

    def checkArgs(self):
        if self.options.username is None:
            raise InvalidRequest('Username must be provided.')
        if self.options.firstName is None:
            raise InvalidRequest('First name must be provided.')
        if self.options.lastName is None:
            raise InvalidRequest('Last name must be provided.')

    def getUsername(self):
        return self.options.username

    def getFirstName(self):
        return self.options.firstName

    def getLastName(self):
        return self.options.lastName

    def getMiddleName(self):
        return self.options.middleName

    def getEmail(self):
        return self.options.email

    def getDescription(self):
        return self.options.description

    def getPassword(self):
        if self.options.password:
            return self.options.password
        if self.options.passwordFile:
            return open(self.options.passwordFile).read().strip()
        return None

    def runCommand(self):
        self.parseArgs(usage="""
    cdb-add-user --username=USERNAME --first-name=FIRSTNAME --last-name=LASTNAME
        [--middle-name=MIDDLENAME]
        [--email=EMAIL]
        [--description=DESCRIPTION]
        [--password=PASSWORD|--password-file=PASSWORDFILE]

Description:
    Adds new user into CDB database. This command goes directly to the
    database and must be run from a CDB administrator account.
        """)
        self.checkArgs()
        api = UserDbApi()
        username = self.getUsername()
        firstName = self.getFirstName()
        lastName = self.getLastName()
        middleName = self.getMiddleName()
        email = self.getEmail()
        description = self.getDescription()
        password = self.getPassword()
        cryptedPassword = None
        if password:
            cryptedPassword = CryptUtility.cryptPasswordWithPbkdf2(password)
        userInfo = api.addUser(username, firstName, lastName, middleName, email, description, cryptedPassword)
        print userInfo.getDisplayString(self.getDisplayKeys(), self.getDisplayFormat())

#######################################################################
# Run command.
if __name__ == '__main__':
    cli = AddUserCli()
    cli.run()
