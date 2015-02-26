#!/usr/bin/env python

from cdbWebServiceCli import CdbWebServiceCli
from cdb.cdb_web_service.api.userInfoRestApi import UserInfoRestApi

class GetUserListCli(CdbWebServiceCli):
    def __init__(self):
        CdbWebServiceCli.__init__(self)

    def runCommand(self):
        self.parseArgs(usage="""
    cdb-get-user-list 

Description:
    Retrieves list of registered users.
        """)
        api = UserInfoRestApi(self.getUsername(), self.getPassword(), self.getServiceHost(), self.getServicePort(), self.getServiceProtocol())
        userInfoList = api.getUserInfoList()
        for userInfo in userInfoList:
            print userInfo.getDisplayString(self.getDisplayKeys(), self.getDisplayFormat())


#######################################################################
# Run command.
if __name__ == '__main__':
    cli = GetUserListCli()
    cli.run()
