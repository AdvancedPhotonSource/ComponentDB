#!/bin/sh

#
# Script used for destroying CDB database
# Deployment configuration can be set in etc/$CDB_DB_NAME.deploy.conf file
#
# Usage:
#
# $0 [CDB_DB_NAME [CDB_DB_ADMIN_PASSWORD]]
#

CDB_DB_HOST=127.0.0.1
CDB_DB_PORT=3306
CDB_DB_ADMIN_USER=root
CDB_DB_ADMIN_HOSTS="127.0.0.1 bluegill1.aps.anl.gov gaeaimac.aps.anl.gov visa%.aps.anl.gov"
CDB_DB_ADMIN_PASSWORD=
CDB_DB_CHARACTER_SET=utf8

CURRENT_DIR=`pwd`
MY_DIR=`dirname $0` && cd $MY_DIR && MY_DIR=`pwd`
cd $CURRENT_DIR

# Use first argument as db name, if provided
if [ ! -z "$1" ]; then
    CDB_DB_NAME=$1
    CDB_DB_USER=$1
    CDB_DB_PASSWORD=$1
else
    echo 'database name must be provided'
    echo "Usage: $0 [CDB_DB_NAME [CDB_DB_ADMIN_PASSWORD]]"
    exit 1
fi

echo "Using DB name: $CDB_DB_NAME will be dropped along with its users."

# Read password if needed
if [ -z "$CDB_DB_ADMIN_PASSWORD" ]; then
    if [ ! -z $2 ]; then
        CDB_DB_ADMIN_PASSWORD=$2
    else
        sttyOrig=`stty -g`
        stty -echo
        read -p "Enter DB root password: " CDB_DB_ADMIN_PASSWORD
        stty $sttyOrig
        echo
    fi
fi

mysqlCmd="mysql --port=$CDB_DB_PORT --host=$CDB_DB_HOST -u $CDB_DB_ADMIN_USER"
if [ ! -z "$CDB_DB_ADMIN_PASSWORD" ]; then
    mysqlCmd="$mysqlCmd -p$CDB_DB_ADMIN_PASSWORD"
fi

execute() {
    msg="$@"
    if [ ! -z "$CDB_DB_ADMIN_PASSWORD" ]; then
        sedCmd="s?$CDB_DB_ADMIN_PASSWORD?\\*\\*\\*\\*\\*\\*?g"
        echo "Executing: $@" | sed -e $sedCmd
    else
        echo "Executing: $@"
    fi
    if eval "$@"; then
	    echo 'Success'
    else
	    exit 1
    fi
}

# destroy db
sqlFile=/tmp/drop_cdb_db.`id -u`.sql
rm -f $sqlFile
echo "DROP DATABASE IF EXISTS $CDB_DB_NAME;" >> $sqlFile
for host in $CDB_DB_ADMIN_HOSTS; do
    echo "DROP USER $CDB_DB_USER@'$host';" >> $sqlFile
done
execute "$mysqlCmd < $sqlFile"

