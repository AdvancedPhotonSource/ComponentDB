#!/bin/bash

# Copyright (c) UChicago Argonne, LLC. All rights reserved.
# See LICENSE file.


# CDB setup script for Bourne-type shells
# This file is typically sourced in user's .bashrc file

if [ -n "$BASH_SOURCE" ]; then
  input_param=$BASH_SOURCE
elif [ -n "$ZSH_VERSION" ]; then
  setopt function_argzero
  input_param=$0
else
  echo 1>&2 "Unsupported shell. Please use bash or zsh."
  exit 2
fi

myDir=`dirname $input_param`

currentDir=`pwd` && cd $myDir
if [ ! -z "$CDB_ROOT_DIR" -a "$CDB_ROOT_DIR" != `pwd` ]; then
    echo "WARNING: Resetting CDB_ROOT_DIR environment variable (old value: $CDB_ROOT_DIR)" 
fi
export CDB_ROOT_DIR=`pwd`

if [ -z $CDB_INSTALL_DIR ]; then
    export CDB_INSTALL_DIR=$CDB_ROOT_DIR/..
    if [ -d $CDB_INSTALL_DIR ]; then
        cd $CDB_INSTALL_DIR
        export CDB_INSTALL_DIR=`pwd`
    fi
fi
if [ -z $CDB_DATA_DIR ]; then
    export CDB_DATA_DIR=$CDB_INSTALL_DIR/data
    if [ -d $CDB_DATA_DIR ]; then
        cd $CDB_DATA_DIR
        export CDB_DATA_DIR=`pwd`
    fi
fi
if [ ! -d $CDB_DATA_DIR ]; then
    echo "WARNING: $CDB_DATA_DIR directory does not exist. Developers should point CDB_DATA_DIR to the desired area." 
    #unset CDB_DATA_DIR
fi

if [ -z $CDB_VAR_DIR ]; then
    export CDB_VAR_DIR=$CDB_INSTALL_DIR/var
    if [ -d $CDB_VAR_DIR ]; then
        cd $CDB_VAR_DIR
        export CDB_VAR_DIR=`pwd`
    else
    	unset CDB_VAR_DIR
    fi
fi

# Establish machine architecture and host name
CDB_HOST_ARCH=$(uname -sm | tr -s '[:upper:][:blank:]' '[:lower:][\-]')
CDB_SHORT_HOSTNAME=`hostname -s`

# Check support setup
if [ -z $CDB_SUPPORT_DIR ]; then
    export CDB_SUPPORT_DIR=$CDB_INSTALL_DIR/support-$CDB_SHORT_HOSTNAME
    if [ -d $CDB_SUPPORT_DIR ]; then
        cd $CDB_SUPPORT_DIR
        export CDB_SUPPORT_DIR=`pwd`
    fi
fi
if [ ! -d $CDB_SUPPORT_DIR ]; then
    echo "Warning: $CDB_SUPPORT_DIR directory does not exist. Developers should point CDB_SUPPORT_DIR to the desired area." 
    #unset CDB_SUPPORT_DIR
else
    export CDB_GLASSFISH_DIR=$CDB_SUPPORT_DIR/payara/$CDB_HOST_ARCH
fi

# Add to path only if directory exists.
prependPathIfDirExists() {
    _dir=$1
    if [ -d ${_dir} ]; then
        PATH=${_dir}:$PATH
    fi
}

prependPathIfDirExists $CDB_GLASSFISH_DIR/bin
prependPathIfDirExists $CDB_SUPPORT_DIR/java/$CDB_HOST_ARCH/bin
prependPathIfDirExists $CDB_SUPPORT_DIR/ant/bin
prependPathIfDirExists $CDB_SUPPORT_DIR/netbeans/currentNetbeans/bin
prependPathIfDirExists $CDB_ROOT_DIR/bin
prependPathIfDirExists $CDB_SUPPORT_DIR/anaconda/$CDB_HOST_ARCH/bin
prependPathIfDirExists $CDB_SUPPORT_DIR/netbeans/currentNetbeans/java/maven/bin
prependPathIfDirExists $CDB_ROOT_DIR/tools/developer_tools/portal_testing/PythonSeleniumTest/support_bin

mysqlPath=$CDB_SUPPORT_DIR/mysql/$CDB_HOST_ARCH
if [ -d $mysqlPath ]; then
    cd $mysqlPath
    pythonDir=`pwd`
    export PATH=`pwd`/bin:$PATH
    export LD_LIBRARY_PATH=`pwd`/lib:$LD_LIBRARY_PATH    
fi

# Check if we have  local python
if [ -z $CDB_PYTHON_DIR ]; then
    pythonDir=$CDB_SUPPORT_DIR/python/$CDB_HOST_ARCH
else
    pythonDir=$CDB_PYTHON_DIR
fi
if [ -d $pythonDir ]; then
    cd $pythonDir
    pythonDir=`pwd`
    export PATH=`pwd`/bin:$PATH
    export LD_LIBRARY_PATH=`pwd`/lib:$LD_LIBRARY_PATH
    export CDB_PYTHON_DIR=$pythonDir
fi

if [ -z $PYTHONPATH ]; then
    PYTHONPATH=$CDB_ROOT_DIR/src/python
else
    PYTHONPATH=$CDB_ROOT_DIR/src/python:$PYTHONPATH
fi
PYTHONPATH=$CDB_ROOT_DIR/tools/developer_tools/python-client:$PYTHONPATH
export PYTHONPATH

# Done
cd $currentDir

