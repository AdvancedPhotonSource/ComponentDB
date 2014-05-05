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
CMS_DB_NAME=${CMS_DB_NAME:=cms}
if [ ! -z "$1" ]; then
    CMS_DB_NAME=$1
fi
echo "Using DB name: $CMS_DB_NAME"

# Look for deployment file in etc directory, and use it to override
# default entries
deployConfigFile=$CMS_ROOT_DIR/etc/${CMS_DB_NAME}.deploy.conf
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

CMS_DB_POOL=mysql_${CMS_DB_NAME}_DbPool
CMS_DATA_SOURCE=${CMS_DB_NAME}_DataSource
CMS_DOMAIN=domain1

# restart server
echo "Restarting glassfish"
$ASADMIN_CMD stop-domain ${CMS_DOMAIN}
$ASADMIN_CMD start-domain ${CMS_DOMAIN}

# delete JDBC resource associated with this connection pool
echo "Deleting JDBC resource $CMS_DATA_SOURCE"
$ASADMIN_CMD delete-jdbc-resource ${CMS_DATA_SOURCE}

# delete JDBC connection pool
echo "Deleting JDBC connection pool $CMS_DB_POOL"
$ASADMIN_CMD delete-jdbc-connection-pool ${CMS_DB_POOL}


