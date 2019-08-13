#!/bin/sh

CDB_SVN_URL=https://svn.aps.anl.gov/cdb

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

cd $CDB_SUPPORT_DIR
execute $CDB_SUPPORT_DIR/bin/build_mysql.sh


