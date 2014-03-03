<?php

/*
 * Written by Dawn Clemons
 * Specialized step in creating a printable report. Specific information
 * that applies only to the IOC PHP viewer is displayed in a look and feel
 * similar to the viewer's.
 */


   echo '<div class="sectionTable"><table width="100%" border="1" cellspacing="0" cellpadding="2">';
   echo '<tr><th colspan="7" class="value">Rack/Enclosure Contents</th></tr>';
   echo '<tr><th>Enclosure Name</th><th>Component Type</th><th>Component Name</th><th>Locator</th><th>Description</th><th>Manufacturer</th><th>Contents</th></tr>';

   //Get the data
   $RackContentsList = $_SESSION['rackContentsList'];
   $rackContentsEntities = $RackContentsList->getArray();

   foreach($rackContentsEntities as $rackContentsEntity)
   {
      //Display the data
      echo '<tr>';
	  if ($_SESSION['rack']) {
	  echo '<td class="primary">'.$_SESSION['rack'].'</td>';
	  } else {
	  echo '<td class="results">'.$_SESSION['compName'].'</td>';
	  }
	  echo '<td class="results">'.$rackContentsEntity->getcomponentTypeName().'</td>';
	  
	  if ($rackContentsEntity->getcomponentInstanceName()) {
      echo '<td class="results">'.$rackContentsEntity->getcomponentInstanceName().'</td>';
	  } else {
	  echo '<td>&nbsp;</td>';
	  }
	  if ($rackContentsEntity->getLogicalDesc()) {
	  echo '<td class="results">'.$rackContentsEntity->getLogicalDesc().'</td>';
	  } else {
	  echo '<td>&nbsp;</td>';
	  }
      echo '<td class="results">'.$rackContentsEntity->getDescription().'</td>';
      echo '<td class="results">'.$rackContentsEntity->getManufacturer().'</td>';
      //echo '<td>'.$rackContentsEntity->getID().'</td>';
			if ($rackContentsEntity->getSum() > 0) {
			  echo '<td class=center>Contents</td>';
			} else {
			  echo '<td class=center>None</td>';
			}
	  
      echo '</tr>';
   }
   echo '</table></div><br>';
?>