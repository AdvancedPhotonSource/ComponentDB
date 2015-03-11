#!/usr/bin/env python

from cdbWebServiceCli import CdbWebServiceCli
from cdb.cdb_web_service.api.componentRestApi import ComponentRestApi
from cdb.common.exceptions.invalidRequest import InvalidRequest

class GetComponentCli(CdbWebServiceCli):
    def __init__(self):
        CdbWebServiceCli.__init__(self)
        self.addOption('', '--id', dest='id', help='Component id. Either id or name must be provided. If both are provided, id takes precedence.')
        self.addOption('', '--name', dest='name', help='Component name. Either id or name must be provided. If both are provided, id takes precedence.')

    def checkArgs(self):
        if self.options.id is None and self.options.name is None:
            raise InvalidRequest('Either component id or name must be provided.')

    def getId(self):
        return self.options.id

    def getName(self):
        return self.options.name

    def runCommand(self):
        self.parseArgs(usage="""
    cdb-get-component --id=ID|--name=NAME

Description:
    Retrieves component.
        """)
        self.checkArgs()
        api = ComponentRestApi(self.getUsername(), self.getPassword(), self.getServiceHost(), self.getServicePort(), self.getServiceProtocol())
        if self.getId() is not None:
            component = api.getComponentById(self.getId())
        else:
            component = api.getComponentByName(self.getName())
        print component.getDisplayString(self.getDisplayKeys(), self.getDisplayFormat())

#######################################################################
# Run command.
if __name__ == '__main__':
    cli = GetComponentCli()
    cli.run()

