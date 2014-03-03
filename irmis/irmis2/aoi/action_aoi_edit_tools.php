<?php

    /* AOI Edit Tools Action handler
     * Perform AOI database editing actions requested by user:
     *
     * 1. make editing changes to an existing AOI (AOI name is allowed to change)
     *
     * 2. delete an existing AOI (and all of its children if they exist)
     *
     * 3. create a new AOI and populate corresponding AOI tables (create new AOI name)
     *
     * 4. create new children AOIs
     *
     * 5. disregard all edits made by the user and re-display selected AOI or show
     *    blank basic information fields for no-selected AOI (if they were going to create
     *    a new AOI and changed her or his mind)
     */

	 include_once('i_common.php');

	 include_once('remove_keywords.php');

	 $names_to_delete;
	 $soap_fault = false;


	 if( $_SESSION['agent'] != md5($_SESSION['user_name'].$_SERVER['HTTP_USER_AGENT']) )
	 {
	 			echo '<br>Invalid access attempt to AOI editor tools.</br>';
				include('aoi_edit.php');
	 			exit;
	 }

	 // Retrieve IRMIS read/write user dbconnect credentials

	 // Establish connection to db (note: actually re-uses pooled connections)
	 // args are: host, port, dbname, user, passwd, tableNamePrefix

	 $dbConnManager = new DBConnectionManager($db_host,$db_port,$db_name_production_1,$db_user_aoi_read_write_name,$db_user_aoi_read_write_passwd,"");

	 if (!$conn = $dbConnManager->getConnection()) {
	  		include('../common/db_error.php');
	 		echo "<br>Could not connect to database. Please see IRMIS administrator.</br>";
	 		exit;
	 }else{
	 		// echo "<br>Connected to AOI Database on bacchus</br>";
	 }

	 	// Capture all user data input fields

	 	$user_machine = mysql_real_escape_string($_POST['machine']);
	 	$user_techsystem = mysql_real_escape_string($_POST['techsystem']);
	 	$user_uniquefunction = mysql_real_escape_string($_POST['unique_function']);
	 	$user_children = mysql_real_escape_string($_POST['aoi_children']);
	 	$user_cognizant_1 = mysql_real_escape_string($_POST['cognizant_1_userid']);
	 	$user_cognizant_2 = mysql_real_escape_string($_POST['cognizant_2_userid']);
	 	$user_customergroup = mysql_real_escape_string($_POST['customer_group']);
	 	$user_criticality_level = mysql_real_escape_string($_POST['criticality_level']);
	 	$user_description = mysql_real_escape_string($_POST['description']);
	 	$user_worklog = mysql_real_escape_string($_POST['worklog']);
	 	$user_keyword = mysql_real_escape_string($_POST['keyword']);
	 	$user_status = mysql_real_escape_string($_POST['status']);

	 	$parent_worklog = $user_worklog;

	 	$user_newaoitopdisplay_1 = mysql_real_escape_string($_POST['new_aoi_topdisplay_1']);
	 	$user_newaoitopdisplay_2 = mysql_real_escape_string($_POST['new_aoi_topdisplay_2']);
	 	$user_newaoitopdisplay_3 = mysql_real_escape_string($_POST['new_aoi_topdisplay_3']);
	 	$user_newaoitopdisplay_4 = mysql_real_escape_string($_POST['new_aoi_topdisplay_4']);
	 	$user_newaoitopdisplay_5 = mysql_real_escape_string($_POST['new_aoi_topdisplay_5']);

	    $user_newtopdisplay_array = array( 1=>$user_newaoitopdisplay_1,
								  $user_newaoitopdisplay_2,
								  $user_newaoitopdisplay_3,
								  $user_newaoitopdisplay_4,
								  $user_newaoitopdisplay_5);

		// Capture possible changes to the existing MEDM top display uri's
		$user_existingtopdisplay_array = array();

		$aoiTopdisplayList = $_SESSION['aoiTopdisplayList'];
		$aoiTopdisplayEntity = $_SESSION['aoiTopdisplayEntity'];
		if($aoiTopdisplayList->length() != 0){

			$aoiTopdisplayEntities = $aoiTopdisplayList->getArray();
			$count_top_displays = 0;
			foreach ($aoiTopdisplayEntities as $aoiTopdisplayEntity){

				$count_top_displays++;
				$existing_top_display = "existing_top_display_".$count_top_displays;
				//echo '<br>existing top display uri:'.$existing_top_display.'</br>';

				$user_existingtopdisplay_array[] = mysql_real_escape_string($_POST[$existing_top_display]);
				//echo '<br>after post, existing top display is:'.$user_existingtopdisplay_array[$count_top_displays - 1].'</br>';
			}
		}


	 	$user_newaoidoctype_1 = mysql_real_escape_string($_POST['new_aoi_doc_type_1']);
	 	$user_newaoidocument_1 = mysql_real_escape_string($_POST['new_aoi_document_1']);
	 	$user_newaoidoctype_2 = mysql_real_escape_string($_POST['new_aoi_doc_type_2']);
		$user_newaoidocument_2 = mysql_real_escape_string($_POST['new_aoi_document_2']);
		$user_newaoidoctype_3 = mysql_real_escape_string($_POST['new_aoi_doc_type_3']);
		$user_newaoidocument_3 = mysql_real_escape_string($_POST['new_aoi_document_3']);
		$user_newaoidoctype_4 = mysql_real_escape_string($_POST['new_aoi_doc_type_4']);
		$user_newaoidocument_4 = mysql_real_escape_string($_POST['new_aoi_document_4']);
	 	$user_newaoidoctype_5 = mysql_real_escape_string($_POST['new_aoi_doc_type_5']);
		$user_newaoidocument_5 = mysql_real_escape_string($_POST['new_aoi_document_5']);

		$user_newaoidoctype_array = array( 1=>$user_newaoidoctype_1,
								  $user_newaoidoctype_2,
								  $user_newaoidoctype_3,
								  $user_newaoidoctype_4,
								  $user_newaoidoctype_5);

		$user_newaoidocument_array = array( 1=>$user_newaoidocument_1,
								   $user_newaoidocument_2,
								   $user_newaoidocument_3,
								   $user_newaoidocument_4,
								   $user_newaoidocument_5);


		// echo '<br>user new topdisplay is: '.$user_newaoitopdisplay_1.'</br>';
		// echo '<br>user new doc type is: '.$user_newaoidoctype_1.'</br>';
		// echo '<br>user new document file name is: '.$user_newaoidocument_1.'</br>';

		// Capture possible changes to existing associated aoi documents
		$user_existing_doc_type_array = array();
		$user_existing_doc_uri_array = array();

		$aoiDocumentList = $_SESSION['aoiDocumentList'];
		// $aoiDocumentEntity = $_SESSION['aoiDocumentEntity'];

		if ($aoiDocumentList->length() != 0) {
			$aoiDocumentEntities = $aoiDocumentList->getArray();
			$count_documents = 0;
			foreach ($aoiDocumentEntities as $aoiDocumentEntity) {
				$count_documents++;
				$existing_doc_type  = "existing_doc_type_".$count_documents;
				$existing_doc_uri = "existing_doc_uri_".$count_documents;
				$user_existing_doc_type_array[] = mysql_real_escape_string($_POST[$existing_doc_type]);
				$user_existing_doc_uri_array[] = mysql_real_escape_string($_POST[$existing_doc_uri]);
				// echo '<br>arguments to post, existing doc type: '.$existing_doc_type.' and existing doc uri: '.$existing_doc_uri.'</br>';

				// echo '<br>after post, existing doc type id and doc uri: '.$user_existing_doc_type_array[$count_documents - 1].'&nbsp;'.$user_existing_doc_uri_array[$count_documents - 1].'</br>';
			}
		}


     // Handle appropriate form request

	 //User request to create a new AOI
	 if($_POST['newAOIfromBlank'] )
	 {

	 	$_SESSION['create_new_aoi_from_blank'] = "true";
	 	$_SESSION['aoi_id'] = 0;
	 	$_SESSION['aoi_name'] = "";
		$_SESSION['aoi_selected'] = "false";
		include('aoi_edit_basic_search_results.php');

	 }

	 if($_POST['newAOIfromSelected'])
	 {
	 	// echo 'user asked for new aoi from selected';
	 	$_SESSION['create_new_aoi_from_selected'] = "true";
	 	include('aoi_edit_basic_search_results.php');
	 }

	 if($_POST['modifyAOI'])
	 {
	 	$_SESSION['modify_selected_aoi'] = "true";
	 	include('aoi_edit_basic_search_results.php');
	 }

	 //User request to delete existing AOI (or delete multiple AOIs if child AOIs exist too)
	 if($_POST['deleteAOI'] )
	 {

		// Get aoi_id that is to be deleted
		$delete_aoi_id = $_SESSION['aoi_id'];
		$delete_aoi_name = $_SESSION['aoi_name'];

		$delete_parent = "false";

		// Determine if a child or a parent AOI has been selected
		if ( ereg( "(([-a-zA-Z0-9]+_){4})", $delete_aoi_name)){
		     	// echo 'Child AOI has been selected to be deleted';
		}else{
				// echo 'Parent AOI has been selected to be deleted';
				$delete_parent = "true";
		}


			    // THE USER HAS CHOSEN TO DELETE AN EXISTING RECORD
            	// WILL NEED TO DELETE CHILDREN AOIS AS WELL IF A PARENT AOI WAS SELECTED FOR DELETION

	    		// DELETE FOREIGN KEY TABLES FIRST, THEN MAIN AOI TABLE
	    		// TABLES TO BE MODIFIED INCLUDE:
	    		//
	    		//		aoi_document
	    		//		aoi_topdisplay
	    		//      aoi_epics_record
	    		//		aoi_ioc_stcmd_line
	    		//		aoi_criticality
	    		//		aoi_plc_stcmd_line
	    		//		aoi_machine
	    		//		aoi_techsys
	    		//      aoi



            if (($delete_aoi_id > 0) && ($_SESSION['aoi_selected'] == "true")){

	    		$aoi_document_delete = "delete from aoi_document where aoi_id = '".$delete_aoi_id."'";
	    		$result = mysql_query($aoi_document_delete);
	    		echo mysql_error();
	    		$commit_query = "commit";
            	$result = mysql_query($commit_query);

	    		$aoi_topdisplay_delete = "delete from aoi_topdisplay where aoi_id = '".$delete_aoi_id."'";
	    		$result = mysql_query($aoi_topdisplay_delete);
	    		echo mysql_error();
	    		$commit_query = "commit";
            	$result = mysql_query($commit_query);

	    		$aoi_criticality_delete = "delete from aoi_criticality where aoi_id = '".$delete_aoi_id."'";
	    		$result = mysql_query($aoi_criticality_delete);
	    		echo mysql_error();
	    		$commit_query = "commit";
            	$result = mysql_query($commit_query);

            	$aoi_machine_delete = "delete from aoi_machine where aoi_id = '".$delete_aoi_id."'";
            	$result = mysql_query($aoi_machine_delete);
            	echo mysql_error();
            	$result = mysql_query($commit_query);
            	echo mysql_error();

	    		$aoi_techsys_delete = "delete from aoi_techsys where aoi_id = '".$delete_aoi_id."'";
	    		$result = mysql_query($aoi_techsys_delete);
	    		echo mysql_error();
	    		$result = mysql_query($commit_query);
	    		echo mysql_error();

	    		// Use two separate queries to perform final deletion of records from table aoi_epics_record
	    		// First, retrieve list of aoi_ioc_stcmd_line_id for this aoi

	    		$get_aoi_ioc_stcmd_line_ids = "select aoi_ioc_stcmd_line_id from aoi_ioc_stcmd_line where aoi_id = '".$delete_aoi_id."'";
	    		$result = mysql_query($get_aoi_ioc_stcmd_line_ids);
	    		echo mysql_error();

	    		// Next, perform delete on corresponding records in table aoi_epics_record

	    		while ($stcmd_line_id_row = mysql_fetch_array($result)) {
	    			   $delete_aoi_ioc_stcmd_line_id = $stcmd_line_id_row['aoi_ioc_stcmd_line_id'];
	    			   $aoi_epics_record_delete = "delete from aoi_epics_record where aoi_ioc_stcmd_line_id = '".$delete_aoi_ioc_stcmd_line_id."'";
	    			   $loop_delete_result = mysql_query($aoi_epics_record_delete);
	    			   echo mysql_error();
	    			   $loop_commit_result = mysql_query($commit_query);
	    			   echo mysql_error();
	    		}

	    		$aoi_ioc_delete = "delete from aoi_ioc_stcmd_line where aoi_id = '".$delete_aoi_id."'";
	    		$result = mysql_query($aoi_ioc_delete);
	    		echo mysql_error();
	    		$result = mysql_query($commit_query);
	    		echo mysql_error();

	    		$aoi_plc_delete = "delete from aoi_plc_stcmd_line where aoi_id = '".$delete_aoi_id."'";
	    		$result = mysql_query($aoi_plc_delete);
	    		echo mysql_error();
	    		$result = mysql_query($commit_query);
	    		echo mysql_error();

	    		$aoi_relation_delete = "delete from aoi_relation where aoi1_id = '".$delete_aoi_id."' or aoi2_id = '".$delete_aoi_id."'";
	    		$result = mysql_query($aoi_ioc_delete);
	    		echo mysql_error();
	    		$result = mysql_query($commit_query);
	   	 		echo mysql_error();

	    		$aoi_delete = "delete from aoi where aoi_id  = '".$delete_aoi_id."'";
            	$result = mysql_query($aoi_delete);
            	echo mysql_error();
	    		$result = mysql_query($commit_query);
	    		echo mysql_error();

				$names_to_delete[] = $delete_aoi_name;

	    		// Need to delete all children aois as well if a parent aoi was deleted in step above

	    		if( $delete_parent == "true" && $delete_aoi_name != ""){

        			$derived_name = $delete_aoi_name;

	    			$compare_children_aoi_name = $derived_name."_";

	    			//echo "compare children aoi name to: $compare_children_aoi_name";

	    			$aoi_children_query = "select aoi_id, aoi_name from aoi where aoi_name like '".$compare_children_aoi_name."%'";

	    			$child_result = mysql_query($aoi_children_query);


					if (!$child_result) {
					   echo 'Could not run query: ' . mysql_error();
					   exit;
					}


	    			while ( $row = mysql_fetch_array($child_result) ){
	    			    echo mysql_error();

					    //echo "<br>about to delete aoi name : ".$row['aoi_name']."</br>";

		    			$child_aoi_id = $row['aoi_id'];
		    			/*
						*** sbohr code
						*/
		    			$child_aoi_name = $row['aoi_name'];
		    			$names_to_delete[] = $child_aoi_name;
		    			/*
						*** sbohr code
						*/

		    			$aoi_document_delete = "delete from aoi_document where aoi_id = '".$child_aoi_id."'";
		    			$result = mysql_query($aoi_document_delete);
		    			//echo mysql_error();
		    			$commit_query = "commit";
		    			$result = mysql_query($commit_query);

		    			$aoi_topdisplay_delete = "delete from aoi_topdisplay where aoi_id = '".$child_aoi_id."'";
		    			$result = mysql_query($aoi_topdisplay_delete);
		    			//echo mysql_error();
						$commit_query = "commit";
						$result = mysql_query($commit_query);

						$aoi_criticality_delete = "delete from aoi_criticality where aoi_id = '".$child_aoi_id."'";
						$result = mysql_query($aoi_criticality_delete);
						//echo mysql_error();
						$commit_query = "commit";
						$result = mysql_query($commit_query);

						// Use two separate queries to perform final deletion of records from table aoi_epics_record
					    // First, retrieve list of aoi_ioc_stcmd_line_id for this "child" aoi

						$get_aoi_ioc_stcmd_line_ids = "select aoi_ioc_stcmd_line_id from aoi_ioc_stcmd_line where aoi_id = '".$child_aoi_id."'";
						$result = mysql_query($get_aoi_ioc_stcmd_line_ids);
					    echo mysql_error();

					    // Next, perform delete on corresponding records in table aoi_epics_record

						while ($stcmd_line_id_row = mysql_fetch_array($result)) {
							   $delete_aoi_ioc_stcmd_line_id = $stcmd_line_id_row['aoi_ioc_stcmd_line_id'];
							   $aoi_epics_record_delete = "delete from aoi_epics_record where aoi_ioc_stcmd_line_id = '".$delete_aoi_ioc_stcmd_line_id."'";
							   $loop_delete_result = mysql_query($aoi_epics_record_delete);
							   echo mysql_error();
							   $loop_commit_result = mysql_query($commit_query);
							   echo mysql_error();
	    		        }

						$aoi_ioc_stcmd_line_delete = "delete from aoi_ioc_stcmd_line where aoi_id = '".$child_aoi_id."'";
						$result = mysql_query($aoi_ioc_stcmd_line_delete);
						//echo mysql_error();
						$commit_query = "commit";
						$result = mysql_query($commit_query);

						$aoi_machine_delete = "delete from aoi_machine where aoi_id = '".$child_aoi_id."'";
						$result = mysql_query($aoi_machine_delete);
						//echo mysql_error();
						$result = mysql_query($commit_query);
						//echo mysql_error();

						$aoi_techsys_delete = "delete from aoi_techsys where aoi_id = '".$child_aoi_id."'";
						$result = mysql_query($aoi_techsys_delete);
						//echo mysql_error();
						$result = mysql_query($commit_query);
						//echo mysql_error();

						$aoi_plc_delete = "delete from aoi_plc_stcmd_line where aoi_id = '".$child_aoi_id."'";
						$result = mysql_query($aoi_plc_delete);
						//echo mysql_error();
						$result = mysql_query($commit_query);
						//echo mysql_error();

						$aoi_delete = "delete from aoi where aoi_id  = '".$child_aoi_id."'";
						$result = mysql_query($aoi_delete);
						//echo mysql_error();
						$result = mysql_query($commit_query);
						//echo mysql_error();

	      			} // end delete all child aois while loop
	      		} // end if statement for not having deleted a single child aoi in previous step

				if ($_SESSION['ICMS_OK']){
				    // only remove keywords if we can actually connect to ICMS
					$soap_fault = remove_keywords($names_to_delete);
				}

			} // end valid aoi id to delete

			$_SESSION['aoi_selected'] = "false";

			$_SESSION['aoi_name'] = "";
   			$_SESSION['aoi_id'] = 0;

   			// the chance of the following soap fault occuring is incredibly unlikely
   			// given the conditional above to only remove keywords if the previous connect to ICMS succeeded
   			if ($soap_fault) {
   				include('soap_failure.php'); // redirects user to soap failure page and exits
   				exit;
   			}

	    	header("Status: 302 redirection");
		    header("Location: aoi_edit_basic_search_results.php?aoiName=".""."&aoiId=0");

	 } // end user request to delete an aoi

	 //User request to save editing changes to an existing AOI
	 if($_POST['saveChangesToSelectedAOI'] )
	 {
		// echo '<br>Save editing changes...</br>';

		// Check for valid AOI ID and AOI Name

		if ($_SESSION['aoi_id'] <= 0 || $_SESSION['aoi_name'] == ""){

			$_SESSION['status_message'] = "Invalid AOI name or AOI Id. Editing changes were not saved.";

			echo 'Invalid AOI name or AOI Id. Editing changes were not saved.';

			header("Status: 302 redirection");
			header("Location: aoi_edit_basic_search_results.php?aoiName=".""."&aoiId=0");

		}

		// Get aoi_id that is to be modified
	    $modify_aoi_id = $_SESSION['aoi_id'];
		$modify_aoi_name = $_SESSION['aoi_name'];

		// echo '<br>aoi name is: '.$modify_aoi_name.'</br>';

		// The user has requested to modify an existing AOI.
		// The following tables will need to be updated:

	    		//		aoi_document
	    		//		aoi_topdisplay
	    		//		aoi_criticality
	    		//      aoi

       if (($modify_aoi_id > 0) && ($_SESSION['aoi_selected'] == "true")){


              // First need to get the id numbers for cognizant 1, cognizant 2, customer group, and aoi status

			  if (!empty($user_status)){

			  	$tempStatusList = $_SESSION['aoiStatusList'];

			  	if (!empty($tempStatusList) ){
			  		$tempStatusEntity = $tempStatusList->getElementForAOIStatus($user_status);
			  		$aoi_status_id = $tempStatusEntity->getAOIStatusID();
			  	}else{
			  		echo "<br>Empty aoiStatusList...";
			  		exit;
			  	}

			  }else{

			  	$aoi_status_id = 6;  // Undefined

			  }


			  if (!empty($user_cognizant_1)) {
			  	$personList = $_SESSION['personList'];
			  	$personEntity = $personList->getElementForPersonUserID($user_cognizant_1);
			  	$cognizant_1_id = $personEntity->getPersonID();
			  }else{
			  	$cognizant_1_id = 45; // Unknown
			  }

			  if (!empty($user_cognizant_2)) {

			  	$personEntity = $personList->getElementForPersonUserID($user_cognizant_2);
			  	$cognizant_2_id = $personEntity->getPersonID();
			  }else{
			  	$cognizant_2_id = 45; // Unknown
			  }

			  if (!empty($user_customergroup)) {
			  	$groupList = $_SESSION['groupNameList'];
			  	$groupEntity = $groupList ->getElementForGroupName($user_customergroup);
			  	$customer_group_id = $groupEntity->getGroupNameID();
			  }else{
			  	$customer_group_id = 2; // No Group
			  }

			  // For now, set the customer contact id to default of "None" in the person table

			  // Come back to this later and use an sql search to find the id number for person = "None"

			  $user_customer_contact_id = 46;

			  // echo "<br>aoi status id is: ".$aoi_status_id."</br>";
			  // echo "<br>cognizant 1 id is: ".$cognizant_1_id."</br>";
			  // echo "<br>cognizant 2 id is: ".$cognizant_2_id."</br>";
			  // echo "<br>customer group id is: ".$customer_group_id."</br>";

			  // Check for empty user data entry text fields for aoi description, aoi worklog, and aoi keywords
			  $user_description = getHTML($user_description);
			  if (empty($user_description)) {
			  	$user_description = "-";
			  }

			  $user_worklog = getHTML($parent_worklog);

			  if (empty($user_worklog) || preg_match('/^Please.*/', $user_worklog)) {
			  	$user_worklog = "\n-";
			  }else{
			  	$user_worklog = "\n".$user_worklog;
			  }

			  if (empty($user_keyword) || preg_match('/^Please.*?/',$user_keyword)) {
			  	$user_keyword = "-";
			  }

			  // echo '<br>About to update table aoi...</br>';

              $aoi_update="update aoi ".
              			  "set  aoi_cognizant1_id = '".$cognizant_1_id."'".
              			  "  ,  aoi_cognizant2_id = '".$cognizant_2_id."'".
              			  "  ,  aoi_customer_group_id = '".$customer_group_id."'".
              			  "  ,  aoi_status_id = '".$aoi_status_id."'".
              			  "  ,  aoi_description = '".$user_description."'".
              			  "  ,  aoi_worklog = '".$user_worklog."'".
              			  "  ,  aoi_keyword = '".$user_keyword."'".
              			  "  ,  aoi_customer_contact_id = '".$user_customer_contact_id."'".
              			  " where aoi_id = '".$modify_aoi_id."'";

              $result = mysql_query($aoi_update);
              echo mysql_error();

              $commit_query = "commit";
              $result = mysql_query($commit_query);

              echo mysql_error();

              // Get the criticality ID from the user selected machine name, technical system name, and criticality level

              if (!empty($user_criticality_level)) {
			  			  	$criticalityList = $_SESSION['criticalityList'];
			  			  	$criticalityEntity = $criticalityList ->getElementForCriticalityLevel($user_criticality_level);
			  			  	$criticality_id = $criticalityEntity->getCriticalityID();
			  			  }else{
			  			  	// Send an error message to the user
			  			  	echo '<br>Invalid selection for criticality.</br>';
			  			  	exit;
			  }

			  // echo '<br>criticality id is: '.$criticality_id.'</br>';

			  if ($aoiDocumentList->length() != 0) {

		 		// Modify existing AOI documents
		 		// 	- Delete all existing documents
		 		// 	- Rebuild list of documents from user entries

		 		$aoi_document_delete = "delete from aoi_document where aoi_id = '".$modify_aoi_id."'";
		 		$result = mysql_query($aoi_document_delete);
		 		echo mysql_error();
		 		$commit_query = "commit";
		    	$result = mysql_query($commit_query);

				$size_array = count($user_existing_doc_type_array);
				for($i=0; $i<$size_array; $i++) {

					$doc_type = $user_existing_doc_type_array[$i];
					$doc_uri = $user_existing_doc_uri_array[$i];

					// echo "<br> aoi document type and uri: ".$doc_type."&nbsp".$doc_uri."</br>";

					if ($doc_type > 0 && !empty($doc_uri)) {

						// Get a new aoi_document_id and insert the doc type and doc uri provided by user
						$get_new_aoi_document_id = "select max(doc_id) + 1 doc_id from aoi_document";
				  		$result = mysql_query($get_new_aoi_document_id);
				  		$row = mysql_fetch_array($result);
				  		echo mysql_error();

            	  		$new_aoi_document_id = $row['aoi_document_id'];

						// echo "<br>new aoi document id is: ".$new_aoi_document_id."</br>";

			  			$aoi_document_update = "insert into aoi_document (doc_id, uri, aoi_id, doc_type_id) ".
						  					 "values ('".$new_aoi_document_id."', ".
						  					 " '".$doc_uri."',".
											 " '".$modify_aoi_id."',".
						  					 " '".$doc_type."')";

			  			$result = mysql_query($aoi_document_update);
			  			echo mysql_error();

		  	  			$result = mysql_query($commit_query);
		  	  			echo mysql_error();

					}

					// Reset loop variables
					$doc_type = 0;
					$doc_uri = "";

				} // end for loop

			  }  // end if have documents in aoi doc list


  		 	// New AOI Documents

			for ($i = 1; $i < 6; $i++) {

				// echo "<br>".$i." New document: ".$user_newaoidocument_array[$i]."</br>";
				// echo "<br>".$i."   its document type is: ".$user_newaoidoctype_array[$i]."</br>";

				if (!empty($user_newaoidocument_array[$i])){
					// Get a new  aoi_document_id and insert the URI given by user

			  		$get_new_aoi_document_id = "select max(doc_id) + 1 doc_id from aoi_document";
			  		$result = mysql_query($get_new_aoi_document_id);
			  		$row = mysql_fetch_array($result);
			  		echo mysql_error();

              		$new_aoi_document_id = $row['aoi_document_id'];

					// echo "<br>new aoi document id is: ".$new_aoi_document_id."</br>";

			  		$aoi_document_update = "insert into aoi_document (doc_id, uri, aoi_id, doc_type_id) ".
						  					 "values ('".$new_aoi_document_id."', ".
						  					 " '".$user_newaoidocument_array[$i]."',".
											 " '".$modify_aoi_id."',".
						  					 " '".$user_newaoidoctype_array[$i]."')";

			  		$result = mysql_query($aoi_document_update);
			  		echo mysql_error();

		  	  		$result = mysql_query($commit_query);
		  	  		echo mysql_error();
				}
			}

        // Modify existing MEDM displays
        // 		- Delete all existing MEDM displays
        //		- Rebuild list of MEDM displays from user entries

        	$aoi_topdisplay_delete = "delete from aoi_topdisplay where aoi_id = '".$modify_aoi_id."'";
			$result = mysql_query($aoi_topdisplay_delete);
			echo mysql_error();
			$commit_query = "commit";
       		$result = mysql_query($commit_query);

       		foreach ($user_existingtopdisplay_array as $medm_uri) {

       			// echo "<br>MEDM top display uri: ".$medm_uri."</br>";

       			if (!empty($medm_uri)) {
       				// Get a new  aoi_topdisplay_id and insert the URI given by user

					$get_new_aoi_topdisplay_id = "select max(aoi_topdisplay_id) + 1 aoi_topdisplay_id from aoi_topdisplay";
					$result = mysql_query($get_new_aoi_topdisplay_id);
					$row = mysql_fetch_array($result);
					echo mysql_error();

					$new_aoi_topdisplay_id = $row['aoi_topdisplay_id'];

					// echo "<br>new aoi topdisplay id is: ".$new_aoi_topdisplay_id."</br>";

					$aoi_topdisplay_update = "insert into aoi_topdisplay (aoi_topdisplay_id, uri, aoi_id) ".
										  					 "values ('".$new_aoi_topdisplay_id."', ".
										  					 " '".$medm_uri."',".
										  					 " '".$modify_aoi_id."')";

					$result = mysql_query($aoi_topdisplay_update);
					echo mysql_error();

					$result = mysql_query($commit_query);
		  	  		echo mysql_error();

		  	  	} // end if not empty
       		}


		// New MEDM Top Displays

			for ($i = 1; $i < 6; $i++) {

				// echo "<br>".$i." New top display: ".$user_newtopdisplay_array[$i]."</br>";

				if (!empty($user_newtopdisplay_array[$i])){
					// Get a new  aoi_topdisplay_id and insert the URI given by user

			  		$get_new_aoi_topdisplay_id = "select max(aoi_topdisplay_id) + 1 aoi_topdisplay_id from aoi_topdisplay";
			  		$result = mysql_query($get_new_aoi_topdisplay_id);
			  		$row = mysql_fetch_array($result);
			  		echo mysql_error();

              		$new_aoi_topdisplay_id = $row['aoi_topdisplay_id'];

					// echo "<br>new aoi topdisplay id is: ".$new_aoi_topdisplay_id."</br>";

			  		$aoi_topdisplay_update = "insert into aoi_topdisplay (aoi_topdisplay_id, uri, aoi_id) ".
						  					 "values ('".$new_aoi_topdisplay_id."', ".
						  					 " '".$user_newtopdisplay_array[$i]."',".
						  					 " '".$modify_aoi_id."')";

			  		$result = mysql_query($aoi_topdisplay_update);
			  		echo mysql_error();

		  	  		$result = mysql_query($commit_query);
		  	  		echo mysql_error();
				}
			}

		// Update aoi_criticality table

				if (!empty($user_criticality_level)) {
							  			  	$criticalityList = $_SESSION['criticalityList'];
							  			  	$criticalityEntity = $criticalityList ->getElementForCriticalityLevel($user_criticality_level);
							  			  	$criticality_id = $criticalityEntity->getCriticalityID();
				}else{
							  			  	// Send an error message to the user
							  			  	exit;
			    }

			    // echo "<br>About to update table aoi_criticality</br>";

	    		$aoi_criticality_update = "update aoi_criticality".
	    								  " set criticality_id = '".$criticality_id."'".
	    								  " where aoi_id = '".$modify_aoi_id."'";

	    		$result = mysql_query($aoi_criticality_update);
	    		echo mysql_error();
	    		$commit_query = "commit";
            	$result = mysql_query($commit_query);

				header("Status: 302 redirection");
				header("Location: aoi_edit_basic_search_results.php?aoiName=".$modify_aoi_name."&aoiId=".$modify_aoi_id."");

	    }else{
	    	echo "<br>Invalid AOI ID or AOI Name requested to be modified.</br>";
	    	exit;
	    }


		header("Status: 302 redirection");
		header("Location: aoi_edit_basic_search_results.php?aoiName=".$_SESSION['aoi_name']."&aoiId=".$_SESSION['aoi_id']."");

	 }


	 //User request to save editing changes to a new AOI (multiple AOIs if child field was defined)
	 if($_POST['saveChangesAsNewAOI'] )
	 {
	 	// echo 'Save editing changes to create as a new AOI...';

		$_SESSION['aoi_id'] = 0;
		$_SESSION['aoi_name'] = "";
		$_SESSION['show_new_aoi'] = "false";

		// Create new AOI name based on machine, techsys, function, and child

	    // Check that the unique function and child aoi entries do not contain an underscore (data entry validation...)
	    // Also check that they do not contain any spaces

	  	if ( ereg( "_+", $user_uniquefunction) || ereg( " ", $user_uniquefunction) ){

			// Incorrect use of underscore or space in the aoi name field "unique_function"

			echo "<br>Incorrect use of characters in the aoi name field: ".$user_uniquefunction."</br>";
		    exit;
	  	}

	  	if ( ereg( "_+", $user_children) ){

	  		// Incorrect use of underscore in the aoi name field "child"

			echo "<br>Incorrect use of characters in the aoi name field: ".$user_children."</br>";
		    exit;
	  	}


      	// Check validity of user defined "unique_function" that is to become part of the aoi name

      	if ($user_uniquefunction == $user_techsystem || $user_uniquefunction == $user_machine || $user_uniquefunction == "" || empty($user_techsystem) || empty($user_machine)){

      	 	echo "<TR><TH class='five' colspan='6'>   DATA ENTRY ERROR: Invalid entry for the AOI name. Please try again.</TH></TR>";
		 	exit;

      	}

        $derived_name = "aoi_".strtolower($user_machine)."_".strtolower($user_techsystem)."_".strtolower($user_uniquefunction);

        $derived_parent_name = $derived_name;

        // CHECK TO SEE IF USER DEFINED PARENT AOI NAME IS ALREADY IN USE

        $check_aoi_name_exists = "select aoi_id from aoi where aoi_name = '".$derived_parent_name."'";

        $result = mysql_query($check_aoi_name_exists);
        echo mysql_error();

        $row = mysql_fetch_array($result);
        echo mysql_error();

        $matched_aoi_id = $row['aoi_id'];

		$parent_aoi_id = $matched_aoi_id;

		if($matched_aoi_id <= 0){

              // parent aoi does not already exist
              // get a new aoi_id number

              $get_aoi_id = "select aoi_id from aoi";
              $result = mysql_query($get_aoi_id);
              echo mysql_error();

              while ($row = mysql_fetch_array($result)){
                echo mysql_error();
              	$new_aoi_id = $row['aoi_id'] + 1;
              }

              //Create a new parent aoi

              // First need to get the id numbers for cognizant 1, cognizant 2, customer group, and aoi status

			  if (!empty($user_status)){

			  	$tempStatusList = $_SESSION['aoiStatusList'];

			  	if (!empty($tempStatusList) ){
			  		$tempStatusEntity = $tempStatusList->getElementForAOIStatus($user_status);
			  		$aoi_status_id = $tempStatusEntity->getAOIStatusID();
			  	}else{
			  		echo "<br>Empty aoiStatusList...";
			  		exit;
			  	}

			  }else{

			  	$aoi_status_id = 6;  // Undefined

			  }


			  if (!empty($user_cognizant_1)) {
			  	$personList = $_SESSION['personList'];
			  	$personEntity = $personList->getElementForPersonUserID($user_cognizant_1);
			  	$cognizant_1_id = $personEntity->getPersonID();
			  }else{
			  	$cognizant_1_id = 45; // Unknown
			  }

			  if (!empty($user_cognizant_2)) {

			  	$personEntity = $personList->getElementForPersonUserID($user_cognizant_2);
			  	$cognizant_2_id = $personEntity->getPersonID();
			  }else{
			  	$cognizant_2_id = 45; // Unknown
			  }

			  if (!empty($user_customergroup)) {
			  	$groupList = $_SESSION['groupNameList'];
			  	$groupEntity = $groupList ->getElementForGroupName($user_customergroup);
			  	$customer_group_id = $groupEntity->getGroupNameID();
			  }else{
			  	$customer_group_id = 2; // No Group
			  }

			  // For now, set the customer contact id to default of "None" in the person table

			  $user_customer_contact_id = 46;

			  // echo "<br>aoi status id is: ".$aoi_status_id."</br>";
			  // echo "<br>cognizant 1 id is: ".$cognizant_1_id."</br>";
			  // echo "<br>cognizant 2 id is: ".$cognizant_2_id."</br>";
			  // echo "<br>customer group id is: ".$customer_group_id."</br>";

			  // Check for empty user data entry text fields for aoi description, aoi worklog, and aoi keywords
			  $user_description = getHTML($user_description);
			  if (empty($user_description)) {
			  	$user_description = "-";
			  }

			  $user_worklog = getHTML($parent_worklog);

			  if (empty($user_worklog) || preg_match('/^Please.*/', $user_worklog)) {
			  	$user_worklog = "\n-";
			  }else{
			  	$user_worklog = "\n".$user_worklog;
			  }

			  if (empty($user_keyword) || preg_match('/^Please.*?/',$user_keyword)) {
			  	$user_keyword = "-";
			  }

			  // echo '<br>About to insert new aoi definition into table aoi...</br>';
			  // echo '<br>new aoi id is: '.$new_aoi_id.'</br>';
			  // echo '<br>parent aoi name is: '.$derived_parent_name.'</br>';
			  // echo '<br>cognizant 1 id is: '.$cognizant_1_id.'</br>';
			  // echo '<br>cognizant 2 id is: '.$cognizant_2_id.'</br>';
			  // echo '<br>customer group id is: '.$customer_group_id.'</br>';
			  // echo '<br>aoi status id is: '.$aoi_status_id.'</br>';
			  // echo '<br>user description is: '.$user_description.'</br>';
			  // echo '<br>user worklog is: '.$user_worklog.'</br>';
			  // echo '<br>user keyword is: '.$user_keyword.'</br>';
			  // echo '<br>user customer contact is: '.$user_customer_contact_id.'</br>';

              $aoi_update="insert into aoi (aoi_id, aoi_name, aoi_cognizant1_id, aoi_cognizant2_id, aoi_customer_group_id, aoi_status_id, aoi_description, aoi_worklog, aoi_keyword, aoi_customer_contact_id)".
                               "values ('".$new_aoi_id."', ".
                               "        '".$derived_parent_name."', ".
                               "        '".$cognizant_1_id."', ".
                               "        '".$cognizant_2_id."', ".
                               "        '".$customer_group_id."', ".
                               "        '".$aoi_status_id."', ".
                               "        '".$user_description."', ".
                               "        '".$user_worklog."', ".
				       		   "        '".$user_keyword."', ".
                               "        '".$user_customer_contact_id."')";

              $result = mysql_query($aoi_update);
              echo mysql_error();

              $commit_query = "commit";
              $result = mysql_query($commit_query);

              echo mysql_error();

              // Get the machine ID, criticality ID, and technical system ID from the user selected machine name, technical system name, and criticality level

              if (!empty($user_machine)) {
			  			  	$machineList = $_SESSION['machineList'];
			  			  	$machineEntity = $machineList ->getElementForMachine($user_machine);
			  			  	$machine_id = $machineEntity->getMachineID();
			  			  }else{
			  			  	// Send an error message to the user
			  			  	echo '<br>Could not retrieve machine list.</br>';
			  			  	exit;
			  }

              if (!empty($user_techsystem)) {
			  			  	$techsystemList = $_SESSION['techsystemList'];
			  			  	$techsystemEntity = $techsystemList ->getElementForTechsystem($user_techsystem);
			  			  	$technical_system_id = $techsystemEntity->getTechnicalSystemID();
			  			  }else{
			  			  	// Send an error message to the user
			  			  	exit;
			  }

              if (!empty($user_criticality_level)) {
			  			  	$criticalityList = $_SESSION['criticalityList'];
			  			  	$criticalityEntity = $criticalityList ->getElementForCriticalityLevel($user_criticality_level);
			  			  	$criticality_id = $criticalityEntity->getCriticalityID();
			  			  }else{
			  			  	// Send an error message to the user
			  			  	exit;
			  }

			  // echo '<br>criticality id is: '.$criticality_id.'</br>';

			  // AOI Machine

 			  // echo '<br>About to insert into table aoi_machine...</br>';


              $get_new_aoi_machine_id = "select max(aoi_machine_id) + 1 aoi_machine_id from aoi_machine";

              $result = mysql_query($get_new_aoi_machine_id);

              $row = mysql_fetch_array($result);

              echo mysql_error();

              $new_aoi_machine_id = $row['aoi_machine_id'];

		      $aoi_machine_update = "insert into aoi_machine (aoi_machine_id, machine_id, aoi_id) ".
				          						"values ('".$new_aoi_machine_id."', ".
				          						"'".$machine_id."', ".
				          						"'".$new_aoi_id."')";

			  $result = mysql_query($aoi_machine_update);
			  echo mysql_error();

			  $result = mysql_query($commit_query);
			  echo mysql_error();


			  // AOI Technical System

			  // echo '<br>About to insert into table aoi_techsys...</br>';

			  $get_new_aoi_techsys_id = "select max(aoi_techsystem_id) + 1 aoi_techsystem_id from aoi_techsys";
			  $result = mysql_query($get_new_aoi_techsys_id);
			  $row = mysql_fetch_array($result);
			  echo mysql_error();

              $new_aoi_techsys_id = $row['aoi_techsystem_id'];

			  $aoi_techsys_update = "insert into aoi_techsys (aoi_techsystem_id, technical_system_id, aoi_id) ".
						  						"values ('".$new_aoi_techsys_id."', ".
						  						"'".$technical_system_id."', ".
						  						"'".$new_aoi_id."')";

			  $result = mysql_query($aoi_techsys_update);
			  echo mysql_error();

			  $result = mysql_query($commit_query);


			  // AOI Criticality

			  // echo '<br>About to insert into table aoi_criticality...</br>';

			  $get_new_aoi_criticality_id = "select max(aoi_criticality_id) + 1 aoi_criticality_id from aoi_criticality";
			  $result = mysql_query($get_new_aoi_criticality_id);
			  $row = mysql_fetch_array($result);
			  echo mysql_error();

              $new_aoi_criticality_id = $row['aoi_criticality_id'];

              // echo '<br> new_aoi_critiality_id is: '.$new_aoi_criticality_id.'</br>';
              // echo '<br> new_aoi_id is: '.$new_aoi_id.'</br>';

			  $aoi_criticality_update = "insert into aoi_criticality (aoi_criticality_id, criticality_id, aoi_id) ".
						  						"values ('".$new_aoi_criticality_id."', ".
						  						"'".$criticality_id."', ".
						  						"'".$new_aoi_id."')";

			  $result = mysql_query($aoi_criticality_update);
			  echo mysql_error();

			  $result = mysql_query($commit_query);

			// Existing MEDM Top Displays to copy to newly created aoi

			foreach ($user_existingtopdisplay_array as $medm_uri) {

			       			// echo "<br>MEDM top display uri: ".$medm_uri."</br>";

			       			if (!empty($medm_uri)) {
			       				// Get a new  aoi_topdisplay_id and insert the URI given by user

								$get_new_aoi_topdisplay_id = "select max(aoi_topdisplay_id) + 1 aoi_topdisplay_id from aoi_topdisplay";
								$result = mysql_query($get_new_aoi_topdisplay_id);
								$row = mysql_fetch_array($result);
								echo mysql_error();

								$new_aoi_topdisplay_id = $row['aoi_topdisplay_id'];

								// echo "<br>new aoi topdisplay id is: ".$new_aoi_topdisplay_id."</br>";

								$aoi_topdisplay_update = "insert into aoi_topdisplay (aoi_topdisplay_id, uri, aoi_id) ".
													  					 "values ('".$new_aoi_topdisplay_id."', ".
													  					 " '".$medm_uri."',".
													  					 " '".$new_aoi_id."')";

								$result = mysql_query($aoi_topdisplay_update);
								echo mysql_error();

								$result = mysql_query($commit_query);
					  	  		echo mysql_error();

					  	  	} // end if not empty
       		}

			// New MEDM Top Displays

			for ($i = 1; $i < 6; $i++) {

				// echo "<br>".$i." New top display: ".$user_newtopdisplay_array[$i]."</br>";

				if (!empty($user_newtopdisplay_array[$i])){
					// Get a new  aoi_topdisplay_id and insert the URI given by user

			  		$get_new_aoi_topdisplay_id = "select max(aoi_topdisplay_id) + 1 aoi_topdisplay_id from aoi_topdisplay";
			  		$result = mysql_query($get_new_aoi_topdisplay_id);
			  		$row = mysql_fetch_array($result);
			  		echo mysql_error();

              		$new_aoi_topdisplay_id = $row['aoi_topdisplay_id'];

					// echo "<br>new aoi topdisplay id is: ".$new_aoi_topdisplay_id."</br>";
					// echo "<br>new topdisplay is: ".$user_newtopdisplay_array[$i]."</br>";
					// echo "<br>aoi id is: ".$new_aoi_id."</br>";

			  		$aoi_topdisplay_update = "insert into aoi_topdisplay (aoi_topdisplay_id, uri, aoi_id) ".
						  					 "values ('".$new_aoi_topdisplay_id."', ".
						  					 " '".$user_newtopdisplay_array[$i]."',".
						  					 " '".$new_aoi_id."')";

			  		$result = mysql_query($aoi_topdisplay_update);
			  		echo mysql_error();

		  	  		$result = mysql_query($commit_query);
		  	  		echo mysql_error();
				}
			}

			// Existing AOI Documents to be copied to newly created aoi
			$size_array = count($user_existing_doc_type_array);
			for($i=0; $i<$size_array; $i++) {

				$doc_type = $user_existing_doc_type_array[$i];
				$doc_uri = $user_existing_doc_uri_array[$i];

				// echo "<br> aoi document type and uri: ".$doc_type."&nbsp".$doc_uri."</br>";

				if ($doc_type > 0 && !empty($doc_uri)) {

					// Get a new aoi_document_id and insert the doc type and doc uri provided by user
					$get_new_aoi_document_id = "select max(doc_id) + 1 doc_id from aoi_document";
					$result = mysql_query($get_new_aoi_document_id);
					$row = mysql_fetch_array($result);
					echo mysql_error();

			        $new_aoi_document_id = $row['aoi_document_id'];

					// echo "<br>new aoi document id is: ".$new_aoi_document_id."</br>";

					$aoi_document_update = "insert into aoi_document (doc_id, uri, aoi_id, doc_type_id) ".
									  					 "values ('".$new_aoi_document_id."', ".
									  					 " '".$doc_uri."',".
														 " '".$new_aoi_id."',".
									  					 " '".$doc_type."')";

					$result = mysql_query($aoi_document_update);
					echo mysql_error();

					$result = mysql_query($commit_query);
					echo mysql_error();
				}

				// Reset loop variables
				$doc_type = 0;
				$doc_uri = "";

			} // end for loop

			// New AOI Documents
			for ($i = 1; $i < 6; $i++) {

				// echo "<br>".$i." New document: ".$user_newaoidocument_array[$i]."</br>";
				// echo "<br>".$i."   its document type is: ".$user_newaoidoctype_array[$i]."</br>";

				if (!empty($user_newaoidocument_array[$i])){
					// Get a new  aoi_document_id and insert the URI given by user

			  		$get_new_aoi_document_id = "select max(doc_id) + 1 doc_id from aoi_document";
			  		$result = mysql_query($get_new_aoi_document_id);
			  		$row = mysql_fetch_array($result);
			  		echo mysql_error();

              		$new_aoi_document_id = $row['aoi_document_id'];

					// echo "<br>new aoi document id is: ".$new_aoi_document_id."</br>";
					// echo "<br>uri is: ".$user_newaoidocument_array[$i]."</br>";
					// echo "<br>aoi id is: ".$new_aoi_id."</br>";
					// echo "<br>doc type is: ".$user_newaoidoctype_array[$i]."</br>";

			  		$aoi_document_update = "insert into aoi_document (doc_id, uri, aoi_id, doc_type_id) ".
						  					 "values ('".$new_aoi_document_id."', ".
						  					 " '".$user_newaoidocument_array[$i]."',".
											 " '".$new_aoi_id."',".
						  					 " '".$user_newaoidoctype_array[$i]."')";

			  		$result = mysql_query($aoi_document_update);
			  		echo mysql_error();

		  	  		$result = mysql_query($commit_query);
		  	  		echo mysql_error();
				}
			}

		  	$parent_aoi_id = $new_aoi_id;

			$_SESSION['aoi_id'] = $parent_aoi_id;
			$_SESSION['aoi_name'] = $derived_parent_name;
			$_SESSION['show_new_aoi'] = "true";

		  }  // end create new parent aoi


        	// Check for children aoi's entered by user

		// Use 'split' function to see if user entered a list of children for an aoi.

		// echo "<br>User defined aoi children:  ".$user_children."</br>";

		if (!empty($user_children)){
		   $child_array = split(',',$user_children);
		   $child_array_size = count($child_array);
		}

		  // PARENT AOI EITHER ALREADY EXISTS IN THE DB OR IT WAS JUST CREATED ABOVE

		  // CHECK TO SEE IF USER SUPPLIED LIST OF CHILDREN AOI'S

		  if($child_array_size > 0){

						// Process creation of new aoi names for children aoi's:
						//
						//		1. Check if valid aoi name sub-parts (child field does not equal any of the other parts of the aoi name)
						//		2. Check if child aoi name already exists
						//		3. Create new aoi.  Modify tables:	aoi
						//								aoi_machine
						//								aoi_techsys
						//								aoi_relation
						//
						//		4. Get new aoi_id for each child, $new_child_aoi_id

						// Loop through array of child aoi's

						foreach ($child_array as $child_aoi){

							//echo "<br>child aoi field: ".$child_aoi."</br>";

							// Remove white space from each end of child_aoi text
							$child_aoi = trim($child_aoi);

	  						if ( ereg( "_+", $child_aoi) || ereg( " ", $child_aoi) ){

	  							// Incorrect use of underscore or space in the aoi name field "child"
								$_SESSION['error_message'] = "Incorrect use of characters in the AOI name child field.";
								echo "<br>Incorrect use of characters in the aoi name field: ".$child_aoi."</br>";
								echo "<br>Please try again.</br>";
		    					exit;
						  	}

							if ($child_aoi != $user_machine && $child_aoi != $user_techsystem && $child_aoi != $user_uniquefuntion) {

								// create new aoi name for this child
								$derived_child_aoi_name = "aoi_".strtolower($user_machine)."_".strtolower($user_techsystem)."_".strtolower($user_uniquefunction)."_".strtolower($child_aoi);

								//echo "<BR> DEBUGGING: User child aoi name:  $derived_child_aoi_name <BR>";

								// check if this new aoi name already exists in the aoi database

          							$check_aoi_name_exists = "select distinct aoi_id from aoi where aoi_name = '".$derived_child_aoi_name."'";

          							$result = mysql_query($check_aoi_name_exists);

								echo mysql_error();

								$row = mysql_fetch_array($result);
								// echo mysql_error();

								$matched_aoi_id = 0;

								$matched_aoi_id = $row['aoi_id'];

						        if($matched_aoi_id <= 0){

              						// aoi does not already exist, so get a new aoi_id number

              						$new_child_aoi_id = 0;

                						$get_new_child_aoi_id = "select max(aoi_id) + 1 aoi_id from aoi";

                						$result = mysql_query($get_new_child_aoi_id);
                						$row = mysql_fetch_array($result);
                						echo mysql_error();

                						$new_child_aoi_id = $row['aoi_id'];

                						// Can now fill aoi database tables with information for this child aoi

              						// First need to get the id numbers for cognizant 1, cognizant 2, customer group, and aoi status

				    			      if (!empty($user_status)){

								  	$tempStatusList = $_SESSION['aoiStatusList'];

								  	if (!empty($tempStatusList) ){
			  							$tempStatusEntity = $tempStatusList->getElementForAOIStatus($user_status);
			  							$aoi_status_id = $tempStatusEntity->getAOIStatusID();
								  	}else{
			  							echo "<br>Empty aoiStatusList...";
			  							exit;
								  	}

				 			      }else{

								  	$aoi_status_id = 6;  // undefined

			  				      }
								if (!empty($user_cognizant_1)) {
								  	$personList = $_SESSION['personList'];
			  						$personEntity = $personList->getElementForPersonUserID($user_cognizant_1);
			  						$cognizant_1_id = $personEntity->getPersonID();
			  					}else{
			  						$cognizant_1_id = 45; // Unknown
			  					}

							      if (!empty($user_cognizant_2)) {

								  	$personEntity = $personList->getElementForPersonUserID($user_cognizant_2);
			  						$cognizant_2_id = $personEntity->getPersonID();
			  					}else{
			  						$cognizant_2_id = 45; // Unknown
			  					}

			  					if (!empty($user_customergroup)) {
			  						$groupList = $_SESSION['groupNameList'];
			  						$groupEntity = $groupList ->getElementForGroupName($user_customergroup);
			  						$customer_group_id = $groupEntity->getGroupNameID();
			  					}else{
			  						$customer_group_id = 2; // No Group
			  					}

			  					// For now, set the customer contact id to default of "None" in the person table

			  					$user_customer_contact_id = 46;

			  					// Check for empty user data entry text fields for aoi description, aoi worklog, and aoi keywords
								$user_description = getHTML($user_description);
			  					if (empty($user_description)) {
			  						$user_description = "Undefined";
			  					}

			  				    $user_worklog = getHTML($parent_worklog);

								if (empty($user_worklog) || preg_match('/^Please.*/', $user_worklog)) {
									  	$user_worklog = "\n-";
								}else{
									  	$user_worklog = "\n".$user_worklog;
								}

			  					if (empty($user_keyword) || preg_match('/^Please.*?/',$user_keyword)) {
			  						$user_keyword = "-";
			  					}


              						$aoi_update="insert into aoi (aoi_id, aoi_name, aoi_cognizant1_id, aoi_cognizant2_id, aoi_customer_group_id, aoi_status_id, aoi_description, aoi_worklog, aoi_keyword, aoi_customer_contact_id)".
                               				"values ('".$new_child_aoi_id."', ".
                               				"        '".$derived_child_aoi_name."', ".
                               				"        '".$cognizant_1_id."', ".
                               				"        '".$cognizant_2_id."', ".
                               				"        '".$customer_group_id."', ".
                               				"        '".$aoi_status_id."', ".
                               				"        '".$user_description."', ".
                               				"        '".$user_worklog."', ".
				       						"        '".$user_keyword."', ".
                               				"        '".$user_customer_contact_id."')";

              						$result = mysql_query($aoi_update);
              						echo mysql_error();

              						$commit_query = "commit";
              						$result = mysql_query($commit_query);

              						echo mysql_error();

              						// Get the machine ID, criticality ID, and technical system ID from the user selected machine name, technical system name, and criticality level

              if (!empty($user_machine)) {
			  			  	$machineList = $_SESSION['machineList'];
			  			  	$machineEntity = $machineList ->getElementForMachine($user_machine);
			  			  	$machine_id = $machineEntity->getMachineID();
			  			  }else{
			  			  	// Send an error message to the user
			  			  	exit;
			  }

              if (!empty($user_techsystem)) {
			  			  	$techsystemList = $_SESSION['techsystemList'];
			  			  	$techsystemEntity = $techsystemList ->getElementForTechsystem($user_techsystem);
			  			  	$technical_system_id = $techsystemEntity->getTechnicalSystemID();
			  			  }else{
			  			  	// Send an error message to the user
			  			  	exit;
			  }

              if (!empty($user_criticality_level)) {
			  			  	$criticalityList = $_SESSION['criticalityList'];
			  			  	$criticalityEntity = $criticalityList ->getElementForCriticalityLevel($user_criticality_level);
			  			  	$criticality_id = $criticalityEntity->getCriticalityID();
			  			  }else{
			  			  	// Send an error message to the user
			  			  	exit;
			  }

			  // AOI Machine

              $get_new_aoi_machine_id = "select max(aoi_machine_id) + 1 aoi_machine_id from aoi_machine";

              $result = mysql_query($get_new_aoi_machine_id);

              $row = mysql_fetch_array($result);

              echo mysql_error();

              $new_aoi_machine_id = $row['aoi_machine_id'];

		      $aoi_machine_update = "insert into aoi_machine (aoi_machine_id, machine_id, aoi_id) ".
				          						"values ('".$new_aoi_machine_id."', ".
				          						"'".$machine_id."', ".
				          						"'".$new_child_aoi_id."')";

			  $result = mysql_query($aoi_machine_update);
			  echo mysql_error();

			  $result = mysql_query($commit_query);
			  echo mysql_error();


			  // AOI Technical System

			  $get_new_aoi_techsys_id = "select max(aoi_techsystem_id) + 1 aoi_techsystem_id from aoi_techsys";
			  $result = mysql_query($get_new_aoi_techsys_id);
			  $row = mysql_fetch_array($result);
			  echo mysql_error();

              $new_aoi_techsys_id = $row['aoi_techsystem_id'];

			  $aoi_techsys_update = "insert into aoi_techsys (aoi_techsystem_id, technical_system_id, aoi_id) ".
						  						"values ('".$new_aoi_techsys_id."', ".
						  						"'".$technical_system_id."', ".
						  						"'".$new_child_aoi_id."')";

			  $result = mysql_query($aoi_techsys_update);
			  echo mysql_error();

			  $result = mysql_query($commit_query);


			  // AOI Criticality

			  $get_new_aoi_criticality_id = "select max(aoi_criticality_id) + 1 aoi_criticality_id from aoi_criticality";
			  $result = mysql_query($get_new_aoi_criticality_id);
			  $row = mysql_fetch_array($result);
			  echo mysql_error();

              $new_aoi_criticality_id = $row['aoi_criticality_id'];

			  $aoi_criticality_update = "insert into aoi_criticality (aoi_criticality_id, criticality_id, aoi_id) ".
						  						"values ('".$new_aoi_criticality_id."', ".
						  						"'".$criticality_id."', ".
						  						"'".$new_child_aoi_id."')";

			  $result = mysql_query($aoi_criticality_update);
			  echo mysql_error();

			  $result = mysql_query($commit_query);

			// Existing MEDM Top Displays to be copied to newly created child aoi

			foreach ($user_existingtopdisplay_array as $medm_uri) {

			       			// echo "<br>MEDM top display uri: ".$medm_uri."</br>";

			       			if (!empty($medm_uri)) {
			       				// Get a new  aoi_topdisplay_id and insert the URI given by user

								$get_new_aoi_topdisplay_id = "select max(aoi_topdisplay_id) + 1 aoi_topdisplay_id from aoi_topdisplay";
								$result = mysql_query($get_new_aoi_topdisplay_id);
								$row = mysql_fetch_array($result);
								echo mysql_error();

								$new_aoi_topdisplay_id = $row['aoi_topdisplay_id'];

								// echo "<br>new aoi topdisplay id is: ".$new_aoi_topdisplay_id."</br>";

								$aoi_topdisplay_update = "insert into aoi_topdisplay (aoi_topdisplay_id, uri, aoi_id) ".
													  					 "values ('".$new_aoi_topdisplay_id."', ".
													  					 " '".$medm_uri."',".
													  					 " '".$new_child_aoi_id."')";

								$result = mysql_query($aoi_topdisplay_update);
								echo mysql_error();

								$result = mysql_query($commit_query);
					  	  		echo mysql_error();

					  	  	} // end if not empty
       		}


			// New MEDM Top Displays

			for ($i = 1; $i < 6; $i++) {

				//echo "<br>".$i." New top display: ".$user_newtopdisplay_array[$i]."</br>";

				if (!empty($user_newtopdisplay_array[$i])){
					// Get a new  aoi_topdisplay_id and insert the URI given by user

			  		$get_new_aoi_topdisplay_id = "select max(aoi_topdisplay_id) + 1 aoi_topdisplay_id from aoi_topdisplay";
			  		$result = mysql_query($get_new_aoi_topdisplay_id);
			  		$row = mysql_fetch_array($result);
			  		echo mysql_error();

              			$new_aoi_topdisplay_id = $row['aoi_topdisplay_id'];

					//echo "<br>new aoi topdisplay id is: ".$new_aoi_topdisplay_id."</br>";

			  		$aoi_topdisplay_update = "insert into aoi_topdisplay (aoi_topdisplay_id, uri, aoi_id) ".
						  					 "values ('".$new_aoi_topdisplay_id."', ".
						  					 " '".$user_newtopdisplay_array[$i]."',".
						  					 " '".$new_child_aoi_id."')";

			  		$result = mysql_query($aoi_topdisplay_update);
			  		echo mysql_error();

		  	  		$result = mysql_query($commit_query);
		  	  		echo mysql_error();
				}
			} // end for loop top dipslays

			// Existing AOI Documents to be copied over to newly created child aoi
			$size_array = count($user_existing_doc_type_array);
			for($i=0; $i<$size_array; $i++) {

					$doc_type = $user_existing_doc_type_array[$i];
					$doc_uri = $user_existing_doc_uri_array[$i];

					// echo "<br> aoi document type and uri: ".$doc_type."&nbsp".$doc_uri."</br>";

					if ($doc_type > 0 && !empty($doc_uri)) {

						// Get a new aoi_document_id and insert the doc type and doc uri provided by user
						$get_new_aoi_document_id = "select max(doc_id) + 1 doc_id from aoi_document";
				  		$result = mysql_query($get_new_aoi_document_id);
				  		$row = mysql_fetch_array($result);
				  		echo mysql_error();

            	  		$new_aoi_document_id = $row['aoi_document_id'];

						// echo "<br>new aoi document id is: ".$new_aoi_document_id."</br>";

			  			$aoi_document_update = "insert into aoi_document (doc_id, uri, aoi_id, doc_type_id) ".
						  					 "values ('".$new_aoi_document_id."', ".
						  					 " '".$doc_uri."',".
											 " '".$new_child_aoi_id."',".
						  					 " '".$doc_type."')";

			  			$result = mysql_query($aoi_document_update);
			  			echo mysql_error();

		  	  			$result = mysql_query($commit_query);
		  	  			echo mysql_error();
					}

					// Reset loop variables
					$doc_type = 0;
					$doc_uri = "";
			} // end for loop

			// New AOI Documents
			for ($i = 1; $i < 6; $i++) {

				// echo "<br>".$i." New document: ".$user_newaoidocument_array[$i]."</br>";
				// echo "<br>".$i."   its document type is: ".$user_newaoidoctype_array[$i]."</br>";

				if (!empty($user_newaoidocument_array[$i])){
					// Get a new  aoi_document_id and insert the URI given by user

			  		$get_new_aoi_document_id = "select max(doc_id) + 1 doc_id from aoi_document";
			  		$result = mysql_query($get_new_aoi_document_id);
			  		$row = mysql_fetch_array($result);
			  		echo mysql_error();

              			$new_aoi_document_id = $row['aoi_document_id'];

					//echo "<br>new aoi document id is: ".$new_aoi_document_id."</br>";

			  		$aoi_document_update = "insert into aoi_document (doc_id, uri, aoi_id, doc_type_id) ".
						  					 "values ('".$new_aoi_document_id."', ".
						  					 " '".$user_newaoidocument_array[$i]."',".
											 " '".$new_child_aoi_id."',".
						  					 " '".$user_newaoidoctype_array[$i]."')";

			  		$result = mysql_query($aoi_document_update);
			  		echo mysql_error();

		  	  		$result = mysql_query($commit_query);
		  	  		echo mysql_error();
				}
			} // end for loop aoi documents

								$_SESSION['aoi_id'] = $new_child_aoi_id;
								$_SESSION['aoi_name'] = $derived_child_aoi_name;
								$_SESSION['show_new_aoi'] = "true";

								// end of filling in aoi tables for the current child aoi

              					} // have a new, unique child aoi to add to the db
              					else{
								echo "<br>   DATA ENTRY ERROR: AOI child name $derived_child_aoi_name already exists. Please try again.</br>";
							    }


							} // end have valid child aoiname
							else{
								echo "<br>   DATA ENTRY ERROR: Invalid AOI child name:  $child_aoi. Please try again.</br>";
							}

					} // end at least one child aoi for loop

	      	} // end $child_array_size is greater than zero

		// END OF ADDING CHILDREN AOIS

		header("Status: 302 redirection");
		header("Location: aoi_edit_basic_search_results.php?aoiName=".$_SESSION['aoi_name']."&aoiId=".$_SESSION['aoi_id']."");

	 } // end user requested to save changes as new aoi(s)


	 //User request to cancel/revert editing changes
	 if($_POST['resetChangesCreateAsNewAOI'] )
	 {
	 	$_SESSION['create_new_aoi_from_blank'] = "true";

		header("Status: 302 redirection");
	    header("Location: aoi_edit_basic_search_results.php?aoiName=".""."&aoiId=0");

	 }

	 //User request to cancel/revert editing changes
	 if($_POST['resetChangesNewFromSelectedAOI'] )
	 {
	 	$_SESSION['create_new_aoi_from_selected'] = "true";

		header("Status: 302 redirection");
	    header("Location: aoi_edit_basic_search_results.php?aoiName=".$_SESSION['aoi_name']."&aoiId=".$_SESSION['aoi_id']."");
	 }


	 //User request to cancel/revert editing changes
	 if($_POST['resetChangesModifySelectedAOI'] )
	 {
	    $_SESSION['modify_selected_aoi'] = "true";
		header("Status: 302 redirection");
	    header("Location: aoi_edit_basic_search_results.php?aoiName=".$_SESSION['aoi_name']."&aoiId=".$_SESSION['aoi_id']."");
	 }

	 //User request to exit the AOI Editor
	 if($_POST['logoutAOIEditor'] )
	 {

	 	// Cancel the user's PHP Session ID to force extinction of user authentication
		// Note: session_destroy() will reset your session and you will lose all your stored session data
		// session_destroy();

		// Reset the user name and user agent to blank values
		$_SESSION['user_name'] = "";
		$_SESSION['agent'] = "";

	 	// Redirect the user to AOI "viewer" with blank data
		$_SESSION['aoi_selected'] = "false";

		$_SESSION['aoi_name'] = "";
		$_SESSION['aoi_id'] = 0;

		// Ensure PHP finishes writing the session information before page redirect gets underway
		session_write_close();

	        header("Status: 302 redirection");
		header("Location: aoi_edit_basic_search_results.php?aoiName=".""."&aoiId=0");
	 }

?>
