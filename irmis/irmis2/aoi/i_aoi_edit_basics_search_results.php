<div class="searchEditResults">

  <script type="text/javascript">

  		function confirm_request(){

			var confirmMessage = new String();
			var aoi_names = new String();
			var aoi_name_machine = new String();
			var aoi_name_techsystem = new String();
			var aoi_name_unique_function = new String();
			var aoi_name_children = new String();

			aoi_names = " ";

			var selected_button = document.getElementById("aoiEditorSubmit").value;

			var selected_aoi = document.getElementById("passed_aoi_name").value;

			var user_request_new_aoi = document.getElementById("test_new_aoi_from_selected").value;

			if (user_request_new_aoi == "true") {

				aoi_name_machine = document.getElementById("aoi_name_machine").value;
				aoi_name_techsystem = document.getElementById("aoi_name_techsystem").value;
				aoi_name_unique_function = document.getElementById("aoi_name_unique_function").value;
				aoi_name_children = document.getElementById("aoi_name_children").value;

			    aoi_names = "aoi_" + aoi_name_machine + "_" + aoi_name_techsystem + "_" + aoi_name_unique_function + "_" + aoi_name_children;
			    aoi_names = aoi_names.toLowerCase();
			}

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
			if (selected_button == "reset")
			{
				confirmMessage = "Are you sure you want to reset your editing changes?";
				return confirm(confirmMessage);
			}
			if (selected_button == "saveChangesSelectedAOI")
			{
				confirmMessage = "Are you sure you want to save editing changes to selected " + selected_aoi + "?";
				return confirm(confirmMessage);
			}
			if (selected_button == "saveChangesAsNewAOI")
			{
				confirmMessage = "Are you sure you want to create new AOIs named: " + aoi_names + "?";
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

  		function process_reset(){

			var selected_button = document.getElementById("aoiEditorSubmit");
			selected_button.value = "reset";
  		}

		function process_save_changes_selected(){

			var selected_button = document.getElementById("aoiEditorSubmit");
			selected_button.value = "saveChangesSelectedAOI";
  		}

  		function process_save_changes_new_aoi(){

			var selected_button = document.getElementById("aoiEditorSubmit");
			selected_button.value = "saveChangesAsNewAOI";
  		}

 </script>

  <form id="aoiEditor" Method ="Post" action ="action_aoi_edit_tools.php" onsubmit="return confirm_request()">

  		<input type="hidden" name="aoiEditorSubmit" id="aoiEditorSubmit" value=" "/>

<?php
    echo '<table width="100%" border="0" cellspacing="0" cellpadding="2"><tr>';

	if( $_SESSION['agent'] != md5($_SESSION['user_name'].$_SERVER['HTTP_USER_AGENT']) )
		{

			echo '<th class="center">Edit Tools</th></tr>';

			echo '<tr border="1">';
			echo '<td align="center"><a href="https://www.aps.anl.gov/APS_Engineering_Support_Division/Controls/slogin/login.php?app='.$ldap_user_name.'&ctx='.session_name().'">Login to AOI Editor</a></td>';


	}else{

	  // Set up logical variable for javascript to know which option below user has requested, modify existing aoi -- or -- create new from selected aoi


	  if ($_SESSION['create_new_aoi_from_selected'] == "true"){

			echo '<th colspan="3" class="center">Edit Tools</th></tr>';
			echo '<tr>';
      		echo '<td align="center"><input type = "submit" class="editbuttons" name = "saveChangesAsNewAOI" value = "Save As New AOI" onclick="process_save_changes_new_aoi()"/></td>';
			echo '<td align="center"><input type = "submit" class="editbuttons" name = "resetChangesNewFromSelectedAOI" value = "Reset Changes" onclick="process_reset()"/></td>';
			echo '<td align="center"><input type = "submit" class="editbuttons" name = "logoutAOIEditor" value = "Logout AOI Editor" onclick="process_logout()"/></td>';

			echo '<input type="hidden" id="test_new_aoi_from_selected" value="true"/>';
      }

      if ($_SESSION['modify_selected_aoi'] == "true") {

			echo '<th colspan="4" class="center">Edit Tools</th></tr>';
			echo '<tr>';
	    	echo '<td align="center"><input type = "submit" class="editbuttons" name = "saveChangesToSelectedAOI" value = "Save Changes To Selected AOI" onclick="process_save_changes_selected()"/></td>';
	    	echo '<td align="center"><input type = "submit" class="editbuttons" name = "resetChangesModifySelectedAOI" value = "Reset Changes" onclick="process_reset()"/></td>';
	    	echo '<td align="center"><input type = "submit" class="editbuttons" name = "deleteAOI" value = "Delete AOI" onclick="process_delete()"/></td>';
        	echo '<td align="center"><input type = "submit" class="editbuttons" name = "logoutAOIEditor" value = "Logout Editor" onclick="process_logout()"/></td>';

        	echo '<input type="hidden" id="test_new_aoi_from_selected" value="false"/>';
      }

    }

  	echo '</tr></table>';
  	echo '<table width="100%"  border="1" cellspacing="0" cellpadding="2">';


	if ( $_SESSION['aoi_id'] > 0 ){

    	$aoiBasicList = $_SESSION['aoiBasicList'];

    	echo '<tr><td class = "sectionHeaderCbox" align = "center" colspan="5"><b>AOI General Information</b></td></tr>';

    	if ($aoiBasicList->length() == 0) {
		   echo '<tr><th class="center" colspan="4">AOI General Information Is Incomplete</th></tr>';
    	   echo '<tr><th class="center" width=20%>AOI Name</th><th class="value">'.$aoiName.'</th></tr>';
    	   echo '<tr><td class="primary">Description</td><td class="center"></td></tr>';
    	   echo '<tr><td class="primary">Criticality</td><td class="center"></td><td class="primary" width="20%">Customer Group</td><td class="center"></td></tr>';
		   echo '<tr><td class="primary">Cognizant 1</td><td class="center"></td><td class="primary">Cognizant 2</td><td class="center"></td></tr>';
		   echo '<tr><td class="primary">Keyword</td><td class="center"></td><td class="primary">Status</td><td class="center"></td></tr>';
		   echo '<tr><th class="center">Worklog</th></tr>';
		   echo '<tr rows="10"><td class="center"></td></tr>';
		   echo '<tr><th class="center">Top MEDM Displays</th></tr>';
		   echo '<tr><td class="center"></td></tr>';
		   echo '<tr><th class="center">Associated Documents [ICMS]</th></tr>';
		   echo '<tr><td class="center"></td></tr>';
		   echo '<tr><th class="center">AOI Crawler Discovered Relationships</th></tr>';
		   echo '<tr rows="10"><td class="center"></td></tr>';
    	}

    	else {

    	  $aoiBasicEntities = $aoiBasicList->getArray();

    	  foreach ($aoiBasicEntities as $aoiBasicEntity) {

    	     if ($aoiBasicEntity == null) {
    	        echo '<tr><td class="warning bold" colspan=8>ERROR:  Cannot open display for AOI General Information.<br>';
    	     }

    	     else {

    	        $selected_aoi_name = $aoiBasicEntity->getAOIName();
		  		$_SESSION['aoi_name'] = $selected_aoi_name;
		  		$_SESSION['aoi_id'] = $aoiBasicEntity->getAOIID();
              	$_SESSION['aoi_selected'] = "true";


              	echo '<input type="hidden" name="passed_aoi_name" id="passed_aoi_name" value="'.$selected_aoi_name.'"/>';

    	        if ($_SESSION['create_new_aoi_from_selected'] == "true") {
 					echo '<tr><th class="center" rowspan="2">AOI Name</th><th class="value" colspan="4">'.$aoiBasicEntity->getAOIName().'</th></tr>';

    	       		echo '<tr>';
    	       		echo '<td>Machine: <select name="machine" class="pulldown" id="aoi_name_machine" style="width: 114px">';

				    if ($aoiBasicEntity->getAOIMachine()) {
						echo '<option value="'.$aoiBasicEntity->getAOIMachine().'" selected>'.$aoiBasicEntity->getAOIMachine().'</option>\n';
						echo '<option value="">---- None ----</option>\n';

					}else {
						echo '<option value="" selected>---- None ----</option>\n';
					}
			    	$result = $_SESSION['machineList'];
			    	$machineArray = $result->getArray();
					foreach ($machineArray as $machineEntity) {
						echo '<option value="'.$machineEntity->getMachine().'">'.$machineEntity->getMachine().'</option>\n';
					}
			    	echo '</select></td>';

    	        	echo '<td> Technical System: <select name="techsystem" class="pulldown" id="aoi_name_techsystem" style="width: 124px">';
		    		if ($aoiBasicEntity->getAOITechnicalSystem()) {
						echo '<option value="'.$aoiBasicEntity->getAOITechnicalSystem().'" selected>'.$aoiBasicEntity->getAOITechnicalSystem().'</option>\n';
						echo '<option value="">---- None ----</option>\n';
		    		}else {
						echo '<option value="" selected>---- None ----</option>\n';
		    		}
		    		$result = $_SESSION['techsystemList'];
		    		$technicalsystemArray = $result->getArray();
		    		foreach ($technicalsystemArray as $techsystemEntity) {
						echo '<option value="'.$techsystemEntity->getTechSystem().'">'.$techsystemEntity->getTechSystem().'</option>\n';
		    		}
		    		echo '</select></td>';


		    		$parsed_unique_function = "";

            		// Extract from the aoi name the <unique function> field

            		if ( ereg( "(([-a-zA-Z0-9]+_){3}([-a-zA-Z0-9]+){1})", $selected_aoi_name, $captured )) {

	  	  		  		// The zeroth element of the array $captured is set to the entire string being matched against,
	  	  		  		// here, it is the name of the aoi.
	  	  		  		// The first element in array $captured is the substring that matched the first subpattern,
	  	  		  		// the second element is the substring that matched the second subpattern, and so on.
	  	  		  		// $captured[3] should contain the substring for <unique function> field in the aoi name.

	  	  		  		$parsed_unique_function = $captured[3];

	  	  		  		// Need to trim off the underscore at the end of the matched phrase...

	  	  		  		$parsed_unique_function = rtrim($parsed_unique_function, '_');
		    		}

		    		// Extract from the aoi name the <child> field (if any exists)

		    		$parsed_child = "";

		    		if ( ereg( "(([-a-zA-Z0-9]+_){4}([-a-zA-Z0-9]+){1})", $selected_aoi_name, $captured )) {

	  		  			// The zeroth element of the array is set to the entire string being matched against,
	  		  			// here, it is the name of the aoi.
	  		  			// The first element in array $captured is the substring that matched the first subpattern,
	  		  			// the second element is the substring that matched the second subpattern, and so on.
	  		  			// $captured[3] should contain the substring for <child> field in the aoi name.

	  		  			$parsed_child = $captured[3];
		    		}

		    		$_SESSION['aoi_unique_function'] = $parsed_unique_function;
		    		$_SESSION['aoi_children'] = $parsed_child;
		    		$_SESSION['aoi_machine'] = $aoiBasicEntity->getAOIMachine();
		    		$_SESSION['aoi_techsystem'] = $aoiBasicEntity->getAOITechnicalSystem();

      	    		echo '<td>Unique Function: <input class="textentry" style="width:114px" name="unique_function" id="aoi_name_unique_function" type="text" value="'.$_SESSION['aoi_unique_function'].'"></td>';

      	    		echo '<td>Children: <input class="textentry" style="width:114px" name="aoi_children" id="aoi_name_children" type="text" value="'.$_SESSION['aoi_children'].'"></td>';

	  	    		echo '</tr>';

	  	    		// end of create_new_aoi_from_selectd option

	  	    }else{

				echo '<tr><th class="center">AOI Name</th><th class="value" colspan="4">'.$aoiBasicEntity->getAOIName().'</th></tr>';

			}
	  	    // Reset conditionals for creating or modifying an aoi...
	  	    $_SESSION['create_new_aoi_from_selected'] = "false";
	  	    $_SESSION['modify_selected_aoi'] = "false";

            echo '<tr><td class="primary">Cognizant 1</td><td class="left" colspan = "4"><select class="pulldown" name="cognizant_1_userid">';
			if ($aoiBasicEntity->getAOICognizant1LastName()) {

				$cognizant1_full_name = $aoiBasicEntity->getAOICognizant1FirstName()." ".$aoiBasicEntity->getAOICognizant1LastName();

				echo '<option value="'.$aoiBasicEntity->getAOICognizant1UserID().'" selected>'.$cognizant1_full_name.'</option>\n';
				echo '<option value="None">---- None ----</option>\n';

			}else {
				echo '<option value="None" selected>---- None ----</option>\n';
			}
		    $result = $_SESSION['personList'];
		    $personArray = $result->getArray();
			foreach ($personArray as $personEntity) {
				$person_full_name = $personEntity->getLast_nm().", ".$personEntity->getFirst_nm();

				echo '<option value="'.$personEntity->getUserID().'">'.$person_full_name.'</option>\n';
			}
		    echo '</select></td></tr>';

		    echo '<tr><td class="primary">Cognizant 2</td><td class="left" colspan = "4"><select class="pulldown" name="cognizant_2_userid">';


			if ($aoiBasicEntity->getAOICognizant2LastName()) {
				$cognizant2_full_name = $aoiBasicEntity->getAOICognizant2FirstName()." ".$aoiBasicEntity->getAOICognizant2LastName();
				echo '<option value="'.$aoiBasicEntity->getAOICognizant2UserID().'" selected>'.$cognizant2_full_name.'</option>\n';
				echo '<option value="None">---- None ----</option>\n';

			}else {
				echo '<option value="None" selected>---- None ----</option>\n';
			}
			$result = $_SESSION['personList'];
			$personArray = $result->getArray();
			foreach ($personArray as $personEntity) {

				$person_full_name = $personEntity->getLast_nm().", ".$personEntity->getFirst_nm();

				echo '<option value="'.$personEntity->getUserID().'">'.$person_full_name.'</option>\n';
			}
		    echo '</select></td></tr>';


            echo '<tr><td class="primary">Customer Group</td><td class="left" colspan = "4"><select class="pulldown" name="customer_group">';
			if ($aoiBasicEntity->getAOICustomerGroup()) {

				echo '<option value="'.$aoiBasicEntity->getAOICustomerGroup().'" selected>'.$aoiBasicEntity->getAOICustomerGroup().'</option>\n';
				echo '<option value="No Group">---- None ----</option>\n';

			}else {
				echo '<option value="No Group" selected>---- None ----</option>\n';
			}
		    $result = $_SESSION['groupNameList'];
		    $groupArray = $result->getArray();
			foreach ($groupArray as $groupEntity) {

				echo '<option value="'.$groupEntity->getGroupName().'">'.$groupEntity->getGroupName().'</option>\n';
			}
		    echo '</select></td></tr>';

            echo '<tr><td class="primary">Criticality</td><td class="left" colspan = "4"><select class="pulldown" name="criticality_level">';
			if ($aoiBasicEntity->getAOICriticality()) {
				$temp = $aoiBasicEntity->getAOICriticalityLevel()." ".$aoiBasicEntity->getAOICriticalityClassification();
				echo '<option value="'.$aoiBasicEntity->getAOICriticalityLevel().'" selected>'.$temp.'</option>\n';
				echo '<option value="6">---- Undefined ----</option>\n';

			}else {
				echo '<option value="6" selected>---- Undefined ----</option>\n';
			}
		    $result = $_SESSION['criticalityList'];
		    $criticalityArray = $result->getArray();
		    $count = 1;
			foreach ($criticalityArray as $criticalityEntity) {

				$temp = $count." ".$criticalityEntity->getCriticalityClassification();
				if ($count < 6){
					echo '<option value="'.$criticalityEntity->getCriticalityLevel().'">'.$temp.'</option>\n';
				}
				$count = $count +1;
			}
		    echo '</select></td></tr>';

			$temp_description = $aoiBasicEntity->getAOIDescription();
            echo '<tr><A name="description_top"><td class="primary">Description</A><font size="2"><br>&nbsp;<A href="#worklog_help">Help Description</A></br></font></td><td class="left" colspan = "4"><textarea name="description" rows="6" cols="60">'.$temp_description.'</textarea></td></tr>';


			// $temp_description = $aoiBasicEntity->getAOIDescription();
            // echo '<tr><td class="primary">Description</td><td class="left" colspan = "4"><textarea name="description" rows="3" cols="60">'.$temp_description.'</textarea></td></tr>';

            echo '<tr><td class="primary">Keywords</td><td class="left" colspan = "4"><input class="textentry" name="keyword" type="text" value="'.$aoiBasicEntity->getAOIKeyword().'" size="78"></td></tr>';


            echo '<tr><td class="primary">Status</td><td class="left" colspan = "4"><select class="pulldown" name="status">';
			if ($aoiBasicEntity->getAOIStatus()) {
				echo '<option value="'.$aoiBasicEntity->getAOIStatus().'" selected>'.$aoiBasicEntity->getAOIStatus().'</option>\n';
				echo '<option value="Undefined">---- Undefined ----</option>\n';

			}else {
				echo '<option value="Undefined" selected>---- Undefined ----</option>\n';
			}
		    $result = $_SESSION['aoiStatusList'];
		    $aoiStatusArray = $result->getArray();
			foreach ($aoiStatusArray as $aoiStatusEntity) {
				echo '<option value="'.$aoiStatusEntity->getAOIStatus().'">'.$aoiStatusEntity->getAOIStatus().'</option>\n';
			}
		    echo '</select></td></tr>';

		  }
        }

   // AOI Worklog
   echo '<A name="worklog_top">&nbsp;</A>';
   $temp_worklog = $aoiBasicEntity->getAOIWorklog();

   // Insert timestamp and user's LDAP login name
   $dt = strftime("__%Y-%m-%d %T");
   $timestamp_user = "\n".$dt." ".$_SESSION['user_name']."__%%%\n\n\n";

   echo '<tr><td class="primary">Worklog<br><br><font size="2">&nbsp;(Please type new entry<br>&nbsp;&nbsp;under timestamp at top)</br></br></br><br>&nbsp;<A href="#worklog_help">Help Worklog</A></br></font></td><td colspan="4"><textarea name="worklog" rows="10" cols="60">'.$timestamp_user.$temp_worklog.'</textarea></td></tr>';

   $aoiTopdisplayList = $_SESSION['aoiTopdisplayList'];
   $aoiTopdisplayEntity = $_SESSION['aoiTopdisplayEntity'];
   $_SESSION['new_aoi_topdisplay'] = "";

   if ($aoiTopdisplayList->length() == 0) {
	echo '<tr><td colspan=5 class="sectionHeaderCbox" align = "center"><b>No AOI Top MEDM Displays Found</b></td></tr>';
 	echo '<tr><td colspan=5>New Top Display: <input name="new_aoi_topdisplay_1" type="text" value="'.$_SESSION['new_aoi_topdisplay'].'" size="95"></td></tr>';
 	echo '<tr><td colspan=5>New Top Display: <input name="new_aoi_topdisplay_2" type="text" value="'.$_SESSION['new_aoi_topdisplay'].'" size="95"></td></tr>';


   }
   else {
   	echo '<tr><td colspan=5 class = "sectionHeaderCbox" align = "center"><b>Top MEDM Displays</b></td></tr>';

	echo '<tr><td colspan=5 class="value">URI</td></tr>';
      $aoiTopdisplayEntities = $aoiTopdisplayList->getArray();
      echo '<tr><td colspan=5 >New Top Display: <textarea name="new_aoi_topdisplay_1" rows="2" cols="70"></textarea></td></tr>';
      echo '<tr><td colspan=5 >New Top Display: <textarea name="new_aoi_topdisplay_2" rows="2" cols="70"></textarea></td></tr>';

	  $count_top_display = 0;
      foreach ($aoiTopdisplayEntities as $aoiTopdisplayEntity) {
         $count_top_display++;
         $existing_top_display = "existing_top_display_".$count_top_display;
		 $temp_topdisplay_uri = $aoiTopdisplayEntity->getURI();
         echo '<tr><td colspan=5>Existing Top Display: <textarea name="'.$existing_top_display.'" rows="2" cols="70">'.$temp_topdisplay_uri.'</textarea></td></tr>';
      }
   }


    $aoiDocumentList = $_SESSION['aoiDocumentList'];
    $aoiDocumentEntity = $_SESSION['aoiDocumentEntity'];
    $_SESSION['new_aoi_document'] = "";

	$aoiBasicList = $_SESSION['aoiBasicList'];
	$aoiBasicEntities = $aoiBasicList->getArray();
	foreach($aoiBasicEntities as $aoiBasicEntity) {
		$aoiName = $aoiBasicEntity->getAOIName();
	}

    $searchStringICMS = "http://icmsdocs.aps.anl.gov/new_docs/idcplg?IdcService=GET_SEARCH_RESULTS_FORCELOGIN&QueryText=%28+%28+%28xComments+%3Csubstring%3E+%60".$aoiName."%60%29+%29+and+not+dDocType+%3Cmatches%3E+%27CAD_Dependency%27+%29+and++not+dDocType+%3Cmatches%3E+%27Model_Drawing%27&ftx=&AdvSearch=True&search_docs_cb=x&ResultCount=20&SortField=SCORE&SortOrder=Desc";


    if ($aoiDocumentList->length() == 0) {
	 echo '<tr><td class="sectionHeaderCbox" align = "center" colspan="5"><b>No AOI Associated Documents Found in IRMIS</b></td></tr>';


     echo '<tr> <td class="value">Document Type</td><td colspan=4 class="value">URI</td></tr>';
     echo '<tr><td>';
     echo '<select class="pulldown" name="new_aoi_doc_type_1">';
     echo '<option value="7" selected>---- Undefined ----</option>\n';
     $result = $_SESSION['aoiDocTypeList'];
     $aoiDocTypeArray = $result->getArray();
     foreach ($aoiDocTypeArray as $aoiDocTypeEntity) {
				echo '<option value="'.$aoiDocTypeEntity->getDocTypeID().'">'.$aoiDocTypeEntity->getDocType().'</option>\n';
     }
     echo '</select>';
     echo '</td>';

     echo '<td colspan=4 >New Document: <input class="textentry" name="new_aoi_document_1" type="textarea" value="'.$_SESSION['new_aoi_document'].'" size="60"></td></tr>';
	 echo '<tr><td>';
     echo '<select class="pulldown" name="new_aoi_doc_type_2">';
     echo '<option value="7" selected>---- Undefined ----</option>\n';
     $result = $_SESSION['aoiDocTypeList'];
     $aoiDocTypeArray = $result->getArray();
     foreach ($aoiDocTypeArray as $aoiDocTypeEntity) {
				echo '<option value="'.$aoiDocTypeEntity->getDocTypeID().'">'.$aoiDocTypeEntity->getDocType().'</option>\n';
     }
     echo '</select>';
     echo '</td>';

     echo '<td colspan=4 >New Document: <input class="textentry" name="new_aoi_document_2" type="textarea" value="'.$_SESSION['new_aoi_document'].'" size="60"></td></tr>';

	 echo '<tr><td colspan = "5" align="center"><a href="'.$searchStringICMS.'">ICMS Documents</td></tr>';

    }

    else {
      echo '<tr><td class = "sectionHeaderCbox" align = "center" colspan="5">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<b>Associated Documents</b>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<a href="'.$searchStringICMS.'">ICMS Documents</a></td></tr>';
      echo '<tr> <td class="value">Document Type</td><td colspan=4  class="value">URI</td></tr>';
      echo '<tr><td>';
	  echo '<select class="pulldown" name="new_aoi_doc_type_1">';
			echo '<option value="7" selected>---- Undefined ----</option>\n';
			$result = $_SESSION['aoiDocTypeList'];
			$aoiDocTypeArray = $result->getArray();
			foreach ($aoiDocTypeArray as $aoiDocTypeEntity) {
				echo '<option value="'.$aoiDocTypeEntity->getDocTypeID().'">'.$aoiDocTypeEntity->getDocType().'</option>\n';
			}
		  	echo '</select>';

      echo '</td>';
      echo '<td colspan=4 >New Document: <input class="textentry" name="new_aoi_document_1" type="textarea" value="'.$_SESSION['new_aoi_document'].'" size="60"></td></tr>';
	  echo '<tr><td>';
	  echo '<select class="pulldown" name="new_aoi_doc_type_2">';
			echo '<option value="7" selected>---- Undefined ----</option>\n';
			$result = $_SESSION['aoiDocTypeList'];
			$aoiDocTypeArray = $result->getArray();
			foreach ($aoiDocTypeArray as $aoiDocTypeEntity) {
				echo '<option value="'.$aoiDocTypeEntity->getDocTypeID().'">'.$aoiDocTypeEntity->getDocType().'</option>\n';
			}
		  	echo '</select>';

      echo '</td>';
      echo '<td colspan=4 >New Document: <input class="textentry" name="new_aoi_document_2" type="textarea" value="'.$_SESSION['new_aoi_document'].'" size="60"></td></tr>';

      // Display already existing documents for this aoi, allow user to change

      $aoiDocumentEntities = $aoiDocumentList->getArray();
	  $count_existing_documents = 0;

      foreach ($aoiDocumentEntities as $aoiDocumentEntity) {
      	 $count_existing_documents++;

         $doc_type = $aoiDocumentEntity->getDoctype();
         $doc_type_id = $aoiDocumentEntity->getDocTypeID();

         $existing_doc_type = "existing_doc_type_".$count_existing_documents;

         $doc_uri = $aoiDocumentEntity->getURI();
         $existing_doc_uri = "existing_doc_uri_".$count_existing_documents;

		 echo '<td>';
         echo '<select class="pulldown" name="'.$existing_doc_type.'">';
		 echo '<option value="'.$doc_type_id.'" selected>'.$doc_type.'</option>\n';

		 $result = $_SESSION['aoiDocTypeList'];
		 $aoiDocTypeArray = $result->getArray();

		 foreach ($aoiDocTypeArray as $aoiDocTypeEntity) {
		 				echo '<option value="'.$aoiDocTypeEntity->getDocTypeID().'">'.$aoiDocTypeEntity->getDocType().'</option>\n';
		 }
		 echo '</select>';
		 echo '</td>';
		 echo '<td colspan=4 >Existing Document: <textarea name="'.$existing_doc_uri.'" type="textarea" rows="2" cols="45">'.$doc_uri.'</textarea></td></tr>';

		 // Reset loop variables
		 $doc_type = "";
		 $doc_type_id = 0;
	  }

    }


      }
   }
?>
</form>
</table>

<?php
	echo '<A name="worklog_help">&nbsp;</A>';
	wikiHelp();
?>
</div>

