#!/usr/bin/env python
#
# CDB attachment status and cleanup script
#
# Usage:
#
# $0 [CDB_DB_NAME]
#

####################################################################

import os
import sys

from cdb.common.utility.configurationManager import ConfigurationManager

cdbRootDir = os.environ.get('CDB_ROOT_DIR')

if (cdbRootDir is None):
    directory = os.path.dirname(__file__)
    fullPath = os.path.abspath(directory + "/..")
    print >> sys.stderr, "Environment not loaded. Please run `source %s/setup.sh` before running this script." %(fullPath)
    exit(1)

if len(sys.argv) < 2:
    print 'This script has not been provided a deployment name. It will run with no configuration (env based developer defaults)'
    print 'please rerun script with the deployment name (CDB_DB_NAME)'
    print 'Usage:'
    print  "$0 [CDB_DB_NAME]"
    response = raw_input("Would you like to continue with developer defaults? [y/N]: ")
    if response.lower() != 'y':
        exit(0)
    else:
        dataDirectory = os.environ['CDB_DATA_DIR']
else:
    databaseName = sys.argv[1]
    cdbInstallDir = os.environ['CDB_INSTALL_DIR']
    cdbWebServiceConfigFile = '%s/etc/%s.cdb-web-service.conf' % (cdbInstallDir, databaseName)
    if os.path.isfile(cdbWebServiceConfigFile):
        os.environ['CDB_CONFIG_FILE'] = cdbWebServiceConfigFile
    else:
        print >> sys.stderr, "Configuration file does not exist: %s." %(cdbWebServiceConfigFile)
        exit(1)

    deploymentConfiguration = open("%s/etc/%s.deploy.conf" % (cdbRootDir, databaseName)).read().split('\n')
    deploymentConfigurationDictionary = {}
    for config in deploymentConfiguration:
        configSplit = config.split('=')
        if configSplit.__len__() == 2:
            deploymentConfigurationDictionary[configSplit[0]] = configSplit[1]
    dataDirectory = deploymentConfigurationDictionary['CDB_DATA_DIR']


dataDirectory = os.environ['CDB_DATA_DIR']
from cdb.common.db.api.propertyDbApi import PropertyDbApi
from cdb.common.db.api.logDbApi import LogDbApi
import re

TOTAL_HEADER_SIZE = 80

propertyApi = PropertyDbApi()
logApi = LogDbApi()

cm = ConfigurationManager.getInstance()
print "DB Name: " + cm.getDbSchema()
print "DB User: " + cm.getDbUser()


def appendNonEmptyValue(array, value):
    if value is None or value == "":
        return
    array.append(value)

def getPropertyValueListByPropertyTypeHandler(handlerName):
    propertyTypeHandler = propertyApi.getPropertyHandlerIdByPropertyHandlerName(handlerName)
    if propertyTypeHandler is not None:
        handlerPropertyTypes = propertyApi.getPropertyTypesByHandlerId(propertyTypeHandler.data['id'])
    else:
        return

    propertyValues = []
    for propertyType in handlerPropertyTypes:
        propertyTypeValues = propertyApi.getPropertyValuesByPropertyTypeId(propertyType.data['id'])
        propertyValues = propertyValues + propertyTypeValues

    attachements = []
    for propertyValue in propertyValues:
        appendNonEmptyValue(attachements,propertyValue.data['value'])
        for propertyValueHistory in propertyValue.data['propertyValueHistory']:
            appendNonEmptyValue(attachements, propertyValueHistory.value)

    return attachements

# Load property value Attachments
imageDBAttachments = getPropertyValueListByPropertyTypeHandler('Image')
documentDBAttachments = getPropertyValueListByPropertyTypeHandler('Document')

# Load Log Attachments
attachments = logApi.getAttachments()
allDBLogAttachments = []
for attachment in attachments:
    allDBLogAttachments.append(attachment.data['name'])

