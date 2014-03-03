
<div class="searchResults">

<table id="plc_table" width="99%" border="1" cellspacing="0" cellpadding="2">

<script type="text/javascript">

	// this function is really a "class"
	function doc(plcName, iocName, pvName, link, author, version, match, color) {

		this.plcName = plcName;
		this.iocName = iocName;
		this.pvName = pvName;
		this.link = link;
		this.author = author;
		this.version = version;
		this.match = match;
		this.color = color;

	}

	// docs will contain an object of type "doc" for each document found by AdvancedSearch
	var docs = new Array();

	function populate_docs(plcName, iocName, pvName, link, author, version, match, color, n) {

		docs[n] = new doc(plcName, iocName, pvName, link, author, version, match, color);

	}

</script>

<?php

    include_once('i_plc_get_info.php');

    // $wsdl = "https://icmsdocs.aps.anl.gov/new_docs/groups/secure/wsdl/custom/Search.wsdl";
    $wsdl = "https://icmsdocs.aps.anl.gov/docs/idcplg?IdcService=DISPLAY_URL&dDocName=SEARCH";

    $client = "";
    $execption = "";

try {

	$client = new SoapClient($wsdl, array('login' => $icms_user_name,'password' => $icms_user_passwd, 'trace' => 1));

	if ($client == "") {
	      include('soap_failure.php'); // redirects user to soap failure page and exits
	      exit;
    }

    // null list in session implies that result size was exceeded
    $plcList = $_SESSION['plcList'];

    echo '<tr><td id="plc_table_header" colspan=7 class=value>Please Wait... loading PLC info</td></tr>';

    echo '<tr>';
    echo '<th nowrap>PLC Name</th>';
    echo '<th nowrap>IOC Name</th>';
    echo '<th nowrap>PV Name</th>';
    echo '<th nowrap>Code - ICMS PDF file</th>';
    echo '<th nowrap>Author</th>';
    echo '<th nowrap>Code Version - PV</th>';
    echo '<th nowrap>PV - ICMS Match</th>';
    echo '</tr>';

    global $plcName;

    if ($plcList == null) {

        echo '<tr><td class="warning bold" colspan=7>Search produced too many results to display.<br>';
        echo 'Limit is 5000. Try using the Additional<br>';
        echo 'Search Terms to constrain your search.</td></tr>';

    } else if ($plcList->length() == 0) {

        echo '<tr><td class="warning bold" colspan=7>No PLCs found.</td></tr>';

    } else {

        $plcEntities = $plcList->getArray();

		// first we search for *all* ICMS documents that have plc_ in the name and "PLC code" in the title

		$ICMSquery = "xComments <substring> `plc_%` <and> dDocTitle <substring> `PLC code%` <and> <not> dDocType <matches> `CAD_Dependency` <and> <not> dDocType <matches> `Model_Drawing`";
		$ICMSqueryDBUG = "xComments &lt;substring&gt; `plc_` and dDocTitle &lt;substring&gt; `PLC code` and not dDocType &lt;matches&gt; 'CAD_Dependency' and not dDocType &lt;matches&gt; 'Model_Drawing'";

		// resultCount is 1 million because this is a magic number for "more aois than we will ever have"

		$param = array('queryText' => $ICMSquery, 'sortField' => 'dInDate', 'sortOrder' => 'desc', 'resultCount' => 1000000);

		$retVal = $client->AdvancedSearch($param);

		//$result = print_r($retVal, true);

		//echo "<br /> WSDL Search return value: $result <br />";

		$search_results = $retVal->AdvancedSearchResult->SearchResults;

		/* why do I need this $done variable?
		* Because $retVal is a different type of object depending on if one or more documents are found! (@#$!)
		* so, $done is used to break out of the following foreach loop in the case when only one document is found
		*/
		$done = false;

		$n = 0;

		foreach ($plcEntities as $plcEntity) {

			$plcName = $plcEntity->getPlcName();

			$iocName='';
			$plcIoc = new CtlParent();

			if ($plcIoc->loadFromDB($conn_2, $plcName)) {
				$iocName = $plcIoc->getCtlParent();
			}

			plcGetInfo($plcName, $iocName, &$ref_pv, &$codeVerStat, &$err);

			$code_found = false;

			if (isset($search_results)) {

				foreach ($search_results as $key => $value) {

					if ($code_found || $done)
						break;

					$code_found = false;

					if (gettype($key) != 'integer') { // then we have one and only one result

						// property #32 happens to be the xComments
						$xComments = $search_results->CustomDocMetaData->property[32]->value;

						$dDocTitle = $search_results->dDocTitle;

						// if the xComments don't contain the plc name, then continue
						if (stripos($xComments, $plcName) === false)
							continue;

						$dDocName = $search_results->dDocName;
						//$dDocAuthor = $search_results->dDocAuthor; // apparently dDocAuthor is not the author - xDocAuthor is!
						// property #28 happens to be the xDocAuthor
						$xDocAuthor = $search_results->CustomDocMetaData->property[28]->value;
						if ($xDocAuthor == '')
							$xDocAuthor = '&nbsp';

						$link = "<a class=\"hyper\" href=\"https://icmsdocs.aps.anl.gov/new_docs/idcplg?IdcService=DISPLAY_URL&dDocName=$dDocName\">$dDocTitle</a>";

						$code_found = true;

						$done = true;

					}

					else {

						// property #32 happens to be the xComments
						$xComments = $value->CustomDocMetaData->property[32]->value;

						$dDocTitle = $value->dDocTitle;

						// if the xComments don't contain the plc name, then continue
						if (stripos($xComments, $plcName) === false)
							continue;

						$dDocName = $value->dDocName;
						//$dDocAuthor = $value->dDocAuthor; // apparently dDocAuthor is not the author - xDocAuthor is!
						// property #28 happens to be the xDocAuthor
						$xDocAuthor = $value->CustomDocMetaData->property[28]->value;
						if ($xDocAuthor == '')
							$xDocAuthor = '&nbsp';

						$link = "<a class=\"hyper\" href=\"https://icmsdocs.aps.anl.gov/new_docs/idcplg?IdcService=DISPLAY_URL&dDocName=$dDocName\">$dDocTitle</a>";

						$code_found = true;

					}

				}

			}

			if ($code_found) {

				// the version is whatever appears after the last v in the title
				// non-numeric OK
				$version = substr($dDocTitle, strripos($dDocTitle, 'v') + 1);

				//if (! is_numeric($version))
					//$version = 'unknown';

				if ($version == $codeVerStat) {
					$match = 'YES';
					$color = '#00FF00';
				}

				elseif ($version == "NO-LADDER") {

					$match = "No ladder logic in ICMS";
					$color = '#000000';
				}
				else {
					$match = "$version != $codeVerStat";
					$color = '#FF0000';
				}

				echo '<script type="text/javascript">';
				echo "populate_docs('$plcName', '$iocName', '$ref_pv', '$link', '$xDocAuthor', '$codeVerStat', '$match', '$color', $n);";
				echo "</script>\n";

				$n++;

			}

			else {

				echo '<script type="text/javascript">';
				echo "populate_docs('$plcName', '$iocName', '$ref_pv', '&nbsp', '&nbsp', '&nbsp', '&nbsp', '', $n);";
				echo "</script>\n";

				$n++;

			}

		}

	}

} catch(SoapFault $exception) {
	echo '<tr><td colspan="5">NOTICE:  ICMS IS UNAVAILABLE AT THIS TIME USING WSDL INTERFACE</td></tr>';
}

