<div class="searchResults">
  <table width="100%"  border="1" cellspacing="0" cellpadding="2">
<?php
	  $iocList = $_SESSION['iocList'];
	  $iocName = $_GET['iocName'];
      $_SESSION['iocName'] = $iocName;
	  $iocEntity = $iocList->getElementForIocName($iocName);

	  echo '<tr>';
	  echo '<th colspan="2" class="center">IOC Network Information</th>';
	  echo '</tr>';
	  echo '<tr>';
      echo '<th nowrap class="center">IOC Name</th><th class=value>'.$iocEntity->getIocName().'</th>';
	  echo '</tr>';
	  // General Information
	  echo '<tr>';
	  echo '<th>General Info</th><td class="results"><a class="hyper" href="iocgeneral_search_results.php?iocName='.$iocEntity->getIocName().'">General Info</a>
	        - Detailed information for this IOC.</td>';
	  echo '</tr>';
      // Network Status
	  echo '<tr>';
	  echo '<th>Network Status</th><td class="results"><a class="hyper" href="network_status.php?ioc='.$iocName.'&SwP='.$iocEntity->getPrimEnetSwitch().'&BlP='.$iocEntity->getPrimEnetBlade().'&PortP='.$iocEntity->getPrimEnetPort().'&SwS='.$iocEntity->getSecEnetSwitch().'&BlS='.$iocEntity->getSecEnetBlade().'&PortS='.$iocEntity->getSecEnetPort().'" target=_blank" class="bold">Network Switch Status</a> - Primary and Secondary Network Connection Status&nbsp;&nbsp;</td>';
	  echo '</tr>';
      // Terminal Server Rack Number
	  echo '<tr>';
	  if ($iocEntity->getTermServRackNo()) {
	    echo '<th class="terminal" nowrap>Terminal Server Rack Number</th><td class="results">'.$iocEntity->getTermServRackNo().'</td>';
	  } else {
	    echo '<th class="terminal" nowrap>Terminal Server Rack Number</th><td class="results">None</td>';
	  }
	  echo '</tr>';
	  // Terminal Server Name
	  echo '<tr>';
	  if ($iocEntity->getTermServName()) {
	    echo '<th class="terminal" nowrap>Terminal Server Name</th><td class="results">'.$iocEntity->getTermServName().'</td>';
	  } else {
	    echo '<th class="terminal" nowrap>Terminal Server Name</th><td class="results">None</td>';
	  }
	  echo '</tr>';
	  // Terminal Server Port
	  echo '<tr>';
	  if ($iocEntity->getTermServPort()) {
	    echo '<th class="terminal" nowrap>Terminal Server Port</th><td class="results">'.$iocEntity->getTermServPort().'</td>';
	  } else {
	    echo '<th class="terminal" nowrap>Terminal Server Port</th><td class="results">None</td>';
	  }
	  echo '</tr>';
	  // Terminal Server Fiber Converter Chassis
	  echo '<tr>';
	  if ($iocEntity->getTermServFiberConvCh()) {
	    echo '<th class="terminal" nowrap>Terminal Server Fiber Converter Chassis</th><td class="results">'.$iocEntity->getTermServFiberConvCh().'</td>';
	  } else {
	    echo '<th class="terminal" nowrap>Terminal Server Fiber Converter Chassis</th><td class="results">None</td>';
	  }
	  echo '</tr>';
	  // Terminal Server Fiber Converter Port
	  echo '<tr>';
	  if ($iocEntity->getTermServFiberConvPort()) {
	    echo '<th class="terminal" nowrap>Terminal Server Fiber Converter Port</th><td class="results">'.$iocEntity->getTermServFiberConvPort().'</td>';
	  } else {
	    echo '<th class="terminal" nowrap>Terminal Server Fiber Converter Port</th><td class="results">None</td>';
	  }
	  echo '</tr>';
	  //Primary Ethernet Switch Rack Number
	  echo '<tr>';
	  if ($iocEntity->getPrimEnetSwRackNo()) {
	    echo '<th class="primary" nowrap>Primary Ethernet Switch Rack Number</th><td class="results">'.$iocEntity->getPrimEnetSwRackNo().'</td>';
	  } else {
	    echo '<th class="primary" nowrap>Primary Ethernet Switch Rack Number</th><td class="results">None</td>';
	  }
	  echo '</tr>';
	  // Primary Ethernet Switch
	  echo '<tr>';
	  if ($iocEntity->getPrimEnetSwitch()) {
	    echo '<th class="primary" nowrap>Primary Ethernet Switch</th><td class="results">'.$iocEntity->getPrimEnetSwitch().'</td>';
      } else {
	    echo '<th class="primary" nowrap>Primary Ethernet Switch</th><td class="results">None</td>';
	  }
	  echo '</tr>';
	  // Primary Ethernet Blade
	  echo '<tr>';
	  if ($iocEntity->getPrimEnetBlade()) {
	    echo '<th class="primary" nowrap>Primary Ethernet Blade</th><td class="results">'.$iocEntity->getPrimEnetBlade().'</td>';
      } else {
	    echo '<th class="primary" nowrap>Primary Ethernet Blade</th><td class="results">None</td>';
	  }
	  echo '</tr>';
	  // Primary Ethernet Port
	  echo '<tr>';
	  if ($iocEntity->getPrimEnetPort()) {
	    echo '<th class="primary" nowrap>Primary Ethernet Port</th><td class="results">'.$iocEntity->getPrimEnetPort().'</td>';
      } else {
	    echo '<th class="primary" nowrap>Primary Ethernet Port</th><td class="results">None</td>';
	  }
	  echo '</tr>';
	  // Primary Ethernet Media Converter Chassis
	  //echo '<tr>';
	  //if ($iocEntity->getPrimEnetMedConvCh()) {
	  //  echo '<th class="primary" nowrap>Primary Ethernet Media Converter Chassis</th><td class="results">'.$iocEntity->getPrimEnetMedConvCh().'</td>';
      //} else {
	  //  echo '<th class="primary" nowrap>Primary Ethernet Media Converter Chassis</th><td>&nbsp;</td>';
      //}
	  //echo '</tr>';
	  // Primary Ethernet Media Converter Port
	  //echo '<tr>';
	  //if ($iocEntity->getPrimMediaConvPort()) {
	  //  echo '<th class="primary" nowrap>Primary Ethernet Media Converter Port</th><td class="results">'.$iocEntity->getPrimMediaConvPort().'</td>';
	  //} else {
	  //  echo '<th class="primary" nowrap>Primary Ethernet Media Converter Port</th><td>&nbsp;</td>';
	  //}
	  //echo '</tr>';
	  //Secondary Ethernet Switch Rack Number
	  echo '<tr>';
	  if ($iocEntity->getSecEnetSwRackNo()) {
	    echo '<th class="secondary" nowrap>Secondary Ethernet Switch Rack Number</th><td class="results">'.$iocEntity->getSecEnetSwRackNo().'</td>';
	  } else {
	    echo '<th class="secondary" nowrap>Secondary Ethernet Switch Rack Number</th><td class="results">None</td>';
	  }
	  echo '</tr>';
	  // Secondary Ethernet Switch
	  echo '<tr>';
	  if ($iocEntity->getSecEnetSwitch()) {
	    echo '<th class="secondary" nowrap>Secondary Ethernet Switch</th><td class="results">'.$iocEntity->getSecEnetSwitch().'</td>';
      } else {
	    echo '<th class="secondary" nowrap>Secondary Ethernet Switch</th><td class="results">None</td>';
	  }
	  echo '</tr>';
	  // Secondary Ethernet Blade
	  echo '<tr>';
	  if ($iocEntity->getSecEnetBlade()) {
	    echo '<th class="secondary" nowrap>Secondary Ethernet Blade</th><td class="results">'.$iocEntity->getSecEnetBlade().'</td>';
      } else {
	    echo '<th class="secondary" nowrap>Secondary Ethernet Blade</th><td class="results">None</td>';
	  }
	  echo '</tr>';
	  // Secondary Ethernet Port
	  echo '<tr>';
	  if ($iocEntity->getSecEnetPort()) {
	    echo '<th class="secondary" nowrap>Secondary Ethernet Port</th><td class="results">'.$iocEntity->getSecEnetPort().'</td>';
      } else {
	    echo '<th class="secondary" nowrap>Secondary Ethernet Port</th><td class="results">None</td>';
	  }
	  echo '</tr>';
	  // Secondary Ethernet Media Converter Chassis
	  //echo '<tr>';
	  //if ($iocEntity->getSecEnetMedConvCh()) {
	  //  echo '<th class="secondary" nowrap>Secondary Ethernet Media Converter Chassis</th><td class="results">'.$iocEntity->getSecEnetMedConvCh().'</td>';
      //} else {
	  //  echo '<th class="secondary" nowrap>Secondary Ethernet Media Converter Chassis</th><td>&nbsp;</td>';
	  //}
	  //echo '</tr>';
	  // Secondary Ethernet Media Converter Port
	  //echo '<tr>';
	  //if ($iocEntity->getSecMedConvPort()) {
	  //  echo '<th class="secondary" nowrap>Secondary Ethernet Media Converter Port</th><td class="results">'.$iocEntity->getSecMedConvPort().'</td>';
	  //} else {
	  //  echo '<th class="secondary" nowrap>Secondary Ethernet Media Converter Port</th><td>&nbsp;</td>';
	  //}
	  //echo '</tr></table>';

     echo '<table width="100%"  border="1" cellspacing="0" cellpadding="2"><tr>';
       include('../report/report_startform.php');
       include('../report/report_submit_ioc_network.php');
     echo '</tr>';

?>
</table>
</div>
