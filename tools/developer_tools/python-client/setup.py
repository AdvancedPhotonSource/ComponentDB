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

setup(name='ComponentDB-API',
      version='3.10.1.dev2',
      packages=find_packages(),
      py_modules=["CdbApiFactory"],
      install_requires=['python-dateutil', 
          'urllib3',
          'six'],
      license='Copyright (c) UChicago Argonne, LLC. All rights reserved.',
      description='Python APIs used to communicate with java hosted ComponentDB API.',
      maintainer='Dariusz Jarosz',
      maintainer_email='djarosz@aps.anl.gov',
      url='https://github.com/AdvancedPhotonSource/ComponentDB',
      entry_points={
        'console_scripts': [
          'cdb-python-client-test = CdbApiFactory:run_command'
        ]
      })
