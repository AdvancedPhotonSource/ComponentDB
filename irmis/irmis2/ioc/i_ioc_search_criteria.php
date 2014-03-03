<link href="../common/irmis2.css" rel="stylesheet" type="text/css" />
<form action="action_ioc_search.php" method="POST">
<div class="searchCriteria">
  <table width="98%" align="right" cellpadding="2" cellspacing="0">
    <tr>
      <td>&nbsp;</td>
    </tr>
    <tr>
      <td class="titleheading">IOC Info<br />&nbsp;Search Criteria</td>
    </tr>
    <tr>
      <td>&nbsp;</td>
    </tr>	
	<tr>
	  <?php // Accelerator IOC checkbox
	  $leroy = $_SESSION['Accioc'];
	  if (isset ($leroy)) {
	  $sid = "checked"; }
	  elseif (!isset ($leroy)) {$sid = "";}
	  ?>	
	  <td><input name="Accioc" type="checkbox"  value="<?php echo $_SESSION['Accioc']; ?>"<? echo $sid; ?>>&nbsp;Accelerator IOC's</td>
	</tr>  
	<tr>
	  <?php // Beamline IOC Checkbox
	  $clive = $_SESSION['BLioc'];
	  if (isset ($clive)) {
	  $milo = "checked"; }
	  elseif (!isset ($clive)) {$milo = "";}
	  ?>
	  <td><input name="BLioc" type="checkbox" value="<?php echo $_SESSION['BLioc']; ?>"<? echo $milo; ?>>&nbsp;Beamline IOC's</td>
	</tr>  	
    <tr>
      <td>&nbsp;</td>
    </tr>	
    <tr>
      <td>IOC Name</td>
    </tr>
    <tr>
      <td><input name="iocNameConstraint"  class="textentry" type="text" value="<?php echo $_SESSION['iocNameConstraint'] ?>" size="20"></td>
    </tr>
    <tr>
      <td>&nbsp;</td>
    </tr>
    <tr>
      <td>System</td>
    </tr>
    <tr>
      <td><select name="system" class="pulldown">
		 <? if ($_SESSION['system']) {
		       echo '<option value="'.$_SESSION['system'].'" selected>'.$_SESSION['system'].'</option>\n';
			   echo '<option value="">---- All ----</option>\n';

			}else {
			   echo '<option value="" selected>---- All ----</option>\n';
			}

	        $result = $_SESSION['systemList'];
		    logEntry('debug',"here's my systemList session var ".print_r($result,true));
			$systemArray = $result->getArray();
			foreach ($systemArray as $systemEntity) {
	        echo '<option value="'.$systemEntity->getSystem().'">'.$systemEntity->getSystem().'</option>\n';

			}?>
			</select></td>
    </tr>
    <tr>
      <td>&nbsp;</td>
    </tr>
    <tr>
      <td>IOC Status</td>
    </tr>
    <tr>
      <td><select name="status" class="pulldown">
		 <? if ($_SESSION['status']) {
		       echo '<option value="'.$_SESSION['status'].'" selected>'.ucfirst($_SESSION['status']).'</option>\n';
			   echo '<option value="">---- All Active----</option>\n';
			   echo '<option value="---- All ----">---- All ----</option>\n';

			}else {
			   echo '<option value="" selected>---- All Active ----</option>\n';
			   echo '<option value="---- All ----" >---- All ----</option>\n';
			}

	        $result = $_SESSION['statusList'];
		    logEntry('debug',"here's my statusList session var ".print_r($result,true));
			$statusArray = $result->getArray();
			foreach ($statusArray as $statusEntity) {
	        echo '<option value="'.$statusEntity->getStatus().'">'.ucfirst($statusEntity->getStatus()).'</option>\n';

			}?>
			</select></td>
    </tr>
    <tr>
      <td>&nbsp;</td>
    </tr>
	<tr>
      <td>Cognizant Technician</td>
    </tr>
    <tr>
      <td><select name="tech" class="pulldown">
		 <? if ($_SESSION['tech']) {
		       echo '<option value="'.$_SESSION['tech'].'" selected>'.$_SESSION['tech'].'</option>\n';
			   echo '<option value="">---- All ----</option>';

			}else {
			   echo '<option value="" selected>---- All ----</option>';
			   echo '<option value="'.$_SESSION['tech'].'">'.$_SESSION['tech'].'</option>\n';
			}

	        $result = $_SESSION['techList'];
		    logEntry('debug',"here's my developerList session var ".print_r($result,true));
			$techArray = $result->getArray();
			foreach ($techArray as $techEntity) {
	        echo '<option value="'.$techEntity->getLast_nm().'">'.$techEntity->getLast_nm().', '. $techEntity->getFirst_nm().'</option>\n';

			}?>
			</select></td>
    </tr>
	<tr>
      <td>&nbsp;</td>
    </tr>
	<tr>
      <td>Cognizant Developer</td>
    </tr>
	<tr>
      <td><select name="developer" class="pulldown">
		 <? if ($_SESSION['developer']) {
		       echo '<option value="'.$_SESSION['developer'].'" selected>'.$_SESSION['developer'].'</option>\n';
			   echo '<option value="">---- All ----</option>';

			}else {
			   echo '<option value="" selected>---- All ----</option>';
			   echo '<option value="'.$_SESSION['developer'].'">'.$_SESSION['developer'].'</option>\n';
			}

	        $result = $_SESSION['developerList'];
		    logEntry('debug',"here's my developerList session var ".print_r($result,true));
			$developerArray = $result->getArray();
			foreach ($developerArray as $developerEntity) {
	        echo '<option value="'.$developerEntity->getLast_nm().'">'.$developerEntity->getLast_nm().', '.$developerEntity->getFirst_nm().'</option>\n';

			}?>
			</select></td>
    </tr>
	<tr>
	  <td>&nbsp;</td>
	</tr>
	<tr>
      <td><span class="buttonposition"><input type="submit" value="Search"  class="buttons" style="background:#ffff00">
	      <input type="submit" name="iocReset" value="Reset"  class="buttons" style="background:#ffff00"></span></td>
    </tr>
	<tr>
      <td>&nbsp;</td>
    </tr>	
  </table>
  
  <div id="help">
  <table>
  <tr><td><a class="hyper" href="../top/wd_help.php#ioc" target="_blank"><b>Web Desktop Help</b></a></td></tr>
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
