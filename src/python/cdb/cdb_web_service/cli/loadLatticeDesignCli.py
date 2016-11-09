#!/usr/bin/env python

"""
Copyright (c) UChicago Argonne, LLC. All rights reserved.
See LICENSE file.
"""


import os
from cdbWebServiceCli import CdbWebServiceCli
from cdb.cdb_web_service.api.designRestApi import DesignRestApi
from cdb.common.exceptions.invalidRequest import InvalidRequest
from cdb.common.exceptions.invalidArgument import InvalidArgument

class LoadLatticeDesignCli(CdbWebServiceCli):
    def __init__(self):
        CdbWebServiceCli.__init__(self)
        self.addOption('', '--csv-file', dest='csvFile', help='CSV file containing lattice design (required).')
        self.addOption('', '--name', dest='name', help='Lattice design name (required).')
        self.addOption('', '--description', dest='description', help='Lattice design description.')
        self.addOption('', '--owner-user-id', dest='ownerUserId', help='Owner user id. If not provided, user who created design will own it.')
        self.addOption('', '--owner-group-id', dest='ownerGroupId', help='Owner user group id. If not provided, owner group will be set to the default group of the user who created design.')
        self.addOption('', '--is-group-writeable', dest='isGroupWriteable', default=False, help='Group writeable flag (default: False).')

    def checkArgs(self):
        if self.options.csvFile is None:
            raise InvalidRequest('Lattice design file in CSV format must be provided.')
        if self.options.name is None:
            raise InvalidRequest('Lattice design name must be provided.')

    def getCsvFile(self):
        return self.options.csvFile

    def getName(self):
        return self.options.name

    def getOwnerUserId(self):
        return self.options.ownerUserId

    def getOwnerGroupId(self):
        return self.options.ownerGroupId

    def getDescription(self):
        return self.options.description

    def getIsGroupWriteable(self):
        return self.options.isGroupWriteable

    def runCommand(self):
        self.parseArgs(usage="""
    cdb-load-lattice-design --csv-file=CSVFILE --name=NAME 
        [--description=DESCRIPTION]
        [--owner-user-id=OWNERUSERID]
        [--owner-group-id=OWNERGROUPID]
        [--is-group-writeable=ISGROUPWRITEABLE]

Description:
    Load lattice design from CSV file.
        """)
        self.checkArgs()
        api = DesignRestApi(self.getUsername(), self.getPassword(), self.getServiceHost(), self.getServicePort(), self.getServiceProtocol())
        designElementList = self.loadLatticeCsvFile(self.getCsvFile())
        design = api.loadDesign(self.getName(), self.getOwnerUserId(), self.getOwnerGroupId(), self.getIsGroupWriteable(), self.getDescription(), designElementList)
        print design.getDisplayString(self.getDisplayKeys(), self.getDisplayFormat())

    # Utility methojd to load csv file and prepare list of design
    # element dictionaries.
    @classmethod
    def loadLatticeCsvFile(cls, fileName):
        if fileName.find('.') < 0 or fileName.split('.')[-1].lower() != 'csv':
            raise InvalidArgument('Input file must be in CSV format.')
        if not os.path.exists(fileName):
            raise InvalidArgument('Input file %s does not exist.' % fileName)

        # These keys are design element attributes (not properties).
        # Any other keys are property names.
        replacementKeyMap = { 'ElementName' : 'name', 'ElementType' : 'componentName' }
        # Read headers
        csvFile = open(fileName, 'r')
        content = csvFile.read()
        if content.find('\r'):
            lineList = content.split('\r')
        elif content.find('\n'):
            lineList = content.split('\n')
        else:
            raise InvalidArgument('Input file %s cannot be parsed.' % fileName)
        headerList = lineList[0].split(',')

        index = 0
        headerMap = {}
        for h in headerList:
            key = h.strip()
            if key is not None:
                headerMap[key] = index
            index += 1
        designElementList = []
        nColumns = index
        sortOrder = 1.0
        for line in lineList[1:]:
            if not line:
                break
            valueList = line.split(',')
            if len(valueList) != nColumns:
                break

            designElementDict = { 'sortOrder' : sortOrder }
            for (key, index) in headerMap.items():
                if valueList[index] is None:
                    continue
                if replacementKeyMap.has_key(key):
                    # Design element attribute
                    designElementDict[replacementKeyMap.get(key)] = valueList[index]
                else:
                    # Design element property
                    value = '%s' % valueList[index] 
                    if len(value) > 0:
                        propertyList = designElementDict.get('propertyList', [])
                        property = { 'name' : key, 'value' : valueList[index] }
                        propertyList.append(property)
                        designElementDict['propertyList'] = propertyList
            designElementList.append(designElementDict)
            sortOrder += 1.0
        return designElementList

#######################################################################
# Run command.
if __name__ == '__main__':
    cli = LoadLatticeDesignCli()
    cli.run()

