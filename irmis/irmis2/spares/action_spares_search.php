<?php
    /* Spares Search Action handler
     * Conduct specified search, and then render results page.
     */
    include_once('i_common.php');
    
    // log post data here to help debugging (note: we don't really have any for this demo)
    // logEntry('debug',"action_comp_search.php: POST DATA ".print_r($_POST,true));

    // put post data into session
    $_SESSION['nameDesc'] = $_POST['nameDesc'];
	$_SESSION['noSpare'] = $_POST['noSpare'];
	$_SESSION['criticalSpare'] = $_POST['criticalSpare'];
	$_SESSION['spareLocation'] = $_POST['spareLocation'];
	$_SESSION['mfgConstraint'] = $_POST['mfgConstraint'];
	//$_SESSION['ffConstraint'] = $_POST['ffConstraint'];
	//$_SESSION['functionConstraint'] = $_POST['functionConstraint'];
	$_SESSION['personConstraint'] = $_POST['personConstraint'];
	$_SESSION['sparesReset'] = $_POST['sparesReset'];
	
	if ($_SESSION['sparesReset'] == "Reset") {
	  unset($_SESSION['criticalSpare']);
	  unset($_SESSION['noSpare']);
	  unset($_SESSION['spareLocation']);
	  include_once ('spares.php');
	  exit;
	  }
	  
    
    // perform spare search
    $SpareList = new SpareList();
    // logEntry('debug',"Performing spare search");
    
    if (!$SpareList->loadFromDB($conn, $_SESSION['nameDesc'], 
                                        $_SESSION['mfgConstraint'],
										$_SESSION['personConstraint'])) {
        include('../common/db_error.php');
        exit;    
    }                
    logEntry('debug',"Search gave ".$SpareList->length()." results.");
         
    // stuff the loaded SpareList in the session, replacing the one
    //  that was there before
    $_SESSION['SpareList'] = $SpareList;
        
    // store num results separately in session
    $_SESSION['SpareListSize'] = $SpareList->length();
   
    include_once('spares_search_results.php');
?>
