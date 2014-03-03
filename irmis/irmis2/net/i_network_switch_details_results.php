<div class="NetResultsRight">
  <table width="100%" border="1" cellspacing="0" cellpadding="2">
<?php 
    // null list in session implies that result size was exceeded
	$swDetailsList = $_SESSION['swDetailsList'];
		echo '<tr><td colspan="5"class=value>"'.$swDetailsList->length().'" Ports for Switch "'.$_GET['swID'].'"</td></tr>';
		echo '<tr>';
        echo '<th nowrap>Blade</th>';
	    echo '<th>Port</th>';
	    //echo '<th>Media Conv.</th>';
		//echo '<th>Module</th>';
		echo '<th nowrap>IOC Location</th>';
		echo '<th>IOC</th>';
		echo '<th>Connection</th>';
        echo '</tr>';
    if ($swDetailsList == null) {
        echo '<tr><td class="warning bold" colspan=5>Search produced too many results to display.<br>';
        echo 'Limit is 5000. Try using the Additional<br>';
        echo 'Search Terms to constrain your search.</td></tr>';      
         
    } else if ($swDetailsList->length() == 0) {
        echo '<tr><td class="warning bold" colspan=5>No Switch Port\'s found: please try another search.</td></tr>';
        
    } else {
	    
        $swDetailsEntities = $swDetailsList->getArray();
        foreach ($swDetailsEntities as $swDetailsEntity) {
			
			if (strcasecmp($swDetailsEntity->getswPrimEnetSwitch(), $swID) == 0) {
			echo '<td class="primary">'.$swDetailsEntity->getswPrimEnetBlade().'</td>';
			echo '<td class="resulttext">'.$swDetailsEntity->getswPrimEnetPort().'</td>';
			echo '<td class="resulttext">'.$swDetailsEntity->getswLocation().'</td>';
			//if ($swDetailsEntity->getswPrimEnetMedConvCh()) {
            //  echo '<td class="resulttext">'.$swDetailsEntity->getswPrimEnetMedConvCh().'</td>';
			//} else {
			//  echo '<td>&nbsp;</td>';
			//}
			//echo '<td class="resulttext">'.$swDetailsEntity->getswPrimMediaConvPort().'</td>';
			echo '<td class="resulttext"><a class="hyper" href="../ioc/action_ioc_search.php?iocName='.$swDetailsEntity->getswConnection().'">'.$swDetailsEntity->getswConnection().'</a></td>';
			echo '<td class="pri">Primary</td></tr>';
			}
			if (strcasecmp($swDetailsEntity->getswSecEnetSwitch(), $swID) == 0) {
			echo '<td class="primary">'.$swDetailsEntity->getswSecEnetBlade().'</td>';
			echo '<td class="resulttext">'.$swDetailsEntity->getswSecEnetPort().'</td>';
            //echo '<td class="resulttext">'.$swDetailsEntity->getswSecEnetMedConvCh().'</td>';
			//echo '<td class="resulttext">'.$swDetailsEntity->getswSecMedConvPort().'</td>';
			echo '<td class="resulttext">'.$swDetailsEntity->getswLocation().'</td>';
			echo '<td class="resulttext"><a class="hyper" href="../ioc/action_ioc_search.php?iocName='.$swDetailsEntity->getswConnection().'">'.$swDetailsEntity->getswConnection().'</a></td>';
			echo '<td class="sec">Secondary</td></tr>';
			}
    }
}
?>
</table>
</div>
