<?php
/*
 * IOC Application db.inc
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

$db_host = $_SERVER['DB_HOST'];
$app_host = $_SERVER['APP_HOST'];
//$db_host = 'ctlrdbdev';
$db_port = $_SERVER['DB_PORT'];
$db_name_production_1 = $_SERVER['DB_NAME_PRODUCTION_1'];
$db_user_read_name = $_SERVER['DB_USER_READ_NAME'];
$db_user_read_passwd = $_SERVER['DB_USER_READ_PASSWD'];

?>
