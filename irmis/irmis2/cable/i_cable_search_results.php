<div class="searchResults">
  <table width="100%"  border="1" cellspacing="0" cellpadding="2">
<?php
echo '<a name="top"></a>';

    // null list in session implies that result size was exceeded
	$CableList = $_SESSION['CableList'];
	    echo '<tr><th colspan="9"class=value>'.$_SESSION['iName'].'&nbsp;&nbsp;'.$_SESSION['ctName'].' (<acronym title="Component ID">'.$_SESSION['ID'].'</acronym>)<br> Room '.$_SESSION['ctRoom'].' &nbsp;&brvbar;&nbsp; '
		.$_SESSION['ctRack'].' &nbsp;&brvbar;&nbsp; '.$_SESSION['ctioc'].'';
	if ($CableList->length() != 0)
		{
		  echo '<font class="valueright">&nbsp;&nbsp;&nbsp;&nbsp;<a class="hyper" href="#report">Report Tools</a></th>';
		}
		echo '</tr>';
	    echo '<tr>';
        echo '<th nowrap class=value colspan=3>Origin Component Info</th>';
		echo '<th class=cable colspan="3">Cable Info</th>';
		echo '<th colspan="3">Destination Component Info</th></tr>';
		echo '<tr>';
		echo '<th class=value>Port Name<br>(Component ID)</th>';
		echo '<th class=value>Port Type</th>';
		echo '<th class=value>Pins</th>';
		echo '<th class=cable>Label</th>';
		echo '<th class=cable>Color</th>';
		echo '<th class=cable>Comments</th>';
		echo '<th>Component<br>(Component ID)</th>';
		echo '<th>Port Name</th>';
		echo '<th>Location</th>';
        echo '</tr>';
    if ($CableList == null) {
        echo '<tr><td class="warning bold" colspan=9>Search produced too many results to display.<br>';
        echo 'Limit is 5000. Try using the Additional<br>';
        echo 'Search Terms to constrain your search.</td></tr>';

    } else if ($CableList->length() == 0) {
        echo '<tr><td class="warning bold" colspan=9>No Port\'s Defined For This Component: Please Try Another Search.</td></tr>';

    } else {
       
        $cableEntities = $CableList->getArray();
        foreach ($cableEntities as $cableEntity) {
			
			$cableInfo=array_values($cableEntity->getcableInfo());
			$oppositePort=array_values($cableEntity->getOppPort());
			
            echo '<tr>';
			//PORT NAME
			if ($oppositePort[2]) { // if there's an opposite port connected, highlight this cell
			    echo '<td class=resulttextsmallcontrast nowrap><strong>'.$cableEntity->getComponentPortName().'</strong>&nbsp;(<acronym title="Component ID">'.$cableEntity->getComponentPortID().'</acronym>)</td>';
			        } else {
				    echo '<td class=resulttextsmall nowrap><strong>'.$cableEntity->getComponentPortName().'</strong>&nbsp;(<acronym title="Component ID">'.$cableEntity->getComponentPortID().'</acronym>)</td>';
			}
			
			//PORT TYPE
			if ($oppositePort[2]) { // if there's an opposite port connected, highlight this cell
			    echo '<td class=resulttextsmallcontrast><strong>'.$cableEntity->getComponentPortType().'</strong></td>';
			        } else {
				    echo '<td class=resulttextsmall>'.$cableEntity->getComponentPortType().'</td>';
			}
			
			//PINS
			if ($oppositePort[2]) { // if there's an opposite port connected, highlight this cell
			    echo '<td class=resulttextsmallcontrast><b>'.$cableEntity->getComponentPortPinCount().'</b></td>';
			        } else {
				    echo '<td class=resulttextsmall>'.$cableEntity->getComponentPortPinCount().'</td>';
			}
			
			//LABEL
			if ($oppositePort[2] && $cableInfo[0]) {
			  echo '<td class=cableboldsmallcenter>'.$cableInfo[0].'</td>'; 
			  } elseif  ($oppositePort[2] && !$cableInfo[0]) {
				  echo '<td class=cableboldsmallcenter>-</td>';
			  } else {
				  echo '<td class=center>-</td>';
			  }
			//COLOR
			if ($oppositePort[2] && $cableInfo[1]) {
			  echo '<td class=cableboldsmallcenter>'.$cableInfo[1].'</td>'; 
				} elseif ($oppositePort[2] && !$cableInfo[1]) {
					echo '<td class=cableboldsmallcenter>-</td>'; 
			  } else {
				  echo '<td class=center>-</td>';
			  }
			//COMMENTS
			if ($oppositePort[2] && $cableInfo[2]) {
			  echo '<td class=cableboldsmallcenter>'.$cableInfo[2].'</td>';
			} elseif ($oppositePort[2] && !$cableInfo[2]) {
				echo '<td class=cableboldsmallcenter>-</td>';
			  } else {
				  echo '<td class=center>-</td>';
			}
			
			//OPPOSITE LOCATION
			 //if ($oppositePort[2]) { 
			  //echo '<td class=resulttextsmallcontrast nowrap><strong>'.$cableEntity->getcomParentRoom().'&nbsp;<span class="divider">|</span>&nbsp;'.$cableEntity->getcomParentRack().'&nbsp;<span class="divider">|</span>&nbsp;<a class="hyper" href="../ioc/action_ioc_search.php?iocName='.$cableEntity->getcomParentIOC().'"><acronym title="General information about '
			 // .$cableEntity->getcomParentIOC().'">'.$cableEntity->getcomParentIOC().'</strong></td>';
			// } else { 
			//echo '<td class=center>-</td>';
			// }
			
			//OPPOSITE PORT NAME
			//if ($oppositePort[2]) {
			//  echo '<td class=resulttextsmallcontrast><strong>'.$oppositePort[3].'</strong></td>'; 
			//  } else {
			//	  echo '<td class=center>-</td>';
			//  }
			  
			//OPPOSITE COMPONENT
			if ($oppositePort[2]) {
			  echo '<td class=resulttextsmallcontrast nowrap><a class="hyper" href="../components/action_comp_search.php?ctID='.$oppositePort[4].'&jmp=1"><strong><acronym title="More information about a '.$oppositePort[0].'">'.$oppositePort[0].'</strong></a>&nbsp;-&nbsp;'.$oppositePort[5].'&nbsp;&nbsp;(<a class="hyper" href="../cable/action_ports_search.php?ID='.$oppositePort[2].'&ct='.$oppositePort[0].'&room='.$cableEntity->getcomParentRoom().'&rack='.$cableEntity->getcomParentRack().'&ioc='.$cableEntity->getcomParentioc().'"><acronym title="Make this '.$oppositePort[0].' the Origin Component">'.$oppositePort[2].'</acronym></a>)</td>';
			  } else {
				  echo '<td class=center>-</td>';
			  }
			  
			  
			//OPPOSITE PORT NAME
			if ($oppositePort[2]) {
			  echo '<td class=resulttextsmallcontrast><strong>'.$oppositePort[3].'</strong></td>'; 
			  } else {
				  echo '<td class=center>-</td>';
			  }
			  
			  
			  
			//OPPOSITE LOCATION
			 if ($oppositePort[2]) { 
			  echo '<td class=resulttextsmallcontrast nowrap><strong>'.$cableEntity->getcomParentRoom().'&nbsp;<span class="divider">|</span>&nbsp;'.$cableEntity->getcomParentRack().'&nbsp;<span class="divider">|</span>&nbsp;<a class="hyper" href="../ioc/action_ioc_search.php?iocName='.$cableEntity->getcomParentIOC().'"><acronym title="General information about '
			  .$cableEntity->getcomParentIOC().'">'.$cableEntity->getcomParentIOC().'</strong></td></tr>';
			 } else { 
			echo '<td class=center>-</td></tr>';
			 }
			  
			  
		}
            
			
      echo '</table><table width="100%"  border="1" cellspacing="0" cellpadding="2">
      <tr><a name="report"></a>';

		include('../report/report_startform.php');
		include('../report/report_submit_cable.php');

      echo '</tr>';


    }

?>
</table>