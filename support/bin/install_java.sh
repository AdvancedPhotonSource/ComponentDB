#!/bin/bash

# Copyright (c) UChicago Argonne, LLC. All rights reserved.
# See LICENSE file.

JAVA_VERSION=11+28
JDK_VERSION=jdk11

CMS_HOST_ARCH=$(uname -sm | tr -s '[:upper:][:blank:]' '[:lower:][\-]')

currentDir=`pwd`
cd `dirname $0`/.. && topDir=`pwd`

srcDir=$topDir/src
buildDir=$topDir/build
javaInstallDir=$topDir/java/$CMS_HOST_ARCH
javaFileName="openjdk-${JAVA_VERSION}_linux-x64_bin.tar.gz"

DOWNLOAD_URL="https://download.java.net/openjdk/$JDK_VERSION/ri/$javaFileName"

mkdir -p $srcDir
cd $srcDir
if [ ! -f $javaFileName ]; then    
    echo "Retrieving $DOWNLOAD_URL"
    curl -o $javaFileName $DOWNLOAD_URL
fi

if [ -f $javaFileName ]; then
    mkdir -p $buildDir

    # Unpack JDK and move it into place
    rm -rf $javaInstallDir

    cd $buildDir

    echo Installing JDK $JAVA_VERSION
    tar zxf $srcDir/$javaFileName
    mkdir -p `dirname $javaInstallDir`
    mv jdk* $javaInstallDir
else
    echo "Error: filename $srcDir/$$javaFileName does not exist"
fi
