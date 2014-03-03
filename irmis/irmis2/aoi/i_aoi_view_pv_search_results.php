<div class="searchEditResultsPV">
  <table width="99%"  border="1" cellspacing="0" cellpadding="2">
<?php

    $aoiPvList = $_SESSION['aoiPvList'];
    $aoiName = $_SESSION['aoi_name'];
    $aoiPvEntity = $_SESSION['aoiPvEntity'];


    // echo '<tr><td class="sectionHeaderCbox" width = "20%"><input type = "checkbox" name = selected[] value = "PVs">Save Results</td>';

    if ($aoiPvList->length() == 0) {
     echo '<td class="sectionHeaderCbox" colspan="3" align="center"><b>No AOI PVs Found</b></td>';
    }
    else
    {
      echo '<td class = "sectionHeaderCbox" colspan="3" align="center"><b>AOI PVs</b></td>';
    }

    echo ' </tr>';

	echo '<tr><td class="value">Record Name</td><td class="value">IOC</td><td class="value">st.cmd Load Line</td></tr>';

    $aoiPvEntities = $aoiPvList->getArray();

    foreach ($aoiPvEntities as $aoiPvEntity) {
		echo '<tr><td>'.$aoiPvEntity->getRecordName().'</td><td>'.$aoiPvEntity->getIOCName().'</td><td>'.$aoiPvEntity->getIOCStCmdLine().'</td> </tr>';
	}

?>
</table>
</div>

