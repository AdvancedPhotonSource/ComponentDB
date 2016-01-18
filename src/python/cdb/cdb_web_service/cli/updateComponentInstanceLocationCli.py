#!/usr/bin/env python

from cdbWebServiceCli import CdbWebServiceCli
from cdb.cdb_web_service.api.componentRestApi import ComponentRestApi
from cdb.common.exceptions.invalidRequest import InvalidRequest

class UpdateComponentInstanceLocationCli(CdbWebServiceCli):
    def __init__(self):
        CdbWebServiceCli.__init__(self)
        self.addOption('', '--component-instance-id', dest='componentInstanceId', help='Component instance id (required).')
        self.addOption('', '--location-id', dest='locationId', help='Location id (required).')
        self.addOption('', '--location-details', dest='locationDetails', help='Details about the new location.')

    def checkArgs(self):
        if self.options.componentInstanceId is None:
            raise InvalidRequest('Component instance id must be provided.')
        if self.options.locationId is None:
            raise InvalidRequest('location id must be provided.')

    def getComponentInstanceId(self):
        return self.options.componentInstanceId

    def getLocationId(self):
        return self.options.locationId

    def getLocationDetails(self):
        return self.options.locationDetails

    def runCommand(self):
        self.parseArgs(usage="""
    cdb-update-component-instance-location --component-instance-id=COMPONENTINSTANCEID
        --location-id=LOCATIONID
        [--location-details=LOCATIONDETAILS]

Description:
    Update component instance location.
        """)
        self.checkArgs()
        api = ComponentRestApi(self.getUsername(), self.getPassword(), self.getServiceHost(), self.getServicePort(), self.getServiceProtocol())
        componentInstance = api.updateComponentInstanceLocation(componentInstanceId=self.getComponentInstanceId(), locationId=self.getLocationId(), locationDetails=self.getLocationDetails())
        print componentInstance.getDisplayString(self.getDisplayKeys(), self.getDisplayFormat())

#######################################################################
# Run command.
if __name__ == '__main__':
    cli = UpdateComponentInstanceLocationCli()
    cli.run()

