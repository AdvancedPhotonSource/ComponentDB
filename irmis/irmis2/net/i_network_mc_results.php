<div class="NetResultsLeft">
  <table width="100%"  border="1" cellspacing="0" cellpadding="2">
<?php 
    // null list in session implies that result size was exceeded
	$mcList = $_SESSION['mcList'];
	    echo '<tr><td colspan="3"class=value>"'.$mcList->length().'" Media Converters\'s Found</td></tr>';
	    echo '<tr>';
        echo '<th nowrap>Media Converter</th>';
	    echo '<th>Location</th>';
	    echo '<th>Ports</th>';
        echo '</tr>';
    if ($mcList == null) {
        echo '<tr><td class="warning bold" colspan=3>Search produced too many results to display.<br>';
        echo 'Limit is 5000. Try using the Additional<br>';
        echo 'Search Terms to constrain your search.</td></tr>';      
         
    } else if ($mcList->length() == 0) {
        echo '<tr><td class="warning bold" colspan=3>No Media Converter\'s found: please try another search.</td></tr>';
        
    } else {
	        
        $mcEntities = $mcList->getArray();
        foreach ($mcEntities as $mcEntity) {
			
			echo '<td class="primary">'.$mcEntity->getmc().'</td>';
			echo '<td class="resulttext">'.$mcEntity->getmcLocation().'</td>';
            echo '<td class="resulttext"><a class="hyper" href="action_net_search.php?mcID='.$mcEntity->getmc().'">Ports</td></tr>';
    }
}
?>
</table>
</div>