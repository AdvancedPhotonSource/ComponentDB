#!/bin/sh

DB_NAME=cms
DB_OWNER=cms
DB_OWNER_PASSWORD=cms
DB_HOST=127.0.0.1
DB_PORT=3306
DB_ADMIN_USER=root
DB_ADMIN_HOSTS="127.0.0.1 bluegill1.aps.anl.gov gaeaimac.aps.anl.gov visa%.aps.anl.gov"
DB_ADMIN_PASSWORD=
DB_CHARACTER_SET=utf8


MY_DIR=`dirname $0` && cd $MY_DIR && MY_DIR=`pwd`
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

# Read password
sttyOrig=`stty -g`
stty -echo
read -p "Enter MySQL root password: " DB_ADMIN_PASSWORD
stty $sttyOrig

mysqlCmd="mysqldump --port=$DB_PORT --host=$DB_HOST -u $DB_ADMIN_USER"
if [ ! -z "$DB_ADMIN_PASSWORD" ]; then
    mysqlCmd="$mysqlCmd -p$DB_ADMIN_PASSWORD"
fi

execute() {
    msg="$@"
    if [ ! -z "$DB_ADMIN_PASSWORD" ]; then
        sedCmd="s?$DB_ADMIN_PASSWORD?\\*\\*\\*\\*\\*\\*?g"
        echo "Executing: $@" | sed -e $sedCmd
    else
        echo "Executing: $@"
    fi
    eval "$@"
}

mysqlCmd="$mysqlCmd $DB_NAME"

timestamp=`date +%Y%m%d.%H%M%S`
backupDir=/tmp/cms_db_backup.$timestamp
backupFile=cms_db_backup.$timestamp.sql
fullBackupFilePath=$backupDir/$backupFile

echo
echo
echo "Using backup directory: $backupDir"

mkdir -p $backupDir
$mysqlCmd > $fullBackupFilePath

nTableLocks=`grep -n LOCK $fullBackupFilePath | grep WRITE | wc -l`
echo "Processing $nTableLocks table locks"

lockCnt=0
processingFile=$backupDir/process.txt
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
    cat $fullBackupFilePath | sed -n ${firstLine},${lastLine}p > $backupDir/populate_$dbTable.sql
done
rm -f $processingFile



