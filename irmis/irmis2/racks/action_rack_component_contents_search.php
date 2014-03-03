<?php
    /* Rack contents Search Action handler
     * Conduct specified search, and then render results page.
     */
    include_once('i_common.php');
    
    // log post data here to help debugging (note: we don't really have any for this demo)
    //logEntry('debug',"action_rack_contents_search.php: POST DATA ".print_r($_POST,true));

    // put post data into session
    $_SESSION['rackName'] = $_POST['rackName'];
	$_SESSION['rack'] = $_POST['rack'];
	$_SESSION['rackType'] = $_POST['rackType'];
	$_SESSION['rackReset'] = $_POST['rackReset'];
	$_SESSION['compName'] = $_POST['compName'];
	
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
	  
	if ($_GET['compID']) {
	  $compID = $_GET['compID'];
	  // put the rackName into the rackNameConstraint in the SESSION
	  $_SESSION['compID'] = $compID;
      }
	  
	if ($_GET['compName']) {
	  $compName = $_GET['compName'];
	  // put the rackName into the rackNameConstraint in the SESSION
	  $_SESSION['compName'] = $compName;
      }
	  
     $CCSumList = new CCSumList();
      //logEntry('debug',"Performing rack contents search");
    
     if (!$CCSumList->loadCCSumFromDB($conn, $_SESSION['compID'])) {
        include('demo_error.php');
        exit;    
    }                
      //logEntry('debug',"Search gave ".$CCSumList->length()." results.");
         
      // stuff the loaded rackContentsList in the session, replacing the one
      //  that was there before
      $_SESSION['CCSumList'] = $CCSumList;
	
    $rackCCList = new RackCCList();
    //logEntry('debug',"Performing rack contents search");
    
    if (!$rackCCList->loadFromDBCC($conn, $_SESSION['compID'])) {
        include('demo_error.php');
        exit;    
    }                
    //logEntry('debug',"Search gave ".$rackCCList->length()." results.");
         
    // stuff the loaded rackContentsList in the session, replacing the one
    //  that was there before
    $_SESSION['rackCCList'] = $rackCCList;
        
    // store num results separately in session
    $_SESSION['rackCCListSize'] = $rackCCList->length();
   
     include('rack_component_contents_search_results.php');
   //}
?>
