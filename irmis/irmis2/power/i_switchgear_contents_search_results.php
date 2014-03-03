<div class="searchResults">
  <table width="100%"  border="1" cellspacing="0" cellpadding="2">
<?php 
	  $sgContentsList = $_SESSION['sgContentsList'];
	    echo '<tr>';
	    echo '<th colspan="8" class="value">AC Power Feed</th>';
	    echo '</tr>';
		if ($sgContentsList->length() > 1) {
	        echo '<tr><td colspan="8"class=value>"'.$sgContentsList->length().'"&nbsp;<i>AC Distribution Component\'s powered by:&nbsp;</i>'.$_SESSION['SG'].'&nbsp;&nbsp;'.$_SESSION['SGType'].',<i>&nbsp;&nbsp;&nbsp;&nbsp;Originating In Switch Gear:&nbsp;</i>'.$_SESSION['switchgearConstraint'].',&nbsp;&nbsp;&nbsp;&nbsp;<i>In:</i>&nbsp;'.$_SESSION['SGRoom'];
			echo '<font class="valueright">&nbsp;&nbsp;&nbsp;&nbsp;<a class="hyper" href="#report">Report Tools</a></td></tr>';
		} else {
			echo '<tr><td colspan="8"class=value>"'.$sgContentsList->length().'"&nbsp;<i>AC Distribution Component powered by:&nbsp;</i>'.$_SESSION['SG'].'&nbsp;&nbsp;'.$_SESSION['SGType'].',<i>&nbsp;&nbsp;&nbsp;&nbsp;Originating In Switch Gear:&nbsp;</i>'.$_SESSION['switchgearConstraint'].',&nbsp;&nbsp;&nbsp;&nbsp;<i>In:</i>&nbsp;'.$_SESSION['SGRoom'];
			echo '<font class="valueright">&nbsp;&nbsp;&nbsp;&nbsp;<a class="hyper" href="#report">Report Tools</a></td></tr>';
		}
	    echo '<tr>';
        echo '<th nowrap>This Power Component</th>';
	    echo '<th>Feeds Power To</th>';
		echo '<th>Description</th>';
		echo '<th>Manufacturer</th>';
		echo '<th>Rack</th>';
	    echo '<th>Room</th>';
		echo '<th>Building</th>';
		echo '<th>Show Power Feed</th>';
        echo '</tr>';
    if ($sgContentsList == null) {
        echo '<tr><td class="warning bold" colspan=8>Search produced too many results to display.<br>';
        echo 'Limit is 5000. Try using the Additional<br>';
        echo 'Search Terms to constrain your search.</td></tr>';      
         
    } else if ($sgContentsList->length() == 0) {
        echo '<tr><td class="warning bold" colspan=8>No Power Component\'s found: please try another search.</td></tr>';
        
    } else {
	        
			echo '<tr>';   
			echo '<td class=primary rowspan='.$sgContentsList->length().'>'.$_SESSION['SG'].'&nbsp;&nbsp;'.$_SESSION['SGType'].'&nbsp;&nbsp;(<acronym title="Component ID">'.$_SESSION['SGName'].'</acronym>)</td>';
			
     	$sgContentsEntities = $sgContentsList->getArray();
        foreach ($sgContentsEntities as $sgContentsEntity) {
			
		//Feeds Power To
			if ($sgContentsEntity->getLogicalDesc()&&$sgContentsEntity->getComponentInstanceName()) {
			  echo '<td class=resulttext><a class="hyper" href="../components/action_comp_search.php?ctID='.$sgContentsEntity->getComponentTypeID().'">'.$sgContentsEntity->getComponentTypeName().'</a>                             &nbsp;&nbsp;'.$sgContentsEntity->getLogicalDesc().'&nbsp;&nbsp;(<acronym title="Component ID">'.$sgContentsEntity->getID().'</acronym>)</td>';
			  
		      } elseif ($sgContentsEntity->getLogicalDesc()&&!$sgContentsEntity->getComponentInstanceName()) {
			  echo '<td class=resulttext><a class="hyper" href="../components/action_comp_search.php?ctID='.$sgContentsEntity->getComponentTypeID().'">'.$sgContentsEntity->getComponentTypeName().'</a>
			  &nbsp;&nbsp;'.$sgContentsEntity->getLogicalDesc().'&nbsp;&nbsp;(<acronym title="Component ID">'.$sgContentsEntity->getID().'</acronym>)</td>';
			  
			  } elseif ($sgContentsEntity->getComponentInstanceName()&&!$sgContentsEntity->getLogicalDesc()) {
			  echo '<td class=resulttext><a class="hyper" href="../components/action_comp_search.php?ctID='.$sgContentsEntity->getComponentTypeID().'">'.$sgContentsEntity->getComponentTypeName().'</a>&nbsp;&nbsp;'.               $sgContentsEntity->getComponentInstanceName().'&nbsp;&nbsp;(<acronym title="Component ID">'.$sgContentsEntity->getID().'</acronym>)</td>';
			  
			  } else if (!$sgContentsEntity->getComponentInstanceName()&&!$sgContentsEntity->getLogicalDesc()&&$sgContentsEntity->getComponentTypeName()) {
			  echo '<td class=resulttext><a class="hyper" href="../components/action_comp_search.php?ctID='.$sgContentsEntity->getComponentTypeID().'">'.$sgContentsEntity->getComponentTypeName().'</a>&nbsp;&nbsp;
			  (<acronym title="Component ID">'.$sgContentsEntity->getID().'</acronym>)</td>';
			}
			
		//Description
			if ($sgContentsEntity->getDescription()) {
			  if ($sgContentsEntity->getDescription()&&strstr($sgContentsEntity->getComponentInstanceName(), "ioc")) {
			    echo '<td class=center><a class="hyper" href="http://ctlappsirmis/irmis2/ioc/iocgeneral_search_results.php?iocName='.$sgContentsEntity->getComponentInstanceName().'">'
				.$sgContentsEntity->getComponentInstanceName().'</a>&nbsp;<span class=divider>-</span>&nbsp;'.$sgContentsEntity->getDescription().'</td>';
			  } else {
				  echo '<td class=center>'.$sgContentsEntity->getDescription().'</td>';
			  }
				
		}
			
		//Manufacturer
		    if ($sgContentsEntity->getManufacturer() != 'None') {
			  echo '<td class=resulttext>'.$sgContentsEntity->getManufacturer();
			} else {
			    echo '<td class=center>-</td>';
			  }
		
        //Rack
	   if ($sgContentsEntity->getParentRack()) {
		   echo '<td class=center><a class="hyper" href="http://ctlappsirmis/irmis2/racks/action_rack_contents_search.php?rackName='.$sgContentsEntity->getParentRackid().
		   '&rack='.$sgContentsEntity->getParentRack().'&rackType=Rack">'.$sgContentsEntity->getParentRack().'</a></td>';
	   } else {
		   echo '<td class=center>-</td>';
	   }
		
	    //Room
	   if ($sgContentsEntity->getParentRoom()) {
		   $_SESSION['roomConstraint'] = $sgContentsEntity->getParentRoom();
		   echo '<td class=center>'.$sgContentsEntity->getParentRoom().'</td>';
	   } else {
		   echo '<td class=center>-</td>';
	   }
		
	    //Buildging
	   if ($sgContentsEntity->getParentBldg()) {
		   echo '<td class=center>'.$sgContentsEntity->getParentBldg().'</td>';
	   } else {
		   echo '<td class=center>-</td>';
	   }
		
		//Show Power
		if ($sgContentsEntity->getSum() > 0) {
			  echo '<td class=center><a class="hyper" href="action_switchgear_contents_search.php?SGName='.$sgContentsEntity->getID().'&SG='.$sgContentsEntity->getComponentTypeName().'&SGType='.              $sgContentsEntity->getLogicalDesc().'">Power</a></td>';
			  //logEntry('debug',"Search gave ".$rackContentsEntity->getSum()." children.");
			} else {
			  echo '<td class=center>None</td>';
			}
		
		//circuit path (2/22/2013)
		
		   /* if ($sgContentsEntity) {
				if ($sgContentsEntity->getAC12()) {
				echo 	'<td><span class="divider"> <-- </span>'.$sgContentsEntity->getAC12();
				}else{
					echo '<td>&nbsp;';
				}
				if ($sgContentsEntity->getAC11()) {
				echo 	'<span class="divider"> <-- </span>'.$sgContentsEntity->getAC11();
				}else{
					echo '<span>&nbsp;</span>';
				}
				if ($sgContentsEntity->getAC10()) {
				echo 	'<span class="divider"> <-- </span>'.$sgContentsEntity->getAC10();
				}else{
					echo '<span>&nbsp;</span>';
				}
				if ($sgContentsEntity->getAC9()) {
				echo 	'<span class="divider"> <-- </span>'.$sgContentsEntity->getAC9();
				}else{
					echo '<span>&nbsp;</span>';
				}
				if ($sgContentsEntity->getAC8()) {
				echo 	'<span class="divider"> <-- </span>'.$sgContentsEntity->getAC8();
				}else{
					echo '<span>&nbsp;</span>';
				}
				if ($sgContentsEntity->getAC7()) {
				echo 	'<span class="divider"> <-- </span>'.$sgContentsEntity->getAC7();
				}else{
					echo '<span>&nbsp;</span>';
				}
				if ($sgContentsEntity->getAC6()) {
				echo 	'<span class="divider"> <-- </span>'.$sgContentsEntity->getAC6();
				}else{
					echo '<span>&nbsp;</span>';
				}
				if ($sgContentsEntity->getAC5()) {
				echo 	'<span class="divider"> <-- </span>'.$sgContentsEntity->getAC5();
				}else{
					echo '<span>&nbsp;</span>';
				}
				if ($sgContentsEntity->getAC4()) {
				echo 	'<span class="divider"> <-- </span>'.$sgContentsEntity->getAC4();
				}else{
					echo '<span>&nbsp;</span>';
				}
				if ($sgContentsEntity->getAC3()) {
				echo 	'<span class="divider"> <-- </span>'.$sgContentsEntity->getAC3();
				}else{
					echo '<span>&nbsp;</span>';
				}
				if ($sgContentsEntity->getAC2()) {
				echo 	'<span class="divider"> <-- </span>'.$sgContentsEntity->getAC2();
				}else{
					echo '<span>&nbsp;</span>';
				}
				if ($sgContentsEntity->getAC1()) {
				echo 	'<span class="divider"> <-- </span>'.$sgContentsEntity->getAC1().'</td>';
				}else{
					echo '<span>&nbsp;</span></td>';
				}
	
			} else {
				echo '<td class=center>-</td>';
			} */
			echo '</td>';			
			
			echo '</tr>';
		}
    }
      //logEntry('debug',$_SESSION['roomConstraint']);
    
   echo '</table><table width="100%"  border="1" cellspacing="0" cellpadding="2"><tr>';
   echo '<A name="report"></A>';
   include_once('../report/report_startform.php');
   include_once('../report/report_submit_SGContents.php');
   echo '</tr>';

?>
</table>
</div>