#!/usr/bin/env python

from cdbWebServiceCli import CdbWebServiceCli
from cdb.common.exceptions.invalidRequest import InvalidRequest
from cdb.cdb_web_service.api.pdmLinkRestApi import PdmLinkRestApi

class AddPdmLinkComponentCli(CdbWebServiceCli):
    def __init__(self):
        CdbWebServiceCli.__init__(self)
        self.addOption('', '--drawing-number', dest='drawingNumber', help='Drawing number (required).')
        self.addOption('', '--component-type-id', dest='componentTypeId', help='Component type id (highly recommended). Defaults to a temp assignment')
        self.addOption('', '--component-type-name', dest='componentTypeName', help='Component type name could be provided instead of component type id')
        self.addOption('', '--description', dest='description', help='Component description.')
        self.addOption('', '--owner-user-id', dest='ownerUserId', help='Owner user id. If not provided, user who created component will own it.')
        self.addOption('', '--owner-group-id', dest='ownerGroupId', help='Owner user group id. If not provided, owner group will be set to the default group of the user who created component.')
        self.addOption('', '--is-group-writeable', dest='isGroupWriteable', default=False, help='Group writeable flag (default: False).')

    def checkArgs(self):
        if self.options.drawingNumber is None:
            raise InvalidRequest('Drawing Number must be provided.')

    def getDrawingNumber(self):
        return self.options.drawingNumber

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

    def getComponentTypeName(self):
        return self.options.componentTypeName

    def runCommand(self):
        self.parseArgs(usage="""
    cdb-add-component --drawing-number=DRAWINGNUMBER
        [--component-type-id=COMPONENTTYPEID]
        [--component-type-id=COMPONENTTYPENAME]
        [--description=DESCRIPTION]
        [--owner-user-id=OWNERUSERID]
        [--owner-group-id=OWNERGROUPID]
        [--is-group-writeable=ISGROUPWRITEABLE]

Description:
    Generate a component using a PdmLink drawing number and add it.
        """)
        self.checkArgs()
        api = PdmLinkRestApi(self.getUsername(), self.getPassword(), self.getServiceHost(), self.getServicePort(), self.getServiceProtocol())
        component = api.createComponent(self.getDrawingNumber(), self.getComponentTypeId(),  self.getDescription(), self.getOwnerUserId(), self.getOwnerGroupId(), self.getIsGroupWriteable(), self.getComponentTypeName())
        print component.getDisplayString(self.getDisplayKeys(), self.getDisplayFormat())

#######################################################################
# Run command.
if __name__ == '__main__':
    cli = AddPdmLinkComponentCli()
    cli.run()

