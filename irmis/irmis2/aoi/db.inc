<?php
/*
 * AOI Application db.inc
 * Defines parameters used to establish database connections.
 */
 
// logging util
include_once ('../util/i_logging.php');

// db access layer
include_once ('../db/i_db_connect.php');

// all php applications need this
// include_once ('../common/i_common_common.php');

// parameters to establish connection to db
// args are:  host, port, dbname, user, password, tableNamePrefix

$db_host = $_SERVER['DB_HOST'];
$db_port = $_SERVER['DB_PORT'];
$db_name_production_1 = $_SERVER['DB_NAME_PRODUCTION_1'];
$db_user_read_name = $_SERVER['DB_USER_READ_NAME'];
$db_user_read_passwd = $_SERVER['DB_USER_READ_PASSWD'];
$db_user_aoi_read_write_name = $_SERVER['DB_USER_AOI_READ_WRITE_NAME'];
$db_user_aoi_read_write_passwd = $_SERVER['DB_USER_AOI_READ_WRITE_PASSWD'];
$ldap_login_secret = $_SERVER['LDAP_LOGIN_SECRET'];
$ldap_user_name = $_SERVER['LDAP_USER_NAME'];
$icms_user_name = $_SERVER['ICMS_USER_NAME'];
$icms_user_passwd = $_SERVER['ICMS_USER_PASSWD'];


$dbConnManager = new DBConnectionManager($db_host,$db_port,$db_name_production_1,$db_user_read_name,$db_user_read_passwd,"");

if (!$conn = $dbConnManager->getConnection()) {
	include('../common/db_error.php');
	exit;
}

?>
