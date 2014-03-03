<form action="../racks/action_racks_search.php" method="POST">
<div class="searchCriteria">
  <table width="98%"  border="0" align="right" cellpadding="2" cellspacing="0">
    <tr>
	  <td>&nbsp;</td>
	</tr>
	<tr>
      <td class="titleheading" colspan="2">Rack/Enclosure Info<br />&nbsp;Search Criteria</td>
    </tr>
    <tr>
      <td>&nbsp;</td>
    </tr>
    <tr>
      <td>Room</td>
    </tr>
    <tr>
      <td><select name="roomConstraint" class="pulldown">
		 <? if ($_SESSION['roomConstraint']) {
		       echo '<option value="'.$_SESSION['roomConstraint'].'" selected>'.$_SESSION['roomConstraint'].'</option>\n';
			   echo '<option value="">---- All ----</option>\n';
			
			}else {
			   echo '<option value="" selected>---- All ----</option>\n';
			}
			
	        $result = $_SESSION['roomList'];
            //logEntry('debug',"here's my roomList session var ".print_r($result,true)); 
			$roomArray = $result->getArray();
			foreach ($roomArray as $roomEntity) {
	        echo '<option value="'.$roomEntity->getRoom().'">'.$roomEntity->getRoom().'</option>\n';
			}?>
			</select></td>
    </tr>
	<tr>
	  <td>&nbsp;</td>
	</tr>	
    <tr>
      <td>Group Ownership</td>
    </tr>    
    <tr>
      <td><select name="groupNameConstraint" class="pulldown">
		 <? if ($_SESSION['groupNameConstraint']) {
		       echo '<option value="'.$_SESSION['groupNameConstraint'].'" selected>'.$_SESSION['groupNameConstraint'].'</option>\n';
			   echo '<option value="">---- All ----</option>\n';
			
			}else {
			   echo '<option value="" selected>---- All ----</option>\n';
			}
			
	        $result = $_SESSION['groupNameList'];
            //logEntry('debug',"here's my roomList session var ".print_r($result,true)); 
			$groupArray = $result->getArray();
			foreach ($groupArray as $groupNameEntity) {
	        echo '<option value="'.$groupNameEntity->getGroupName().'">'.$groupNameEntity->getGroupName().'</option>\n';
			}?>
			</select></td>
    </tr>
<!--    <tr>
      <td>Building</td>
	</tr>
    <tr>
      <td><select name="buildingConstraint" style="width: 175px">
		 <? //if ($_SESSION['buildingConstraint']) {
		    //   echo '<option value="'.$_SESSION['buildingConstraint'].'" selected>'.$_SESSION['buildingConstraint'].'</option>\n';
			//   echo '<option value="">---- All ----</option>\n';
			
			//}else {
			//   echo '<option value="" selected>---- All ----</option>\n';
			//}
			
	        //$result = $_SESSION['buildingList'];
            //logEntry('debug',"here's my mfgList session var ".print_r($result,true)); 
			//$buildingArray = $result->getArray();
			//foreach ($buildingArray as $buildingEntity) {
	        //echo '<option value="'.$buildingEntity->getBuilding().'">'.$buildingEntity->getBuilding().'</option>\n';
			 
			//}?>
			</select></td>
    </tr>
 -->	<tr>
      <td>&nbsp;</td>
    </tr>
	<tr>
      <td>Rack/Enclosure Name</td>
    </tr>
	<tr>
      <td><input name="nameDesc" type="text" class="textentry" value="<?php echo $_SESSION['nameDesc'] ?>" size="20"></td>
								
    </tr>
 	<tr>
	  <td>&nbsp;</td>
	</tr>
    <tr>
      <td><span class="buttonposition"><input type="submit" value="Search"  class="buttons" style="background:#ffff00">
	      <input type="submit" name="rackReset" value="Reset"  class="buttons" style="background:#ffff00"></span></td>
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
  <tr><td><a class="hyper" href="../top/wd_help.php#racks" target="_blank"><b>Web Desktop Help</b></a></td></tr>
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