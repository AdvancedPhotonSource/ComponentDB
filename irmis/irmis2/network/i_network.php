<link href="../common/irmis2.css" rel="stylesheet" type="text/css">
<div id="criteria" style="position:absolute; left:0px; top:90px; width:160px; height:460px; z-index:2; background-color: #dddddd; border: 1px none #000000;">
  <table width="98%"  border="0" align="right" cellpadding="2" cellspacing="0">
    <tr>
      <td><h4>IOC Search Criteria</h4></td>
    </tr>
    <tr>
      <td></td>
    </tr>
    <tr>
      <td>IOC Name</td>
    </tr>
    <tr>
      <td><input name="iocNameConstraint" type="text" value="<?php echo $_SESSION['iocNameConstraint'] ?>" size="20"></td>
    </tr>
    <tr>
      <td>&nbsp;</td>
    </tr>
    <tr>
      <td>System</td>
	</tr>
    <tr>
      <td><select name="iocSystemConstraint">
                                      <?
										$query="select distinct system from ioc order by system";
                                        $result=mysql_query($query);
                                        if ($result) {
                                        ?>
                                        <option value="" selected>---- All ----</option>
										<?
                                        while ($row=mysql_fetch_array($result)) {
                                        ?>
									    <option value="<?=($row['system'])?>"><?=$row['system']?><? $iocSystemConstraint = $_SESSION['iocSystemConstraint'];?></option>
									    <?php
                                       }
                                      }
                                     ?>
					                 </select> 
    </tr>
    <tr>
      <td>&nbsp;</td>
    </tr>
	<tr>
      <td>Cognizant Technician</td>
    </tr>
    <tr>
      <td><select name="cogTech">
                                      <?
										$query="select distinct person.last_nm, person.person_id, aps_ioc.cog_technician_id from person, aps_ioc where person_id=cog_technician_id order by last_nm";
                                        $result=mysql_query($query);
                                        if ($result) {
                                        ?>
                                        <option value="" selected>---- All ----</option>
										<?
                                        while ($row=mysql_fetch_array($result)) {
                                        ?>
									    <option value="<?=($row['last_nm'])?>"><?=$row['last_nm']?><? $cogTech = $_SESSION['cogTech'];?></option>
									    <?php
                                       }
                                      }
                                     ?>
									 </select></td>
								
    </tr>
	<tr>
      <td>&nbsp;</td>
    </tr>
	<tr>
      <td>Cognizant Developer</td>
    </tr>
	<tr>
      <td><select name="cogDeveloper">
                                      <?
										$query="select distinct person.last_nm, person.person_id, aps_ioc.cog_developer_id from person, aps_ioc where person_id=cog_developer_id order by last_nm";
                                        $result=mysql_query($query);
                                        if ($result) {
                                        ?>
                                        <option value="" selected>---- All ----</option>
										<?
                                        while ($row=mysql_fetch_array($result)) {
                                        ?>
									    <option value="<?=($row['last_nm'])?>"><?=$row['last_nm']?><? $cogDeveloper = $_SESSION['cogDeveloper'];?></option>
									    <?php
                                       }
                                      }
                                     ?>
									 </select></td>
								
    </tr>
	<tr>
      <td>&nbsp;</td>
    </tr>
	<tr>
      <td>&nbsp;</td>
    </tr>
    <tr>
      <td><input type="submit" name="IOC Search" value="IOC Search" style="background:#ffff00"></td>
    </tr>
  </table>
  <p>&nbsp;</p>
</div>