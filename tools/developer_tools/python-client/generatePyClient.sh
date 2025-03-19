#!/bin/bash

# Copyright (c) UChicago Argonne, LLC. All rights reserved.
# See LICENSE file.

#
# Script used to generate the required API client from the running cdbCli server instance
#
# Usage:
#
# $0 CDB_BASE_PATH
#

MY_DIR=`dirname $0` && cd $MY_DIR && MY_DIR=`pwd`
ROOT_DIR=$MY_DIR

OPEN_API_VERSION="4.3.1"
OPEN_API_GENERATOR_JAR="openapi-generator-cli-$OPEN_API_VERSION.jar"
OPEN_API_GENERATOR_JAR_URL="https://repo1.maven.org/maven2/org/openapitools/openapi-generator-cli/$OPEN_API_VERSION/$OPEN_API_GENERATOR_JAR" 

GEN_CONFIG_FILE_PATH=$MY_DIR/ClientApiConfig.yml
GEN_OUT_DIR="pythonApi"

if [ -z "$1" ]; then
    echo "Please specify CDB_BASE_PATH";
    echo "Usage: $0 CDB_BASE_PATH"
    exit 1; 
fi
CDB_OPENAPI_YML_PATH="api/openapi.yaml"
CDB_OPENAPI_YML_URL="$1/$CDB_OPENAPI_YML_PATH"

cd $ROOT_DIR

curl -O $OPEN_API_GENERATOR_JAR_URL

java -jar $OPEN_API_GENERATOR_JAR  generate -i "$CDB_OPENAPI_YML_URL" -g python -o $GEN_OUT_DIR -c $GEN_CONFIG_FILE_PATH

if [ $? -ne 0 ]; then
    exit 1
fi

# Clean up
rm cdbApi -rv
rm $OPEN_API_GENERATOR_JAR

# Fetch the generated Api
cd $GEN_OUT_DIR
cp -rv cdbApi ../
cd ..

# Clean up
rm -rf $GEN_OUT_DIR 
