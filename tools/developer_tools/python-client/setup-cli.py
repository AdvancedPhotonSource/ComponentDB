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
      version='3.13.2',
      packages=['cdbCli',
                'cdbCli.common',
                'cdbCli.common.cli',
                'cdbCli.common.utility',
                'cdbCli.service',
                'cdbCli.service.cli'],
      install_requires=['python-dateutil', 
                        'urllib3',
                        'six',
                        'paho-mqtt',
                        'click',
                        'ComponentDB-API==3.13.0'],
      license='Copyright (c) UChicago Argonne, LLC. All rights reserved.',
      description='Python APIs used to communicate with java hosted ComponentDB API.',
      maintainer='Dariusz Jarosz',
      maintainer_email='djarosz@aps.anl.gov',
      url='https://github.com/AdvancedPhotonSource/ComponentDB',
      scripts=[
          'cdbCli/service/cli/CDBclick.py'
          ],
      entry_points={
        'console_scripts': [
          'click-python-client-test = CdbApiFactory:run_command',
          'cdb-cli = cdbCli.service.cli.CDBclick:main',
        ]
      })
