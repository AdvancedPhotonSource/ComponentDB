<?php
    /* Rack contents Search Action handler
     * Conduct specified search, and then render results page.
     */
    include_once('i_common.php');
    
    // log post data here to help debugging (note: we don't really have any for this demo)
    // logEntry('debug',"action_rack_contents_search.php: POST DATA ".print_r($_POST,true));

    // put post data into session
    $_SESSION['rackName'] = $_POST['rackName'];
	$_SESSION['rack'] = $_POST['rack'];
	$_SESSION['rackType'] = $_POST['rackType'];
	$_SESSION['rackReset'] = $_POST['rackReset'];
	
	if ($_SESSION['rackReset'] == "Reset") {
	  include_once ('rack.php');
	  exit;
	  }
	
      // perform rack contents search
	if ($_GET['rackName']) {
	  $rackName = $_GET['rackName'];
	  // put the rackName into the rackNameConstraint in the SESSION
	  $_SESSION['rackName'] = $rackName;
	  }
	  
	  // perform rack contents search
	if ($_GET['rack']) {
	  $rack = $_GET['rack'];
	  // put the rackName into the rackNameConstraint in the SESSION
	  $_SESSION['rack'] = $rack;
      }
	  
	  // perform rack contents search
	if ($_GET['rackType']) {
	  $rackType = $_GET['rackType'];
	  // put the rackName into the rackNameConstraint in the SESSION
	  $_SESSION['rackType'] = $rackType;
      }
	  
      $rackContentsList = new RackContentsList();
      // logEntry('debug',"Performing rack contents search");
    
     if (!$rackContentsList->loadFromDB($conn, $_SESSION['rackName'])) {
          include('demo_error.php');
          exit;    
      }                
      // logEntry('debug',"Search gave ".$rackContentsList->length()." results.");
         
      // stuff the loaded rackContentsList in the session, replacing the one
      // that was there before
      $_SESSION['rackContentsList'] = $rackContentsList;
        
      // store num results separately in session
      $_SESSION['rackContentsListSize'] = $rackContentsList->length();
   
       include('power_contents_search_results.php');
?>
