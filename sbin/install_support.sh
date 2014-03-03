#!/bin/sh

CMS_SVN_URL=https://svn.aps.anl.gov/cms

MY_DIR=`dirname $0` && cd $MY_DIR && MY_DIR=`pwd`
if [ -z "${CMS_ROOT_DIR}" ]; then
    CMS_ROOT_DIR=$MY_DIR/..
fi
CMS_SUPPORT=$CMS_ROOT_DIR/../support

execute() {
    echo "Executing: $@"
    eval "$@"
}

if [ ! -d $CMS_SUPPORT ]; then
    echo "Creating new CMS support directory $CMS_SUPPORT."
    cd $CMS_SUPPORT/..
    execute svn co $CMS_SVN_URL/support support
fi
cd $CMS_SUPPORT
execute svn update
execute $CMS_SUPPORT_DIR/bin/clean_all.sh
execute $CMS_SUPPORT_DIR/bin/install_all.sh



