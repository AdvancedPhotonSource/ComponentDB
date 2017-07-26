#!/bin/bash

# Copyright (c) UChicago Argonne, LLC. All rights reserved.
# See LICENSE file.


MYSQL_VERSION=5.6.37
MYSQL_TGZ_FILE=mysql-$MYSQL_VERSION.tar.gz
CDB_HOST_ARCH=`uname | tr [A-Z] [a-z]`-`uname -m`

currentDir=`pwd`
cd `dirname $0`/.. && topDir=`pwd`

srcDir=$topDir/src
buildDir=$topDir/build
mysqlBuildDir=$buildDir/mysql-$MYSQL_VERSION
mysqlInstallDir=$topDir/mysql/$CDB_HOST_ARCH

DOWNLOAD_URL=https://dev.mysql.com/get/Downloads/MySQL-5.6/$MYSQL_TGZ_FILE

mkdir -p $srcDir
cd $srcDir
if [ ! -f $MYSQL_TGZ_FILE ]; then
    echo "Retrieving $DOWNLOAD_URL"
    curl -L -O $DOWNLOAD_URL
fi


if [ -f $MYSQL_TGZ_FILE ]; then
    mkdir -p $buildDir
    cd $buildDir

    # Download package

    cd $buildDir

    # Build mysql.
    echo Building mysql $MYSQL_VERSION
    tar zxf $srcDir/$MYSQL_TGZ_FILE
    cd $mysqlBuildDir
    cmake -DCMAKE_INSTALL_PREFIX:PATH=$mysqlInstallDir
    make || exit 1
    make install || exit 1   
    cd $topDir
else
    echo "File $srcDir/$MYSQL_TGZ_FILE not found."
    exit 1
fi
