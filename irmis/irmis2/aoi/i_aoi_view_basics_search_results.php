<div class="searchResults">

<script type="text/javascript">

  		function confirm_request(){

			var confirmMessage = new String();

			var selected_button = document.getElementById("aoiEditorSubmit").value;

			var selected_aoi = document.getElementById("passed_aoi_name").value;

			if (selected_button == "deleteAOI")
			{
				confirmMessage = "Are you sure you want to delete " + selected_aoi + "?";
				return confirm(confirmMessage);
			}

			if (selected_button == "logoutAOIEditor")
			{
				confirmMessage = "Are you sure you want to log out of the AOI Editor session?";
				return confirm(confirmMessage);
			}
  		}

  		function process_delete(){

			var selected_button = document.getElementById("aoiEditorSubmit");
			selected_button.value = "deleteAOI";
  		}

  		function process_logout(){

			var selected_button = document.getElementById("aoiEditorSubmit");
			selected_button.value = "logoutAOIEditor";
  		}

 </script>

 <form id="aoiEditor" Method ="Post" action ="action_aoi_edit_tools.php" onsubmit="return confirm_request()">

 <?php

 		echo '<input type="hidden" name="aoiEditorSubmit" id="aoiEditorSubmit" value=" "/>';

		echo '<table width="99%" border="0" cellspacing="0" cellpadding="2">';
		echo '<tr><th colspan="7">Edit Tools</th></tr>';


		if( $_SESSION['agent'] != md5($_SESSION['user_name'].$_SERVER['HTTP_USER_AGENT']) )
		{

			echo '<tr><td align="center"><a href="https://www.aps.anl.gov/APS_Engineering_Support_Division/Controls/slogin/login.php?app='.$ldap_user_name.'&ctx='.session_name().'">Login AOI Editor</a></td></tr>';


		}else{

			echo '<tr rowspan="2">';
			echo '<td align="center"><input type = "submit" class="editbuttons" name = "newAOIfromBlank" value = "New AOI From Blank"/></td>';
			echo '<td align="center"><input type = "submit" class="editbuttons" name = "newAOIfromSelected" value = "New AOI From Selected"/></td>';
       		echo '<td align="center"><input type = "submit" class="editbuttons" name = "modifyAOI" value = "Modify AOI"/></td>';
      		echo '<td align="center"><input type = "submit" class="editbuttons" name = "deleteAOI" value = "Delete AOI" onclick="process_delete()"/></td>';
      		echo '<td align="center"><input type = "submit" class="editbuttons" name = "logoutAOIEditor" value = "Logout Editor" onclick="process_logout()"/></td>';

  			echo '</tr>';
  		}

		echo '</table>';
  		echo '<table width="99%"  border="1" cellspacing="0" cellpadding="2" align="left">';


    $aoiBasicList = $_SESSION['aoiBasicList'];
    $selected_aoi_name = "";

    if ($aoiBasicList->length() == 0) {
	   echo '<tr><th colspan="4" class=center>AOI General Information Is Incomplete</th></tr>';
       echo '<tr><th class="center" colspan="1">AOI Name</th><th class="value" colspan = "3">'.$_SESSION['aoi_name'].'</th></tr>';

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

    }

    else {

      $aoiBasicEntities = $aoiBasicList->getArray();

      foreach ($aoiBasicEntities as $aoiBasicEntity) {

         if ($aoiBasicEntity == null) {
            echo '<tr><td class="warning bold" colspan=8>ERROR:  Cannot open display for AOI General Information.<br>';
         }

         else {

            echo '<tr><th colspan="4" class="center">AOI General Information</th></tr>';
            echo '<tr><th class="center" width=20%>AOI Name</th><th class="value" colspan="3">'.$aoiBasicEntity->getAOIName().'</th></tr>';

            $temp_description = $aoiBasicEntity->getAOIDescription();
			$temp_description = getWikiHtml($temp_description);
			echo '<tr><td class="primary" colspan="1">Description</td><td class="left" colspan="3">'.$temp_description.'</td></tr>';

            echo '<tr><td class="primary" colspan="1">Criticality</td><td class="left" width="30%">'.$aoiBasicEntity->getAOICriticalityLevel()." ".$aoiBasicEntity->getAOICriticalityClassification().'</td>';
            echo '<td class="primary" colspan="1" width="20%">Customer Group</td><td class="left" colspan="1">'.$aoiBasicEntity->getAOICustomerGroup().'</td></tr>';
			echo '<tr><td class="primary" colspan="1">Cognizant 1</td><td class="left" colspan="1">'.$aoiBasicEntity->getAOICognizant1FirstName()." ".$aoiBasicEntity->getAOICognizant1LastName().'</td>';
	        echo '<td class="primary" colspan="1">Cognizant 2</td><td class="left" colspan="1">'.$aoiBasicEntity->getAOICognizant2FirstName()." ".$aoiBasicEntity->getAOICognizant2LastName().'</td></tr>';

            echo '<tr><td class="primary" colspan="1">ICMS Keywords</td><td class="left" colspan="1">'.$aoiBasicEntity->getAOIKeyword().'</td>';
            echo '<td class="primary" colspan="1">Status</td><td class="left" colspan="1">'.$aoiBasicEntity->getAOIStatus().'</td></tr>';
            echo '<tr><th class="center" colspan="4">Worklog</th></tr>';

			// Convert worklog to html friendly output
            $temp_worklog = $aoiBasicEntity->getAOIWorklog();
            $temp_worklog = getWikiHtml($temp_worklog);
			echo '<tr><td class="left" colspan="4">'.$temp_worklog.'</td></tr>';

            $selected_aoi_name = $aoiBasicEntity->getAOIName();
         }
      }
   }

   echo '<input type="hidden" name="passed_aoi_name" id="passed_aoi_name" value="'.$selected_aoi_name.'"/>';


?>
</table>
</form>
</div>

