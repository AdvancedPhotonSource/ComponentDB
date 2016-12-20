#!/bin/bash

# Copyright (c) UChicago Argonne, LLC. All rights reserved.
# See LICENSE file.


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

CDB_INSTALL_DIR=${CDB_INSTALL_DIR:=$CDB_ROOT_DIR/..}

# Look for deployment file in etc directory, and use it to override
# default entries
deployConfigFile=$CDB_INSTALL_DIR/etc/${CDB_DB_NAME}.deploy.conf
if [ -f $deployConfigFile ]; then
    echo "Using deployment config file: $deployConfigFile"
    . $deployConfigFile
else
    echo "Deployment config file $deployConfigFile not found, using defaults"
fi

CDB_HOST_ARCH=`uname | tr [A-Z] [a-z]`-`uname -m`
CDB_CONTEXT_ROOT=${CDB_CONTEXT_ROOT:=cdb}
CDB_DATA_DIR=${CDB_DATA_DIR:=/cdb}
CDB_ETC_DIR=${CDB_INSTALL_DIR}/etc
CDB_SSL_DIR=${CDB_ETC_DIR}/ssl
CDB_LOG_DIR=${CDB_INSTALL_DIR}/var/log
CDB_CA_DIR=${CDB_ETC_DIR}/CA
CDB_CA_CERT_FILE=${CDB_SSL_DIR}/cdb-ca-cert.pem
CDB_WEB_SERVICE_DAEMON=cdb-web-service
CDB_WEB_SERVICE_CERT_FILE=${CDB_SSL_DIR}/$CDB_DB_NAME.$CDB_WEB_SERVICE_DAEMON.crt
CDB_WEB_SERVICE_KEY_FILE=${CDB_SSL_DIR}/$CDB_DB_NAME.$CDB_WEB_SERVICE_DAEMON.key
CDB_WEB_SERVICE_CONFIG_FILE=${CDB_ETC_DIR}/$CDB_DB_NAME.$CDB_WEB_SERVICE_DAEMON.conf
CDB_WEB_SERVICE_LOG_FILE=${CDB_LOG_DIR}/$CDB_DB_NAME.$CDB_WEB_SERVICE_DAEMON.log
CDB_WEB_SERVICE_INIT_CMD=${CDB_ROOT_DIR}/etc/init.d/$CDB_WEB_SERVICE_DAEMON
CDB_DB_PASSWORD_FILE=${CDB_ETC_DIR}/${CDB_DB_NAME}.db.passwd
CDB_USER_SETUP_FILE=${CDB_ETC_DIR}/${CDB_DB_NAME}.setup.sh
CDB_WEB_SERVICE_HOST=`hostname -f`
CDB_DATE=`date +%Y.%m.%d`

echo "CDB install directory: $CDB_INSTALL_DIR"

mkdir -p $CDB_ETC_DIR
mkdir -p $CDB_SSL_DIR
mkdir -p $CDB_LOG_DIR
chmod 700 $CDB_SSL_DIR

echo "Stopping web service for $CDB_DB_NAME"
$CDB_WEB_SERVICE_INIT_CMD stop $CDB_DB_NAME

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
    if [ ! -f $CDB_CA_DIR/certs/$CDB_WEB_SERVICE_HOST.crt ]; then
        $MY_DIR/cdb_create_server_cert.sh $CDB_WEB_SERVICE_HOST $CDB_WEB_SERVICE_HOST cdb@aps.anl.gov || exit 1
    fi
    rsync -ar $CDB_CA_DIR/certs/$CDB_WEB_SERVICE_HOST.crt $CDB_WEB_SERVICE_CERT_FILE
    rsync -ar $CDB_CA_DIR/certs/$CDB_WEB_SERVICE_HOST.key $CDB_WEB_SERVICE_KEY_FILE
else
    echo "CDB service certificate exists"
fi

