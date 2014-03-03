#!/bin/sh

# CMS setup script for Bourne-type shells
# This file is typically sourced in user's .bashrc file

myDir=`dirname $BASH_SOURCE`
currentDir=`pwd` && cd $myDir
if [ ! -z "$CMS_ROOT_DIR" -a "$CMS_ROOT_DIR" != `pwd` ]; then
    echo "WARNING: Resetting CMS_ROOT_DIR environment variable (old value: $CMS_ROOT_DIR)" 
fi
export CMS_ROOT_DIR=`pwd`

if [ -z $CMS_DATA_DIR ]; then
    export CMS_DATA_DIR=$CMS_ROOT_DIR/../data
    if [ -d $CMS_DATA_DIR ]; then
        cd $CMS_DATA_DIR
        export CMS_DATA_DIR=`pwd`
    fi
fi
if [ ! -d $CMS_DATA_DIR ]; then
    #echo "WARNING: $CMS_DATA_DIR directory does not exist. Developers should point CMS_DATA_DIR to the desired area." 
    unset CMS_DATA_DIR
fi

if [ -z $CMS_VAR_DIR ]; then
    export CMS_VAR_DIR=$CMS_ROOT_DIR/../var
    if [ -d $CMS_VAR_DIR ]; then
        cd $CMS_VAR_DIR
        export CMS_VAR_DIR=`pwd`
    else
    	unset CMS_VAR_DIR
    fi
fi

# Check support setup
if [ -z $CMS_SUPPORT ]; then
    export CMS_SUPPORT=$CMS_ROOT_DIR/../support 
    if [ -d $CMS_SUPPORT ]; then
        cd $CMS_SUPPORT
        export CMS_SUPPORT=`pwd`
    fi
fi
if [ ! -d $CMS_SUPPORT ]; then
    echo "Warning: $CMS_SUPPORT directory does not exist. Developers should point CMS_SUPPORT to the desired area." 
    unset CMS_SUPPORT
fi

# Establish machine architecture
CMS_HOST_ARCH=`uname | tr [A-Z] [a-z]`-`uname -m` 

# Add to path only if directory exists.
prependPathIfDirExists() {
    _dir=$1
    if [ -d ${_dir} ]; then
        PATH=${_dir}:$PATH
    fi
}

prependPathIfDirExists $CMS_SUPPORT/java/$CMS_HOST_ARCH/bin
prependPathIfDirExists $CMS_SUPPORT/ant/bin
prependPathIfDirExists $CMS_ROOT_DIR/bin

# Done
cd $currentDir

