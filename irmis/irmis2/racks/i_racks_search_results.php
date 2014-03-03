<div class="searchResults">
  
  <table width="100%"  border="1" cellspacing="0" cellpadding="2">
<?php 
    // null list in session implies that result size was exceeded
	$RackList = $_SESSION['RackList'];
        echo '<a name="top"></a>';	
	    echo '<tr>';
	    echo '<th colspan="6" class="value">Room Contents</th>';
	    echo '</tr>';	
		$roomCon = $_SESSION['roomConstraint'];
		$num = $RackList->length();
		if (($num > 1) && ($roomCon != null)) {
		  echo '<tr><td colspan="6"class=value>"'.$RackList->length().'" Enclosure\'s found in room: '.$_SESSION['roomConstraint']; 
		  echo '<font class="valueright">&nbsp;&nbsp;&nbsp;&nbsp;<a class="hyper" href="#report">Report Tools</a></td></tr>';
		} else if ($num == 1) {
		  echo '<tr><td colspan="6"class=value>"'.$RackList->length().'" Enclosure found in room: '.$_SESSION['roomConstraint'].'</td></tr>'; 
		} else if ($num > 1) {
		  echo '<tr><td colspan="6"class=value>"'.$RackList->length().'" Enclosure\'s found in multiple rooms';
		  echo '<font class="valueright">&nbsp;&nbsp;&nbsp;&nbsp;<a class="hyper" href="#report">Report Tools</a></td>';
		}
	    echo '<tr>';
        echo '<th nowrap>Enclosure Name</th>';
	    echo '<th>Enclosure Type</th>';
		echo '<th>Owner</th>';
		echo '<th>Show Contents</th>';
	    echo '<th>Description</th>';
		echo '<th>Parent Room</th>';
        echo '</tr>';
    if ($RackList == null) {
        echo '<tr><td class="warning bold" colspan=6>Search produced too many results to display.<br>';
        echo 'Limit is 5000. Try using the Additional<br>';
        echo 'Search Terms to constrain your search.</td></tr>';      
         
    } else if ($RackList->length() == 0) {
        echo '<tr><td class="warning bold" colspan=7>No Enclosure\'s found: please try another search.</td></tr>';
        
    } else {
	        
        $rackEntities = $RackList->getArray();
        foreach ($rackEntities as $rackEntity) {
            echo '<tr>';  
			 if ($rackEntity->getImage()) {
				 $image_array=$rackEntity->getImage();
				 $images=explode( ";", $image_array);
			echo '<td class=primary>'.$rackEntity->getInstanceName().' (<acronym title="Component ID">'.$rackEntity->getID().'</acronym>)';
			  if ($rackEntity->getLogical_desc() != $rackEntity->getInstanceName()) {
			  echo '&nbsp;&nbsp;'.$rackEntity->getLogical_desc();
			  }
			'&nbsp;&nbsp;<a class="imagepopup" href="'.$images[0].'" target="_blank"><img src="../common/images/bullet_picture.png" class="middle" width="16" height="16" /><span><img src="'.$images[0].'">
			<br />Front</span></a>
			&nbsp;<a class="imagepopup" href="'.$images[1].'" target="_blank"><img src="../common/images/bullet_picture.png" class="middle" width="16" height="16" /><span><img src="'.$images[1].'"><br />            Back</span></a></td>';
			 } else {
				 echo '<td class=primary>'.$rackEntity->getInstanceName().' (<acronym title="Component ID">'.$rackEntity->getID().'</acronym>)';
				 if ($rackEntity->getLogical_desc() != $rackEntity->getInstanceName()) {
			  echo '&nbsp;&nbsp;'.$rackEntity->getLogical_desc().'</td>';
			  }
			 }
			
			echo '<td class=resulttext>'.$rackEntity->getrackType().'</td>';
			echo '<td class=resulttext>'.$rackEntity->getGroup().'</td>';
			echo '<td class=center><a class="hyper" href="action_rack_contents_search.php?rackName='.$rackEntity->getID().'&rack='.$rackEntity->getInstanceName().'&rackType='.$rackEntity->getrackType().'">'.Contents.'</a></td>';
			echo '<td class=center>'.$rackEntity->getDescription().'</td>';
			echo '<td class=center>'.$rackEntity->getParentRoom().'</td>';
			echo '</tr>';
		}
    }

   echo '</table><table width="100%"  border="1" cellspacing="0" cellpadding="2"><tr>';
   echo '<a name="report"></a>';
   include_once('../report/report_startform.php');
   include_once('../report/report_submit_rack.php');
   echo '</tr>';

?>
</table>