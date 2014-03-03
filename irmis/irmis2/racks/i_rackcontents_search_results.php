<div class="searchResults">
  <table width="100%"  border="1" cellspacing="0" cellpadding="2">
<?php 
	  $RackContentsList = $_SESSION['rackContentsList'];
	    echo '<tr>';
	    echo '<th colspan="7" class="value">Rack/Enclosure Contents</th>';
	    echo '</tr>';
	    echo '<tr><td colspan="7"class=value>"'.$rackContentsList->length().'" Component\'s found in '.$_SESSION['rackType'].' '.$_SESSION['rack'].', Room '.$_SESSION['roomConstraint'].'</td></tr>';
	    echo '<tr>';
        echo '<th nowrap>Enclosure Name</th>';
	    echo '<th>Component Type</th>';
		echo '<th>Show Contents</th>';
		echo '<th>Component Name</th>';
		echo '<th>Locator</th>';
	    echo '<th>Description</th>';
		echo '<th>Manufacturer</th>';
        echo '</tr>';
    if ($RackContentsList == null) {
        echo '<tr><td class="warning bold" colspan=7>Search produced too many results to display.<br>';
        echo 'Limit is 5000. Try using the Additional<br>';
        echo 'Search Terms to constrain your search.</td></tr>';      
         
    } else if ($RackContentsList->length() == 0) {
        echo '<tr><td class="warning bold" colspan=7>No Component\'s found: please try another search.</td></tr>';
        
    } else {
	        
     	$rackContentsEntities = $RackContentsList->getArray();
        foreach ($rackContentsEntities as $rackContentsEntity) {
            echo '<tr>';   
			echo '<td class=primary>'.$_SESSION['rack'].'</td>';
			echo '<td class=resulttext><a class="hyper" href="../components/action_comp_search.php?ctID='.$rackContentsEntity->getcomponentTypeID().'">'.$rackContentsEntity->getcomponentTypeName().'</a> (<acronym title="Component ID">'.$rackContentsEntity->getID().'</acronym>)</td>';
			
			if ($rackContentsEntity->getSum() > 0) {
			  echo '<td class=center><a class="hyper" href="action_rack_component_contents_search.php?compID='.$rackContentsEntity->getID().'&compName='.$rackContentsEntity->getcomponentTypeName().'&rackName='.$_SESSION['rack'].'">Contents</a></td>';
			  //logEntry('debug',"Search gave ".$rackContentsEntity->getSum()." children.");
			} else {
			  echo '<td class=center>None</td>';
			}

			
			if (strstr($rackContentsEntity->getComponentInstanceName(), "plc")) {
			  echo '<td class=resulttext><a class="hyper" href="../plc/action_plc_search.php?plcNameConstraint='.$rackContentsEntity->getComponentInstanceName().'">'.$rackContentsEntity->getComponentInstanceName().'</a></td>';
			} elseif (!$rackContentsEntity->getComponentInstanceName()){
			  echo '<td>&nbsp;</td>';
			} else {
			  echo '<td class=resulttext>'.$rackContentsEntity->getComponentInstanceName().'</td>';
			}
			
			if ($rackContentsEntity->getLogicalDesc()) {
			  echo '<td class=center>'.$rackContentsEntity->getLogicalDesc().'</td>';
			} else {
			  echo '<td>&nbsp;</td>';
			}
			
			echo '<td class=center>'.$rackContentsEntity->getDescription().'</td>';
			echo '<td class=center>'.$rackContentsEntity->getManufacturer().'</td>';
			
			echo '</tr>';
		}
    }
      
    
   echo '</table><table width="100%"  border="1" cellspacing="0" cellpadding="2"><tr>';
   echo '<a name="report"></a>';
   include_once('../report/report_startform.php');
   include_once('../report/report_submit_rack_contents.php');
   echo '</tr>';

?>
</table>
</div>