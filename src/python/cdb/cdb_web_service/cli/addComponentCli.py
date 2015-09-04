#!/usr/bin/env python

from cdbWebServiceCli import CdbWebServiceCli
from cdb.cdb_web_service.api.componentRestApi import ComponentRestApi
from cdb.common.exceptions.invalidRequest import InvalidRequest

class AddComponentCli(CdbWebServiceCli):
    def __init__(self):
        CdbWebServiceCli.__init__(self)
        self.addOption('', '--name', dest='name', help='Component name (required).')
        self.addOption('', '--model-number', dest='modelNumber', help='Component model number (optional).')
        self.addOption('', '--component-type-id', dest='componentTypeId', help='Component type id (required).')
        self.addOption('', '--description', dest='description', help='Component description.')
        self.addOption('', '--owner-user-id', dest='ownerUserId', help='Owner user id. If not provided, user who created component will own it.')
        self.addOption('', '--owner-group-id', dest='ownerGroupId', help='Owner user group id. If not provided, owner group will be set to the default group of the user who created component.')
        self.addOption('', '--is-group-writeable', dest='isGroupWriteable', default=False, help='Group writeable flag (default: False).')

    def checkArgs(self):
        if self.options.name is None:
            raise InvalidRequest('Component name must be provided.')
        if self.options.componentTypeId is None:
            raise InvalidRequest('Component type id must be provided.')

    def getName(self):
        return self.options.name

    def getModelNumber(self):
        return self.options.modelNumber

    def getComponentTypeId(self):
        return self.options.componentTypeId

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
    cdb-add-component --name=NAME --component-type-id=COMPONENTTYPEID 
        [--model-number=MODELNUMBER]
        [--description=DESCRIPTION]
        [--owner-user-id=OWNERUSERID]
        [--owner-group-id=OWNERGROUPID]
        [--is-group-writeable=ISGROUPWRITEABLE]

Description:
    Add component.
        """)
        self.checkArgs()
        api = ComponentRestApi(self.getUsername(), self.getPassword(), self.getServiceHost(), self.getServicePort(), self.getServiceProtocol())
        component = api.addComponent(self.getName(), self.getModelNumber(), self.getComponentTypeId(), self.getOwnerUserId(), self.getOwnerGroupId(), self.getIsGroupWriteable(), self.getDescription())
        print component.getDisplayString(self.getDisplayKeys(), self.getDisplayFormat())

#######################################################################
# Run command.
if __name__ == '__main__':
    cli = AddComponentCli()
    cli.run()

