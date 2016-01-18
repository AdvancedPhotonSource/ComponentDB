#!/usr/bin/env python

from cdbWebServiceCli import CdbWebServiceCli
from cdb.cdb_web_service.api.componentRestApi import ComponentRestApi
from cdb.common.exceptions.invalidRequest import InvalidRequest

class GetComponentLocationHistoryCli(CdbWebServiceCli):
    def __init__(self):
        CdbWebServiceCli.__init__(self)
        self.addOption('', '--component-instance-id', dest='componentInstanceId', help='Component instance id must be provided.')

    def checkArgs(self):
        if self.options.componentInstanceId is None:
            raise InvalidRequest('Component instance id must be provided.')

    def getComponentInstanceId(self):
        return self.options.componentInstanceId


    def runCommand(self):
        self.parseArgs(usage="""
    cdb-get-component-instance-location-history --component-instance-id=COMPONENTINSTANCEID

Description:
    Retrieves component instance location history.
        """)
        self.checkArgs()
        api = ComponentRestApi(self.getUsername(), self.getPassword(), self.getServiceHost(), self.getServicePort(), self.getServiceProtocol())
        componentInstanceLocationHistory = api.getComponentInstanceLocationHistoryByComponentInstnaceId(self.getComponentInstanceId())

        for locationHistory in componentInstanceLocationHistory:
            print locationHistory.getDisplayString(self.getDisplayKeys(), self.getDisplayFormat())

#######################################################################
# Run command.
if __name__ == '__main__':
    cli = GetComponentLocationHistoryCli()
    cli.run()

