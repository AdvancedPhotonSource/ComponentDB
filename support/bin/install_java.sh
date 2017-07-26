#!/bin/bash

# Copyright (c) UChicago Argonne, LLC. All rights reserved.
# See LICENSE file.

JAVA_VERSION=8u141
SHA=336fa29ff2bb4ef291e347e091f7f4a7
BREF=15
CMS_HOST_ARCH=`uname | tr [A-Z] [a-z]`-`uname -m`

currentDir=`pwd`
cd `dirname $0`/.. && topDir=`pwd`

srcDir=$topDir/src
buildDir=$topDir/build
javaInstallDir=$topDir/java/$CMS_HOST_ARCH
javaFileName=jdk-$JAVA_VERSION-linux-x64.tar.gz

DOWNLOAD_URL=http://download.oracle.com/otn-pub/java/jdk/$JAVA_VERSION-b$BREF/$SHA/$javaFileName

mkdir -p $srcDir
cd $srcDir
if [ ! -f $javaFileName ]; then
    echo 'To Download Java you must accept the Oracle license agreement defined in: http://www.oracle.com/technetwork/java/javase/terms/license/index.html'
    read -p 'Do you accept the Oracle Binary Code License Agreement for Java SE (y/N): ' licenseAgreement

    if [ -z $licenseAgreement ]; then
        licenseAgreement="n"
    fi

    if [ "$licenseAgreement" = "y" -o "$licenseAgreement" = "Y" ]; then
        echo 'License Accepted'
        echo "Retrieving $DOWNLOAD_URL"
        curl -L -b "oraclelicense=accept-securebackup-cookie" -O $DOWNLOAD_URL
    else
        echo 'Cannot continue with the Java installation, license Declined.'
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

    # Create link needed for some apps to find libjvm.so
    cd $javaInstallDir/jre/lib/amd64
    ln -s server client
else
    echo "Error: filename $srcDir/$$javaFileName does not exist"
fi
