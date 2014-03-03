<div class="searchResults">
  <table width="100%"  border="1" cellspacing="0" cellpadding="2">
<?php
        $ctEntity = $_SESSION['selectedComponentType'];
        $cList = &$ctEntity->getComponentList();
		logEntry('debug',"here i am in results details, cList is: ".print_r($cList,true));

	    echo '<tr><td colspan="2"class=value>Component Type Details</td></tr>';
	    echo '<tr>';
        echo '<td class="terminal" nowrap width="270">Component Type</td><td class="resulttextbold">'.$ctEntity->getCtName().'</td>';
		echo '</tr>';
		
  // Description		
		echo '<tr>';
	    echo '<td class="primary">Description</td><td class="resulttext">'.$ctEntity->getCtDesc().'</td>';
		echo '</tr>';
		
  // Manufacturer		
		echo '<tr>';
	    echo '<td class="primary">Manufacturer</td><td class="resulttext">'.$ctEntity->getMfgName().'</td>';
        echo '</tr>';
		
  // Form Factor		
		echo '<tr>';
		echo '<td class="primary">Form Factor</td><td class="resulttext">'.$ctEntity->getffName().'</td>';
		echo '</tr>';
		
  // Functions		
		echo '<tr>';
        echo '<td class="primary">Functions</td><td class="resulttext">';

        $functionList = &$ctEntity->getFunctionList();
		$fArray = &$functionList->getArray();
		$funcs = 0;
          foreach ($fArray as $functionEntity) {
			if ($PersonEntity->getLast()) {
			  $funcs = 1;
              echo $functionEntity->getFunction() .',';
            }
          }
		  if ($funcs != 1) {
		    echo 'Person Not Available';
		    }
		echo '</td></tr>';

  // Cognizant Person
		echo '<tr>';
		echo '<td class="primary">Cognizant Person</td><td class="resulttext">';
		$PersonList = &$ctEntity->getPersonList();
		$pArray = &$PersonList->getArray();
		$peeps = 0;
          foreach ($pArray as $PersonEntity) {
			if ($PersonEntity->getLast()) {
			  $peeps = 1;
              echo $PersonEntity->getFirst() .' ';
			  echo $PersonEntity->getLast();
            }
          }
		  if ($peeps != 1) {
		    echo 'Person Not Available';
		    }
		echo '</td></tr>';

		echo '<tr>';
		
		/*
		// the next lines will display an entry on the details page with a link to any ICMS committed doc.
        echo '<td class="primary">ICMS Documentation</td><td class="resulttext">';
		$DocList = &$ctEntity->getDocList();
		  $dArray = &$DocList->getArray();
		  $icmsdoc = 0;
		  foreach ($dArray as $DocEntity) {
		  //ICMS doc is a guess at what Claude will name the doc type
		    if ($DocEntity->getdocType() == 'ICMS doc') {
			  $icmsdoc = 1;
			  echo '<a href="http://icmsdocs.aps.anl.gov/new_docs/idcplg?IdcService=PNE_GET_SEARCH_RESULTS&QueryText=(+(dDocTitle+<substring>+'.$ctEntity->getCtName().')+)+and+not+dDocType+<matches>+'CAD_Dependency'&ftx=&SortField=SCORE&SortOrder=Desc&topicString1=updateKeys:" target="_blank">ICMS Documentation</a>';
		    }
		  }
		  if ($icmsdoc != 1) {
			  echo 'Documentation Not Available';
		  }
		echo '</td></tr>';
		//
		*/
		
  // Engineering Documentation		
		echo '<td class="primary">Engineering Documentation</td><td class="resulttext">';
		$DocList = &$ctEntity->getDocList();
		  $dArray = &$DocList->getArray();
		  $engdoc = 0;
		  foreach ($dArray as $DocEntity) {
		    if ($DocEntity->getdocType() == 'eng doc') {
			  $engdoc = 1;
			  echo '<a class="hyper" href="'.$DocEntity->geturi().'" target="_blank">Engineering Documentation</a>';
		    }
		  }
		  if ($engdoc != 1) {
			  echo 'Documentation Not Available';
		  }
		echo '</td></tr>';

  // Feature Sheet
		echo '<tr>';
		echo '<td class="primary">Feature Sheet</td><td class="resulttext">';
		$DocList = &$ctEntity->getDocList();
		  $dArray = &$DocList->getArray();
		  $fsdoc = 0;
		  foreach ($dArray as $DocEntity) {
		    if ($DocEntity->getdocType() == 'feature sheet') {
			  $fsdoc = 1;
			  echo '<a class="hyper" href="'.$DocEntity->geturi().'" target="_blank">Feature Sheet</a>';
		    }
		  }
		  if ($fsdoc != 1) {
			  echo 'Documentation Not Available';
		  }
		echo '</td></tr>';

  // Quick Reference Manual
		echo '<tr>';
		echo '<td class="primary">Quick Reference Manual</td><td class="resulttext">';
		$DocList = &$ctEntity->getDocList();
		  $dArray = &$DocList->getArray();
		  $qrmdoc = 0;
		  foreach ($dArray as $DocEntity) {
		    if ($DocEntity->getdocType() == 'quick ref') {
			  $qrmdoc = 1;
			  echo '<a class="hyper" href="'.$DocEntity->geturi().'" target="_blank">Quick Reference Manual</a>';
		    }
		  }
		  if ($qrmdoc != 1) {
			  echo 'Documentation Not Available';
		  }
		echo '</td></tr>';

  // Component Instances
		echo '<tr>';
		echo '<td class="primary">Component Instances</td>';
		echo '<td class="resulttext">"<strong>'.$cList->length().'</strong>" Components Installed';
		echo '&nbsp;&nbsp;<a class="hyper" href="action_comp_instances_search.php?ID='.$ctEntity->getID().'&path=short">Short Location Path</a>';
		echo '&nbsp;&nbsp;<a class="hyper" href="action_comp_instances_search.php?ID='.$ctEntity->getID().'&path=expanded">Expanded Location Path</a></td>';
		echo '</tr>';

  // Control Interface Required
		echo '<tr>';
		echo '<td class="secondary">Control IFR</td><td class="resulttext">';
		$IFList = &$ctEntity->getIFList();
		  $ifArray = &$IFList->getArray();
		  $ifcount = 0;
		  foreach ($ifArray as $IFEntity) {
		    if (($IFEntity->getRequired() == 1) && ($IFEntity->getComponentRelTypeID() == 1)) {
			  $ifcount = 1;
			  echo $IFEntity->getIFType().', ';
		    }
		  }
		  if ($ifcount != 1) {
			  echo 'Interface Not Provided';
		  }
		echo '</td></tr>';
  
  // Control Interface Provided
		echo '<tr>';
		echo '<td class="secondary">Control IFP</td><td class="resulttext">';
		$IFList = &$ctEntity->getIFList();
		  $ifArray = &$IFList->getArray();
		  $ifcount = 0;
		  foreach ($ifArray as $IFEntity) {
		    if (($IFEntity->getPresented() == 1) && ($IFEntity->getComponentRelTypeID() == 1)) {
			  $ifcount = 1;
			  echo $IFEntity->getIFType().', ';
		    }
		  }
		  if ($ifcount != 1) {
			  echo 'Interface Not Provided';
		  }
		echo '</td></tr>';

  // Housing Interface Required
		echo '<tr>';
		echo '<td class="secondary">Housing IFR</td><td class="resulttext">';
		$IFList = &$ctEntity->getIFList();
		  $ifArray = &$IFList->getArray();
		  $ifcount = 0;
		  foreach ($ifArray as $IFEntity) {
		    if (($IFEntity->getRequired() == 1) && ($IFEntity->getComponentRelTypeID() == 2)) {
			  $ifcount = 1;
			  echo $IFEntity->getIFType().', ';
		    }
		  }
		  if ($ifcount != 1) {
			  echo 'Interface Not Provided';
		  }
		echo '</td></tr>';

  // Housing Interface Provided
		echo '<tr>';
		echo '<td class="secondary">Housing IFP</td><td class="resulttext">';
		$IFList = &$ctEntity->getIFList();
		  $ifArray = &$IFList->getArray();
		  $ifcount = 0;
		  foreach ($ifArray as $IFEntity) {
		    if (($IFEntity->getPresented() == 1) && ($IFEntity->getComponentRelTypeID() == 2)) {
			  $ifcount = 1;
			  echo $IFEntity->getIFType().', ';
		    }
		  }
		  if ($ifcount != 1) {
			  echo 'Interface Not Provided';
		  }
		echo '</td></tr>';

  // Power Interface Required
		echo '<tr>';
		echo '<td class="secondary">Power IFR</td><td class="resulttext">';
		$IFList = &$ctEntity->getIFList();
		  $ifArray = &$IFList->getArray();
		  $ifcount = 0;
		  foreach ($ifArray as $IFEntity) {
		    if (($IFEntity->getRequired() == 1) && ($IFEntity->getComponentRelTypeID() == 3)) {
			  $ifcount = 1;
			  echo $IFEntity->getIFType().', ';
		    }
		  }
		  if ($ifcount != 1) {
			  echo 'Interface Not Provided';
		  }
		echo '</td></tr>';

  // Power Interface Provided
		echo '<tr>';
		echo '<td class="secondary">Power IFP</td><td class="resulttext">';
		$IFList = &$ctEntity->getIFList();
		  $ifArray = &$IFList->getArray();
		  $ifcount = 0;
		  foreach ($ifArray as $IFEntity) {
		    if (($IFEntity->getPresented() == 1) && ($IFEntity->getComponentRelTypeID() == 3)) {
			  $ifcount = 1;
			  echo $IFEntity->getIFType().', ';
		    }
		  }
		  if ($ifcount != 1) {
			  echo 'Interface Not Provided';
		  }
		echo '</td></tr>';
		include('../report/report_startform.php');
		include('../report/report_submit_comp_details.php');

?>
</table>

