#!/bin/sh

#
# Script used for creating CMS database
# Deployment configuration can be set in etc/$CMS_DB_NAME.deploy.conf file
#
# Usage:
#
# $0 [CMS_DB_NAME [CMS_DB_SCRIPTS_DIR]]
#

CMS_DB_NAME=cms
CMS_DB_USER=cms
CMS_DB_PASSWORD=cms
CMS_DB_HOST=127.0.0.1
CMS_DB_PORT=3306
CMS_DB_ADMIN_USER=root
CMS_DB_ADMIN_HOSTS="127.0.0.1 bluegill1.aps.anl.gov gaeaimac.aps.anl.gov visa%.aps.anl.gov"
CMS_DB_ADMIN_PASSWORD=
CMS_DB_CHARACTER_SET=utf8

CURRENT_DIR=`pwd`
MY_DIR=`dirname $0` && cd $MY_DIR && MY_DIR=`pwd`
cd $CURRENT_DIR

if [ -z "${CMS_ROOT_DIR}" ]; then
    CMS_ROOT_DIR=$MY_DIR/..
fi
CMS_SQL_DIR=$CMS_ROOT_DIR/db/sql/cms
CMS_ENV_FILE=${CMS_ROOT_DIR}/setup.sh
if [ ! -f ${CMS_ENV_FILE} ]; then
    echo "Environment file ${CMS_ENV_FILE} does not exist." 
    exit 1
fi
. ${CMS_ENV_FILE} > /dev/null 

# Use first argument as db name, if provided
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

# Second argument overrides directory with db population scripts
CMS_DB_SCRIPTS_DIR=${CMS_DB_SCRIPTS_DIR:=$CMS_SQL_DIR}
if [ ! -z "$2" ]; then
    CMS_DB_SCRIPTS_DIR=$2
fi
if [ ! -d $CMS_DB_SCRIPTS_DIR ]; then
    echo "DB Scripts directory $CMS_DB_SCRIPTS_DIR does not exist."
    exit 1
fi

echo "Using DB scripts directory: $CMS_DB_SCRIPTS_DIR"

# Read password if needed
if [ -z "$CMS_DB_ADMIN_PASSWORD" ]; then
    sttyOrig=`stty -g`
    stty -echo
    read -p "Enter MySQL root password: " CMS_DB_ADMIN_PASSWORD
    stty $sttyOrig
fi

mysqlCmd="mysql --port=$CMS_DB_PORT --host=$CMS_DB_HOST -u $CMS_DB_ADMIN_USER"
if [ ! -z "$CMS_DB_ADMIN_PASSWORD" ]; then
    mysqlCmd="$mysqlCmd -p$CMS_DB_ADMIN_PASSWORD"
fi

execute() {
    msg="$@"
    if [ ! -z "$CMS_DB_ADMIN_PASSWORD" ]; then
        sedCmd="s?$CMS_DB_ADMIN_PASSWORD?\\*\\*\\*\\*\\*\\*?g"
        echo "Executing: $@" | sed -e $sedCmd
    else
        echo "Executing: $@"
    fi
    eval "$@"
}

# recreate db
cd $CMS_SQL_DIR
sqlFile=/tmp/create_cms_db.`id -u`.sql
rm -f $sqlFile
echo "DROP DATABASE IF EXISTS $CMS_DB_NAME;" >> $sqlFile
echo "CREATE DATABASE $CMS_DB_NAME CHARACTER SET $CMS_DB_CHARACTER_SET;" >> $sqlFile
for host in $CMS_DB_ADMIN_HOSTS; do
    echo "GRANT ALL PRIVILEGES ON $CMS_DB_NAME.* TO '$CMS_DB_USER'@'$host'
    IDENTIFIED BY '$CMS_DB_PASSWORD';" >> $sqlFile
done
execute "$mysqlCmd < $sqlFile"

# create db tables
mysqlCmd="$mysqlCmd -D $CMS_DB_NAME <"
execute $mysqlCmd create_cms_tables.sql

# populate db
cd $CURRENT_DIR && cd $CMS_DB_SCRIPTS_DIR
CMS_DB_TABLES="\
    setting_type \
    user \
    user_group \
    user_user_group \
    entity_info \
    component_state \
    source \
    property_type \
    resource_type_category \
    connector_type_category \
    connector_type \
    component_type_category \
    component_type \
    resource_type \
    log \
    component \
    component_source \
    component_property \
    component_component_type \
    component_connector \
    component_connector_resource \
    component_log \
    collection \
    collection_component \
    collection_link \
    collection_log \
"
for dbTable in $CMS_DB_TABLES; do
    dbFile=populate_$dbTable.sql
    if [ -f $dbFile ]; then
        echo "Populating $dbTable using $dbFile script"
        execute $mysqlCmd $dbFile
    else
        echo "$dbFile not found, skipping $dbTable update"
    fi
done

# cleanup
execute rm -f $sqlFile
