<div class="searchResults">
  <table width="100%"  border="1" cellspacing="0" cellpadding="2">
<?php 
    // null list in session implies that result size was exceeded
	$RackList = $_SESSION['RackList'];
        echo '<a name="top"></a>';	
	    echo '<tr>';
	    echo '<th colspan="5" class="value">Room Contents</th>';
	    echo '</tr>';	
		$roomCon = $_SESSION['roomConstraint'];
		$num = $RackList->length();
		if (($num > 1) && ($roomCon != null)) {
		  echo '<tr><td colspan="5"class=value>"'.$RackList->length().'" Enclosure\'s found in room: '.$_SESSION['roomConstraint']; 
		  echo '<font class="valueright">&nbsp;&nbsp;&nbsp;&nbsp;<a class="hyper" href="#report">Report Tools</a></td></tr>';
		} else if ($num == 1) {
		  echo '<tr><td colspan="5"class=value>"'.$RackList->length().'" Enclosure found in room: '.$_SESSION['roomConstraint'].'</td></tr>'; 
		} else if ($num > 1) {
		  echo '<tr><td colspan="5"class=value>"'.$RackList->length().'" Enclosure\'s found in multiple rooms';
		  echo '<font class="valueright">&nbsp;&nbsp;&nbsp;&nbsp;<a class="hyper" href="#report">Report Tools</a></td>';
		}
	    echo '<tr>';
        echo '<th nowrap>Enclosure Name</th>';
	    echo '<th>Enclosure Type</th>';
		echo '<th>Owner</th>';
		echo '<th>Show Power</th>';
		echo '<th>Parent Room</th>';
        echo '</tr>';
    if ($RackList == null) {
        echo '<tr><td class="warning bold" colspan=7>Search produced too many results to display.<br>';
        echo 'Limit is 5000. Try using the Additional<br>';
        echo 'Search Terms to constrain your search.</td></tr>';      
         
    } else if ($RackList->length() == 0) {
        echo '<tr><td class="warning bold" colspan=7>No Enclosure\'s found: please try another search.</td></tr>';
        
    } else {
	        
        $rackEntities = $RackList->getArray();
        foreach ($rackEntities as $rackEntity) {
            echo '<tr>';   
			echo '<td class=primary>'.$rackEntity->getInstanceName().'&nbsp;(<acronym title="Component ID">'.$rackEntity->getID().'</acronym>)</td>';
			echo '<td class=resulttext>'.$rackEntity->getrackType().'</td>';
			echo '<td class=resulttext>'.$rackEntity->getGroup().'</td>';
			echo '<td class=center><a class="hyper" href="action_power_contents_search.php?rackName='.$rackEntity->getID().'&rack='.$rackEntity->getInstanceName().'&rackType='.$rackEntity->getrackType().'&roomConstraint='.$rackEntity->getParentRoom().'">'.Power.'</a></td>';
			//echo '<td class=center>'.$rackEntity->getDescription().'</td>';
			echo '<td class=center>'.$rackEntity->getParentRoom().'</td>';
			echo '</tr>';
		}
    }

   echo '</table><table width="100%" border="1" cellspacing="0" cellpadding="2"><tr>';
   echo '<a name="report"></a>';
   include_once('../report/report_startform.php');
   include_once('../report/report_submit_rack.php');
   echo '</tr>';

?>
</table>