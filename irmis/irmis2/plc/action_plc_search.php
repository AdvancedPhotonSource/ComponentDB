<?php
    /* PLC Search Action handler
     * Conduct specified search, and then render results page.
     */
    include_once('i_common.php');

    // log post data here to help debugging (note: we don't really have any for this demo)
    // logEntry('debug',"action_plc_search.php: POST DATA ".print_r($_POST,true));

    // put post data into session
    $_SESSION['plcAdmin'] = $_POST['plcAdmin'];

    if ($_POST['plcNameConstraint'])
    {
        $_SESSION['plcNameConstraint'] = $_POST['plcNameConstraint'];
    }else{
        $_SESSION['plcNameConstraint'] = $_GET['plcNameConstraint'];
    }
    $_SESSION['plcReset'] = $_POST['plcReset'];
    $_SESSION['system'] = $_POST['system'];
    $_SESSION['developer'] = $_POST['developer'];

    if ($_SESSION['plcReset'] == " Reset ") {
        include_once ('plc.php');
        exit;
    }


    // perform plc search
	$plcList = new PLCList();
	logEntry('debug',"Performing plc search");

	if (!$plcList->loadFromDB($conn_2, $_SESSION['plcNameConstraint'])) {
		include('demo_error.php');
		exit;
	}
	logEntry('debug',"Search gave ".$plcList->length()." results.");

	// stuff the loaded plcList in the session, replacing the one
	//  that was there before
	$_SESSION['plcList'] = $plcList;

	// store num results separately in session
	$_SESSION['plcListSize'] = $plcList->length();


    if ($_SESSION['plcAdmin'] == "Administrative Page") {
        include_once('plc_admin_results.php');
    }

    else { // then they must have clicked "PLC Search"
        include_once('plc_search_results.php');
    }
?>
