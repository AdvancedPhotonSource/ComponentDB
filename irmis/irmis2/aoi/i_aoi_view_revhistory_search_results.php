<div class="searchEditResultsRevhistory">
  <table width="100%"  border="1" cellspacing="0" cellpadding="2">
<?php

   $aoiRevhistoryList = $_SESSION['aoiRevhistoryList'];
   $aoiName = $_SESSION['aoi_name'];
   $aoiRevhistoryEntity = $_SESSION['aoiRevhistoryEntity'];
   $_SESSION['new_aoi_comment'] = "";


   if ($aoiRevhistoryList->length() == 0) {
   	  	echo '<tr><td class = "sectionHeaderCbox" align = "center" colspan="2"><b>No AOI Revision History Comments Found</b></td></tr>';
      	echo '<tr><td class="value" width="20%" >Revision Date</td><td class="value">Comment</td></tr>';

   }
   else{
		echo '<tr><td class = "sectionHeaderCbox" align = "center" colspan = "2"><b>AOI Revision History</b></td></tr>';
   		echo '<tr><td class="value" width="20%" >Revision Date</td><td class="value">Comment</td></tr>';

   		$aoiRevhistoryEntities = $aoiRevhistoryList->getArray();

   		foreach ($aoiRevhistoryEntities as $aoiRevhistoryEntity) {
			echo '<tr><td>'.$aoiRevhistoryEntity->getRevhistoryDate().'</td><td>'.$aoiRevhistoryEntity->getRevhistoryComment().'</td></tr>';
   		}
   }
?>
</table>
</div>

