#!/usr/bin/env python

from cdbWebServiceCli import CdbWebServiceCli
from cdb.cdb_web_service.api.designRestApi import DesignRestApi
from cdb.common.exceptions.invalidRequest import InvalidRequest

class AddDesignCli(CdbWebServiceCli):
    def __init__(self):
        CdbWebServiceCli.__init__(self)
        self.addOption('', '--name', dest='name', help='Design name (required).')
        self.addOption('', '--description', dest='description', help='Design description.')
        self.addOption('', '--owner-user-id', dest='ownerUserId', help='Owner user id. If not provided, user who created design will own it.')
        self.addOption('', '--owner-group-id', dest='ownerGroupId', help='Owner user group id. If not provided, owner group will be set to the default group of the user who created design.')
        self.addOption('', '--is-group-writeable', dest='isGroupWriteable', default=False, help='Group writeable flag (default: False).')

    def checkArgs(self):
        if self.options.name is None:
            raise InvalidRequest('Design name must be provided.')

    def getName(self):
        return self.options.name

    def getOwnerUserId(self):
        return self.options.ownerUserId

    def getOwnerGroupId(self):
        return self.options.ownerGroupId

    def getDescription(self):
        return self.options.description

    def getIsGroupWriteable(self):
        return self.options.isGroupWriteable

    def runCommand(self):
        self.parseArgs(usage="""
    cdb-add-design --name=NAME 
        [--description=DESCRIPTION]
        [--owner-user-id=OWNERUSERID]
        [--owner-group-id=OWNERGROUPID]
        [--is-group-writeable=ISGROUPWRITEABLE]

Description:
    Add design.
        """)
        self.checkArgs()
        api = DesignRestApi(self.getUsername(), self.getPassword(), self.getServiceHost(), self.getServicePort(), self.getServiceProtocol())
        design = api.addDesign(self.getName(), self.getOwnerUserId(), self.getOwnerGroupId(), self.getIsGroupWriteable(), self.getDescription())
        print design.getDisplayString(self.getDisplayKeys(), self.getDisplayFormat())

#######################################################################
# Run command.
if __name__ == '__main__':
    cli = AddDesignCli()
    cli.run()

