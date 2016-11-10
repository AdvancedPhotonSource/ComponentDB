#!/bin/bash

# Copyright (c) UChicago Argonne, LLC. All rights reserved.
# See LICENSE file.


PYTHON_VERSION=2.7.9
CDB_HOST_ARCH=`uname | tr [A-Z] [a-z]`-`uname -m`

currentDir=`pwd`
cd `dirname $0`/.. && topDir=`pwd`

srcDir=$topDir/src
buildDir=$topDir/build
pythonBuildDir=$buildDir/Python-$PYTHON_VERSION
pythonInstallDir=$topDir/python/$CDB_HOST_ARCH
pythonInstallFileName=Python-$PYTHON_VERSION.tgz

DOWNLOAD_URL=https://www.python.org/ftp/python/$PYTHON_VERSION/$pythonInstallFileName

mkdir -p $srcDir
cd $srcDir
if [ ! -f $pythonInstallFileName ]; then
    echo "Retrieving $DOWNLOAD_URL"
    curl -L -O $DOWNLOAD_URL
fi

if [ -f $pythonInstallFileName ]; then
    mkdir -p $buildDir

    # Build python.
    export PATH=$pythonInstallDir/bin:$PATH
    export PATH=$pythonInstallDir/lib:$PATH
    export LD_LIBRARY_PATH=$pythonInstallDir/lib:$LD_LIBRARY_PATH
    cd $buildDir
    echo Building python $PYTHON_VERSION
    tar zxf $srcDir/$pythonInstallFileName
    cd $pythonBuildDir
    ./configure --prefix=$pythonInstallDir || exit 1
    make || exit 1
    make install || exit 1
else
    echo "File $srcDir/$pythonInstallFileName not found."
    exit 1
fi
