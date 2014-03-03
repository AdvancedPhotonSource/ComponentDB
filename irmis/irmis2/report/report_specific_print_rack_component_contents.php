<?php

/*
 * Written by Dawn Clemons
 * Specialized step in creating a printable report. Specific information
 * that applies only to the IOC PHP viewer is displayed in a look and feel
 * similar to the viewer's.
 */


   echo '<div class="sectionTable"><table width="100%" border="1" cellspacing="0" cellpadding="2">';
   echo '<tr><th colspan="7" class="value">'.$_SESSION['compName'].' Contents</th></tr>';
   echo '<tr><th>Enclosure Name</th><th>Component Type</th><th>Component Name</th><th>Locator</th><th>Description</th><th>Manufacturer</th><th>Contents</th></tr>';

   //Get the data
   $RackCCList = $_SESSION['rackCCList'];
   $rackCCEntities = $RackCCList->getArray();

   foreach($rackCCEntities as $rackCCEntity)
   {
      //Display the data
      echo '<tr>';
	  if ($_SESSION['rack']) {
	  echo '<td class="primary">'.$_SESSION['rack'].'</td>';
	  } else {
	  echo '<td class="primary">'.$_SESSION['compName'].'</td>';
	  }
	  echo '<td class="results">'.$rackCCEntity->getcomponentTypeName().'</td>';
	  
	  if ($rackCCEntity->getcomponentInstanceName()) {
      echo '<td class="results">'.$rackCCEntity->getcomponentInstanceName().'</td>';
	  } else {
	  echo '<td>&nbsp;</td>';
	  }
	  if ($rackCCEntity->getLogicalDesc()) {
	  echo '<td class="results">'.$rackCCEntity->getLogicalDesc().'</td>';
	  } else {
	  echo '<td>&nbsp;</td>';
	  }
      echo '<td class="results">'.$rackCCEntity->getDescription().'</td>';
      echo '<td class="results">'.$rackCCEntity->getManufacturer().'</td>';
      //echo '<td>'.$rackContentsEntity->getID().'</td>';
			if ($rackCCEntity->getSum() > 0) {
			  echo '<td class=center>Contents</td>';
			} else {
			  echo '<td class=center>None</td>';
			}
	  
      echo '</tr>';
   }
   echo '</table></div><br>';
?>