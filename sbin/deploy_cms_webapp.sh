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

GLASSFISH_DIR=$CMS_SUPPORT/glassfish/$EPICS_HOST_ARCH
CMS_DEPLOY_DIR=$GLASSFISH_DIR/glassfish/domains/domain1/autodeploy
CMS_DIST_DIR=$CMS_ROOT_DIR/src/java/CmsWebPortal/dist
CMS_WAR_FILE=$CMS_DIST_DIR/CmsWebPortal.war
JAVA_HOME=$CMS_SUPPORT/java/$EPICS_HOST_ARCH

export AS_JAVA=$JAVA_HOME
ASADMIN_CMD=$GLASSFISH_DIR/bin/asadmin

# copy war file
echo "Deploying war file"
rm -f $CMS_DEPLOY_DIR/`basename $CMS_WAR_FILE`*
cp $CMS_WAR_FILE $CMS_DEPLOY_DIR

