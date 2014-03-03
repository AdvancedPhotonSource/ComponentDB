<div class="searchEditCreateNewAOI">


  <script type="text/javascript">

  		function confirm_request(){

			var confirmMessage = new String();
			var aoi_names = new String();
			var aoi_name_machine = new String();
			var aoi_name_techsystem = new String();
			var aoi_name_unique_function = new String();
			var aoi_name_children = new String();

			var selected_button = document.getElementById("aoiEditorSubmit").value;

			aoi_names = " ";

			aoi_name_machine = document.getElementById("aoi_name_machine").value;
			aoi_name_techsystem = document.getElementById("aoi_name_techsystem").value;
			aoi_name_unique_function = document.getElementById("aoi_name_unique_function").value;
			aoi_name_children = document.getElementById("aoi_name_children").value;

			aoi_names = "aoi_" + aoi_name_machine + "_" + aoi_name_techsystem + "_" + aoi_name_unique_function + "_" + aoi_name_children;
			aoi_names = aoi_names.toLowerCase();


			if (selected_button == "logoutAOIEditor")
			{
				confirmMessage = "Are you sure you want to log out of the AOI Editor session?";
				return confirm(confirmMessage);
			}
			if (selected_button == "reset")
			{
				confirmMessage = "Are you sure you want to reset your editing changes?";
				return confirm(confirmMessage);
			}
			if (selected_button == "saveChangesAsNewAOI")
			{
				confirmMessage = "Are you sure you want to create new AOIs named: " + aoi_names + "?";
				return confirm(confirmMessage);
			}
  		}

  		function process_logout(){

			var selected_button = document.getElementById("aoiEditorSubmit");
			selected_button.value = "logoutAOIEditor";
  		}

  		function process_reset(){

			var selected_button = document.getElementById("aoiEditorSubmit");
			selected_button.value = "reset";
  		}

  		function process_save_changes_new_aoi(){

			var selected_button = document.getElementById("aoiEditorSubmit");
			selected_button.value = "saveChangesAsNewAOI";
  		}

 </script>

 <form id="aoiEditor" Method ="Post" action ="action_aoi_edit_tools.php" onsubmit="return confirm_request()">

  		<input type="hidden" name="aoiEditorSubmit" id="aoiEditorSubmit" value=" "/>
