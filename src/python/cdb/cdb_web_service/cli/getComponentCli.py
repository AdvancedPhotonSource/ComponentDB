#!/usr/bin/env python

from cdbWebServiceCli import CdbWebServiceCli
from cdb.cdb_web_service.api.componentRestApi import ComponentRestApi
from cdb.common.exceptions.invalidRequest import InvalidRequest

class GetComponentCli(CdbWebServiceCli):
    def __init__(self):
        CdbWebServiceCli.__init__(self)
        self.addOption('', '--id', dest='id', help='Component id. Either id, name, or model number must be provided. If more than one argument is provided, order of precedence is id, name, model number.')
        self.addOption('', '--name', dest='name', help='Component name. Either id, name, or model number must be provided. If more than one argument is provided, order of precedence is id, name, model number.')
        self.addOption('', '--model-number', dest='modelNumber', help='Component model number. Either id, name, or model number must be provided. If more than one argument is provided, order of precedence is id, name, model number.')

    def checkArgs(self):
        if self.options.id is None and self.options.name is None and self.options.modelNumber is None:
            raise InvalidRequest('One of component id, name or model number must be provided.')

    def getId(self):
        return self.options.id

    def getName(self):
        return self.options.name

    def getModelNumber(self):
        return self.options.modelNumber

    def runCommand(self):
        self.parseArgs(usage="""
    cdb-get-component --id=ID|--name=NAME|--model-number=MODELNUMBER

Description:
    Retrieves component.
        """)
        self.checkArgs()
        api = ComponentRestApi(self.getUsername(), self.getPassword(), self.getServiceHost(), self.getServicePort(), self.getServiceProtocol())
        if self.getId() is not None:
            component = api.getComponentById(self.getId())
        elif self.getName() is not None:
            component = api.getComponentByName(self.getName())
        else:
            component = api.getComponentByModelNumber(self.getModelNumber())
        print component.getDisplayString(self.getDisplayKeys(), self.getDisplayFormat())

#######################################################################
# Run command.
if __name__ == '__main__':
    cli = GetComponentCli()
    cli.run()

