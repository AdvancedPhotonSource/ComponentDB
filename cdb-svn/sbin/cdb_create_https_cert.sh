#!/bin/bash

sbindir=`dirname $0` 
$sbindir/cdb_create_server_cert.sh httpd cdb.aps.anl.gov cdb@aps.anl.gov

