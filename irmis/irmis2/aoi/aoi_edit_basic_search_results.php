<?php
    /* AOI Edit Basic Search Results Page
     * Display basic aoi info results
     */

    include_once('i_common.php');
?>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
"http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>

<title>AOI Edit Basic Search Results Page</title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
<link href="../common/irmis_aoi.css" rel="stylesheet" type="text/css">
</head>
<body bgcolor="#999999">
<?php

	include_once('i_common.php');

    if ($_GET['aoiId'] > 0) {

   		$_SESSION['aoi_id'] = $_GET['aoiId'];
   		$_SESSION['aoi_name'] = $_GET['aoiName'];
   		$_SESSION['aoi_selected'] = "true";
   	}

   	$temp_aoi_name = $_SESSION['aoi_name'];
   	$temp_aoi_id = $_SESSION['aoi_id'];

   	if ($_SESSION['create_new_aoi_from_blank'] == "true"){

		// This script was called from the "New AOI From Blank" button on the AOI Edit Tool bar
		// Skip building list of data for a specific aoi

		// Check to see if $_SESSION['aoiList'] exists

		if (!isset($_SESSION['aoiList']) ) {

			//echo "Create new aoiList...\n";

    		$_SESSION['aoi_name'] = "";

		   	$aoiList = new AOIList();

		   	// logEntry('debug',"Performing aoi search");

		   	// $_SESSION['aoiNameConstraint'] = "";

		   	if (!$aoiList->loadFromDB($conn, $_SESSION['aoiNameConstraint'])) {
		    	    include('demo_error.php');
				    echo "Could not load aoiList from database.";
		    	    exit();
		   	}

		   	// logEntry('debug',"Search gave ".$aoiList->length()." results.");

		   	$searchresultslength = & $aoiList->length();

		   	// stuff the loaded aoiList in the session, replacing the one
		   	//  that was there before

		   	$_SESSION['aoiList'] = & $aoiList;

   		}


		$_SESSION['create_new_aoi_from_blank'] = "false";

		//echo "Debugging session name:".session_name()."/n";
		//echo "create new aoi from blank was true.../n";

		include('../common/i_irmis_aoi_header.php');
		include('i_aoi_edit_search_criteria.php');
		include('i_aoi_edit_name_search_results.php');

		include_once('i_aoi_edit_create_new.php');

	}else{

    // echo 'about to instantiate new AOIList...';

	// $dbConnManager = new DBConnectionManager("bacchus","3306","irmis","public-read","aps-irmis","");
	$dbConnManager = new DBConnectionManager($db_host,$db_port,$db_name_production_1,$db_user_read_name,$db_user_read_passwd,"");

	if (!$conn = $dbConnManager->getConnection()) {
	  		include('../common/db_error.php');
	  		exit;
	}

   	$aoiList = new AOIList();

   	// echo 'after creating new AOIList....';

	   		logEntry('debug',"Performing aoi search");

	   		if (!$aoiList->loadFromDB($conn, $_SESSION['aoiNameConstraint'])) {
	    	    		include('demo_error.php');
			    	    echo "Could not load aoiList from database.";
	    	    		exit();
	   		}

	   		logEntry('debug',"Search gave ".$aoiList->length()." results.");

	   		$searchresultslength = $aoiList->length();

	   		// stuff the loaded aoiList in the session, replacing the one
	   		//  that was there before
   		$_SESSION['aoiList'] = $aoiList;

   	if ($temp_aoi_name == "" || $temp_aoi_name == NULL){

   		include('../common/i_irmis_aoi_header.php');
		include('i_aoi_edit_search_criteria.php');

    	include('i_aoi_edit_name_search_results.php');

    	// if( $_SESSION['agent'] == md5($_SESSION['user_name'].$_SERVER['HTTP_USER_AGENT']) )
		// {
		//     include('aoi_editor_submit_authenticated.php');
		// }else{
		//     include('aoi_editor_submit.php');
    	// }

    	include('i_aoi_edit_blank_search_results.php');

   	}else{


    	// Generate the basic information for a specific aoi
    	$aoiBasicList = new AOIBasicList();

    	logEntry('debug',"Performing aoi basic information search.");

   		if (!$aoiBasicList->loadFromDB($conn, $temp_aoi_id)) {
    			include('demo_error.php');
    			echo "Search gave no result.";
    			exit;
   		}
   		logEntry('debug',"Search gave ".$aoiBasicList->length()." results. ");


    	$_SESSION['aoiBasicList'] = $aoiBasicList;

		// Generate set of MEDM top displays for the specific aoi being viewed
		// perform aoi topdisplay search

		$aoiTopdisplayList = new AOITopdisplayList();
		logEntry('debug',"Performing aoi top display search");

		if (!$aoiTopdisplayList->loadFromDB($conn, $temp_aoi_id)) {
				include('demo_error.php');
				echo "Could not load AOITopdisplayList from database.";
				exit;
		}
		logEntry('debug',"Search gave ".$aoiTopdisplayList->length()." results. ");

		// stuff the loaded aoiTopdisplayList in the session, replacing the one
		//  that was there before
		$_SESSION['aoiTopdisplayList'] = $aoiTopdisplayList;

		// Generate set of AOI documents for the specific aoi being viewed
		// perform aoi document search

		$aoiDocumentList = new AOIDocumentList();
		logEntry('debug',"Performing aoi document search");

		if (!$aoiDocumentList->loadFromDB($conn, $temp_aoi_id)) {
				include('demo_error.php');
				echo "Could not load AOIDocumentList from database.";
				exit;
		}
		logEntry('debug',"Search gave ".$aoiTopdisplayList->length()." results. ");

		// Stuff the loaded aoiDocumentList in the session, replacing the one
		//  that was there before
		$_SESSION['aoiDocumentList'] = $aoiDocumentList;

		include('../common/i_irmis_aoi_header.php');
		include('i_aoi_edit_search_criteria.php');

    	include('i_aoi_edit_name_search_results.php');

    	if($_SESSION['create_new_aoi_from_selected'] == "true" || $_SESSION['modify_selected_aoi'] == "true")
    	{
    		include_once('i_aoi_edit_basics_search_results.php');
    	}
    	else{
    		include_once('i_aoi_view_basics_search_results.php');

    		// this is being moved to under AOI Details -- include_once('i_aoi_view_revhistory_search_results.php');

			include_once('i_aoi_view_topdisplay_search_results.php');
			include_once('i_aoi_view_document_search_results.php');
    		include_once('aoidetails_search_results.php');
		}

    }
  }
?>
</body>
</html>
