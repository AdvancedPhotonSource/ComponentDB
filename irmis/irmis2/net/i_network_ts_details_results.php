<div class="NetResultsRight">
  <table width="100%"  border="1" cellspacing="0" cellpadding="2">
<?php 
    // null list in session implies that result size was exceeded
	$tsDetailsList = $_SESSION['tsDetailsList'];
		echo '<tr><td colspan="4"class=value>"'.$tsDetailsList->length().'" Ports for Terminal Server "'.$_GET['tsID'].'"</td></tr>';
		echo '<tr>';
        echo '<th nowrap>TS Port</th>';
	    echo '<th>Fiber Conv Chassis</th>';
	    echo '<th>Fiber Conv Port</th>';
		echo '<th>Connection</th>';
        echo '</tr>';
    if ($tsDetailsList == null) {
        echo '<tr><td class="warning bold" colspan=8>Search produced too many results to display.<br>';
        echo 'Limit is 5000. Try using the Additional<br>';
        echo 'Search Terms to constrain your search.</td></tr>';      
         
    } else if ($tsDetailsList->length() == 0) {
        echo '<tr><td class="warning bold" colspan=8>No Terminal Server Port\'s found: please try another search.</td></tr>';
        
    } else {
	        
        $tsDetailsEntities = $tsDetailsList->getArray();
        foreach ($tsDetailsEntities as $tsDetailsEntity) {
			
			echo '<td class="primary">'.$tsDetailsEntity->gettsPort().'</td>';
			echo '<td class="resulttext">'.$tsDetailsEntity->gettsFiberConvCh().'</td>';
            echo '<td class="resulttext">'.$tsDetailsEntity->gettsFiberConvPort().'</td>';
			echo '<td class="resulttext"><a class="hyper" href="../ioc/action_ioc_search.php?iocName='.$tsDetailsEntity->gettsConnection().'">'.$tsDetailsEntity->gettsConnection().'</a></td></tr>';
    }
}
?>
</table>

