	<div class="searchResultsAOINames">
	<form id="reportResults" Method ="Post" action ="../report/action_report_form.php">

    <script type="text/javascript" src="../report/selectDeselectAll.js"></script>

	<table width="100%" border="1" cellspacing="0" cellpadding="2">
	<?php


    $aoiList = $_SESSION['aoiList'];
    $aoiName = $_SESSION['aoi_name'];

    if ($aoiList->length() != 0) {

	    	echo '<tr class="sectionHeaderCbox" align="left"><td colspan="2"><A name="top"><input type="checkbox" name=selected[] value="Names">Save Results&nbsp&nbsp&nbsp&nbsp<b>'.$aoiList->length().' AOIs Found</b>&nbsp&nbsp&nbsp</A><A href="#report">Report Tools</A></td></tr>';
			echo '<tr><b><td class = "value" align = "left"><b>Index</b></td><td class = "value" align = "center">&nbsp;&nbsp;&nbsp;<b>AOI Name</b>&nbsp;&nbsp;&nbsp;<font size="1"><A href="#'.$aoiName.'">Scroll To Last AOI Selected</A></font></td></tr>';

			$aoiNamePrevious = "";
        	$aoiEntities = $aoiList->getArray();
        	foreach ($aoiEntities as $aoiEntity) {

				// $_SESSION['aoi_name'] = $aoiEntity->getAOIName();

				if ($aoiEntity->getAOIName() != $aoiNamePrevious) {

				    //echo '<A name="'.$aoiEntity->getAOIName().'">&nbsp;</A>';

 					if ( ereg( "(([-a-zA-Z0-9]+_){4})", $aoiEntity->getAOIName())){
     	    			echo '<tr><td class="child"><A name="'.$aoiEntity->getAOIName().'">'.$aoiEntity->getAOIId().'</A></td><td class="child_name"><a href=aoi_edit_basic_search_results.php?aoiName='.$aoiEntity->getAOIName().'&aoiId='.$aoiEntity->getAOIId().'>'.$aoiEntity->getAOIName().'</td></tr>';

					}else{
						echo '<tr><td align="center"><A name="'.$aoiEntity->getAOIName().'">'.$aoiEntity->getAOIId().'</A></td><td><a href=aoi_edit_basic_search_results.php?aoiName='.$aoiEntity->getAOIName().'&aoiId='.$aoiEntity->getAOIId().'>'.$aoiEntity->getAOIName().'</td></tr>';
					}


					$aoiNamePrevious = $aoiEntity->getAOIName();
				}

			}
			// echo '<A name="report">&nbsp;</A>';


	 }
	 else{
	    	echo'<tr><td colspan="8"class=value>No AOIs Found</td></tr>';
	 }
	?>

	</table>

	<?php

		$aoiName = $_SESSION['aoi_name'];
		echo '<table width="100%" border="0" cellspacing="0" cellpadding="2">';
		echo '<tr><b><td class="value" align="center"><A href="#'.$aoiName.'">Scroll To Last AOI Selected</A></td></tr>';
		echo '</table>';
	?>


	 <table width="100%" border="1" cellspacing="0" cellpadding="2">

	 		<tr><th><A name="report">Report Tools&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<font class="valueright"></A><A href="#top">Top</A></font></th></tr>

	 <tr><td>
	       <input type = "button" value = "Select All" class="buttons" onClick = "selectDeselect('reportResults', true);"/>
	       <input type = "button" value = "Deselect All" class="buttons" onClick = "selectDeselect('reportResults', false);"/><br><br />

	       Comments: <br><textarea class="textbox" name = "comments" cols = "44" rows = "3"></textarea><br><br />

	       <input type = "hidden" name = "viewName" value = "AOIViewerEditor"/>

	       <input type = "submit" name = "text" value = "Save As .txt" class="buttons"/>
	       <input type = "submit" name = "print" value = "Print Report" class="buttons"/>
	       <input type = "submit" name = "csv" value = "Save As CSV" class="buttons"/>

	 </td></tr>
	 </form>
	 </table>
	 </div>




