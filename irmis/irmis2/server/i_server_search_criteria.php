<link href="../common/irmis2.css" rel="stylesheet" type="text/css" />
<form action="action_server_search.php" method="POST">
<div class="searchCriteria">
  <table width="98%" align="right" cellpadding="2" cellspacing="0">
    <tr>
      <td>&nbsp;</td>
    </tr>
    <tr>
      <td class="titleheading">Server Info<br />&nbsp;Search Criteria</td>
    </tr>
    <tr>
      <td>&nbsp;</td>
    </tr>
    <tr>
      <td>Server Name</td>
    </tr>
    <tr>
      <td><input name="serverNameConstraint"  class="textentry" type="text" value="<?php echo $_SESSION['serverNameConstraint'] ?>" size="20"></td>
    </tr>
    <tr>
      <td>&nbsp;</td>
    </tr>
    <tr>
      <td>Operating System</td>
    </tr>
    <tr>
      <td><select name="operating_system" class="pulldown">
		 <? if ($_SESSION['operating_system']) {
		       echo '<option value="'.$_SESSION['operating_system'].'" selected>'.$_SESSION['operating_system'].'</option>\n';
			   echo '<option value="">---- All ----</option>\n';

			}else {
			   echo '<option value="" selected>---- All ----</option>\n';
			}

	        $result = $_SESSION['operating_systemList'];
		    logEntry('debug',"here's my operating_systemList session var ".print_r($result,true));
			$operating_systemArray = $result->getArray();
			foreach ($operating_systemArray as $operating_systemEntity) {
	        echo '<option value="'.$operating_systemEntity->getOperating_System().'">'.$operating_systemEntity->getOperating_System().'</option>\n';

			}?>
			</select></td>
    </tr>
    <tr>
      <td>&nbsp;</td>
    </tr>
	<tr>
      <td>Cognizant</td>
    </tr>
	<tr>
      <td><select name="server_cognizant" class="pulldown">
		 <? if ($_SESSION['server_cognizant']) {
		       echo '<option value="'.$_SESSION['server_cognizant'].'" selected>'.$_SESSION['server_cognizant'].'</option>\n';
			   echo '<option value="">---- All ----</option>';

			}else {
			   echo '<option value="" selected>---- All ----</option>';
			}

	        $result = $_SESSION['cognizantList'];
		    logEntry('debug',"here's my cognizantList session var ".print_r($result,true));
			$cognizantArray = $result->getArray();
			foreach ($cognizantArray as $cognizantEntity) {
	        echo '<option value="'.$cognizantEntity->getLast_nm().'">'.$cognizantEntity->getLast_nm().'</option>\n';

			}?>
			</select></td>
    </tr>
	<tr>
	  <td>&nbsp;</td>
	</tr>
    <tr>
      <td><span class="buttonposition"><input type="submit" value="Search"  class="buttons" style="background:#ffff00">
	      <input type="submit" name="serverReset" value="Reset"  class="buttons" style="background:#ffff00"></span></td>
    </tr>
	<tr>
      <td>&nbsp;</td>
    </tr>
	<tr>
      <td>&nbsp;</td>
    </tr>
	<tr>
      <td>&nbsp;</td>
    </tr>
	<tr>
      <td>&nbsp;</td>
    </tr>
  </table>
  <div id="help">
  <table>
  <tr><td><a class="hyper" href="../top/wd_help.php#server" target="_blank"><b>Web Desktop Help</b></a></td></tr>
  </table>
  </div>
  
  <div id="servers">
  <?
    include_once ('db.inc');
    echo '<table><tr><td class="serverbackground"><b>Database Server:</b> <br />&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; '.$db_host.':'.$db_name_production_1.'</td></tr>';
	echo '<td class="serverbackground"><b>Application Server:</b> <br />&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;'.$app_host.'</td></tr>';
    echo '</table>';
  ?>
   </div>
</div>
</form>
