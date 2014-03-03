<?php

    /* AOI Edit Search Action handler
     * Conduct specified search, and then render results page.
     */

    include_once('i_common.php');

	//echo session_id();


    // log post data here to help debugging (note: we don't really have any for this demo)
    // logEntry('debug',"action_aoi_search.php: POST DATA ".print_r($_POST,true));

    // put post data into session

		$_SESSION['aoiNameConstraint'] = $_POST['aoiNameConstraint'];

		$_SESSION['aoiReset'] = $_POST['aoiReset'];

		$_SESSION['techsystem'] = $_POST['techsystem'];

		$_SESSION['machine'] = $_POST['machine'];
		$_SESSION['plcname'] = $_POST['plcname'];



	if($_POST['iocname'])
	{
           $_SESSION['iocname'] = $_POST['iocname'];
	}else
	{
	   $_SESSION['iocname'] = $_GET['iocname'];
	}

		$_SESSION['person'] = $_POST['person'];
		$_SESSION['aoi_keyword'] = $_POST['aoi_keyword'];
		$_SESSION['aoi_worklog'] = $_POST['aoi_worklog'];



	if($_POST['pv_search'])
	{
	   $_SESSION['pv_search'] = $_POST['pv_search'];
	}else
	{
	   $_SESSION['pv_search'] = $_GET['pv_search'];
	}

	if($_POST['desc_search'])
	{
	   $_SESSION['desc_search'] = $_POST['desc_search'];
	}else
	{
	   $_SESSION['desc_search'] = $_GET['desc_search'];
	}

	if($_SESSION['desc_search']){
		$aoiDescSearchConstraint = $_SESSION['desc_search'];
	}

	if($_POST['criticality']){
		$_SESSION['criticality'] = $_POST['criticality'];
	}
	if($_POST['editAOIs']){
		$_SESSION['aoiEdit'] = $_POST['editAOIs'];
	}


	if ($_SESSION['aoiReset'] == "Reset") {
	  include_once('aoi_edit.php');
	  exit;
	}

	// Reset indicator of user selecting a single aoi to view details

	$_SESSION['aoi_selected'] = "false";

   	// perform aoi search using user criteria

   	$aoiList = new AOIList();

   	// logEntry('debug',"Performing aoi search");

   	$aoi_name_constraint = $_SESSION['aoiNameConstraint'];


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

   	// store num results separately in session

   	$_SESSION['aoiListSize'] = & $aoiList->length();

    	include_once('aoi_edit_search_results.php');

    // echo 'made it through action_aoi_edit_search.php\n';

?>
