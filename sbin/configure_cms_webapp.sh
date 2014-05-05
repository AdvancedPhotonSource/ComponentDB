#!/bin/sh

MY_DIR=`dirname $0` && cd $MY_DIR && MY_DIR=`pwd`
if [ -z "${CMS_ROOT_DIR}" ]; then
    CMS_ROOT_DIR=$MY_DIR/..
fi
CMS_ENV_FILE=${CMS_ROOT_DIR}/setup.sh
if [ ! -f ${CMS_ENV_FILE} ]; then
    echo "Environment file ${CMS_ENV_FILE} does not exist." 
    exit 2
fi
. ${CMS_ENV_FILE} > /dev/null

# Use first argument as db name, if provided
if [ ! -z "$1" ]; then
    DB_NAME=$1
fi
echo "Using DB name: $DB_NAME"

# Look for deployment file in etc directory, and use it to override
# default entries
deployConfigFile=$CMS_ROOT_DIR/etc/${DB_NAME}.deploy.conf
if [ -f $deployConfigFile ]; then
    echo "Using deployment config file: $deployConfigFile"
    . $deployConfigFile
else
    echo "Deployment config file $deployConfigFile not found, using defaults"
fi

CMS_HOST_ARCH=`uname | tr [A-Z] [a-z]`-`uname -m`
GLASSFISH_DIR=$CMS_SUPPORT/glassfish/$CMS_HOST_ARCH
JAVA_HOME=$CMS_SUPPORT/java/$CMS_HOST_ARCH

export AS_JAVA=$JAVA_HOME
ASADMIN_CMD=$GLASSFISH_DIR/bin/asadmin 

CMS_DB_NAME=${DB_NAME:=cms}
CMS_DB_HOST=${DB_HOST:=localhost}
CMS_DB_PORT=${DB_PORT:=3306}
CMS_DB_USER=${DB_USER:=cms}
CMS_DB_PASSWORD=${DB_PASSWORD:=cms}
CMS_DB_POOL=mysql_${DB_NAME}_DbPool
CMS_DATA_SOURCE=${DB_NAME}_DataSource
CMS_DOMAIN=domain1

# copy mysql driver
echo "Copying mysql driver"
rsync -ar $CMS_ROOT_DIR/src/java/CmsWebPortal/lib/mysql-connector-java-5.1.23-bin.jar $GLASSFISH_DIR/glassfish/domains/${CMS_DOMAIN}/lib/ext

# restart server
echo "Restarting glassfish"
$ASADMIN_CMD stop-domain ${CMS_DOMAIN}
$ASADMIN_CMD start-domain ${CMS_DOMAIN}

# create a JDBC connection pool
echo "Creating JDBC connection pool"
$ASADMIN_CMD create-jdbc-connection-pool --datasourceclassname com.mysql.jdbc.jdbc2.optional.MysqlDataSource --restype javax.sql.DataSource --property user=${CMS_DB_USER}:password=${CMS_DB_PASSWORD}:driverClass="com.mysql.jdbc.Driver":portNumber=${CMS_DB_PORT}:databaseName=${CMS_DB_NAME}:serverName=${CMS_DB_HOST}:url="jdbc\:mysql\://${CMS_DB_HOST}\:${CMS_DB_PORT}/${CMS_DB_NAME}?zeroDateTimeBehavior\=convertToNull" ${CMS_DB_POOL}

# create a JDBC resource associated with this connection pool
echo "Creating JDBC resource"
$ASADMIN_CMD create-jdbc-resource --connectionpoolid ${CMS_DB_POOL} ${CMS_DATA_SOURCE}

# test the connection settings 
echo "Testing connection"
$ASADMIN_CMD ping-connection-pool $CMS_DB_POOL || exit 1

