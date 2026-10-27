#!/bin/bash

# Copyright (c) UChicago Argonne, LLC. All rights reserved.
# See LICENSE file.

#
# Script to run tests for the CDB Portal and API
# Backups the current database, deploys a test database, runs tests and restores the backup database. 
# To be used only within a development enviornment with CDB running in payara. 
#
# Usage:
#
# $0
#

echo -n Database root password: 
read -s db_password

CURRENT_DIR=`pwd`
MY_DIR=`dirname $0` && cd $MY_DIR && MY_DIR=`pwd`
cd $MY_DIR/..
CDB_DIST_DIR=`pwd`
cd $CURRENT_DIR

printf "\nBack up the current database and restore it after testing? [Y/n] "
read -r backup_response

backup_database=1
case "$backup_response" in
    [Nn]*) backup_database=0 ;;
    *)
        $MY_DIR/cdb_backup_db.sh
        timestamp=`date +%Y%m%d`
        CDB_BACKUP_DIR=$CDB_INSTALL_DIR/backup/cdb/$timestamp
        ;;
esac

yes $db_password | $MY_DIR/cdb_create_db.sh cdb $CDB_DIST_DIR/db/sql/test || exit 1

printf "\nRestart Payara so it uses the newly deployed test database, then press enter to continue..."
read -r

# Regenerate the API with current code base
API_CLIENT_PATH=$CDB_DIST_DIR/tools/developer_tools/python-client
export PYTHONPATH="$API_CLIENT_PATH/packages/api:$API_CLIENT_PATH/packages/cli${PYTHONPATH:+:$PYTHONPATH}"
cd $API_CLIENT_PATH
./generatePyClient.sh http://localhost:8080/cdb

HEADER_TEXT='****************'
PRINTF_HEADER="\n\n$HEADER_TEXT Starting %s Test $HEADER_TEXT\n\n\n"

printf "$PRINTF_HEADER" "Unit Tests"
cd $CDB_DIST_DIR/tools/developer_tools/code_testing/CdbWebPortalTest
mvn test

printf "$PRINTF_HEADER" "API"
cd $API_CLIENT_PATH
pytest test/

printf "$PRINTF_HEADER" "Selenium"
cd $CDB_DIST_DIR/tools/developer_tools/portal_testing/PythonSeleniumTest

# Selenium Manager resolves and downloads a compatible driver when Chrome starts.
pytest tests/ -n auto --dist loadfile --reruns 1 --reruns-delay 1 -rR

if [[ $backup_database == 1 ]]; then
    printf "\n\nRestore the original database from the backup? [Y/n] "
    read -r restore_response
    case "$restore_response" in
        [Nn]*) printf "The test database remains installed as cdb.\n" ;;
        *) yes $db_password | $MY_DIR/cdb_create_db.sh cdb $CDB_BACKUP_DIR ;;
    esac
else
    printf "\n\nBackup was skipped; the test database remains installed as cdb.\n"
fi

cd $CURRENT_DIR
