
<div class="searchEditResultsDocument">

<table id="assoc_docs_table" width="99%" border="1" cellspacing="0" cellpadding="2">

<script type="text/javascript">

	// this function is really a "class"
	function doc(type, title, name, author, date) {

		this.type = type;
		this.title = title;
		this.name = name;
		this.author = author;
		this.date = date;

	}

	// docs will contain an object of type "doc" for each document found by AdvancedSearch
	var docs = new Array();

	// "batch" determines which "batch" of $results_per_page documents is currently displayed (0 is first batch)
	var batch = 0;

	function populate_docs(type, title, name, author, date, n) {

		//alert("populating index "+n+" with title "+title);
		docs[n] = new doc(type, title, name, author, date);

	}

</script>

<?php

    $aoiDocumentList = $_SESSION['aoiDocumentList'];
    $aoiDocumentEntity = $_SESSION['aoiDocumentEntity'];
    $_SESSION['new_aoi_document'] = "";

	$aoiBasicList = $_SESSION['aoiBasicList'];
	$aoiBasicEntities = $aoiBasicList->getArray();
	foreach($aoiBasicEntities as $aoiBasicEntity) {
		$aoiName = $aoiBasicEntity->getAOIName();
		$aoiKeywords = $aoiBasicEntity->getAOIKeyword();
		$aoi_keyword_array = explode(' ',$aoiKeywords);
	}


    // build ICMS search string

	$searchStringICMS = "xComments <substring> `";
	$searchStringDBUG = "xComments &lt;substring&gt; `";

	// No CAD drawings included:  $end_of_searchStringICMS =
	//"%60%29+%29+and+not+dDocType+%3Cmatches%3E+%27CAD_Dependency%27+%29+and++not+dDocType+%3Cmatches%3E+%27Model_Drawing%27&ftx=&AdvSearch=True&search_docs_cb=x&ResultCount=20&SortField=SCORE&SortOrder=Desc";

	// CAD drawings included:

	//$end_of_searchStringICMS = "` <and> <not> dDocType <matches> 'CAD_Dependency'";
	//$end_of_searchStringDBUG = "` &lt;and&gt; &lt;not&gt; dDocType &lt;matches&gt; 'CAD_Dependency'";

	$end_of_searchStringICMS = "`";
	$end_of_searchStringDBUG = "`";

	if (ereg( "(([-a-zA-Z0-9]+_){4})", $aoiName)) {

	    // AOI is a child, need to capture in the parent AOI name and then perform search on ICMS documents for both the parent and child AOI names

		if (preg_match("/([-a-zA-Z0-9]+_){3}[-a-zA-Z0-9]+/", $aoiName, $matches)) {

			$parent_aoi_name = $matches[0];

            		// echo "\n aoi parent name is $parent_aoi_name \n";

            		// old... $searchStringICMS = "https://icmsdocs.aps.anl.gov/new_docs/idcplg?IdcService=GET_SEARCH_RESULTS_FORCELOGIN&QueryText=%28+%28xComments+%3Ccontains%3E+%60" . $parent_aoi_name . "%60+%3Cor%3E+xComments+%3Ccontains%3E+%60" . $aoiName . "%60%29+%29+and+not+dDocType+%3Cmatches%3E+%27CAD_Dependency%27&ftx=&AdvSearch=True&search_docs_cb=x&search_models_cb=y&ResultCount=20&SortField=SCORE&SortOrder=Desc";

	    	//SB$searchStringICMS = $searchStringICMS . $parent_aoi_name . "%60+";
	    	$searchStringICMS = $searchStringICMS . $parent_aoi_name . "` ";
	    	$searchStringDBUG = $searchStringDBUG . $parent_aoi_name . "` ";

	    	// Check for AOI keywords...

	    	foreach ($aoi_keyword_array as $icms_keyword) {
	      		//SB$searchStringICMS = $searchStringICMS . "%3Cor%3E+xComments+%3Ccontains%3E+%60" . $icms_keyword . "%60+";

	      		if ($icms_keyword != '-') {
	      			$searchStringICMS = $searchStringICMS . "<or> xComments <substring> `" . $icms_keyword . "` ";
	      			$searchStringDBUG = $searchStringDBUG . "&lt;or&gt; xComments &lt;substring&gt; `" . $icms_keyword . "` ";
	      		}
	    	}

	    	// End the search string with child aoi name
	    	//SB$searchStringICMS = $searchStringICMS . "%3Cor%3E+xComments+%3Ccontains%3E+%60" . $aoiName . $end_of_searchStringICMS;
	    	$searchStringICMS = $searchStringICMS . "<or> xComments <substring> `" . $aoiName . $end_of_searchStringICMS;
	    	$searchStringDBUG = $searchStringDBUG . "&lt;or&gt; xComments &lt;substring&gt; `" . $aoiName . $end_of_searchStringDBUG;

		} else {

			// Check for AOI keywords...

	    	foreach ($aoi_keyword_array as $icms_keyword) {
	      		//SB$searchStringICMS = $searchStringICMS . "%3Cor%3E+xComments+%3Ccontains%3E+%60" . $icms_keyword . "%60+";

	      		if ($icms_keyword != '-') {
	      			$searchStringICMS = $searchStringICMS . "<or> xComments <substring> `" . $icms_keyword . "` ";
	      			$searchStringDBUG = $searchStringDBUG . "&lt;or&gt; xComments &lt;substring&gt; `" . $icms_keyword . "` ";
	      		}
	    	}

		   	//SB$searchStringICMS = $searchStringICMS . "%3Cor%3E+xComments+%3Ccontains%3E+%60" . $aoiName . $end_of_searchStringICMS;
		   	$searchStringICMS = $searchStringICMS . "<or> xComments <substring> `" . $aoiName . $end_of_searchStringICMS;
		   	$searchStringDBUG = $searchStringDBUG . "&lt;or&gt; xComments &lt;substring&gt; `" . $aoiName . $end_of_searchStringDBUG;
		}

	} else {

	    	// AOI is a parent
	    	// Perform search on ICMS documents for exact match of parent AOI name only

	    	// Check for AOI keywords...

	    	foreach ($aoi_keyword_array as $icms_keyword) {
	      		//SB$searchStringICMS = $searchStringICMS . $icms_keyword . "%60+%3Cor%3E+xComments+%3Ccontains%3E+%60";

	      		if ($icms_keyword != '-') {
	      			$searchStringICMS = $searchStringICMS . $icms_keyword . "` <or> xComments <substring> `";
	      			$searchStringDBUG = $searchStringDBUG . $icms_keyword . "` &lt;or&gt; xComments &lt;substring&gt; `";
	      		}
	    	}

	    	// End the search string
	    	//SB$searchStringICMS = $searchStringICMS . $aoiName . $end_of_searchStringICMS;
	    	$searchStringICMS = $searchStringICMS . $aoiName . $end_of_searchStringICMS;
	    	$searchStringDBUG = $searchStringDBUG . $aoiName . $end_of_searchStringDBUG;
    }

    echo '<tr><th id="assoc_doc_header" class="sectionHeaderCbox" colspan="5"><b>Associated Documents</b></th></tr>';
	echo '<tr><td class="value">Document Type</td><td class="value">Title</td><td class="value">URI</td><td class="value">Author</td><td class="value">Check-In Date</td></tr>';


	// $wsdl = "https://icmsdocs.aps.anl.gov/new_docs/groups/secure/wsdl/custom/Search.wsdl";

	// $wsdl = "https://icmsdocs.aps.anl.gov/docs/groups/public/documents/data/Search.wsdl";

	$wsdl = "https://icmsdocs.aps.anl.gov/docs/idcplg?IdcService=DISPLAY_URL&dDocName=SEARCH";

	include("db.inc");

	//Debug test...
	//$searchStringICMS = "`aoi_site_acis_controlled-access` <in> xComments";

	//echo '<tr><td colspan="5"> ICMS search string: '.$searchStringICMS.' </td></tr>';
	//echo '<tr><td colspan="5"> ICMS WSDL address: '.$wsdl.' </td></tr>';


	try {

		$client = new SoapClient($wsdl, array('login' => $icms_user_name,'password' => $icms_user_passwd, 'trace' => 1));


	// resultCount is 1 million because this is a magic number for "more aois than we will ever have"

	// $param = array('queryText' => $searchStringICMS, 'resultCount' => 1000000);


	$param = array("queryText" => $searchStringICMS, "sortField" => "dInDate", "sortOrder" => "desc", "resultCount" => 1000000);

	//$results = print_r($param, true);

	//echo '<tr><td colspan="5"> ICMS WSDL param: '.$results.'</td></tr>';

	$retVal = $client->AdvancedSearch($param);

	//$results = print_r($retVal, true);

	//echo '<tr><td colspan="5"> ICMS WSDL Return Object Values: '.$results.'</td></tr>';


	// this determines how many found documents are displayed at a time
	$results_per_page = 5;

	$n = 0;

	if ($aoiDocumentList->length() == 0)
		//echo '<tr><th colspan="5" align="center">No AOI Associated Documents Found in IRMIS.</th></tr>';
		; //the preceding is probably not necessary to display, so I got rid of it

    else {

    	$aoiDocumentEntities = $aoiDocumentList->getArray();
    	foreach ($aoiDocumentEntities as $aoiDocumentEntity) {
       		// Test to see if document is an "http" type of url.  If so, display as live link with href assignment tag

        	$doc_uri = $aoiDocumentEntity->getURI();
        	$doc_type = $aoiDocumentEntity->getDoctype();

        	if (eregi('http://',$doc_uri) || eregi('https://',$doc_uri))
        		$dDocName = "<a href='$doc_uri'>$doc_uri";
        	else
        		$dDocName = "$doc_uri";

			// only display the first $results_per_page of the results
        	if ($n < $results_per_page) {

        		$id = "doc$n";
        		echo "<tr id=$id>";
				echo '<td align="center">'.$doc_type.'</td><td>-</td><td>'.$dDocName.'</td><td>-</td><td>-</td>';
				echo "</tr>\n";

			}

			echo '<script type="text/javascript">';
			echo "populate_docs('$doc_type', '-', '$dDocName', '-', '-', $n);";
			echo "</script>\n";

          	$n++;

        }
	}

	$search_results = $retVal->AdvancedSearchResult->SearchResults;

	if(isset($search_results)) {

		foreach ($search_results as $key => $value) {

			if ($done)
				break;

			if (gettype($key) != 'integer') { // then we have one and only one result

				$dDocType = $search_results->dDocType;
				$dDocTitle = $search_results->dDocTitle;
				$dDocName = "<a href=\"https://icmsdocs.aps.anl.gov/new_docs/idcplg?IdcService=DISPLAY_URL&dDocName={$search_results->dDocName}\">{$search_results->dDocName}</a>";
				$dInDate = $search_results->dInDate;

				//$dDocAuthor = $search_results->dDocAuthor; // apparently dDocAuthor is not the author - xDocAuthor is!
				// property #27 happens to be the xDocAuthor
				$xDocAuthor = $search_results->CustomDocMetaData->property[27]->value;
				if ($xDocAuthor == '')
					$xDocAuthor = '&nbsp';

				$done = true;

			}

			else {

				$dDocType = $value->dDocType;
				$dDocTitle = $value->dDocTitle;
				$dDocName = "<a href=\"https://icmsdocs.aps.anl.gov/new_docs/idcplg?IdcService=DISPLAY_URL&dDocName={$value->dDocName}\">{$value->dDocName}</a>";
				$dInDate = $value->dInDate;

				//$dDocAuthor = $value->dDocAuthor; // apparently dDocAuthor is not the author - xDocAuthor is!
				// property #27 is now the xDocAuthor

				$xDocAuthor = $value->CustomDocMetaData->property[27]->value;

				if ($xDocAuthor == '')
					$xDocAuthor = '&nbsp';

			}

			// only display the first $results_per_page of the results
			if ($n < $results_per_page) {

				$id = "doc$n";
				echo "<tr id=$id>";
				echo '<td align="center">'.$dDocType.'</td><td>'.$dDocTitle.'</td><td>'.$dDocName.'</td><td>'.$xDocAuthor.'</td><td>'.$dInDate.'</td>';
				echo "</tr>\n";

			}

			echo '<script type="text/javascript">';
			echo "populate_docs('$dDocType', '$dDocTitle', '$dDocName', '$xDocAuthor', '$dInDate', $n);";
			echo "</script>\n";

			$n++;

		}

		$total_num_docs = $n;

?>

<script type="text/javascript">
	document.getElementById('assoc_doc_header').innerHTML = '<b><?php echo $total_num_docs ?> Associated Documents Found</b>';
</script>

<?php

		echo '</table>';

		echo '<table width="20%" border="0" cellspacing="0" cellpadding="2">';
		echo '<tr>';

		//echo "<td align='right' id='lbutton'><input type='submit' class='editbuttons' name = 'last' value = 'last $results_per_page' onclick='last($results_per_page)'/></td>";

		echo "<th class='lastnextbuttons' id='lbutton'>&nbsp;</th>";

		if ($n > $results_per_page) // only display the "next" button if there are actually more documents to display
			echo "<th class='lastnextbuttons' id='nbutton'><input type='submit' class='editbuttons' name = 'next' value = 'next $results_per_page' onclick='next_batch($results_per_page)'/></th>";
		else
			echo "<th class='lastnextbuttons' id='nbutton'>&nbsp;</th>";


		echo '</tr>';

	}

	else

		echo '<tr><td colspan="5" align="center">No ICMS documents found.</td></tr>';


	} catch(SoapFault $exception) {
			echo '<tr><td colspan="5">NOTICE:  ICMS IS UNAVAILABLE AT THIS TIME USING WSDL INTERFACE</td></tr>';
	}