usedDBLogAttachments = []
unusedDBLogAttachments = allDBLogAttachments[:]
logAttachments = logApi.getLogAttachments()
for logAttachment in logAttachments:
    attachmentName = logAttachment.data['attachment'].data['name']
    usedDBLogAttachments.append(attachmentName)
    if attachmentName in unusedDBLogAttachments:
        unusedDBLogAttachments.remove(attachmentName)

documentPropertyValueDataDirectory = dataDirectory + "/propertyValue/documents"
imagePropertyValueDataDirectory = dataDirectory + "/propertyValue/images"
logAttachmentsDataDirectory = dataDirectory + "/log/attachments"

documentDirectoryAttachments = os.listdir(documentPropertyValueDataDirectory)
rawImageDirectoryAttachments = os.listdir(imagePropertyValueDataDirectory)
logDirectoryAttachments = os.listdir(logAttachmentsDataDirectory)

# Clean up the image directory attachments list since it includes various versions of the attachment.
imageDirectoryAttachments = []
for imageAttachment in rawImageDirectoryAttachments:
    imageNameParts = imageAttachment.split('.')
    imageAttachmentName = "%s.%s.%s" % (imageNameParts[0],imageNameParts[1],imageNameParts[2])
    if imageAttachmentName not in imageDirectoryAttachments:
        imageDirectoryAttachments.append(imageAttachmentName)

def findAttachmentsOnlyPresentInFirstList(firstAttachmentList, secondAttachmentList):
    attachmentsFound = firstAttachmentList[:]
    for attachment in secondAttachmentList:
        if attachment in attachmentsFound:
            attachmentsFound.remove(attachment)

    return attachmentsFound

def printHeader(header):
    headerFillerCount = ((TOTAL_HEADER_SIZE - header.__len__()) / 2) - 1
    firstHeaderFiller = '*' * headerFillerCount
    secondHeaderFiller = firstHeaderFiller
    if (header.__len__() % 2) != 0:
        secondHeaderFiller = secondHeaderFiller + '*'
    print '*' * TOTAL_HEADER_SIZE
    print "%s %s %s" % (firstHeaderFiller, header, secondHeaderFiller)
    print '*' * TOTAL_HEADER_SIZE

def resultPrint(header, dataList, printList):
    print ''
    printHeader(header)
    print "* Total: " + str(dataList.__len__())
    if printList:
        for item in dataList:
            print '*** ' + item
    print '*' * TOTAL_HEADER_SIZE


def promptRemovalOfDirectoryFilesIfAny(attachmentList, dataDirectory, listDescription, matchSearch = False):
    filesRemoved = 0
    if attachmentList.__len__() > 0:
        userResponse = raw_input("Would You like to remove %s? [y/N]: " % listDescription)
        if userResponse.lower() == 'y':
            removedBytes = 0
            if matchSearch:
                for directoryFile in os.listdir(dataDirectory):
                    for attachmentListFile in attachmentList:
                        if re.search(attachmentListFile+"*", directoryFile):
                            filePath = dataDirectory + "/" + directoryFile
                            fileSize = os.path.getsize(filePath)
                            removedBytes += fileSize
                            print " Removing: %s (size: %s bytes) " % (filePath, fileSize)
                            os.remove(filePath)
                            filesRemoved += 1
            else:
                for attachmentListFile in attachmentList:
                    filePath = dataDirectory + "/" + attachmentListFile
                    fileSize = os.path.getsize(filePath)
                    removedBytes +=  fileSize
                    print " Removing: %s (size: %s bytes) " % (filePath, fileSize)
                    os.remove(filePath)
                    filesRemoved += 1
            print " -- Total Bytes Removed: %s bytes" % removedBytes
    return filesRemoved

