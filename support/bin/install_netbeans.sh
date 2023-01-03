#!/bin/bash

# Copyright (c) UChicago Argonne, LLC. All rights reserved.
# See LICENSE file.

CDB_HOST_ARCH=$(uname -sm | tr -s '[:upper:][:blank:]' '[:lower:][\-]')
CDB_HOSTNAME=`hostname -f`

NETBEANS_VERSION=16
NETBEANS_ZIP_FILE=netbeans-$NETBEANS_VERSION-bin.zip
DOWNLOAD_URL=https://dlcdn.apache.org/netbeans/netbeans/$NETBEANS_VERSION/$NETBEANS_ZIP_FILE

currentDir=`pwd`
cd `dirname $0`/.. && topDir=`pwd`
srcDir=$topDir/src
buildDir=$topDir/build
binDir=$topDir/bin
netbeansDir=$topDir/netbeans

netbeansInstallDir=$netbeansDir/netbeans-$NETBEANS_VERSION

mkdir -p $srcDir
mkdir -p $netbeansDir

cd $srcDir
if [ ! -f $NETBEANS_ZIP_FILE ]; then
    echo "Retrieving $DOWNLOAD_URL"
    curl -L -O $DOWNLOAD_URL
fi

if [ ! -f $NETBEANS_ZIP_FILE ]; then
    echo "File $srcDir/$NETBEANS_ZIP_FILE not found."
    exit 1
fi

mkdir -p $buildDir
cd $buildDir

echo "Unpacking $NETBEANS_ZIP_FILE"
mkdir -p `dirname $netbeansInstallDir`
cd `dirname $netbeansInstallDir`
rm -rf `basename $netbeansInstallDir`
unzip -q $srcDir/$NETBEANS_ZIP_FILE
mv netbeans `basename $netbeansInstallDir`

# Create symlink to current netbeans
rm currentNetbeans
ln -s $netbeansInstallDir currentNetbeans

echo "Netbeans installation done"
