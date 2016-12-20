#!/usr/bin/env python

"""
Copyright (c) UChicago Argonne, LLC. All rights reserved.
See LICENSE file.
"""

import shutil
import uuid

from cdb.common.exceptions.commandFailed import CommandFailed
from cdb.common.utility.configurationManager import ConfigurationManager
from cdb.common.utility.loggingManager import LoggingManager


class StorageUtility:
    """
    Singleton utility for saving data streams into the storage directories of CDB.
    """

    CONFIG_SECTION_NAME = 'Storage'
    CONFIG_OPTION_NAME_LIST = ['storageDirectory'
                               , 'logAttachmentPath']

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
        self.storageDirectory = cm.getStorageDirectory()

    def storeLogAttachment(self, data, fileName):
        """
        Stores a data stream in the log attachment directory

        :param data: data stream
        :param fileName: original name of the file
        :return: generated unique file name
        """
        logAttachmentsDirectory = "%s/%s" % (self.storageDirectory, self.logAttachmentStoragePath)

        uniqueFileName = '%s%s' % (str(uuid.uuid4()), self.__getFileExtension(fileName))
        logAttachmentDestination = '%s/%s' % (logAttachmentsDirectory, uniqueFileName)

        self.logger.debug("Saving log attachment (%s) to: %s" % (fileName, logAttachmentDestination))

        with open(logAttachmentDestination, 'wb') as destf:
            shutil.copyfileobj(data, destf)

        return uniqueFileName

    def __getFileExtension(self, filename):
        filenameSplit = filename.split('.')

        ext = ''
        for i in range(1, len(filenameSplit)):
            extToUse = filenameSplit[i]
            if extToUse.__len__() > 5:
                continue
            ext += '.%s' % filenameSplit[i]

        return ext
