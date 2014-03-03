<form action="action_comp_search.php" method="POST">
<div class="searchCriteria">
  <table width="98%"  border="0" align="right" cellpadding="2" cellspacing="0">
    <tr>
	  <td>&nbsp;</td>
	</tr>
	<tr>
      <td class="titleheading">Component Type Info<br />&nbsp;Search Criteria</td>
	</tr>
    <tr>
      <td>&nbsp;</td>
    </tr>
    <tr>
      <td>Name/Description</td>
    </tr>
    <tr>
      <td><input name="nameDesc" class="textentry" type="text" value="<?php echo $_SESSION['nameDesc'] ?>" size="20"></td>
    </tr>
    <tr>
      <td>&nbsp;</td>
    </tr>
    <tr>
      <td>Manufacturer</td>
	</tr>
    <tr>
      <td><select name="mfgConstraint" class="pulldown">
		 <? if ($_SESSION['mfgConstraint']) {
		       echo '<option value="'.$_SESSION['mfgConstraint'].'" selected>'.$_SESSION['mfgConstraint'].'</option>\n';
			   echo '<option value="">---- All ----</option>\n';

			}else {
			   echo '<option value="" selected>---- All ----</option>\n';
			}

	        $result = $_SESSION['mfgList'];
            //logEntry('debug',"here's my mfgList session var ".print_r($result,true));
			$mfgArray = $result->getArray();
			foreach ($mfgArray as $mfgEntity) {
	        echo '<option value="'.$mfgEntity->getMfg().'">'.$mfgEntity->getMfg().'</option>\n';

			}?>
			</select></td>
    </tr>
    <tr>
      <td>&nbsp;</td>
    </tr>
	<tr>
      <td>Form Factor</td>
    </tr>
    <tr>
      <td><select name="ffConstraint" class="pulldown">
		 <? if ($_SESSION['ffConstraint']) {
		       echo '<option value="'.$_SESSION['ffConstraint'].'" selected>'.$_SESSION['ffConstraint'].'</option>\n';
			   echo '<option value="">---- All ----</option>\n';

			}else {
			   echo '<option value="" selected>---- All ----</option>\n';
			}

	        $result = $_SESSION['ffList'];
            //logEntry('debug',"here's my ffList session var ".print_r($result,true));
			$ffArray = $result->getArray();
			foreach ($ffArray as $ffEntity) {
	        echo '<option value="'.$ffEntity->getFf().'">'.$ffEntity->getFf().'</option>\n';

			}?>
			</select></td>

    </tr>
	<tr>
      <td>&nbsp;</td>
    </tr>
	<tr>
      <td>Function</td>
    </tr>
	<tr>
      <td><select name="functionConstraint" class="pulldown">
		 <? if ($_SESSION['functionConstraint']) {
		       echo '<option value="'.$_SESSION['functionConstraint'].'" selected>'.$_SESSION['functionConstraint'].'</option>\n';
			   echo '<option value="">---- All ----</option>\n';

			}else {
			   echo '<option value="" selected>---- All ----</option>\n';
			}

	        $result = $_SESSION['functionList'];
            //logEntry('debug',"here's my functionList session var ".print_r($result,true));
			$functionArray = $result->getArray();
			foreach ($functionArray as $functionEntity) {
	        echo '<option value="'.$functionEntity->getFunction().'">'.$functionEntity->getFunction().'</option>\n';

			}?>
			</select></td>

    </tr>
	<tr>
      <td>&nbsp;</td>
    </tr>
	<tr>
      <td>Cognizant Person</td>
    </tr>
	<tr>
      <td><select name="personConstraint" class="pulldown">
		 <? if ($_SESSION['personConstraint']) {
		       echo '<option value="'.$_SESSION['personConstraint'].'" selected>'.$_SESSION['personConstraint'].'</option>\n';
			   echo '<option value="">---- All ----</option>\n';

			}else {
			   echo '<option value="" selected>---- All ----</option>\n';
			}

	        $result = $_SESSION['PersonList'];
            //logEntry('debug',"here's my functionList session var ".print_r($result,true));
			$PersonArray = $result->getArray();
			foreach ($PersonArray as $PersonEntity) {
	        echo '<option value="'.$PersonEntity->getLast().'"'.$PersonEntity->getFirst().'>'.$PersonEntity->getLast().', '. $PersonEntity->getFirst().'</option>\n';

			}?>
			</select></td>
    </tr>
	<tr>
      <td>&nbsp;</td>
    </tr>
    <tr>
    	<td><span class="buttonposition"><input type="submit" value="Search"  class="buttons" style="background:#ffff00">
	      <input type="submit" name="compReset" value="Reset"  class="buttons" style="background:#ffff00"></span></td>
    </tr>
    <tr>
    	<td>&nbsp;</td>
    </tr>	
  </table>
  <div id="help">
  <table>
  <tr><td><a class="hyper" href="../top/wd_help.php#component" target="_blank"><b>Web Desktop Help</b></a></td></tr>
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