<table cellpadding="1" bgcolor="dbcddc" width="100%">
<tr>
<th align="left" width="20%">IOC</th><th align="left" width="60%">PV Name</th><th align="left" width="20%">Type</th>
</tr>
<?php 
    $recList = $_SESSION['PVSearchResults'];
    
    // null list in session implies that result size was exceeded
    if ($recList == null) {
        echo "<tr><td colspan=3><font color=red>Search produced too many results to display.<br>\n";
        echo "Limit is 5000. Try using the Additional<br>\n";
        echo "Search Terms to constrain your search.</font></td></tr>\n";      
         
    } else if ($recList->length() == 0) {
        echo "<tr><td colspan=3>No Process Variables found: please try another search.</td></tr>\n";
        
    } else {
        $recEntities = $recList->getDisplayArray();
        foreach ($recEntities as $recEntity) {
            echo "<tr>\n";   
            echo '<td class="one">'.$recEntity->getIocName()."</td>\n";
            echo '<td class="one"><a href="action_pv_details.php?pvName='.urlencode($recEntity->getRecName()).'">'.$recEntity->getRecName()."</a></td>\n";
            echo '<td class="one">'.$recEntity->getRecType()."</td>\n";
            echo "</tr>\n";
        }
    }

?>
</table>