<?php  
/*
 * PV Viewer Common Include 
 * Common file for header of all top-level php pages of PV Viewer application.
 * Defines any needed classes and functions, and establishes needed default data
 * in session if it does not already exist.
 */

// logging util
include_once ('../util/i_logging.php');

// db access layer
include_once ('../db/i_db_connect.php');
include_once ('../db/i_db_querybuilder.php');
include_once ('../db/i_db_entity_iocboot.php');
include_once ('../db/i_db_entity_rec.php');
include_once ('../db/i_db_entity_fld.php');
include_once ('../db/i_db_entity_link.php');

// pv viewer specific includes
include_once ('enum/i_enum_record_type.php');
include_once ('util/i_pv_utils.php');
include_once ('i_pv_search_choices.php');

// all php applications need this
include_once ('../common/i_common_common.php');

// establish connection to db (note: actually re-uses pooled connections)
// args are: host, port, dbname, user, passwd, tableNamePrefix
$dbConnManager = new DBConnectionManager("maia","3306","claude","saunders","spittle","");
//$dbConnManager = new DBConnectionManager("bacchus","3306","irmis","trust-read","hiawj","");
if (!$conn = $dbConnManager->getConnection()) {
    include('pv_error.php');
    exit;
}

// Store core search options in session
if (!isset ($_SESSION['iocBootList'])) {
    $iocBootList = new IOCBootList();
    if (!$iocBootList->loadFromDB($conn)) {
        include('pv_error.php');
        exit;    
    }
    $_SESSION['iocBootList'] = $iocBootList;
}
// Initialize search choices if not in session already
if (!isset ($_SESSION['PVSearchChoices'])) {
    $_SESSION['PVSearchChoices'] = 
        new PVSearchChoices(array ('-1'), array ('-1'), array ('-1'), null, null, null);
}

?>