?>

<script type="text/javascript">

	// displays the next $results_per_page results
	function next_batch(results_per_page) {

		if (batch >= ((docs.length / results_per_page) - 1))
			return;
		batch++;

		for (var n=0; n<results_per_page; n++) {

			var d = batch*results_per_page + n;

			var row = document.getElementById('doc'+n);

			if (d < docs.length) {

				var type = docs[d].type;
				var title = docs[d].title;
				var name = docs[d].name;
				var author = docs[d].author;
				var date = docs[d].date;

				// delete and replace each cell in the row with the appropriate new data
				for (i=0; i<5; i++) {

					row.deleteCell(i);
					cell = row.insertCell(i);

					switch(i) {

						case 0:
							cell.innerHTML = type;
							cell.align = 'center';
							break;
						case 1:
							cell.innerHTML = title;
							break;
						case 2:
							cell.innerHTML = name;
							break;
						case 3:
							cell.innerHTML = author;
							break;
						case 4:
							cell.innerHTML = date;
							break;

					}

				}

			}

			else // else delete the last row

				document.getElementById('assoc_docs_table').deleteRow(-1);

		}

		// if there are no further batches, then don't display the "next" button anymore, but be sure to re-instate the "last" button
		if (batch >= ((docs.length / results_per_page) - 1))
			document.getElementById('nbutton').innerHTML = '&nbsp;';
		document.getElementById('lbutton').innerHTML = "<input type='submit' class='editbuttons' name = 'last' value = 'last "+results_per_page+"' onclick='last_batch("+results_per_page+")'/>";

	}

	// displays the last $results_per_page results
	function last_batch(results_per_page) {

		if (batch == 0)
			return;
		batch--;

		var num_rows = document.getElementById('assoc_docs_table').rows.length;

		// if some rows have been deleted, then reinstate them
		if (num_rows < results_per_page + 2) {

			for (i=0; i<results_per_page + 2 - num_rows; i++) {

				row = document.getElementById('assoc_docs_table').insertRow(-1);
				row.id = 'doc'+(num_rows + i - 2);

				for (j=0; j<5; j++)
					row.insertCell(0);

			}

		}

		for (var n=0; n<results_per_page; n++) {

			var d = batch*results_per_page + n;

			var row = document.getElementById('doc'+n);

			var type = docs[d].type;
			var title = docs[d].title;
			var name = docs[d].name;
			var author = docs[d].author;
			var date = docs[d].date;

			// delete and replace each cell in the row with the appropriate new data
			for (i=0; i<5; i++) {

				row.deleteCell(i);
				cell = row.insertCell(i);

				switch(i) {

					case 0:
						cell.innerHTML = type;
						cell.align = 'center';
						break;
					case 1:
						cell.innerHTML = title;
						break;
					case 2:
						cell.innerHTML = name;
						break;
					case 3:
						cell.innerHTML = author;
						break;
					case 4:
						cell.innerHTML = date;
						break;

				}

			}

		}

		// if there are no previous batches, then don't display the "last" button anymore, but be sure to re-instate the "next" button
		if (batch == 0)
			document.getElementById('lbutton').innerHTML = '&nbsp;';
		document.getElementById('nbutton').innerHTML = "<input type='submit' class='editbuttons' name = 'next' value = 'next "+results_per_page+"' onclick='next_batch("+results_per_page+")'/>";

	}

</script>

</table>

</div>