echo "Checking service configuration file"
if [ ! -f $CDB_WEB_SERVICE_CONFIG_FILE ]; then
    echo "Generating service config file"
    generatedURL="https://`hostname`:8181/$CDB_DB_NAME"
    emailDomainSection=`dnsdomainname`

    if [[ -z $emailDomainSection ]]; then
      emailDomainSection=`hostname`
    fi

    generatedAdminEmail="`whoami`@$emailDomainSection"
    generatedFromEmail="cdb@$emailDomainSection"

    read -p "Please enter the portal web address to use for generating urls to portal [$generatedURL]: " CDB_PORTAL_URL
    read -p "Please enter the email address to use as a 'FROM' cdb address [$generatedFromEmail]: " CDB_SENDER_EMAIL_ADDRESS
    read -p "Please enter the system admin email address, used for system notifications such as exceptions [$generatedAdminEmail]: " ADMIN_EMAIL_ADDRESS

    EMAIL_UTILITY_MODE='production'
    EMAIL_SUBJECT_START=`echo $CDB_DB_NAME | tr [a-z] [A-Z]`
    EMAIL_SUBJECT_START="[$EMAIL_SUBJECT_START]"

    if [[ -z $CDB_PORTAL_URL ]]; then
        CDB_PORTAL_URL="$generatedURL"
    fi

    if [[ -z $CDB_SENDER_EMAIL_ADDRESS ]]; then
        CDB_SENDER_EMAIL_ADDRESS="$generatedFromEmail"
    fi

    if [[ -z $ADMIN_EMAIL_ADDRESS ]]; then
        ADMIN_EMAIL_ADDRESS="$generatedAdminEmail"
    fi

    cmd="cat $CDB_ROOT_DIR/etc/cdb-web-service.conf.template \
        | sed 's?servicePort=.*?servicePort=$CDB_WEB_SERVICE_PORT?g' \
        | sed 's?sslCaCertFile=.*?sslCaCertFile=$CDB_CA_CERT_FILE?g' \
        | sed 's?sslCertFile=.*?sslCertFile=$CDB_WEB_SERVICE_CERT_FILE?g' \
        | sed 's?sslKeyFile=.*?sslKeyFile=$CDB_WEB_SERVICE_KEY_FILE?g' \
        | sed 's?handler=TimedRotatingFileLoggingHandler.*?handler=TimedRotatingFileLoggingHandler(\"$CDB_WEB_SERVICE_LOG_FILE\")?g' \
        | sed 's?CDB_INSTALL_DIR?$CDB_INSTALL_DIR?g' \
        | sed 's?CDB_DB_NAME?$CDB_DB_NAME?g' \
        | sed 's?CDB_PORTAL_URL?$CDB_PORTAL_URL?g' \
        | sed 's?EMAIL_UTILITY_MODE?$EMAIL_UTILITY_MODE?g' \
        | sed 's?CDB_SENDER_EMAIL_ADDRESS?$CDB_SENDER_EMAIL_ADDRESS?g' \
        | sed 's?ADMIN_EMAIL_ADDRESS?$ADMIN_EMAIL_ADDRESS?g' \
        | sed 's?EMAIL_SUBJECT_START?$EMAIL_SUBJECT_START?g' \
        | sed 's?CDB_LDAP_AUTH_SERVER_URL?$CDB_LDAP_AUTH_SERVER_URL?g' \
        | sed 's?CDB_LDAP_AUTH_DN_FORMAT=?$CDB_LDAP_AUTH_DN_FORMAT?g' \
        | sed 's?CDB_DATA_DIR?$CDB_DATA_DIR?g'\
        > $CDB_WEB_SERVICE_CONFIG_FILE"
    eval $cmd || exit 1
else
    echo "Service config file exists"
fi
if [ -f $CDB_ROOT_DIR/etc/$CDB_DB_NAME.db.passwd ]; then
    rsync -ar $CDB_ROOT_DIR/etc/$CDB_DB_NAME.db.passwd $CDB_ETC_DIR || exit 1
fi

# Modify version
CDB_SOFTWARE_VERSION=`cat $CDB_ROOT_DIR/etc/version`
echo "Modifying python module version"
versionFile=$CDB_ROOT_DIR/src/python/cdb/__init__.py
cmd="cat $versionFile | sed 's?__version__ =.*?__version__ = \"${CDB_SOFTWARE_VERSION} ($CDB_DATE)\"?g' > $versionFile.2
&& mv $versionFile.2 $versionFile"
eval $cmd

# Prepare setup file
echo "Preparing setup file"
cmd="cat $CDB_ROOT_DIR/etc/setup.sh.template \
        | sed 's?CDB_ROOT_DIR=.*?CDB_ROOT_DIR=$CDB_ROOT_DIR?g' \
        | sed 's?CDB_SERVICE_PROTOCOL=.*?CDB_SERVICE_PROTOCOL=https?g' \
        | sed 's?CDB_SERVICE_HOST=.*?CDB_SERVICE_HOST=$CDB_WEB_SERVICE_HOST?g' \
        | sed 's?CDB_SERVICE_PORT=.*?CDB_SERVICE_PORT=$CDB_WEB_SERVICE_PORT?g' \
        > $CDB_USER_SETUP_FILE"
eval $cmd || exit 1

echo "Starting web service for $CDB_DB_NAME"
$CDB_WEB_SERVICE_INIT_CMD start $CDB_DB_NAME

echo "Done deploying web service for $CDB_DB_NAME"
