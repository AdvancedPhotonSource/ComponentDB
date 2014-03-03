<?php  
/*
 * PV Viewer Common Include 
 * Common file for header of all top-level php pages of PV Viewer application.
 * Defines any needed classes and functions, and establishes needed default data
 * in session if it does not already exist.
 */
// NOTE: below must be included before session_start
include_once ('../util/i_logging.php');
include_once ('../db/i_db_connect.php');
include_once ('../db/i_db_querybuilder.php');
include_once ('../db/i_db_entity_ioc.php');
include_once ('../db/i_db_entity_rec.php');
include_once ('../db/i_db_entity_fld.php');
include_once ('../db/i_db_entity_link.php');
include_once ('enum/i_enum_record_type.php');
include_once ('util/i_pv_utils.php');
include_once ('i_pv_search_choices.php');
session_start();
header("Cache-control: private");

// establish connection to db (note: actually re-uses pooled connections)
// args are: host, port, dbname, user, passwd, tableNamePrefix
$dbConnManager = new DBConnectionManager("host","3306","db name","username","password","");
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
