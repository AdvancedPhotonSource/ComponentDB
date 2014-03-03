<?php
/*
 * IOC Application Common Include
 * Common file for header of all top-level php pages of IOC application.
 * Defines any needed classes and functions, and establishes needed default data
 * in session if it does not already exist.
 */

// logging util
//include_once ('../util/i_logging.php');
$DEBUG_FILE = "/tmp/debug_ioc.php";

// db access layer
include_once ('../db/i_db_connect.php');
include_once ('../db/i_db_querybuilder.php');
include_once ('../db/i_db_entity_ioc.php');
include_once ('../db/i_db_entity_ioc_system_pd.php');
include_once ('../db/i_db_entity_ioc_status_pd.php');
include_once ('../db/i_db_entity_ioc_developer_pd.php');
include_once ('../db/i_db_entity_ioc_tech_pd.php');
include_once ('../db/i_db_entity_ioc_contents.php');
include_once ('../db/i_db_entity_componenttype.php');
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
//  IOCList object in the session. Note that this
//  doesn't create one if it is already set in the
//  session.
if (!isset ($_SESSION['iocList'])) {
  $initializedIocList = new IOCList();
  $_SESSION['iocList'] = $initializedIocList;
}

if (!isset ($_SESSION['iocContentsList'])) {
  $initializedIocContentsList = new IOCContentsList();
  $_SESSION['iocContentsList'] = $initializedIocContentsList;
}

if (!isset ($_SESSION['ComponentTypeList'])) {
  $initializedComponentTypeList = new ComponentTypeList();
  $_SESSION['ComponentTypeList'] = $initializedComponentTypeList;
}

unset($_SESSION['BLioc']); // this makes this checkbox, "unchecked" when first opening the page
$_SESSION['Accioc'] = 'checked'; // this make this checkbox, "checked" when first opening the page

// make sure we have initial blank value for ioc name constraint
if (!isset ($_SESSION['iocNameConstraint'])) {
  $_SESSION['iocNameConstraint'] = "";
}


if (!isset ($_SESSION['systemList'])) {
   $initializedSystemList = new systemList();
   if (!$initializedSystemList->loadFromDB($conn)) {
       include('../common/db_error.php');
       exit;
   }
   $_SESSION['systemList'] = $initializedSystemList;
}


if (!isset ($_SESSION['statusList'])) {
   $initializedStatusList = new statusList();
   if (!$initializedStatusList->loadFromDB($conn)) {
       include('../common/db_error.php');
       exit;
   }
   $_SESSION['statusList'] = $initializedStatusList;
}



if (!isset ($_SESSION['developerList'])) {
   $initializedDeveloperList = new developerList();
   if (!$initializedDeveloperList->loadFromDB($conn)) {
       include('../common/db_error.php');
       exit;
   }
   $_SESSION['developerList'] = $initializedDeveloperList;
}

if (!isset ($_SESSION['techList'])) {
   $initializedTechList = new techList();
   if (!$initializedTechList->loadFromDB($conn)) {
       include('../common/db_error.php');
       exit;
   }
   $_SESSION['techList'] = $initializedTechList;
}



?>
