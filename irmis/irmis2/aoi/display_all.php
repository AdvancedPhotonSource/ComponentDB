<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
"http://www.w3.org/TR/html4/loose.dtd">

<html>

<head>
<title>All AOI ICMS Documents</title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
<link href="../common/irmis_aoi.css" rel="stylesheet" type="text/css">
</head>

<body>

<table id="docs_table" border="1" cellspacing="0" cellpadding="2">

<script type="text/javascript">

	// this function is really a "class"
	function doc(aoi, title, name, author, date) {

		this.aoi = aoi;
		this.title = title;
		this.name = name;
		this.author = author;
		this.date = date;

	}

	// docs will contain an object of type "doc" for each document found by AdvancedSearch
	var docs = new Array();

	// "batch" determines which "batch" of $results_per_page documents is currently displayed (0 is first batch)
	var batch = 0;

	function populate_docs(aoi, title, name, author, date, n) {

		//alert("populating index "+n+" with title "+title);
		docs[n] = new doc(aoi, title, name, author, date);

	}

</script>

<?php

	$searchStringICMS = "xComments <substring> `aoi_%` <and> <not> dDocType <matches> `CAD_Dependency`";
	$searchStringDBUG = "xComments &lt;substring&gt; `aoi_%` &lt;and&gt; &lt;not&gt; dDocType &lt;matches&gt; `CAD_Dependency`";

	$wsdl = "https://icmsdocs.aps.anl.gov/docs/idcplg?IdcService=DISPLAY_URL&dDocName=SEARCH";

	// $wsdl = "https://icmsdocs.aps.anl.gov/docs/groups/public/documents/data/search.wsdl";

	include("db.inc");

	$client = new SoapClient($wsdl, array('login' => $icms_user_name,'password' => $icms_user_passwd));

	// resultCount is 1 million because this is a magic number for "more aois than we will ever have"

	$param = array("queryText" => $searchStringICMS, "sortField" => "dInDate", "sortOrder" => "desc", "resultCount" => 10000);

	// echo $searchStringICMS;


	$retVal = $client->AdvancedSearch($param);

	// $myresult = print_r($retVal, true);

	// echo $myresult;


    echo '<tr><th id="all_docs_header" class="sectionHeaderCbox" colspan="5"><b>All AOI ICMS Documents</b></th></tr>';
    echo '<tr><td class="value">AOI Name</td><td class="value">Title</td><td class="value">URI</td><td class="value">Author</td><td class="value">Check-In Date</td></tr>';


	// this determines how many found documents are displayed at a time
	$results_per_page = 1000;

	$n = 0;
	$done = false;

	$search_results = $retVal->AdvancedSearchResult->SearchResults;

	if(isset($search_results)) {

		// echo 'begin foreach results loop...';

		foreach ($search_results as $key => $value) {



			// echo 'n counter = '.$n.'\n';

			if ($done) {

				// echo 'finished foreach results loop';
				break;

			}


			if (gettype($key) != 'integer') { // then we have one and only one result

				// property #32 happens to be the xComments
				$xComments = $search_results->CustomDocMetaData->property[32]->value;

				//$xComments = $search_results->CustomDocMetaData->property["name='xComments'"]->value;

				$dDocTitle = $search_results->dDocTitle;
				$dDocName = "<a href=\"https://icmsdocs.aps.anl.gov/new_docs/idcplg?IdcService=DISPLAY_URL&dDocName={$search_results->dDocName}\">{$search_results->dDocName}</a>";
				$dInDate = $search_results->dInDate;

				//$dDocAuthor = $search_results->dDocAuthor; // apparently dDocAuthor is not the author - xDocAuthor is!
				// property #28 happens to be the xDocAuthor
				$xDocAuthor = $search_results->CustomDocMetaData->property[28]->value;

				// $xDocAuthor = $search_results->CustomDocMetaData->property["name='xDocAuthor'"]->value;

				if ($xDocAuthor == '')
					$xDocAuthor = '&nbsp';

				$done = true;

			}

			else {

				// property #32 happens to be the xComments
				$xComments = $value->CustomDocMetaData->property[32]->value;

				//$xComments = $value->CustomDocMetaData->property["name='xComments'"]->value;

				$xComments = "comments: ".$xComments;

				$dDocTitle = $value->dDocTitle;

				$dDocTitle = "title: ".$dDocTitle;

				$dDocName = "<a href=\"https://icmsdocs.aps.anl.gov/new_docs/idcplg?IdcService=DISPLAY_URL&dDocName={$value->dDocName}\">{$value->dDocName}</a>";

				$dDocName = "docname: ".$dDocName;

				$dInDate = $value->dInDate;

				$dInDate = "date: ".$dInDate;

				//$dDocAuthor = $value->dDocAuthor; // apparently dDocAuthor is not the author - xDocAuthor is!
				// property #28 happens to be the xDocAuthor

				$xDocAuthor = $value->CustomDocMetaData->property[28]->value;

				//$xDocAuthor = $value->CustomDocMetaData->property["name='xDocAuthor'"]->value;

				$xDocAuthor = "author: ".$xDocAuthor;

				if ($xDocAuthor == '')
					$xDocAuthor = '&nbsp';

			}

			$xComments_split = split('[[:space:],]', $xComments); // we split according to characters which are either spaces or commas

			// strcmp returns 0 iff $kw and 'aoi_' are equal
			$lambda = create_function('$kw','return !strncmp($kw, "aoi_", 4) && strlen($kw) > 0;');
			$xComments_split = array_filter($xComments_split, $lambda);

			$xComments = implode(", ", $xComments_split);

			// if it turned out that none of the xComments actually begin with 'aoi_', then move on
			if ($xComments == '')
				continue;

			  echo '<script type="text/javascript">';
			  echo "populate_docs('$xComments', '$dDocTitle', '$dDocName', '$xDocAuthor', '$dInDate', $n);";

				// echo $xComments,$dDocTitle,$dDocName,$xDocAuthor,$dInDate;

			  echo "</script>\n";

			// only display the first $results_per_page of the results (blank until sorted)
			if ($n < $results_per_page) {

				$id = "doc$n";
				echo "<tr id=$id>";
				echo "</tr>\n";

			}

			$n++;

		}

	$total_num_docs = $n;

?>

<script type="text/javascript">
	document.getElementById('all_docs_header').innerHTML = '<b><?php echo $total_num_docs ?> AOI ICMS Documents Found</b>';
</script>

<script type="text/javascript">

	// this function sorts the documents alphabetically according to AOI name
	function doc_sort(doc1, doc2) {

		return doc1.aoi > doc2.aoi;

	}

	// sort the documents
	docs.sort(doc_sort);

	// now we can display the first $results_per_page documents!
	for (var n=0; n<'<?php echo $results_per_page; ?>'; n++) {

		if (n < docs.length) {

			var aoi = docs[n].aoi;
			var title = docs[n].title;
			var name = docs[n].name;
			var author = docs[n].author;
			var date = docs[n].date;

			var row = document.getElementById('doc'+n);

			for (i=0; i<5; i++) {

				cell = row.insertCell(-1);

				switch(i) {

					case 0:
						cell.innerHTML = aoi;
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
						cell.align = 'center';
						break;
					case 4:
						cell.innerHTML = date;
						break;

				}

			}

		}

	}

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

				var aoi = docs[d].aoi;
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
							cell.innerHTML = aoi;
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
							cell.align = 'center';
							break;
						case 4:
							cell.innerHTML = date;
							break;

					}

				}

			}

			else // else delete the last row

				document.getElementById('docs_table').deleteRow(-1);

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

		var num_rows = document.getElementById('docs_table').rows.length;

		// if some rows have been deleted, then reinstate them
		if (num_rows < results_per_page + 2) {

			for (i=0; i<results_per_page + 2 - num_rows; i++) {

				row = document.getElementById('docs_table').insertRow(-1);
				row.id = 'doc'+(num_rows + i - 2);

				for (j=0; j<5; j++)
					row.insertCell(0);

			}

		}

		for (var n=0; n<results_per_page; n++) {

			var d = batch*results_per_page + n;

			var row = document.getElementById('doc'+n);

			var aoi = docs[d].aoi;
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
						cell.innerHTML = aoi;
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
						cell.align = 'center';
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

</body>

</html>