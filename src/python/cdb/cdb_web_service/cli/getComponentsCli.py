#!/usr/bin/env python

from cdbWebServiceCli import CdbWebServiceCli
from cdb.cdb_web_service.api.componentRestApi import ComponentRestApi

class GetComponentsCli(CdbWebServiceCli):
    def __init__(self):
        CdbWebServiceCli.__init__(self)
        self.addOption('', '--name', dest='name', help='Component name (optional).')

    def getName(self):
        return self.options.name

    def runCommand(self):
        self.parseArgs(usage="""
    cdb-get-components [--name=NAME]

Description:
    Retrieves list of components. Command returns all components if name is 
    not specified.
        """)
        api = ComponentRestApi(self.getUsername(), self.getPassword(), self.getServiceHost(), self.getServicePort(), self.getServiceProtocol())
        if self.getName() is None:
            components = api.getComponents()
        else:
            components = api.getComponentsByName(self.getName())

        for component in components:
            print component.getDisplayString(self.getDisplayKeys(), self.getDisplayFormat())


#######################################################################
# Run command.
if __name__ == '__main__':
    cli = GetComponentsCli()
    cli.run()
