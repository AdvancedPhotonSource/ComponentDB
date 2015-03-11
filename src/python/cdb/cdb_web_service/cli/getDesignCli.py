#!/usr/bin/env python

from cdbWebServiceCli import CdbWebServiceCli
from cdb.cdb_web_service.api.designRestApi import DesignRestApi
from cdb.common.exceptions.invalidRequest import InvalidRequest

class GetDesignCli(CdbWebServiceCli):
    def __init__(self):
        CdbWebServiceCli.__init__(self)
        self.addOption('', '--id', dest='id', help='Design id. Either id or name must be provided. If both are provided, id takes precedence.')
        self.addOption('', '--name', dest='name', help='Design name. Either id or name must be provided. If both are provided, id takes precedence.')

    def checkArgs(self):
        if self.options.id is None and self.options.name is None:
            raise InvalidRequest('Either design id or name must be provided.')

    def getId(self):
        return self.options.id

    def getName(self):
        return self.options.name

    def runCommand(self):
        self.parseArgs(usage="""
    cdb-get-design --id=ID|--name=NAME

Description:
    Retrieves design.
        """)
        self.checkArgs()
        api = DesignRestApi(self.getUsername(), self.getPassword(), self.getServiceHost(), self.getServicePort(), self.getServiceProtocol())
        if self.getId() is not None:
            design = api.getDesignById(self.getId())
        else:
            design = api.getDesignByName(self.getName())
        print design.getDisplayString(self.getDisplayKeys(), self.getDisplayFormat())

#######################################################################
# Run command.
if __name__ == '__main__':
    cli = GetDesignCli()
    cli.run()

