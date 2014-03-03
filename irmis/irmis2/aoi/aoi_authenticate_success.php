
<?php
    /* AOI Authenticate Success Page
     *
     */

    include_once('i_common.php');

    if( !isset($_SESSION['agent']) || ( $_SESSION['agent'] != md5($_SESSION['user_name'].$_SERVER['HTTP_USER_AGENT']) ) )
    {
    	// Report and log invalid access attempt to AOI Editor

    	echo "Invalid access attempt to AOI Editor.";
    	exit();

    }else{



    	// Made it to successful authentication
    	// Begin AOI Editor session

    	// include_once('i_common.php');

	include_once('i_aoi_edit_common.php');

    	// Initialize session variables

		/*
	 	$techsystem = "";
	 	$_SESSION['techsystem'] = $techsystem;
     		$machine = "";
     		$_SESSION['machine'] = $machine;
     		$plcname = "";
	 	$_SESSION['plcname'] = $plcname;
     		$iocname = "";
     		$_SESSION['iocname'] = $iocname;
	 	$person = "";
	 	$_SESSION['person'] = $person;
	 	$criticality = 0;
	 	$_SESSION['criticality'] = $criticality;
	 	$aoi_keyword = "";
     		$_SESSION['aoi_keyword'] = $aoi_keyword;
     		$pv_search = "";
     		$_SESSION['pv_search'] = $pv_search;
	 	$aoi_name = "";
	 	$_SESSION['aoi_name'] = $aoi_name;
	 	$aoi_id = 0;
	 	$_SESSION['aoi_id'] = $aoi_id;
	 	$aoiNameConstraint = "";
	 	$_SESSION['aoiNameConstraint'] = $aoiNameConstraint;
	 	$relatives = "Yes";
	 	$_SESSION['relatives'] = $relatives;

		*/

    	// Valid user php session, continue on with editing session

	session_write_close();

    	include('../common/i_irmis_aoi_header.php');

	include ('i_aoi_edit_search_criteria.php');


    }
?>
<html>
<head>
<title>AOI Authenticate Success Page</title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
<link href="../common/irmis_aoi.css" rel="stylesheet" type="text/css">
</head>
<body bgcolor="#999999">

	<noscript>

			<?php
				include('javascript_disabled_warning.php');
			?>

	</noscript>

</body>
</html>
