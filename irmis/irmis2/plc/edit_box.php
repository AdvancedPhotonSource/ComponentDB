<div class="searchResults">
  <form ACTION="descr_editor.php" METHOD="POST">
  <table width="100%"  border="1" cellspacing="0" cellpadding="2">
<?php

   $plcName = $_SESSION['plcName'];
   $plcInstance = $_SESSION['plcDescEntity'];
   $plcDescr = $_SESSION['plcDescr'];
   $cID=$_SESSION['cID'];

   // check whether plc is in table; if it is not, add it
   // $plcInstance->insertDB($conn_2, $plcName);
   $plcInstance->insertDB($conn_plc_write, $cID);

   echo '<tr>';
   echo '<th colspan="3" class="center">PLC General Description</th>';
   echo '</tr>';
   echo '<tr>';
   echo '<th>PLC Name</th><th colspan="2" nowrap width="20%" class="value">'.$plcName.'</th>';
   echo '</tr>';
   echo '<tr>';
   echo '<th nowrap class="primary" width="20%">Description </th>';
   echo '<TD class="results" ALIGN=center colspan="3"><textarea NAME="description" ROWS=3 COLS=80>'.$plcDescr.'</textarea><br>';
   echo '<input type = "submit" align = "center" name = "descr_action" value = "Save">   ';
   echo '<input type = "submit" align = "center" name = "descr_action" value = "Revert">   ';
   echo '<input type = "submit" align = "center" name = "descr_action" value = "Cancel">';
   echo '</TD>';
   echo '</tr>';

?>
</table>
</form>

