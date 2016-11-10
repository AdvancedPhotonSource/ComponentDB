#!/bin/bash

# Copyright (c) UChicago Argonne, LLC. All rights reserved.
# See LICENSE file.


# Need to update pypi Download URL manually upon version update
SUDS_VERSION=0.4
CDB_HOST_ARCH=`uname | tr [A-Z] [a-z]`-`uname -m`

currentDir=`pwd`
cd `dirname $0`/.. && topDir=`pwd`

srcDir=$topDir/src
buildDir=$topDir/build
sudsBuildDir=$buildDir/suds-$SUDS_VERSION
pythonInstallDir=$topDir/python/$CDB_HOST_ARCH
sudsInstallFileName=suds-$SUDS_VERSION.tar.gz

PERM_DOWNLOAD_URL=https://pypi.python.org/packages/bc/d6/960acce47ee6f096345fe5a7d9be7708135fd1d0713571836f073efc7393/suds-0.4.tar.gz

mkdir -p $srcDir
cd $srcDir
if [ ! -f $sudsInstallFileName ]; then
    echo "Retrieving $PERM_DOWNLOAD_URL"
    curl -L -O $PERM_DOWNLOAD_URL
fi

if [ -f $sudsInstallFileName ]; then
    mkdir -p $buildDir

    # Build.
    export PATH=$pythonInstallDir/bin:$PATH
    export PATH=$pythonInstallDir/lib:$PATH
    export LD_LIBRARY_PATH=$pythonInstallDir/lib:$LD_LIBRARY_PATH
    cd $buildDir
    echo Building suds $SUDS_VERSION
    tar zxf $srcDir/$sudsInstallFileName
    cd $sudsBuildDir
    python setup.py build || exit 1
    python setup.py install || exit 1
    cd $topDir
else
    echo "File $srcDir/$sudsInstallFileName was not found"
    exit 1
fi
