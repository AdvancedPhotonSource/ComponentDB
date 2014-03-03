<?php
    /* Network Search Action handler
     * Conduct specified search, and then render results page.
     */
    include_once('i_common.php');
    
    // log post data here to help debugging (note: we don't really have any for this demo)
    // logEntry('debug',"action_ioc_search.php: POST DATA ".print_r($_POST,true));

    // put post data into session
	$_SESSION['show_network_switches'] = $_POST['show_network_switches'];
	$_SESSION['show_media_converters'] = $_POST['show_media_converters'];
	$_SESSION['show_terminal_servers'] = $_POST['show_terminal_servers'];
	$_SESSION['show_ioc_connections'] = $_POST['show_ioc_connections'];
	
	// if the Show Switches button has been passed from the 
	// network search criteria page, perform the following
	if ($_POST['show_network_switches'] == "Show Network Switches") {
	  // perform switch search
	  $swList = new swList();
	  $swList->loadFromDB($conn);
	  // put the loaded swList into the SESSION
	  $_SESSION['swList'] = $swList;
	  // logEntry('debug',print_r($swList,true));
	  include ('network_switch_results.php');
	  exit;
	}
	
	if ($_GET['swID']) {
	  $swID = $_GET['swID'];
	  // perform switchsw details search
	  $swDetailsList = new swDetailsList();
	  $swDetailsList->loadFromDB($conn, $swID);
	  // put the loaded swDetailsList into the SESSION
	  $_SESSION['swDetailsList'] = $swDetailsList;
	  // logEntry('debug',"action_net_search.php: POST DATA ".print_r($swDetailsList,true));
	  include ('network_switch_details_results.php');
	  exit;
	}
	

	// if the Show Terminal Servers button has been passed from the 
	// network search criteria page, perform the following
	if ($_POST['show_terminal_servers'] == "Show Terminal Servers") {
	  // perform terminal server search
	  $tsList = new tsList();
	  $tsList->loadFromDB($conn);
	  // put the loaded tsList into the SESSION
	  $_SESSION['tsList'] = $tsList;
	  include ('network_ts_results.php');
	  exit;
	}
	
	if ($_GET['tsID']) {
	  $tsID = $_GET['tsID'];
	  // perform terminal server details search
	  $tsDetailsList = new tsDetailsList();
	  $tsDetailsList->loadFromDB($conn, $tsID);
	  // put the loaded tsDetailsList into the SESSION
	  $_SESSION['tsDetailsList'] = $tsDetailsList;
	  //logEntry('debug',"action_ioc_search.php: POST DATA ".print_r($tsDetails,true));
	  include ('network_ts_details_results.php');
	  exit;
	}
	
	// if the Show Media Converters button has been passed from the 
	// network search criteria page, perform the following
	if ($_POST['show_media_converters'] == "Show Media Converters") {
	  // perform media converter search
	  $mcList = new mcList();
	  $mcList->loadFromDB($conn);
	  // put the loaded mcList into the SESSION
	  $_SESSION['mcList'] = $mcList;
	  // logEntry('debug',print_r($mcList,true));
	  include ('network_mc_results.php');
	  exit;
	}
	
	if ($_GET['mcID']) {
	  $mcID = $_GET['mcID'];
	  // perform media converter details search
	  $mcDetailsList = new mcDetailsList();
	  $mcDetailsList->loadFromDB($conn, $mcID);
	  // put the loaded mcDetailsList into the SESSION
	  $_SESSION['mcDetailsList'] = $mcDetailsList;
	  // logEntry('debug',"action_net_search.php: POST DATA ".print_r($mcDetailsList,true));
	  include ('network_mc_details_results.php');
	  exit;
	}
	
	// if the Show IOC Connection button has been passed from the 
	// network search criteria page, perform the following
	if ($_POST['show_ioc_connections'] == "Show IOC Connections") {
	  // perform ioc connections search
	  $iocConnectionsList = new iocConnectionsList();
	  $iocConnectionsList->loadFromDB($conn);
	  // put the loaded iocConnectionsList into the SESSION
	  $_SESSION['iocConnectionsList'] = $iocConnectionsList;
	  // logEntry('debug',print_r($iocConnectionsList, true));
	  include ('network_iocConnections_results.php');
	  exit;
	}
?>
