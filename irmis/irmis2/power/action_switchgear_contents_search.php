<?php
    /* Rack contents Search Action handler
     * Conduct specified search, and then render results page.
     */
    include_once('i_common.php');
    
    // log post data here to help debugging (note: we don't really have any for this demo)
    // logEntry('debug',"action_rack_contents_search.php: POST DATA ".print_r($_POST,true));

    // put post data into session
	
    $_SESSION['SGName'] = $_POST['SGName'];
	$_SESSION['SG'] = $_POST['SG'];
	$_SESSION['SGType'] = $_POST['SGType'];
	$_SESSION['rackReset'] = $_POST['rackReset'];
	
	if ($_SESSION['rackReset'] == "Reset") {
	  include_once ('rack.php');
	  exit;
	  }
	
      // perform rack contents search
	if ($_GET['SGName']) {
	  $sgName = $_GET['SGName'];
	  // put the rackName into the rackNameConstraint in the SESSION
	  $_SESSION['SGName'] = $sgName;
	  }
	  
	  // perform rack contents search
	if ($_GET['SG']) {
	  $sg = $_GET['SG'];
	  // put the rackName into the rackNameConstraint in the SESSION
	  $_SESSION['SG'] = $sg;
      }
	  
	  // perform rack contents search
	if ($_GET['SGType']) {
	  $sgType = $_GET['SGType'];
	  // put the rackName into the rackNameConstraint in the SESSION
	  $_SESSION['SGType'] = $sgType;
      }
	  
      $sgContentsList = new SGContentsList();
      // logEntry('debug',"Performing rack contents search");
    
     if (!$sgContentsList->loadFromDB($conn, $_SESSION['SGName'])) {
          include('demo_error.php');
          exit;    
      }                
      // logEntry('debug',"Search gave ".$rackContentsList->length()." results.");
         
      // stuff the loaded sgContentsList in the session, replacing the one
      // that was there before
      $_SESSION['sgContentsList'] = $sgContentsList;
        
      // store num results separately in session
      $_SESSION['sgContentsListSize'] = $sgContentsList->length();
   
       include('switchgear_contents_search_results.php');
?>
