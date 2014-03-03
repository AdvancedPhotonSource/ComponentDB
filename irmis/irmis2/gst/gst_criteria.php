<?
	if (isset($_POST['sentData']))
	{
		header('Content-type: text/xml');
		$data = $_POST['sentData'];
    	$dom = new domDocument;
		$dom->loadXML($data);
		$xml = simplexml_import_dom($dom);
		
		if ($xml) 
		{
			$majorCategoryID = $xml->majorCategoryID[0];
	        $minorCategoryID = $xml->minorCategorySectionID[0];
	        $criteriaResultsSectionID = $xml->criteriaResultsSectionID[0];
	        $searchBoxID = $xml->searchBoxID[0];
	        $tabLinksSectionID = $xml->tabLinksSectionID[0];
	        $allTabContentSectionID = $xml->allTabContentSectionID[0];
	        $searchTabSectionID = $xml->searchTabSectionID[0];
	        $criteriaString = $xml->criteriaString[0];
	        $searchTabID = $xml->searchTabID[0];
    	}
	    else
	    {
		    echo "ERROR could not parse xml string from sent data<br />";
    	}
		
		$rows = explode(":", $criteriaString);
		
		$select_box_string = "";
		
		for($i = 0; $i < count($rows); $i++)
		{
			$categoryRowData = explode(",", $rows[$i]);
			
			// Find in the string where the Major Category that you want starts, in order to get the data for that Major Category
			
			if ($majorCategoryID == $categoryRowData[0])
			{
				// If it is equal to 'ALL' it has a different set up than the other categories. This 'ALL' is hardcoded in and is saved in the javascript
				// file as an index in the variable Category.
				
				if ($majorCategoryID == 'ALL')
				{ 
					$select_box_string = $select_box_string."<select id='".$majorCategoryID."' multiple size='".(count($categoryRowData)-1)."' onchange=\"updateCriteria('".$majorCategoryID."', '".$minorCategoryID."', '".$criteriaResultsSectionID."', '".$searchBoxID."', '".$tabLinksSectionID."', '".$allTabContentSectionID."', '".$searchTabSectionID."', '".$searchTabID."');\">";
				}
				else
				{
					$select_box_string = $select_box_string."<b class='title'>".$majorCategoryID."</b><br><select id='".$majorCategoryID."' multiple size='".(count($categoryRowData)-1)."' onchange=\"checkAll('".$majorCategoryID."');displaySearchCriteriaResults('".$majorCategoryID."', '".$criteriaResultsSectionID."', '".$searchBoxID."', '".$tabLinksSectionID."', '".$allTabContentSectionID."', '".$searchTabSectionID."', '".$searchTabID."', '');\">";
				}
				
				// For every Minor Category in the Major Category, make an option for it.
				
				for($j = 1; $j < count($categoryRowData); $j++)
				{
					$select_box_string = $select_box_string."<option>".$categoryRowData[$j]."</option>";
				}
				$select_box_string = $select_box_string."</select>";
			}
		}

		$doc = new DOMDocument();        
	    $root = $doc->createElement('criteriaResponse');
	    $root = $doc->appendChild($root);
	    
	    $child = $doc->createElement('selectBox');
	    $child = $root->appendChild($child);	    
	    $value = $doc->createTextNode($select_box_string);
	    $value = $child->appendChild($value);
	    
	    $xml_string = $doc->saveXML();
		echo $xml_string;
	}
?>
