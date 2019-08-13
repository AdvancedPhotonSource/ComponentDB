#!/usr/bin/env python

from cdb.common.constants import cdbRole
from cdb.common.objects.authorizationPrincipal import AuthorizationPrincipal
from cdb.common.db.api.userDbApi import UserDbApi
from authorizationPrincipalRetriever import AuthorizationPrincipalRetriever 

class DbPrincipalRetriever(AuthorizationPrincipalRetriever):

    def __init__(self):
        AuthorizationPrincipalRetriever.__init__(self, self.__class__.__name__)
        self.dbApi = UserDbApi()

    def getAuthorizationPrincipal(self, username):
        principal = None
        try:
            user = self.dbApi.getUserWithPasswordByUsername(username)
            principal = AuthorizationPrincipal(username, user.get('password'))
            principal.setRole(cdbRole.CDB_USER_ROLE)
            principal.setUserInfo(user)
            if self.adminGroupName is not None:
                for userGroup in user.get('userGroupList', []):
                    if userGroup.get('name') == self.adminGroupName:
                        principal.setRole(cdbRole.CDB_ADMIN_ROLE)
        except Exception, ex:
            self.logger.debug(ex)
        return principal

#######################################################################
# Testing.
if __name__ == '__main__':
    pass

