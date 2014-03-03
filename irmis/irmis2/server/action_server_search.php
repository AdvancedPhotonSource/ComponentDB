<?php
    /* Server Search Action handler
     * Conduct specified search, and then render results page.
     */
    include_once('i_common.php');

    // log post data here to help debugging (note: we don't really have any for this demo)
    logEntry('debug',"action_server_search.php: POST DATA ".print_r($_POST,true));

    // put post data into session
    if ($_POST['serverNameConstraint'])
    {
        $_SESSION['serverNameConstraint'] = $_POST['serverNameConstraint'];
    }else
    {
        $_SESSION['serverNameConstraint'] = $_GET['serverNameConstraint'];
    }
	$_SESSION['serverReset'] = $_POST['serverReset'];
	$_SESSION['operating_system'] = $_POST['operating_system'];
	$_SESSION['server_cognizant'] = $_POST['server_cognizant'];

	if ($_SESSION['serverReset'] == "Reset") {
	  include_once ('server.php');
	  exit;
	}

	// if the serverName comes from the component instance page,
	// set the serverNameConstraint equal to it

	// if the serverName has been passed from the component instance
	// page, perform the following

	if ($_GET['serverName']) {
	  $serverName = $_GET['serverName'];

	  // put the serverName into the serverNameConstraint in the SESSION
	  $_SESSION['serverNameConstraint'] = $serverName;

	  // perform server search
	  $serverList = new ServerList();
	  $serverList->loadFromDB($conn, $_SESSION['serverNameConstraint']);

	  // put the loaded serverList into the SESSION
	  $_SESSION['serverList'] = $serverList;
	  include ('../server/server_search_results.php');
	  exit;
	}

    // perform server search
    $serverList = new ServerList();
    logEntry('debug',"Performing server search");

    if (!$serverList->loadFromDB($conn, $_SESSION['serverNameConstraint'])) {
        include('demo_error.php');
        exit;
    }
    logEntry('debug',"Search gave ".$serverList->length()." results.");

    // stuff the loaded serverList in the session, replacing the one
    //  that was there before
    $_SESSION['serverList'] = $serverList;

    // store num results separately in session
    $_SESSION['serverListSize'] = $serverList->length();

    // echo "server list size: ".$_SESSION['serverListSize'];

    include('server_search_results.php');

?>
