<?php  
/*
 * Component Application Common Include 
 * Common file for header of all top-level php pages of Spares application.
 * Defines any needed classes and functions, and establishes needed default data
 * in session if it does not already exist.
 */

// logging util
//include_once ('../util/i_logging.php');
$DEBUG_FILE = "/tmp/debug_power.php";

// db access layer
include_once ('../db/i_db_connect.php');
include_once ('../db/i_db_querybuilder.php');
include_once ('../db/i_db_entity_componenttype.php');
include_once ('../db/i_db_entity_component.php');
include_once ('../db/i_db_entity_component_person.php');
//include_once ('../db/i_db_entity_component_mfg_pd.php');
//include_once ('../db/i_db_entity_component_ff_pd.php');
//include_once ('../db/i_db_entity_component_function_pd.php');
//include_once ('../db/i_db_entity_component_doc.php');
//include_once ('../db/i_db_entity_spares.php');
include_once ('../db/i_db_entity_rack.php');
//include_once ('../db/i_db_entity_rack_contents.php');
include_once ('../db/i_db_entity_rack_component_contents.php');
include_once ('../db/i_db_entity_rack_component_children_sum.php');
include_once ('../db/i_db_entity_power_contents.php');
include_once ('../db/i_db_entity_bldg_pd.php');
include_once ('../db/i_db_entity_room_pd.php');
include_once ('../db/i_db_entity_switchgear_pd.php');
include_once ('../db/i_db_entity_switchgear.php');
include_once ('../db/i_db_entity_switchgear_contents.php');
include_once ('../db/i_db_entity_group_name_pd.php');
include_once ('../db/i_db_entity_component_if.php');
include_once ('db.inc');



// all php applications need this
include_once ('../common/i_common_common.php');

// establish connection to db (note: actually re-uses pooled connections)
// args are: host, port, dbname, user, passwd, tableNamePrefix
//$dbConnManager = new DBConnectionManager("bacchus","3306","irmis","public-read","aps-irmis","");
//$dbConnManager = new DBConnectionManager("maia","3306","claude","saunders","spittle","");
//if (!$conn = $dbConnManager->getConnection()) {
//  include('../common/db_error.php');
//  exit;
//}

$dbConnManager = new DBConnectionManager($db_host,$db_port,$db_name_production_1,$db_user_read_name,$db_user_read_passwd,"");

if (!$conn = $dbConnManager->getConnection()) {
  include('../common/db_error.php');
  exit;
}


// Make sure that we always have an initialized
//  IOCList object in the session. Note that this
//  doesn't create one if it is already set in the
//  session.

//if (!isset ($_SESSION['RackList'])) {
//  $initializedRackList = new RackList();
//  $_SESSION['RackList'] = $initializedRackList;
//}

// make sure we have initial blank value for ioc name constraint
if (!isset ($_SESSION['nameDesc'])) {
  $_SESSION['nameDesc'] = "";
}

// make sure we have initial blank value for ioc name constraint
//if (!isset ($_SESSION['noSpare'])) {
//  $_SESSION['noSpare'] = "0";
//}


if (!isset ($_SESSION['ComponentList'])) {
  $initializedComponentList = new ComponentList();
  $_SESSION['ComponentList'] = $initializedComponentList;
}


if (!isset ($_SESSION['RackList'])) {
   $initializedRackList = new RackList();
   if (!$initializedRackList->loadFromDBForRack($conn, $_SESSION['roomConstraint'])) {
       include('../common/db_error.php');
       exit;
   }
   $_SESSION['RackList'] = $initializedRackList;
}

if (!isset ($_SESSION['rackContentsList'])) {
   $initializedRackContentsList = new RackContentsList();
   if (!$initializedRackContentsList->loadFromDB($conn, $rackName)) {
       include('../common/db_error.php');
       exit;
   }
   $_SESSION['rackContentsList'] = $initializedRackContentsList;
}

if (!isset ($_SESSION['CCSumList'])) {
   $initializedCCSumList = new CCSumList();
   if (!$initializedCCSumList->loadCCSumFromDB($conn, $compID)) {
       include('../common/db_error.php');
       exit;
   }
   $_SESSION['CCSumList'] = $initializedCCSumList;
}


if (!isset ($_SESSION['rackCCList'])) {
   $initializedRackCCList = new RackCCList();
   if (!$initializedRackCCList->loadFromDBCC($conn, $compID)) {
       include('../common/db_error.php');
       exit;
   }
   $_SESSION['rackCCList'] = $initializedRackCCList;
}

if (!isset ($_SESSION['buildingList'])) {
   $initializedBuildingList = new buildingList();
   if (!$initializedBuildingList->loadFromDB($conn)) {
       include('../common/db_error.php');
       exit;
   }
   $_SESSION['buildingList'] = $initializedBuildingList;
}

if (!isset ($_SESSION['roomList'])) {
   $initializedRoomList = new roomList();
   if (!$initializedRoomList->loadFromDB($conn)) {
       include('../common/db_error.php');
       exit;
   }
   $_SESSION['roomList'] = $initializedRoomList;
}

if (!isset ($_SESSION['groupNameList'])) {
   $initializedGroupNameList = new groupNameList();
   if (!$initializedGroupNameList->loadFromDB($conn)) {
       include('../common/db_error.php');
       exit;
   }
   $_SESSION['groupNameList'] = $initializedGroupNameList;
}

if (!isset ($_SESSION['switchgearList'])) {
   $initializedSwitchgearList = new switchgearList();
   if (!$initializedSwitchgearList->loadFromDB($conn)) {
       include('../common/db_error.php');
       exit;
   }
   $_SESSION['switchgearList'] = $initializedSwitchgearList;
}

if (!isset ($_SESSION['SGList'])) {
   $initializedSGList = new SGList();
   if (!$initializedSGList->loadFromDBForSG($conn, $_SESSION['switchgearConstraint'])) {
       include('../common/db_error.php');
       exit;
   }
   $_SESSION['SGList'] = $initializedSGList;
}

if (!isset ($_SESSION['sgContentsList'])) {
   $initializedSGContentsList = new SGContentsList();
   if (!$initializedSGContentsList->loadFromDB($conn, $_SESSION['SGName'])) {
       include('../common/db_error.php');
       exit;
   }
   $_SESSION['sgContentsList'] = $initializedSGContentsList;
}



?>
