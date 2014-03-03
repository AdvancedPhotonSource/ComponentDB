<?php 
    /* PV Viewer Search Results Page
     * Display pv search results, with original search form still at top of
     * page.
     */

    include_once('i_common.php');
?>
<html>
<head>
<title>PV Viewer Home Page</title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
<style type="text/css">
        td.one {font-size:10pt; text-align:left; font-family:Arial;}
        td.two {font-size:8pt; text-align:left; font-family:Arial;}
</style>
<?php include_once('i_pv.js'); ?>
</head>

<body onLoad="initializeSelects()">
<?php include('i_header.php'); ?>
<form action="action_pv_search.php" method="POST" name="pvSearchForm">
<b>Search Request</b></br>
<?php include_once('i_pv_search.php'); ?>
</form>
<table width="100%">
<tr>
<th width="40%" align="left">Results <font size=2>(<a href="action_pv_display.php?prevDisplayIndices=true&sourcePage=pv_search_results_with_details">prev</a>)</font>&nbsp;<?php echo $_SESSION['PVSearchResults']->getDisplayIndexLow(); ?> through <?php echo $_SESSION['PVSearchResults']->getDisplayIndexHigh(); ?>&nbsp;<font size=2>(<a href="action_pv_display.php?nextDisplayIndices=true&sourcePage=pv_search_results_with_details">next</a>)</font> of <?php echo $_SESSION['NumPVSearchResults']; ?></th>
<th width="60%" align="left">PV Details for <?php echo $_SESSION['PVSelectedResult']->getRecName(); ?>
</th>
</tr>
<tr><td valign="top">
<?php include_once('i_pv_search_results.php'); ?>
</td>
<td valign="top">
<?php include_once('i_pv_details.php'); ?>
</td></tr>
</table>
</body>
</html>