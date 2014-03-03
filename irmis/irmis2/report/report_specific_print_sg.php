<?php

/*
 * Written by Dawn Clemons
 * Modified by Scott Benes
 * Specialized step in creating a printable report. Specific information
 * that applies only to the IOC PHP viewer is displayed in a look and feel
 * similar to the viewer's.
 */
   $SGList = $_SESSION['SGList'];
   $SGEntities = $SGList->getArray();
   
   echo '<div class="sectionTable"><table width="100%" border="1" cellspacing="0" cellpadding="2">';
   echo '<tr><th colspan="6" class="value">Switch Gear Contents</th></tr>';
   echo '<tr><td colspan="6"class=value>'.$SGList->length().'&nbsp;Power Component\'s found in Switch Gear:&nbsp;'.$_SESSION['switchgearConstraint'].'</td></tr>';
   echo '<tr><th>Switch Gear Component</th>';
   echo '<th>Power Component Type</th>';
   echo '<th>Description</th>';
   echo '<th>Room</th>';
   echo '<th>Building</th>';
   echo '<th>Show Power</th>';
   echo '</tr>';

   //Get the data

   foreach($SGEntities as $SGEntity)
   {
      //Display the data
     echo '<tr>';
	 
	  //Switch Gear Component
	  echo '<td class=primary>'.$SGEntity->getlogicalDesc().'&nbsp;(<acronym title="Component ID">'.$SGEntity->getID().'</acronym>)</td>';
	  
	  //Power Component Type
	  echo '<td class=resulttext>'.$SGEntity->getSGType().'</td>';
	  
	  //Description
	  echo '<td class=resulttext>'.$SGEntity->getDescription().'</td>';
	   
      //Room		
		if ($SGEntity->getParentRoom()) {
			echo '<td class=center>'.$SGEntity->getParentRoom().'</td>';
			$_SESSION['SGRoom'] = $SGEntity->getParentRoom();
			} else {
				echo '<td class=center>-</td>';
			}			
			
	   //Building
			if ($SGEntity->getParentBldg()) {
				echo '<td class=center>'.$SGEntity->getParentBldg().'</td>';
				$_SESSION['SGBldg'] = $SGEntity->getParentBldg();
			} else {
			    echo '<td class=center>-</td>';
			}
			
		//Show Power
			echo '<td class=center><a class="hyper" href="action_switchgear_contents_search.php?SGName='.$SGEntity->getID().'&SG='.$SGEntity->getInstanceName().
			'&SGType='.$SGEntity->getSGType().'">Feeds Power to</a></td>';
			

      echo '</tr>';
   }
   echo '</table></div><br>';
?>