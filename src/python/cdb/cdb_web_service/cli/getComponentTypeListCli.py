#!/usr/bin/env python

from cdbWebServiceCli import CdbWebServiceCli
from cdb.cdb_web_service.api.componentRestApi import ComponentRestApi

class GetComponentTypeListCli(CdbWebServiceCli):
    def __init__(self):
        CdbWebServiceCli.__init__(self)

    def runCommand(self):
        self.parseArgs(usage="""
    cdb-get-component-type-list 

Description:
    Retrieves list of component types.
        """)
        api = ComponentRestApi(self.getUsername(), self.getPassword(), self.getServiceHost(), self.getServicePort(), self.getServiceProtocol())
        componentTypeList = api.getComponentTypeList()
        for componentType in componentTypeList:
            print componentType.getDisplayString(self.getDisplayKeys(), self.getDisplayFormat())


#######################################################################
# Run command.
if __name__ == '__main__':
    cli = GetComponentTypeListCli()
    cli.run()
