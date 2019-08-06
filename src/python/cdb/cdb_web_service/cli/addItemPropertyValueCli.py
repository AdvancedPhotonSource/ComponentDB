#!/usr/bin/env python

"""
Copyright (c) UChicago Argonne, LLC. All rights reserved.
See LICENSE file.
"""
from cdb.cdb_web_service.api.itemRestApi import ItemRestApi
from cdbWebServiceSessionCli import CdbWebServiceSessionCli
from cdb.common.exceptions.invalidRequest import InvalidRequest

class addItemPropertyValueCli(CdbWebServiceSessionCli):
    def __init__(self):
        CdbWebServiceSessionCli.__init__(self)
        self.addOption('', '--item-id', dest='itemId', help='item id of item to add property value for')
        self.addOption('', '--property-type-name', dest='propertyTypeName', help='Property type name of the property to add to item')
        self.addOption('', '--value', dest='value', help='value of the newly added property')
        self.addOption('', '--display-value', dest='displayValue', help='display value of the newly added property')

    def checkArgs(self):
        if self.options.itemId is None:
            raise InvalidRequest('Item id must be provided.')
        if self.options.propertyTypeName is None:
            raise InvalidRequest('Property type name must be provided.')

    def getItemId(self):
        return self.options.itemId

    def getPropertyTypeName(self):
        return self.options.propertyTypeName

    def getValue(self):
        return self.options.value

    def getDisplayValue(self):
        return self.options.displayValue

    def runCommand(self):
        self.parseArgs(usage="""
    cdb-add-item-property --item-id=ITEMID
        --property-type-name=PROPERTYTYPENAME

Description:
    Adds a property value to an item with a id.
        """)
        self.checkArgs()
        api = ItemRestApi(self.getUsername(), self.getPassword(), self.getServiceHost(), self.getServicePort(), self.getServiceProtocol())

        propertyValue = api.addPropertyValueToItemWithId(self.getItemId(), self.getPropertyTypeName(), value=self.getValue(), displayValue=self.getDisplayValue())

        print propertyValue.getDisplayString(self.getDisplayKeys(), self.getDisplayFormat())

#######################################################################
# Run command.
def runCommand():
    cli = addItemPropertyValueCli()
    cli.run()

if __name__ == '__main__':
    runCommand()
