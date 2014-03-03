<div class="searchResults">
  <table width="100%"  border="1" cellspacing="0" cellpadding="2">
<?php 
	  $iocList = $_SESSION['iocList'];
	  $iocName = $_GET['iocName'];
	  $iocEntity = $iocList->getElementForIocName($iocName);
	  
	  echo '<tr>';
	  echo '<th colspan="2" class="center">IOC Port Traffic and Error Plots</th>';
	  echo '</tr>';
	  echo '<tr>';
      echo '<th nowrap width="200">IOC Name</th><th class=value>'.$iocEntity->getIocName().'</th>';
	  echo '</tr>';
	  echo '<tr>';
	  echo '<th>VCCT Info</th><td><a class="bold" href="http://maia/irmis/device/dv_start_page_vw.php?p_ioc_name='.$iocEntity->getIocName().'" target="_blank">VCCT Info</a>
	        - VCCT information for this IOC.</td>';
	  echo '</tr>';
	  echo '<tr>';
	  echo '<th>General Info</th><td><a class="bold" href="iocgeneral_search_results.php?iocName='.$iocEntity->getIocName().'">General Info</a>
	        - Detailed information for this IOC.</td>';
	  echo '</tr>';
	  echo '<tr>';
	  echo '<th>Network Status</th><td><a class="bold" href="http://maia/irmis/net/net_startPage.php?name='.$iocEntity->getIocName().'" target="_blank">Network Status</a>
	        - Network connection status for this IOC.</td>';
	  echo '</tr>';
	  
	  echo '<tr>';
	  echo '<th class="primary" nowrap>'.$iocEntity->getIocName().'<br>
	        Network Traffic</th><td class="results"><a href="http://www2.aps.anl.gov/cgi-bin/routers2.cgi?rtr=accel%2F.hubmcrd.cfg&bars=Cami&xgtype=d&page=graph&xgstyle=l2&xmtype=options&if=hubmcrd.aps.anl.gov_Fa4_11"></td>';
	  echo '</tr>';
	  
	  echo '<tr>';
	  echo '<th class="secondary" nowrap>'.$iocEntity->getIocName().'<br>
	        Network Errors</th><td class="results"></td>';
	  echo '</tr>';
	  
?>

</table>
</div>
<!--http://www2.aps.anl.gov/cgi-bin/routers2.cgi?rtr=accel%2F".$Sw.".cfg&bars=Cami&xgtype=d&page=graph&xgstyle=l2&xmtype=options&if=".$Sw.".aps.anl.gov_".$Bl."_".$Port." -->
