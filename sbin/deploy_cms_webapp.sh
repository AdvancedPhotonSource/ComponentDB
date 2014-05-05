#!/bin/sh

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
if [ ! -z "$1" ]; then
    DB_NAME=$1
fi
echo "Using DB name: $DB_NAME"

# Look for deployment file in etc directory, and use it to override
# default entries
deployConfigFile=$CMS_ROOT_DIR/etc/${DB_NAME}.deploy.conf
if [ -f $deployConfigFile ]; then
    echo "Using deployment config file: $deployConfigFile"
    . $deployConfigFile
else
    echo "Deployment config file $deployConfigFile not found, using defaults"
fi

CMS_HOST_ARCH=`uname | tr [A-Z] [a-z]`-`uname -m`
GLASSFISH_DIR=$CMS_SUPPORT/glassfish/$CMS_HOST_ARCH
CMS_DEPLOY_DIR=$GLASSFISH_DIR/glassfish/domains/domain1/autodeploy
CMS_DIST_DIR=$CMS_ROOT_DIR/src/java/CmsWebPortal/dist
CMS_WAR_FILE=CmsWebPortal.war
JAVA_HOME=$CMS_SUPPORT/java/$CMS_HOST_ARCH

export AS_JAVA=$JAVA_HOME
ASADMIN_CMD=$GLASSFISH_DIR/bin/asadmin

# copy war file
echo "Copying war file"
rm -f $CMS_DEPLOY_DIR/${CMS_WAR_FILE}_*
cp $CMS_DIST_DIR/$CMS_WAR_FILE $CMS_DEPLOY_DIR

# wait on deployment
echo "Waiting on war deployment..."
WAIT_TIME=30
cd $CMS_DEPLOY_DIR
t=0
while [ $t -lt $WAIT_TIME ]; do
    sleep 1
    deploymentStatus=`ls -c1 ${CMS_WAR_FILE}_* 2> /dev/null | cut -f2 -d'_'`
    if [ ! -z "$deploymentStatus" ]; then
        break
    fi
    t=`expr $t + 1`
done
echo "Deployment Status: $deploymentStatus"


