#!/bin/bash

# Copyright (c) UChicago Argonne, LLC. All rights reserved.
# See LICENSE file.


#
# Script used for creation of configuration file used for CDB.
#
# Usage:
#
# $0 [CDB_DB_NAME]
#

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

CDB_ETC_DIR=$CDB_INSTALL_DIR/etc

OPENSSL_CONFIG_FILE=$CDB_ETC_DIR/cdb.openssl.cnf

if [ -f $OPENSSL_CONFIG_FILE ]; then
    echo "Configuration file $OPENSSL_CONFIG_FILE has already been created"
    exit 0;
fi

OPEN_SSL_TEMPLATE_FILE=$CDB_ROOT_DIR/etc/cdb.openssl.cnf.template

echo "Creating the cdb openSSL configuration file."
read -p "Enter the organizational name [company]: " SSL_ORG_NAME
read -p "Enter the secondary organizational name [company]: " SSL_SECONDARY_ORG_NAME
read -p "Enter the organizational unit name [accounting]: " SSL_ORG_UNIT_NAME

if [ -z $SSL_ORG_NAME ]; then
    SSL_ORG_NAME=company
fi

if [ -z $SSL_SECONDARY_ORG_NAME ]; then
    SSL_SECONDARY_ORG_NAME=company
fi

if [ -z $SSL_ORG_UNIT_NAME ]; then
    SSL_ORG_UNIT_NAME=accounting
fi

cmd="cat $OPEN_SSL_TEMPLATE_FILE \
    | sed 's?SSL_ORG_NAME?$SSL_ORG_NAME?g' \
    | sed 's?SSL_SECONDARY_ORG_NAME?$SSL_SECONDARY_ORG_NAME?g' \
    | sed 's?SSL_ORG_UNIT_NAME?$SSL_ORG_UNIT_NAME?g' \
    > $OPENSSL_CONFIG_FILE"

eval $cmd || exit 1
