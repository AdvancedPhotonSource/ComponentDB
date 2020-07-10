#!/bin/bash

# Copyright (c) UChicago Argonne, LLC. All rights reserved.
# See LICENSE file.


# Need to update pypi Download URL manually upon version update
MYSQL_PYTHON_VERSION=1.2.5
CDB_HOST_ARCH=$(uname -s | tr [:uppercase:] [:lowercase:])-$(uname -m)

currentDir=`pwd`
cd `dirname $0`/.. && topDir=`pwd`

srcDir=$topDir/src
buildDir=$topDir/build
mysqlPythonBuildDir=$buildDir/MySQL-python-$MYSQL_PYTHON_VERSION
pythonInstallDir=$topDir/python/$CDB_HOST_ARCH
pythonMysqlInstallFilename=MySQL-python-$MYSQL_PYTHON_VERSION.zip

PERM_DOWNLOAD_URL=https://pypi.python.org/packages/a5/e9/51b544da85a36a68debe7a7091f068d802fc515a3a202652828c73453cad/MySQL-python-1.2.5.zip

mkdir -p $srcDir
cd $srcDir
if [ ! -f $pythonMysqlInstallFilename ]; then
    echo "Retrieving $PERM_DOWNLOAD_URL"
    curl -L -O $PERM_DOWNLOAD_URL
fi

if [ -f $pythonMysqlInstallFilename ]; then
    mkdir -p $buildDir

    # Build.
    export PATH=$pythonInstallDir/bin:$PATH
    export PATH=$pythonInstallDir/lib:$PATH
    export LD_LIBRARY_PATH=$pythonInstallDir/lib:$LD_LIBRARY_PATH
    cd $buildDir
    echo Building MySQL-python $MYSQL_PYTHON_VERSION
    unzip -o $srcDir/$pythonMysqlInstallFilename
    cd $mysqlPythonBuildDir
    python setup.py build || exit 1
    python setup.py install || exit 1
    cd $topDir
else
    echo "File $srcDir/$pythonMysqlInstallFilename was not found."
    exit 1
fi
