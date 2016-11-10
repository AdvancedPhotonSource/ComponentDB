#!/bin/bash

# Copyright (c) UChicago Argonne, LLC. All rights reserved.
# See LICENSE file.


CDB_HOST_ARCH=`uname | tr [A-Z] [a-z]`-`uname -m`

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
echo Installing python ldap
easy_install python_ldap || exit 1
