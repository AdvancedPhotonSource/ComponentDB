#!/bin/bash

# Copyright (c) UChicago Argonne, LLC. All rights reserved.
# See LICENSE file.


#
# Script used for updating the SSL certificates the application runs on
#
# Usage:
#
# $0 KEY_FILE CRT_FILE
#

CALL_DIR=`pwd`
MY_DIR=`dirname $0` && cd $MY_DIR && MY_DIR=`pwd`
if [ -z "${CDB_ROOT_DIR}" ]; then
    CDB_ROOT_DIR=$MY_DIR/..
fi
cd $CALL_DIR
CDB_ENV_FILE=${CDB_ROOT_DIR}/setup.sh
if [ ! -f ${CDB_ENV_FILE} ]; then
    echo "Environment file ${CDB_ENV_FILE} does not exist."
    exit 2
fi
. ${CDB_ENV_FILE} > /dev/null

if [ ! -z "$1" ]; then
    KEY_FILE=$1
else
    echo "Please provide a key file input."
    exit 2
fi

if [ ! -z "$1" ]; then
    CRT_FILE=$2
else
    >&2 echo "Please provide a crt file input."
    exit 2
fi

echo -n "Please enter the master password for glassfish: "
read -s masterPassword
echo ""; 

if [ -z $masterPassword ]; then
    >&2 echo "A master password must be provided"
    exit 2
fi

CDB_DOMAIN_NAME=production
GLASSFISH_KEYSTORE_PATH=$CDB_GLASSFISH_DIR/glassfish/domains/$CDB_DOMAIN_NAME/config/keystore.jks

# Test entered password
failed=0
keytool -list -v -keystore $GLASSFISH_KEYSTORE_PATH --storepass $masterPassword > /dev/null || failed=1

if [ $failed == 1 ]; then
    >&2 echo "The password entered was incorrect"
    exit 2
fi

PKCS12_CERT_STORE="/tmp/${RANDOM}cert.pkcs12"
SSL_ALIAS="cdbcert"

echo "Creating keystore: $PKCS12_CERT_STORE"
openssl pkcs12 -export -in $CRT_FILE -inkey $KEY_FILE -out $PKCS12_CERT_STORE -name $SSL_ALIAS -passout pass:$masterPassword

keytool -keystore $GLASSFISH_KEYSTORE_PATH -delete -alias $SSL_ALIAS -storepass $masterPassword

keytool -importkeystore \
-srckeystore $PKCS12_CERT_STORE \
-srcstoretype pkcs12 \
-srcstorepass $masterPassword \
-deststoretype jks \
-destkeystore $GLASSFISH_KEYSTORE_PATH \
-deststorepass $masterPassword

asadmin start-domain $CDB_DOMAIN_NAME

asadmin set configs.config.server-config.network-config.protocols.protocol.http-listener-2.ssl.cert-nickname=$SSL_ALIAS

# Restart
asadmin stop-domain $CDB_DOMAIN_NAME
asadmin start-domain $CDB_DOMAIN_NAME

echo "Removing keystore: $PKCS12_CERT_STORE"
rm $PKCS12_CERT_STORE 
