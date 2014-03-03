<?php
    include_once('i_common.php');
?>
<html>
<head>
<title>AOI Edit Search Home Page</title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
<link href="../common/irmis_aoi.css" rel="stylesheet" type="text/css">
</head>
<body bgcolor="#999999">
<?php
        include('../common/i_irmis_aoi_header.php');

	 	$techsystem = "";
	 	$_SESSION['techsystem'] = $techsystem;
        	$machine = "";
     		$_SESSION['machine'] = $machine;
		$unique_function = "";
		$_SESSION['unique_function'] = $unique_function;
     		$plcname = "";
	 	$_SESSION['plcname'] = $plcname;
     		$iocname = "";
     		$_SESSION['iocname'] = $iocname;
	 	$person = "";
	 	$_SESSION['person'] = $person;
	 	$criticality = 0;
	 	$_SESSION['criticality'] = $criticality;
        	$aoi_status = "";
        	$_SESSION['aoi_status'] = $aoi_status;
	 	$aoi_keyword = "";
     		$_SESSION['aoi_keyword'] = $aoi_keyword;
     		$_SESSION['pv_search'] = $pv_search;
	 	$aoi_name = "";
	 	$_SESSION['aoi_name'] = $aoi_name;
	 	$aoi_id = 0;
	 	$_SESSION['aoi_id'] = $aoi_id;
	 	$aoiNameConstraint = "";
	 	$_SESSION['aoiNameConstraint'] = $aoiNameConstraint;

	 	//$relatives = "Yes";
	 	//$_SESSION['relatives'] = $relatives;

	 	$doc_type = "";
	 	$_SESSION['new_aoi_doc_type_1'] = $doc_type;
	 	$_SESSION['new_aoi_doc_type_2'] = $doc_type;
	 	$_SESSION['new_aoi_doc_type_3'] = $doc_type;
	 	$_SESSION['new_aoi_doc_type_4'] = $doc_type;
	 	$_SESSION['new_aoi_doc_type_5'] = $doc_type;

		include('i_aoi_edit_search_criteria.php');

 ?>
</body>
</html>
