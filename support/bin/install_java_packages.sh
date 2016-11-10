#!/bin/bash

# Copyright (c) UChicago Argonne, LLC. All rights reserved.
# See LICENSE file.


currentDir=`pwd`
cd `dirname $0`/.. && topDir=`pwd`
binDir=$topDir/bin

$binDir/install_java.sh || exit 1
$binDir/install_ant.sh || exit 1
$binDir/install_glassfish.sh || exit 1
