#!/bin/bash

# Copyright (c) UChicago Argonne, LLC. All rights reserved.
# See LICENSE file.


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
CDB_DB_PASSWORD_FILE=$CDB_INSTALL_DIR/etc/${CDB_DB_NAME}.db.passwd
read -p "Please enter a url for the LDAP server: " CDB_LDAP_AUTH_SERVER_URL
read -p "Please enter the dn string for the LDAP server (%s is replaced with username when lookup not needed): " CDB_LDAP_AUTH_DN_FORMAT
read -p "Please enter the lookup service dn (don't specify if no user lookup required): " CDB_LDAP_SERVICE_DN
read -p "Please enter the lookup service password (don't specify if no user lookup required): " CDB_LDAP_SERVICE_PASS
read -p "Please enter the user lookup filter. (%s is replaced with username; don't specify if no user lookup required): " CDB_LDAP_LOOKUP_FILTER

CDB_LDAP_LOOKUP_FILTER=`echo ${CDB_LDAP_LOOKUP_FILTER/&/\\\&}`

echo "Preparing development configuration"
mkdir -p $CDB_ETC_DIR
mkdir -p $CDB_LOG_DIR

echo "Modifying glassfish-web config file"
portalSrcDir=$CDB_ROOT_DIR/src/java/CdbWebPortal
configFile=$portalSrcDir/web/WEB-INF/glassfish-web.xml
cmd="cat $configFile.template | sed 's?CDB_DATA_DIR?$CDB_DATA_DIR?g' > $configFile"
eval $cmd || exit 1

echo "Modifying web config file"
webConfigFile=$portalSrcDir/web/WEB-INF/web.xml
cmd="cat $webConfigFile.template \
    | sed 's?CDB_PROJECT_STAGE?Development?g' \
    > $webConfigFile"
eval $cmd || exit 1

echo "Modifying cdb.portal.properties config file"
portalSrcDir=$CDB_ROOT_DIR/src/java/CdbWebPortal
configFile=$portalSrcDir/src/java/cdb.portal.properties
cmd="cat $configFile.template \
    | sed 's?CDB_LDAP_AUTH_SERVER_URL?$CDB_LDAP_AUTH_SERVER_URL?g' \
    | sed 's?CDB_LDAP_AUTH_DN_FORMAT?$CDB_LDAP_AUTH_DN_FORMAT?g' \
    | sed 's?CDB_LDAP_SERVICE_DN?$CDB_LDAP_SERVICE_DN?g' \
    | sed 's?CDB_LDAP_SERVICE_PASS?$CDB_LDAP_SERVICE_PASS?g' \
    | sed 's?CDB_LDAP_LOOKUP_FILTER?$CDB_LDAP_LOOKUP_FILTER?g' \
    | sed 's?CDB_DATA_DIR?$CDB_DATA_DIR?g' \
    > $configFile"
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

CDB_PORTAL_URL="http://localhost:8080/cdb"

read -p "Please enter developer email address, used for web service email notification testing (Optional): " ADMIN_EMAIL_ADDRESS

if [[ ! -z $ADMIN_EMAIL_ADDRESS ]]; then
    EMAIL_UTILITY_MODE='developmentWithEmail'
else
    EMAIL_UTILITY_MODE='development'
    ADMIN_EMAIL_ADDRESS='None'
fi

# uncomment principal authenticator
if [ ! -z $CDB_LDAP_SERVICE_DN ]; then
    uncommentAuthenticator="sed 's?#principalAuthenticator3?principalAuthenticator3?g'"
else
    uncommentAuthenticator="sed 's?#principalAuthenticator2?principalAuthenticator2?g'"
fi

CDB_SENDER_EMAIL_ADDRESS='cdb@cdb'
EMAIL_SUBJECT_START="[CDB_DEV] - `hostname` : "

CDB_LDAP_LOOKUP_FILTER=`echo ${CDB_LDAP_LOOKUP_FILTER/&/\\\&}`

echo "Generating web service config file"
cmd="cat $CDB_ROOT_DIR/etc/cdb-web-service.conf.template \
    | $uncommentAuthenticator \
    | sed 's?sslCaCertFile=.*??g' \
    | sed 's?sslCertFile=.*??g' \
    | sed 's?sslKeyFile=.*??g' \
    | sed 's?CDB_INSTALL_DIR?$CDB_INSTALL_DIR?g' \
    | sed 's?CDB_DB_NAME?$CDB_DB_NAME?g' \
    | sed 's?handler=TimedRotatingFileLoggingHandler.*?handler=TimedRotatingFileLoggingHandler(\"$CDB_WEB_SERVICE_LOG_FILE\")?g' \
    | sed 's?CDB_PORTAL_URL?$CDB_PORTAL_URL?g' \
    | sed 's?EMAIL_UTILITY_MODE?$EMAIL_UTILITY_MODE?g' \
    | sed 's?CDB_SENDER_EMAIL_ADDRESS?$CDB_SENDER_EMAIL_ADDRESS?g' \
    | sed 's?ADMIN_EMAIL_ADDRESS?$ADMIN_EMAIL_ADDRESS?g' \
    | sed 's?EMAIL_SUBJECT_START?$EMAIL_SUBJECT_START?g' \
    | sed 's?CDB_LDAP_AUTH_DN_FORMAT?$CDB_LDAP_AUTH_DN_FORMAT?g' \
    | sed 's?CDB_LDAP_AUTH_SERVER_URL?$CDB_LDAP_AUTH_SERVER_URL?g' \
    | sed 's?CDB_LDAP_SERVICE_DN?$CDB_LDAP_SERVICE_DN?g' \
    | sed 's?CDB_LDAP_SERVICE_PASS?$CDB_LDAP_SERVICE_PASS?g' \
    | sed 's?CDB_LDAP_LOOKUP_FILTER?$CDB_LDAP_LOOKUP_FILTER?g' \
    | sed 's?CDB_DATA_DIR?$CDB_DATA_DIR?g'\
    > $CDB_WEB_SERVICE_CONFIG_FILE"
eval $cmd || exit 1
#rsync -ar $CDB_DB_PASSWORD_FILE $CDB_ETC_DIR || exit 1

python3 $CDB_ROOT_DIR/tools/developer_tools/cdb_plugins/update_plugin_generated_files.py

echo "Done preparing development configuration"
