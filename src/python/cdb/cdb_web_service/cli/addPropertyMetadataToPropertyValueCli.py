#!/usr/bin/env python

"""
Copyright (c) UChicago Argonne, LLC. All rights reserved.
See LICENSE file.
"""
from cdb.cdb_web_service.api.propertyRestApi import PropertyRestApi
from cdbWebServiceCli import CdbWebServiceCli
from cdb.common.exceptions.invalidRequest import InvalidRequest

class addPropertyMetadataToPropertyValueCli(CdbWebServiceCli):
    def __init__(self):
        CdbWebServiceCli.__init__(self)
        self.addOption('', '--property-value-id', dest='propertyValueId', help='Property value id of the property metadata is being added to')
        self.addOption('', '--metadata-dict', dest='metadataDict', help='Metadata dict with key value pairs for new metadata')
        self.addOption('', '--metadata-key', dest='metadataKey', help='Key of the metadata value being added')
        self.addOption('', '--metadata-value', dest='metadataValue', help='Value of the metadata key being added')

    def checkArgs(self):
        if self.getPropertyValueId() is None:
            raise InvalidRequest('Property value id must be provided.')
        if self.getMetadataKey() is None:
            if self.getMetadataDict() is None:
                raise InvalidRequest('Metadata key and value or metadataDict must be provided')
        else:
            if self.getMetadataValue() is None:
                raise InvalidRequest('Metadata value must be provided')

    def getPropertyValueId(self):
        return self.options.propertyValueId

    def getMetadataDict(self):
        return self.options.metadataDict

    def getMetadataKey(self):
        return self.options.metadataKey

    def getMetadataValue(self):
        return self.options.metadataValue

    def runCommand(self):
        self.parseArgs(usage="""
    cdb-add-property-metadata-to-property-value --property-value-id=PROPERTYVALUEID
        --metadata-dict=METADATADICT
        --metadata-key=METADATAKEY (Alternatively provide metadata dict)
        --metadata-value=METADATAVALUE (Alternatively provide metadata dict)

Description:
    Adds a property metadata to a property value
        """)
        self.checkArgs()
        api = PropertyRestApi(self.getUsername(), self.getPassword(), self.getServiceHost(), self.getServicePort(), self.getServiceProtocol())

        propertyMetadata = api.addPropertyMetadataToPropertyValue(self.getPropertyValueId(), self.getMetadataKey(), self.getMetadataValue(), self.getMetadataDict())

        if isinstance(propertyMetadata, list):
            for metadata in propertyMetadata:
                print metadata.getDisplayString(self.getDisplayKeys(), self.getDisplayFormat())
        else:
            print propertyMetadata.getDisplayString(self.getDisplayKeys(), self.getDisplayFormat())

#######################################################################
# Run command.
def runCommand():
    cli = addPropertyMetadataToPropertyValueCli()
    cli.run()

if __name__ == '__main__':
    runCommand()
