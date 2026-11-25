#!/bin/bash

# Copyright (c) UChicago Argonne, LLC. All rights reserved.
# See LICENSE file.

currentDir=`pwd`
cd `dirname $0`/.. && topDir=`pwd`
. $topDir/bin/versions.sh

srcDir=$topDir/src
buildDir=$topDir/build
antInstallDir=$topDir/ant
antFileName=apache-ant-${ANT_VERSION}-bin.tar.gz
DOWNLOAD_URL=https://archive.apache.org/dist/ant/binaries/$antFileName

mkdir -p $srcDir
cd $srcDir
if [ ! -f $antFileName ]; then
    echo "Retrieving $DOWNLOAD_URL"
    curl -f -L -O $DOWNLOAD_URL || { rm -f $antFileName; exit 1; }
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
