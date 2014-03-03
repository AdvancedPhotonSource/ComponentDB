<?php  
/*
 * Component Application Common Include 
 * Common file for header of all top-level php pages of Spares application.
 * Defines any needed classes and functions, and establishes needed default data
 * in session if it does not already exist.
 */

// logging util
//include_once ('../util/i_logging.php');
$DEBUG_FILE = "/tmp/debug_racks.php";

// db access layer
include_once ('../db/i_db_connect.php');
include_once ('../db/i_db_querybuilder.php');
include_once ('../db/i_db_entity_cable.php');
include_once ('db.inc');

// all php applications need this
include_once ('../common/i_common_common.php');

$dbConnManager = new DBConnectionManager($db_host,$db_port,$db_name_production_1,$db_user_read_name,$db_user_read_passwd,"");

if (!$conn = $dbConnManager->getConnection()) {
  include('../common/db_error.php');
  exit;
}

// Make sure that we always have an initialized
//  CableList object in the session. Note that this
//  doesn't create one if it is already set in the
//  session.

if (!isset ($_SESSION['CableList'])) {
  $initializedCableList = new CableList();
  $_SESSION['CableList'] = $initializedCableList;
}

?>
