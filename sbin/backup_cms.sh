#!/bin/sh

#
# Script used for backing up CMS database
# Deployment configuration can be set in etc/$CMS_CMS_DB_NAME.deploy.conf file
#
# Usage:
#
# $0 [CMS_DB_NAME [CMS_BACKUP_DIR]]
#

CMS_DB_NAME=cms
CMS_DB_OWNER=cms
CMS_DB_OWNER_PASSWORD=cms
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
    exit 2
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
timestamp=`date +%Y%m%d.%H%M%S`
CMS_BACKUP_DIR=$2
if [ -z $CMS_BACKUP_DIR ]; then
    CMS_BACKUP_DIR=/tmp/$CMS_DB_NAME.backup.$timestamp
fi
backupFile=$CMS_DB_NAME.backup.$timestamp.sql
fullBackupFilePath=$CMS_BACKUP_DIR/$backupFile

# Read password
sttyOrig=`stty -g`
stty -echo
read -p "Enter MySQL root password: " CMS_DB_ADMIN_PASSWORD
stty $sttyOrig

mysqlCmd="mysqldump --port=$CMS_DB_PORT --host=$CMS_DB_HOST -u $CMS_DB_ADMIN_USER"
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

mysqlCmd="$mysqlCmd $CMS_DB_NAME"

echo
echo
echo "Using backup directory: $CMS_BACKUP_DIR"

mkdir -p $CMS_BACKUP_DIR

# Backup web app
rsync -arlvP $CMS_SUPPORT/glassfish/linux-x86_64/glassfish/domains/domain1/autodeploy/$CMS_DB_NAME.war $CMS_BACKUP_DIR

# Backup DB
$mysqlCmd > $fullBackupFilePath

nTableLocks=`grep -n LOCK $fullBackupFilePath | grep WRITE | wc -l`
echo "Processing $nTableLocks table locks"

lockCnt=0
processingFile=$CMS_BACKUP_DIR/process.txt
while [ $lockCnt -lt $nTableLocks ]; do
    lockCnt=`expr $lockCnt + 1`
    headLine=`expr $lockCnt \* 2`
    tailLine=2
    echo "Working on table lock #: $lockCnt" 
    grep -n LOCK $fullBackupFilePath | head -$headLine | tail -$tailLine > $processingFile
    dbTable=`cat $processingFile | head -1 | awk '{print $3}' | sed 's?\`??g'`
    firstLine=`cat $processingFile | head -1 | cut -f1 -d':'`
    lastLine=`cat $processingFile | tail -1 | cut -f1 -d':'`
    echo "Creating sql script for $dbTable"
    cat $fullBackupFilePath | sed -n ${firstLine},${lastLine}p > $CMS_BACKUP_DIR/populate_$dbTable.sql
done
rm -f $processingFile



