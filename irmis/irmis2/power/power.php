<?php 
    // Power Search Home Page
    include_once('i_common.php');
?>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
"http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<title>AC Power Search</title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
<link href="../common/irmis2.css" rel="stylesheet" type="text/css">
<?php include('../top/analytics.php'); ?>
</head>
<body bgcolor="#999999">
<body>
<?php include('../common/i_irmis_header.php');
      $nameDesc = "";
	  $_SESSION['nameDesc'] = $nameDesc;
	  $roomConstraint = "";
	  $_SESSION['roomConstraint'] = $roomConstraint;
	  $groupNameConstraint = "";
	  $_SESSION['groupNameConstraint'] = $groupNameConstraint;
	  $switchgearConstraint = "";
	  $_SESSION['switchgearConstraint'] = $switchgearConstraint;
      include_once('i_power_search_criteria.php');
?>
</body>
</html>
