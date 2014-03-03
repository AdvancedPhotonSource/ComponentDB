<?php
/*
 * Server Application db.inc
 * Establishes database connections.
 */
 
// logging util
include_once ('../util/i_logging.php');

// db access layer
include_once ('../db/i_db_connect.php');

// all php applications need this
include_once ('../common/i_common_common.php');

// parameters to establish connection to db
// args are:  host, port, dbname, user, password, tableNamePrefix

$db_host = 'ctlrdbprod';
$app_host = $_SERVER['APP_HOST'];
$db_port = '3306';
$db_name_production_1 = 'irmis';
$db_user_read_name = 'trust-read';
$db_user_read_passwd = 'hiawj';

?>
