<?php

    include_once('i_common.php');

?>

<form action="action_aoi_edit_search.php" method="POST">
<div class="searchCriteria">
  <table align="left" cellpadding="2" cellspacing="1">

    <tr>
      <td class="titleheading">&nbsp;AOI Search Criteria</td>

	  <td align="center"><input type="submit" class="buttons" name="searchAOIs" value="AOI Search" style="background:#ffff00"></td>

	  <td align="center"><input type="submit" class="buttons" name="aoiReset" value="Reset" style="background:#ffff00"></td>

	  <td>&nbsp;&nbsp;Desc&nbsp;<input name="desc_search" class="textentry" type="text" value="<?php echo
	  $_SESSION['desc_search'] ?>" size="15"></td>

	  <td>&nbsp;&nbsp;PV&nbsp;<input name="pv_search" class="textentry" type="text" value="<?php echo
      $_SESSION['pv_search'] ?>" size="24"></td>

	  <?

	  $my_sessionname = session_name();

	  $_SESSION['SESSION_NAME'] = $my_sessionname;

	  echo '<td align="left" bgcolor="CCCCCC"><span style = font-size: 10pt;>Server
	  '.$db_host.':'.$db_name_production_1.'</span></td>';


	  ?>

    <tr>
    <td>&nbsp;&nbsp;AOI Name/ICMS Keyword</td>
    <td>&nbsp;&nbsp;Machine</td>
	<td>&nbsp;&nbsp;Technical System</td>
    <td>&nbsp;&nbsp;Cognizant</td>
    <td>&nbsp;&nbsp;Criticality</td>
	<td>&nbsp;&nbsp;IOC</td>

    </tr>
    <tr>
	<td>&nbsp;&nbsp;<input name="aoiNameConstraint" class="textentry" type="text" value="<?php echo
      $_SESSION['aoiNameConstraint'] ?>" size="20"></td>


		<td>&nbsp;&nbsp;<select name="machine" class="pulldown">
		 <? if ($_SESSION['machine']) {
		       echo '<option value="'.$_SESSION['machine'].'" selected>'.$_SESSION['machine'].'</option>\n';
			   echo '<option value="">---- All ----</option>\n';

			}else {
			   echo '<option value="" selected>---- All ----</option>\n';
			}

	          $result = $_SESSION['machineList'];
		    logEntry('debug',"here's my machineList session var ".print_r($result,true));
		    $machineArray = $result->getArray();
		    foreach ($machineArray as $machineEntity) {
	        		echo '<option value="'.$machineEntity->getMachine().'">'.$machineEntity->getMachine().'</option>\n';

		    }?>
		</select></td>


      <td>&nbsp;&nbsp;<select name="techsystem" class="pulldown">
		 <? if ($_SESSION['techsystem']) {
		       echo '<option value="'.$_SESSION['techsystem'].'" selected>'.$_SESSION['techsystem'].'</option>\n';
			   echo '<option value="">---- All ----</option>\n';

			}else {
			   echo '<option value="" selected>---- All ----</option>\n';
			}

	          $result = $_SESSION['techsystemList'];
		    logEntry('debug',"here's my techsystemList session var ".print_r($result,true));
		    $technicalsystemArray = $result->getArray();
		    foreach ($technicalsystemArray as $techsystemEntity) {
	        		echo '<option value="'.$techsystemEntity->getTechSystem().'">'.$techsystemEntity->getTechSystem().'</option>\n';

			}?>
			</select></td>

            <td>&nbsp;&nbsp;<select name="person" class="pulldown">
	  		 <? if ($_SESSION['person']) {
	  		       echo '<option value="'.$_SESSION['person'].'" selected>'.$_SESSION['person'].'</option>\n';
	  			   echo '<option value="">---- All ----</option>\n';

	  			}else {
	  			   echo '<option value="" selected>---- All ----</option>\n';
	  			}

	  	          $result = $_SESSION['personList'];
	  		    logEntry('debug',"here's my personList session var ".print_r($result,true));
	  		    $personArray = $result->getArray();
	  		    foreach ($personArray as $personEntity) {

	  		            echo '<option value="'.$personEntity->getLast_nm().", ".$personEntity->getFirst_nm().'">'.$personEntity->getLast_nm().", ".$personEntity->getFirst_nm().'</option>\n';

	  	        		// echo '<option value="'.$personEntity->getLast_nm().'">'.$personEntity->getLast_nm().", ".$personEntity->getFirst_nm().'</option>\n';

	  		    }?>
	  		</select></td>

	        <td>&nbsp;&nbsp;<select name="criticality" class="pulldown">
	  		 <? if ($_SESSION['criticality']) {
	  		       echo '<option value="'.$_SESSION['criticality'].'" selected>'.$_SESSION['criticality'].'</option>\n';
	  			   echo '<option value="">---- All ----</option>\n';

	  			}else {
	  			   echo '<option value="" selected>---- All ----</option>\n';
	  			}

	  	          $result = $_SESSION['criticalityList'];
	  		    logEntry('debug',"here's my criticalityList session var ".print_r($result,true));
	  		    $criticalityArray = $result->getArray();
	  		    foreach ($criticalityArray as $criticalityEntity) {
	  	        		echo '<option value="'.$criticalityEntity->getCriticalityLevel().'">'.$criticalityEntity->getCriticalityLevel()." ".$criticalityEntity->getCriticalityClassification().'</option>\n';

	  		    }?>
		</select></td>
		<td>&nbsp;&nbsp;<select name="iocname" class="pulldown">
		 <? if ($_SESSION['iocname']) {
		       echo '<option value="'.$_SESSION['iocname'].'" selected>'.$_SESSION['iocname'].'</option>\n';
			   echo '<option value="">---- All ----</option>\n';

			}else {
			   echo '<option value="" selected>---- All ----</option>\n';
			}

	          $result = $_SESSION['iocnameList'];
		    logEntry('debug',"here's my iocnameList session var ".print_r($result,true));
		    $iocnameArray = $result->getArray();
		    foreach ($iocnameArray as $iocnameEntity) {
	        		echo '<option value="'.$iocnameEntity->getIocName().'">'.$iocnameEntity->getIocName().'</option>\n';

		    }?>
		</select></td>


    </tr>

	</table><br /><br /><br /><br />

	<table align="left" cellpadding="2" cellspacing="1">


    </table>

</div>
</form>
