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
		echo '<tr>';
	    echo '<td class="primary">Description</td><td class="resulttext">'.$ctEntity->getCtDesc().'</td>';
		echo '</tr>';
		
		
		$wikiDesc = $ctEntity->getCtvDesc();
		$wikiDesc = getWikiHtml($wikiDesc);
		echo '<tr>';
		echo '<td class="chc">Verbose Description</td><td>'.$wikiDesc.'</td>';
		echo '</tr>';
		//echo '<tr>';
		//echo '<td class="primary">Verbose Description</td><td class="resulttext">'.$ctEntity->getCtvDesc().'</td>';
		//echo '</tr>';

        if ($ctEntity->getbeamlineInterest() == 'unknown') {
		echo '<tr>';
		echo '<td class="chc">Beamline Interest</td><td class="resulttext">Unknown</td>';
		echo '</tr>';
        } elseif ($ctEntity->getbeamlineInterest() == 'supported') {
        echo '<tr>';
		echo '<td class="chc">Beamline Interest</td><td class="resulttext">Supported</td>';
		echo '</tr>';
        } elseif ($ctEntity->getbeamlineInterest() == 'preferred') {
        echo '<tr>';
		echo '<td class="chc">Beamline Interest</td><td class="resulttext">Preferred</td>';
		echo '</tr>';
        } elseif ($ctEntity->getbeamlineInterest() == 'obsolete') {
		echo '<tr>';
		echo '<td class="chc">Beamline Interest</td><td class="resulttext">Obsolete</td>';
		echo '</tr>';
        }
		

		echo '<tr>';
		echo '<td class="chc">CHC Contact</td><td class="resulttext">';
		$chcPersonList = &$ctEntity->getchcPersonList();
		$pArray = &$chcPersonList->getArray();
		$peeps = 0;
          foreach ($pArray as $chcPersonEntity) {
			if ($chcPersonEntity->getchcLast()) {
			  $peeps = 1;
              echo $chcPersonEntity->getchcFirst() .' ';
			  echo $chcPersonEntity->getchcLast();
            }
          }
		  if ($peeps != 1) {
		    echo 'Person Not Available';
		    }
		echo '</td></tr>';

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
	    echo '<td class="primary">Manufacturer</td><td class="resulttext">'.$ctEntity->getMfgName().'</td>';
        echo '</tr>';
		
		if ($ctEntity->getnrtlStatus() == 'ANL') {
		echo '<tr>';
		echo '<td class="primary">NRTL Status</td><td class="resulttext">ANL Inspection Required</td>';
		echo '</tr>';		
		} elseif ($ctEntity->getnrtlStatus() == 'NA') {
		echo '<tr>';
		echo '<td class="primary">NRTL Status</td><td class="resulttext">Not Applicable</td>';
		echo '</tr>';		
		} elseif ($ctEntity->getnrtlStatus() == 'TBD') {
		echo '<tr>';
		echo '<td class="primary">NRTL Status</td><td class="resulttext">To Be Determined</td>';
		echo '</tr>';		
		} elseif ($ctEntity->getnrtlStatus() == 'NRTL') {
		echo '<tr>';
		echo '<td class="primary">NRTL Status</td><td class="resulttext">NRTL Approved - '.$ctEntity->getnrtlAgency().'</td>';
		echo '</tr>';		
		}
		
		echo '<tr>';
		echo '<td class="primary">Form Factor</td><td class="resulttext">'.$ctEntity->getffName().'</td>';
		echo '</tr>';
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
		    echo 'Function Not Available';
		    }
		echo '</td></tr>';



		
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
		
		echo '<td class="primary">Engineering Documentation</td><td class="resulttext">';
		$DocList = &$ctEntity->getDocList();
		  $dArray = &$DocList->getArray();
		  $engdoc = 0;
		  foreach ($dArray as $DocEntity) {
		    if ($DocEntity->getdocType() == 'eng doc') {
			  $engdoc = 1;
			  echo '<a href="'.$DocEntity->geturi().'" target="_blank">Engineering Documentation</a>';
		    }
		  }
		  if ($engdoc != 1) {
			  echo 'Documentation Not Available';
		  }
		echo '</td></tr>';

		echo '<tr>';
		echo '<td class="primary">Feature Sheet</td><td class="resulttext">';
		$DocList = &$ctEntity->getDocList();
		  $dArray = &$DocList->getArray();
		  $fsdoc = 0;
		  foreach ($dArray as $DocEntity) {
		    if ($DocEntity->getdocType() == 'feature sheet') {
			  $fsdoc = 1;
			  echo '<a href="'.$DocEntity->geturi().'" target="_blank">Feature Sheet</a>';
		    }
		  }
		  if ($fsdoc != 1) {
			  echo 'Documentation Not Available';
		  }
		echo '</td></tr>';

		echo '<tr>';
		echo '<td class="primary">Quick Reference Manual</td><td class="resulttext">';
		$DocList = &$ctEntity->getDocList();
		  $dArray = &$DocList->getArray();
		  $qrmdoc = 0;
		  foreach ($dArray as $DocEntity) {
		    if ($DocEntity->getdocType() == 'quick ref') {
			  $qrmdoc = 1;
			  echo '<a href="'.$DocEntity->geturi().'" target="_blank">Quick Reference Manual</a>';
		    }
		  }
		  if ($qrmdoc != 1) {
			  echo 'Documentation Not Available';
		  }
		echo '</td></tr>';

		//echo '<tr>';
		//echo '<td class="primary">Component Instances</td><td class="resulttext"><a href="action_comp_instances_search.php?ID='.$ctEntity->getID().'">"'.$cList->length().'" Components Installed</a></td>';
		//echo '</tr>';

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
		//include('../report/report_startform.php');
		//include('../report/report_submit_comp_details.php');

