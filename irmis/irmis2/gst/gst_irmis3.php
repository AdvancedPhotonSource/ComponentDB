<html>
<head>
<?
include_once('db.inc');
include('../top/analytics.php');
?>
<link href="gst_irmis.css" rel="stylesheet" type="text/css">
<script type="text/javascript" src="gst_irmis3.js">
</script>
</head>
<body>
	<div>
		<a href='http://ctlappsirmis.aps.anl.gov/irmis2/top/irmis2.php'><img src='irmis2Logo.png'></img></a>
		<span class='globalTitle'>Global Search Tool</span>
		<span class='serverTitle'><?echo "Database Server ".$db_host.":".$db_name_production_1;?></span>
		<span id="helpHints"><a class='help' href='#' onMouseOver="showHelp('helpSection');" onMouseOut="hideHelp('helpSection');" >Helpful Hints</a></span><span id='helpSection'></span>
	</div>
	<div id='tabLinksSection'>
		<a href='#' id='searchTab' onClick="expandTab('tabLinksSection', 'allTabContentSection', 'searchTabSection', 'searchTab');">Search</a>
	</div>
	<div id='Section'>
		<div id='searchTabSection'>
			<div id='searchFormSection'>
				<!-- The functions in these inputs have the word 'ALL' hardcoded into it. It is to reference the index in the variable Category. -->
				<input type='text' size='30'  id='searchBox' value="<? if($_POST['gst_search']) echo $_POST['gst_search']; ?>" onBlur="updateSearch('searchBox');" onFocus="clearSearch('searchBox');" onKeyUp="updateDisplayCheck('ALL', 'criteriaResultsSection', 'searchBox', 'tabLinksSection', 'allTabContentSection', 'searchTabSection', 'majorCategorySection', 'searchTab', event);">
				<input type='button' value='Advanced Search' onClick="displayAdvanceSearch('majorCategorySection', 'minorCategorySection', 'criteriaResultsSection', 'searchBox', 'tabLinksSection', 'allTabContentSection', 'searchTabSection', 'ALL', 'searchTab');">
			</div>
			<div id='searchCriteriaSection'>
				<div id='majorCategorySection'></div>
				<div id='minorCategorySection'></div>
			</div>
			<div id='resultsSection'>
				<div id='criteriaResultsSection'></div>
			</div>
		</div>
		<div id='allTabContentSection'>
		</div>
	</div>
	<script type='text/javascript'>
		init('searchTab', 'minorCategorySection', 'ALL', 'criteriaResultsSection', 'searchBox', 'tabLinksSection', 'allTabContentSection', 'searchTabSection', 'majorCategorySection', 'helpSection')
	</script>
	<noscript>Your browser does not support JavaScript!</noscript>
</body>
</html>
