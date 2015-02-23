#!/bin/sh

#
# Script used for undeploying CDB web service
# Deployment configuration can be set in etc/$CDB_DB_NAME.deploy.conf file
#
# Usage:
#
# $0 [CDB_DB_NAME]
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

# Use first argument as db name, if provided
CDB_DB_NAME=${CDB_DB_NAME:=cdb}
if [ ! -z "$1" ]; then
    CDB_DB_NAME=$1
fi
echo "Using DB name: $CDB_DB_NAME"

# Look for deployment file in etc directory, and use it to override
# default entries
deployConfigFile=$CDB_ROOT_DIR/etc/${CDB_DB_NAME}.deploy.conf
if [ -f $deployConfigFile ]; then
    echo "Using deployment config file: $deployConfigFile"
    . $deployConfigFile
else
    echo "Deployment config file $deployConfigFile not found, using defaults"
fi

CDB_HOST_ARCH=`uname | tr [A-Z] [a-z]`-`uname -m`
CDB_CONTEXT_ROOT=${CDB_CONTEXT_ROOT:=cdb}
CDB_DATA_DIR=${CDB_DATA_DIR:=/cdb}
CDB_INSTALL_DIR=${CDB_INSTALL_DIR:=$CDB_ROOT_DIR/..}
CDB_ETC_DIR=${CDB_INSTALL_DIR}/etc
CDB_SSL_DIR=${CDB_ETC_DIR}/ssl
CDB_CA_DIR=${CDB_ETC_DIR}/CA
CDB_CA_CERT_FILE=${CDB_SSL_DIR}/cdb-ca-cert.pem
CDB_WEB_SERVICE_DAEMON=cdb-web-service
CDB_WEB_SERVICE_CERT_FILE=${CDB_SSL_DIR}/$CDB_WEB_SERVICE_DAEMON.$CDB_DB_NAME.crt
CDB_WEB_SERVICE_KEY_FILE=${CDB_SSL_DIR}/$CDB_WEB_SERVICE_DAEMON.$CDB_DB_NAME.key
CDB_WEB_SERVICE_CONFIG_FILE=${CDB_ETC_DIR}/$CDB_WEB_SERVICE_DAEMON.$CDB_DB_NAME.conf
CDB_WEB_SERVICE_LOG_FILE=${CDB_INSTALL_DIR}/var/log/$CDB_WEB_SERVICE_DAEMON.$CDB_DB_NAME.log
CDB_WEB_SERVICE_INIT_CMD=${CDB_ROOT_DIR}/etc/init.d/$CDB_WEB_SERVICE_DAEMON
CDB_DB_PASSWORD_FILE=${CDB_ETC_DIR}/${CDB_DB_NAME}.db.passwd

echo "CDB install directory: $CDB_INSTALL_DIR"

echo "Stopping web service for $CDB_DB_NAME"
$CDB_WEB_SERVICE_INIT_CMD stop $CDB_DB_NAME

rm -f $CDB_WEB_SERVICE_CONFIG_FILE
rm -f $CDB_DB_PASSWORD_FILE
echo "Done undeploying web service for $CDB_DB_NAME"
