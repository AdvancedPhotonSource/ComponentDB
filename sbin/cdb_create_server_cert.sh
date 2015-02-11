#!/bin/bash

sbindir=`dirname $0` 
if [ -z $CDB_RUN_DIR ]; then
    cd $sbindir/..
    export CDB_RUN_DIR=`pwd`
fi

CA_ROOT=$CDB_RUN_DIR/etc/CA
CA_CONFIG=$CDB_RUN_DIR/etc/openssl.cnf
LOG_FILE=/tmp/cdb-server-cert.log.$$

SERVER_NAME=$1
SERVER_CN=$2
ADMIN_EMAIL=$3

if [ $# -ne 3 ]; then
    echo "Usage: $0 <server type> <server description> <admin email>"
    exit 1
fi 

CERT_DIR=$CA_ROOT/certs
CERT_REQ_DIR=$CA_ROOT/certreqs

REQUEST_CMD="openssl req -days 3650 -nodes -new -keyout $CERT_DIR/$SERVER_NAME.key -out $CERT_REQ_DIR/$SERVER_NAME.csr -extensions server -config $CA_CONFIG"
SIGN_CMD="openssl ca -days 3650 -out $CERT_DIR/$SERVER_NAME.crt -in $CERT_REQ_DIR/$SERVER_NAME.csr -extensions server -config $CA_CONFIG"
$REQUEST_CMD >> $LOG_FILE 2>&1 << EOF




$SERVER_CN
$ADMIN_EMAIL


EOF

if [ $? -ne 0 ]; then
    echo "Error creating server certificate, check '$LOG_FILE'."
    exit -1
fi

$SIGN_CMD >> $LOG_FILE 2>&1 << EOF
y
y
EOF

if [ $? -eq 0 ]; then
    chmod 400 $CERT_DIR/*
    exit 0
else
    echo "Error creating server certificate, check '$LOG_FILE'."
    exit -1
fi

