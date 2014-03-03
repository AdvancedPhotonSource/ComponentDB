<?php
/*
 * Network Application Common Include
 * Common file for header of all top-level php pages of Network application.
 * Defines any needed classes and functions, and establishes needed default data
 * in session if it does not already exist.
 */

// logging util
include_once ('../util/i_logging.php');

// db access layer
include_once ('../db/i_db_connect.php');
include_once ('../db/i_db_querybuilder.php');
include_once ('../db/i_db_entity_network_ts.php');
include_once ('../db/i_db_entity_network_ts_details.php');
include_once ('../db/i_db_entity_network_switch.php');
include_once ('../db/i_db_entity_network_switch_details.php');
include_once ('../db/i_db_entity_network_mc.php');
include_once ('../db/i_db_entity_network_mc_details.php');
include_once ('../db/i_db_entity_network_iocConn.php');
include_once ('db.inc');

// all php applications need this
include_once ('../common/i_common_common.php');

// establish connection to db (note: actually re-uses pooled connections)
// args are: host, port, dbname, user, passwd, tableNamePrefix

//$dbConnManager = new DBConnectionManager("bacchus","3306","irmis","public-read","aps-irmis","");
//$dbConnManager = new DBConnectionManager("maia","3306","claude","saunders","spittle","");

$dbConnManager = new DBConnectionManager($db_host,$db_port,$db_name_production_1,$db_user_read_name,$db_user_read_passwd,"");

if (!$conn = $dbConnManager->getConnection()) {
  include('../common/db_error.php');
  exit;
}

//  Make sure that we always have an initialized
//  xxxList object in the session. Note that this
//  doesn't create one if it is already set in the
//  session.
if (!isset ($_SESSION['tsList'])) {
  $initializedtsList = new tsList();
  $_SESSION['tsList'] = $initializedtsList;
}

if (!isset ($_SESSION['tsDetailsList'])) {
  $initializedtsDetailsList = new tsDetailsList();
  $_SESSION['tsDetailsList'] = $initializedtsDetailsList;
}

if (!isset ($_SESSION['swList'])) {
  $initializedswList = new swList();
  $_SESSION['swList'] = $initializedswList;
}

if (!isset ($_SESSION['swDetailsList'])) {
  $initializedswDetailsList = new swDetailsList();
  $_SESSION['swDetailsList'] = $initializedswDetailsList;
}

if (!isset ($_SESSION['mcList'])) {
  $initializedmcList = new mcList();
  $_SESSION['mcList'] = $initializedmcList;
}

if (!isset ($_SESSION['mcDetailsList'])) {
  $initializedmcDetailsList = new mcDetailsList();
  $_SESSION['mcDetailsList'] = $initializedmcDetailsList;
}

if (!isset ($_SESSION['iocConnectionsList'])) {
  $initializediocConnectionsList = new iocConnectionsList();
  $_SESSION['iocConnectionsList'] = $initializediocConnectionsList;
}

?>
