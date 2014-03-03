<?php
/*
 * Common file for header of all top-level php pages of PLC application.
 * Defines any needed classes and functions, and establishes needed default data
 * in session if it does not already exist.
 */

// logging util
include_once ('../util/i_logging.php');

// db access layer
include_once ('../db/i_db_connect.php');
include_once ('../db/i_db_querybuilder.php');
include_once ('../db/i_db_entity_plc.php');
include_once ('../db/i_db_entity_parents.php');
include_once ('../db/i_db_entity_ctl.php');
include_once ('../db/i_db_entity_plcrecord.php');
include_once ('../db/i_db_entity_plclink.php');
include_once ('../db/i_db_entity_plcdescr.php');
//include_once ('../db/i_db_entity_plcsvn.php');
include_once ('db.inc');

// all php applications need this
include_once ('../common/i_common_common.php');

// establish connection to db (note: actually re-uses pooled connections)
// args are: host, port, dbname, user, passwd, tableNamePrefix

//$dbConnManager = new
//DBConnectionManager("maia","3306","stage","mvarotto","22505","");
//if (!$conn = $dbConnManager->getConnection()) {
//  include('../common/db_error.php');
//  exit;
//}

// $dbConnManager_2 = new DBConnectionManager("bacchus","3306","irmis","public-read","aps-irmis","");

$dbConnManager_2 = new DBConnectionManager($db_host,$db_port,$db_name_production_1,$db_user_read_name,$db_user_read_passwd,"");

if (!$conn_2 = $dbConnManager_2->getConnection()) {
  include('../common/db_error.php');
  exit;
}

$dbConnManager_plc_write = new DBConnectionManager($db_host,$db_port,$db_name_production_1,$db_user_plc_read_write_name,$db_user_plc_read_write_passwd,"");

if (!$conn_plc_write = $dbConnManager_plc_write->getConnection()) {
	include('../common/db_error.php');
	exit;
}

//$dbConnManager_3 = new
//DBConnectionManager("maia","3306","marianna","mvarotto","22505","");
//if (!$conn_3 = $dbConnManager_3->getConnection()) {
//  include('../common/db_error.php');
//  exit;
//}

// Make sure that we always have an initialized
//  PLCList object in the session. Note that this
//  doesn't create one if it is already set in the
//  session.
if (!isset ($_SESSION['plcList'])) {
  $initializedPlcList = new PLCList();
  $_SESSION['plcList'] = $initializedPlcList;
}

// make sure we have initial blank value for plc name constraint
if (!isset ($_SESSION['plcNameConstraint'])) {
  $_SESSION['plcNameConstraint'] = "";
}

// Make sure we have an initialized PLCRecList object in the session.

if (!isset ($_SESSION['plcRecList'])) {
  $initializedPlcRecList = new PLCRecList();
  $_SESSION['plcRecList'] = $initializedPlcRecList;
}

?>
