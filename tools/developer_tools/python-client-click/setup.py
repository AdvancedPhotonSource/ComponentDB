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
      version='1.0.0',
      packages=find_packages(),
      py_modules=["CdbApiFactory"],
      install_requires=['python-dateutil', 
                        'urllib3',
                        'six',
                        'paho-mqtt',
                        'click',
                        'ComponentDB-API'],
      license='Copyright (c) UChicago Argonne, LLC. All rights reserved.',
      description='Python APIs used to communicate with java hosted ComponentDB API.',
      maintainer='Dariusz Jarosz',
      maintainer_email='djarosz@aps.anl.gov',
      url='https://github.com/AdvancedPhotonSource/ComponentDB',
      scripts=[
          'cdbClick/service/cli/CDBclick.py',
          'cdbClick/service/cli/addLogToItemByIdCli.py', 
          'cdbClick/service/cli/getItemByIdCli.py',
          'cdbClick/service/cli/CDBclickCmnds/cdb_log_to_mqtt.py'
          ],
      entry_points={
        'console_scripts': [
          'click-python-client-test = CdbApiFactory:run_command',
          'click-get-item-by-id = cdbClick.service.cli.getItemByIdCli:get_item_by_id',
          'click-add-log-to-item-by-id = cdbClick.service.cli.addLogToItemByIdCli:add_log_to_item_by_id',
          'click-cli-tool = cdbClick.service.cli.CDBclick:main',
          'click-log-to-mqtt = cdbClick.service.cli.CDBclickCmnds.cdb_log_to_mqtt:cdb_log_to_mqtt'
        ]
      })
