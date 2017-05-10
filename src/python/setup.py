#!/usr/bin/env python

"""
Copyright (c) UChicago Argonne, LLC. All rights reserved.
See LICENSE file.
"""

from setuptools import setup

setup(name='cdb-api',
      version='3.3.0.dev3',
      license='Copyright (c) UChicago Argonne, LLC. All rights reserved.',
      packages=['cdb',
                'cdb.cdb_web_service',
                'cdb.cdb_web_service.api',
                'cdb.common',
                'cdb.common.utility',
                'cdb.common.exceptions',
                'cdb.common.objects',
                'cdb.common.constants',
                'cdb.common.api',
                'cdb.common.client'],
      description='Python APIs used to communicate with Component Database',
      maintainer='Dariusz Jarosz',
      maintainer_email='djarosz@aps.anl.gov',
      url='https://github.com/AdvancedPhotonSource/ComponentDB'
      )
