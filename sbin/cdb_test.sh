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

$MY_DIR/cdb_backup_db.sh

timestamp=`date +%Y%m%d`
CDB_BACKUP_DIR=$CDB_INSTALL_DIR/backup/cdb/$timestamp

yes $db_password | $MY_DIR/cdb_create_db.sh cdb $CDB_DIST_DIR/db/sql/test || exit 1

# Regenerate the API with current code base
API_CLIENT_PATH=$CDB_DIST_DIR/tools/developer_tools/python-client/
cd $API_CLIENT_PATH
./generatePyClient.sh http://localhost:8080/cdb

HEADER_TEXT='****************'
PRINTF_HEADER="\n\n$HEADER_TEXT Starting %s Test $HEADER_TEXT\n\n\n"

printf "$PRINTF_HEADER" "Unit Tests"
cd $CDB_DIST_DIR/tools/developer_tools/code_testing/CdbWebPortalTest
mvn test

printf "$PRINTF_HEADER" "API"
cd $API_CLIENT_PATH/test
pytest api_test.py

printf "$PRINTF_HEADER" "Selenium"
cd $CDB_DIST_DIR/tools/developer_tools/portal_testing/PythonSeleniumTest

if [ ! -d "support_bin" ]; then
    mkdir support_bin
fi

cd support_bin

skiptest=0
needs_download=1

chromedriver_version_start=`google-chrome --version | grep -oP "[^A-Z ][0-9]+.[0-9]+.[0-9]+"`
chromedriver_version_regex="$chromedriver_version_start.[0-9]+/chromedriver_linux64.zip"
chromedriver_version=`curl https://chromedriver.storage.googleapis.com/ | grep -oP $chromedriver_version_regex`

if [ -f "chromedriver" ]; then 
    current_version_result=`./chromedriver --version | grep -oP $chromedriver_version`
    if [[ -z "$current_version_result" ]]; then        
        rm chromedriver
    else
        needs_download=0        
    fi
fi

if [[ $needs_download == 1 ]]; then
    wget https://chromedriver.storage.googleapis.com/$chromedriver_version || skiptest=1
    unzip chromedriver_linux64.zip
    rm chromedriver_linux64.zip
fi


if [[ $skiptest == 0 ]]; then    
    export PATH=`pwd`:$PATH
    cd ..
    pytest gui_test.py
else
    echo "Error finding required chromedriver" 
fi

printf "\n\nPress enter to proceed restoring original database back to cdb..."
read -s 

yes $db_password | $MY_DIR/cdb_create_db.sh cdb $CDB_BACKUP_DIR

cd $CURRENT_DIR