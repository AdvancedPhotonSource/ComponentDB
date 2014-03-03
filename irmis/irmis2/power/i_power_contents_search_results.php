<div class="searchResults">
  <table width="100%"  border="1" cellspacing="0" cellpadding="2">
<?php 
	  $RackContentsList = $_SESSION['rackContentsList'];
	    echo '<tr>';
	    echo '<th colspan="4" class="value">AC Power Feed</th>';
	    echo '</tr>';
		if ($rackContentsList->length() > 1) {
	      echo '<tr><td colspan="4"class=value>"'.$rackContentsList->length().'"<i> AC Distribution Component\'s found in:</i> '.$_SESSION['rackType'].'&nbsp;'.$_SESSION['rack'].'<i>&nbsp;&nbsp;In:</i>&nbsp;Room&nbsp;'.$_SESSION['roomConstraint'].'</td></tr>';
		} else {
			echo '<tr><td colspan="4"class=value>"'.$rackContentsList->length().'"<i> AC Distribution Component found in:</i> '.$_SESSION['rackType'].'&nbsp;'.$_SESSION['rack'].'<i>&nbsp;&nbsp;In:</i>&nbsp;Room&nbsp;'.$_SESSION['roomConstraint'].'</td></tr>';
		}
	    echo '<tr>';
        echo '<th nowrap>Enclosure<br> Name</th>';
	    echo '<th>Name / Location</th>';
		echo '<th>Enclosure Power / Manufacturer</th>';
		echo '<th>AC Power Path From Rack To Switch Gear</th>';
        echo '</tr>';
    if ($RackContentsList == null) {
        echo '<tr><td class="warning bold" colspan=4>Search produced too many results to display.<br>';
        echo 'Limit is 5000. Try using the Additional<br>';
        echo 'Search Terms to constrain your search.</td></tr>';      
         
    } else if ($RackContentsList->length() == 0) {
        echo '<tr><td class="warning bold" colspan=4>No Power Component\'s found: please try another search.</td></tr>';
        
    } else {
	        
     	$rackContentsEntities = $RackContentsList->getArray();
        foreach ($rackContentsEntities as $rackContentsEntity) {
						
            echo '<tr>';   
			echo '<td class=primary>'.$_SESSION['rack'].'</td>';
			
					
		//Name / Location
			if ($rackContentsEntity->getLogicalDesc()&&$rackContentsEntity->getComponentInstanceName()) {
			  echo '<td class=center>'.$rackContentsEntity->getLogicalDesc().'&nbsp;/&nbsp;'.$rackContentsEntity->getComponentInstanceName().'</td>';
		      } elseif ($rackContentsEntity->getLogicalDesc()&&!$rackContentsEntity->getComponentInstanceName()) {
			  echo '<td class=center>-&nbsp;/&nbsp;'.$rackContentsEntity->getLogicalDesc().'</td>';
			  } elseif ($rackContentsEntity->getComponentInstanceName()&&!$rackContentsEntity->getLogicalDesc()) {
			  echo '<td class=center>'.$rackContentsEntity->getComponentInstanceName().'&nbsp;/&nbsp;-</td>';
			  } else {
			  echo '<td class=center>-&nbsp;/&nbsp;-</td>';
			}
			
			
		//Enclosure Power / Manufacturer
			if ($rackContentsEntity->getManufacturer() != 'None') {
			  echo '<td class=resulttext><a class="hyper" href="../components/action_comp_search.php?ctID='.$rackContentsEntity->getcomponentTypeID().'">'.$rackContentsEntity->getcomponentTypeName().'</a>                   &nbsp;/&nbsp;'.$rackContentsEntity->getManufacturer().'&nbsp;&nbsp;(<acronym title="Component ID">'.$rackContentsEntity->getID().'</acronym>)</td>';
			} else {
			    echo '<td class=resulttext><a class="hyper" href="../components/action_comp_search.php?ctID='.$rackContentsEntity->getcomponentTypeID().'">'.$rackContentsEntity->getcomponentTypeName().'</a>                     &nbsp;&nbsp;(<acronym title="Component ID">'.$rackContentsEntity->getID().'</acronym>)</td>';
			  }
		
			
		//circuit path (2/22/2013)
		
		    if ($rackContentsEntity) {
				if ($rackContentsEntity->getAC24()) {
				echo 	'<td><span class="divider"> <-- </span>'.$rackContentsEntity->getAC24().'<b>&nbsp;'.$rackContentsEntity->getAC23().'</b>';
				}else{
					echo '<td>&nbsp;';
				}
				if ($rackContentsEntity->getAC22()) {
				echo 	'<span class="divider"> <-- </span>'.$rackContentsEntity->getAC22().'<b>&nbsp;'.$rackContentsEntity->getAC21().'</b>';
				}else{
					echo '<span>&nbsp;</span>';
				}
				if ($rackContentsEntity->getAC20()) {
				echo 	'<span class="divider"> <-- </span>'.$rackContentsEntity->getAC20().'<b>&nbsp;'.$rackContentsEntity->getAC19().'</b>';
				}else{
					echo '<span>&nbsp;</span>';
				}
				if ($rackContentsEntity->getAC18()) {
				echo 	'<span class="divider"> <-- </span>'.$rackContentsEntity->getAC18().'<b>&nbsp;'.$rackContentsEntity->getAC17().'</b>';
				}else{
					echo '<span>&nbsp;</span>';
				}
				if ($rackContentsEntity->getAC16()) {
				echo 	'<span class="divider"> <-- </span>'.$rackContentsEntity->getAC16().'<b>&nbsp;'.$rackContentsEntity->getAC15().'</b>';
				}else{
					echo '<span>&nbsp;</span>';
				}
				if ($rackContentsEntity->getAC14()) {
				echo 	'<span class="divider"> <-- </span>'.$rackContentsEntity->getAC14().'<b>&nbsp;'.$rackContentsEntity->getAC13().'</b>';
				}else{
					echo '<span>&nbsp;</span>';
				}
				if ($rackContentsEntity->getAC12()) {
				echo 	'<span class="divider"> <-- </span>'.$rackContentsEntity->getAC12().'<b>&nbsp;'.$rackContentsEntity->getAC11().'</b>';
				}else{
					echo '<span>&nbsp;</span>';
				}
				if ($rackContentsEntity->getAC10()) {
				echo 	'<span class="divider"> <-- </span>'.$rackContentsEntity->getAC10().'<b>&nbsp;'.$rackContentsEntity->getAC9().'</b>';
				}else{
					echo '<span>&nbsp;</span>';
				}
				if ($rackContentsEntity->getAC8()) {
				echo 	'<span class="divider"> <-- </span>'.$rackContentsEntity->getAC8().'<b>&nbsp;'.$rackContentsEntity->getAC7().'</b>';
				}else{
					echo '<span>&nbsp;</span>';
				}
				if ($rackContentsEntity->getAC6()) {
				echo 	'<span class="divider"> <-- </span>'.$rackContentsEntity->getAC6().'<b>&nbsp;'.$rackContentsEntity->getAC5().'</b>';
				}else{
					echo '<span>&nbsp;</span>';
				}
				if ($rackContentsEntity->getAC4()) {
				echo 	'<span class="divider"> <-- </span>'.$rackContentsEntity->getAC4().'<b>&nbsp;'.$rackContentsEntity->getAC3().'</b>'; //switch gear breaker
				}else{
					echo '<span>&nbsp;</span>';
				}
				if ($rackContentsEntity->getAC2()) {
				echo 	'<span class="divider"> <-- </span>'.$rackContentsEntity->getAC2().'<b>&nbsp;'.$rackContentsEntity->getAC1().'</td>'; //switch gear
				}else{
					echo '<span>&nbsp;</span></td>';
				}
	
			} else {
				echo '<td class=center>-</td>';
			}
			echo '</td>';			
			
			echo '</tr>';
		}
    }
      //logEntry('debug',$_SESSION['roomConstraint']);
    
   echo '</table><table width="100%"  border="1" cellspacing="0" cellpadding="2"><tr>';
   echo '<A name="report"></A>';
   include_once('../report/report_startform.php');
   include_once('../report/report_submit_power.php');
   echo '</tr>';

?>
</table>
</div>