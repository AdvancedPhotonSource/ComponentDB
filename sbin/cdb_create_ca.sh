#!/bin/bash

sbindir=`dirname $0` 

if [ -z $CDB_RUN_DIR ]; then
    cd $sbindir/..
    export CDB_RUN_DIR=`pwd`
fi

CA_ROOT=$CDB_RUN_DIR/etc/CA
CA_CONFIG=$CDB_RUN_DIR/etc/openssl.cnf
CA_DESC="CDB Certificate Authority"
LOG_FILE=/tmp/cdb-ca.log.$$

LOCKFILE=$CDB_RUN_DIR/etc/cdb-ca.lock
mkdir -p `dirname $LOCKFILE`

echo "Creating $CA_DESC"

# Check for lock file
if [ -f $LOCKFILE ]; then
    if [ "x$1" != "x--force" ]; then
        echo "$0 has already been run and there is no need to re-run it."
        exit -1
    else
        # Clean up CA...
        rm -rf $CA_ROOT
    fi
fi

# Prep directory
HOSTNAME=`hostname`
mkdir -p $CA_ROOT/newcerts
mkdir -p $CA_ROOT/certs
mkdir -p $CA_ROOT/certreqs
mkdir -p $CA_ROOT/private
mkdir -p $CA_ROOT/crl
touch $CA_ROOT/index.txt
echo "01" > $CA_ROOT/serial
openssl req -days 3650 -nodes -new -x509 -keyout $CA_ROOT/private/cakey.pem -out $CA_ROOT/cacert.pem -config $CA_CONFIG >> $LOG_FILE 2>&1 << EOF




$CA_DESC

EOF

#Set the lockfile
if [ $? -eq 0 ]; then
    echo "Created $CA_DESC"
    touch $LOCKFILE
    exit 0
else
    echo "Error creating CA: check '$LOG_FILE'."
    exit -2
fi
