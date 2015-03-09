#!/usr/bin/env python

from cdbWebServiceCli import CdbWebServiceCli
from cdb.cdb_web_service.api.componentRestApi import ComponentRestApi

class GetComponentsCli(CdbWebServiceCli):
    def __init__(self):
        CdbWebServiceCli.__init__(self)

    def runCommand(self):
        self.parseArgs(usage="""
    cdb-get-components 

Description:
    Retrieves list of components.
        """)
        api = ComponentRestApi(self.getUsername(), self.getPassword(), self.getServiceHost(), self.getServicePort(), self.getServiceProtocol())
        components = api.getComponents()
        for component in components:
            print component.getDisplayString(self.getDisplayKeys(), self.getDisplayFormat())


#######################################################################
# Run command.
if __name__ == '__main__':
    cli = GetComponentsCli()
    cli.run()
