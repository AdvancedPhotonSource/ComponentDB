#!/bin/sh

# Copyright (c) UChicago Argonne, LLC. All rights reserved.
# See LICENSE file.


#
# Script used for deploying CDB MySQL DB Server
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
CDB_SHORT_HOSTNAME=`hostname -s`
CDB_SUPPORT_DIR=${CDB_SUPPORT_DIR:=$CDB_INSTALL_DIR/support-$CDB_SHORT_HOSTNAME}
CDB_ETC_DIR=${CDB_INSTALL_DIR}/etc
CDB_LOG_DIR=${CDB_INSTALL_DIR}/var/log
CDB_MYSQLD_INIT_CMD=$CDB_ROOT_DIR/etc/init.d/cdb-mysqld
CDB_MYSQLD_CONFIG_FILE=$CDB_ETC_DIR/mysql.conf

echo "CDB install directory: $CDB_INSTALL_DIR"

mkdir -p $CDB_ETC_DIR
mkdir -p $CDB_LOG_DIR

echo "Checking service configuration file"
setRootPassword=false
if [ ! -f $CDB_MYSQLD_CONFIG_FILE ]; then
    echo "Generating service config file"
    cmd="cat $CDB_ROOT_DIR/etc/mysql.conf.template \
        | sed 's?CDB_INSTALL_DIR?$CDB_INSTALL_DIR?g' \
        | sed 's?CDB_DB_HOST?$CDB_DB_HOST?g' \
        | sed 's?CDB_DB_PORT?$CDB_DB_PORT?g' \
        > $CDB_MYSQLD_CONFIG_FILE"
    eval $cmd || exit 1
    setRootPassword=true
else
    echo "Service config file exists"
fi

echo "Restarting mysqld service"
$CDB_MYSQLD_INIT_CMD restart 

if [ $setRootPassword = "true" ]; then
    if [ -z "$CDB_DB_ADMIN_PASSWORD" ]; then
        sttyOrig=`stty -g`
        stty -echo
        read -p "Enter DB root password: " CDB_DB_ADMIN_PASSWORD
        stty $sttyOrig
        echo
    fi
    echo "Setting DB root password"
    cmd="echo \"SET PASSWORD FOR 'root'@'localhost' = PASSWORD('$CDB_DB_ADMIN_PASSWORD');\" | $CDB_SUPPORT_DIR/mysql/$CDB_HOST_ARCH/bin/mysql -u root -h $CDB_DB_HOST" 
    eval $cmd || exit 1
fi

echo "Done deploying mysqld service"

