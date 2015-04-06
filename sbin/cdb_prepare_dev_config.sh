#!/bin/sh

#
# Script used for preparing CDB development
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
CDB_INSTALL_DIR=${CDB_INSTALL_DIR:=$CDB_ROOT_DIR/..}
CDB_ETC_DIR=${CDB_INSTALL_DIR}/etc
CDB_LOG_DIR=${CDB_INSTALL_DIR}/var/log

CDB_DB_NAME=cdb
CDB_WEB_SERVICE_CONFIG_FILE=${CDB_ETC_DIR}/${CDB_DB_NAME}.conf
CDB_WEB_SERVICE_LOG_FILE=${CDB_LOG_DIR}/${CDB_DB_NAME}.log
CDB_DB_PASSWORD_FILE=$CDB_ROOT_DIR/etc/${CDB_DB_NAME}.db.passwd

echo "Preparing development configuration"
mkdir -p $CDB_ETC_DIR
mkdir -p $CDB_LOG_DIR

echo "Modifying glassfish-web config file"
portalSrcDir=$CDB_ROOT_DIR/src/java/CdbWebPortal
configFile=$portalSrcDir/web/WEB-INF/glassfish-web.xml
cmd="cat $configFile.template | sed 's?CDB_DATA_DIR?$CDB_DATA_DIR?g' > $configFile"
eval $cmd || exit 1

echo "Configuring glassfish db access"
if [ ! -f $CDB_DB_PASSWORD_FILE ]; then
    echo "File $CDB_DB_PASSWORD_FILE does not exist."
    exit 1
fi

CDB_DB_PASSWORD=`cat $CDB_DB_PASSWORD_FILE`
configFile=$portalSrcDir/setup/glassfish-resources.xml
cmd="cat $configFile.template | sed 's?CDB_DB_PASSWORD?$CDB_DB_PASSWORD?g' > $configFile"
eval $cmd || exit 1

echo "Generating web service config file"
cmd="cat $CDB_ROOT_DIR/etc/cdb-web-service.conf.template \
    | sed 's?sslCaCertFile=.*??g' \
    | sed 's?sslCertFile=.*??g' \
    | sed 's?sslKeyFile=.*??g' \
    | sed 's?CDB_INSTALL_DIR?$CDB_INSTALL_DIR?g' \
    | sed 's?CDB_DB_NAME?$CDB_DB_NAME?g' \
    | sed 's?handler=TimedRotatingFileLoggingHandler.*?handler=TimedRotatingFileLoggingHandler(\"$CDB_WEB_SERVICE_LOG_FILE\")?g' \
    > $CDB_WEB_SERVICE_CONFIG_FILE"
eval $cmd || exit 1
rsync -ar $CDB_DB_PASSWORD_FILE $CDB_ETC_DIR || exit 1

echo "Done preparing development configuration"
