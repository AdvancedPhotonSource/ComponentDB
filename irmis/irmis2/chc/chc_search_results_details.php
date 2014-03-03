<?php 
    /* Component Hardware Search Results Page
     * Display component hardware catalog search results
     */

    include_once('i_common.php');
?>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
"http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<title>Component Hardware Catalog Search Results Page</title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
<link href="../common/irmis2.css" rel="stylesheet" type="text/css">
</head>
<body bgcolor="#999999">
<?php 
    include('../common/i_irmis_header.php');
    include('i_chc_search_criteria.php');
    include_once('i_chc_search_results_details.php'); 
?>

</body>
</html>
