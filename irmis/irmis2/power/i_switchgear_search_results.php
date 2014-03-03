<div class="searchResults">
  <table width="100%"  border="1" cellspacing="0" cellpadding="2">
<?php 
    // null list in session implies that result size was exceeded
	$SGList = $_SESSION['SGList'];
        echo '<a name="top"></a>';	
	    echo '<tr>';
	    echo '<th colspan="6" class="value">Switch Gear Contents</th>';
	    echo '</tr>';	
		$switchgearCon = $_SESSION['switchgearConstraint'];
		$num = $SGList->length();
		if (($num > 1) && ($switchgearCon != null)) {
		  echo '<tr><td colspan="6"class=value>"'.$SGList->length().'"<i> Power Components found in Switch Gear: </i>'.$_SESSION['switchgearConstraint']; 
		  echo '<font class="valueright">&nbsp;&nbsp;&nbsp;&nbsp;<a class="hyper" href="#report">Report Tools</a></td></tr>';
		} else if ($num == 1) {
		  echo '<tr><td colspan="6"class=value>"'.$SGList->length().'"<i> Power Component found in Switch Gear: </i>'.$_SESSION['switchgearConstraint'];
		  echo '<font class="valueright">&nbsp;&nbsp;&nbsp;&nbsp;<a class="hyper" href="#report">Report Tools</a></td></tr>';
		} else if ($num > 1) {
		  echo '<tr><td colspan="6"class=value>"'.$SGList->length().'"<i> Power Components\'s found in multiple Switch Gears</i>';
		  echo '<font class="valueright">&nbsp;&nbsp;&nbsp;&nbsp;<a class="hyper" href="#report">Report Tools</a></td>';
		}
	    echo '<tr>';
        echo '<th nowrap>Switch Gear Component</th>';
	    echo '<th>Power Component Type</th>';
		echo '<th>Description</th>';
		echo '<th>Room</th>';
		echo '<th>Building</th>';
		echo '<th>Show Power</th>';
        echo '</tr>';
    if ($SGList == null) {
        echo '<tr><td class="warning bold" colspan=6>Search produced too many results to display.<br>';
        echo 'Limit is 5000. Try using the Additional<br>';
        echo 'Search Terms to constrain your search.</td></tr>';      
         
    } else if ($SGList->length() == 0) {
        echo '<tr><td class="warning bold" colspan=6>No Switch Gear Components\'s found: please try another search.</td></tr>';
        
    } else {
	        
        $SGEntities = $SGList->getArray();
        foreach ($SGEntities as $SGEntity) {
            echo '<tr>';   
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
    }

   echo '</table><table width="100%" border="1" cellspacing="0" cellpadding="2"><tr>';
   echo '<a name="report"></a>';
   include_once('../report/report_startform.php');
   include_once('../report/report_submit_SG.php');
   echo '</tr>';

?>
</table>