<?php 
    /* Demo Search Results Page
     * Display ioc search results, which is pretty much the same as the
     * demo.php page, only with a different title.
     */

    include_once('i_common.php');
?>
<html>
<head>
<title>Demo Search Results Page</title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
<style type="text/css">
        td.one {font-size:10pt; text-align:left; font-family:Arial;}
        td.two {font-size:8pt; text-align:left; font-family:Arial;}
</style>
</head>

<body>
<?php include('i_header.php'); ?>
<form action="action_ioc_search.php" method="POST" name="iocSearchForm">
<b>Search Results</b></br>
<?php include_once('i_demo.php'); ?>
</form>
</body>
</html>