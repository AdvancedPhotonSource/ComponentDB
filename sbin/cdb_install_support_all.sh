#!/bin/bash

# Copyright (c) UChicago Argonne, LLC. All rights reserved.
# See LICENSE file.


MY_DIR=`dirname $0` && cd $MY_DIR && MY_DIR=`pwd`
if [ -z "${CDB_ROOT_DIR}" ]; then
    CDB_ROOT_DIR=$MY_DIR/..
fi
CDB_INSTALL_DIR=$CDB_ROOT_DIR/..
CDB_SHORT_HOSTNAME=`hostname -s`

# Check support directory
if [ -z $CDB_SUPPORT_DIR ]; then
    export CDB_SUPPORT_DIR=$CDB_INSTALL_DIR/support-$CDB_SHORT_HOSTNAME
    if [ -d $CDB_SUPPORT_DIR ]; then
        cd $CDB_SUPPORT_DIR
	export CDB_SUPPORT_DIR=`pwd`
    fi
fi

# Check data directory
if [ -z $CDB_DATA_DIR ]; then
    export CDB_DATA_DIR=$CDB_INSTALL_DIR/data
    if [ -d $CDB_DATA_DIR ]; then
        cd $CDB_DATA_DIR
	export CDB_DATA_DIR=`pwd`
    fi
fi

execute() {
    echo "Executing: $@"
    eval "$@"
}

echo "Creating new CDB support directory $CDB_SUPPORT_DIR."
cd `dirname $CDB_SUPPORT_DIR`
execute cp -R $CDB_ROOT_DIR/support $CDB_SUPPORT_DIR

cd $CDB_SUPPORT_DIR
echo "Building support in $PWD"

execute $CDB_SUPPORT_DIR/bin/clean_all.sh
execute $CDB_SUPPORT_DIR/bin/install_all.sh

if [ ! -d $CDB_DATA_DIR ]; then
    echo "Creating data directories in $CDB_DATA_DIR"
    mkdir -p "$CDB_DATA_DIR/log"
    mkdir -p "$CDB_DATA_DIR/propertyValue"
fi
