#!/bin/bash

# Copyright (c) UChicago Argonne, LLC. All rights reserved.
# See LICENSE file.


CDB_HOST_ARCH=$(uname -s | tr [:uppercase:] [:lowercase:])-$(uname -m)

currentDir=`pwd`
cd `dirname $0`/.. && topDir=`pwd`

srcDir=$topDir/src
buildDir=$topDir/build
pythonInstallDir=$topDir/python/$CDB_HOST_ARCH

mkdir -p $buildDir

# Install.
export PATH=$pythonInstallDir/bin:$PATH
export PATH=$pythonInstallDir/lib:$PATH
export LD_LIBRARY_PATH=$pythonInstallDir/lib:$LD_LIBRARY_PATH
cd $buildDir
echo Installing pip
easy_install pip || exit 1
