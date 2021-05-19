#!/bin/bash

# Copyright (c) UChicago Argonne, LLC. All rights reserved.
# See LICENSE file.


#
# Script used for deploying CDB web portal
# Deployment configuration can be set in etc/$CDB_DB_NAME.deploy.conf file
#
# Usage:
#
# $0 [CDB_DB_NAME] [MODE]
#
# Options for mode include: Dev

MY_DIR=`dirname $0` && cd $MY_DIR && MY_DIR=`pwd`
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
CDB_DB_NAME=${CDB_DB_NAME:=cdb}
if [ ! -z "$1" ]; then
    CDB_DB_NAME=$1
fi
echo "Using DB name: $CDB_DB_NAME"

# Look for deployment file in etc directory, and use it to override
# default entries
deployConfigFile=$CDB_INSTALL_DIR/etc/${CDB_DB_NAME}.deploy.conf
if [ -f $deployConfigFile ]; then
    echo "Using deployment config file: $deployConfigFile"
    . $deployConfigFile
else
    echo "Deployment config file $deployConfigFile not found, using defaults"
fi

CDB_HOST_ARCH=$(uname -sm | tr -s '[:upper:][:blank:]' '[:lower:][\-]')
CDB_CONTEXT_ROOT=${CDB_CONTEXT_ROOT:=cdb}
CDB_PERM_CONTEXT_ROOT_URL=${CDB_PERM_CONTEXT_ROOT_URL:=http://localhost:8080/cdb}
CDB_DATA_DIR=${CDB_DATA_DIR:=/cdb}
GLASSFISH_DIR=$CDB_SUPPORT_DIR/payara/$CDB_HOST_ARCH
CDB_DIST_DIR=$CDB_ROOT_DIR/src/java/CdbWebPortal/dist
CDB_BUILD_WAR_FILE=CdbWebPortal.war
CDB_WAR_FILE=$CDB_CONTEXT_ROOT.war
JAVA_HOME=$CDB_SUPPORT_DIR/java/$CDB_HOST_ARCH
CDB_WEB_SERVICE_HOST=`hostname -f`
CDB_DATE=`date +%Y.%m.%d`

CDB_REPOSITORY_URL=https://github.com/AdvancedPhotonSource/ComponentDB
CDB_REPOSITORY_MILESTONES_PATH=/milestones
CDB_REPOSITORY_RELEASES_PATH=/releases

CDB_REPOSITORY_FULL_URL="$CDB_REPOSITORY_URL$CDB_REPOSITORY_MILESTONES_PATH"

if [ ! -z $2 ]; then
    DEPLOY_MODE=$2
    if [ $DEPLOY_MODE = "Dev" ]; then
        CDB_SOFTWARE_VERSION="Development Snapshot"
    fi
fi

if [[ -z $CDB_SOFTWARE_VERSION ]]; then
    CDB_SOFTWARE_VERSION=`cat $CDB_ROOT_DIR/etc/version`
    CDB_REPOSITORY_FULL_URL="$CDB_REPOSITORY_URL$CDB_REPOSITORY_RELEASES_PATH"
fi

if [ ! -f $CDB_DIST_DIR/$CDB_BUILD_WAR_FILE ]; then
    echo "$CDB_BUILD_WAR_FILE not found in $CDB_DIST_DIR."
    exit 1
fi

# Create needed data directories
mkdir -p $CDB_DATA_DIR/propertyValue
mkdir -p $CDB_DATA_DIR/log

# Modify war file for proper settings and repackage it into new war
echo "Repackaging war file for context root $CDB_CONTEXT_ROOT"
cd $CDB_DIST_DIR
rm -rf $CDB_CONTEXT_ROOT
mkdir -p $CDB_CONTEXT_ROOT
cd $CDB_CONTEXT_ROOT
jar xf ../$CDB_BUILD_WAR_FILE

configFile=WEB-INF/glassfish-web.xml
cmd="cat $configFile | sed 's?<context-root.*?<context-root>${CDB_CONTEXT_ROOT}</context-root>?g' > $configFile.2 && mv $configFile.2 $configFile"
eval $cmd

webConfigFile=WEB-INF/web.xml
cmd="cat $webConfigFile.template | sed 's?CDB_PROJECT_STAGE?Production?g' > $webConfigFile"
eval $cmd

cmd="cat $configFile | sed 's?dir=.*\"?dir=${CDB_DATA_DIR}\"?g' > $configFile.2 && mv $configFile.2 $configFile"
eval $cmd

configFile=WEB-INF/classes/META-INF/persistence.xml
cmd="cat $configFile | sed 's?<jta-data-source.*?<jta-data-source>${CDB_DB_NAME}_DataSource</jta-data-source>?g' > $configFile.2 && mv $configFile.2 $configFile"
eval $cmd

configFile=WEB-INF/classes/cdb.portal.properties
cmd="cat $configFile | sed 's?storageDirectory=.*?storageDirectory=${CDB_DATA_DIR}?g' > $configFile.2 && mv $configFile.2 $configFile"
eval $cmd
cmd="cat $configFile | sed 's?cdb.webService.url=.*?cdb.webService.url=https://${CDB_WEB_SERVICE_HOST}:${CDB_WEB_SERVICE_PORT}/cdb?g' > $configFile.2 && mv $configFile.2 $configFile"
eval $cmd
cmd="cat $configFile | sed 's?cdb.permanentContextRoot.url=.*?cdb.permanentContextRoot.url=${CDB_PERM_CONTEXT_ROOT_URL}?g' > $configFile.2 && mv $configFile.2 $configFile"
eval $cmd

CDB_LDAP_LOOKUP_FILTER=`echo ${CDB_LDAP_LOOKUP_FILTER/&/\\\&}`

cmd="cat $configFile \
     | sed 's?CDB_LDAP_AUTH_SERVER_URL?$CDB_LDAP_AUTH_SERVER_URL?g' \
     | sed 's?CDB_LDAP_AUTH_DN_FORMAT?$CDB_LDAP_AUTH_DN_FORMAT?g' \
     | sed 's?CDB_LDAP_SERVICE_DN?$CDB_LDAP_SERVICE_DN?g' \
     | sed 's?CDB_LDAP_SERVICE_PASS?$CDB_LDAP_SERVICE_PASS?g' \
     | sed 's?CDB_LDAP_LOOKUP_FILTER?$CDB_LDAP_LOOKUP_FILTER?g' \
     > $configFile.2 && mv $configFile.2 $configFile"
eval $cmd

configFile=WEB-INF/classes/resources.properties
cmd="cat $configFile | sed 's?CdbPortalTitle=.*?CdbPortalTitle=${CDB_PORTAL_TITLE}?g' > $configFile.2 && mv $configFile.2 $configFile"
eval $cmd
cmd="cat $configFile | sed 's?CdbSoftwareVersion=.*?CdbSoftwareVersion=${CDB_SOFTWARE_VERSION} ($CDB_DATE)?g' > $configFile.2 && mv $configFile.2 $configFile"
eval $cmd

cmd="cat $configFile | sed 's?CdbSoftwareVersionUrl=.*?CdbSoftwareVersionUrl=${CDB_REPOSITORY_FULL_URL}?g' > $configFile.2 && mv $configFile.2 $configFile"
eval $cmd

for cssFile in portal; do
    configFile=resources/css/$cssFile.css
    cmd="cat $configFile | sed 's?color:.*CDB_CSS_PORTAL_TITLE_COLOR.*?color: ${CDB_CSS_PORTAL_TITLE_COLOR};?g' > $configFile.2 && mv $configFile.2 $configFile"
    eval $cmd
done

jar cf ../$CDB_WAR_FILE *

export AS_JAVA=$JAVA_HOME
ASADMIN_CMD=$GLASSFISH_DIR/bin/asadmin

echo "Attempting to undeploy application"
$ASADMIN_CMD undeploy $CDB_CONTEXT_ROOT
echo "Attempting to deploy application"
$ASADMIN_CMD deploy $CDB_DIST_DIR/$CDB_WAR_FILE
