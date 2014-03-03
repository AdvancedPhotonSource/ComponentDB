<?php
    /* Rack Search Action handler
     * Conduct specified search, and then render results page.
     */
    include_once('i_common.php');
    
    // log post data here to help debugging (note: we don't really have any for this demo)
    // logEntry('debug',"action_comp_search.php: POST DATA ".print_r($_POST,true));

    // put post data into session
    $_SESSION['nameDesc'] = $_POST['nameDesc'];
	$_SESSION['buildingConstraint'] = $_POST['buildingConstraint'];
	$_SESSION['roomConstraint'] = $_POST['roomConstraint'];
	$_SESSION['groupNameConstraint'] = $_POST['groupNameConstraint'];
	$_SESSION['parent_id'] = $_POST['parent_id'];
	$_SESSION['rackReset'] = $_POST['rackReset'];
	//$_SESSION['component_id'] = $_POST['component_id'];
	
	if ($_SESSION['rackReset'] == "Reset") {
	  include_once ('racks.php');
	  exit;
	  }
    
    // perform rack search
    $RackList = new RackList();
    // logEntry('debug',"Performing rack search");
    
    if (!$RackList->loadFromDBForRack($conn, $_SESSION['roomConstraint'])){
        include('../common/db_error.php');
        exit;    
    }                
    logEntry('debug',"Search gave ".$RackList->length()." results. (action php)");
         
    // stuff the loaded RackList in the session, replacing the one
    //  that was there before
    $_SESSION['RackList'] = $RackList;
        
    // store num results separately in session
    $_SESSION['RackListSize'] = $RackList->length();
   
    include_once('racks_search_results.php');
?>
