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

OPEN_API_VERSION="7.24.0"
OPEN_API_GENERATOR_JAR="openapi-generator-cli-$OPEN_API_VERSION.jar"
OPEN_API_GENERATOR_JAR_URL="https://repo1.maven.org/maven2/org/openapitools/openapi-generator-cli/$OPEN_API_VERSION/$OPEN_API_GENERATOR_JAR"

# Cache the generator jar so repeated runs don't re-download it: OPENAPI_GENERATOR_CACHE_DIR override, else $CDB_SUPPORT_DIR/src, else a repo-local support_bin/ fallback.
if [ -n "$OPENAPI_GENERATOR_CACHE_DIR" ]; then
    CACHE_DIR="$OPENAPI_GENERATOR_CACHE_DIR"
elif [ -n "$CDB_SUPPORT_DIR" ] && [ -d "$CDB_SUPPORT_DIR" ]; then
    CACHE_DIR="$CDB_SUPPORT_DIR/src"
else
    CACHE_DIR="$MY_DIR/support_bin"
fi
mkdir -p "$CACHE_DIR"
OPEN_API_GENERATOR_JAR_PATH="$CACHE_DIR/$OPEN_API_GENERATOR_JAR"

GEN_CONFIG_FILE_PATH=$MY_DIR/ClientApiConfig.yml
GEN_OUT_DIR="pythonApi"
API_PKG_DIR="$MY_DIR/packages/api"

if [ -z "$1" ]; then
    echo "Please specify CDB_BASE_PATH";
    echo "Usage: $0 CDB_BASE_PATH"
    exit 1; 
fi
CDB_OPENAPI_YML_PATH="api/openapi.yaml"
CDB_OPENAPI_YML_URL="$1/$CDB_OPENAPI_YML_PATH"

cd $ROOT_DIR

if [ ! -f "$OPEN_API_GENERATOR_JAR_PATH" ]; then
    echo "Retrieving $OPEN_API_GENERATOR_JAR_URL"
    curl -L -o "$OPEN_API_GENERATOR_JAR_PATH" $OPEN_API_GENERATOR_JAR_URL
fi

if [ ! -f "$OPEN_API_GENERATOR_JAR_PATH" ]; then
    echo "File $OPEN_API_GENERATOR_JAR_PATH was not found"
    exit 1
fi

echo "Using openapi-generator jar: $OPEN_API_GENERATOR_JAR_PATH"

java -jar "$OPEN_API_GENERATOR_JAR_PATH"  generate -i "$CDB_OPENAPI_YML_URL" -g python -o $GEN_OUT_DIR -c $GEN_CONFIG_FILE_PATH

if [ $? -ne 0 ]; then
    exit 1
fi

# Clean up
rm -rf "$API_PKG_DIR/cdbApi"

# Fetch the generated Api
cd $GEN_OUT_DIR
cp -rv cdbApi "$API_PKG_DIR/"
cd ..

# Clean up
rm -rf $GEN_OUT_DIR
