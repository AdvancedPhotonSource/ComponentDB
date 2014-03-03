<div class="searchResults">
  <table width="100%"  border="1" cellspacing="0" cellpadding="2">
<?php 
    // null list in session implies that result size was exceeded
echo '<a name="top"></a>';
	$SpareList = $_SESSION['SpareList'];
	    echo '<tr><th colspan="6"class=value>"'.$SpareList->length().'" Component\'s Found';
				if ($SpareList->length() != 0)
		{
		  echo '<font class="valueright">&nbsp;&nbsp;&nbsp;&nbsp;<a class="hyper" href="#report">Report Tools</a></th>';
		}
		echo '</tr>';
	    echo '<tr>';
        echo '<th nowrap>Component Type</th>';
	    echo '<th>Description</th>';
	    echo '<th>Installed Qty</th>';
		echo '<th>Spare Qty</th>';
		echo '<th>Spare Location</th>';
		echo '<th>Stock Qty</th>';
        echo '</tr>';
    if ($SpareList == null) {
        echo '<tr><td class="warning bold" colspan=8>Search produced too many results to display.<br>';
        echo 'Limit is 5000. Try using the Additional<br>';
        echo 'Search Terms to constrain your search.</td></tr>';      
         
    } else if ($SpareList->length() == 0) {
        echo '<tr><td class="warning bold" colspan=8>No Component\'s found: please try another search.</td></tr>';
        
    } else {
	        
        $spEntities = $SpareList->getArray();
        foreach ($spEntities as $spEntity) {
            echo '<tr>';   
			echo '<td class=primary>'.$spEntity->getCtName().'</td>';
			echo '<td class=resulttext>'.$spEntity->getCtDesc().'</td>';
			echo '<td class=center>'.$spEntity->getInsQty().'</td>';
			echo '<td class=center>'.$spEntity->getSpareQty().'</td>';
			if ($spEntity->getSpareLoc()) {
			  echo '<td class=center>'.$spEntity->getSpareLoc().'</td>';
			} else {
			  echo '<td>&nbsp;</td>';
			}
			echo '<td class=center>'.$spEntity->getStockQty().'</td>';
			echo '</tr>';
		}
		      echo '</table><table width="100%"  border="1" cellspacing="0" cellpadding="2">
      <tr><a name="report"></a>';

		include('../report/report_startform.php');
		include('../report/report_submit_spares.php');

      echo '</tr>';
    }

?>
</table>