<?php
		echo '<table width="100%" border="0" cellspacing="0" cellpadding="2">';

		echo '<tr><th colspan="3">Edit Tools</th></tr><tr>';

		if( $_SESSION['agent'] != md5($_SESSION['user_name'].$_SERVER['HTTP_USER_AGENT']) )
		{

			echo '<td align="center" colspan="3"><a href="https://www.aps.anl.gov/APS_Engineering_Support_Division/Controls/slogin/login.php?app='.$ldap_user_name.'&ctx='.session_name().'">Login AOI Editor</a></td>';

		}else{

      		echo '<td align="center" colspan="1"><input type = "submit" class="editbuttons" name = "saveChangesAsNewAOI" value = "Save to New AOI" onclick="process_save_changes_new_aoi()"/></td>';
			echo '<td align="center" colspan="1"><input type = "submit" class="editbuttons" name = "resetChangesCreateAsNewAOI" value = "Reset Changes" onclick="process_reset()"/></td>';
			echo '<td align="center" colspan="1"><input type = "submit" class="editbuttons" name = "logoutAOIEditor" value = "Logout Editor" onclick="process_logout()"/></td>';

		}

		echo '</tr></table>';

        echo '<table width="100%"  border="1" cellspacing="0" cellpadding="2">';

   		  // User requested to create a new AOI from a blank template

   		  // Reset conditionals for creating or modifying an aoi...
   		  $_SESSION['create_new_aoi_from_blank'] = "false";
		  $_SESSION['create_new_aoi_from_selected'] = "false";
	  	  $_SESSION['modify_selected_aoi'] = "false";

    	  echo '<tr><th class="editOption" colspan="5">Create New AOI - Required Fields</th></tr>';
		  echo '<tr><th class="center" colspan="5">Define AOI General Information</th></tr>';
		  echo '<tr><th class="primary">AOI Name</th>';

    	  echo '<td>Machine: <select class="pulldown" name="machine" id="aoi_name_machine" style="width: 114px">';

		  echo '<option value="" selected>---- Undefined ----</option>\n';

		  $result = $_SESSION['machineList'];
		  $machineArray = $result->getArray();
		  foreach ($machineArray as $machineEntity) {
					echo '<option value="'.$machineEntity->getMachine().'">'.$machineEntity->getMachine().'</option>\n';
		  }
		  echo '</select></td>';

    	  echo '<td> Technical System: <select class="pulldown" name="techsystem" id="aoi_name_techsystem" style="width: 114px">';

		  echo '<option value="" selected>---- Undefined ----</option>\n';

		  $result = $_SESSION['techsystemList'];
		  $technicalsystemArray = $result->getArray();
		  foreach ($technicalsystemArray as $techsystemEntity) {
				echo '<option value="'.$techsystemEntity->getTechSystem().'">'.$techsystemEntity->getTechSystem().'</option>\n';
		  }
		  echo '</select></td>';

		  $parsed_unique_function = "";

		  $parsed_child = "";

		    $_SESSION['aoi_unique_function'] = $parsed_unique_function;
		    $_SESSION['aoi_children'] = $parsed_child;

      	    echo '<td>Unique Function: <input class="textentry" name="unique_function" id="aoi_name_unique_function" type="text" value="'.$_SESSION['aoi_unique_function'].'" size="16"></td>';
      	    echo '<td>Children (Optional): <input class="textentry" name="aoi_children" id="aoi_name_children" type="text" value="'.$_SESSION['aoi_children'].'" size="16"></td>';

	  	    echo '</tr>';

            echo '<tr><td class="primary">Cognizant 1</td><td class="left" colspan = "4"><select class="pulldown" name="cognizant_1_userid">';

			echo '<option value="None" selected>---- None ----</option>\n';

		    $result = $_SESSION['personList'];
		    $personArray = $result->getArray();
			foreach ($personArray as $personEntity) {
				$person_full_name = $personEntity->getLast_nm().", ".$personEntity->getFirst_nm();
				echo '<option value="'.$personEntity->getUserID().'">'.$person_full_name.'</option>\n';
			}
		    echo '</select></td></tr>';

		    echo '<tr><td class="primary">Cognizant 2</td><td class="left" colspan = "4"><select class="pulldown" name="cognizant_2_userid">';

			echo '<option value="None" selected>---- None ----</option>\n';

			$result = $_SESSION['personList'];
			$personArray = $result->getArray();
			foreach ($personArray as $personEntity) {
				$person_full_name = $personEntity->getLast_nm().", ".$personEntity->getFirst_nm();
				echo '<option value="'.$personEntity->getUserID().'">'.$person_full_name.'</option>\n';
			}
		    echo '</select></td></tr>';

            echo '<tr><td class="primary">Customer Group</td><td class="left" colspan = "4"><select class="pulldown" name="customer_group">';

			echo '<option value="No Group" selected>---- None ----</option>\n';

		    $result = $_SESSION['groupNameList'];
		    $groupNameArray = $result->getArray();
			foreach ($groupNameArray as $groupNameEntity) {
				echo '<option value="'.$groupNameEntity->getGroupName().'">'.$groupNameEntity->getGroupName().'</option>\n';
			}
		    echo '</select></td></tr>';

            echo '<tr><td class="primary">Criticality</td><td class="left" colspan = "4"><select class="pulldown" name="criticality_level">';
			echo '<option value="6" selected>---- Undefined ----</option>\n';

		    $result = $_SESSION['criticalityList'];
		    $criticalityArray = $result->getArray();
		    $count = 1;
			foreach ($criticalityArray as $criticalityEntity) {
				$temp = $count." ".$criticalityEntity->getCriticalityClassification();

				if ($count < 6){
					echo '<option value="'.$criticalityEntity->getCriticalityLevel().'">'.$temp.'</option>\n';
				}
				$count = $count + 1;

			}
		    echo '</select></td></tr>';

			echo '<tr rows="3"><A name="description_top"><td class="primary">Description</A><br><font size="2">&nbsp;<A href="#worklog_help">Help Description</A></br></font></td><td class="left" colspan = "4"><textarea name="description" rows="6" cols="60" wrap="hard" value="'.$_SESSION['aoi_description'].'" ></textarea></td></tr>';

            // echo '<tr rows="3"><td class="primary">Description</td><td class="left" colspan = "4"><textarea name="description" rows="3" cols="60" wrap="hard" value="'.$_SESSION['aoi_description'].'" ></textarea></td></tr>';

            echo '<tr><td class="primary">Status</td><td class="left" colspan = "4"><select class="pulldown" name="status">';

			echo '<option value="Undefined" selected>---- Undefined ----</option>\n';

		    $result = $_SESSION['aoiStatusList'];
		    $aoiStatusArray = $result->getArray();
			foreach ($aoiStatusArray as $aoiStatusEntity) {
				echo '<option value="'.$aoiStatusEntity->getAOIStatus().'">'.$aoiStatusEntity->getAOIStatus().'</option>\n';
			}
		    echo '</select></td></tr>';

			echo '<tr><th class="editOption" colspan="5">Optional Fields</th></tr>';


			echo '<tr><td class = "sectionHeaderCbox" align = "center" colspan="5"><b>Define AOI ICMS Keywords</b></td></tr>';


			echo '<tr><td colspan = "5">&nbsp;&nbsp;&nbsp;<input class="textentry" name="keyword" type="text" value="Please enter keywords here..." size="105"></td></tr>';


     		echo '<tr><td class = "sectionHeaderCbox" colspan="5" align = "center"><b> &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Define AOI Worklog&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<A href="#worklog_help">Help Worklog</A></b></td></tr>';
			echo '<A name="worklog_top">&nbsp;</A>';

			// Insert timestamp and user's LDAP login name
			$dt = strftime("__%Y-%m-%d %T");
   			$timestamp_user = "\n".$dt." ".$_SESSION['user_name']."__%%%\n\nPlease enter worklog here...\n";

      		echo '<tr><td class="left" colspan = "5">&nbsp;&nbsp;&nbsp;<textarea name="worklog" rows="5" cols="85" wrap="hard" value="'.$_SESSION['aoi_worklog'].'">'.$timestamp_user.'</textarea></td></tr>';






      		$_SESSION['new_aoi_topdisplay'] = "";

			echo '<tr><td class = "sectionHeaderCbox" align = "center" colspan="5"><b>Define Top MEDM Displays</b></td></tr>';

			echo '<tr><td class="value" colspan = "5">URI</td></tr>';

			echo '<tr><td colspan = "5">Top Display: <input class="textentry" name="new_aoi_topdisplay_1" type="text" value="'.$_SESSION['new_aoi_topdisplay_1'].'" size="101"></td></tr>';
			echo '<tr><td colspan = "5">Top Display: <input class="textentry" name="new_aoi_topdisplay_2" type="text" value="'.$_SESSION['new_aoi_topdisplay_2'].'" size="101"></td></tr>';
			echo '<tr><td colspan = "5">Top Display: <input class="textentry" name="new_aoi_topdisplay_3" type="text" value="'.$_SESSION['new_aoi_topdisplay_3'].'" size="101"></td></tr>';
			// echo '<tr><td colspan = "5">Top Display: <input class="textentry" name="new_aoi_topdisplay_4" type="text" value="'.$_SESSION['new_aoi_topdisplay_4'].'" size="101"></td></tr>';
			// echo '<tr><td colspan = "5">Top Display: <input class="textentry" name="new_aoi_topdisplay_5" type="text" value="'.$_SESSION['new_aoi_topdisplay_5'].'" size="101"></td></tr>';


			$_SESSION['new_aoi_document'] = "";

			echo '<tr><td class = "sectionHeaderCbox" align = "center" colspan= "5"><b>Define Associated Documents</b></td></tr>';
			echo '<tr><td class="value">Document Type</td><td class="value" colspan = "4">URI</td></tr>';

			echo '<tr><td><select class="pulldown" name="new_aoi_doc_type_1">';
			echo '<option value="7" selected>---- Undefined ----</option>\n';
			$result = $_SESSION['aoiDocTypeList'];
			$aoiDocTypeArray = $result->getArray();
			foreach ($aoiDocTypeArray as $aoiDocTypeEntity) {
								echo '<option value="'.$aoiDocTypeEntity->getDocTypeID().'">'.$aoiDocTypeEntity->getDocType().'</option>\n';
			}
		  	echo '</select></td>';
			echo '<td colspan = "5">&nbsp<input class="textentry" name="new_aoi_document_1" type="textarea" value="'.$_SESSION['new_aoi_document_1'].'" size="78"></td></tr>';

			echo '<tr><td><select class="pulldown" name="new_aoi_doc_type_2">';
			echo '<option value="7" selected>---- Undefined ----</option>\n';
			$result = $_SESSION['aoiDocTypeList'];
			$aoiDocTypeArray = $result->getArray();
			foreach ($aoiDocTypeArray as $aoiDocTypeEntity) {
								echo '<option value="'.$aoiDocTypeEntity->getDocTypeID().'">'.$aoiDocTypeEntity->getDocType().'</option>\n';
			}
		  	echo '</select></td>';
			echo '<td colspan = "5">&nbsp<input class="textentry" name="new_aoi_document_2" type="textarea" value="'.$_SESSION['new_aoi_document_2'].'" size="78"></td></tr>';

			echo '<tr><td><select class="pulldown" name="new_aoi_doc_type_3">';
			echo '<option value="7" selected>---- Undefined ----</option>\n';
			$result = $_SESSION['aoiDocTypeList'];
			$aoiDocTypeArray = $result->getArray();
			foreach ($aoiDocTypeArray as $aoiDocTypeEntity) {
								echo '<option value="'.$aoiDocTypeEntity->getDocTypeID().'">'.$aoiDocTypeEntity->getDocType().'</option>\n';
			}
		  	echo '</select></td>';
			echo '<td colspan = "5">&nbsp<input class="textentry" name="new_aoi_document_3" type="textarea" value="'.$_SESSION['new_aoi_document_3'].'" size="78"></td></tr>';

			echo '<tr><td><select class="pulldown" name="new_aoi_doc_type_4">';
			echo '<option value="7" selected>---- Undefined ----</option>\n';
			$result = $_SESSION['aoiDocTypeList'];
			$aoiDocTypeArray = $result->getArray();
			foreach ($aoiDocTypeArray as $aoiDocTypeEntity) {
								echo '<option value="'.$aoiDocTypeEntity->getDocTypeID().'">'.$aoiDocTypeEntity->getDocType().'</option>\n';
			}
		  	echo '</select></td>';
			echo '<td colspan = "5">&nbsp<input class="textentry" name="new_aoi_document_4" type="textarea" value="'.$_SESSION['new_aoi_document_4'].'" size="78"></td></tr>';

			echo '<tr><td><select class="pulldown" name="new_aoi_doc_type_5">';
			echo '<option value="7" selected>---- Undefined ----</option>\n';
			$result = $_SESSION['aoiDocTypeList'];
			$aoiDocTypeArray = $result->getArray();
			foreach ($aoiDocTypeArray as $aoiDocTypeEntity) {
								echo '<option value="'.$aoiDocTypeEntity->getDocTypeID().'">'.$aoiDocTypeEntity->getDocType().'</option>\n';
			}
		  	echo '</select></td>';
			echo '<td colspan = "5">&nbsp<input class="textentry" name="new_aoi_document_5" type="textarea" value="'.$_SESSION['new_aoi_document_5'].'" size="78"></td></tr>';
			echo '</table>';

	echo '<A name="worklog_help">&nbsp;</A>';
	//echo 'DEBUGGING: AOI Create New on Linux...';

	//wikiHelp();
?>

</div>
</form>
