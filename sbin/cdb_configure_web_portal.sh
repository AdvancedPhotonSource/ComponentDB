#!/bin/bash

# Copyright (c) UChicago Argonne, LLC. All rights reserved.
# See LICENSE file.


#
# Script used for configuring CDB webapp
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
deployConfigFile=$CDB_INSTALL_DIR/etc/${CDB_DB_NAME}.deploy.`hostname -s`.conf
if [ ! -f $deployConfigFile ]; then
    deployConfigFile=$CDB_INSTALL_DIR/etc/${CDB_DB_NAME}.deploy.conf
fi

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

CDB_DB_HOST=${CDB_DB_HOST:=localhost}
CDB_DB_PORT=${CDB_DB_PORT:=3306}
CDB_DB_USER=${CDB_DB_USER:=cdb}
CDB_DB_POOL=mysql_${CDB_DB_NAME}_DbPool
CDB_DATA_SOURCE=${CDB_DB_NAME}_DataSource
CDB_DOMAIN=production

# Check password from file
passwordFile=$CDB_INSTALL_DIR/etc/$CDB_DB_NAME.db.passwd
if [ -f $passwordFile ]; then
    CDB_DB_PASSWORD=`cat $passwordFile`
else
	CDB_DB_PASSWORD=${CDB_DB_PASSWORD:=cdb}
fi

# copy mysql driver
echo "Copying mysql driver"
rsync -ar $CDB_ROOT_DIR/src/java/CdbWebPortal/lib/mariadb-java-client-3.1.0.jar $GLASSFISH_DIR/glassfish/domains/${CDB_DOMAIN}/lib/

# restart server
echo "Restarting glassfish"
$ASADMIN_CMD stop-domain ${CDB_DOMAIN}
$ASADMIN_CMD start-domain ${CDB_DOMAIN}

# create JDBC connection pool
echo "Creating JDBC connection pool $CDB_DB_POOL"
$ASADMIN_CMD create-jdbc-connection-pool --datasourceclassname com.mysql.jdbc.jdbc2.optional.MysqlDataSource --restype javax.sql.DataSource --property user=${CDB_DB_USER}:password=${CDB_DB_PASSWORD}:driverClass="com.mysql.jdbc.Driver":portNumber=${CDB_DB_PORT}:databaseName=${CDB_DB_NAME}:serverName=${CDB_DB_HOST}:url="jdbc\:mysql\://${CDB_DB_HOST}\:${CDB_DB_PORT}/${CDB_DB_NAME}?zeroDateTimeBehavior\=convertToNull" ${CDB_DB_POOL}

# create JDBC resource associated with this connection pool
echo "Creating JDBC resource $CDB_DATA_SOURCE"
$ASADMIN_CMD create-jdbc-resource --connectionpoolid ${CDB_DB_POOL} ${CDB_DATA_SOURCE}

# test the connection settings
echo "Testing connection"
$ASADMIN_CMD ping-connection-pool $CDB_DB_POOL || exit 1
