<div class="searchBlankEditResults">

<form id="aoiEditor" Method ="Post" action ="action_aoi_edit_tools.php">
		<table width="100%" border="0" cellspacing="0" cellpadding="2">
		<tr><th colspan="2">Edit Tools</th></tr>

		<tr>
<?php
		if( $_SESSION['agent'] == md5($_SESSION['user_name'].$_SERVER['HTTP_USER_AGENT']) )
		{
       		echo '<td align="center"><input type = "submit" class="editbuttons" name = "newAOIfromBlank" value = "Create New AOI from Blank Template"/></td>';
      		echo '<td align="center"><input type = "submit" class="editbuttons" name = "logoutAOIEditor" value = "Logout Editor"/></td>';
		}else{

			echo '<td align="center"><a href="https://www.aps.anl.gov/APS_Engineering_Support_Division/Controls/slogin/login.php?app='.$ldap_user_name.'&ctx='.session_name().'">Login AOI Editor</a></td>';

		}
?>
  		</tr>
		</table>
</form>

  <table width="100%"  border="1" cellspacing="0" cellpadding="2">
<?php

  	    echo '<tr><th colspan="4" class="center">AOI General Information</th></tr>';
	    echo '<tr><th colspan="1" class="center">AOI Name</th><th colspan="3" class="valueitalic">Select AOI Name From Results</th></tr>';

    	echo '<tr><td colspan="1" class="primary" width="20%">Description</td><td colspan="3" class="center">&nbsp;</td></tr>';
    	echo '<tr><td colspan="1" class="primary">Criticality</td><td class="center" colspan="1" width="30%">&nbsp;</td><td class="primary" colspan="1" width="20%">Customer Group</td><td class="center" colspan="1">&nbsp;</td></tr>';
		echo '<tr><td class="primary">Cognizant 1</td><td class="center">&nbsp;</td><td class="primary">Cognizant 2</td><td class="center">&nbsp;</td></tr>';
		echo '<tr><td class="primary">ICMS Keywords</td><td class="center">&nbsp;</td><td class="primary">Status</td><td class="center">&nbsp;</td></tr>';
		echo '<tr><th colspan="4" class="center">Worklog</th></tr>';
		echo '<tr><td colspan="4" class="center">&nbsp;</td></tr>';
		echo '<tr><th colspan="4" class="center">Top MEDM Displays</th></tr>';
		echo '<tr><td colspan="4" class="center">&nbsp;</td></tr>';
		echo '<tr><th colspan="4" class="center">Associated Documents</th></tr>';
		echo '<tr><td colspan="4" class="center">Integrated Content Management System (ICMS)</td></tr>';
		echo '<tr><th colspan="4" class="center">AOI Crawler Discovered Relationships</th></tr>';
		echo '<tr><td class="center">Process Variables</td><td colspan="1" class="center">st.cmd Lines</td><td  colspan="1" class="center">Revision History</td><td colspan="1" class="center">User Programmable Components (UPC)</td></tr>';

?>
</table>
</div>