?>

<script type="text/javascript">

	// this function sorts the documents alphabetically according to AOI name
	function doc_sort(doc1, doc2) {

		return doc1.plcName > doc2.plcName;

	}

	// sort the documents
	docs.sort(doc_sort);

	// finally, we display them in the table

	var table = document.getElementById('plc_table');

	for (var n=0; n < docs.length; n++) {

		/*var row = table.insertRow(-1);*/

		for (i=0; i<7; i++) {

			/*var cell = row.insertCell(-1);*/

			switch(i) {

				case 0:
					document.write("<tr><td class=primary nowrap>"+docs[n].plcName+"</td>");
					/*cell.innerHTML = docs[n].plcName;*/
					/*cell.align = 'center';*/
					break;
				case 1:
					document.write("<td class=results>"+docs[n].iocName+"</td>");
					/*cell.innerHTML = docs[n].iocName;*/
					break;
				case 2:
					document.write("<td class=results>"+docs[n].pvName+"</td>");
					/*cell.innerHTML = docs[n].pvName;*/
					break;
				case 3:
					document.write("<td class=results>"+docs[n].link+"</td>");
					/*cell.innerHTML = docs[n].link;*/
					break;
				case 4:
					document.write("<td class=results>"+docs[n].author+"</td>");
					/*cell.innerHTML = docs[n].author;*/
					break;
				case 5:
					document.write("<td class=results>"+docs[n].version+"</td>");
					/*cell.innerHTML = docs[n].version;*/
					break;
				case 6:
					document.write("<td class=results><font color='"+docs[n].color+"'>"+docs[n].match+"</font></td></tr>");
					/*cell.innerHTML = "<font color='"+docs[n].color+"'>"+docs[n].match+"</font>";*/
					break;

			}

		}

	}

	document.getElementById('plc_table_header').innerHTML = docs.length+' PLCs Found';

</script>

</table>

