#!/bin/sh

CDB_SVN_URL=https://svn.aps.anl.gov/cdb

MY_DIR=`dirname $0` && cd $MY_DIR && MY_DIR=`pwd`
if [ -z "${CDB_ROOT_DIR}" ]; then
    CDB_ROOT_DIR=$MY_DIR/..
fi
CDB_INSTALL_DIR=$CDB_ROOT_DIR/..

# Check support directory
if [ -z $CDB_SUPPORT_DIR ]; then
    export CDB_SUPPORT_DIR=$CDB_INSTALL_DIR/support
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

if [ ! -d $CDB_SUPPORT_DIR ]; then
    echo "Creating new CDB support directory $CDB_SUPPORT_DIR."
    cd `dirname $CDB_SUPPORT_DIR`
    execute svn co $CDB_SVN_URL/support `basename $CDB_SUPPORT_DIR`
fi
cd $CDB_SUPPORT_DIR
echo "Building support in $PWD"
execute svn update
execute $CDB_SUPPORT_DIR/bin/clean_all.sh
execute $CDB_SUPPORT_DIR/bin/install_all.sh

if [ ! -d $CDB_DATA_DIR ]; then
    echo "Creating data directories in $CDB_DATA_DIR"
    mkdir -p "$CDB_DATA_DIR/log"
    mkdir -p "$CDB_DATA_DIR/propertyValue"
fi



