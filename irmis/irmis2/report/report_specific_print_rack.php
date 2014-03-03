<?php

/*
 * Written by Dawn Clemons
 * Specialized step in creating a printable report. Specific information
 * that applies only to the IOC PHP viewer is displayed in a look and feel
 * similar to the viewer's.
 */
   $RackList = $_SESSION['RackList'];
   $roomCon = $_SESSION['roomConstraint'];
   $num = $RackList->length();
   echo '<div class="sectionTable"><table width="100%" border="1" cellspacing="0" cellpadding="2">';
   echo '<tr><th colspan="6" class="value">Room Contents</th></tr>';
   echo '<tr><td colspan="6"class=value>"'.$RackList->length().'" Enclosure\'s found in room: '.$_SESSION['roomConstraint'].'</td></tr>'; 
   echo '<tr><th>Enclosure Name</th><th>Enclosure Type</th><th>Owner</th><th>Parent Room</th></tr>';

   //Get the data
   $RackList = $_SESSION['RackList'];
   $rackEntities = $RackList->getArray();

   foreach($rackEntities as $rackEntity)
   {
      //Display the data
      echo '<tr>';
      echo '<td class="primary">'.$rackEntity->getInstanceName().'&nbsp;('.$rackEntity->getID().')</td>';
      echo '<td class="results">'.$rackEntity->getrackType().'</td>';
	  echo '<td class="results">'.$rackEntity->getGroup().'</td>';
	  echo '<td class="results">'.$rackEntity->getParentRoom().'</td>';
      echo '</tr>';
   }
   echo '</table></div><br>';
?>