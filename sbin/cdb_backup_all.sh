#!/bin/bash

# Copyright (c) UChicago Argonne, LLC. All rights reserved.
# See LICENSE file.


#
# Script used for backing up CDB database + web app
# Deployment configuration can be set in etc/$CDB_CDB_DB_NAME.deploy.conf file
#
# Usage:
#
# $0 [CDB_DB_NAME [CDB_BACKUP_DIR]]
#

CDB_DB_NAME=cdb

CURRENT_DIR=`pwd`
MY_DIR=`dirname $0` && cd $MY_DIR && MY_DIR=`pwd`

$MY_DIR/cdb_backup_db.sh $1 $2 || exit 1

cd $CURRENT_DIR
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
if [ ! -z "$1" ]; then
    CDB_DB_NAME=$1
fi

timestamp=`date +%Y%m%d`
CDB_BACKUP_DIR=$2
if [ -z $CDB_BACKUP_DIR ]; then
    CDB_BACKUP_DIR=$CDB_INSTALL_DIR/backup/$CDB_DB_NAME/$timestamp
fi

# Backup web app
echo "Backing up $CDB_DB_NAME web app"
# Find the last version already backed up.
CDB_WAR_BACKUP_DIR=$CDB_INSTALL_DIR/backup/$CDB_DB_NAME/deployments
if [ ! -d $CDB_WAR_BACKUP_DIR ]; then
    mkdir -p $CDB_WAR_BACKUP_DIR
fi

CDB_GLASSFISH_WAR_FILE_PATH="$CDB_SUPPORT_DIR/payara/linux-x86_64/glassfish/domains/production/applications/__internal/$CDB_DB_NAME/$CDB_DB_NAME.war"
if [ ! -f $CDB_GLASSFISH_WAR_FILE_PATH ]; then
    >&2 echo "Error: file $CDB_GLASSFISH_WAR_FILE_PATH was not found"
    exit 1
fi

createNewDeploymentBackupFile=false
lastAddedWarFileName=`ls -t $CDB_WAR_BACKUP_DIR | head -1`
lastAddedWarFilePath=$CDB_WAR_BACKUP_DIR/$lastAddedWarFileName

if [ -z "$lastAddedWarFileName" ]; then
    createNewDeploymentBackupFile=true
else
    # Compare hash of latest saved war deployment file
    currentWarHash=`md5sum $CDB_GLASSFISH_WAR_FILE_PATH | awk '{print $1}'`
    lastAddedWarFile=`ls -t $CDB_WAR_BACKUP_DIR | head -1`

    lastAddedWarHash=`md5sum $lastAddedWarFilePath | awk '{print $1}'`

    if [ "$currentWarHash" != "$lastAddedWarHash" ]; then
	createNewDeploymentBackupFile=true
    fi
fi

getNextBackupCounter() {
    pathUpToCounter=$1

    fileBaseName=`basename $pathUpToCounter`
    lastPath=`ls -t $pathUpToCounter* | head -1 | grep $fileBaseName-`

    if [ -z $lastPath ]; then
	return 1
    else
	lastFileName=`basename $lastPath`
	lastFileBaseName=`echo "$lastFileName" | awk 'BEGIN { FS = "." } ; { print $1 }'`
	lastBackupCount=`echo $lastFileBaseName  | awk 'BEGIN { FS = "-" } ; { print $3 }'`
	return $(($lastBackupCount + 1))
    fi

}

if $createNewDeploymentBackupFile ; then
    deploymentBackupFileName="$CDB_DB_NAME-$timestamp.war"

    # Check if the file with standard name was already created.
    if [ -f "$CDB_WAR_BACKUP_DIR/$deploymentBackupFileName" ]; then
	deploymentBackupFileBaseName=`echo "$deploymentBackupFileName" | awk 'BEGIN { FS = "." } ; { print $1 }'`

	getNextBackupCounter "$CDB_WAR_BACKUP_DIR/$deploymentBackupFileBaseName"

	backupCounter=$?
	deploymentBackupFileName="$deploymentBackupFileBaseName-$backupCounter.war"
    fi

    deploymentBackupFilePath="$CDB_WAR_BACKUP_DIR/$deploymentBackupFileName"
    echo "Creating new war backup: $deploymentBackupFilePath"
    rsync -arlvP $CDB_GLASSFISH_WAR_FILE_PATH $deploymentBackupFilePath
fi

# Create Symbolic link to the backup with a relative path this way the backup directory is portable.
lastAddedWarFilePath=$CDB_WAR_BACKUP_DIR/`ls -t "$CDB_WAR_BACKUP_DIR" | head -1`
backupLinkFileName="deployment-$CDB_DB_NAME.war"
backupLinkFilePath="$CDB_BACKUP_DIR/deployment-$CDB_DB_NAME.war"

if [ -f "$CDB_BACKUP_DIR/$backupLinkFileName" ]; then
    backupLinkFileBaseName=`echo "$backupLinkFileName" | awk 'BEGIN { FS = "." } ; { print $1 }'`
    lastBackupLinkFilePath=`ls -t $CDB_BACKUP_DIR/$backupLinkFileBaseName* | head -1`

    linkHash=`md5sum $lastBackupLinkFilePath | awk '{print $1}'`
    lastAddedWarHash=`md5sum $lastAddedWarFilePath | awk '{print $1}'`

    if [ "$linkHash" == "$lastAddedWarHash" ]; then
	backupLinkFilePath=$lastBackupLinkFilePath
    else
	getNextBackupCounter "$CDB_BACKUP_DIR/$backupLinkFileBaseName"
	backupCounter=$?
	backupLinkFileName="$backupLinkFileBaseName-$backupCounter.war"
    fi
fi

backupLinkFilePath=$CDB_BACKUP_DIR/$backupLinkFileName

if [ ! -f $backupLinkFilePath ]; then
    lastAddedWarFileName=`basename $lastAddedWarFilePath`
    echo "Creating new deployment backup link: $backupLinkFilePath -> $lastAddedWarFileName"
    ln -s ../deployments/$lastAddedWarFileName $backupLinkFilePath
fi

echo "Backup of $CDB_DB_NAME is done."
