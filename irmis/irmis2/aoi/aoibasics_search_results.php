<?php
    /* AOI Basics Search Results Page
     * Display basic aoi info results
     */

    include_once('i_common.php');
?>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
"http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>


<title>AOI Basics Search Results Page</title>
<link rel="icon" type="image/png" href="../common/images/irmisiconLogo.png" />
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
<link href="../common/irmis_aoi.css" rel="stylesheet" type="text/css">
<?php include('../top/analytics.php'); ?>
</head>
<body bgcolor="#999999">
<?php

   $_SESSION['aoi_id'] = $_GET['aoiId'];

   $_SESSION['aoi_name'] = $_GET['aoiName'];

   $temp_aoi_name = $_SESSION['aoi_name'];
   $temp_aoi_id = $_SESSION['aoi_id'];

   if ($temp_aoi_name == "" || $temp_aoi_name == NULL){
		echo "AOI name is the empty string  ";
		exit;
   }


    // Generate the basic information for an aoi
    $aoiBasicList = new AOIBasicList();
    logEntry('debug',"Performing aoi basic information search");

    if (!$aoiBasicList->loadFromDB($conn, $temp_aoi_id)) {
    	include('demo_error.php');
    	echo "Search gave no result.";
    	exit;
    }
    logEntry('debug',"Search gave ".$aoiBasicList->length()." results. ");
    $_SESSION['aoiBasicList'] = $aoiBasicList;


 	//Generate set of revhistory for the specific aoi being viewed
	// perform aoi revhistory search

	$aoiRevhistoryList = new AOIRevhistoryList();
	logEntry('debug',"Performing aoi revhistory search");

	if (!$aoiRevhistoryList->loadFromDB($conn, $temp_aoi_id)) {
			      include('demo_error.php');
				  echo "Could not load aoiRevhistoryList from database.";
			      exit;
	}
	logEntry('debug',"Search gave ".$aoiRevhistoryList->length()." results. ");


	// stuff the loaded aoiRevhistoryList in the session, replacing the one
	//  that was there before
	$_SESSION['aoiRevhistoryList'] = $aoiRevhistoryList;

	// store num results separately in session
	$_SESSION['aoiRevhistoryListSize'] = $aoiRevhistoryList->length();

	include('../common/i_irmis_aoi_header.php');
	include('i_aoi_search_criteria.php');
    include('i_aoi_name_search_results.php');
    include_once('i_aoi_basics_search_results.php');
    include_once('i_aoi_revhistory_search_results.php');
    include_once('aoidetails_search_results.php');

?>
</body>
</html>
