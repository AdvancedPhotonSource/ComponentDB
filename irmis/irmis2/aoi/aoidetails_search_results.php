<div class="searchEditResultsDetails">
  <table width="100%"  border="1" cellspacing="0" cellpadding="2">
<?php

   // Route the user's requsted AOI to detailed information displays.

   $_SESSION['aoi_id'] = $_GET['aoiId'];

   $_SESSION['aoi_name'] = $_GET['aoiName'];

   $aoi_name = $_SESSION['aoi_name'];
   $aoi_id = $_SESSION['aoi_id'];

    echo '<tr><th colspan="4" class="center"><a href=i_aoi_view_details_search_results.php?aoi_name='.$aoi_name.'&aoi_id='.$aoi_id.'>AOI Crawler Discovered Relationships</th></tr>';
	echo '<tr> <td class="value" width="20%">Process Variables</td><td class="value" width="20%">st.cmd Lines</td><td class="value_light">Revision History</td><td class="value">User Programmable Components</td></tr>';

?>
</table>
</div>

