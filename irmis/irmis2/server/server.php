<?php
    include_once('i_common.php');
?>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
"http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<title>Server Search</title>
<link rel="icon" type="image/png" href="../common/images/irmisiconLogo.png" />
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
<link href="../common/irmis2.css" rel="stylesheet" type="text/css">
<?php include('../top/analytics.php'); ?>
</head>
<body bgcolor="#999999">
<?php
	  include('../common/i_irmis_header.php');
	  $operating_system = "";
	  $_SESSION['operating_system'] = $operating_system;
	  $server_cognizant = "";
	  $_SESSION['server_cognizant'] = $server_cognizant;
	  $server_name = "";
	  $_SESSION['serverNameConstraint'] = $server_name;
	  include('i_server_search_criteria.php');
 ?>
</body>
</html>
