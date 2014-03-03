<form action="action_plc_search.php" method="POST">
<div class="searchCriteria">
  <table width="98%" align="right" cellpadding="2" cellspacing="0">
    <tr>
      <td class="titleheading">PLC Info<br />&nbsp;Search Criteria</td>
    </tr>
    <tr>
      <td>&nbsp;</td>
    </tr>
    <tr>
      <td>PLC Name</td>
    </tr>
    <tr>
      <td><input name="plcNameConstraint" class="textentry" type="text" value="<?php echo $_SESSION['plcNameConstraint'] ?>" size="20"></td>
    </tr>
    <tr>
      <td>&nbsp;</td>
    </tr>
	<tr>
      <td><span class="buttonposition"><input type="submit" value="Search"  class="buttons" style="background:#ffff00">
	      <input type="submit" name="plcReset" value=" Reset "  class="buttons" style="background:#ffff00"></span></td>
    </tr>
    <tr>
      <td>&nbsp;</td>
    </tr>
    <tr>
      <td>&nbsp;</td>
    </tr>
    <tr>
      <td><input type="submit" class="buttonswide" name="plcAdmin" value="Administrative Page" style="background:#ffff00"></td>
    </tr>
	<tr>
      <td>&nbsp;</td>
    </tr>
    <tr>
	<tr>
      <td>&nbsp;</td>
    </tr>
	<tr>
      <td>&nbsp;</td>
    </tr>
  </table>
  <div id="help">
  <table>
  <tr><td><a class="hyper" href="../top/wd_help.php#plc" target="_blank"><b>Web Desktop Help</b></a></td></tr>
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
