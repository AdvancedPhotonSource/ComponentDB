#!/bin/sh

DB_NAME=cms
DB_OWNER=cms
DB_OWNER_PASSWORD=cms
DB_HOST=127.0.0.1
DB_PORT=3306
DB_ADMIN_USER=root
DB_ADMIN_PASSWORD=

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

# Read password


sttyOrig=`stty -g`
stty -echo
read -p "Enter MySQL root password: " DB_ADMIN_PASSWORD
stty $sttyOrig

mysqlCmd="mysql --port=$DB_PORT --host=$DB_HOST -u $DB_ADMIN_USER"
if [ ! -z "$DB_ADMIN_PASSWORD" ]; then
    mysqlCmd="$mysqlCmd -p$DB_ADMIN_PASSWORD"
fi

execute() {
    msg="$@"
    if [ ! -z "$DB_ADMIN_PASSWORD" ]; then
        sedCmd="s?$DB_ADMIN_PASSWORD?\\*\\*\\*\\*\\*\\*?g"
        echo $sedCmd
        echo "Executing: $@" | sed -e $sedCmd
    else
        echo "Executing: $@"
    fi
    eval "$@"
}


SQL_DIR=$CMS_ROOT_DIR/db/sql
cd $SQL_DIR

# drop old db
sqlFile=/tmp/create_cms_db.`id -u`.sql
rm -f $sqlFile
echo "drop database if exists $DB_NAME;" >> $sqlFile
echo "create database $DB_NAME;" >> $sqlFile
echo "grant all privileges on $DB_NAME.* to '$DB_OWNER'@'$DB_HOST' identified by '$DB_OWNER_PASSWORD';" >> $sqlFile
execute "$mysqlCmd < $sqlFile"

# create db
mysqlCmd="$mysqlCmd -D $DB_NAME <"
execute $mysqlCmd create_all_tables.sql

# populate db

# Add development rows
execute $mysqlCmd add_development_entries.sql

# cleanup
execute rm -f $sqlFile
