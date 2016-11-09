#!/bin/sh

# Copyright (c) UChicago Argonne, LLC. All rights reserved.
# See LICENSE file.

# Helper functions for CDB commands.

# Fix command line arguments 
CDB_COMMAND_ARGS=""
while [ $# -ne 0 ]; do
    arg=$1
    if [[ $arg == -* ]]; then
        key=`echo $arg | cut -f1 -d'='`
        keyHasValue=`echo $arg | grep '='`
        if [ ! -z "$keyHasValue" ]; then
            value=`echo $arg | cut -f2- -d'='`
            CDB_COMMAND_ARGS="$CDB_COMMAND_ARGS $key=\"$value\""
        else
            CDB_COMMAND_ARGS="$CDB_COMMAND_ARGS $key"
        fi 
    else
        CDB_COMMAND_ARGS="$CDB_COMMAND_ARGS \"$arg\""
    fi
    shift
done


