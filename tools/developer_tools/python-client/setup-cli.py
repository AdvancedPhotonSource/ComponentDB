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
      version='3.13.4.dev3',
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
                        'ComponentDB-API==3.13.0'],
      license='Copyright (c) UChicago Argonne, LLC. All rights reserved.',
      description='Python APIs used to communicate with java hosted ComponentDB API.',
      maintainer='Dariusz Jarosz',
      maintainer_email='djarosz@aps.anl.gov',
      url='https://github.com/AdvancedPhotonSource/ComponentDB',      
      entry_points={
        'console_scripts': [          
          'cdb-cli = cdbCli.service.cli.cli:main',
        ]
      })
