#!/usr/bin/env python

from cdbWebServiceCli import CdbWebServiceCli
from cdb.cdb_web_service.api.designRestApi import DesignRestApi
from cdb.common.exceptions.invalidRequest import InvalidRequest

class AddDesignElementPropertyCli(CdbWebServiceCli):
    def __init__(self):
        CdbWebServiceCli.__init__(self)
        self.addOption('', '--design-element-id', dest='designElementId', help='Design element id (required).')
        self.addOption('', '--property-type-id', dest='propertyTypeId', help='Property type id (required).')
        self.addOption('', '--tag', dest='tag', help='Property value tag.')
        self.addOption('', '--value', dest='value', help='Property value.')
        self.addOption('', '--units', dest='units', help='Property value units.')
        self.addOption('', '--description', dest='description', help='Property value description.')
        self.addOption('', '--is-dynamic', dest='isDynamic', default=False, help='Dynamic flag (default: False).')
        self.addOption('', '--is-user-writeable', dest='isUserWriteable', default=False, help='User writeable flag (default: False).')

    def checkArgs(self):
        if self.options.designElementId is None:
            raise InvalidRequest('Design element id must be provided.')
        if self.options.propertyTypeId is None:
            raise InvalidRequest('Property type id must be provided.')

    def getDesignElementId(self):
        return self.options.designElementId

    def getPropertyTypeId(self):
        return self.options.propertyTypeId

    def getDescription(self):
        return self.options.description

    def getTag(self):
        return self.options.tag

    def getValue(self):
        return self.options.value

    def getUnits(self):
        return self.options.units

    def getIsDynamic(self):
        return self.options.isDynamic

    def getIsUserWriteable(self):
        return self.options.isUserWriteable

    def runCommand(self):
        self.parseArgs(usage="""
    cdb-add-design-element-property --design-element-id=DESIGNELEMENTID 
        --property-type-id=PROPERTYTYPEID
        [--tag=TAG]
        [--value=VALUE]
        [--units=UNITS]
        [--description=DESCRIPTION]
        [--is-dynamic=ISDYNAMIC]
        [--is-user-writeable=ISUSERWRITEABLE]

Description:
    Add design element property.
        """)
        self.checkArgs()
        api = DesignRestApi(self.getUsername(), self.getPassword(), self.getServiceHost(), self.getServicePort(), self.getServiceProtocol())
        designElementProperty = api.addDesignElementProperty(designElementId=self.getDesignElementId(), propertyTypeId=self.getPropertyTypeId(), tag=self.getTag(), value=self.getValue(), units=self.getUnits(), description=self.getDescription(), isDynamic=self.getIsDynamic(), isUserWriteable=self.getIsUserWriteable())
        print designElementProperty.getDisplayString(self.getDisplayKeys(), self.getDisplayFormat())

#######################################################################
# Run command.
if __name__ == '__main__':
    cli = AddDesignElementPropertyCli()
    cli.run()

