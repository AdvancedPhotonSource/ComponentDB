<?php 
    /* IOC Network Search Results Page
     * Display ioc network search results
     */

    include_once('i_common.php');
?>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
"http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<title>Expanded Component Locations</title>
<link rel="icon" type="image/png" href="../common/images/irmisiconLogo.png" />
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
<link href="../common/irmis2.css" rel="stylesheet" type="text/css">
<?php include('../top/analytics.php'); ?>
</head>
<body bgcolor="#999999">
<?php 
    include('../common/i_irmis_header.php');
    include('i_comp_search_criteria.php');
    include_once('i_comp_instances.php'); 
?>

</body>
</html>
