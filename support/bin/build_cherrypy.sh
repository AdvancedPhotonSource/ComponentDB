#!/bin/bash

# Copyright (c) UChicago Argonne, LLC. All rights reserved.
# See LICENSE file.


# Need to update pypi Download URL manually upon version update
CHERRY_PY_VERSION=8.1.2
CDB_HOST_ARCH=`uname | tr [A-Z] [a-z]`-`uname -m`

currentDir=`pwd`
cd `dirname $0`/.. && topDir=`pwd`

srcDir=$topDir/src
buildDir=$topDir/build
cherryPyBuildDir=$buildDir/CherryPy-$CHERRY_PY_VERSION
pythonInstallDir=$topDir/python/$CDB_HOST_ARCH
cherryPyInstallFileName=CherryPy-$CHERRY_PY_VERSION.tar.gz


PERM_DOWNLOAD_URL=https://pypi.python.org/packages/d9/ec/a9cd68e57af9fead92158028d32cc79f1908fd81f486d8a3826e9847ada6/CherryPy-8.1.2.tar.gz

mkdir -p $srcDir
cd $srcDir
if [ ! -f $cherryPyInstallFileName ]; then
    echo "Retrieving $PERM_DOWNLOAD_URL"
    curl -L -O $PERM_DOWNLOAD_URL
fi

if [ -f $cherryPyInstallFileName ]; then
    mkdir -p $buildDir

    # Build python.
    export PATH=$pythonInstallDir/bin:$PATH
    export PATH=$pythonInstallDir/lib:$PATH
    export LD_LIBRARY_PATH=$pythonInstallDir/lib:$LD_LIBRARY_PATH
    cd $buildDir
    echo Building CherryPy $CHERRY_PY_VERSION
    tar zxf $srcDir/$cherryPyInstallFileName
    cd $cherryPyBuildDir
    python setup.py build || exit 1
    python setup.py install || exit 1
    cd $topDir
else
    echo "File $srcDir/$cherryPyInstallFileName was not found"
    exit 1
fi
