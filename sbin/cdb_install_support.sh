#!/bin/sh

CDB_SVN_URL=https://svn.aps.anl.gov/cdb

MY_DIR=`dirname $0` && cd $MY_DIR && MY_DIR=`pwd`
if [ -z "${CDB_ROOT_DIR}" ]; then
    CDB_ROOT_DIR=$MY_DIR/..
fi
CDB_SUPPORT_DIR=$CDB_ROOT_DIR/../support
CDB_DATA_DIR=$CDB_ROOT_DIR/../data

execute() {
    echo "Executing: $@"
    eval "$@"
}

if [ ! -d $CDB_SUPPORT_DIR ]; then
    echo "Creating new CDB support directory $CDB_SUPPORT_DIR."
    cd `dirname $CDB_SUPPORT_DIR`
    execute svn co $CDB_SVN_URL/support support
fi
cd $CDB_SUPPORT_DIR
execute svn update
execute $CDB_SUPPORT_DIR/bin/clean_all.sh
execute $CDB_SUPPORT_DIR/bin/install_all.sh

if [ ! -d $CDB_DATA_DIR ]; then
    echo "Creating data directories"
    mkdir -p "$CDB_DATA_DIR/log"
    mkdir -p "$CDB_DATA_DIR/propertyValue"
fi



