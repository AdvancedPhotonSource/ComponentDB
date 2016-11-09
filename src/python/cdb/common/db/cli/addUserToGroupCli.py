#!/usr/bin/env python

"""
Copyright (c) UChicago Argonne, LLC. All rights reserved.
See LICENSE file.
"""


from cdb.common.exceptions.invalidRequest import InvalidRequest
from cdb.common.utility.cryptUtility import CryptUtility
from cdb.common.cli.cdbDbCli import CdbDbCli
from cdb.common.db.api.userDbApi import UserDbApi

class AddUserToGroupCli(CdbDbCli):
    def __init__(self):
        CdbDbCli.__init__(self)
        self.addOption('', '--username', dest='username', help='User username.')
        self.addOption('', '--group-name', dest='groupName', help='To group name.')

    def checkArgs(self):
        if self.options.username is None:
            raise InvalidRequest('Username must be provided.')
        if self.options.groupName is None:
            raise InvalidRequest('Group name must be provided.')

    def getUsername(self):
        return self.options.username

    def getGroupName(self):
        return self.options.groupName

    def runCommand(self):
        self.parseArgs(usage="""
    cdb-add-user-to-group --username=USERNAME --group-name=GROUPNAME

Description:
    Adds user to group. This command goes directly to the
    database and must be run from a CDB administrator account.
        """)
        self.checkArgs()
        api = UserDbApi()
        username = self.getUsername()
        groupName = self.getGroupName()
        userUserGroup = api.addUserToGroup(username, groupName)
        print userUserGroup.getDisplayString(self.getDisplayKeys(), self.getDisplayFormat())

#######################################################################
# Run command.
if __name__ == '__main__':
    cli = AddUserToGroupCli()
    cli.run()
