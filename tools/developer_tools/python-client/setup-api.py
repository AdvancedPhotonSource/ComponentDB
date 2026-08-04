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

setup(
    name="ComponentDB_API",
    version="3.17.0",
    packages=["cdbApi", "cdbApi.api", "cdbApi.models"],
    py_modules=["CdbApiFactory"],
    install_requires=[
        "urllib3 >= 2.6.3, < 3.0.0",
        "python-dateutil >= 2.8.2",
        "pydantic >= 2.11",
        "typing-extensions >= 4.7.1",
        "certifi",
    ],
    license="Copyright (c) UChicago Argonne, LLC. All rights reserved.",
    description="Python APIs used to communicate with java hosted ComponentDB API.",
    maintainer="Dariusz Jarosz",
    maintainer_email="djarosz@aps.anl.gov",
    url="https://github.com/AdvancedPhotonSource/ComponentDB",
    entry_points={
        "console_scripts": ["cdb-python-client-test = CdbApiFactory:run_command"]
    },
)
