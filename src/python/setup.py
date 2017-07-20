#!/usr/bin/env python

"""
Copyright (c) UChicago Argonne, LLC. All rights reserved.
See LICENSE file.
"""

from setuptools import setup

setup(name='cdb-api',
      version='3.3.0.dev12',
      license='Copyright (c) UChicago Argonne, LLC. All rights reserved.',
      packages=['cdb',
                'cdb.cdb_web_service',
                'cdb.cdb_web_service.api',
                'cdb.cdb_web_service.cli',
                'cdb.common',
                'cdb.common.cli',
                'cdb.common.utility',
                'cdb.common.exceptions',
                'cdb.common.objects',
                'cdb.common.constants',
                'cdb.common.api',
                'cdb.common.client'],
      description='Python APIs used to communicate with Component Database',
      maintainer='Dariusz Jarosz',
      maintainer_email='djarosz@aps.anl.gov',
      url='https://github.com/AdvancedPhotonSource/ComponentDB',
      entry_points={'console_scripts': [
          'cdb-add-item-log-entry = cdb.cdb_web_service.cli.addItemLogEntryCli:runCommand',
          'cdb-add-item-property-value = cdb.cdb_web_service.cli.addItemPropertyValueCli:runCommand',
          'cdb-add-log-attachment = cdb.cdb_web_service.cli.addLogAttachmentCli:runCommand',
          'cdb-add-property-metadata-to-property-value = cdb.cdb_web_service.cli.addPropertyMetadataToPropertyValueCli:runCommand',
          'cdb-delete-log = cdb.cdb_web_service.cli.deleteLogCli:runCommand',
          'cdb-get-item-logs = cdb.cdb_web_service.cli.getItemLogsCli:runCommand',
          'cdb-get-user = cdb.cdb_web_service.cli.getUserCli:runCommand',
          'cdb-get-user-groups = cdb.cdb_web_service.cli.getUserGroupsCli:runCommand',
          'cdb-get-users = cdb.cdb_web_service.cli.getUsersCli:runCommand',
          'cdb-update-log = cdb.cdb_web_service.cli.updateLogCli:runCommand',
        ],
      },
      )
