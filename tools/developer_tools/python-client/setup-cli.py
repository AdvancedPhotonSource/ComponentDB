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

setup(
    name="ComponentDB-CLI",
    version="3.17.0",
    packages=[
        "cdbCli",
        "cdbCli.common",
        "cdbCli.common.cli",
        "cdbCli.common.utility",
        "cdbCli.service",
        "cdbCli.service.cli",
        "cdbCli.service.cli.cdbCliCmnds",
    ],
    install_requires=[
        "python-dateutil >= 2.8.2",
        "urllib3 >= 2.6.3, < 3.0.0",
        "paho-mqtt",
        "click",
        "pandas",
        "rich",
        "ComponentDB-API==3.17.0",
    ],
    license="Copyright (c) UChicago Argonne, LLC. All rights reserved.",
    description="Python APIs used to communicate with java hosted ComponentDB API.",
    maintainer="Dariusz Jarosz",
    maintainer_email="djarosz@aps.anl.gov",
    url="https://github.com/AdvancedPhotonSource/ComponentDB",
    entry_points={
        "console_scripts": [
            "cdb-cli = cdbCli.service.cli.cli:main",
            "cdbSearch = cdbCli.service.cli.cdbCliCmnds.search:cdb_search",
            "cdbInfo = cdbCli.service.cli.cdbCliCmnds.info:cdb_info",
            "cdbHelp = cdbCli.service.cli.cdbCliCmnds.help:showHelp",
        ]
    },
)
