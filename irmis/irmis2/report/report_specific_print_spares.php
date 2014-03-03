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
	$SpareList = $_SESSION['SpareList'];
   //Display column headings
   echo '<tr><td colspan="8"class=value>"'.$SpareList->length().'" Components Found</td></tr>';
   echo '<tr>';
   echo '<th nowrap>Component Type</th>';
   echo '<th>Description</th>';
   echo '<th>Installed Qty</th>';
   echo '<th>Spare Qty</th>';
   echo '<th>Spare Location</th>';
   echo '<th>Stock Qty</th>';
   echo '</tr>';

   //If there were too many results, report will not be finished
   if ($SpareList == null) {
      echo '<tr><td class="warning bold" colspan=8>Search produced too many results to display.<br>';
      echo 'Limit is 5000. Try using the Additional<br>';
      echo 'Search Terms to constrain your search.</td></tr>';
    }
   //If there aren't any results, report will not be finished
   else if ($SpareList->length() == 0) {
      echo '<tr><td class="warning bold" colspan=8>No Component\'s found: please try another search.</td></tr>';
   }
   //The report will be finished, so display data
   else {
      $spEntities = $SpareList->getArray();
      foreach ($spEntities as $spEntity) {
         echo '<tr>';
         echo '<td class=primary>'.$spEntity->getCtName().'</td>';
         echo '<td class=results>'.$spEntity->getCtDesc().'</td>';
         echo '<td class=center>'.$spEntity->getInsQty().'</td>';
		 echo '<td class=center>'.$spEntity->getSpareQty().'</td>';
		 if ($spEntity->getSpareLoc()) {
			  echo '<td class=center>'.$spEntity->getSpareLoc().'</td>';
			} else {
			  echo '<td>&nbsp;</td>';
			}
		 echo '<td class=center>'.$spEntity->getStockQty().'</td>';
         echo '</tr>';
      }
   }
   echo '</table></div><br>';
?>