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

	$ServerList = $_SESSION['serverList'];

   //Display column headings
   echo '<tr><td colspan="8"class=value>"'.$ServerList->length().'" Servers Found</td></tr>';
	echo '<tr>';
   echo '<th nowrap>Server Name</th>';
	echo '<th>Description</th>';
	echo '<th>Operating System</th>';
	echo '<th>Cognizant</th>';
   echo '</tr>';

   //If there were too many results, report will not be finished
   if ($ServerList == null) {
      echo '<tr><td class="warning bold" colspan=8>Search produced too many results to display.<br>';
      echo 'Limit is 5000. Try using the Additional<br>';
      echo 'Search Terms to constrain your search.</td></tr>';
    }
   //If there aren't any results, report will not be finished
   else if ($ServerList->length() == 0) {
      echo '<tr><td class="warning bold" colspan=8>No Server\'s found: please try another search.</td></tr>';
   }
   //The report will be finished, so display data
   else {
      $serverEntities = $ServerList->getArray();
      foreach ($serverEntities as $serverEntity) {
         echo '<tr>';
         echo '<td class=primary>'.$serverEntity->getServerName().'</td>';

         $description = $serverEntity->getServerDescription();

         if ($description){
         	echo '<td class=results>'.$description.'</td>';
         }else {
         	echo '<td>&nbsp;</td>';
         }
         echo '<td class=center nowrap>'.$serverEntity->getOperatingSystem().'</td>';
         $cognizant = $serverEntity->getCognizantFirstName()." ".$serverEntity->getCognizantLastName();
         echo '<td class=center nowrap>'.$cognizant.'</td>';
         echo '</tr>';
      }
   }
   echo '</table></div><br>';
?>