#!/bin/sh

# Copyright (c) UChicago Argonne, LLC. All rights reserved.
# See LICENSE file.


currentDir=`pwd`
cd `dirname $0`/.. && topDir=`pwd`
binDir=$topDir/bin

$binDir/install_java_packages.sh || exit 1
$binDir/install_python_packages.sh || exit 1

