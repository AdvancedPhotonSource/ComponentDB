<div class="sectionTable">
  <table width="100%"  border="1" cellspacing="0" cellpadding="2">
<?php

/*
 * Written by Dawn Clemons
 * Specialized step in creating a printable report. Specific information
 * that applies only to the Component Types (Details) PHP viewer is displayed in a look
 * and feel similar to the viewer's. This output is buffered.
 */

   $ctEntity = $_SESSION['selectedComponentType'];
   $cList = &$ctEntity->getComponentList();

   echo '<tr><td colspan="2"class=value>Component Type Details</td></tr>';
   echo '<tr>';

   //Component Type
   echo '<td class="terminal" nowrap width="270">Component Type</td><td class="resulttextbold">'.$ctEntity->getCtName().'</td>';
	echo '</tr>';
	echo '<tr>';

   //Description
   echo '<td class="primary">Description</td><td class="results">'.$ctEntity->getCtDesc().'</td>';
	echo '</tr>';
	echo '<tr>';

   //Manufacturer
   echo '<td class="primary">Manufacturer</td><td class="results">'.$ctEntity->getMfgName().'</td>';
   echo '</tr>';
	echo '<tr>';

   //Form Factor
	echo '<td class="primary">Form Factor</td><td class="results">'.$ctEntity->getffName().'</td>';
	echo '</tr>';

   //Functions
	$result = $_SESSION['PersonList'];
	$PersonArray = $result->getArray();
	echo '<tr>';
   echo '<td class="primary">Functions</td><td class="results">';
   $functionList = &$ctEntity->getFunctionList();
	$fArray = &$functionList->getArray();
	$funcs = 0;
   foreach ($fArray as $functionEntity){
		$PersonEntity =$PersonArray[0];
			if ($PersonEntity->getLast()){
				$funcs = 1;
				echo $functionEntity->getFunction() .',';
			}
			if ($funcs != 1){
				echo 'Person Not Available';
			}
	}
	echo '</td></tr>';

   //Cognizant Person
	echo '<tr>';
	echo '<td class="primary">Cognizant Person</td><td class="results">';
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

   //Engineering Documentation
	echo '<tr>';
	echo '<td class="primary">Engineering Documentation</td><td class="results">';
	$DocList = &$ctEntity->getDocList();
	  $dArray = &$DocList->getArray();
	  $engdoc = 0;
	  foreach ($dArray as $DocEntity) {
	    if ($DocEntity->getdocType() == 'eng doc') {
		  $engdoc = 1;
		  echo 'Documentation Available';
	    }
	  }
	  if ($engdoc != 1) {
		  echo 'Documentation Not Available';
	  }
	echo '</td></tr>';

   //Component Instances
	echo '<tr>';
	echo '<td class="primary">Component Instances</td><td class="results">'.$cList->length().' Components Installed</td>';
	echo '</tr>';

   //Control IFR
	echo '<tr>';
	echo '<td class="secondary">Control IFR</td><td class="results">';
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

   //Control IFP
	echo '<tr>';
	echo '<td class="secondary">Control IFP</td><td class="results">';
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

   //Housing IFR
	echo '<tr>';
	echo '<td class="secondary">Housing IFR</td><td class="results">';
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

   //Housing IFP
	echo '<tr>';
	echo '<td class="secondary">Housing IFP</td><td class="results">';
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

   //Power IFR
	echo '<tr>';
	echo '<td class="secondary">Power IFR</td><td class="results">';
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

   //Power IFP
	echo '<tr>';
	echo '<td class="secondary">Power IFP</td><td class="results">';
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
?>
</table>
<br>