?>
</table>
<p>
  <table width="90%" align="center" border="0">
  <tr><td class="resulttext">The table below show the wiki formatting that is availavle for the Verbose Description field in the Details above.  
  Use this markup in the IDT Component Type window in the Verbose Description entry box.</td></tr>
  </table>
  <p>
  <table border='1' bgcolor='ghostwhite' align="center">
  <tr><th>Entered Text</th><th>Resulting Output</th></tr>
  <tr><td><tt>Task#99</tt></td><td><a href='http://www.aps.anl.gov/asd/controls/tman/tasks.php?id=99'>Task#99</a></td></tr>
  <tr><td><tt>Ctllog#99</tt></td><td><a href='http://www.aps.anl.gov/asd/controls/newlog/entries.php?id=99'>Ctllog#99</a></td></tr>
  <tr><td><tt>IDlog#99</tt></td><td><a href='http://www.aps.anl.gov/asd/controls/idlog/entries.php?id=99'>IDlog#99</a></td></tr>
  <tr><td><tt>RMD#99</tt></td><td><a href='http://www.aps4.anl.gov/cgi-bin/ops/rmd-cgi/rmd_view.cgi?mode=manager&rmdNo=99'>RMD#99</a></td></tr>
  <tr><td><tt>RSP#99</tt></td><td><a href='http://www.aps4.anl.gov/cgi-bin/ops/rmd-cgi/response_view.cgi?mode=manager&responseNo=99'>RSP#99</a></td></tr>
  <tr><td><tt>http://www.aps.anl.gov/</tt></td>
  <td><a href='http://www.aps.anl.gov/'>http://www.aps.anl.gov/</a></td></tr>
  <tr><td><tt>__bold__</tt></td><td><b>bold</b></td></td></tr>
  <tr><td><tt>''italic''</tt></td><td><i>italic</i></td></tr>
  <tr><td><tt>__''bold italic''__</tt></td><td><i><b>bold italic</b></i></td></tr>
  <tr><td><tt>----</tt></td><td><hr></td></tr>
  <tr><td><tt>!!! Major Heading</tt></td><td><h1>Major Heading</h1></td></tr>
  <tr><td><tt>!! Medium Heading</tt></td><td><h2>Medium Heading</h2></td></tr>
  <tr><td><tt>! Heading</tt></td><td><h4>Heading</h4></td></tr>
  <tr><td><tt>Adjacent lines combine<br>into a single paragraph.</tt></td>
  <td><p>Adjacent lines combine into a single paragraph.</p></td></tr>
  <tr><td><tt>Line%%%break</tt></td><td><p>Line<br>break</p></td></tr>
  <tr><td><tt>Leave a blank line.<br>&nbsp;<br>Separate paragraphs.</tt></td>
  <td><p>Leave a blank line.</p><p>Separate paragraphs.</p></td></tr>
  <tr><td><tt>&nbsp;Start lines with a<br>&nbsp;space to maintain the<br>
  &nbsp;typed formatting.</tt></td><td><tt>&nbsp;Start lines with a<br>
  &nbsp;space to maintain the<br>&nbsp;typed formatting.</pre></td></tr>
  </table>

