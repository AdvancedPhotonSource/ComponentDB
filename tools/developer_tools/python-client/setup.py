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
      version='3.12.3.dev0',
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
      scripts=[
          'cdbCli/service/cli/CDBcli.py',
          'cdbCli/service/cli/addLogToItemByIdCli.py', 
          'cdbCli/service/cli/getItemByIdCli.py'
          ],
      entry_points={
        'console_scripts': [
          'cdb-python-client-test = CdbApiFactory:run_command',
          'cdb-get-item-by-id = cdbCli.service.cli.getItemByIdCli:get_item_by_id',
          'cdb-add-log-to-item-by-id = cdbCli.service.cli.addLogToItemByIdCli:add_log_to_item_by_id',
          'cdb-cli-tool = cdbCli.service.cli.CDBcli:main'
        ]
      })
