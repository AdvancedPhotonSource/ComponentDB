#!/usr/bin/env python

"""
Copyright (c) UChicago Argonne, LLC. All rights reserved.
See LICENSE file.
"""


#
# Log route descriptor.
#

from cdb.cdb_web_service.service.logSessionController import LogSessionController
from cdb.common.utility.configurationManager import ConfigurationManager


class LogRouteDescriptor:
    @classmethod
    def getRoutes(cls):
        contextRoot = ConfigurationManager.getInstance().getContextRoot()

        # Static instances shared between different routes
        logSessionController = LogSessionController()

        # Define routes.
        routes = [

            # Add log Attachment
            {
                'name': 'addAttachmentToLogEntry',
                'path': '%s/logs/:(logId)/addAttachment' % contextRoot,
                'controller': logSessionController,
                'action': 'addLogAttachment',
                'method': ['POST']
            },
            {
                'name': 'updateLogEntry',
                'path': '%s/logs/:(logId)/update' % contextRoot,
                'controller': logSessionController,
                'action': 'updateLogEntry',
                'method': ['PUT']
            },

            {
                'name': 'deleteLogEntry',
                'path': '%s/logs/:(logId)/delete' % contextRoot,
                'controller': logSessionController,
                'action': 'deleteLogEntry',
                'method': ['DELETE']
            }

        ]

        return routes
