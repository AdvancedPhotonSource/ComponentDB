#!/bin/bash

# Copyright (c) UChicago Argonne, LLC. All rights reserved.
# See LICENSE file.


#
# Script used for creation of configuration file used for CDB.
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

CDB_ETC_DIR=$CDB_INSTALL_DIR/etc

# Look for deployment file in etc directory.
deployConfigFile=$CDB_ETC_DIR/${CDB_DB_NAME}.deploy.conf
if [ -f $deployConfigFile ]; then
    echo "Using deployment config file: $deployConfigFile"
    . $deployConfigFile
else
    echo "Deployment config file $deployConfigFile not found, creating new file."
    mkdir -p $CDB_ETC_DIR
fi



# Get a DB name based on configuration file.
CDB_DB_NAME=${CDB_DB_NAME:=$1}
CDB_DB_NAME=${CDB_DB_NAME:=cdb}

CDB_DB_USER=${CDB_DB_USER:=$CDB_DB_NAME}
CDB_DB_HOST=${CDB_DB_HOST:=127.0.0.1}
CDB_DB_PORT=${CDB_DB_PORT:=3306}
CDB_DB_ADMIN_USER=${CDB_DB_ADMIN_USER:=admin}
CDB_DB_ADMIN_HOSTS=${CDB_DB_ADMIN_HOSTS:=127.0.0.1}
CDB_DB_CHARACTER_SET=${CDB_DB_CHARACTER_SET:=utf8}
CDB_DB_SCRIPTS_DIR=${CDB_DB_SCRIPTS_DIR:=$CDB_INSTALL_DIR/db/$CDB_DB_NAME}
CDB_CONTEXT_ROOT=${CDB_CONTEXT_ROOT:=$CDB_DB_NAME}
CDB_DATA_DIR=${CDB_DATA_DIR:=$CDB_INSTALL_DIR/data/CDB_DB_NAME}
CDB_PORTAL_TITLE=${CDB_PORTAL_TITLE:=Component Database Portal}
CDB_CSS_PORTAL_TITLE_COLOR=${CDB_CSS_PORTAL_TITLE_COLOR:=#f2f4f7}
CDB_WEB_SERVICE_PORT=${CDB_WEB_SERVICE_PORT:=10232}

read -p "DB Name [$CDB_DB_NAME]: " userDbName
read -p "DB User [$CDB_DB_USER]: " userDbUser
read -p "DB Host [$CDB_DB_HOST]: " userDbHost
read -p "DB Port [$CDB_DB_PORT]: " userDbPort
read -p "DB Admin User [$CDB_DB_ADMIN_USER]: " userDbAdminUser
read -p "DB Admin Hosts [$CDB_DB_ADMIN_HOSTS]: " userDbAdminHosts
read -p "DB Character Set [$CDB_DB_CHARACTER_SET]: " userDbCharset
read -p "DB Populate Scripts Dir [$CDB_DB_SCRIPTS_DIR]: " userDbScriptsDir
read -p "DB Data Dir [$CDB_DATA_DIR]: " userDataDir
read -p "Context Root [$CDB_CONTEXT_ROOT]: " userContextRoot
read -p "Portal Title [$CDB_PORTAL_TITLE]: " userPortalTitle
read -p "Portal Title Color [$CDB_CSS_PORTAL_TITLE_COLOR]: " userPortalTitleColor
read -p "Service Port [$CDB_WEB_SERVICE_PORT]: " userServicePort
read -p "Auth LDAP server [$CDB_LDAP_AUTH_SERVER_URL]: " userLdapServerUrl
read -p "Auth LDAP dn format (use %s for username placeholder) [$CDB_LDAP_AUTH_DN_FORMAT]: " userLdapServerDnFormat

if [ ! -z $userDbName ]; then
	CDB_DB_NAME=$userDbName
fi
if [ ! -z $userDbUser ]; then
	CDB_DB_USER=$userDbUser
fi
if [ ! -z $userDbHost ]; then
	CDB_DB_HOST=$userDbHost
fi
if [ ! -z $userDbPort ]; then
	CDB_DB_PORT=$userDbPort
fi
if [ ! -z $userDbAdminUser ]; then
	CDB_DB_ADMIN_USER=$userDbAdminUser
fi
if [ ! -z $userDbAdminHosts ]; then
	CDB_DB_ADMIN_HOSTS=$userDbAdminHosts
fi
if [ ! -z $userDbCharset ]; then
	CDB_DB_CHARACTER_SET=$userDbCharset
fi
if [ ! -z $userDbScriptsDir ]; then
	CDB_DB_SCRIPTS_DIR=$userDbScriptsDir
fi
if [ ! -z $userDataDir ]; then
	CDB_DATA_DIR=$userDataDir
fi
if [ ! -z $userContextRoot ]; then
	CDB_CONTEXT_ROOT=$userContextRoot
fi
if [ ! -z "$userPortalTitle" ]; then
	CDB_PORTAL_TITLE=$userPortalTitle
fi
if [ ! -z $userPortalTitleColor ]; then
	CDB_CSS_PORTAL_TITLE_COLOR=$userPortalTitleColor
fi
if [ ! -z $userServicePort ]; then
	CDB_WEB_SERVICE_PORT=$userServicePort
fi
if [ ! -z $userLdapServerUrl ]; then
	CDB_LDAP_AUTH_SERVER_URL=$userLdapServerUrl
fi
if [ ! -z $userLdapServerDnFormat ]; then
	CDB_LDAP_AUTH_DN_FORMAT=$userLdapServerDnFormat
fi

configContents="CDB_DB_NAME=$CDB_DB_NAME"
configContents="$configContents\nCDB_DB_USER=$CDB_DB_USER"
configContents="$configContents\nCDB_DB_HOST=$CDB_DB_HOST"
configContents="$configContents\nCDB_DB_PORT=$CDB_DB_PORT"
configContents="$configContents\nCDB_DB_ADMIN_USER=$CDB_DB_ADMIN_USER"
configContents="$configContents\nCDB_DB_ADMIN_HOSTS=$CDB_DB_ADMIN_HOSTS"
configContents="$configContents\nCDB_DB_CHARACTER_SET=$CDB_DB_CHARACTER_SET"
configContents="$configContents\nCDB_DB_SCRIPTS_DIR=$CDB_DB_SCRIPTS_DIR"
configContents="$configContents\nCDB_DATA_DIR=$CDB_DATA_DIR"
configContents="$configContents\nCDB_CONTEXT_ROOT=$CDB_CONTEXT_ROOT"
configContents="$configContents\nCDB_PORTAL_TITLE=\"$CDB_PORTAL_TITLE\""
configContents="$configContents\nCDB_CSS_PORTAL_TITLE_COLOR=$CDB_CSS_PORTAL_TITLE_COLOR"
configContents="$configContents\nCDB_WEB_SERVICE_PORT=$CDB_WEB_SERVICE_PORT"
configContents="$configContents\nCDB_LDAP_AUTH_SERVER_URL=$CDB_LDAP_AUTH_SERVER_URL"
configContents="$configContents\nCDB_LDAP_AUTH_DN_FORMAT=$CDB_LDAP_AUTH_DN_FORMAT"





echo '**************** RESULTING CONFIGURATION ****************'
echo -e $configContents
echo '*********************************************************'

echo "Saving configuration to: $deployConfigFile"
echo -e $configContents > $deployConfigFile

$CDB_ROOT_DIR/sbin/cdb_create_configuration_openssl.sh
