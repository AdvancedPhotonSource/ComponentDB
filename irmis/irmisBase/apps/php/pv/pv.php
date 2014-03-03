<?php 
    // PV Viewer Home Page
    include_once('i_common.php');
?>
<html>
<head>
<title>PV Viewer Home Page</title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
<?php include_once('i_pv.js'); ?>
</head>

<body onLoad="initializeSelects()">
<?php include('i_header.php'); ?>
<form action="action_pv_search.php" method="POST" name="pvSearchForm">
<b>Search Request</b><br>
<?php include_once('i_pv_search.php'); ?>
</form>
</body>
</html>
