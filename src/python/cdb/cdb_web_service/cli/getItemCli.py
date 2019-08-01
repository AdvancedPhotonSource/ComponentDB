#!/usr/bin/env python

"""
Copyright (c) UChicago Argonne, LLC. All rights reserved.
See LICENSE file.
"""
from cdb.cdb_web_service.api.itemRestApi import ItemRestApi
from cdbWebServiceCli import CdbWebServiceCli
from cdb.common.exceptions.invalidRequest import InvalidRequest

class GetItemCli(CdbWebServiceCli):
    def __init__(self):
        CdbWebServiceCli.__init__(self)
        self.addOption('', '--qr-id', dest='qrId', help='Item QR id')
        self.addOption('', '--id', dest='id', help='Item id')
        self.addOption('', '--item-name', dest='itemName', help='Item name')
        self.addOption('', '--domain-name', dest='domainName', help='Domain name')

    def checkArgs(self):
        if self.options.id is None and self.options.qrId is None and (self.options.itemName is None or self.options.domainName is None):
            raise InvalidRequest('Either item id,  or item QR id, or both item/domain name must be provided.')

    def runCommand(self):
        self.parseArgs(usage="""
    cdb-get-item --id=ID|--qr-id=QRID|--item-name=ITEMNAME --domain-name=DOMAINNAME

Description:
    Gets an item.
        """)
        self.checkArgs()
        api = ItemRestApi(self.getUsername(), self.getPassword(), self.getServiceHost(), self.getServicePort(), self.getServiceProtocol())

        if self.options.id:
            item = api.getItemById(self.options.id)
        elif self.options.qrId:
            item = api.getItemByQrId(self.options.qrId)
        elif self.options.itemName and self.options.domainName:
            item = api.getItemByUniqueAttributes(self.options.domainName, self.options.itemName)
        print(item.getDisplayString(self.getDisplayKeys(), self.getDisplayFormat()))

#######################################################################
# Run command.
def runCommand():
    cli = GetItemCli()
    cli.run()

if __name__ == '__main__':
    runCommand()
