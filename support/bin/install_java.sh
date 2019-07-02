#!/bin/bash

# Copyright (c) UChicago Argonne, LLC. All rights reserved.
# See LICENSE file.

JAVA_VERSION=11.0.3
SHA=37f5e150db5247ab9333b11c1dddcd30
BREF=12
CMS_HOST_ARCH=`uname | tr [A-Z] [a-z]`-`uname -m`

currentDir=`pwd`
cd `dirname $0`/.. && topDir=`pwd`

srcDir=$topDir/src
buildDir=$topDir/build
javaInstallDir=$topDir/java/$CMS_HOST_ARCH
javaFileName=jdk-$JAVA_VERSION-linux-x64.tar.gz

DOWNLOAD_URL="https://download.oracle.com/otn/java/jdk/$JAVA_VERSION+$BREF/$SHA/$javaFileName"

mkdir -p $srcDir
cd $srcDir
if [ ! -f $javaFileName ]; then
    echo "Please Follow the $javaFileName in your webrowser and when download begins copy the download url from the browser."
    echo "https://www.oracle.com/technetwork/java/javase/downloads/jdk11-downloads-5066655.html"
    read -p 'Resulting URL with Auth complete: ' NEW_DOWNLOAD_URL

    echo $NEW_DOWNLOAD_URL

    if [ ! -z $NEW_DOWNLOAD_URL ]; then
        echo 'License Accepted'
        echo "Retrieving $NEW_DOWNLOAD_URL"
        curl -o $javaFileName $NEW_DOWNLOAD_URL -L -b "oraclelicense=accept-securebackup-cookie"
        ls
        #mv $javaFileName* $javaFileName
    else
        echo 'Cannot continue with the Java installation, Download URL needs to be provided.'
        exit 1
    fi
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
