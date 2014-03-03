<?php
    /* Cable Search Action handler
     * Conduct specified search, and then render results page.
     */
    include_once('i_common.php');
    
    // log post data here to help debugging (note: we don't really have any for this demo)
    // logEntry('debug',"action_comp_search.php: POST DATA ".print_r($_POST,true));

    // put post data into session
    $_SESSION['label'] = $_POST['label'];
	$_SESSION['ctName'] = $_GET['ct'];
	$_SESSION['ctRoom'] = $_GET['room'];
	$_SESSION['ctRack'] = $_GET['rack'];
	$_SESSION['ctioc'] = $_GET['ioc'];
	$_SESSION['iName'] = $_GET['iName'];
	$_SESSION['ID'] = $_GET['ID'];
	$_SESSION['cableReset'] = $_POST['cableReset'];
	
	if ($_SESSION['cableReset'] == "Reset") {
	  include_once ('cable.php');
	  exit;
	  }
 
    // perform cable search
    $CableList = new CableList();
     logEntry('debug',"Performing cable search");

    $ID = $_GET['ID'];    //$ID = the component_id

    if (!$CableList->loadFromDB($conn, $_SESSION['label'], $ID)) {
		include('../common/db_error.php');
        exit;    
    }                
	
	$CableArray = &$CableList->getArray();
	$idx = 0;
	  foreach ($CableArray as $CableEntity) {
	    $CableEntity->loadDetailsFromDB($conn);
		$CableList->setElement($idx, $CableEntity);
		$idx++;
	  }
	
	$_SESSION['CableList'] = $CableList;
	$_SESSION['CableListSize'] = $CableList->length();

    If ($ID) {
		include_once ('cable_search_results.php');
	} else {
		include_once ('cable.php');
	}

?>
