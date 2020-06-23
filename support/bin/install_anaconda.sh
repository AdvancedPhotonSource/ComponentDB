#!/bin/bash

# Copyright (c) UChicago Argonne, LLC. All rights reserved.
# See LICENSE file.

ANACONDA_VERSION=2020.02

CDB_HOST_ARCH=`uname | tr [A-Z] [a-z]`-`uname -m`

currentDir=`pwd`
cd `dirname $0`/.. && topDir=`pwd`

srcDir=$topDir/src
buildDir=$topDir/build
anacondaInstallDir=$topDir/anaconda/$CDB_HOST_ARCH
anacondaFileName="Anaconda3-${ANACONDA_VERSION}-Linux-x86_64.sh"

echo $anacondaFileName

DOWNLOAD_URL="https://repo.anaconda.com/archive/$anacondaFileName"

mkdir -p $srcDir
cd $srcDir

if [ ! -f $anacondaFileName ]; then
    echo "Retrieving $DOWNLOAD_URL"
    curl -o $anacondaFileName $DOWNLOAD_URL
fi

if [ -f $anacondaFileName ]; then
    help=-h
    batch=-b
    skip=-s
    path="-p /mnt/c/Users/liamj/CDBDevEnv/support-DESKTOP-PIDH0CC/anaconda/linux-x86_64/"

    echo "Installing anaconda"
    ./$anacondaFileName $batch $skip $path
    #./Anaconda3-2020.02-Linux-x86_64.sh $batch $skip $path
    #./Anaconda3-2020.02-Linux-x86_64.sh $help
fi
