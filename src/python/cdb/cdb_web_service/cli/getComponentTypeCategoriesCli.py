#!/usr/bin/env python

from cdbWebServiceCli import CdbWebServiceCli
from cdb.cdb_web_service.api.componentRestApi import ComponentRestApi

class GetComponentTypeCategoriesCli(CdbWebServiceCli):
    def __init__(self):
        CdbWebServiceCli.__init__(self)

    def runCommand(self):
        self.parseArgs(usage="""
    cdb-get-component-type-categories

Description:
    Retrieves list of component type categories.
        """)
        api = ComponentRestApi(self.getUsername(), self.getPassword(), self.getServiceHost(), self.getServicePort(), self.getServiceProtocol())
        componentTypeCategories = api.getComponentTypeCategories()
        for componentTypeCategory in componentTypeCategories:
            print componentTypeCategory.getDisplayString(self.getDisplayKeys(), self.getDisplayFormat())


#######################################################################
# Run command.
if __name__ == '__main__':
    cli = GetComponentTypeCategoriesCli()
    cli.run()
