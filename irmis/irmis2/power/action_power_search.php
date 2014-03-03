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
	$_SESSION['switchgearConstraint'] = $_POST['switchgearConstraint'];
	$_SESSION['parent_id'] = $_POST['parent_id'];
	$_SESSION['powerReset'] = $_POST['powerReset'];
	//$_SESSION['component_id'] = $_POST['component_id'];
	
	if ($_SESSION['powerReset'] == "Reset") {
	  include_once ('power.php');
	  exit;
	  }
    
    // perform rack search
    $RackList = new RackList();
    // logEntry('debug',"Performing rack search");
    
    if (!$RackList->loadFromDBForRack($conn, $_SESSION['roomConstraint'])){
        include('../common/db_error.php');
        exit;    
    }                
    //logEntry('debug',"Search gave ".$RackList->length()." results. (action php)");
         
    // stuff the loaded RackList in the session, replacing the one
    //  that was there before
    $_SESSION['RackList'] = $RackList;
        
    // store num results separately in session
    $_SESSION['RackListSize'] = $RackList->length();
	
	
	
	
	// perform switchgear search
    $SGList = new SGList();
    // logEntry('debug',"Performing switchgear search");
    
    if (!$SGList->loadFromDBForSG($conn, $_SESSION['switchgearConstraint'])){
        include('../common/db_error.php');
        exit;    
    }                
    //logEntry('debug',"Search gave ".SGList->length()." results. (action php)");
         
    // stuff the loaded SGList in the session, replacing the one
    //  that was there before
    $_SESSION['SGList'] = $SGList;
        
    // store num results separately in session
    $_SESSION['SGListSize'] = $SGList->length();
	
	
   /* if this page is being called from the switchgear link in the ioc search results page,
    * put the switchgear name that was passed ($_GET['SG']), into the session and execute the switchgear
	* search query.
    */
   $sgname = $_GET['SG'];
   if ($sgname) {
   $_SESSION['switchgearConstraint'] = $sgname;
   $SGList = new SGList();
   $SGList->loadFromDBForSG($conn, $sgname);
   $_SESSION['SGList'] = $SGList;
   include_once('switchgear_search_results.php');
   }
   
   
   /* if the search criteria is a switchgear search, go to the switchgear_search_results page,
    * otherwise go to the power_search_results page.
	*/
   if ($_SESSION['switchgearConstraint']) {
    include_once('switchgear_search_results.php');
   } else {
	include_once('power_search_results.php');
   }
?>
