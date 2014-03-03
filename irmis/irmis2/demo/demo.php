<?php 
    // Demo Home Page
    include_once('i_common.php');
?>
<html>
<head>
<title>Demo Home Page</title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
</head>

<body>
<?php include('i_header.php'); ?>
<form action="action_ioc_search.php" method="POST" name="iocSearchForm">
<b>Search Request</b><br>
<?php include_once('i_demo.php'); ?>
</form>
</body>
</html>
