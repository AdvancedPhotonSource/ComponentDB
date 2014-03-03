<div class="searchEditResultsUPC">
  <table width="99%"  border="1" cellspacing="0" cellpadding="2">
<?php

    $aoiList = $_SESSION['aoiList'];
    $aoiPlcList = $_SESSION['aoiPlcList'];//Added these next two lines
    $aoiIocList = $_SESSION['aoiIocList'];

    echo '<tr>';

        if ($aoiPlcList->length() == 0 && $aoiIocList->length() == 0) {
		    echo '<td class="sectionHeaderCbox" align="center" colspan="2"><b>No AOI UPCs Found</b></td>';
	    }
        else {
        	echo '<td class = "sectionHeaderCbox" align="center" colspan="2"> <b>AOI UPCs</b></td>';
        }
    echo '</tr>';

			echo '<tr> <td class="value" width = "20%">UPC Type</td> <td class="value">UPC Name</td></tr>';

	        $aoiPlcEntities = $aoiPlcList->getArray();
	        $aoiIocEntities = $aoiIocList->getArray();

	        foreach ($aoiPlcEntities as $aoiPlcEntity) {
				echo '<tr> <td>PLC</td><td>'.$aoiPlcEntity->getPLCName().'</td> </tr>';
			}

	        foreach ($aoiIocEntities as $aoiIocEntity) {
				echo '<tr> <td>IOC</td><td>'.$aoiIocEntity->getIOCName().'</td> </tr>';
			}

?>
</table>
</div>

