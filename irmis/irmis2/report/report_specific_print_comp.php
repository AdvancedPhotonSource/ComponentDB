<?php

/*
 * Written by Dawn Clemons
 * Specialized step in creating a printable report. Specific information
 * that applies only to the Component Types PHP viewer is displayed in a look
 * and feel similar to the viewer's. This output is buffered.
 */

   //Displays specific information from the selected individual Component report component type sections

   echo '<div class="sectionTable"><table width="100%" border="1" cellspacing="0" cellpadding="2">';
   // null list in session implies that result size was exceeded
	$ComponentTypeList = $_SESSION['ComponentTypeList'];
   //Display column headings
   echo '<tr><td colspan="8"class=value>"'.$ComponentTypeList->length().'" Components Found</td></tr>';
	echo '<tr>';
   echo '<th nowrap>Component Type</th>';
	echo '<th>Description</th>';
	echo '<th>Manufacturer</th>';
   echo '</tr>';

   //If there were too many results, report will not be finished
   if ($ComponentTypeList == null) {
      echo '<tr><td class="warning bold" colspan=8>Search produced too many results to display.<br>';
      echo 'Limit is 5000. Try using the Additional<br>';
      echo 'Search Terms to constrain your search.</td></tr>';
    }
   //If there aren't any results, report will not be finished
   else if ($ComponentTypeList->length() == 0) {
      echo '<tr><td class="warning bold" colspan=8>No Component\'s found: please try another search.</td></tr>';
   }
   //The report will be finished, so display data
   else {
      $ctEntities = $ComponentTypeList->getArray();
      foreach ($ctEntities as $ctEntity) {
         echo '<tr>';
         echo '<td>'.$ctEntity->getCtName().'&nbsp;('.$ctEntity->getID().')</td>';
         echo '<td>'.$ctEntity->getCtDesc().'</td>';
         echo '<td>'.$ctEntity->getMfgName().'</td>';
         echo '</tr>';
      }
   }
   echo '</table></div><br>';
?>