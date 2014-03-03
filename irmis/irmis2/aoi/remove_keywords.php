<?php

$keyword_to_delete;

// $input here is an array of the keywords which need to be deleted from all ICMS documents

function remove_keywords($input) {

	try {

		$keywords_to_delete = $input;

		include("db.inc");

		$client_search = new SoapClient($_SESSION['wsdl_search'], array('login' => $icms_user_name, 'password' => $icms_user_passwd, 'exceptions' => true));
		$client_checkin = new SoapClient($_SESSION['wsdl_checkin'], array('login' => $icms_user_name, 'password' => $icms_user_passwd, 'exceptions' => true));

		//echo 'remove keyword function running<br />';

		foreach ($keywords_to_delete as $keyword) {

			global $keyword_to_delete;
			$keyword_to_delete = $keyword;

			$searchStringICMS = "xComments <substring> `$keyword`";
			$searchStringDBUG = "xComments &lt;substring&gt; `$keyword`";
			echo "searchStringDBUG: $searchStringDBUG<br />";

			// resultCount is 1 million because this is a magic number for "more aois than we will ever have"

			$param = array("queryText" => $searchStringICMS, "sortField" => "dInDate", "sortOrder" => "desc", "resultCount" => 1000000);

			$retVal = $client_search->AdvancedSearch($param);

			if (isset($retVal->AdvancedSearchResult->SearchResults)) {

				/* why do I need this $done variable?
				* Because $retVal is a different type of object depending on if one or more documents are found! (@#$!)
				* so, $done is used to break out of the following foreach loop in the case when only one document is found
				*/
				$done = false;

				$search_results = $retVal->AdvancedSearchResult->SearchResults;

				foreach ($search_results as $key => $value) {

					if ($done)
						break;

					// this if statement is true if only one result is found, making the inner foreach loop the correct choice

					if (gettype($key) != 'integer') { // then we have one and only one result

						foreach ($search_results->CustomDocMetaData->property as $p)
							if ($p->name == 'xComments')
								$xComments = $p->value;

						$dID = $search_results->dID;
						$dRevLabel = $search_results->dRevisionID;
						$dDocName = $search_results->dDocName;
						$dDocTitle = $search_results->dDocTitle;
						$dDocType = $search_results->dDocType;

						//$dDocAuthor = $search_results->dDocAuthor;
						$xDocAuthor = $search_results->CustomDocMetaData->property[27]->value;

						$dSecurityGroup = $search_results->dSecurityGroup;
						$dDocAccount = $search_results->dDocAccount;

						$done = true;

					}

					// this else statement executes if multiple results are found, making the inner foreach loop the correct choice

					else {

						foreach ($value->CustomDocMetaData->property as $p)
							if ($p->name == 'xComments')
								$xComments = $p->value;

						$dID = $value->dID;
						$dRevLabel = $value->dRevisionID;
						$dDocName = $value->dDocName;
						$dDocTitle = $value->dDocTitle;
						$dDocType = $value->dDocType;

						//$dDocAuthor = $value->dDocAuthor;
						$xDocAuthor = $search_results->CustomDocMetaData->property[27]->value;

						$dSecurityGroup = $value->dSecurityGroup;
						$dDocAccount = $value->dDocAccount;

					}

					// if $docs_to_mod already has an instance of this document,
					// then we must modify *its* xComments instead of using the ones returned by AdvancedSearch!
					// thus, the following check

					if ($docs_to_mod[$dID] != null) {

						//echo "previous instance found<br />";
						$xComments = $docs_to_mod[$dID][CustomDocMetaData][property][value];

					}

					echo "original xComments: $xComments<br />";
					$xComments_split = split('[[:space:],]', $xComments); // we split according to characters which are either spaces or commas
					foreach ($xComments_split as $x){
						echo 'x: '.$x.'<br />';
					}
					// strcmp returns 0 iff $kw and $keyword_to_delete are equal, which is interpreted as false, thus all $kw == $keyword_to_delete are filtered out in the following

					$lambda = create_function('$kw','global $keyword_to_delete; return strcmp($kw, $keyword_to_delete) && strlen($kw) > 0;');
					$xComments_split = array_filter($xComments_split, $lambda);

					$new_xComments = implode(", ", $xComments_split);
					echo "new_xComments: $new_xComments<br />";

					$p = array('name' => 'xComments', 'value' => $new_xComments);
					$idcPL = array('property' => $p);

					echo "dID: $dID<br />";
					echo "dRevLabel: $dRevLabel<br />";
					echo "dDocName: $dDocName<br />";
					echo "dDocTitle: $dDocTitle<br />";
					echo "dDocType: $dDocType<br />";

					//echo "dDocAuthor: $dDocAuthor<br />";
					echo "xDocAuthor: $xDocAuthor<br />";

					echo "dSecurityGroup: $dSecurityGroup<br />";
					echo "dDocAccount: $dDocAccount<br />";

					$param = array('dID' => $dID,
									'dRevLabel' => $dRevLabel,
									'dDocName' => $dDocName,
									'dDocTitle' => $dDocTitle,
									'dDocType' => $dDocType,
									'xDocAuthor' => $xDocAuthor,
									'dSecurityGroup' => $dSecurityGroup,
									'dDocAccount' => $dDocAccount, // change this for production!
									'CustomDocMetaData' => $idcPL);

					$docs_to_mod[$dID] = $param; // we update the xComments of the appropriate document in the array

					echo '<br />';

				}

			}

		}

		// if $docs_to_change was not initialized (that is, if nothing was found for any name)
		// then this following loop will simply not run, and nothing will be updated

		foreach ($docs_to_mod as $dID => $param) {
			$client_checkin->UpdateDocInfo($param); // here we actually update the metadata of each document in the array
		}

		return false; // there was no soap failure

	}

	catch(SoapFault $e) {

		echo $e->faultstring;
		return true; // there was a soap failure

	}

}

?>