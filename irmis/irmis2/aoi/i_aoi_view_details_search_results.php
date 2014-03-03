<?php
    /* AOI Details Search Results Page
     * Display aoi detailed information results
     */

    include_once('i_common.php');
?>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
"http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<script type="text/javascript" src="../report/selectDeselectAll.js"></script>
<title>AOI Details Search Results Page</title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
<link href="../common/irmis_aoi.css" rel="stylesheet" type="text/css">
</head>
<body bgcolor="#999999">
<?php

	$_SESSION['aoi_name'] = $_GET['aoi_name'];
	$_SESSION['aoi_id'] = $_GET['aoi_id'];

	$temp_aoi_id = 0;

    $temp_aoi_name = $_SESSION['aoi_name'];
    $temp_aoi_id = $_SESSION['aoi_id'];

    if ($temp_aoi_name == "" || $temp_aoi_name == NULL){
		echo "AOI name is the empty string";
		exit;
    }

    // echo "at top of aoi details, temp_aoi_id is: $temp_aoi_id";
    // echo "at top of aoi details, temp_aoi_name is: $temp_aoi_name";

    include('../common/i_irmis_aoi_header.php');
    include('i_aoi_edit_search_criteria.php');
    include('i_aoi_edit_name_search_results.php');

    include_once('i_aoi_view_basics_search_results.php');


    //Generate set of plc's and ioc's for the specific aoi being viewed
	// perform aoi plc search and ioc search

	$aoiPlcList = new AOIPLCList();
	logEntry('debug',"Performing aoi plc search");

	if (!$aoiPlcList->loadFromDB($conn, $temp_aoi_id)) {
	        		include('demo_error.php');
		  			echo "Could not load aoiPlcList from database.";
	        		exit;
	}
	logEntry('debug',"Search gave ".$aoiPlcList->length()." results. ");


	// stuff the loaded aoiPlcList in the session, replacing the one
	//  that was there before
	$_SESSION['aoiPlcList'] = $aoiPlcList;

	// store num results separately in session
    $_SESSION['aoiPlcListSize'] = $aoiPlcList->length();

    $aoiIocList = new AOIIOCList();
	logEntry('debug',"Performing aoi ioc search");

	if (!$aoiIocList->loadFromDB($conn, $temp_aoi_id)) {
					        		include('demo_error.php');
					echo "Could not load aoiIocList from database.";
					exit;
	}
	logEntry('debug',"Search gave ".$aoiIocList->length()." results. ");

	// stuff the loaded aoiIocList in the session, replacing the one
	//  that was there before
	$_SESSION['aoiIocList'] = $aoiIocList;

	// store num results separately in session
    $_SESSION['aoiIocListSize'] = $aoiIocList->length();

    include_once('i_aoi_view_upc_search_results.php');

    //Generate set of epics pv records for the specific aoi being viewed
    // perform aoi epics pv record search

    $aoiPvList = new AOIPVList();
    logEntry('debug',"Performing aoi epics pv record search");

    if (!$aoiPvList->loadFromDB($conn, $temp_aoi_name)) {
        		include('demo_error.php');
	  			echo "Could not load aoiPvList from database.";
        		exit;
    }
    logEntry('debug',"Search gave ".$aoiPvList->length()." results. ");

    // stuff the loaded aoiPvList in the session, replacing the one
    //  that was there before
    $_SESSION['aoiPvList'] = $aoiPvList;

    // store num results separately in session
    $_SESSION['aoiPvListSize'] = $aoiPvList->length();

    include_once('i_aoi_view_pv_search_results.php');

	//Generate set of epics st.cmd lines for the specific aoi being viewed
	// perform aoi st.cmd line search

	$aoiStCmdLineList = new AOIStCmdLineList();
	logEntry('debug',"Performing aoi st.cmd line search");

	echo "temp aoi id is: $temp_aoi_id";

	if (!$aoiStCmdLineList->loadFromDB($conn, $temp_aoi_id)) {
			       	include('demo_error.php');
				  	echo "Could not load aoiStCmdLineList from database.";
			      	exit;
	}
	logEntry('debug',"Search gave ".$aoiStCmdLineList->length()." results. ");

	// stuff the loaded aoiStCmdLineList in the session, replacing the one
	//  that was there before
	$_SESSION['aoiStCmdLineList'] = $aoiStCmdLineList;

	// store num results separately in session
    $_SESSION['aoiStCmdLineListSize'] = $aoiStCmdLineList->length();

    include_once('i_aoi_view_topdisplay_search_results.php');
    include_once('i_aoi_view_document_search_results.php');
    // include_once('i_aoi_view_revhistory_search_results.php');
    include_once('i_aoi_view_stcmd_line_search_results.php');

?>
</body>
</html>
