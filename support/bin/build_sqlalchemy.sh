#!/bin/bash

# Copyright (c) UChicago Argonne, LLC. All rights reserved.
# See LICENSE file.


# Need to update pypi Download URL manually upon version update
SQL_ALCHEMY_VERSION=0.9.8
CDB_HOST_ARCH=`uname | tr [A-Z] [a-z]`-`uname -m`

currentDir=`pwd`
cd `dirname $0`/.. && topDir=`pwd`

srcDir=$topDir/src
buildDir=$topDir/build
sqlAlchemyBuildDir=$buildDir/SQLAlchemy-$SQL_ALCHEMY_VERSION
pythonInstallDir=$topDir/python/$CDB_HOST_ARCH
sqlAlchemyInstallFileName=SQLAlchemy-$SQL_ALCHEMY_VERSION.tar.gz

PERM_DOWNLOAD_URL=https://pypi.python.org/packages/e2/21/34dd095ddd2d5a6b04d0d990e2a31a70400db6a3da2f045fc672d5943f0a/SQLAlchemy-0.9.8.tar.gz

mkdir -p $srcDir
cd $srcDir
if [ ! -f $sqlAlchemyInstallFileName ]; then
    echo "Retrieving $PERM_DOWNLOAD_URL"
    curl -L -O $PERM_DOWNLOAD_URL
fi

if [ -f $sqlAlchemyInstallFileName ]; then
    mkdir -p $buildDir

    # Build.
    export PATH=$pythonInstallDir/bin:$PATH
    export PATH=$pythonInstallDir/lib:$PATH
    export LD_LIBRARY_PATH=$pythonInstallDir/lib:$LD_LIBRARY_PATH
    cd $buildDir
    echo Building SQLAlchemy $SQL_ALCHEMY_VERSION
    tar zxf $srcDir/$sqlAlchemyInstallFileName
    cd $sqlAlchemyBuildDir
    python setup.py build || exit 1
    python setup.py install || exit 1
    cd $topDir
else
    echo "File $srcDir/$sqlAlchemyInstallFileName was not found."
    exit 1
fi
