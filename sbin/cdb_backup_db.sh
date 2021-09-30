#!/bin/bash

# Copyright (c) UChicago Argonne, LLC. All rights reserved.
# See LICENSE file.


#
# Script used for backing up CDB database
# Deployment configuration can be set in etc/$CDB_CDB_DB_NAME.deploy.conf file
#
# Usage:
#
# $0 [CDB_DB_NAME [CDB_BACKUP_DIR]]
#

CDB_DB_NAME=cdb
CDB_DB_OWNER=cdb
CDB_DB_OWNER_PASSWORD=cdb
CDB_DB_HOST=127.0.0.1
CDB_DB_PORT=3306
CDB_DB_ADMIN_USER=root
CDB_DB_ADMIN_HOSTS="127.0.0.1 bluegill1.aps.anl.gov gaeaimac.aps.anl.gov visa%.aps.anl.gov"
CDB_DB_ADMIN_PASSWORD=
CDB_DB_CHARACTER_SET=utf8

CURRENT_DIR=`pwd`
MY_DIR=`dirname $0` && cd $MY_DIR && MY_DIR=`pwd`
cd $CURRENT_DIR
if [ -z "${CDB_ROOT_DIR}" ]; then
    CDB_ROOT_DIR=$MY_DIR/..
fi
CDB_SQL_DIR=$CDB_ROOT_DIR/db/sql/cdb
CDB_ENV_FILE=${CDB_ROOT_DIR}/setup.sh
if [ ! -f ${CDB_ENV_FILE} ]; then
    echo "Environment file ${CDB_ENV_FILE} does not exist."
    exit 2
fi
. ${CDB_ENV_FILE} > /dev/null

# Use first argument as db name, if provided
if [ ! -z "$1" ]; then
    CDB_DB_NAME=$1
fi
echo "Backing up $CDB_DB_NAME"

# Determine run directory
if [ -z "${CDB_INSTALL_DIR}" ]; then
    CDB_INSTALL_DIR=$CDB_ROOT_DIR/..
fi

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

# Second argument overrides directory with db population scripts
#timestamp=`date +%Y%m%d.%H%M%S`
timestamp=`date +%Y%m%d`
CDB_BACKUP_DIR=$2
if [ -z $CDB_BACKUP_DIR ]; then
    CDB_BACKUP_DIR=$CDB_INSTALL_DIR/backup/$CDB_DB_NAME/$timestamp
fi
backupFile=${CDB_DB_NAME}.backup.$timestamp.sql
fullBackupFilePath=$CDB_BACKUP_DIR/$backupFile

# Check for database passwd file
databasePasswdFile=$CDB_INSTALL_DIR/etc/$CDB_DB_NAME.db.passwd
if [ -f $databasePasswdFile ]; then
    CDB_DB_USER_PASSWORD=`cat $databasePasswdFile`
else
    if [ -t 0 ]; then
	read -s -p "Enter MySQL $CDB_DB_NAME password: " CDB_DB_USER_PASSWORD
    else
	# Script is not runnig in an interactive shell
	# User cannot be prompted for password
	>&2 echo "ERROR: $databasePasswdFile does not exist"
	exit 1
    fi
fi

if [ -z $CDB_DB_USER_PASSWORD ]; then
    >&2 echo "ERROR: password provided is blank"
    exit 1
fi

mysqlCmd="mysqldump --port=$CDB_DB_PORT --host=$CDB_DB_HOST -u $CDB_DB_NAME -p$CDB_DB_USER_PASSWORD"

mysqlCmd="$mysqlCmd $CDB_DB_NAME"

echo
echo
echo "Using DB backup directory: $CDB_BACKUP_DIR"

mkdir -p $CDB_BACKUP_DIR
$mysqlCmd > $fullBackupFilePath || exit 1

nTableLocks=`grep -n LOCK $fullBackupFilePath | grep WRITE | wc -l`
echo "Processing $nTableLocks table locks"

lockCnt=0
processingFile=$CDB_BACKUP_DIR/process.txt
while [ $lockCnt -lt $nTableLocks ]; do
    lockCnt=`expr $lockCnt + 1`
    headLine=`expr $lockCnt \* 2`
    tailLine=2
    echo "Working on table lock #: $lockCnt"
    grep -n "LOCK TABLES" $fullBackupFilePath | head -$headLine | tail -$tailLine > $processingFile
    dbTable=`cat $processingFile | head -1 | awk '{print $3}' | sed 's?\`??g'`
    firstLine=`cat $processingFile | head -1 | cut -f1 -d':'`
    lastLine=`cat $processingFile | tail -1 | cut -f1 -d':'`
    echo "Creating sql script for $dbTable"
    targetFile=$CDB_BACKUP_DIR/populate_$dbTable.sql
    cat $fullBackupFilePath | sed -n ${firstLine},${lastLine}p > $targetFile
    cat $targetFile | sed 's?VALUES ?VALUES\n?g' | sed 's?),(?),\n(?g' > $targetFile.2 && mv $targetFile.2 $targetFile
done
rm -f $processingFile

echo "Backup of $CDB_DB_NAME is done."
