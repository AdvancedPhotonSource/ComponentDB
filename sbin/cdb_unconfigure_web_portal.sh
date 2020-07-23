#!/bin/bash

# Copyright (c) UChicago Argonne, LLC. All rights reserved.
# See LICENSE file.


#
# Script used for un-configuring CDB webapp
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

CDB_HOST_ARCH=$(uname -sm | tr -s '[:upper:][:blank:]' '[:lower:][\-]')
GLASSFISH_DIR=$CDB_SUPPORT_DIR/payara/$CDB_HOST_ARCH
JAVA_HOME=$CDB_SUPPORT_DIR/java/$CDB_HOST_ARCH

export AS_JAVA=$JAVA_HOME
ASADMIN_CMD=$GLASSFISH_DIR/bin/asadmin

CDB_DB_POOL=mysql_${CDB_DB_NAME}_DbPool
CDB_DATA_SOURCE=${CDB_DB_NAME}_DataSource
CDB_DOMAIN=production

# restart server
echo "Restarting glassfish"
$ASADMIN_CMD stop-domain ${CDB_DOMAIN}
$ASADMIN_CMD start-domain ${CDB_DOMAIN}

# delete JDBC resource associated with this connection pool
echo "Deleting JDBC resource $CDB_DATA_SOURCE"
$ASADMIN_CMD delete-jdbc-resource ${CDB_DATA_SOURCE}

# delete JDBC connection pool
echo "Deleting JDBC connection pool $CDB_DB_POOL"
$ASADMIN_CMD delete-jdbc-connection-pool ${CDB_DB_POOL}
