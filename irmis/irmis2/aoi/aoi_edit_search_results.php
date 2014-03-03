<?php
    /* AOI Edit Search Results Page
     * Display aoi search results
     */

    include_once('i_common.php');

?>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
"http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>

<title>AOI Edit Search Results Page</title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
<link href="../common/irmis_aoi.css" rel="stylesheet" type="text/css">
</head>
<body bgcolor="#999999">
<?php
    include('../common/i_irmis_aoi_header.php');
    include('i_aoi_edit_search_criteria.php');
    include('i_aoi_edit_name_search_results.php');
    include('i_aoi_edit_blank_search_results.php');

?>
</body>
</html>
