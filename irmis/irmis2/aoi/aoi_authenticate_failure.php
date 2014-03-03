<?php
    /* AOI Authenticate Failure Page
     * Display message to user to try again
     */

    include_once('i_common.php');
?>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
"http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>

<title>AOI Search Results Page</title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
<link href="../common/irmis_aoi.css" rel="stylesheet" type="text/css">
</head>
<body bgcolor="#999999">
<?php
    // include('../common/i_irmis_header.php');

    echo '<br>User login to AOI editor session failed.<br></br> <a href= https://www.aps.anl.gov/APS_Engineering_Support_Division/Controls/slogin/login.php?app='.$ldap_user_name.'&ctx='.session_name().'>Please try login again.</br>';

?>
</body>
</html>
