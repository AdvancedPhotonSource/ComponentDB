<?php

/*
 * Written by Dawn Clemons
 * Specialized step in creating a printable report. Specific information
 * that applies only to the Component Types (Instances) PHP viewer is displayed in a look
 * and feel similar to the viewer's. This output is buffered.
 */

   echo '<div class="sectionTable">
  <table width="100%"  border="1" cellspacing="0" cellpadding="2">';

   // null list in session implies that result size was exceeded
	
	$CableList = $_SESSION['CableList'];

   //Display column headers
	    echo '<tr><th colspan="9"class=value>'.$_SESSION['ctName'].' ('.$_SESSION['ID'].') Ports Found: '.$CableList->length().'  <br> Room '.$_SESSION['ctRoom'].' &nbsp;&brvbar;&nbsp; '
		.$_SESSION['ctRack'].' &nbsp;&brvbar;&nbsp; '.$_SESSION['ctioc'].'';
	if ($CableList->length() != 0)
		{
		  echo '<font class="valueright">&nbsp;&nbsp;&nbsp;&nbsp;<A href="#report">Report Tools</A></th>';
		}
		echo '</tr>';
	    echo '<tr>';
        echo '<th nowrap class=value colspan=3>Origin Component Info</th>';
		echo '<th class=cable colspan="3">Cable Info</th>';
		echo '<th colspan="3">Destination Component Info</th></tr>';
		echo '<tr>';
		echo '<th class=value>Port Name<br>(Port ID)</th>';
		echo '<th class=value>Port Type</th>';
		echo '<th class=value>Pins</th>';
		echo '<th class=cable>Label</th>';
		echo '<th class=cable>Color</th>';
		echo '<th class=cable>Comments</th>';
		echo '<th>Location</th>';
		echo '<th>Port Name</th>';
		echo '<th>Component<br>(Comp ID)</th>';
        echo '</tr>';
    if ($CableList == null) {
        echo '<tr><td class="warning bold" colspan=9>Search produced too many results to display.<br>';
        echo 'Limit is 5000. Try using the Additional<br>';
        echo 'Search Terms to constrain your search.</td></tr>';

    } else if ($CableList->length() == 0) {
        echo '<tr><td class="warning bold" colspan=9>No Cable\'s found: please try another search.</td></tr>';

    } else {
	     $cableEntities = $CableList->getArray();
          foreach ($cableEntities as $cableEntity) {
			
		   $cableInfo=array_values($cableEntity->getcableInfo());
		   $oppositePort=array_values($cableEntity->getOppPort());

            echo '<tr>';
			//PORT NAME
			if ($oppositePort[2]) {
			    echo '<td class=resulttextsmallcontrast nowrap><b>'.$cableEntity->getComponentPortName().'</b>&nbsp;&nbsp;('.$cableEntity->getComponentPortID().')</td>';
			        } else {
				    echo '<td class=resulttextsmall nowrap><b>'.$cableEntity->getComponentPortName().'</b>&nbsp;&nbsp;('.$cableEntity->getComponentPortID().')</td>';
			}
			
			//PORT TYPE
			if ($oppositePort[2]) {
			    echo '<td class=resulttextsmallcontrast><b>'.$cableEntity->getComponentPortType().'</b></td>';
			        } else {
				    echo '<td class=resulttextsmall>'.$cableEntity->getComponentPortType().'</td>';
			}
			
			//PINS
			if ($oppositePort[2]) {
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
			 if ($oppositePort[2]) { 
			  echo '<td class=resulttextsmallcontrast nowrap><b>'.$cableEntity->getcomParentRoom().'&nbsp;&brvbar;&nbsp;'.$cableEntity->getcomParentRack().'&nbsp;&brvbar;&nbsp;
			  <a href="../ioc/action_ioc_search.php?iocName='.$cableEntity->getcomParentIOC().'"><acronym title="General information about '
			  .$cableEntity->getcomParentIOC().'">'.$cableEntity->getcomParentIOC().'</b></td>';
			 } else { 
			echo '<td class=center>-</td>';
			 }
			
			//OPPOSITE PORT NAME
			if ($oppositePort[2]) {
			  echo '<td class=resulttextsmallcontrast nowrap><b>'.$oppositePort[3].'</b></td>'; 
			  } else {
				  echo '<td class=center>-</td>';
			  }
			  
			//OPPOSITE COMPONENT
			if ($oppositePort[2]) {
			  echo '<td class=resulttextsmallcontrast><a href="../components/action_comp_search.php?ctID='.$oppositePort[4].'&jmp=1"><b>'.$oppositePort[0].'</b></a>&nbsp;&nbsp;('.$oppositePort[2].')</td></tr>';
			  } else {
				  echo '<td class=center>-</td></tr>';
			  }
			  
		}
	  }
echo '</table></div>';
echo '<br>';
?>