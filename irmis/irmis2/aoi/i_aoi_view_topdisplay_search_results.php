<div class="searchEditResultsTopdisplay">
  <table width="99%"  border="1" cellspacing="0" cellpadding="2">
<?php

   $aoiTopdisplayList = $_SESSION['aoiTopdisplayList'];

   $aoiTopdisplayEntity = $_SESSION['aoiTopdisplayEntity'];
   $_SESSION['new_aoi_topdisplay'] = "";


   if ($aoiTopdisplayList->length() == 0) {
	echo '<tr><th class="sectionHeaderCbox"><b>No AOI Top MEDM Displays Found</b></th></tr>';
	//SBecho '<tr><td class="value">URI</td></tr>';
 	// echo '<tr><td>New Top Display: <input name="new_aoi_topdisplay" type="text" value="'.$_SESSION['new_aoi_topdisplay'].'" size="120"></td></tr>';
   }
   else {
   	echo '<tr><th class = "sectionHeaderCbox"><b>Top MEDM Displays</b></th></tr>';

	echo '<tr><td class="value">URI</td></tr>';
      $aoiTopdisplayEntities = $aoiTopdisplayList->getArray();
      // echo '<tr><td>New Top Display: <input name="new_aoi_topdisplay" type="text" value="'.$_SESSION['new_aoi_topdisplay'].'" size="120"></td></tr>';

      foreach ($aoiTopdisplayEntities as $aoiTopdisplayEntity) {
         echo '<tr><td>'.$aoiTopdisplayEntity->getURI().'</td></tr>';
      }
   }
?>
</table>
</div>

