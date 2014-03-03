<div class="NetResultsLeft">
  <table width="100%"  border="1" cellspacing="0" cellpadding="2">
<?php 
    // null list in session implies that result size was exceeded
	$tsList = $_SESSION['tsList'];
	    echo '<tr><td colspan="3"class=value>"'.$tsList->length().'" Terminal Server\'s Found</td></tr>';
	    echo '<tr>';
        echo '<th nowrap>Terminal Server</th>';
	    echo '<th>Terminal Server Location</th>';
	    echo '<th>Ports</th>';
        echo '</tr>';
    if ($tsList == null) {
        echo '<tr><td class="warning bold" colspan=8>Search produced too many results to display.<br>';
        echo 'Limit is 5000. Try using the Additional<br>';
        echo 'Search Terms to constrain your search.</td></tr>';      
         
    } else if ($tsList->length() == 0) {
        echo '<tr><td class="warning bold" colspan=8>No Terminal Server\'s found: please try another search.</td></tr>';
        
    } else {
	        
        $tsEntities = &$tsList->getArray();
        foreach ($tsEntities as $tsEntity) {
			
			echo '<td class="primary">'.$tsEntity->getts().'</td>';
			echo '<td class="resulttext">'.$tsEntity->gettsLocation().'</td>';
            echo '<td class="resulttext"><a class="hyper" href="action_net_search.php?tsID='.$tsEntity->getts().'">Ports</td></tr>';
    }
}
?>
</table>
</div>
