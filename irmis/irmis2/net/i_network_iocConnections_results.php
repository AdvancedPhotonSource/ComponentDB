<div class="searchResults">
  <table width="100%"  border="1" cellspacing="0" cellpadding="2">
<?php 
    // null list in session implies that result size was exceeded
	$iocConnectionsList = $_SESSION['iocConnectionsList'];
		echo '<tr><td colspan="6"class=value>"'.$iocConnectionsList->length().'" IOC\'s</td></tr>';
		echo '<tr>';
        echo '<th nowrap>IOC Name</th>';
	    echo '<th>Console Connection<br>Terminal Server Name - Port</th>';
	    echo '<th>Primary Ethernet<br>Switch - Blade - Port</th>';
		echo '<th>Secondary Ethernet<br>Switch - Blade - Port</th>';
        echo '</tr>';
    if ($iocConnectionsList == null) {
        echo '<tr><td class="warning bold" colspan=6>Search produced too many results to display.<br>';
        echo 'Limit is 5000. Try using the Additional<br>';
        echo 'Search Terms to constrain your search.</td></tr>';      
         
    } else if ($iocConnectionsList->length() == 0) {
        echo '<tr><td class="warning bold" colspan=4>No IOC\'s found: please try another search.</td></tr>';
        
    } else {
	    
        $iocConnectionsEntities = $iocConnectionsList->getArray();
        foreach ($iocConnectionsEntities as $iocConnectionsEntity) {
            echo '<tr>';   
			if ($iocConnectionsEntity->getiocConnStatus() == 2){
            echo '<td class="valuedim">'.$iocConnectionsEntity->getiocConniocName().'</td>';
			}
			else {
            echo '<td class="primary">'.$iocConnectionsEntity->getiocConniocName().'</td>';
			}
			echo '<td class="resulttext">'.$iocConnectionsEntity->getiocConnTermServName().' - '.$iocConnectionsEntity->getiocConnTermServPort().'</td>';
            echo '<td class="resulttext">'.$iocConnectionsEntity->getiocConnPrimEnetSwitch().' - '.$iocConnectionsEntity->getiocConnPrimEnetBlade().' - '.$iocConnectionsEntity->getiocConnPrimEnetPort().'</td>';
			echo '<td class="resulttext">'.$iocConnectionsEntity->getiocConnSecEnetSwitch().' - '.$iocConnectionsEntity->getiocConnSecEnetBlade().' - '.$iocConnectionsEntity->getiocConnSecEnetPort().'</td>';
			echo '</tr>';

    }
}
?>
</table>

