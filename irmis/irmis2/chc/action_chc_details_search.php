<?php
    /* Component Type Details Search Action handler
     * Get additional component type details from database.
     * The ID of the component type we are interested in is given as GET data.
     */
    include_once('i_common.php');
    
    // log get data here to help debugging 
    // logEntry('debug',"action_comp_details_search.php: GET DATA ".print_r($_GET,true));

    if($_POST['ID'])
    {
    	$ctID = $_POST['ID'];
    }else
    {
    	$ctID = $_GET['ID'];
    }
    
    // Added block IF statement below for the case where this file is being entered
    // from another IRMIS application that only sends a component_type_id, $ctID
    
    if ( empty($_SESSION['ComponentTypeList']) ) {
    
    	if (!$ComponentTypeList->loadFromDB($conn, "", 
                                                   "",
                                                   "",
                                                   "",
	                                               "", 
												   "",
												   "",
		$ctID)) {
        	include('../common/db_error.php');
        	exit;    
    	}    
    }
    
    $ComponentTypeList = $_SESSION['ComponentTypeList'];
    $compType = $ComponentTypeList->getElementForComponentID($ctID);
	

    // ask compType to load up additional details from database
    if (!$compType->loadDetailsFromDB($conn)) {
      include('../common/db_error.php');
      exit;
    }
	
	// logEntry('debug',"here i am, selectedComponentType is: ".print_r($compType,true));

    // put selected component type into session by itself (easier)
    $_SESSION['selectedComponentType'] = $compType;
         
    include_once('chc_search_results_details.php');
?>
