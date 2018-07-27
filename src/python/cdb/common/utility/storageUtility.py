#!/usr/bin/env python

"""
Copyright (c) UChicago Argonne, LLC. All rights reserved.
See LICENSE file.
"""

import shutil
import uuid

from cdb.common.utility.cdbSubprocess import CdbSubprocess
from cdb.common.utility.configurationManager import ConfigurationManager
from cdb.common.utility.loggingManager import LoggingManager


class StorageUtility:
    """
    Singleton utility for saving data streams into the storage directories of CDB.
    """

    CONFIG_SECTION_NAME = 'Storage'
    CONFIG_OPTION_NAME_LIST = ['storageDirectory'
                               , 'logAttachmentPath'
                               , 'cdbInstallationDirectory']

    GALLERY_UPDATE_SCRIPT_PATH = 'sbin/cdb_attachment_gallery_update.sh'

    # Singleton
    __instance = None

    @classmethod
    def getInstance(cls):
        from cdb.common.utility.storageUtility import StorageUtility
        try:
            attachmentUtility = StorageUtility()
        except StorageUtility as utility:
            attachmentUtility = utility
        return attachmentUtility

    def __init__(self):
        if StorageUtility.__instance:
            raise StorageUtility.__instance
            StorageUtility.__instance = self
        self.logger = LoggingManager.getInstance().getLogger(self.__class__.__name__)

        cm = ConfigurationManager.getInstance()
        cm.setOptionsFromConfigFile(self.CONFIG_SECTION_NAME, self.CONFIG_OPTION_NAME_LIST)

        self.logAttachmentStoragePath = cm.getLogAttachmentPath()
        self.propertyImageStoragePath = cm.getPropertyImagePath()
        self.storageDirectory = cm.getStorageDirectory()
        self.installDirectory = cm.getCdbInstallationDirectory()
        self.galleryUpdateScript = '%s/%s' % (self.installDirectory, self.GALLERY_UPDATE_SCRIPT_PATH)

    def storeLogAttachment(self, data, fileName):
        """
        Stores a data stream in the log attachment directory

        :param data: data stream
        :param fileName: original name of the file
        :return: generated unique file name
        """
        logAttachmentsDirectory = "%s/%s" % (self.storageDirectory, self.logAttachmentStoragePath)

        uniqueFileName = self.__getUniqueFileName(fileName)
        logAttachmentDestination = '%s/%s' % (logAttachmentsDirectory, uniqueFileName)

        self.logger.debug("Saving log attachment (%s) to: %s" % (fileName, logAttachmentDestination))

        self.__storeFile(logAttachmentDestination, data)

        return uniqueFileName

    def storePropertyImage(self, data, fileName):
        """
        Stores data stream with image into the CDB
        :param data:
        :param fileName
        :return:
        """

        propertyImageDirectory = "%s/%s" % (self.storageDirectory, self.propertyImageStoragePath)

        uniqueName = self.__getUniqueFileName(fileName)
        propertyFileName = "%s.%s" % ('image', uniqueName)
        uniqueName = "%s.%s" % (propertyFileName, 'original')
        propertyImageDestination = '%s/%s' % (propertyImageDirectory, uniqueName)


        self.logger.debug("Saving property image to (%s) to: %s" % (fileName, propertyImageDestination))
        self.__storeFile(propertyImageDestination, data)

        # Generate previews
        galleryUpdateCommand = '%s %s' % (self.galleryUpdateScript, propertyImageDestination)
        galleryUpdateProcess = CdbSubprocess(galleryUpdateCommand)
        galleryUpdateProcess.run()

        return propertyFileName

    def __storeFile(self, destination, data):
        with open(destination, 'wb') as destf:
            if type(data) == str:
                destf.write(data)
            else:
                shutil.copyfileobj(data, destf)

    def __getUniqueFileName(self, fileName):
        return '%s%s' % (str(uuid.uuid4()), self.__getFileExtension(fileName))

    def __getFileExtension(self, filename):
        filenameSplit = filename.split('.')

        ext = ''
        for i in range(1, len(filenameSplit)):
            extToUse = filenameSplit[i]
            if extToUse.__len__() > 5:
                continue
            ext += '.%s' % filenameSplit[i]

        return ext
