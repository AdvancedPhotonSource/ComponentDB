<div class="sectionTable">
  <table width="100%"  border="1" cellspacing="0" cellpadding="2">
<?php
	  $iocList = $_SESSION['iocList'];
	  $iocName = $_SESSION['iocName'];
	  $iocEntity = $iocList->getElementForIocName($iocName);

	  echo '<tr>';
	  echo '<th colspan="2">IOC Network Information</th>';
	  echo '</tr>';
	  echo '<tr>';
      echo '<th nowrap width="50%">IOC Name</th><th>'.$iocEntity->getIocName().'</th>';
	  echo '</tr>';

	  echo '<tr>';
	  echo '<th nowrap class="terminal">Terminal Server Rack Number</th><td class="results">'.$iocEntity->getTermServRackNo().'</td>';
	  echo '</tr>';
	  echo '<tr>';
	  echo '<th nowrap class="terminal">Terminal Server Name</th><td class="results">'.$iocEntity->getTermServName().'</td>';
	  echo '</tr>';
	  echo '<tr>';
	  echo '<th nowrap class="terminal">Terminal Server Port</th><td class="results">'.$iocEntity->getTermServPort().'</td>';
	  echo '</tr>';
	  echo '<tr>';
	  echo '<th nowrap class="terminal">Terminal Server Fiber Converter Chassis</th><td class="results">'.$iocEntity->getTermServFiberConvCh().'</td>';
	  echo '</tr>';
	  echo '<tr>';
	  echo '<th nowrap class="terminal">Terminal Server Fiber Converter Port</th><td class="results">'.$iocEntity->getTermServFiberConvPort().'</td>';
	  echo '</tr>';
	  echo '<tr>';
	  echo '<th nowrap class="primary">Primary Ethernet Switch Rack Number</th><td class="results">'.$iocEntity->getPrimEnetSwRackNo().'</td>';
	  echo '</tr>';
	  echo '<tr>';
	  echo '<th nowrap class="primary">Primary Ethernet Switch</th><td class="results">'.$iocEntity->getPrimEnetSwitch().'</td>';
      echo '</tr>';
	  echo '<tr>';
	  echo '<th nowrap class="primary">Primary Ethernet Blade</th><td class="results">'.$iocEntity->getPrimEnetBlade().'</td>';
      echo '</tr>';
	  echo '<tr>';
	  echo '<th nowrap class="primary">Primary Ethernet Port</th><td class="results">'.$iocEntity->getPrimEnetPort().'</td>';
      echo '</tr>';
	  echo '<tr>';
	  echo '<th nowrap class="primary">Primary Ethernet Media Converter Chassis</th><td class="results">'.$iocEntity->getPrimEnetMedConvCh().'</td>';
      echo '</tr>';
	  echo '<tr>';
	  echo '<th nowrap class="primary">Primary Ethernet Media Converter Port</th><td class="results">'.$iocEntity->getPrimMediaConvPort().'</td>';
	  echo '</tr>';
	  echo '<tr>';
	  echo '<th nowrap class="secondary">Secondary Ethernet Switch Rack Number</th><td class="results">'.$iocEntity->getSecEnetSwRackNo().'</td>';
	  echo '</tr>';
	  echo '<tr>';
	  echo '<th nowrap class="secondary">Secondary Ethernet Switch</th><td class="results">'.$iocEntity->getSecEnetSwitch().'</td>';
      echo '</tr>';
	  echo '<tr>';
	  echo '<th nowrap class="secondary">Secondary Ethernet Blade</th><td class="results">'.$iocEntity->getSecEnetBlade().'</td>';
      echo '</tr>';
	  echo '<tr>';
	  echo '<th nowrap class="secondary">Secondary Ethernet Port</th><td class="results">'.$iocEntity->getSecEnetPort().'</td>';
      echo '</tr>';
	  echo '<tr>';
	  echo '<th nowrap class="secondary">Secondary Ethernet Media Converter Chassis</th><td class="results">'.$iocEntity->getSecEnetMedConvCh().'</td>';
      echo '</tr>';
	  echo '<tr>';
	  echo '<th nowrap class="secondary">Secondary Ethernet Media Converter Port</th><td class="results">'.$iocEntity->getSecMedConvPort().'</td>';
	  echo '</tr>';
?>
</table>
</div><br>
