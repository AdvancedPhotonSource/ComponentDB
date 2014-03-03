<div class="searchResults">
  <table width="100%"  border="1" cellspacing="0" cellpadding="2">
<?php
    // null list in session implies that result size was exceeded
    $plcList = $_SESSION['plcList'];
    unset($_SESSION['plcName']);

    echo '<tr><td colspan="8"class=value>'.$plcList->length().' PLCs Found</td></tr>';
    echo '<tr>';
    echo '<th nowrap>PLC Name</th>';
    echo '<th width=25%>Description</th>';
    echo '<th width=10%>Cog Group</th>';
    echo '<th>Location</th>';
    echo '<th>IOC Name</th>';
    echo '</tr>';

    if ($plcList == null) {
        echo '<tr><td class="warning bold" colspan=8>Search produced too many results to display.<br>';
        echo 'Limit is 5000. Try using the Additional<br>';
        echo 'Search Terms to constrain your search.</td></tr>';
    } else if ($plcList->length() == 0) {
        echo '<tr><td class="warning bold" colspan=8>No PLC\'s found: please try another search.</td></tr>';
    } else {
        $plcEntities = $plcList->getArray();
        foreach ($plcEntities as $plcEntity) {

            $plcParentList = new ParentList();
            logEntry('debug',"Performing plc parents search");
            $plcParentList->loadFromDB($conn_2, $plcEntity->getPlcName());

            // THIS IS TO LOOK FOR CTL PARENT - AKA IOC
            $iocName='';
            $plcIoc = new CtlParent();
            logEntry('debug',"Performing ioc search");
            if ($plcIoc->loadFromDB($conn_2, $plcEntity->getPlcName())) {
                $iocName=$plcIoc->getCtlParent();
            }
            echo '<tr>';
            // echo '<td>'.$plcEntity->getPlcName().'</td>';
            echo '<td><a

href="plc_detail_results.php?cID='.$plcEntity->getPlcCompID().'&iocName='.$iocName.'&plcName='.$plcEntity->getPlcName().'&plcVerPVName='.$plcEntity->getPlcVerPVName().'">'.$plcEntity->getPlcName().'</td>';

            echo '<td>'.$plcEntity->getPlcDesc().'</td>';
            echo '<td>'.$plcEntity->getPlcGroup().'</td>';
	    echo '<td>';
            $plcParents = $plcParentList->getArray();
            $parentsNr = count($plcParents)-1;
            foreach ($plcParents as $count) {
	        echo $plcParents[$parentsNr--].'<br>';
	    }
	    echo '</td>';
            echo '<td>'.$iocName.'</td>';
        }
    }
?>
</table>

