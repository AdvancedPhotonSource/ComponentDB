<?php 
    // Component Hardware Catalog Search Home Page
    include_once('i_common.php');
?>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
"http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<title>Component Hardware Catalog Search Home Page</title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
<link href="../common/irmis2.css" rel="stylesheet" type="text/css">
</head>
<body bgcolor="#999999">
<body>
<?php include('../common/i_irmis_header.php');
      $nameDesc = "";
	  $_SESSION['nameDesc'] = $nameDesc;
	  $mfgConstraint = "";
	  $_SESSION['mfgConstraint'] = $mfgConstraint;
	  $ffConstraint = "";
	  $_SESSION['ffConstraint'] = $ffConstraint;
	  $functionConstraint = "";
	  $_SESSION['functionConstraint'] = $functionConstraint;
	  $personConstraint = "";
	  $_SESSION['personConstraint'] = $personConstraint;
	  $blConstraint = "";
	  $_SESSION['blConstraint'] = $blConstraint;
	  $chcPersonConstraint = "";
	  $_SESSION['chcPersonConstraint'] = $chcPersonConstraint;
      include_once('i_chc_search_criteria.php'); ?>
</body>
</html>
