<?php
    /* IOC Search Action handler
     * Conduct specified search, and then render results page.
     */
    include_once('i_common.php');
    
    // log post data here to help debugging (note: we don't really have any for this demo)
    logEntry('debug',"action_ioc_search.php: POST DATA ".print_r($_POST,true));

    // put post data into session
    if ($_POST['iocNameConstraint'])
    {
        $_SESSION['iocNameConstraint'] = $_POST['iocNameConstraint'];
    }else
    {
        $_SESSION['iocNameConstraint'] = $_GET['iocNameConstraint'];
    }
	$_SESSION['Accioc'] = $_POST['Accioc'];
	$_SESSION['BLioc'] = $_POST['BLioc'];
	$_SESSION['iocReset'] = $_POST['iocReset'];
	$_SESSION['system'] = $_POST['system'];
	$_SESSION['status'] = $_POST['status'];
	$_SESSION['developer'] = $_POST['developer'];
	$_SESSION['tech'] = $_POST['tech'];
	
	if ($_SESSION['iocReset'] == "Reset") {
	  unset($_SESSION['BLioc']);
	  $_SESSION['Accioc'] = 'checked'; // this makes the default "checked"
	  include_once ('ioc.php');
	  exit;
	  }
    
	// if the iocName comes from the component instance page,
	// set the iocNameConstraint equal to it
	
	// if the iocName has been passed from the component instance
	// page, perform the following
	if ($_GET['iocName']) {
	  $iocName = $_GET['iocName'];
	  // put the iocName into the iocNameConstraint in the SESSION
	  $_SESSION['iocNameConstraint'] = $iocName;
	  // perform ioc search
	  $iocList = new IOCList();
	  $iocList->loadFromDB($conn, $_SESSION['iocNameConstraint']);
	  // put the loaded iocList into the SESSION
	  $_SESSION['iocList'] = $iocList;
	  include ('../ioc/iocgeneral_search_results.php');
	  exit;
	}
	
    // perform ioc search
    $iocList = new IOCList();
    logEntry('debug',"Performing ioc search");
    
    if (!$iocList->loadFromDB($conn, $_SESSION['iocNameConstraint'])) {
        include('demo_error.php');
        exit;    
    }                
    logEntry('debug',"Search gave ".$iocList->length()." results.");
         
		 
    // stuff the loaded iocList in the session, replacing the one
    //  that was there before
    $_SESSION['iocList'] = $iocList;
        
    // store num results separately in session
    $_SESSION['iocListSize'] = $iocList->length();
   
   //if ($iocNameSource = 1) {
    // include ('../ioc/iocgeneral_search_results.php');
   //} else {
     include('ioc_search_results.php');
   //}
?>
