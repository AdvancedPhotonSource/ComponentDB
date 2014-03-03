<div class="searchEditResultsNote">
  <table width="100%"  border="1" cellspacing="0" cellpadding="2">
<?php

    $aoiNoteList = $_SESSION['aoiNoteList'];
    $aoiName = $_SESSION['aoi_name'];
    $aoiNoteEntity = $_SESSION['aoiNoteEntity'];

    $_SESSION['new_aoi_note'] = "";

    if ($aoiNoteList->length() == 0 && $_SESSION['aoi_id'] > 0) {
	  echo '<td class = "sectionHeaderCbox" colspan=2 align="center"><b>No AOI Notes Found</b></td></tr>';
      echo '<tr> <td class="value" width = "20%">Note Date</td><td class="value">Note</td></tr>';
    }

   else
   {
      echo '<td class = "sectionHeaderCbox" colspan=2 align = "center"><b> AOI Notes</b></td></tr>';
	  echo '<tr> <td class="value" width = "20%">Note Date</td><td class="value">Note</td></tr>';

      if ($_SESSION['aoi_id'] > 0){
	      $aoiNoteEntities = $aoiNoteList->getArray();

	      foreach ($aoiNoteEntities as $aoiNoteEntity) {
			echo '<tr> <td>'.$aoiNoteEntity->getNoteDate().'</td><td>'.$aoiNoteEntity->getNote().'</td></tr>';
		}
	  }
   }

?>
</table>
</div>

