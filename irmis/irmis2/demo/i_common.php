<?php  
/*
 * Demo Application Common Include 
 * Common file for header of all top-level php pages of Demo application.
 * Defines any needed classes and functions, and establishes needed default data
 * in session if it does not already exist.
 */

// logging util
include_once ('../util/i_logging.php');

// db access layer
include_once ('../db/i_db_connect.php');
include_once ('../db/i_db_querybuilder.php');
include_once ('../db/i_db_entity_ioc.php');

// all php applications need this
include_once ('../common/i_common_common.php');

// establish connection to db (note: actually re-uses pooled connections)
// args are: host, port, dbname, user, passwd, tableNamePrefix
$dbConnManager = new DBConnectionManager("maia","3306","claude","saunders","spittle","");
if (!$conn = $dbConnManager->getConnection()) {
  include('pv_error.php');
  exit;
}

// Make sure that we always have an initialized
//  IOCList object in the session. Note that this
//  doesn't create one if it is already set in the
//  session.
if (!isset ($_SESSION['iocList'])) {
  $initializedIocList = new IOCList();
  $_SESSION['iocList'] = $initializedIocList;
}

// make sure we have initial blank value for ioc name constraint
if (!isset ($_SESSION['iocNameConstraint'])) {
  $_SESSION['iocNameConstraint'] = "";
}

?>