unusedDirectoryImageAttachments = findAttachmentsOnlyPresentInFirstList(imageDirectoryAttachments, imageDBAttachments)
missingDirectoryImageAttachments = findAttachmentsOnlyPresentInFirstList(imageDBAttachments, imageDirectoryAttachments)
unusedDirectoryDocumentAttachments = findAttachmentsOnlyPresentInFirstList(documentDirectoryAttachments, documentDBAttachments)
missingDirectoryDocumentAttachments = findAttachmentsOnlyPresentInFirstList(documentDBAttachments, documentDirectoryAttachments)
unusedDirectoryLogAttachments = findAttachmentsOnlyPresentInFirstList(logDirectoryAttachments, allDBLogAttachments)
missingDirectoryLogAttachments = findAttachmentsOnlyPresentInFirstList(allDBLogAttachments, logDirectoryAttachments)

resultPrint("Unused Image Attachments Present in the directory (Not present in the DB)", unusedDirectoryImageAttachments, True)
imageFilesRemoved = promptRemovalOfDirectoryFilesIfAny(unusedDirectoryImageAttachments, imagePropertyValueDataDirectory, "unused image attachment(s)", matchSearch=True)
resultPrint("Missing Image Attachments (Not present in the directory)", missingDirectoryImageAttachments, True)
resultPrint("Unused Document Attachments Present in the directory (Not present in the DB)", unusedDirectoryDocumentAttachments, True)
documentFilesRemoved = promptRemovalOfDirectoryFilesIfAny(unusedDirectoryDocumentAttachments, documentPropertyValueDataDirectory, "unused document attachment(s)")
resultPrint("Missing Document Attachments (Not present in the directory)", missingDirectoryDocumentAttachments, True)
resultPrint("Unused Log Attachments Present in the directory (Not present in the DB)", unusedDirectoryLogAttachments, True)
logAttachmentsRemoved = promptRemovalOfDirectoryFilesIfAny(unusedDirectoryLogAttachments, logAttachmentsDataDirectory, 'unused log attachment(s)')
resultPrint("Missing Log Attachments (Not present in the directory)", missingDirectoryLogAttachments, True)


print ''

printHeader('Summary Information')
summaryPrintItem = '********** %-50s %-7s **********'
print summaryPrintItem % ('Total DB Image Property Attachments: ', str(imageDBAttachments.__len__()))
print summaryPrintItem % ('Total DB Document Property Attachments: ', str(documentDBAttachments.__len__()))
print summaryPrintItem % ('Total DB Property Attachments: ', str(imageDBAttachments.__len__() + documentDBAttachments.__len__()))
print summaryPrintItem % ('Total DB Log Attachments: ', str(allDBLogAttachments.__len__()))
print summaryPrintItem % ('Total DB Linked Log Attachments: ', str(usedDBLogAttachments.__len__()))
print summaryPrintItem % ('Total DB unused Log Attachments: ', str(unusedDBLogAttachments.__len__()))
print summaryPrintItem % ('Total DB Attachments: ', str(allDBLogAttachments.__len__() + imageDBAttachments.__len__() + documentDBAttachments.__len__()))
print summaryPrintItem % ('Total Missing Image Files: ', str(missingDirectoryImageAttachments.__len__()))
print summaryPrintItem % ('Total Missing Document Files: ', str(missingDirectoryDocumentAttachments.__len__()))
print summaryPrintItem % ('Total Missing Log Files: ', str(missingDirectoryLogAttachments.__len__()))
print summaryPrintItem % ('Total Unused Image Files: ', str(unusedDirectoryImageAttachments.__len__()))
print summaryPrintItem % ('Total Unused Document Files: ', str(unusedDirectoryDocumentAttachments.__len__()))
print summaryPrintItem % ('Total Unused Log Files: ', str(unusedDirectoryLogAttachments.__len__()))
print summaryPrintItem % ('Total Image Files Removed: ', str(imageFilesRemoved))
print summaryPrintItem % ('Total Document Files Removed: ', str(documentFilesRemoved))
print summaryPrintItem % ('Total Log Files Removed: ', str(logAttachmentsRemoved))
print summaryPrintItem % ('Total Files Removed: ', str(logAttachmentsRemoved + documentFilesRemoved + imageFilesRemoved))
print '*' * TOTAL_HEADER_SIZE
print '*' * TOTAL_HEADER_SIZE
print '*' * TOTAL_HEADER_SIZE