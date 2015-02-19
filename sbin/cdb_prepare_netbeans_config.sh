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


echo "Preparing netbeans configuration"

echo "Modifying glassfish-web config file"
portalSrcDir=$CDB_ROOT_DIR/src/java/CdbWebPortal
configFile=$portalSrcDir/web/WEB-INF/glassfish-web.xml
cmd="cat $configFile.template | sed 's?CDB_DATA_DIR?$CDB_DATA_DIR?g' > $configFile"
eval $cmd

# configure glassfish db access
passwordFile=$CDB_ROOT_DIR/etc/cdb.db.passwd
CDB_DB_PASSWORD=`cat $passwordFile`
configFile=$portalSrcDir/setup/glassfish-resources.xml
cmd="cat $configFile.template | sed 's?CBD_DB_PASSWORD?$CDB_DB_PASSWORD?g' > $configFile"
eval $cmd

echo "Done preparing netbeans configuration"
