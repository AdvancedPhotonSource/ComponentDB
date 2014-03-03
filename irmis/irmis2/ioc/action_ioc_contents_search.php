<?php
    /* IOC Search Action handler
     * Conduct specified search, and then render results page.
     */
    include_once('i_common.php');
    
    // log post data here to help debugging (note: we don't really have any for this demo)
    logEntry('debug',"action_ioc_contents_search.php: POST DATA ".print_r($_POST,true));

    // put post data into session
    $_SESSION['iocNameConstraint'] = $_POST['iocNameConstraint'];
	$_SESSION['iocReset'] = $_POST['iocReset'];
	$_SESSION['system'] = $_POST['system'];
	$_SESSION['status'] = $_POST['status'];
	$_SESSION['developer'] = $_POST['developer'];
	$_SESSION['tech'] = $_POST['tech'];
	
	if ($_SESSION['iocReset'] == "Reset") {
	  include_once ('ioc.php');
	  exit;
	  }
 
	
	if ($_GET['componentID']) {
	  $componentID = $_GET['componentID'];
	  // put the ioc component ID into the SESSION
	  $_SESSION['componentID'] = $componentID;
	  }	
      // perform ioc contents search
	  
	if ($_GET['iocName']) {
	  $iocName = $_GET['iocName'];
	  // put the iocName into the iocNameConstraint in the SESSION
	  $_SESSION['iocName'] = $iocName;
      }
	
	if ($_GET['room']) {
		$room = $_GET['room'];
		$_SESSION['room'] = $room;
	}
	
	if ($_GET['rack']) {
		$rack = $_GET['rack'];
		$_SESSION['rack'] = $rack;
	}
		
    $iocContentsList = new IOCContentsList();
    logEntry('debug',"Performing ioc contents search");
    
    if (!$iocContentsList->loadFromDB($conn, $_SESSION['iocName'], $_SESSION['componentID'])) {
        include('demo_error.php');
        exit;    
    }                
    logEntry('debug',"Search gave ".$iocContentsList->length()." results.");
         
    // stuff the loaded iocContentsList in the session, replacing the one
    //  that was there before
    $_SESSION['iocContentsList'] = $iocContentsList;
        
    // store num results separately in session
    $_SESSION['iocContentsListSize'] = $iocContentsList->length();
   
   //if ($componentID = "none") {
   //  include ('ioccontents_search_results.php');
   //} else {
     include('ioccontents_search_results.php');
?>
