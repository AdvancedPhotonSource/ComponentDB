#!/usr/bin/env python

"""
Copyright (c) UChicago Argonne, LLC. All rights reserved.
See LICENSE file.
"""
import smtplib
from email.mime.text import MIMEText
from email.mime.multipart import MIMEMultipart

from cdb.common.utility.loggingManager import LoggingManager
from cdb.common.utility.configurationManager import ConfigurationManager
from cdb.common.exceptions.configurationError import ConfigurationError


class CdbPortalUrlUtility:
    """
    Singleton Utility is used for generating urls to CDB web portal.
    """

    CONFIG_SECTION_NAME = 'WebPortal'
    CONFIG_OPTION_NAME_LIST = ['portalWebAddress']

    PORTAL_ITEM_PATH = 'views/item'
    PORTAL_ITEM_VIEW_PATH = '%s/view' % PORTAL_ITEM_PATH
    PORTAL_ITEM_ID_QUERY_STRING = 'id=%s'

    # Singleton
    __instance = None

    @classmethod
    def getInstance(cls):
        from cdb.common.utility.cdbPortalUrlUtility import CdbPortalUrlUtility
        try:
            cdbUrlUtility = CdbPortalUrlUtility()
        except CdbPortalUrlUtility as utility:
            cdbUrlUtility = utility
        return cdbUrlUtility

    def __init__(self):
        if CdbPortalUrlUtility.__instance:
            raise CdbPortalUrlUtility.__instance
        CdbPortalUrlUtility.__instance = self
        self.logger = LoggingManager.getInstance().getLogger(self.__class__.__name__)

        cm = ConfigurationManager.getInstance()
        cm.setOptionsFromConfigFile(CdbPortalUrlUtility.CONFIG_SECTION_NAME, CdbPortalUrlUtility.CONFIG_OPTION_NAME_LIST)

        self.portalWebAddress = cm.getPortalWebAddress()

    def getItemUrlAddress(self, itemId):
        queryString = self.PORTAL_ITEM_ID_QUERY_STRING % itemId
        return '%s/%s?%s' % (self.portalWebAddress, self.PORTAL_ITEM_VIEW_PATH, queryString)


if __name__ == '__main__':
    urlUtility = CdbPortalUrlUtility.getInstance()
    print urlUtility.getItemUrlAddress(653)
