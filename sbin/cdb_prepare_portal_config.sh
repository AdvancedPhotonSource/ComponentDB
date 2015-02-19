#!/bin/sh

#
# Script used for preparing CDB webapp development
#
# Usage:
#
# $0 
#

MY_DIR=`dirname $0` && cd $MY_DIR && MY_DIR=`pwd`
if [ -z "${CDB_ROOT_DIR}" ]; then
    CDB_ROOT_DIR=$MY_DIR/..
fi
CDB_ENV_FILE=${CDB_ROOT_DIR}/setup.sh
if [ ! -f ${CDB_ENV_FILE} ]; then
    echo "Environment file ${CDB_ENV_FILE} does not exist." 
    exit 2
fi
. ${CDB_ENV_FILE} > /dev/null

CDB_HOST_ARCH=`uname | tr [A-Z] [a-z]`-`uname -m`
CDB_CONTEXT_ROOT=${CDB_CONTEXT_ROOT:=cdb}
CDB_DATA_DIR=${CDB_DATA_DIR:=$CDB_INSTALL_DIR/data}

echo "Creating data directories"
mkdir -p "$CDB_DATA_DIR/log"
mkdir -p "$CDB_DATA_DIR/propertyValue"

echo "Modifying glassfish-web config file"
configFile=$CDB_ROOT_DIR/src/java/CdbWebPortal/web/WEB-INF/glassfish-web.xml
cmd="cat $configFile.template | sed 's?CDB_DATA_DIR?$CDB_DATA_DIR?g' > $configFile"
eval $cmd

echo "Done preparing portal configuration"
