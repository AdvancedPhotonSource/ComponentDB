<?php
/*
 * AOI Application Common Include
 * Common file for header of all top-level php pages of AOI application.
 * Defines any needed classes and functions, and establishes needed default data
 * in session if it does not already exist.
 */

// db access layer

include_once ('../db/i_db_connect.php');
include_once ('../db/i_db_entity_aoi_editor.php');
include_once ('../db/i_db_querybuilder.php');
include_once ('db.inc');

// establish connection to db (note: actually re-uses pooled connections)
// args are: host, port, dbname, user, passwd, tableNamePrefix

$dbConnManager = new
DBConnectionManager($db_host,$db_port,$db_name_production_1,$db_user_read_name,$db_user_read_passwd,"");

if (!$conn = $dbConnManager->getConnection()) {
  include('../common/db_error.php');
  exit;
}
?>
