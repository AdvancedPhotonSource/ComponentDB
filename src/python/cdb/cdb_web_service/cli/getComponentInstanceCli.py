#!/usr/bin/env python

from cdbWebServiceCli import CdbWebServiceCli
from cdb.cdb_web_service.api.componentRestApi import ComponentRestApi
from cdb.common.exceptions.invalidRequest import InvalidRequest

class GetComponentCli(CdbWebServiceCli):
    def __init__(self):
        CdbWebServiceCli.__init__(self)
        self.addOption('', '--id', dest='id', help='Component id. Either id, name, or model number must be provided. If more than one argument is provided, order of precedence is id, name, model number.')

    def checkArgs(self):
        if self.options.id is None:
            raise InvalidRequest('Component instance id must be provided.')

    def getId(self):
        return self.options.id


    def runCommand(self):
        self.parseArgs(usage="""
    cdb-get-component-instance --id=ID

Description:
    Retrieves component instance.
        """)
        self.checkArgs()
        api = ComponentRestApi(self.getUsername(), self.getPassword(), self.getServiceHost(), self.getServicePort(), self.getServiceProtocol())
        componentInstance = api.getComponentInstanceById(self.getId())

        print componentInstance.getDisplayString(self.getDisplayKeys(), self.getDisplayFormat())

#######################################################################
# Run command.
if __name__ == '__main__':
    cli = GetComponentCli()
    cli.run()

