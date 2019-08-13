#!/bin/sh

#
# Script used for updating scaled portal images (used for thumbnails and gallery) for images and viewable documents.
#
# Usage:
#
# $0 [CDB_DB_NAME | CDB_DATA_DIR]
#

MY_DIR=`dirname $0` && cd $MY_DIR && MY_DIR=`pwd`
if [ -z "${CDB_ROOT_DIR}" ]; then
    CDB_ROOT_DIR=$MY_DIR/..
fi
CDB_ENV_FILE=${CDB_ROOT_DIR}/setup.sh
if [ ! -f ${CDB_ENV_FILE} ]; then
    >&2 echo "Environment file ${CDB_ENV_FILE} does not exist." 
    exit 1
fi
. ${CDB_ENV_FILE} > /dev/null

# Unset custom varaibles that need to still be set from ENV_FILE. 
unset CDB_DB_NAME
unset CDB_DATA_DIR

# Use first argument as db name or data directory.
if [ ! -z "$1" ]; then
    if [ -d "$1" ]; then
	CDB_DATA_DIR=$1
    else
    	CDB_DB_NAME=$1
    fi
else 
    >&2 echo "No databse or data directory provided, please provide the database or data directory and try again"
    echo "USAGE: $0 [CDB_DB_NAME | CDB_DATA_DIR]"
    exit 1
fi

if [ -z $CDB_DATA_DIR ]; then 
    echo "Using DB name: $CDB_DB_NAME"

    # Look for deployment file in etc directory, and use it to override
    # default entries

    deployConfigFile=$CDB_ROOT_DIR/etc/${CDB_DB_NAME}.deploy.conf
    if [ -f $deployConfigFile ]; then
	echo "Using deployment config file: $deployConfigFile"
	. $deployConfigFile
    else
	>&2 echo "Deployment config file $deployConfigFile not found, exiting."
	exit 1
    fi

    if [ ! -d $CDB_DATA_DIR ]; then
	>&2 echo "Data directory: $CDB_DATA_DIR does not exist, exiting."
	exit 1
    fi
fi

CDB_BUILD_WEB_INF_DIR=$CDB_ROOT_DIR/src/java/CdbWebPortal/build/web/WEB-INF

if [ ! -d "$CDB_BUILD_WEB_INF_DIR" ]; then
    >&2 echo "Build directory: $CDB_BUILD_WEB_INF_DIR was not found, please build the portal and try again" 
    exit 1
fi

echo "Using data directory: $CDB_DATA_DIR" 
echo 
read -p "Would you like to backup the data directory before continuing? [Y/n] " backupDataDir 
if [ -z $backupDataDir ]; then
    backupDataDir="y"
fi

if [ $backupDataDir == "y" -o $backupDataDir == "Y" ]; then
    if [ -z $CDB_DB_NAME ]; then 
	read -p "Please enter the deployment database for the entered data directory [cdb]: " CDB_DB_NAME
	if [ -z $CDB_DB_NAME ]; then
	    CDB_DB_NAME="cdb"
	fi
    fi
    CDB_BACKUP_DATA_DIR=$CDB_INSTALL_DIR/backup/$CDB_DB_NAME/data
    echo ""
    echo "Using backup directory: $CDB_BACKUP_DATA_DIR"
    read -p "Proceed? [Y/n]: " proceedBackup

    if [ -z $proceedBackup ]; then
	proceedBackup="y"
    fi

    if [ $proceedBackup == "y" -o $proceedBackup == "Y" ]; then
	echo "Backing up" 

	if [ ! -d $CDB_BACKUP_DATA_DIR ]; then
	    echo "Creating Directory: $CDB_BACKUP_DATA_DIR"
	    mkdir -p $CDB_BACKUP_DATA_DIR
	fi

	timestamp=`date +%Y%m%d-%H%M%S`
	backupName="$timestamp.tgz"
	backupCommand="tar -czvf $CDB_BACKUP_DATA_DIR/$backupName $CDB_DATA_DIR"
	echo "Executing: $backupCommand"
	eval $backupCommand
    else
	echo "Exiting script" 
	exit
    fi  
fi

javaCmd="java -cp $CDB_BUILD_WEB_INF_DIR/classes:$CDB_BUILD_WEB_INF_DIR/lib/* gov.anl.aps.cdb.portal.utilities.GalleryUtility $CDB_DATA_DIR"

echo "Executing: $javaCmd"
eval $javaCmd