#!/bin/sh

# Copyright (c) UChicago Argonne, LLC. All rights reserved.
# See LICENSE file.


# Need to update pypi Download URL manually upon version update
ROUTES_VERSION=2.3.1
CDB_HOST_ARCH=`uname | tr [A-Z] [a-z]`-`uname -m`

currentDir=`pwd`
cd `dirname $0`/.. && topDir=`pwd`

srcDir=$topDir/src
buildDir=$topDir/build
routesBuildDir=$buildDir/Routes-$ROUTES_VERSION
pythonInstallDir=$topDir/python/$CDB_HOST_ARCH
routesInstallFileName=Routes-$ROUTES_VERSION.tar.gz

PERM_DOWNLOAD_URL=https://pypi.python.org/packages/96/da/6a61cdab81a76cfc6c5d160cbc11942d6135c19923a6a984969c506a8b82/Routes-2.3.1.tar.gz
mkdir -p $srcDir
cd $srcDir

if [ ! -f $routesInstallFileName ]; then
    echo "Retrieving $PERM_DOWNLOAD_URL"
    curl -L -O $PERM_DOWNLOAD_URL
fi

if [ -f $routesInstallFileName ]; then
    mkdir -p $buildDir

    # Build.
    export PATH=$pythonInstallDir/bin:$PATH
    export PATH=$pythonInstallDir/lib:$PATH
    export LD_LIBRARY_PATH=$pythonInstallDir/lib:$LD_LIBRARY_PATH
    cd $buildDir
    echo Building Routes $ROUTES_VERSION
    tar zxf $srcDir/$routesInstallFileName
    cd $routesBuildDir
    python setup.py build || exit 1
    python setup.py install || exit 1
    cd $topDir
else
    echo "File $srcDir/$routesInstallFileName was not found."
    exit 1
fi


