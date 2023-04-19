#!/usr/bin/env python

"""
Copyright (c) UChicago Argonne, LLC. All rights reserved.
See LICENSE file.
"""

"""
DEV NOTE: To publish API
# Update version in this file
python3 setup.py sdist
twine upload dist/(specific version file)
"""

from setuptools import setup
from setuptools import find_packages

setup(name='ComponentDB-CLI',
      version='3.15.5',
      packages=['cdbCli',
                'cdbCli.common',
                'cdbCli.common.cli',
                'cdbCli.common.utility',
                'cdbCli.service',
                'cdbCli.service.cli',
                'cdbCli.service.cli.cdbCliCmnds',],
      install_requires=['python-dateutil', 
                        'urllib3',
                        'six',
                        'paho-mqtt',
                        'click',
                        'pandas',
                        'rich',
                        'InquirerPy',
                        'ComponentDB-API==3.15.5'],
      license='Copyright (c) UChicago Argonne, LLC. All rights reserved.',
      description='Python APIs used to communicate with java hosted ComponentDB API.',
      maintainer='Dariusz Jarosz',
      maintainer_email='djarosz@aps.anl.gov',
      url='https://github.com/AdvancedPhotonSource/ComponentDB',      
      entry_points={
        'console_scripts': [          
          'cdb-cli = cdbCli.service.cli.cli:main',
          'cdbSearch = cdbCli.service.cli.cdbCliCmnds.search:cdb_search',
          'cdbInfo = cdbCli.service.cli.cdbCliCmnds.info:cdb_info',
          'cdbHelp = cdbCli.service.cli.cdbCliCmnds.help:showHelp'
        ]
      })
