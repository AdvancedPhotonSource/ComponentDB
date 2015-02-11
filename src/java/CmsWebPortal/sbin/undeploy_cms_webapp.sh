#!/bin/sh

#
# Script used for undeploying CMS webapp
# Deployment configuration can be set in etc/$CMS_DB_NAME.deploy.conf file
#
# Usage:
#
# $0 [CMS_DB_NAME]
#

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

# Use first argument as db name, if provided
CMS_DB_NAME=${CMS_DB_NAME:=cms}
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

CMS_HOST_ARCH=`uname | tr [A-Z] [a-z]`-`uname -m`
CMS_CONTEXT_ROOT=${CMS_CONTEXT_ROOT:=CmsWebPortal}
GLASSFISH_DIR=$CMS_SUPPORT/glassfish/$CMS_HOST_ARCH
CMS_DEPLOY_DIR=$GLASSFISH_DIR/glassfish/domains/domain1/autodeploy
CMS_APP_DIR=$GLASSFISH_DIR/glassfish/domains/domain1/applications/$CMS_CONTEXT_ROOT
CMS_DIST_DIR=$CMS_ROOT_DIR/src/java/CmsWebPortal/dist
CMS_WAR_FILE=$CMS_CONTEXT_ROOT.war
JAVA_HOME=$CMS_SUPPORT/java/$CMS_HOST_ARCH

export AS_JAVA=$JAVA_HOME
ASADMIN_CMD=$GLASSFISH_DIR/bin/asadmin

# remove war file from autodeploy directory
echo "Removing war file $CMS_DEPLOY_DIR/$CMS_WAR_FILE"
rm -f $CMS_DEPLOY_DIR/${CMS_WAR_FILE}*

# remove war file from autodeploy directory
if [ -d $CMS_APP_DIR ]; then
    echo "Removing application directory $CMS_APP_DIR"
    rm -rf $CMS_APP_DIR
else
    echo "Application directory $CMS_APP_DIR not found"
fi

# restart server
echo "Restarting glassfish"
$ASADMIN_CMD stop-domain ${CMS_DOMAIN}
$ASADMIN_CMD start-domain ${CMS_DOMAIN}




