#!/bin/sh

#
# Script used for deploying CDB web service
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
CDB_LOG_DIR=${CDB_INSTALL_DIR}/var/log
CDB_CA_DIR=${CDB_ETC_DIR}/CA
CDB_CA_CERT_FILE=${CDB_SSL_DIR}/cdb-ca-cert.pem
CDB_WEB_SERVICE_DAEMON=cdb-web-service
CDB_WEB_SERVICE_CERT_FILE=${CDB_SSL_DIR}/$CDB_WEB_SERVICE_DAEMON.$CDB_DB_NAME.crt
CDB_WEB_SERVICE_KEY_FILE=${CDB_SSL_DIR}/$CDB_WEB_SERVICE_DAEMON.$CDB_DB_NAME.key
CDB_WEB_SERVICE_CONFIG_FILE=${CDB_ETC_DIR}/$CDB_WEB_SERVICE_DAEMON.$CDB_DB_NAME.conf
CDB_WEB_SERVICE_LOG_FILE=${CDB_LOG_DIR}/$CDB_WEB_SERVICE_DAEMON.$CDB_DB_NAME.log
CDB_WEB_SERVICE_INIT_CMD=${CDB_ROOT_DIR}/etc/init.d/$CDB_WEB_SERVICE_DAEMON
CDB_DB_PASSWORD_FILE=${CDB_ETC_DIR}/${CDB_DB_NAME}.db.passwd 
CDB_DATE=`date +%Y.%m.%d`

echo "CDB install directory: $CDB_INSTALL_DIR"

mkdir -p $CDB_ETC_DIR
mkdir -p $CDB_SSL_DIR
mkdir -p $CDB_LOG_DIR
chmod 700 $CDB_SSL_DIR

echo "Checking CA certificate"
if [ ! -f $CDB_CA_CERT_FILE ]; then
    echo "Creating CDB CA"
    $MY_DIR/cdb_create_ca.sh || exit 1
    rsync -ar $CDB_CA_DIR/cacert.pem $CDB_CA_CERT_FILE
else
    echo "CDB CA certificate exists"
fi

echo "Checking service certificates"
if [ ! -f $CDB_WEB_SERVICE_CERT_FILE -o ! -f $CDB_WEB_SERVICE_KEY_FILE ]; then
    echo "Creating CDB Web Service certificate"
    $MY_DIR/cdb_create_server_cert.sh $CDB_DB_NAME "$CDB_DB_NAME web service" $CDB_DB_NAME@aps.anl.gov || exit 1
    rsync -ar $CDB_CA_DIR/certs/$CDB_DB_NAME.crt $CDB_WEB_SERVICE_CERT_FILE
    rsync -ar $CDB_CA_DIR/certs/$CDB_DB_NAME.key $CDB_WEB_SERVICE_KEY_FILE
else
    echo "CDB service certificate exists"
fi

echo "Checking service configuration file"
if [ ! -f $CDB_WEB_SERVICE_CONFIG_FILE ]; then
    echo "Generating service config file"
    cmd="cat $CDB_ROOT_DIR/etc/cdb-web-service.conf.template \
        | sed 's?servicePort=.*?servicePort=$CDB_WEB_SERVICE_PORT?g' \
        | sed 's?sslCaCertFile=.*?sslCaCertFile=$CDB_CA_CERT_FILE?g' \
        | sed 's?sslCertFile=.*?sslCertFile=$CDB_WEB_SERVICE_CERT_FILE?g' \
        | sed 's?sslKeyFile=.*?sslKeyFile=$CDB_WEB_SERVICE_KEY_FILE?g' \
        | sed 's?handler=TimedRotatingFileLoggingHandler.*?handler=TimedRotatingFileLoggingHandler(\"$CDB_WEB_SERVICE_LOG_FILE\")?g' \
        | sed 's?CDB_INSTALL_DIR?$CDB_INSTALL_DIR?g' \
        | sed 's?CDB_DB_NAME?$CDB_DB_NAME?g' \
        > $CDB_WEB_SERVICE_CONFIG_FILE"
    eval $cmd || exit 1
else
    echo "Service config file exists"
fi
rsync -ar $CDB_ROOT_DIR/etc/$CDB_DB_NAME.db.passwd $CDB_ETC_DIR || exit 1

# Modify version
echo "Modifying python module version"
versionFile=$CDB_ROOT_DIR/src/python/cdb/__init__.py
cmd="cat $versionFile | sed 's?__version__ =.*?__version__ = \"${CDB_SOFTWARE_VERSION}\"?g' | sed 's?CDB_DATE?$CDB_DATE?g' > $versionFile.2
&& mv $versionFile.2 $versionFile"

eval $cmd

echo "Starting web service for $CDB_DB_NAME"
$CDB_WEB_SERVICE_INIT_CMD start $CDB_DB_NAME

echo "Done deploying web service for $CDB_DB_NAME"
