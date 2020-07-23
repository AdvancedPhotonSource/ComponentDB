#!/bin/bash

# Copyright (c) UChicago Argonne, LLC. All rights reserved.
# See LICENSE file.

ANACONDA_VERSION=2020.02

CDB_HOST_ARCH=$(uname -sm | tr -s '[:upper:][:blank:]' '[:lower:][\-]')

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
    path="-p $anacondaInstallDir"

    echo "Installing anaconda"
    sh $anacondaFileName $batch $skip $path
fi
