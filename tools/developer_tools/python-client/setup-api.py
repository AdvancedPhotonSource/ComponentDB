#!/usr/bin/env python

"""
Copyright (c) UChicago Argonne, LLC. All rights reserved.
See LICENSE file.
"""

"""
DEV NOTE: To publish API
# Update version in this file
python3 setup-api.py sdist
twine upload dist/(specific version file)
"""

from setuptools import setup
from setuptools import find_packages

setup(name='ComponentDB-API',
      version='3.15.5',
      packages=["cdbApi",
                "cdbApi.api",
                "cdbApi.models"],
      py_modules=["CdbApiFactory"],
      install_requires=['python-dateutil', 
          'urllib3',
          'certifi',
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
