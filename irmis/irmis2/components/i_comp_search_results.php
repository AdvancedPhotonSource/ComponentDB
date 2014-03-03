<div class="searchResults">
  <table width="100%"  border="1" cellspacing="0" cellpadding="2">
<?php
echo '<a name="top"></a>';

    // null list in session implies that result size was exceeded
	$ComponentTypeList = $_SESSION['ComponentTypeList'];
	    echo '<tr><th colspan="8"class=value>"'.$ComponentTypeList->length().'" Components Found';
		if ($ComponentTypeList->length() != 0)
		{
		  echo '<font class="valueright">&nbsp;&nbsp;&nbsp;&nbsp;<a class="hyper" href="#report">Report Tools</a></th>';
		}
		echo '</tr>';
	    echo '<tr>';
        echo '<th nowrap>Component Type</th>';
	    echo '<th>Description</th>';
	    echo '<th>Manufacturer</th>';
		echo '<th>Details</th>';
        echo '</tr>';
    if ($ComponentTypeList == null) {
        echo '<tr><td class="warning bold" colspan=8>Search produced too many results to display.<br>';
        echo 'Limit is 5000. Try using the Additional<br>';
        echo 'Search Terms to constrain your search.</td></tr>';

    } else if ($ComponentTypeList->length() == 0) {
        echo '<tr><td class="warning bold" colspan=8>No Component\'s found: please try another search.</td></tr>';

    } else {

        $ctEntities = $ComponentTypeList->getArray();
        foreach ($ctEntities as $ctEntity) {
            echo '<tr>';
			echo '<td class=primary>'.$ctEntity->getCtName().'&nbsp;(<acronym title="Component Type ID">'.$ctEntity->getID().'</acronym>)</td>';
			echo '<td class=resulttext>'.$ctEntity->getCtDesc().'</td>';
			echo '<td class=center>'.$ctEntity->getMfgName().'</td>';
			echo '<td><a class="hyper" href="action_comp_details_search.php?ID='.$ctEntity->getID().'">Details</td>';
			echo '</tr>';
		}

      echo '</table><table width="100%"  border="1" cellspacing="0" cellpadding="2">
      <tr><A name="report"></A>';

		include('../report/report_startform.php');
		include('../report/report_submit_comp.php');

      echo '</tr>';


    }

?>
</table>