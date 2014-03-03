<?php
/*
 * Component Application Common Include
 * Common file for header of all top-level php pages of IOC application.
 * Defines any needed classes and functions, and establishes needed default data
 * in session if it does not already exist.
 */

// logging util
//include_once ('../util/i_logging.php');
$DEBUG_FILE = "/tmp/debug_component.php";

// db access layer
include_once ('../db/i_db_connect.php');
include_once ('../db/i_db_querybuilder.php');
include_once ('../db/i_db_entity_componenttype.php');
include_once ('../db/i_db_entity_component.php');
include_once ('../db/i_db_entity_component_person.php');
include_once ('../db/i_db_entity_component_mfg_pd.php');
include_once ('../db/i_db_entity_component_ff_pd.php');
include_once ('../db/i_db_entity_component_function_pd.php');
include_once ('../db/i_db_entity_component_doc.php');
include_once ('../db/i_db_entity_component_if.php');
include_once ('db.inc');


// all php applications need this
include_once ('../common/i_common_common.php');

// establish connection to db (note: actually re-uses pooled connections)
// args are: host, port, dbname, user, passwd, tableNamePrefix
// $dbConnManager = new DBConnectionManager("bacchus","3306","irmis","public-read","aps-irmis","");

//$dbConnManager = new DBConnectionManager("maia","3306","claude","saunders","spittle","");

$dbConnManager = new DBConnectionManager($db_host,$db_port,$db_name_production_1,$db_user_read_name,$db_user_read_passwd,"");

if (!$conn = $dbConnManager->getConnection()) {
  include('../common/db_error.php');
  exit;
}

// Make sure that we always have an initialized
//  IOCList object in the session. Note that this
//  doesn't create one if it is already set in the
//  session.
if (!isset ($_SESSION['ComponentTypeList'])) {
  $initializedComponentTypeList = new ComponentTypeList();
  $_SESSION['ComponentTypeList'] = $initializedComponentTypeList;
}

// make sure we have initial blank value for ioc name constraint
if (!isset ($_SESSION['nameDesc'])) {
  $_SESSION['nameDesc'] = "";
}

//if (!isset ($_SESSION['ComponentList'])) {
//  $initializedComponentList = new ComponentList();
//  $_SESSION['ComponentList'] = $initializedComponentList;
//}

if (!isset ($_SESSION['PersonList'])) {
   $initializedPersonList = new PersonList();
   if (!$initializedPersonList->loadFromDB($conn)) {
       include('../common/db_error.php');
       exit;
   }
   $_SESSION['PersonList'] = $initializedPersonList;
}

if (!isset ($_SESSION['mfgList'])) {
   $initializedMfgList = new mfgList();
   if (!$initializedMfgList->loadFromDB($conn)) {
       include('../common/db_error.php');
       exit;
   }
   $_SESSION['mfgList'] = $initializedMfgList;
}

if (!isset ($_SESSION['ffList'])) {
   $initializedFfList = new ffList();
   if (!$initializedFfList->loadFromDB($conn)) {
       include('../common/db_error.php');
       exit;
   }
   $_SESSION['ffList'] = $initializedFfList;
}

if (!isset ($_SESSION['functionList'])) {
   $initializedFunctionList = new functionList();
   if (!$initializedFunctionList->loadFromDB($conn)) {
       include('../common/db_error.php');
       exit;
   }
   $_SESSION['functionList'] = $initializedFunctionList;
}

/*
if (!isset ($_SESSION['DocList'])) {
   $initializedDocList = new DocList();
   if (!$initializedDocList->loadFromDB($conn)) {
       include('../common/db_error.php');
       exit;
   }
   $_SESSION['DocList'] = $initializedDocList;
}
*/
?>
