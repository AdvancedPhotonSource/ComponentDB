<?php
    include_once('i_common.php');
?>
<html>
<head>
<title>PLC Search</title>
<link rel="icon" type="image/png" href="../common/images/irmisiconLogo.png" />
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
<link href="../common/irmis2.css" rel="stylesheet" type="text/css">
<?php include('../top/analytics.php'); ?>
</head>
<body bgcolor="#999999">
<?php include('../common/i_irmis_header.php');
	  $system = "";
	  $_SESSION['system'] = $system;
	  $tech = "";
	  $developer = "";
	  $_SESSION['developer'] = $developer;
	  $plc_name = "";
	  $_SESSION['plcNameConstraint'] = $plc_name;
	  include('i_plc_search_criteria.php');
 ?>
</body>
</html>
