#!/bin/bash

# Copyright (c) UChicago Argonne, LLC. All rights reserved.
# See LICENSE file.


ANT_VERSION=1.10.9

currentDir=`pwd`
cd `dirname $0`/.. && topDir=`pwd`

srcDir=$topDir/src
buildDir=$topDir/build
antInstallDir=$topDir/ant
antFileName=apache-ant-${ANT_VERSION}-bin.tar.gz
DOWNLOAD_URL=http://archive.apache.org/dist/ant/binaries/$antFileName

mkdir -p $srcDir
cd $srcDir
if [ ! -f $antFileName ]; then
    echo "Retrieving $DOWNLOAD_URL"
    curl -L -O $DOWNLOAD_URL
fi


if [ -f $antFileName ]; then
    mkdir -p $buildDir

    # Install ant
    rm -rf $antInstallDir
    cd $buildDir
    echo Installing ant $ANT_VERSION
    tar zxf $srcDir/$antFileName
    mv apache-ant* $antInstallDir
else
    echo "File: $srcDir/$antFileName was not found"
    exit 1
fi
