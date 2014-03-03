<?php
    /* Component Type Search Action handler
     * Conduct specified search, and then render results page.
     */
    include_once('i_common.php');
    
    // log post data here to help debugging (note: we don't really have any for this demo)
    // logEntry('debug',"action_comp_search.php: POST DATA ".print_r($_POST,true));

    // put post data into session
    $_SESSION['nameDesc'] = $_POST['nameDesc'];
	$_SESSION['mfgConstraint'] = $_POST['mfgConstraint'];
	$_SESSION['ffConstraint'] = $_POST['ffConstraint'];
	$_SESSION['functionConstraint'] = $_POST['functionConstraint'];
	$_SESSION['blConstraint'] = $_POST['blConstraint'];
	$_SESSION['personConstraint'] = $_POST['personConstraint'];
	$_SESSION['chcPersonConstraint'] = $_POST['chcPersonConstraint'];
	$_SESSION['chcReset'] = $_POST['chcReset'];
	
	if ($_SESSION['chcReset'] == "Reset") {
	  include_once ('chc.php');
	  exit;
	  }
    
    // perform component search
    $ComponentTypeList = new ComponentTypeList();
    // logEntry('debug',"Performing component search");
	
	
	// $ctID and $jmp are fed here from the i_contents_search_results page
	// (VME Crate Hardware Contents page)
	$ctID = $_GET['ctID'];
	$jmp = $_GET['jmp'];
	
	logEntry('debug', $ctID, $jmp);
	
    
    if (!$ComponentTypeList->loadFromDB($conn, $_SESSION['nameDesc'], 
                                        $_SESSION['mfgConstraint'],
                                        $_SESSION['ffConstraint'],
                                        $_SESSION['functionConstraint'],
										$_SESSION['blConstraint'],
										$_SESSION['personConstraint'],
										$_SESSION['chcPersonConstraint'], $ctID)) {
        include('../common/db_error.php');
        exit;    
    }                
    logEntry('debug',"Search gave ".$ComponentTypeList->length()." results.");
         
    // stuff the loaded ComponentTypeList in the session, replacing the one
    //  that was there before
    $_SESSION['ComponentTypeList'] = $ComponentTypeList;
        
    // store num results separately in session
    $_SESSION['ComponentTypeListSize'] = $ComponentTypeList->length();
   
    // If jmp==1, this means that this page is being
	// referred to from the components listed on the VME Crate Hardware Contents page.
	// In this case, bypass the comp_search_results page and display the details for
	// the component that was clicked on.
	// Otherwise, display the comp_search_results page because the it's being referred to 
	// from the component search page.
    if ($jmp==1) {
	$_GET['ID'] = $ctID;
	include_once ('action_chc_details_search.php');
	}
	else {
    include_once('chc_search_results.php');
	}
?>
