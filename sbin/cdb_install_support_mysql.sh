#!/bin/bash

# Copyright (c) UChicago Argonne, LLC. All rights reserved.
# See LICENSE file.


MY_DIR=`dirname $0` && cd $MY_DIR && MY_DIR=`pwd`
if [ -z "${CDB_ROOT_DIR}" ]; then
    CDB_ROOT_DIR=$MY_DIR/..
fi
CDB_SHORT_HOSTNAME=`hostname -s`
CDB_INSTALL_DIR=${CDB_INSTALL_DIR:=$CDB_ROOT_DIR/..}
CDB_SUPPORT_DIR=${CDB_SUPPORT_DIR:=$CDB_INSTALL_DIR/support-$CDB_SHORT_HOSTNAME}

execute() {
    echo "Executing: $@"
    eval "$@"
}

echo "Creating new CDB support directory $CDB_SUPPORT_DIR."
cd `dirname $CDB_SUPPORT_DIR`
execute cp -R $CDB_ROOT_DIR/support/* $CDB_SUPPORT_DIR

cd $CDB_SUPPORT_DIR
execute $CDB_SUPPORT_DIR/bin/build_mysql.sh
