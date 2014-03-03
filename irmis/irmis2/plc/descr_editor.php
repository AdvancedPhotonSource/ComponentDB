<?php
  include_once('i_common.php');

  $plcName=$_SESSION['plcName'];
  $iocName=$_SESSION['iocName'];
  $cID=$_SESSION['cID'];
  $plcInstance=$_SESSION['plcDescEntity'];

  if ($_POST[descr_action] == "Save") {

    // save entry in descr table
    $plcInstance->setPlcDesc($_POST[description]);

    // $plcInstance->updateDB($conn_2, $plcName);

    $plcInstance->updateDB($conn_plc_write, $cID);

    // logEntry('debug','Saved '.$plcName.' description: '.$_POST[description]);
    header('Location: plc_detail_results.php?cID='.$cID.'&plcName='.$plcName.'&iocName='.$iocName);
  }
  else if ($_POST[descr_action] == "Revert") {
    header('Location: action_edit_form.php');
  }
  else if ($_POST[descr_action] == "Cancel") {
    header('Location: plc_detail_results.php?cID='.$cID.'&plcName='.$plcName.'&iocName='.$iocName);
  }

?>
