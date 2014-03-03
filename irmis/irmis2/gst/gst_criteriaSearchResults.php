<?
	if (isset($_POST['sentData']))
	{
		include('gst_i_common.php');
		header('Content-type: text/xml');
	    $data = $_POST['sentData'];
	    $dom = new domDocument;
		$dom->loadXML($data);
		$xml = simplexml_import_dom($dom);

	    // parse the incoming sentData as XML elements
		// should see angle brackets around each xml element...

		if ($xml)
		{
			$majorCategoryID = $xml->majorCategoryID[0];
	        $minorCategoryID = $xml->minorCategoryID[0];
	        $placeHolderID = $xml->placeHolderID[0];
	        $searchBoxIDValue = $xml->searchBoxIDValue[0];
	        $tabLinksSectionID = $xml->tabLinksSectionID[0];
	        $allTabContentSectionID = $xml->allTabContentSectionID[0];
	        $searchTabSectionID = $xml->searchTabSectionID[0];
	        $searchTabID = $xml->searchTabID[0];
	    }
	    else
	    {
		    echo "ERROR could not parse xml string from sent data<br />";
	    }

	    include_once('db.inc');
	    $host = $db_host;
		$user = $db_user_read_name;
		$passwordsql = $db_user_read_passwd;
		$dmbname = $db_name_production_1;
		$dbc = mysql_connect($host, $user, $passwordsql);
		mysql_select_db($dmbname);

	    // Need to replace the '*' at the beginning with a '%' for the sql select statement.
	    // Sometimes a ereg is used, so for those, you have to take away the '^' from the beginning in order to search the whole string

	    $searchStringEreg = $searchBoxIDValue;
	    //$searchString = strip_tags($searchBoxIDValue);
	    $searchString = mysql_real_escape_string($searchBoxIDValue);
		$searchString = ereg_replace('_', '\_', $searchString);
		$searchString = ereg_replace('%', '\%', $searchString);

		$checkEreg = "^";

		if (ereg ('^\*', $searchString))
		{
			$searchString = substr($searchString, 1);
			$searchStringEreg = substr($searchBoxIDValue, 1);
			$checkEreg = "";
			$searchString = '%'.$searchString;
		}
		$searchStringEreg = ereg_replace('\\\\', '\\\\', $searchStringEreg);
		$searchStringEreg = ereg_replace('\*', '\*', $searchStringEreg);
		$searchStringEreg = ereg_replace('\^', '\^', $searchStringEreg);
		$searchStringEreg = ereg_replace('\$', '\$', $searchStringEreg);
		$searchStringEreg = ereg_replace('\.', '\.', $searchStringEreg);
		$searchStringEreg = ereg_replace('\|', '\|', $searchStringEreg);
		$searchStringEreg = ereg_replace('\(', '\(', $searchStringEreg);
		$searchStringEreg = ereg_replace('\)', '\)', $searchStringEreg);
		$searchStringEreg = ereg_replace('\+', '\+', $searchStringEreg);
		$searchStringEreg = ereg_replace('\{', '\{', $searchStringEreg);
		$searchStringEreg = ereg_replace('\}', '\}', $searchStringEreg);
		$searchStringEreg = ereg_replace('\?', '\?', $searchStringEreg);
		$searchStringEreg = ereg_replace('\[', '\[', $searchStringEreg);
		$searchStringEreg = ereg_replace('\]', '\]', $searchStringEreg);
		$searchStringEreg = $checkEreg.$searchStringEreg;



		switch ($majorCategoryID)
		{
			case 'AOI':
				$count_aoi_all = 0;
				$count_aoi_name = 0;
				$count_aoi_machine = 0;
				$count_aoi_technical_system = 0;
				$count_aoi_description = 0;
				$count_aoi_criticality = 0;
				$count_aoi_ioc_name = 0;
				$count_aoi_keywords = 0;
				$count_aoi_status = 0;
				$count_aoi_cognizant_person = 0;
				$count_aoi_customer_group = 0;

				if ( ($minorCategoryID == 'AOI NAME') || ($minorCategoryID == '--ALL--') )
				{
					$query_aoi_name = "SELECT DISTINCT aoi.aoi_id FROM aoi WHERE aoi_name like '".$searchString."%'";
					$result_aoi_name = mysql_query($query_aoi_name);
					$count_aoi_name = mysql_numrows($result_aoi_name);

				}
	       		if ( ($minorCategoryID == 'MACHINE') || $minorCategoryID == '--ALL--')
	       		{
			      	$query_aoi_machine = "SELECT DISTINCT aoi.aoi_id FROM aoi, aoi_machine, machine WHERE aoi.aoi_id = aoi_machine.aoi_id and aoi_machine.machine_id = machine.machine_id and machine.machine like '".$searchString."%'";
			      	$result_aoi_machine = mysql_query($query_aoi_machine);
			      	$count_aoi_machine = mysql_numrows($result_aoi_machine);
	       		}
	       		if ( ($minorCategoryID == 'TECHNICAL SYSTEM') || $minorCategoryID == '--ALL--')
	       		{
			      	$query_aoi_technical_system = "SELECT DISTINCT aoi.aoi_id FROM aoi, aoi_techsys, technical_system WHERE aoi.aoi_id = aoi_techsys.aoi_id and aoi_techsys.technical_system_id = technical_system.technical_system_id and technical_system.technical_system like '".$searchString."%'";
			      	$result_aoi_technical_system = mysql_query($query_aoi_technical_system);
			      	$count_aoi_technical_system = mysql_numrows($result_aoi_technical_system);
	       		}
				if ( ($minorCategoryID == 'DESCRIPTION') || ($minorCategoryID == '--ALL--') )
				{
					$query_aoi_description = "SELECT DISTINCT aoi.aoi_id FROM aoi WHERE aoi_description like '".$searchString."%'";
					$result_aoi_description = mysql_query($query_aoi_description);
					$count_aoi_description = mysql_numrows($result_aoi_description);
				}
				if ( ($minorCategoryID == 'CRITICALITY') || ($minorCategoryID == '--ALL--') )
				{
					$query_aoi_criticality = "SELECT DISTINCT aoi.aoi_id FROM aoi, aoi_criticality, criticality_type WHERE aoi.aoi_id = aoi_criticality.aoi_id and aoi_criticality.criticality_id = criticality_type.criticality_id and criticality_type.criticality_classification like '".$searchString."%'";
					$result_aoi_criticality = mysql_query($query_aoi_criticality);
					$count_aoi_criticality = mysql_numrows($result_aoi_criticality);
				}
				if ( ($minorCategoryID == 'KEYWORDS') || ($minorCategoryID == '--ALL--') )
				{
					$query_aoi_keywords = "SELECT DISTINCT aoi.aoi_id FROM aoi WHERE aoi_keyword like '".$searchString."%'";
					$result_aoi_keywords = mysql_query($query_aoi_keywords);
					$count_aoi_keywords = mysql_numrows($result_aoi_keywords);
				}
	       		if ( ($minorCategoryID == 'STATUS') || ($minorCategoryID == '--ALL--') )
	       		{
			      	$query_aoi_status = "SELECT DISTINCT aoi.aoi_id FROM aoi, aoi_status WHERE aoi.aoi_status_id = aoi_status.aoi_status_id and aoi_status.aoi_status like '".$searchString."%'";
			      	$result_aoi_status = mysql_query($query_aoi_status);
			      	$count_aoi_status = mysql_numrows($result_aoi_status);
	       		}
	       		if ( ($minorCategoryID == 'COGNIZANT PERSON') || ($minorCategoryID == '--ALL--') )
	      		 {
			      	$query_aoi_cognizant_person1 = "SELECT DISTINCT aoi.aoi_id FROM aoi, person WHERE aoi.aoi_cognizant1_id = person.person_id and person.first_nm like '".$searchString."%'";
			      	$result_aoi_cognizant_person1 = mysql_query($query_aoi_cognizant_person1);
			      	$count_aoi_cognizant_person1 = mysql_numrows($result_aoi_cognizant_person1);

			      	$query_aoi_cognizant_person2 = "SELECT DISTINCT aoi.aoi_id FROM aoi, person WHERE aoi.aoi_cognizant1_id = person.person_id and person.last_nm like '".$searchString."%'";
			      	$result_aoi_cognizant_person2 = mysql_query($query_aoi_cognizant_person2);
			      	$count_aoi_cognizant_person2 = mysql_numrows($result_aoi_cognizant_person2);

			      	$query_aoi_cognizant_person3 = "SELECT DISTINCT aoi.aoi_id FROM aoi, person WHERE aoi.aoi_cognizant2_id = person.person_id and person.first_nm like '".$searchString."%'";
			      	$result_aoi_cognizant_person3 = mysql_query($query_aoi_cognizant_person3);
			      	$count_aoi_cognizant_person3 = mysql_numrows($result_aoi_cognizant_person3);

			      	$query_aoi_cognizant_person4 = "SELECT DISTINCT aoi.aoi_id FROM aoi, person WHERE aoi.aoi_cognizant2_id = person.person_id and person.last_nm like '".$searchString."%'";
			      	$result_aoi_cognizant_person4 = mysql_query($query_aoi_cognizant_person4);
			      	$count_aoi_cognizant_person4 = mysql_numrows($result_aoi_cognizant_person4);

			      	$count_aoi_cognizant_person = $count_aoi_cognizant_person1 + $count_aoi_cognizant_person2 + $count_aoi_cognizant_person3 + $count_aoi_cognizant_person4;
	       		}
	       		if ( ($minorCategoryID == 'CUSTOMER GROUP') || $minorCategoryID == '--ALL--')
	       		{
			      	$query_aoi_customer_group = "SELECT DISTINCT aoi.aoi_id FROM aoi, group_name WHERE aoi.aoi_customer_group_id = group_name.group_name_id and group_name.group_name like '".$searchString."%'";
			      	$result_aoi_customer_group = mysql_query($query_aoi_customer_group);
			      	$count_aoi_customer_group = mysql_numrows($result_aoi_customer_group);
	       		}
	       		if ( ($minorCategoryID == 'IOC NAME') || $minorCategoryID == '--ALL--')
	       		{
			      	$query_aoi_ioc_name = "select DISTINCT aoi.aoi_id from ioc, ioc_stcmd_line, aoi_ioc_stcmd_line, aoi where ioc_nm like '".$searchString."%' and aoi.aoi_id = aoi_ioc_stcmd_line.aoi_id AND aoi_ioc_stcmd_line.ioc_stcmd_line_id = ioc_stcmd_line.ioc_stcmd_line_id AND ioc_stcmd_line.ioc_id = ioc.ioc_id";
			      	$result_aoi_ioc_name = mysql_query($query_aoi_ioc_name);
			      	$count_aoi_ioc_name = mysql_numrows($result_aoi_ioc_name);
	       		}

			    $count_aoi_all = $count_aoi_name
			   			       + $count_aoi_machine
				   		       + $count_aoi_technical_system
				   		       + $count_aoi_description
			    			   + $count_aoi_criticality
			    			   + $count_aoi_ioc_name
			   			       + $count_aoi_keywords
			   			       + $count_aoi_status
			   		 		   + $count_aoi_cognizant_person
			   	               + $count_aoi_customer_group;

			    $num = $count_aoi_all;
			    break;

	    case 'IOC':
	    	$count_ioc_all = 0;
	   	 	$count_ioc_name = 0;
	    	$count_ioc_system = 0;
	    	$count_ioc_location = 0;
	    	$count_ioc_cognizant_technician = 0;
	    	$count_ioc_cognizant_developer = 0;

	       if ( ($minorCategoryID == 'IOC NAME') || $minorCategoryID == '--ALL--')
	       {
			      $query_ioc_name = "SELECT DISTINCT ioc.ioc_id FROM ioc WHERE ioc.ioc_nm like '".$searchString."%'";
			      $result_ioc_name = mysql_query($query_ioc_name);
			      $count_ioc_name = mysql_numrows($result_ioc_name);
	       }
		   if ( ($minorCategoryID == 'STATUS') || $minorCategoryID == '--ALL--')
	       {
			      $query_ioc_status = "SELECT DISTINCT ioc.ioc_id FROM ioc, ioc_status WHERE ioc_status.ioc_status like '".$searchString."%' and ioc.ioc_status_id = ioc_status.ioc_status_id";
			      $result_ioc_status = mysql_query($query_ioc_status);
			      $count_ioc_status = mysql_numrows($result_ioc_status);
	       }
	       if ( ($minorCategoryID == 'SYSTEM') || $minorCategoryID == '--ALL--')
	       {
			      $query_ioc_system = "SELECT DISTINCT ioc.ioc_id FROM ioc WHERE ioc.system like '".$searchString."%'";
			      $result_ioc_system = mysql_query($query_ioc_system);
			      $count_ioc_system = mysql_numrows($result_ioc_system);
	       }
	       if ( ($minorCategoryID == 'LOCATION') || $minorCategoryID == '--ALL--')
	       {
			      $query_ioc_location = "SELECT DISTINCT ioc.ioc_id FROM ioc, aps_ioc WHERE ioc.ioc_id = aps_ioc.ioc_id and aps_ioc.location like '".$searchString."%'";
			      $result_ioc_location = mysql_query($query_ioc_location);
			      $count_ioc_location = mysql_numrows($result_ioc_location);
	       }
	       if ( ($minorCategoryID == 'COGNIZANT TECHNICIAN') || $minorCategoryID == '--ALL--')
	       {
			      $query_ioc_cognizant_technician1 = "SELECT DISTINCT ioc.ioc_id FROM ioc, aps_ioc, person WHERE ioc.ioc_id = aps_ioc.ioc_id and aps_ioc.cog_technician_id = person.person_id and person.first_nm like '".$searchString."%'";
			      $result_ioc_cognizant_technician1 = mysql_query($query_ioc_cognizant_technician1);
			      $count_ioc_cognizant_technician1 = mysql_numrows($result_ioc_cognizant_technician1);

			      $query_ioc_cognizant_technician2 = "SELECT DISTINCT ioc.ioc_id FROM ioc, aps_ioc, person WHERE ioc.ioc_id = aps_ioc.ioc_id and aps_ioc.cog_technician_id = person.person_id and person.last_nm like '".$searchString."%'";
			      $result_ioc_cognizant_technician2 = mysql_query($query_ioc_cognizant_technician2);
			      $count_ioc_cognizant_technician2 = mysql_numrows($result_ioc_cognizant_technician2);

			      $count_ioc_cognizant_technician = $count_ioc_cognizant_technician1 + $count_ioc_cognizant_technician2;
	       }
	       if ( ($minorCategoryID == 'COGNIZANT DEVELOPER') || $minorCategoryID == '--ALL--')
	       {
			      $query_ioc_cognizant_developer1 = "SELECT DISTINCT ioc.ioc_id FROM ioc, aps_ioc, person WHERE ioc.ioc_id = aps_ioc.ioc_id and aps_ioc.cog_developer_id = person.person_id and person.first_nm like '".$searchString."%'";
			      $result_ioc_cognizant_developer1 = mysql_query($query_ioc_cognizant_developer1);
			      $count_ioc_cognizant_developer1 = mysql_numrows($result_ioc_cognizant_developer1);

			      $query_ioc_cognizant_developer2 = "SELECT DISTINCT ioc.ioc_id FROM ioc, aps_ioc, person WHERE ioc.ioc_id = aps_ioc.ioc_id and aps_ioc.cog_developer_id = person.person_id and person.last_nm like '".$searchString."%'";
			      $result_ioc_cognizant_developer2 = mysql_query($query_ioc_cognizant_developer2);
			      $count_ioc_cognizant_developer2 = mysql_numrows($result_ioc_cognizant_developer2);

			      $count_ioc_cognizant_developer = $count_ioc_cognizant_developer1 + $count_ioc_cognizant_developer2;
	       }

	       $count_ioc_all = $count_ioc_name
	       				  + $count_ioc_status
	       				  + $count_ioc_system
	       				  + $count_ioc_location
	       				  + $count_ioc_cognizant_technician
	       				  + $count_ioc_cognizant_developer;

	       $num = $count_ioc_all;
	    		break;

		case 'PLC':
		        // Initialize plc query result variables to zero

		        $count_plc_all = 0;
		        $count_plc_name = 0;
		        $count_plc_description = 0;
		        $count_plc_location = 0;
		        $count_plc_ioc_name = 0;

			    if ( ($minorCategoryID == 'PLC NAME') || $minorCategoryID == '--ALL--')
	            {
			      $query_plc_name = "SELECT DISTINCT component.component_id FROM component, plc WHERE component_instance_name like
			      '".$searchString."%' and component_instance_name like 'plc_%' and plc.component_id = component.component_id";
			      $result_plc_name = mysql_query($query_plc_name);
			      $count_plc_name = mysql_numrows($result_plc_name);
			    }

			    if ( ($minorCategoryID == 'DESCRIPTION') || $minorCategoryID == '--ALL--')
			    {
				  $query_plc_description = "SELECT DISTINCT plc_id FROM plc WHERE plc_description like '".$searchString."%'";
				  $result_plc_description = mysql_query($query_plc_description);
				  $count_plc_description = mysql_numrows($result_plc_description);
			    }

				if ( ($minorCategoryID == 'LOCATION') || $minorCategoryID == '--ALL--')
				{
					// Need to obtain location information for each plc, then check if global search string is a match

			   	    // Obtain list of all plc names and plc IDs
			   	    $plc_name_id_array = getPLCNameList();

                    foreach ($plc_name_id_array as $plc_id => $plc_name)
                    {
                            $plc_location_array = array();

                    		// Obtain the location of each plc
                    		$plc_location_array = getPLCLocation($plc_name);

			   				// Check to see if have a match on plcs location with the global search string

			   				$plc_location_matched = 0;

			   				foreach ($plc_location_array as $value)
			   				{
			   					if (eregi($searchStringEreg,$value))
			   					{
					   				$plc_location_matched = 1;
					   			}
			   				}

			   				if ($plc_location_matched == 1)
			   				{
			   					$count_plc_location++;
			   				}


					}  // end foreach
				}

	            if ( ($minorCategoryID == 'IOC NAME') || $minorCategoryID == '--ALL--')
	            {
					   // Obtain list of all plc component ids

					   $query_plc_component_id = "SELECT DISTINCT component.component_id FROM component, plc WHERE component_instance_name like 'plc_%' and plc.component_id = component.component_id";
					   $result_plc_component_id = mysql_query($query_plc_component_id);

					   if ($result_plc_component_id)
					   {
					     while ($plc_component_id_row = mysql_fetch_array($result_plc_component_id))
					     {
					   		$plc_component_id = $plc_component_id_row[0];

					   		// Obtain ioc name that matches to plc component id using parent/child hierarchy in component_rel table

					   		$child_component_id = $plc_component_id;
					   		$safety_count = 0;
					   		while ($child_component_id != '1' && $safety_count<10)
					   		{
					   			$safety_count ++;

					   			$query_plc_ioc_name = "SELECT DISTINCT parent_component_id, logical_desc FROM component_rel WHERE child_component_id = ".$child_component_id." AND component_rel_type_id = 1";
					   			$result_plc_ioc_name = mysql_query($query_plc_ioc_name);
					   			$ctlParent = mysql_fetch_array($result_plc_ioc_name);
					   			$child_component_id = $ctlParent[0];

					   			if ($child_component_id == 1)
					   			{
					   			 	// Have obtained the ioc as the component
					   			 	// Finally,check to see if ioc name is a match to the users global search string

					   				if (eregi($searchStringEreg,$ctlParent[1]))
					   				    $count_plc_ioc_name ++;
					   			}
					   		}
					   	}
					  }
			    }

			    $count_plc_all = $count_plc_name + $count_plc_description + $count_plc_location + $count_plc_ioc_name;
				$num = $count_plc_all;
			    break;

	       case 'COMPONENT TYPE':
		        $count_component_all = 0;
		        $count_component_type = 0;
		        $count_component_description = 0;
		        $count_component_manufacturer = 0;
				$count_component_form_factor = 0;
				$count_component_function = 0;
				$count_component_cognizant_person = 0;

			    if ( ($minorCategoryID == 'COMPONENT TYPE') || $minorCategoryID == '--ALL--')
	            {
			      $query_component_type = "SELECT DISTINCT component_type_id FROM component_type WHERE component_type_name like '".$searchString."%'";
			      $result_component_type = mysql_query($query_component_type);
			      $count_component_type = mysql_numrows($result_component_type);
			    }
			    if ( ($minorCategoryID == 'DESCRIPTION') || $minorCategoryID == '--ALL--')
	            {
			      $query_component_description = "SELECT DISTINCT component_type_id FROM component_type WHERE description like '".$searchString."%'";
			      $result_component_description = mysql_query($query_component_description);
			      $count_component_description = mysql_numrows($result_component_description);
			    }
			    if ( ($minorCategoryID == 'MANUFACTURER') || $minorCategoryID == '--ALL--')
	            {
			      $query_component_manufacturer = "SELECT DISTINCT component_type.component_type_id FROM component_type, mfg WHERE component_type.mfg_id = mfg.mfg_id and mfg.mfg_name like '".$searchString."%'";
			      $result_component_manufacturer = mysql_query($query_component_manufacturer);
			      $count_component_manufacturer = mysql_numrows($result_component_manufacturer);
			    }
			    if ( ($minorCategoryID == 'FORM FACTOR') || $minorCategoryID == '--ALL--')
	            {
			      $query_component_form_factor = "SELECT DISTINCT component_type.component_type_id FROM component_type, form_factor WHERE component_type.form_factor_id = form_factor.form_factor_id and form_factor.form_factor like '".$searchString."%'";
			      $result_component_form_factor = mysql_query($query_component_form_factor);
			      $count_component_form_factor = mysql_numrows($result_component_form_factor);
			    }
			    if ( ($minorCategoryID == 'FUNCTION') || $minorCategoryID == '--ALL--')
	            {
			      $query_component_function = "SELECT DISTINCT component_type.component_type_id FROM component_type, component_type_function, function WHERE component_type.component_type_id = component_type_function.component_type_id and component_type_function.function_id = function.function_id and function.function like '".$searchString."%'";
			      $result_component_function = mysql_query($query_component_function);
			      $count_component_function = mysql_numrows($result_component_function);
			    }
			    if ( ($minorCategoryID == 'COGNIZANT PERSON') || $minorCategoryID == '--ALL--')
	            {
			      $query_component_cognizant_person1 = "SELECT DISTINCT component_type.component_type_id FROM component_type, component_type_person, person WHERE component_type.component_type_id = component_type_person.component_type_id and component_type_person.person_id = person.person_id and person.first_nm like '".$searchString."%'";
			      $result_component_cognizant_person1 = mysql_query($query_component_cognizant_person1);
			      $count_component_cognizant_person1 = mysql_numrows($result_component_cognizant_person1);

			      $query_component_cognizant_person2 = "SELECT DISTINCT component_type.component_type_id FROM component_type, component_type_person, person WHERE component_type.component_type_id = component_type_person.component_type_id and component_type_person.person_id = person.person_id and person.last_nm like '".$searchString."%'";
			      $result_component_cognizant_person2 = mysql_query($query_component_cognizant_person2);
			      $count_component_cognizant_person2 = mysql_numrows($result_component_cognizant_person2);

			      $count_component_cognizant_person = $count_component_cognizant_person1 + $count_component_cognizant_person2;
			    }

	            $count_component_all = $count_component_type
	            					 + $count_component_description
	           						 + $count_component_manufacturer
	           						 + $count_component_form_factor
	           						 + $count_component_function
	           						 + $count_component_cognizant_person;
	           $num = $count_component_all;

			    break;

		case 'EPICS RECORD': //Example to search: B1C0P1:disableDV_noarm_bi
			$count_epics_pv_name = 0;
			$count_epics_alias_name = 0;
			$count_epics_all = 0;

			if ( ($minorCategoryID == 'PV NAME') || ($minorCategoryID == '--ALL--') )
			{
				$query_epics_pv_name = "SELECT DISTINCT rec_id, rec_nm FROM rec, ioc_boot WHERE rec_nm like '".$searchString."%' and rec.ioc_boot_id = ioc_boot.ioc_boot_id and ioc_boot.current_load = 1";
				$result_epics_pv_name = mysql_query($query_epics_pv_name);
				$count_epics_pv_name = mysql_numrows($result_epics_pv_name);
			}

			if ( ($minorCategoryID == 'ALIAS NAME') || ($minorCategoryID == '--ALL--') )
			{
				$query_epics_alias_name = "SELECT DISTINCT alias_nm FROM rec_alias WHERE alias_nm like '".$searchString."%'";
				$result_epics_alias_name = mysql_query($query_epics_alias_name);
				$count_epics_alias_name = mysql_numrows($result_epics_alias_name);
			}

			$count_epics_all = $count_epics_pv_name
							 + $count_epics_alias_name;
			$num = $count_epics_all;

			break;

		case 'HISTORY EPICS RECORD': //Example to search: B1C0P1:disableDV_noarm_bi
				$count_epics_pv_name = 0;
				$count_epics_alias_name = 0;
				$count_epics_history_all = 0;

				if ( ($minorCategoryID == 'PV NAME') || ($minorCategoryID == '--ALL--') )
				{
					$pv_names = array();

					$query_epics_pv_name = "SELECT DISTINCT rec_id, rec_nm FROM rec, ioc_boot WHERE rec_nm like '".$searchString."%' and rec.ioc_boot_id = ioc_boot.ioc_boot_id and ioc_boot.current_load = 1";
					$result_epics_pv_name = mysql_query($query_epics_pv_name);

					$count_epics_pv_name = mysql_numrows($result_epics_pv_name);

					// Get obsolete PV Names, if any

					$query_pv_obsolete = "SELECT DISTINCT rec_history.rec_nm FROM rec_history WHERE rec_history.rec_nm like '".$searchString."%' AND rec_history.rec_nm NOT IN (SELECT DISTINCT rec.rec_nm FROM rec WHERE rec.rec_nm like '".$searchString."%')";
					$result_pv_obsolete = mysql_query($query_pv_obsolete);
					$count_epics_pv_name += mysql_numrows($result_pv_obsolete);

				}

				if ( ($minorCategoryID == 'ALIAS NAME') || ($minorCategoryID == '--ALL--') )
				{
					$query_epics_alias_name = "SELECT DISTINCT alias_nm FROM rec_alias WHERE alias_nm like '".$searchString."%'";
					$result_epics_alias_name = mysql_query($query_epics_alias_name);
					$count_epics_alias_name = mysql_numrows($result_epics_alias_name);

					// Get obsolete PV alias names, if any

					$query_alias_obsolete = "SELECT DISTINCT rec_alias_history.alias_nm FROM rec_alias_history, rec_alias WHERE rec_alias_history.alias_nm like '".$searchString."%' AND rec_alias_history.alias_nm NOT IN (SELECT DISTINCT rec_alias.alias_nm FROM rec_alias WHERE rec_alias.alias_nm like '".$searchString."%')";
					$result_alias_obsolete = mysql_query($query_alias_obsolete);
					$count_epics_alias_name += mysql_numrows($result_alias_obsolete);
				}

				$count_epics_history_all = $count_epics_pv_name + $count_epics_alias_name;

				$num = $count_epics_history_all;

			break;

		case 'INSTALLED COMPONENTS':
			$count_installed_components_all = 0;
			$count_installed_components_component_name = 0;
			$count_installed_components_component_type = 0;
			// $count_installed_components_component_manufacturer = 0;
			$count_installed_components_component_form_factor = 0;
			$count_installed_components_component_function = 0;

			if ( ($minorCategoryID == 'INSTALLED COMPONENT NAME') || ($minorCategoryID == '--ALL--') )
			{
				$query_installed_components_component_name = "SELECT DISTINCT component_id FROM component WHERE component_instance_name like '".$searchString."%'";
				$result_installed_components_component_name = mysql_query($query_installed_components_component_name);
				$count_installed_components_component_name = mysql_numrows($result_installed_components_component_name);
			}
			if ( ($minorCategoryID == 'COMPONENT TYPE') || ($minorCategoryID == '--ALL--') )
			{
				$query_installed_components_component_type = "SELECT DISTINCT component.component_id FROM component, component_type WHERE component_type.component_type_name like '".$searchString."%' AND component.component_type_id = component_type.component_type_id order by component.component_instance_name";
				$result_installed_components_component_type = mysql_query($query_installed_components_component_type);
				$count_installed_components_component_type = mysql_numrows($result_installed_components_component_type);
			}

			//if ( ($minorCategoryID == 'MANUFACTURER') || ($minorCategoryID == '--ALL--') )
			//{
			//	$query_installed_components_component_manufacturer = "SELECT DISTINCT component.component_id FROM component, component_type, mfg WHERE mfg.mfg_name like '".$searchString."%' AND component.component_type_id = component_type.component_type_id AND component_type.mfg_id = mfg.mfg_id order by component.component_instance_name";
			//	$result_installed_components_component_manufacturer = mysql_query($query_installed_components_component_manufacturer );
			//	$count_installed_components_component_manufacturer = mysql_numrows($result_installed_components_component_manufacturer );
			//}

			if ( ($minorCategoryID == 'FORM FACTOR') || ($minorCategoryID == '--ALL--') )
			{
				$query_installed_components_component_form_factor = "SELECT DISTINCT component.component_id FROM component, component_type, form_factor WHERE form_factor.form_factor like '".$searchString."%' AND component.component_type_id = component_type.component_type_id AND component_type.form_factor_id = form_factor.form_factor_id order by component.component_instance_name";
				$result_installed_components_component_form_factor = mysql_query($query_installed_components_component_form_factor);
				$count_installed_components_component_form_factor = mysql_numrows($result_installed_components_component_form_factor);
			}
			if ( ($minorCategoryID == 'FUNCTION') || ($minorCategoryID == '--ALL--') )
			{
				$query_installed_components_component_function = "SELECT DISTINCT component.component_id FROM component, component_type, component_type_function, function WHERE function.function like '".$searchString."%' AND component.component_type_id = component_type.component_type_id AND component_type.component_type_id = component_type_function.component_type_id and component_type_function.function_id = function.function_id order by component.component_instance_name";
				$result_installed_components_component_function = mysql_query($query_installed_components_component_function);
				$count_installed_components_component_function = mysql_numrows($result_installed_components_component_function);
			}




			$count_installed_components_all = $count_installed_components_component_name
											+ $count_installed_components_component_type
											+ $count_installed_components_component_form_factor
											+ $count_installed_components_component_function;

			$num = $count_installed_components_all;
			break;

		case 'IMS':
			$count_ims_all = 0;
			$count_ims_service_name = 0;
			$count_ims_comment = 0;
			$count_ims_status = 0;

			// Create connection to Nagios database
			$conn_nagios = mysql_connect($db_host_nagios, $db_user_name_nagios, $db_user_pswd_nagios);
			mysql_select_db($db_name_nagios);

			if ( ($minorCategoryID == 'SERVICE NAME') || ($minorCategoryID == '--ALL--') )
			{
				$query_ims_service_name = "SELECT DISTINCT service_object_id FROM nagios_services WHERE display_name like '".$searchString."%'";
				$result_ims_service_name = mysql_query($query_ims_service_name);
				$count_ims_service_name = mysql_numrows($result_ims_service_name);
			}
			if ( ($minorCategoryID == 'COMMENTS') || ($minorCategoryID == '--ALL--') )
			{
				$query_ims_comment = "SELECT DISTINCT comment_id FROM nagios_comments WHERE comment_data like '%".$searchString."%'";
				$result_ims_comment = mysql_query($query_ims_comment);
				$count_ims_comment = mysql_numrows($result_ims_comment);
			}
			if ( ($minorCategoryID == 'STATUS') || ($minorCategoryID == '--ALL--') )
			{
				// Perform user entered string match to four possible Nagios status levels: OK, WARNING, CRITICAL and UNKNOWN

				// OK = 0
				// WARNING = 1
				// CRITICAL = 2
				// UNKNOWN = 3

				$ims_service_status = -1;

				if(stristr('OK ', $searchString) == true) {
					$ims_service_status = 0;
				}
				elseif (stristr('WARNING', $searchString) == true) {
					$ims_service_status = 1;
				}
				elseif (stristr('CRITICAL', $searchString) == true) {
					$ims_service_status = 2;
				}
				elseif (stristr('UNKNOWN', $searchString) == true) {
					$ims_service_status = 3;
				}

				if ($ims_service_status >= 0) {

					$query_ims_status = "SELECT DISTINCT service_object_id FROM nagios_servicestatus WHERE current_state = '".$ims_service_status."'";
					$result_ims_status = mysql_query($query_ims_status);
					$count_ims_status = mysql_numrows($result_ims_status);

				}
			}


			$count_ims_all = $count_ims_service_name + $count_ims_comment + $count_ims_status;

			$num = $count_ims_all;

			mysql_close($conn_nagios);

			break;



		case 'ICMS':
			$count_icms_all = 0;
			$count_icms_content_id = 0;
			$count_icms_title = 0;
			$count_icms_author = 0;
			$count_icms_comments_keywords = 0;

			//$wsdl = "https://icmsdocs.aps.anl.gov/new_docs/groups/secure/wsdl/custom/Search.wsdl";

			$wsdl = "https://icmsdocs.aps.anl.gov/docs/idcplg?IdcService=DISPLAY_URL&dDocName=SEARCH";

			include("db.inc");

			$client = new SoapClient($wsdl, array('login' => $icms_user_name,'password' => $icms_user_passwd, 'trace' => 1));


			if ( ($minorCategoryID == 'CONTENT ID') || ($minorCategoryID == '--ALL--') )
			{

				// build ICMS search string
				$searchStringICMS = "dDocName <substring> `";
				$searchStringDBUG = "dDocName &lt;substring&gt; `";

				// CAD drawings included:
				$end_of_searchStringICMS = "` <and> <not> dDocType <matches> `CAD_Dependency`";
				$end_of_searchStringDBUG = "` <and> <not> dDocType &lt;matches&gt; `CAD_Dependency`";

				// End the search string
				$searchStringICMS = $searchStringICMS . $searchString . $end_of_searchStringICMS;
				$searchStringDBUG = $searchStringDBUG . $searchString . $end_of_searchStringDBUG;

				// resultCount is 1 million because this is a magic number for "more matched documents than possible"
				// $param = array('queryText' => $searchStringICMS, 'resultCount' => 1000000);

				$param = array("queryText" => $searchStringICMS, "sortField" => "dInDate", "sortOrder" => "desc", "resultCount" => 1000000);

				$retVal = $client->AdvancedSearch($param);

				$n = 0;

				$search_results = $retVal->AdvancedSearchResult->SearchResults;

				if(isset($search_results)) {

					foreach ($search_results as $key => $value) {

						if ($done)
							break;

						if (gettype($key) != 'integer') { // then we have one and only one result
							$done = true;
						}

						$n++;

					}

					$count_icms_content_id = $n;
				}


			}

			if ( ($minorCategoryID == 'TITLE') || ($minorCategoryID == '--ALL--') )
			{

				// build ICMS search string
				$searchStringICMS = "dDocTitle <substring> `";
				$searchStringDBUG = "dDocTitle &lt;substring&gt; `";

				// CAD drawings included:
				$end_of_searchStringICMS = "` <and> <not> dDocType <matches> `CAD_Dependency`";
				$end_of_searchStringDBUG = "` <and> <not> dDocType &lt;matches&gt; `CAD_Dependency`";

				// End the search string
		    	$searchStringICMS = $searchStringICMS . $searchString . $end_of_searchStringICMS;
	    		$searchStringDBUG = $searchStringDBUG . $searchString . $end_of_searchStringDBUG;

	    		// resultCount is 1 million because this is a magic number for "more matched documents than possible"
				// $param = array('queryText' => $searchStringICMS, 'resultCount' => 1000000);

				$param = array("queryText" => $searchStringICMS, "sortField" => "dInDate", "sortOrder" => "desc", "resultCount" => 1000000);

				$retVal = $client->AdvancedSearch($param);

	    		$n = 0;

				$search_results = $retVal->AdvancedSearchResult->SearchResults;

				if(isset($search_results)) {

					foreach ($search_results as $key => $value) {

						if ($done)
							break;

						if (gettype($key) != 'integer') { // then we have one and only one result
							$done = true;
						}

						$n++;

					}

					$count_icms_title = $n;
				}


			}
			if ( ($minorCategoryID == 'AUTHOR') || ($minorCategoryID == '--ALL--') )
			{

				// build ICMS search string
				$searchStringICMS = "xDocAuthor <substring> `";
				$searchStringDBUG = "xDocAuthor &lt;substring&gt; `";

				// CAD drawings included:
				$end_of_searchStringICMS = "` <and> <not> dDocType <matches> `CAD_Dependency`";
				$end_of_searchStringDBUG = "` <and> <not> dDocType &lt;matches&gt; `CAD_Dependency`";

				// End the search string
				$searchStringICMS = $searchStringICMS . $searchString . $end_of_searchStringICMS;
				$searchStringDBUG = $searchStringDBUG . $searchString . $end_of_searchStringDBUG;

				// resultCount is 1 million because this is a magic number for "more matched documents than possible"
				// $param = array('queryText' => $searchStringICMS, 'resultCount' => 1000000);

				$param = array("queryText" => $searchStringICMS, "sortField" => "dInDate", "sortOrder" => "desc", "resultCount" => 1000000);

				$retVal = $client->AdvancedSearch($param);

				$n = 0;

				$search_results = $retVal->AdvancedSearchResult->SearchResults;

				if(isset($search_results)) {

					foreach ($search_results as $key => $value) {

						if ($done)
							break;

						if (gettype($key) != 'integer') { // then we have one and only one result
							$done = true;
						}

						$n++;

					}

					$count_icms_author = $n;
				}

			}

			if ( ($minorCategoryID == 'COMMENTS') || ($minorCategoryID == '--ALL--') )
			{

				// build ICMS search string
				$searchStringICMS = "xComments <substring> `";
				$searchStringDBUG = "xComments &lt;substring&gt; `";

				// CAD drawings included:
				$end_of_searchStringICMS = "` <and> <not> dDocType <matches> `CAD_Dependency`";
				$end_of_searchStringDBUG = "` <and> <not> dDocType &lt;matches&gt; `CAD_Dependency`";

				// End the search string
		    	$searchStringICMS = $searchStringICMS . $searchString . $end_of_searchStringICMS;
	    		$searchStringDBUG = $searchStringDBUG . $searchString . $end_of_searchStringDBUG;

	    		// resultCount is 1 million because this is a magic number for "more matched documents than possible"
				// $param = array('queryText' => $searchStringICMS, 'resultCount' => 1000000);

				$param = array("queryText" => $searchStringICMS, "sortField" => "dInDate", "sortOrder" => "desc", "resultCount" => 1000000);

				$retVal = $client->AdvancedSearch($param);

	    		$n = 0;

				$search_results = $retVal->AdvancedSearchResult->SearchResults;

				if(isset($search_results)) {

					foreach ($search_results as $key => $value) {

						if ($done)
							break;

						if (gettype($key) != 'integer') { // then we have one and only one result
							$done = true;
						}

						$n++;

					}

					$count_icms_comments_keywords = $n;
				}

            }



			$count_icms_all = $count_icms_content_id + $count_icms_title + $count_icms_author + $count_icms_comments_keywords;

			$num = $count_icms_all;
			break;
	   }


	   mysql_close($dbc);

		$doc = new DOMDocument();
	    $root = $doc->createElement('searchResponse');
	    $root = $doc->appendChild($root);

	    //
	    $child = $doc->createElement('majorCategoryID');
	    $child = $root->appendChild($child);

	    $value = $doc->createTextNode($majorCategoryID);
	    $value = $child->appendChild($value);

	    //
	    $child = $doc->createElement('minorCategoryID');
	    $child = $root->appendChild($child);

	    $value = $doc->createTextNode($minorCategoryID);
	    $value = $child->appendChild($value);

	    //
	    $child = $doc->createElement('placeHolderID');
	    $child = $root->appendChild($child);

	    $value = $doc->createTextNode($placeHolderID);
	    $value = $child->appendChild($value);

	    //
	    $child = $doc->createElement('searchBoxIDValue');
	    $child = $root->appendChild($child);

	    $value = $doc->createTextNode($searchBoxIDValue);
	    $value = $child->appendChild($value);

	    //
	    $child = $doc->createElement('tabLinksSectionID');
	    $child = $root->appendChild($child);

	    $value = $doc->createTextNode($tabLinksSectionID);
	    $value = $child->appendChild($value);

	    //
	    $child = $doc->createElement('allTabContentSectionID');
	    $child = $root->appendChild($child);

	    $value = $doc->createTextNode($allTabContentSectionID);
	    $value = $child->appendChild($value);

	    //
	    $child = $doc->createElement('searchTabSectionID');
	    $child = $root->appendChild($child);

	    $value = $doc->createTextNode($searchTabSectionID);
	    $value = $child->appendChild($value);

	    //
	    $child = $doc->createElement('searchTabID');
	    $child = $root->appendChild($child);

	    $value = $doc->createTextNode($searchTabID);
	    $value = $child->appendChild($value);

	    //
		$child = $doc->createElement('numberResultsSearch');
	    $child = $root->appendChild($child);

		$value = $doc->createTextNode($num);
	    $value = $child->appendChild($value);

		$xml_string = $doc->saveXML();
		echo $xml_string;
	}
?>
