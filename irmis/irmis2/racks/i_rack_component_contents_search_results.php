<div class="searchResults">
  <table width="100%"  border="1" cellspacing="0" cellpadding="2">
<?php 
	  $RackCCList = $_SESSION['rackCCList'];
	    echo '<tr>';
	    echo '<th colspan="7" class="value">Component Contents</th>';
	    echo '</tr>';
	    echo '<tr><td colspan="7"class=value>"'.$rackCCList->length().'" Component\'s found in the '.$compName.' (<acronym title="Component ID">'.$_SESSION['compID'].'</acronym>), Rack '.$rackName.', Room '.$_SESSION['roomConstraint'].'</td></tr>';
	    echo '<tr>';
        echo '<th nowrap>Enclosure Name</th>';
	    echo '<th>Component Type</th>';
		echo '<th>Show Contents</th>';
		echo '<th>Component Name</th>';
		echo '<th>Locator</th>';
	    echo '<th>Description</th>';
		echo '<th>Manufacturer</th>';
        echo '</tr>';
    if ($RackCCList == null) {
        echo '<tr><td class="warning bold" colspan=7>Search produced too many results to display.<br>';
        echo 'Limit is 5000. Try using the Additional<br>';
        echo 'Search Terms to constrain your search.</td></tr>';      
         
    } else if ($RackCCList->length() == 0) {
        echo '<tr><td class="warning bold" colspan=7>No Component\'s found: please try another search.</td></tr>';
        
    } else {
	        
    $rackCCEntities = $RackCCList->getArray();
    foreach ($rackCCEntities as $rackCCEntity) {
     // Enclosure Name   
		echo '<tr>';   
		echo '<td class=primary>'.$_SESSION['compName'].'</td>';
	// Component Type
		echo '<td class=resulttext><a class="hyper" href="../components/action_comp_search.php?ctID='.$rackCCEntity->getcomponentTypeID().'">'.$rackCCEntity->getcomponentTypeName().'</a> (<acronym title="Component ID">'.$rackCCEntity->getID().'</acronym>)</td>';
	// Show Contents
		if ($rackCCEntity->getSum() > 0) {
		echo '<td class=center><a class="hyper" href="action_rack_component_contents_search.php?compID='.$rackCCEntity->getID().'&compName='.$rackCCEntity->getcomponentTypeName().'&rackName='.$rackName.'">Contents</a></td>';
		//logEntry('debug',"Search gave ".$rackContentsEntity->getSum()." children.");
	} else {
		echo '<td class=center>None</td>';
	}
			
	// if the getComponentInstanceName has the string "ioc" as it's contents,
	// display the full name as a link to the ioc page.
	// Component Name
	if (strstr($rackCCEntity->getComponentInstanceName(), "ioc")) {
		echo '<td class=resulttext><a class="hyper" href="../ioc/action_ioc_search.php?iocNameConstraint='.$rackCCEntity->getComponentInstanceName().'">'.$rackCCEntity->getComponentInstanceName().'</a></td>';
	} else if (strstr($rackCCEntity->getComponentInstanceName(), "plc")) {
		echo '<td class=resulttext><a class="hyper" href="../plc/action_plc_search.php?plcNameConstraint='.$rackCCEntity->getComponentInstanceName().'">'.$rackCCEntity->getComponentInstanceName().'</a></td>';
	} elseif (!$rackCCEntity->getComponentInstanceName()) {
	    echo '<td>&nbsp;</td>';
	} else {
		echo '<td class=resulttext>'.$rackCCEntity->getComponentInstanceName().'</td>';
	}
	// Locator
	if ($rackCCEntity->getLogicalDesc()>=0) {
	    echo '<td class=center>'.$rackCCEntity->getLogicalDesc().'</td>';
	} else {
	    echo '<td>&nbsp;</td>';
	}
    // Description
		echo '<td class=center>'.$rackCCEntity->getDescription().'</td>';
	// Manufacturer
		echo '<td class=center>'.$rackCCEntity->getManufacturer().'</td>';

		echo '</tr>';
	  }
    }
      
   		echo '</table><table width="100%"  border="1" cellspacing="0" cellpadding="2"><tr>';
   		echo '<a name="report"></a>';
   		include_once('../report/report_startform.php');
   		include_once('../report/report_submit_rack_component_contents.php');
   		echo '</tr>';

?>
</table>
</div>