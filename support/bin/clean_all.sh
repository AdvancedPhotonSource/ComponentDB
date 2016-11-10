#!/bin/bash

# Copyright (c) UChicago Argonne, LLC. All rights reserved.
# See LICENSE file.


currentDir=`pwd`
cd `dirname $0`/.. && topDir=`pwd`
binDir=$topDir/bin

rm -rf $topDir/java
rm -rf $topDir/ant
rm -rf $topDir/glassfish
rm -rf $topDir/python
rm -rf $topDir/mysql
