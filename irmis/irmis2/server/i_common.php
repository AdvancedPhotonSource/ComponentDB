<?php
/*
 * Server Application Common Include
 * Common file for header of all top-level php pages of Server application.
 * Defines any needed classes and functions, and establishes needed default data
 * in session if it does not already exist.
 */

// logging util
include_once ('../util/i_logging.php');

// db access layer
include_once ('../db/i_db_connect.php');
include_once ('../db/i_db_querybuilder.php');
include_once ('../db/i_db_entity_server.php');
include_once ('../db/i_db_entity_server_operating_system_pd.php');
include_once ('../db/i_db_entity_server_cognizant_pd.php');
include_once ('../db/i_db_entity_component.php');
include_once ('db.inc');

// all php applications need this
include_once ('../common/i_common_common.php');

// establish connection to db (note: actually re-uses pooled connections)
// args are: host, port, dbname, user, passwd, tableNamePrefix

$dbConnManager = new DBConnectionManager($db_host,$db_port,$db_name_production_1,$db_user_read_name,$db_user_read_passwd,"");

if (!$conn = $dbConnManager->getConnection()) {
  include('../common/db_error.php');
  exit;
}

// Make sure that we always have an initialized
//  ServerList object in the session. Note that this
//  doesn't create one if it is already set in the
//  session.
if (!isset ($_SESSION['serverList'])) {
  $initializedServerList = new ServerList();
  $_SESSION['serverList'] = $initializedServerList;
}


// make sure we have initial blank value for server name constraint
if (!isset ($_SESSION['serverNameConstraint'])) {
  $_SESSION['serverNameConstraint'] = "";
}


if (!isset ($_SESSION['operating_systemList'])) {
   $initializedOperating_SystemList = new operating_systemList();
   if (!$initializedOperating_SystemList->loadFromDB($conn)) {
       include('../common/db_error.php');
       exit;
   }
   $_SESSION['operating_systemList'] = $initializedOperating_SystemList;
}

if (!isset ($_SESSION['cognizantList'])) {
   $initializedCognizantList = new cognizantList();
   if (!$initializedCognizantList->loadFromDB($conn)) {
       include('../common/db_error.php');
       exit;
   }
   $_SESSION['cognizantList'] = $initializedCognizantList;
}


?>
