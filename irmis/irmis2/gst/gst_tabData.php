<?
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
        $originalSearchString = $xml->searchString[0];
        $header = $xml->header1[0];
    }
    else
    {
	    echo "ERROR could not parse xml string from sent data<br />";
    }

	$checkbox_array = array();
	$count = 0;
	$searchString = "";

    include('gst_i_common.php');
	include_once('db.inc');
	$host = $db_host;
	$user = $db_user_read_name;
	$passwordsql = $db_user_read_passwd;
	$dmbname = $db_name_production_1;
	$dbc = mysql_connect($host, $user, $passwordsql);
	mysql_select_db($dmbname);

	// Need to replace the '*' at the beginning with a '%' for the sql select statement.
	// Sometimes a ereg is used, so for those, you have to take away the '^' from the beginning in order to search the whole string

	$searchStringEreg = $originalSearchString;
    //$searchString = strip_tags($searchBoxIDValue);
    $searchString = mysql_real_escape_string($originalSearchString);
	$searchString = ereg_replace('_', '\_', $searchString);
	$searchString = ereg_replace('%', '\%', $searchString);

	$checkEreg = "^";

	if (ereg ('^\*', $searchString))
	{
		$searchString = substr($searchString, 1);
		$searchStringEreg = substr($originalSearchString, 1);
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
			// AOI minor categories include: AOI Name, Machine, Tech Sys, Description, Cognizant 1, Cognizant 2, Criticality, IOC Name(s), Customer Group, AOI Status, Keywords

			if ($minorCategoryID == '--ALL--')
            {
	            // Obtain list of all AOIs that meet matching criteria on fields: AOI Name, AOI Description, AOI Cognizant 1 or 2, AOI Criticality, IOC Name (that has an AOI), AOI Customer Group, AOI Status and AOI Keywords
	            // First perform match on IOC Name and save as a list of AOI Names
	            // Obtain list of aoi names where the ioc name matches user's global search string
	            $query_aoi_ioc_name = "SELECT DISTINCT aoi.aoi_name FROM ioc, ioc_stcmd_line, aoi_ioc_stcmd_line, aoi WHERE ioc_nm like '".$searchString."%' AND aoi.aoi_id = aoi_ioc_stcmd_line.aoi_id AND aoi_ioc_stcmd_line.ioc_stcmd_line_id = ioc_stcmd_line.ioc_stcmd_line_id AND ioc_stcmd_line.ioc_id = ioc.ioc_id";
	            $result_aoi_ioc_name = mysql_query($query_aoi_ioc_name);
	            $aoi_name_ioc_match = array();
	            $index = 0;
	            while ($aoi_name_row = mysql_fetch_row($result_aoi_ioc_name) )
	            {
		            $aoi_name_ioc_match[$index] = $aoi_name_row[0];
		            $index++;
	            }
	            // Next, obtain list of AOIs that meet matching criteria on the remaining fields
	            $query_aoi_name = "SELECT DISTINCT aoi_name FROM aoi, person, person as cognizant2, criticality_type, aoi_criticality, machine, aoi_machine, aoi_techsys, technical_system, aoi_status, group_name WHERE (aoi_name like '".$searchString."%' OR machine.machine like '".$searchString."%' OR technical_system.technical_system like '".$searchString."%' OR aoi_description like '".$searchString."%' OR criticality_type.criticality_classification like '".$searchString."%' OR person.first_nm like '".$searchString."%' OR person.last_nm like '".$searchString."%' OR cognizant2.first_nm like '".$searchString."%' OR cognizant2.last_nm like '".$searchString."%' OR aoi_keyword like '".$searchString."%' or aoi_status.aoi_status like '".$searchString."%' OR group_name.group_name like '".$searchString."%') AND person.person_id = aoi.aoi_cognizant1_id AND cognizant2.person_id = aoi.aoi_cognizant2_id AND aoi_criticality.aoi_id = aoi.aoi_id AND aoi_criticality.criticality_id = criticality_type.criticality_id AND aoi.aoi_status_id = aoi_status.aoi_status_id AND aoi.aoi_customer_group_id = group_name.group_name_id and aoi.aoi_id = aoi_techsys.aoi_id AND aoi_techsys.technical_system_id = technical_system.technical_system_id and aoi.aoi_id = aoi_machine.aoi_id AND aoi_machine.machine_id = machine.machine_id order by aoi.aoi_name";
	            $result_aoi_name_list = mysql_query($query_aoi_name);
	            $aoi_name_remainder_match = array();
	            $index = 0;
	            while ($aoi_name_row = mysql_fetch_row($result_aoi_name_list) )
	            {
		            $aoi_name_remainder_match[$index] = $aoi_name_row[0];
		            $index++;
	            }
	            // Now compare the two lists of AOI names and throw out duplicates
	            $aoi_name_final_list = array_merge($aoi_name_ioc_match, $aoi_name_remainder_match);
	            $aoi_name_final_list = array_unique($aoi_name_final_list);
	            sort($aoi_name_final_list);
	            // Finally, retrieve all desired information on each AOI in the aoi name list and display data to user
	            foreach ($aoi_name_final_list as $aoi_name_final)
	            {
		            $query_aoi_name = "SELECT DISTINCT aoi.aoi_name, aoi.aoi_id FROM aoi, person, person as cognizant2, criticality_type, aoi_criticality, technical_system, aoi_techsys, machine, aoi_machine, aoi_status, group_name WHERE aoi_name = '".$aoi_name_final."' AND person.person_id = aoi.aoi_cognizant1_id AND cognizant2.person_id = aoi.aoi_cognizant2_id AND aoi_criticality.aoi_id = aoi.aoi_id AND aoi_criticality.criticality_id = criticality_type.criticality_id AND aoi.aoi_status_id = aoi_status.aoi_status_id AND aoi.aoi_customer_group_id = group_name.group_name_id and aoi.aoi_id = aoi_techsys.aoi_id AND aoi_techsys.technical_system_id = technical_system.technical_system_id and aoi.aoi_id = aoi_machine.aoi_id AND aoi_machine.machine_id = machine.machine_id order by aoi.aoi_name";
					$result_aoi_name_list = mysql_query($query_aoi_name);
					while ($aoi_name_row = mysql_fetch_row($result_aoi_name_list) )
					{
			    		$aoi_name = $aoi_name_row[0];
			    		$aoi_id = $aoi_name_row[1];
						$id = $header.$count;
				    	$checkbox_array[$count] = "<label for='".$id."'>".($count + 1).".<label> "."<input type='checkbox' id='".$id."' value='".$aoi_id."' onclick=\"openResults('".$header."', '".$aoi_id."', '".$count."');\"><label for='".$id."'>".$aoi_name."</label><BR>";
						$count++;
      				}
      		  	}
  		  	}
	        elseif ($minorCategoryID == 'AOI NAME')
            {
	            $query_aoi_name = "SELECT DISTINCT aoi_name, aoi.aoi_id FROM aoi, person, person as cognizant2, criticality_type, aoi_criticality, aoi_status, group_name WHERE aoi_name like '".$searchString."%' AND person.person_id = aoi.aoi_cognizant1_id AND cognizant2.person_id = aoi.aoi_cognizant2_id AND aoi_criticality.aoi_id = aoi.aoi_id AND aoi_criticality.criticality_id = criticality_type.criticality_id AND aoi.aoi_status_id = aoi_status.aoi_status_id AND aoi.aoi_customer_group_id = group_name.group_name_id order by aoi.aoi_name";
		    	$result_aoi_name_list = mysql_query($query_aoi_name);

		    	while ($aoi_name_row = mysql_fetch_row($result_aoi_name_list) )
		    	{
			    	$aoi_name = $aoi_name_row[0];
			    	$aoi_id = $aoi_name_row[1];
					$id = $header.$count;
				    $checkbox_array[$count] = "<label for='".$id."'>".($count + 1).".<label> "."<input type='checkbox' id='".$id."' value='".$aoi_id."' onclick=\"openResults('".$header."', '".$aoi_id."', '".$count."');\"><label for='".$id."'>".$aoi_name."</label><BR>";
					$count++;
	      		}
      		}
	        elseif ($minorCategoryID == 'MACHINE')
            {
	            $query_aoi_machine = "SELECT DISTINCT aoi_name, aoi.aoi_id FROM aoi, person, person as cognizant2, criticality_type, aoi_criticality, aoi_status, group_name, aoi_machine, machine WHERE aoi.aoi_id = aoi_machine.aoi_id AND aoi_machine.machine_id = machine.machine_id AND machine.machine like '".$searchString."%' AND person.person_id = aoi.aoi_cognizant1_id AND cognizant2.person_id = aoi.aoi_cognizant2_id AND aoi_criticality.aoi_id = aoi.aoi_id AND aoi_criticality.criticality_id = criticality_type.criticality_id AND aoi.aoi_status_id = aoi_status.aoi_status_id AND aoi.aoi_customer_group_id = group_name.group_name_id order by aoi.aoi_name";
		    	$result_aoi_machine_list = mysql_query($query_aoi_machine);

		    	while ($aoi_machine_row = mysql_fetch_row($result_aoi_machine_list) )
		    	{
			    	$aoi_name = $aoi_machine_row[0];
			    	$aoi_id = $aoi_machine_row[1];
					$id = $header.$count;
				    $checkbox_array[$count] = "<label for='".$id."'>".($count + 1).".<label> "."<input type='checkbox' id='".$id."' value='".$aoi_id."' onclick=\"openResults('".$header."', '".$aoi_id."', '".$count."');\"><label for='".$id."'>".$aoi_name."</label><BR>";
					$count++;
		      	}
	      	}
	        elseif ($minorCategoryID == 'TECHNICAL SYSTEM')
            {
	            $query_aoi_technical_system = "SELECT DISTINCT aoi_name, aoi.aoi_id FROM aoi, person, person as cognizant2, criticality_type, aoi_criticality, aoi_status, group_name, aoi_techsys, technical_system WHERE aoi.aoi_id = aoi_techsys.aoi_id AND aoi_techsys.technical_system_id = technical_system.technical_system_id AND technical_system.technical_system like '".$searchString."%' AND person.person_id = aoi.aoi_cognizant1_id AND cognizant2.person_id = aoi.aoi_cognizant2_id AND aoi_criticality.aoi_id = aoi.aoi_id AND aoi_criticality.criticality_id = criticality_type.criticality_id AND aoi.aoi_status_id = aoi_status.aoi_status_id AND aoi.aoi_customer_group_id = group_name.group_name_id order by aoi.aoi_name";
		    	$result_aoi_technical_system_list = mysql_query($query_aoi_technical_system);

		    	while ($aoi_technical_system_row = mysql_fetch_row($result_aoi_technical_system_list) )
		    	{
			    	$aoi_name = $aoi_technical_system_row[0];
			    	$aoi_id = $aoi_technical_system_row[1];
					$id = $header.$count;
				    $checkbox_array[$count] = "<label for='".$id."'>".($count + 1).".<label> "."<input type='checkbox' id='".$id."' value='".$aoi_id."' onclick=\"openResults('".$header."', '".$aoi_id."', '".$count."');\"><label for='".$id."'>".$aoi_name."</label><BR>";
					$count++;
			    }
		    }
	        elseif ($minorCategoryID == 'DESCRIPTION')
            {
	            $query_aoi_description = "SELECT DISTINCT aoi_name, aoi.aoi_id FROM aoi, person, person as cognizant2, criticality_type, aoi_criticality, aoi_status, group_name WHERE aoi_description like '".$searchString."%' AND person.person_id = aoi.aoi_cognizant1_id AND cognizant2.person_id = aoi.aoi_cognizant2_id AND aoi_criticality.aoi_id = aoi.aoi_id AND aoi_criticality.criticality_id = criticality_type.criticality_id AND aoi.aoi_status_id = aoi_status.aoi_status_id AND aoi.aoi_customer_group_id = group_name.group_name_id order by aoi.aoi_name";
			    $result_aoi_description_list = mysql_query($query_aoi_description);

			    while ($aoi_description_row = mysql_fetch_row($result_aoi_description_list) )
			    {
			    	$aoi_name = $aoi_description_row[0];
			    	$aoi_id = $aoi_description_row[1];
					$id = $header.$count;
				    $checkbox_array[$count] = "<label for='".$id."'>".($count + 1).".<label> "."<input type='checkbox' id='".$id."' value='".$aoi_id."' onclick=\"openResults('".$header."', '".$aoi_id."', '".$count."');\"><label for='".$id."'>".$aoi_name."</label><BR>";
					$count++;
			    }
		    }
	        elseif ($minorCategoryID == 'CRITICALITY')
            {
	            $query_aoi_criticality = "SELECT DISTINCT aoi_name, aoi.aoi_id FROM aoi, person, person as cognizant2, criticality_type, aoi_criticality, aoi_status, group_name WHERE criticality_type.criticality_classification like '".$searchString."%' AND person.person_id = aoi.aoi_cognizant1_id AND cognizant2.person_id = aoi.aoi_cognizant2_id AND aoi_criticality.aoi_id = aoi.aoi_id AND aoi_criticality.criticality_id = criticality_type.criticality_id AND aoi.aoi_status_id = aoi_status.aoi_status_id AND aoi.aoi_customer_group_id = group_name.group_name_id order by aoi.aoi_name";
			    $result_aoi_criticality_list = mysql_query($query_aoi_criticality);

			    while ($aoi_criticality_row = mysql_fetch_row($result_aoi_criticality_list) )
			    {
			    	$aoi_name = $aoi_criticality_row[0];
			    	$aoi_id = $aoi_criticality_row[1];
					$id = $header.$count;
				    $checkbox_array[$count] = "<label for='".$id."'>".($count + 1).".<label> "."<input type='checkbox' id='".$id."' value='".$aoi_id."' onclick=\"openResults('".$header."', '".$aoi_id."', '".$count."');\"><label for='".$id."'>".$aoi_name."</label><BR>";
					$count++;
			    }
		    }
	        elseif ($minorCategoryID == 'COGNIZANT PERSON')
            {

	            $query_aoi_cognizant_person = "SELECT DISTINCT aoi_name, aoi.aoi_id FROM aoi, person, person as cognizant2, criticality_type, aoi_criticality, aoi_status, group_name WHERE (person.first_nm like '".$searchString."%' OR person.last_nm like '".$searchString."%' OR cognizant2.first_nm like '".$searchString."%' OR cognizant2.last_nm like '".$searchString."%') AND person.person_id = aoi.aoi_cognizant1_id AND cognizant2.person_id = aoi.aoi_cognizant2_id AND aoi_criticality.aoi_id = aoi.aoi_id AND aoi_criticality.criticality_id = criticality_type.criticality_id AND aoi.aoi_status_id = aoi_status.aoi_status_id AND aoi.aoi_customer_group_id = group_name.group_name_id order by aoi.aoi_name";
			    $result_aoi_cognizant_person_list = mysql_query($query_aoi_cognizant_person);

			    while ($aoi_cognizant_person_row = mysql_fetch_row($result_aoi_cognizant_person_list) )
			    {
			    	$aoi_name = $aoi_cognizant_person_row[0];
			    	$aoi_id = $aoi_cognizant_person_row[1];
					$id = $header.$count;
				    $checkbox_array[$count] = "<label for='".$id."'>".($count + 1).".<label> "."<input type='checkbox' id='".$id."' value='".$aoi_id."' onclick=\"openResults('".$header."', '".$aoi_id."', '".$count."');\"><label for='".$id."'>".$aoi_name."</label><BR>";
					$count++;
			    }
		    }
	        elseif ($minorCategoryID == 'KEYWORDS')
            {

	            $query_aoi_keyword = "SELECT DISTINCT aoi_name, aoi.aoi_id FROM aoi, person, person as cognizant2, criticality_type, aoi_criticality, aoi_status, group_name WHERE aoi_keyword like '".$searchString."%' AND person.person_id = aoi.aoi_cognizant1_id AND cognizant2.person_id = aoi.aoi_cognizant2_id AND aoi_criticality.aoi_id = aoi.aoi_id AND aoi_criticality.criticality_id = criticality_type.criticality_id AND aoi.aoi_status_id = aoi_status.aoi_status_id AND aoi.aoi_customer_group_id = group_name.group_name_id order by aoi.aoi_name";
			    $result_aoi_keyword_list = mysql_query($query_aoi_keyword);

			    while ($aoi_keyword_row = mysql_fetch_row($result_aoi_keyword_list) )
			    {
			    	$aoi_name = $aoi_keyword_row[0];
			    	$aoi_id = $aoi_keyword_row[1];
					$id = $header.$count;
				    $checkbox_array[$count] = "<label for='".$id."'>".($count + 1).".<label> "."<input type='checkbox' id='".$id."' value='".$aoi_id."' onclick=\"openResults('".$header."', '".$aoi_id."', '".$count."');\"><label for='".$id."'>".$aoi_name."</label><BR>";
					$count++;
			    }
		    }
	        elseif ($minorCategoryID == 'STATUS')
            {

	            $query_aoi_status = "SELECT DISTINCT aoi_name, aoi.aoi_id FROM aoi, person, person as cognizant2, criticality_type, aoi_criticality, aoi_status, group_name WHERE aoi_status.aoi_status like '".$searchString."%' AND person.person_id = aoi.aoi_cognizant1_id AND cognizant2.person_id = aoi.aoi_cognizant2_id AND aoi_criticality.aoi_id = aoi.aoi_id AND aoi_criticality.criticality_id = criticality_type.criticality_id AND aoi.aoi_status_id = aoi_status.aoi_status_id AND aoi.aoi_customer_group_id = group_name.group_name_id order by aoi.aoi_name";
			    $result_aoi_status_list = mysql_query($query_aoi_status);

			    while ($aoi_status_row = mysql_fetch_row($result_aoi_status_list) )
			    {
			    	$aoi_name = $aoi_status_row[0];
			    	$aoi_id = $aoi_status_row[1];
					$id = $header.$count;
				    $checkbox_array[$count] = "<label for='".$id."'>".($count + 1).".<label> "."<input type='checkbox' id='".$id."' value='".$aoi_id."' onclick=\"openResults('".$header."', '".$aoi_id."', '".$count."');\"><label for='".$id."'>".$aoi_name."</label><BR>";
					$count++;
			    }
		    }
	        elseif ($minorCategoryID == 'CUSTOMER GROUP')
            {
	            $query_aoi_customer_group = "SELECT DISTINCT aoi_name, aoi.aoi_id FROM aoi, person, person as cognizant2, criticality_type, aoi_criticality, aoi_status, group_name WHERE group_name.group_name like '".$searchString."%' AND person.person_id = aoi.aoi_cognizant1_id AND cognizant2.person_id = aoi.aoi_cognizant2_id AND aoi_criticality.aoi_id = aoi.aoi_id AND aoi_criticality.criticality_id = criticality_type.criticality_id AND aoi.aoi_status_id = aoi_status.aoi_status_id AND aoi.aoi_customer_group_id = group_name.group_name_id order by aoi.aoi_name";
			    $result_aoi_customer_group_list = mysql_query($query_aoi_customer_group);

			    while ($aoi_customer_group_row = mysql_fetch_row($result_aoi_customer_group_list) )
			    {
			    	$aoi_name = $aoi_customer_group_row[0];
			    	$aoi_id = $aoi_customer_group_row[1];
					$id = $header.$count;
				    $checkbox_array[$count] = "<label for='".$id."'>".($count + 1).".<label> "."<input type='checkbox' id='".$id."' value='".$aoi_id."' onclick=\"openResults('".$header."', '".$aoi_id."', '".$count."');\"><label for='".$id."'>".$aoi_name."</label><BR>";
					$count++;
			    }
		    }
			elseif ($minorCategoryID == 'IOC NAME')
            {
		    	// Obtain list of aoi names where the ioc name matches user's global search string
		    	$query_aoi_names = "SELECT DISTINCT aoi.aoi_name, ioc.ioc_nm FROM ioc, ioc_stcmd_line, aoi_ioc_stcmd_line, aoi WHERE ioc_nm like '".$searchString."%' AND aoi.aoi_id = aoi_ioc_stcmd_line.aoi_id AND aoi_ioc_stcmd_line.ioc_stcmd_line_id = ioc_stcmd_line.ioc_stcmd_line_id AND ioc_stcmd_line.ioc_id = ioc.ioc_id";
		    	$result_aoi_names = mysql_query($query_aoi_names);
		    	while ($aoi_name_row = mysql_fetch_row($result_aoi_names) )
		    	{
		            $aoi_name = $aoi_name_row[0];

		            // Obtain full list of information for each aoi
	                $query_aoi = "SELECT DISTINCT aoi_name, aoi.aoi_id FROM aoi, person, person as cognizant2, criticality_type, aoi_criticality, aoi_status, group_name WHERE aoi.aoi_name = '".$aoi_name."' AND person.person_id = aoi.aoi_cognizant1_id AND cognizant2.person_id = aoi.aoi_cognizant2_id AND aoi_criticality.aoi_id = aoi.aoi_id AND aoi_criticality.criticality_id = criticality_type.criticality_id AND aoi.aoi_status_id = aoi_status.aoi_status_id AND aoi.aoi_customer_group_id = group_name.group_name_id";
			    	$result_aoi = mysql_query($query_aoi);

			    	while ($aoi_row = mysql_fetch_row($result_aoi) )
			    	{
				    	$aoi_name = $aoi_row[0];
				    	$aoi_id = $aoi_row[1];
						$id = $header.$count;
					    $checkbox_array[$count] = "<label for='".$id."'>".($count + 1).".<label> "."<input type='checkbox' id='".$id."' value='".$aoi_id."' onclick=\"openResults('".$header."', '".$aoi_id."', '".$count."');\"><label for='".$id."'>".$aoi_name."</label><BR>";
						$count++;
			    	}
			    }
		    }
		    break;

		case 'IOC':
			// IOC has minor categories: IOC Name, IOC System, IOC Location, IOC Cognizant Developer, IOC Cognizant Technician, PV Names, IOC Contents
			if ($minorCategoryID == '--ALL--')
            {
	            $query_ioc_all = "SELECT DISTINCT ioc.ioc_nm, ioc.ioc_id FROM ioc, aps_ioc, person, person as technician WHERE (person.first_nm like '".$searchString."%' OR person.last_nm like '".$searchString."%' or technician.first_nm like '".$searchString."%' OR technician.last_nm like '".$searchString."%' or aps_ioc.location like '".$searchString."%' or ioc.system like '".$searchString."%' or ioc.ioc_nm like '".$searchString."%') AND ioc.ioc_id = aps_ioc.ioc_id AND aps_ioc.cog_technician_id = technician.person_id AND aps_ioc.cog_developer_id = person.person_id order by ioc.ioc_nm";
	            $result_ioc_all = mysql_query($query_ioc_all);

	            while ($ioc_all_row = mysql_fetch_row($result_ioc_all) )
	            {
	            	$ioc_name = $ioc_all_row[0];
	            	$ioc_id = $ioc_all_row[1];
					$id = $header.$count;
				    $checkbox_array[$count] = "<label for='".$id."'>".($count + 1 ).".<label> "."<input type='checkbox' id='".$id."' value='".$ioc_id."' onclick=\"openResults('".$header."', '".$ioc_id."', '".$count."');\"><label for='".$id."'>".$ioc_name."</label><BR>";
					$count++;
	            }
            }
		    elseif ($minorCategoryID == 'IOC NAME')
            {
	            $query_ioc_name = "SELECT DISTINCT ioc.ioc_nm, ioc.ioc_id FROM ioc, aps_ioc, person, person as technician WHERE ioc.ioc_nm like '".$searchString."%' AND ioc.ioc_id = aps_ioc.ioc_id AND aps_ioc.cog_technician_id = technician.person_id AND aps_ioc.cog_developer_id = person.person_id order by ioc.ioc_nm";
	            $result_ioc_name = mysql_query($query_ioc_name);

	            while ($ioc_name_row = mysql_fetch_row($result_ioc_name) )
	            {
	            	$ioc_name = $ioc_name_row[0];
         			$ioc_id = $ioc_name_row[1];
					$id = $header.$count;
				    $checkbox_array[$count] = "<label for='".$id."'>".($count + 1 ).".<label> "."<input type='checkbox' id='".$id."' value='".$ioc_id."' onclick=\"openResults('".$header."', '".$ioc_id."', '".$count."');\"><label for='".$id."'>".$ioc_name."</label><BR>";
					$count++;
	            }
            }
 		    elseif ($minorCategoryID == 'STATUS')
            {

	            $query_ioc_status = "SELECT DISTINCT ioc.ioc_nm, ioc.ioc_id FROM ioc, ioc_status, aps_ioc, person, person as technician WHERE ioc_status.ioc_status like '".$searchString."%' AND ioc_status.ioc_status_id = ioc.ioc_status_id and ioc.ioc_id = aps_ioc.ioc_id AND aps_ioc.cog_technician_id = technician.person_id AND aps_ioc.cog_developer_id = person.person_id order by ioc.ioc_nm";
	            $result_ioc_status = mysql_query($query_ioc_status);

	            while ($ioc_status_row = mysql_fetch_row($result_ioc_status) )
	            {
	            	$ioc_name = $ioc_status_row[0];
         			$ioc_id = $ioc_status_row[1];
					$id = $header.$count;
				    $checkbox_array[$count] = "<label for='".$id."'>".($count + 1 ).".<label> "."<input type='checkbox' id='".$id."' value='".$ioc_id."' onclick=\"openResults('".$header."', '".$ioc_id."', '".$count."');\"><label for='".$id."'>".$ioc_name."</label><BR>";
					$count++;
	            }
            }
		    elseif ($minorCategoryID == 'SYSTEM')
            {

	            $query_ioc_system = "SELECT DISTINCT ioc.ioc_nm, ioc.ioc_id FROM ioc, aps_ioc, person, person as technician WHERE ioc.system like '".$searchString."%' AND ioc.ioc_id = aps_ioc.ioc_id AND aps_ioc.cog_technician_id = technician.person_id AND aps_ioc.cog_developer_id = person.person_id order by ioc.ioc_nm";
	            $result_ioc_system = mysql_query($query_ioc_system);

	            while ($ioc_system_row = mysql_fetch_row($result_ioc_system) )
	            {
	            	$ioc_name = $ioc_system_row[0];
         			$ioc_id = $ioc_system_row[1];
					$id = $header.$count;
				    $checkbox_array[$count] = "<label for='".$id."'>".($count + 1 ).".<label> "."<input type='checkbox' id='".$id."' value='".$ioc_id."' onclick=\"openResults('".$header."', '".$ioc_id."', '".$count."');\"><label for='".$id."'>".$ioc_name."</label><BR>";
					$count++;
	            }
            }
            elseif ($minorCategoryID == 'LOCATION')
            {
	    	    $query_ioc_location = "SELECT DISTINCT ioc.ioc_nm, ioc.ioc_id FROM ioc, aps_ioc, person, person as technician WHERE aps_ioc.location like '".$searchString."%' AND ioc.ioc_id = aps_ioc.ioc_id AND aps_ioc.cog_technician_id = technician.person_id AND aps_ioc.cog_developer_id = person.person_id order by ioc.ioc_nm";
	            $result_ioc_location = mysql_query($query_ioc_location);

	            while ($ioc_location_row = mysql_fetch_row($result_ioc_location) )
	            {
	            	$ioc_name = $ioc_location_row[0];
         			$ioc_id = $ioc_location_row[1];
					$id = $header.$count;
				    $checkbox_array[$count] = "<label for='".$id."'>".($count + 1 ).".<label> "."<input type='checkbox' id='".$id."' value='".$ioc_id."' onclick=\"openResults('".$header."', '".$ioc_id."', '".$count."');\"><label for='".$id."'>".$ioc_name."</label><BR>";
					$count++;
                }
            }
            elseif ($minorCategoryID == 'COGNIZANT TECHNICIAN')
            {
	    	    $query_ioc_cognizant_technician = "SELECT DISTINCT ioc.ioc_nm, ioc.ioc_id FROM ioc, aps_ioc, person, person as technician WHERE (technician.first_nm like '".$searchString."%' OR technician.last_nm like '".$searchString."%') AND ioc.ioc_id = aps_ioc.ioc_id AND aps_ioc.cog_technician_id = technician.person_id AND aps_ioc.cog_developer_id = person.person_id order by ioc.ioc_nm";
	            $result_ioc_cognizant_technician = mysql_query($query_ioc_cognizant_technician);

	            while ($ioc_cognizant_technician_row = mysql_fetch_row($result_ioc_cognizant_technician) )
	            {
	            	$ioc_name = $ioc_cognizant_technician_row[0];
         			$ioc_id = $ioc_cognizant_technician_row[1];
					$id = $header.$count;
				    $checkbox_array[$count] = "<label for='".$id."'>".($count + 1 ).".<label> "."<input type='checkbox' id='".$id."' value='".$ioc_id."' onclick=\"openResults('".$header."', '".$ioc_id."', '".$count."');\"><label for='".$id."'>".$ioc_name."</label><BR>";
					$count++;
                }
            }
		    elseif ($minorCategoryID == 'COGNIZANT DEVELOPER')
            {
	            $query_ioc_cognizant_developer = "SELECT DISTINCT ioc.ioc_nm, ioc.ioc_id FROM ioc, aps_ioc, person, person as technician WHERE (person.first_nm like '".$searchString."%' OR person.last_nm like '".$searchString."%') AND ioc.ioc_id = aps_ioc.ioc_id AND aps_ioc.cog_technician_id = technician.person_id AND aps_ioc.cog_developer_id = person.person_id order by ioc.ioc_nm";
	            $result_ioc_cognizant_developer = mysql_query($query_ioc_cognizant_developer);

	            while ($ioc_cognizant_developer_row = mysql_fetch_row($result_ioc_cognizant_developer) )
	            {
	            	$ioc_name = $ioc_cognizant_developer_row[0];
	         		$ioc_id = $ioc_cognizant_developer_row[1];
					$id = $header.$count;
				    $checkbox_array[$count] = "<label for='".$id."'>".($count + 1 ).".<label> "."<input type='checkbox' id='".$id."' value='".$ioc_id."' onclick=\"openResults('".$header."', '".$ioc_id."', '".$count."');\"><label for='".$id."'>".$ioc_name."</label><BR>";
					$count++;
                }
            }

            break;

		case 'PLC':
			// Major category 'PLC' currently has 4 minor categories: PLC NAME, DESCRIPTION, LOCATION, and IOC NAME
			$plc_location_array = array();
			if ($minorCategoryID == '--ALL--')
			{
				// First perform match on PLC's Name, PLC's location and the PLC's IOC Name and store list of PLC names in an array
				// Strategy here is to get a list of all of the PLCs, then obtain each PLCs location.
				// Each PLC location is then checked to see if there is a match with the global search tool user string
				$plc_final_name_array = array();
				$index = 0;
				$plc_name_array = array();
				$plc_name_array = getPLCNameList();
				foreach ($plc_name_array as $key => $plc_name)
				{
					$plc_ioc_matched = 0;
					$plc_location_array = array();
					$plc_location_array = getPLCLocation($plc_name);
					$plc_location_html_string = "";$plc_location_matched = 0;

					foreach ($plc_location_array as $value)
					{
						if (eregi($searchStringEreg,$value))
						{
							$plc_location_matched = 1;
						}
						$plc_location_html_string = $plc_location_html_string.$value."<br />";
					}
					$plc_ioc_name = getPLCIOCName($plc_name);
					if (eregi($searchStringEreg,$ioc_name)) $plc_ioc_matched = 1;
					if ($plc_location_matched == 1 || $plc_ioc_matched == 1 || eregi($searchStringEreg,$plc_name))
					{
						// Save PLC name to final PLC name list
						$plc_final_name_array[$key] = $plc_name;

					}
				}
				// Next, perform match on remaining PLC field DESCRIPTION
				$query_plc_descr = "SELECT DISTINCT component_instance_name, component.component_id FROM plc, component WHERE plc.plc_description like '".$searchString."%' AND plc.component_id = component.component_id order by component_instance_name";
				$result = mysql_query($query_plc_descr);
				// Continue on with current value of index counter
				while ($result_plc_name_row = mysql_fetch_row($result))
				{
					$index = $result_plc_name_row[1];

					// Check that this plc is not already in the plc final name array
					if (!array_key_exists($index,$plc_final_name_array))
					{
					    // Add plc to the list
						$plc_final_name_array[$index] = $result_plc_name_row[0];
					}
		      	}

				// Last check to make sure have unique plc names
		      	$plc_final_name_array = array_unique($plc_final_name_array);

		      	foreach ($plc_final_name_array as $plc_id => $plc_name)
			    {
					$id = $header.$count;
					$checkbox_array[$count] = "<label for='".$id."'>".($count + 1 ).".<label> "."<input type='checkbox' id='".$id."' value='".$plc_id."' onclick=\"openResults('".$header."', '".$plc_id."', '".$count."');\"><label for='".$id."'>".$plc_name."</label><BR>";
					$count++;
                }
      		}
	      	elseif ($minorCategoryID == 'PLC NAME')
			{
				$query_plc_name = "SELECT DISTINCT component_instance_name, component.component_id  FROM component, plc WHERE
				component_instance_name like '".$searchString."%' and component_instance_name like 'plc_%' and plc.component_id = component.component_id order by component_instance_name";
				$result = mysql_query($query_plc_name);
				while ($result_plc_name_row = mysql_fetch_row($result))
				{
			      	$plc_name = $result_plc_name_row[0];
			      	$plc_id = $result_plc_name_row[1];
					$id = $header.$count;
				    $checkbox_array[$count] = "<label for='".$id."'>".($count + 1 ).".<label> "."<input type='checkbox' id='".$id."' value='".$plc_id."' onclick=\"openResults('".$header."', '".$plc_id."', '".$count."');\"><label for='".$id."'>".$plc_name."</label><BR>";
					$count++;
		      	}
	      	}
	      	elseif ($minorCategoryID == 'DESCRIPTION')
	      	{
		      	$query_plc_descr = "SELECT DISTINCT component_instance_name, component.component_id FROM plc, component WHERE plc_description like '".$searchString."%' AND plc.component_id = component.component_id order by component_instance_name";
		      	$result = mysql_query($query_plc_descr);
		      	while ($result_plc_name_row = mysql_fetch_row($result))
		      	{
			      	$plc_name = $result_plc_name_row[0];
			      	$plc_id = $result_plc_name_row[1];
					$id = $header.$count;
				    $checkbox_array[$count] = "<label for='".$id."'>".($count + 1 ).".<label> "."<input type='checkbox' id='".$id."' value='".$plc_id."' onclick=\"openResults('".$header."', '".$plc_id."', '".$count."');\"><label for='".$id."'>".$plc_name."</label><BR>";
					$count++;
		      	}
	      	}
	      	elseif ($minorCategoryID == 'LOCATION')
	      	{
		      	// Strategy here is to get a list of all of the PLCs, then obtain each PLCs location.
		      	// Each PLC location is then checked to see if there is a match with the global search tool user string.
		      	$plc_name_array = array();
		      	$plc_name_array = getPLCNameList();
		      	foreach ($plc_name_array as $plc_id => $plc_name)
		      	{
			      	$plc_location_array = array();
			      	$plc_location_array = getPLCLocation($plc_name);

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
					   	$id = $header.$count;
						$checkbox_array[$count] = "<label for='".$id."'>".($count + 1 ).".<label> "."<input type='checkbox' id='".$id."' value='".$plc_id."' onclick=\"openResults('".$header."', '".$plc_id."', '".$count."');\"><label for='".$id."'>".$plc_name."</label><BR>";
						$count++;
				   	}
			   	}
		   	}
		   	elseif ($minorCategoryID == 'IOC NAME')
		   	{
			   	// Obtain list of all PLC names, then obtain the IOC name for each PLC.
			   	// Perform a comparison of the IOC name with the global search tool users string to see if there is a match.
			   	$plc_name_array = array();
			   	$plc_name_array = getPLCNameList();
			   	foreach ($plc_name_array as $plc_id => $plc_name)
			   	{
				   	$ioc_name = getPLCIOCName($plc_name);
				   	if (eregi($searchStringEreg,$ioc_name))
				   	{
					   	$id = $header.$count;
						$checkbox_array[$count] = "<label for='".$id."'>".($count + 1 ).".<label> "."<input type='checkbox' id='".$id."' value='".$plc_id."' onclick=\"openResults('".$header."', '".$plc_id."', '".$count."');\"><label for='".$id."'>".$plc_name."</label><BR>";
						$count++;
				   	}
			   	}
		   	}
		   	break;

		case 'COMPONENT TYPE':
			// Minor categories for COMPONENT TYPE include: Component Type Name, Description, Manufacturer, Form Factor, Function, Cognizant Person
			if ($minorCategoryID == '--ALL--')
            {
	            $query_component_function = "SELECT DISTINCT component_type_name, component_type.component_type_id FROM component_type, mfg, form_factor, component_type_function, function, component_type_person, person WHERE (person.first_nm like '".$searchString."%' OR person.last_nm like '".$searchString."%' or form_factor.form_factor like '".$searchString."%' or mfg.mfg_name like '".$searchString."%' or component_type.description like '".$searchString."%' or component_type_name like '".$searchString."%' or function.function like '".$searchString."%') AND component_type.mfg_id = mfg.mfg_id AND component_type.form_factor_id = form_factor.form_factor_id AND component_type.component_type_id = component_type_function.component_type_id AND component_type_function.function_id = function.function_id AND component_type.component_type_id = component_type_person.component_type_id AND component_type_person.person_id = person.person_id order by component_type.component_type_name";
	            $result_component_function = mysql_query($query_component_function);
	            while ($component_row = mysql_fetch_row($result_component_function) )
	            {
		            $component_name = $component_row[0];
		            $component_id = $component_row[1];
					$id = $header.$count;
				    $checkbox_array[$count] = "<label for='".$id."'>".($count + 1 ).".<label> "."<input type='checkbox' id='".$id."' value='".$component_id."' onclick=\"openResults('".$header."', '".$component_id."', '".$count."');\"><label for='".$id."'>".$component_name."</label><BR>";
					$count++;
	            }

	        }
	        elseif ($minorCategoryID == 'COMPONENT TYPE')
            {
	            $query_component_type = "SELECT DISTINCT component_type_name, component_type.component_type_id FROM component_type, mfg, form_factor, component_type_person, person WHERE component_type_name like '".$searchString."%' AND component_type.mfg_id = mfg.mfg_id AND component_type.form_factor_id = form_factor.form_factor_id AND component_type.component_type_id = component_type_person.component_type_id AND component_type_person.person_id = person.person_id order by component_type.component_type_name";
	            $result_component_type = mysql_query($query_component_type);
	            while ($component_type_row = mysql_fetch_row($result_component_type) )
	            {
		            $component_name = $component_type_row[0];
		            $component_id = $component_type_row[1];
					$id = $header.$count;
				    $checkbox_array[$count] = "<label for='".$id."'>".($count + 1 ).".<label> "."<input type='checkbox' id='".$id."' value='".$component_id."' onclick=\"openResults('".$header."', '".$component_id."', '".$count."');\"><label for='".$id."'>".$component_name."</label><BR>";
					$count++;
	            }
            }
            elseif ($minorCategoryID == 'DESCRIPTION')
            {
	            $query_component_description = "SELECT DISTINCT component_type_name, component_type.component_type_id FROM component_type, mfg, form_factor, component_type_person, person WHERE component_type.description like '".$searchString."%' AND component_type.mfg_id = mfg.mfg_id AND component_type.form_factor_id = form_factor.form_factor_id AND component_type.component_type_id = component_type_person.component_type_id AND component_type_person.person_id = person.person_id order by component_type.component_type_name";
	            $result_component_description = mysql_query($query_component_description);
	            while ($component_row = mysql_fetch_row($result_component_description) )
	            {
		            $component_name = $component_row[0];
		            $component_id = $component_row[1];
					$id = $header.$count;
				    $checkbox_array[$count] = "<label for='".$id."'>".($count + 1 ).".<label> "."<input type='checkbox' id='".$id."' value='".$component_id."' onclick=\"openResults('".$header."', '".$component_id."', '".$count."');\"><label for='".$id."'>".$component_name."</label><BR>";
					$count++;
	            }
            }
            elseif ($minorCategoryID == 'MANUFACTURER')
            {
	            $query_component_manufacturer = "SELECT DISTINCT component_type_name, component_type.component_type_id FROM component_type, mfg, form_factor, component_type_person, person WHERE mfg.mfg_name like '".$searchString."%' AND component_type.mfg_id = mfg.mfg_id AND component_type.form_factor_id = form_factor.form_factor_id AND component_type.component_type_id = component_type_person.component_type_id AND component_type_person.person_id = person.person_id order by component_type.component_type_name";
	            $result_component_manufacturer = mysql_query($query_component_manufacturer);
	            while ($component_row = mysql_fetch_row($result_component_manufacturer) )
	            {
		            $component_name = $component_row[0];
		            $component_id = $component_row[1];
					$id = $header.$count;
				    $checkbox_array[$count] = "<label for='".$id."'>".($count + 1 ).".<label> "."<input type='checkbox' id='".$id."' value='".$component_id."' onclick=\"openResults('".$header."', '".$component_id."', '".$count."');\"><label for='".$id."'>".$component_name."</label><BR>";
					$count++;
	            }
            }
            elseif ($minorCategoryID == 'FORM FACTOR')
            {
	            $query_component_form_factor = "SELECT DISTINCT component_type_name, component_type.component_type_id FROM component_type, mfg, form_factor, component_type_person, person WHERE form_factor.form_factor like '".$searchString."%' AND component_type.mfg_id = mfg.mfg_id AND component_type.form_factor_id = form_factor.form_factor_id AND component_type.component_type_id = component_type_person.component_type_id AND component_type_person.person_id = person.person_id order by component_type.component_type_name";
	            $result_component_form_factor = mysql_query($query_component_form_factor);
	            while ($component_row = mysql_fetch_row($result_component_form_factor) )
	            {
		            $component_name = $component_row[0];
		            $component_id = $component_row[1];
					$id = $header.$count;
				    $checkbox_array[$count] = "<label for='".$id."'>".($count + 1 ).".<label> "."<input type='checkbox' id='".$id."' value='".$component_id."' onclick=\"openResults('".$header."', '".$component_id."', '".$count."');\"><label for='".$id."'>".$component_name."</label><BR>";
					$count++;
	            }
            }
            elseif ($minorCategoryID == 'FUNCTION')
            {
	            $query_component_function = "SELECT DISTINCT component_type_name, component_type.component_type_id FROM component_type, mfg, form_factor, component_type_function, function, component_type_person, person WHERE function.function like '".$searchString."%' AND component_type.mfg_id = mfg.mfg_id AND component_type.form_factor_id = form_factor.form_factor_id AND component_type.component_type_id = component_type_function.component_type_id AND component_type_function.function_id = function.function_id AND component_type.component_type_id = component_type_person.component_type_id AND component_type_person.person_id = person.person_id order by component_type.component_type_name";
	            $result_component_function = mysql_query($query_component_function);
	            while ($component_row = mysql_fetch_row($result_component_function) )
	            {
		            $component_name = $component_row[0];
		            $component_id = $component_row[1];
					$id = $header.$count;
				    $checkbox_array[$count] = "<label for='".$id."'>".($count + 1 ).".<label> "."<input type='checkbox' id='".$id."' value='".$component_id."' onclick=\"openResults('".$header."', '".$component_id."', '".$count."');\"><label for='".$id."'>".$component_name."</label><BR>";
					$count++;
	            }
            }
            elseif ($minorCategoryID == 'COGNIZANT PERSON')
            {
	            $query_component_cognizant = "SELECT DISTINCT component_type_name, component_type.component_type_id FROM component_type, mfg, form_factor, component_type_person, person WHERE (person.first_nm like '".$searchString."%' OR person.last_nm like '".$searchString."%') AND component_type.mfg_id = mfg.mfg_id AND component_type.form_factor_id = form_factor.form_factor_id AND component_type.component_type_id = component_type_person.component_type_id AND component_type_person.person_id = person.person_id order by component_type.component_type_name";
	            $result_component_cognizant = mysql_query($query_component_cognizant);
	            while ($component_row = mysql_fetch_row($result_component_cognizant) )
	            {
		            $component_name = $component_row[0];
		            $component_id = $component_row[1];
					$id = $header.$count;
				    $checkbox_array[$count] = "<label for='".$id."'>".($count + 1 ).".<label> "."<input type='checkbox' id='".$id."' value='".$component_id."' onclick=\"openResults('".$header."', '".$component_id."', '".$count."');\"><label for='".$id."'>".$component_name."</label><BR>";
					$count++;
				}
			}
		    break;

		case 'EPICS RECORD':
			// Major category EPICS RECORD has minor categories: PV NAME and ALIAS NAME

			if ($minorCategoryID == '--ALL--')
			{
				$query_pv_alias = "SELECT DISTINCT rec_alias.alias_nm, rec_alias.rec_id from rec_alias WHERE alias_nm like '".$searchString."%'";
				$result_pv_alias = mysql_query($query_pv_alias);

				while ($pv_alias_row = mysql_fetch_row($result_pv_alias) )
				{
					$pv_alias_name = $pv_alias_row[0];
					$pv_id = $pv_alias_row[1];
					$id = $header.$count;
					$checkbox_array[$count] = "<label for='".$id."'>".($count + 1 ).".<label> "."<input type='checkbox' id='".$id."' value='".$pv_id."' onclick=\"openResults('".$header."', '".$pv_id."', '".$count."');\"><label for='".$id."'>".$pv_alias_name."</label><BR>";
					$count++;
				}

				$query_pv_name = "SELECT DISTINCT rec.rec_nm, rec.rec_id FROM rec, rec_type, ioc, ioc_boot WHERE rec_nm like '".$searchString."%' AND rec.ioc_boot_id = ioc_boot.ioc_boot_id and ioc_boot.ioc_id = ioc.ioc_id and ioc_boot.current_load = '1'";
				$result_pv_name = mysql_query($query_pv_name);

				while ($pv_name_row = mysql_fetch_row($result_pv_name) )
				{
					$pv_name = $pv_name_row[0];
					$pv_id = $pv_name_row[1];
					$id = $header.$count;
					$checkbox_array[$count] = "<label for='".$id."'>".($count + 1 ).".<label> "."<input type='checkbox' id='".$id."' value='".$pv_id."' onclick=\"openResults('".$header."', '".$pv_id."', '".$count."');\"><label for='".$id."'>".$pv_name."</label><BR>";
					$count++;
		   		}
			}
			elseif ($minorCategoryID == 'ALIAS NAME')
			{
				// Obtain match on Alias Name stored in table rec_alias

				$query_pv_alias = "SELECT DISTINCT rec_alias.alias_nm, rec_alias.rec_id from rec_alias WHERE alias_nm like '".$searchString."%'";
				$result_pv_alias = mysql_query($query_pv_alias);

				while ($pv_alias_row = mysql_fetch_row($result_pv_alias) )
				{
					$pv_alias_name = $pv_alias_row[0];
					$pv_id = $pv_alias_row[1];
					$id = $header.$count;
					$checkbox_array[$count] = "<label for='".$id."'>".($count + 1 ).".<label> "."<input type='checkbox' id='".$id."' value='".$pv_id."' onclick=\"openResults('".$header."', '".$pv_id."', '".$count."');\"><label for='".$id."'>".$pv_alias_name."</label><BR>";
					$count++;
				}
			}
		    elseif ($minorCategoryID == 'PV NAME')
		    {
				// Will display PV 'attributes': PV Name, Alias Names, Technical System, IOC Name, EPICS Record Type, and AOI Name

				// First obtain match on PV Name and store list of PV Name results

				$query_pv_name = "SELECT DISTINCT rec.rec_nm, rec.rec_id FROM rec, ioc, ioc_boot WHERE rec_nm like '".$searchString."%' AND rec.ioc_boot_id = ioc_boot.ioc_boot_id and ioc_boot.ioc_id = ioc.ioc_id and ioc_boot.current_load = '1'";
				$result_pv_name = mysql_query($query_pv_name);

				while ($pv_name_row = mysql_fetch_row($result_pv_name) )
				{
					$pv_name = $pv_name_row[0];
					$pv_id = $pv_name_row[1];
					$id = $header.$count;
				    $checkbox_array[$count] = "<label for='".$id."'>".($count + 1 ).".<label> "."<input type='checkbox' id='".$id."' value='".$pv_id."' onclick=\"openResults('".$header."', '".$pv_id."', '".$count."');\"><label for='".$id."'>".$pv_name."</label><BR>";
					$count++;
		   		}
	   		}
	   		break;

	   	case 'HISTORY EPICS RECORD':
			// Major category HISTORY EPICS RECORD has minor categories: PV NAME and ALIAS NAME

			if ($minorCategoryID == 'ALIAS NAME' || $minorCategoryID == '--ALL--')
			{
				// Obtain match on Alias Name stored in table rec_alias, then look in table rec_alias_history

				$query_alias_name = "SELECT DISTINCT rec_alias.alias_nm, rec_alias.rec_id, ioc.ioc_nm FROM rec_alias, rec, ioc, ioc_boot WHERE alias_nm like '".$searchString."%' AND rec_alias.rec_id = rec.rec_id and rec.ioc_boot_id = ioc_boot.ioc_boot_id and ioc_boot.ioc_id = ioc.ioc_id and ioc_boot.current_load = 1";
				$result_alias_name = mysql_query($query_alias_name);

				$all_alias_names = array();

				while ($alias_name_row = mysql_fetch_row($result_alias_name) )
				{
					$alias_name = $alias_name_row[0];
					$pv_id = $alias_name_row[1];
					$pv_ioc = $alias_name_row[2];

					$id = $header.$count;
				    $checkbox_array[$count] = "<label for='".$id."'>".($count + 1 ).".<label> "."<input type='checkbox' id='".$id."' value='".$pv_id."' onclick=\"openResults('".$header."', '".$pv_id."', '".$count."');\"><label for='".$id."'>".$alias_name." (Alias) - ".$pv_ioc."</label><BR>";
					$count++;
					$all_alias_names[] = $alias_name;
				}

		   		// Next, look for historical information on possible alias names in table rec_alias_history where alias name was not found in current data table rec_alias

				$query_alias_name_history = "SELECT DISTINCT alias_nm, rec_alias_history.rec_history_id, ioc.ioc_nm FROM rec_alias_history, rec_history, ioc, ioc_boot WHERE alias_nm like '".$searchString."%' and rec_alias_history.rec_history_id = rec_history.rec_history_id and rec_history.ioc_boot_id = ioc_boot.ioc_boot_id and ioc_boot.ioc_id = ioc.ioc_id";
				$result_alias_name_history = mysql_query($query_alias_name_history);

				while ($alias_row = mysql_fetch_row($result_alias_name_history) )
				{
						$alias_name = $alias_row[0];

						if (!in_array($alias_name,$all_alias_names) ){

							$pv_id = $alias_row[1];
							$pv_ioc = $alias_row[2];

							$id = $header.$count;
							$checkbox_array[$count] = "<label for='".$id."'>".($count + 1 ).".<label> "."<input type='checkbox' id='".$id."' value='".$pv_id."' onclick=\"openResults('".$header."', '".$pv_id."', '".$count."');\"><label for='".$id."'>".$alias_name." (Alias obsolete) - ".$pv_ioc."</label><BR>";
							$count++;

							$all_alias_names[] = $alias_name;
						}
		   		}
			}

		    if ($minorCategoryID == 'PV NAME' || $minorCategoryID == '--ALL--')
		    {
				// Will display PV 'attributes': PV Name, First Boot Date, Last Boot Date, IOC Name

				// First obtain match on PV Name and store list of PV Name results
				// Look for current information on PV in table rec

				$query_pv_name = "SELECT DISTINCT rec.rec_nm, rec.rec_id, ioc.ioc_nm FROM rec, ioc, ioc_boot WHERE rec_nm like '".$searchString."%' AND rec.ioc_boot_id = ioc_boot.ioc_boot_id and ioc_boot.ioc_id = ioc.ioc_id and ioc_boot.current_load = '1'";
				$result_pv_name = mysql_query($query_pv_name);

				$all_pv_names = array();

				while ($pv_name_row = mysql_fetch_row($result_pv_name) )
				{
					$pv_name = $pv_name_row[0];
					$pv_id = $pv_name_row[1];
					$pv_ioc = $pv_name_row[2];

					$id = $header.$count;
				    $checkbox_array[$count] = "<label for='".$id."'>".($count + 1 ).".<label> "."<input type='checkbox' id='".$id."' value='".$pv_id."' onclick=\"openResults('".$header."', '".$pv_id."', '".$count."');\"><label for='".$id."'>".$pv_name." - ".$pv_ioc."</label><BR>";
					$count++;
					$all_pv_names[] = $pv_name;

		   			// Test for case where this specific PV was moved from one IOC to another

		   			$query_pv_name_history = "SELECT DISTINCT rec_history.rec_nm, rec_history.rec_history_id, ioc.ioc_nm FROM rec_history, ioc, ioc_boot WHERE rec_history.rec_nm = '".$pv_name."' and rec_history.ioc_boot_id = ioc_boot.ioc_boot_id and ioc_boot.ioc_id = ioc.ioc_id";
					$result_pv_name_history = mysql_query($query_pv_name_history);

					$pv_iocs = array();
					$pv_iocs[] = $pv_ioc;

					$pv_name_history = "";
					$pv_ioc_history = "";
					$pv_id_history = 0;

					while ($pv_name_history_row = mysql_fetch_row($result_pv_name_history) )
					{
							$pv_name_history = $pv_name_history_row[0];
							$pv_ioc_history = $pv_name_history_row[2];

							if (!in_array($pv_ioc_history,$pv_iocs)){

									$pv_id = $pv_name_history_row[1];
									$id = $header.$count;
									$checkbox_array[$count] = "<label for='".$id."'>".($count + 1 ).".<label> "."<input type='checkbox' id='".$id."' value='".$pv_id."' onclick=\"openResults('".$header."', '".$pv_id."', '".$count."');\"><label for='".$id."'>".$pv_name_history." (PV obsolete) - ".$pv_ioc_history."</label><BR>";
									$count++;
									$pv_iocs[] = $pv_ioc_history;

							}
		   			}

				}

		   		// Next, look for historical information on possible PVs in table rec_history where PV Name was not found in current data table rec

				$query_pv_name_history = "SELECT DISTINCT rec_history.rec_nm, rec_history.rec_history_id, ioc.ioc_nm FROM rec_history, ioc, ioc_boot WHERE rec_nm like '".$searchString."%' and rec_history.ioc_boot_id = ioc_boot.ioc_boot_id and ioc_boot.ioc_id = ioc.ioc_id";
				$result_pv_name_history = mysql_query($query_pv_name_history);

				while ($pv_row = mysql_fetch_row($result_pv_name_history) )
				{
						$pv_name = $pv_row[0];

						if (!in_array($pv_name,$all_pv_names)){

							$pv_id = $pv_row[1];
							$pv_ioc = $pv_row[2];

							$id = $header.$count;
							$checkbox_array[$count] = "<label for='".$id."'>".($count + 1 ).".<label> "."<input type='checkbox' id='".$id."' value='".$pv_id."' onclick=\"openResults('".$header."', '".$pv_id."', '".$count."');\"><label for='".$id."'>".$pv_name." (PV obsolete) - ".$pv_ioc."</label><BR>";
							$count++;

							$all_pv_names[] = $pv_name;
						}
		   		}

	   		}

	   		break;


		case 'INSTALLED COMPONENTS':
			// Major category INSTALLED COMPONENTS has minor category: INSTALLED COMPONENT NAME

			if ($minorCategoryID == '--ALL--')
			{
				// Will display Installed Component 'attributes': Component Name, Description, Form Factor, and Function(s)
				// First obtain match on Installed Component Name
				$query_installed_component_all = "SELECT DISTINCT component.component_instance_name, component.component_id FROM component, component_type, form_factor, component_type_function, function WHERE (form_factor.form_factor like '".$searchString."%' or component_type.component_type_name like '".$searchString."%' or component.component_instance_name like '".$searchString."%' or function.function like '".$searchString."%') AND component.component_type_id = component_type.component_type_id AND component_type.form_factor_id = form_factor.form_factor_id and component_type.component_type_id = component_type_function.component_type_id and component_type_function.function_id = function.function_id order by component.component_instance_name";
				$result_installed_component_all = mysql_query($query_installed_component_all);
				while ($component_all_row = mysql_fetch_row($result_installed_component_all) )
				{
					if (!empty($component_all_row[0]))
					{
						$component_name = $component_all_row[0];
					}
					else
					{
						$component_name = "Not Specified";
					}
					$component_id = $component_all_row[1];
					$id = $header.$count;
				    $checkbox_array[$count] = "<label for='".$id."'>".($count + 1 ).".<label> "."<input type='checkbox' id='".$id."' value='".$component_id."' onclick=\"openResults('".$header."', '".$component_id."', '".$count."');\"><label for='".$id."'>".$component_name."</label><BR>";
					$count++;
				}
			}
			elseif ($minorCategoryID == 'INSTALLED COMPONENT NAME')
			{
				// Will display Installed Component 'attributes': Component Name, Description, Form Factor, and Function(s)
				// First obtain match on Installed Component Name
				$query_installed_component_name = "SELECT DISTINCT component.component_instance_name, component.component_id  FROM component, component_type, form_factor WHERE component.component_instance_name like '".$searchString."%' AND component.component_type_id = component_type.component_type_id AND component_type.form_factor_id = form_factor.form_factor_id order by component.component_instance_name";
				$result_installed_component_name = mysql_query($query_installed_component_name);
				while ($component_name_row = mysql_fetch_row($result_installed_component_name) )
				{
					if (!empty($component_name_row[0]))
					{
						$component_name = $component_name_row[0];
					}
					else
					{
						$component_name = "Not Specified";
					}
					$component_id = $component_name_row[1];
					$id = $header.$count;
				    $checkbox_array[$count] = "<label for='".$id."'>".($count + 1 ).".<label> "."<input type='checkbox' id='".$id."' value='".$component_id."' onclick=\"openResults('".$header."', '".$component_id."', '".$count."');\"><label for='".$id."'>".$component_name."</label><BR>";
					$count++;
				}
			}
			elseif ($minorCategoryID == 'COMPONENT TYPE')
			{
				// Will display Installed Component 'attributes': Component Name, Description, Form Factor, and Function(s)
				// First obtain match on Installed Component Name
				$query_installed_component_type = "SELECT DISTINCT component.component_instance_name, component.component_id  FROM component, component_type, form_factor WHERE component_type.component_type_name like '".$searchString."%' AND component.component_type_id = component_type.component_type_id AND component_type.form_factor_id = form_factor.form_factor_id order by component.component_instance_name";
				$result_installed_component_type = mysql_query($query_installed_component_type);
				while ($component_type_row = mysql_fetch_row($result_installed_component_type) )
				{
					if (!empty($component_type_row[0]))
					{
						$component_name = $component_type_row[0];
					}
					else
					{
						$component_name = "Not Specified";
					}
					$component_id = $component_type_row[1];
					$id = $header.$count;
				    $checkbox_array[$count] = "<label for='".$id."'>".($count + 1 ).".<label> "."<input type='checkbox' id='".$id."' value='".$component_id."' onclick=\"openResults('".$header."', '".$component_id."', '".$count."');\"><label for='".$id."'>".$component_name."</label><BR>";
					$count++;
				}
			}

			//elseif ($minorCategoryID == 'MANUFACTURER')
			//{
				// Will display Installed Component 'attributes': Component Name, Description, Manufacturer, Form Factor, and Function(s)
				// First obtain match on Installed Component Name
			//	$query_installed_component_manufacturer = "SELECT DISTINCT component.component_instance_name, component.component_id  FROM component, component_type, mfg, form_factor WHERE mfg.mfg_name like '".$searchString."%' AND component.component_type_id = component_type.component_type_id AND component_type.mfg_id = mfg.mfg_id AND component_type.form_factor_id = form_factor.form_factor_id order by component.component_instance_name";
			//	$result_installed_component_manufacturer = mysql_query($query_installed_component_manufacturer);
			//	while ($component_manufacturer_row = mysql_fetch_row($result_installed_component_manufacturer) )
			//	{
			//		$component_name = $component_manufacturer_row[0];
			//		$component_id = $component_manufacturer_row[1];
			//		$id = $header.$count;
			//	    $checkbox_array[$count] = "<label for='".$id."'>".($count + 1 ).".<label> "."<input type='checkbox' id='".$id."' value='".$component_id."' onclick=\"openResults('".$header."', '".$component_id."', '".$count."');\"><label for='".$id."'>".$component_name."</label><BR>";
			//		$count++;
			//	}
			//}

			if ($minorCategoryID == 'FORM FACTOR')
			{
				// Will display Installed Component 'attributes': Component Name, Description, Form Factor, and Function(s)
				// First obtain match on Installed Component Name
				$query_installed_component_form_factor = "SELECT DISTINCT component.component_instance_name, component.component_id  FROM component, component_type, form_factor WHERE form_factor.form_factor like '".$searchString."%' AND component.component_type_id = component_type.component_type_id AND component_type.form_factor_id = form_factor.form_factor_id order by component.component_instance_name";
				$result_installed_component_form_factor = mysql_query($query_installed_component_form_factor);
				while ($component_form_factor_row = mysql_fetch_row($result_installed_component_form_factor) )
				{
					if (!empty($component_form_factor_row[0]))
					{
						$component_name = $component_form_factor_row[0];
					}
					else
					{
						$component_name = "Not Specified";
					}
					$component_id = $component_form_factor_row[1];
					$id = $header.$count;
				    $checkbox_array[$count] = "<label for='".$id."'>".($count + 1 ).".<label> "."<input type='checkbox' id='".$id."' value='".$component_id."' onclick=\"openResults('".$header."', '".$component_id."', '".$count."');\"><label for='".$id."'>".$component_name."</label><BR>";
					$count++;
				}
			}
			elseif ($minorCategoryID == 'FUNCTION')
			{
				// Will display Installed Component 'attributes': Component Name, Description, Form Factor, and Function(s)
				// First obtain match on Installed Component Name
				$query_installed_component_function = "SELECT DISTINCT component.component_instance_name, component.component_id  FROM component, component_type, form_factor, component_type_function, function WHERE function.function like '".$searchString."%' AND component.component_type_id = component_type.component_type_id AND component_type.form_factor_id = form_factor.form_factor_id and component_type.component_type_id = component_type_function.component_type_id and component_type_function.function_id = function.function_id order by component.component_instance_name";
				$result_installed_component_function = mysql_query($query_installed_component_function);
				while ($component_function_row = mysql_fetch_row($result_installed_component_function) )
				{
					if (!empty($component_function_row[0]))
					{
						$component_name = $component_function_row[0];
					}
					else
					{
						$component_name = "Not Specified";
					}
					$component_id = $component_function_row[1];
					$id = $header.$count;
				    $checkbox_array[$count] = "<label for='".$id."'>".($count + 1 ).".<label> "."<input type='checkbox' id='".$id."' value='".$component_id."' onclick=\"openResults('".$header."', '".$component_id."', '".$count."');\"><label for='".$id."'>".$component_name."</label><BR>";
					$count++;
				}
			}
			break;


		case 'IMS':
			// Major category IMS (Infrastructure Monitoring System) has minor categories: Service Name, Comments, Status

			// Create connection to Nagios database
			$conn_nagios = mysql_connect($db_host_nagios, $db_user_name_nagios, $db_user_pswd_nagios);
			mysql_select_db($db_name_nagios);

			if ($minorCategoryID == '--ALL--')
			{
				// Obtain match on IMS Nagios Service Name, and also separately, Nagios Comment and Nagios Service Status

				// Perform match on user entered string to four possible Nagios status levels: OK, WARNING, CRITICAL and UNKNOWN

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

					$query_ims_service_name = "SELECT DISTINCT nagios_services.service_object_id, display_name FROM nagios_services, nagios_comments, nagios_servicestatus WHERE display_name like '%".$searchString."%' or (comment_data like '%".$searchString."%' and nagios_comments.object_id = nagios_services.service_object_id) or (nagios_servicestatus.current_state = '".$ims_service_status."' and nagios_servicestatus.service_object_id = nagios_services.service_object_id)";

				}
				else
				{
					$query_ims_service_name = "SELECT DISTINCT nagios_services.service_object_id, display_name FROM nagios_services, nagios_comments WHERE display_name like '%".$searchString."%' or (comment_data like '%".$searchString."%' and nagios_comments.object_id = nagios_services.service_object_id)";

				}

				$result_ims_service_name = mysql_query($query_ims_service_name);

				while ($ims_service_name_row = mysql_fetch_row($result_ims_service_name) )
				{
					if (!empty($ims_service_name_row[1]))
					{
						$service_name = $ims_service_name_row[1];

						if ($ims_service_status == 0) {
							$service_name = $service_name." -- OK";
						}
						elseif ($ims_service_status == 1) {
							$service_name = $service_name." -- WARNING";
						}
						elseif ($ims_service_status == 2) {
							$service_name = $service_name." -- CRITICAL";
						}
						elseif ($ims_service_status == 3) {
							$service_name = $service_name." -- UNKNOWN";
						}
					}
					else
					{
						$service_name = "Not Specified";
					}

					$ims_object_id = $ims_service_name_row[0];

					$id = $header.$count;
				    $checkbox_array[$count] = "<label for='".$id."'>".($count + 1 ).".<label> "."<input type='checkbox' id='".$id."' value='".$ims_object_id."' onclick=\"openResults('".$header."', '".$ims_object_id."', '".$count."');\"><label for='".$id."'>".$service_name."</label><BR>";
					$count++;
				}
			}
			elseif ($minorCategoryID == 'SERVICE NAME')
			{
				// Obtain match on IMS Nagios Service Name

				$query_ims_service_name = "SELECT DISTINCT service_object_id, display_name FROM nagios_services WHERE display_name like '%".$searchString."%'";
				$result_ims_service_name = mysql_query($query_ims_service_name);

				while ($ims_service_name_row = mysql_fetch_row($result_ims_service_name) )
				{
					if (!empty($ims_service_name_row[1]))
					{
						$service_name = $ims_service_name_row[1];
					}
					else
					{
						$service_name = "Not Specified";
					}
					$ims_object_id = $ims_service_name_row[0];
					$id = $header.$count;
				    $checkbox_array[$count] = "<label for='".$id."'>".($count + 1 ).".<label> "."<input type='checkbox' id='".$id."' value='".$ims_object_id."' onclick=\"openResults('".$header."', '".$ims_object_id."', '".$count."');\"><label for='".$id."'>".$service_name."</label><BR>";
					$count++;
				}

			}
			elseif ($minorCategoryID == 'STATUS')
			{
				// Obtain match on IMS Nagios Service Status

				// Perform match on user entered string to four possible Nagios status levels: OK, WARNING, CRITICAL and UNKNOWN

				// OK = 0
				// WARNING = 1
				// CRITICAL = 2
				// UNKNOWN = 3

				$ims_service_status = -1;

				if(stristr('OK', $searchString) == true) {
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

					$query_ims_status = "SELECT DISTINCT nagios_services.service_object_id, nagios_services.display_name FROM nagios_services, nagios_servicestatus WHERE nagios_servicestatus.current_state = '".$ims_service_status."' and nagios_servicestatus.service_object_id = nagios_services.service_object_id";
					$result_ims_status = mysql_query($query_ims_status);

					while ($ims_row = mysql_fetch_row($result_ims_status) )
					{
						if (!empty($ims_row[1]))
						{
							$service_name = $ims_row[1];

							if ($ims_service_status == 0) {
								$service_name = $service_name." -- OK";
							}
							elseif ($ims_service_status == 1) {
								$service_name = $service_name." -- WARNING";
							}
							elseif ($ims_service_status == 2) {
								$service_name = $service_name." -- CRITICAL";
							}
							elseif ($ims_service_status == 3) {
								$service_name = $service_name." -- UNKNOWN";
							}
						}
						else
						{
							$service_name = "Not Specified";
						}
						$ims_object_id = $ims_row[0];


						$id = $header.$count;
						$checkbox_array[$count] = "<label for='".$id."'>".($count + 1 ).".<label> "."<input type='checkbox' id='".$id."' value='".$ims_object_id."' onclick=\"openResults('".$header."', '".$ims_object_id."', '".$count."');\"><label for='".$id."'>".$service_name."</label><BR>";
						$count++;
					}
				}

			}
			elseif ($minorCategoryID == 'COMMENTS')
			{
				// Obtain match on IMS Nagios Comment

				$query_ims_service_name = "SELECT DISTINCT nagios_services.service_object_id, display_name FROM nagios_services, nagios_comments WHERE comment_data like '%".$searchString."%' and nagios_comments.object_id = nagios_services.service_object_id";

				$result_ims_service_name = mysql_query($query_ims_service_name);

				while ($ims_service_name_row = mysql_fetch_row($result_ims_service_name) )
				{
					if (!empty($ims_service_name_row[1]))
					{
						$service_name = $ims_service_name_row[1];
					}
					else
					{
						$service_name = "Not Specified";
					}

					$ims_object_id = $ims_service_name_row[0];

					$id = $header.$count;
				    $checkbox_array[$count] = "<label for='".$id."'>".($count + 1 ).".<label> "."<input type='checkbox' id='".$id."' value='".$ims_object_id."' onclick=\"openResults('".$header."', '".$ims_object_id."', '".$count."');\"><label for='".$id."'>".$service_name."</label><BR>";
					$count++;
				}

			}


			mysql_close($conn_nagios);

			break;


		case 'ICMS':



			// Major category ICMS has minor category: ALL

			if ($minorCategoryID == '--ALL--')
			{

				$wsdl = "https://icmsdocs.aps.anl.gov/docs/idcplg?IdcService=DISPLAY_URL&dDocName=SEARCH";

				$clientTabData = new SoapClient($wsdl, array('login' => $icms_user_name,'password' => $icms_user_passwd, 'trace' => 1));

				// build ICMS search string
				$searchStringICMSdDocName = "dDocName <substring> `".$searchString."`";

				$searchStringICMSxComments = " <or> xComments <substring> `".$searchString."`";
				$searchStringICMSxDocAuthor = " <or> xDocAuthor <substring> `".$searchString."`";
				$searchStringICMSdDocTitle = " <or> dDocTitle <substring> `".$searchString."`";

				// CAD drawings included:
				$end_of_searchStringICMS = " <and> <not> dDocType <matches> `CAD_Dependency`";

				// End the search string
				$searchStringICMS = $searchStringICMSdDocName . $searchStringICMSxComments . $searchStringICMSxDocAuthor. $searchStringICMSdDocTitle . $end_of_searchStringICMS;

				// resultCount is 1 million because this is a magic number for "more matched documents than possible"
				// $param = array('queryText' => $searchStringICMS, 'resultCount' => 1000000);

				$param = array("queryText" => $searchStringICMS, "sortField" => "dInDate", "sortOrder" => "desc", "resultCount" => 1000000);


				$retVal = $clientTabData->AdvancedSearch($param);

				$count = 0;

				$search_results = $retVal->AdvancedSearchResult->SearchResults;

				if(isset($search_results)) {

					foreach ($search_results as $key => $value) {

						if ($done)
							break;

						if (gettype($key) != 'integer') { // then we have one and only one result

							$dDocType = $search_results->dDocType;
							$dDocTitle = $search_results->dDocTitle;
							$dDocName = $search_results->dDocName;
							$dInDate = $search_results->dInDate;

							//$dDocAuthor = $search_results->dDocAuthor; // apparently dDocAuthor is not the author - xDocAuthor is!
							// property #28 happens to be the xDocAuthor
							$xDocAuthor = $search_results->CustomDocMetaData->property[28]->value;
							if ($xDocAuthor == '')
								$xDocAuthor = '&nbsp';

							$done = true;
						}

						else {

							$dDocType = $value->dDocType;
							$dDocTitle = $value->dDocTitle;
							$dDocName = $value->dDocName;
							$dInDate = $value->dInDate;

							//$dDocAuthor = $value->dDocAuthor; // apparently dDocAuthor is not the author - xDocAuthor is!
							// property #28 happens to be the xDocAuthor
							$xDocAuthor = $value->CustomDocMetaData->property[28]->value;
							if ($xDocAuthor == '')
								$xDocAuthor = '&nbsp';
						}
						// Parse out the prefix part of the ICMS document name so that are only passing a number for 'value'
						$dDocName_number = str_replace("APS_","",$dDocName);

						$id = $header.$count;
						$checkbox_array[$count] = "<label for='".$id."'>".($count + 1 ).".<label> "."<input type='checkbox' id='".$id."' value='".$dDocName_number."' onclick=\"openResults('".$header."', '".$dDocName_number."', '".$count."');\"><label for='".$id."'>".$dDocTitle."</label><BR>";

						$count++;

					}
				}

			}

			elseif ($minorCategoryID == 'CONTENT ID')
			{

				// $wsdl = "https://icmsdocs.aps.anl.gov/new_docs/groups/secure/wsdl/custom/Search.wsdl";
				$wsdl = "https://icmsdocs.aps.anl.gov/docs/idcplg?IdcService=DISPLAY_URL&dDocName=SEARCH";

				// include("db.inc");

				$clientTabData = new SoapClient($wsdl, array('login' => $icms_user_name,'password' => $icms_user_passwd, 'trace' => 1));

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


				$retVal = $clientTabData->AdvancedSearch($param);

				$count = 0;

				$search_results = $retVal->AdvancedSearchResult->SearchResults;

				if(isset($search_results)) {

					foreach ($search_results as $key => $value) {

						if ($done)
							break;

						if (gettype($key) != 'integer') { // then we have one and only one result

							$dDocType = $search_results->dDocType;
							$dDocTitle = $search_results->dDocTitle;
							$dDocName = $search_results->dDocName;
							$dInDate = $search_results->dInDate;

							//$dDocAuthor = $search_results->dDocAuthor; // apparently dDocAuthor is not the author - xDocAuthor is!
							// property #27 happens to be the xDocAuthor
							$xDocAuthor = $search_results->CustomDocMetaData->property[27]->value;
							if ($xDocAuthor == '')
								$xDocAuthor = '&nbsp';

							$done = true;
						}

						else {

							$dDocType = $value->dDocType;
							$dDocTitle = $value->dDocTitle;
							$dDocName = $value->dDocName;
							$dInDate = $value->dInDate;

							//$dDocAuthor = $value->dDocAuthor; // apparently dDocAuthor is not the author - xDocAuthor is!
							// property #27 happens to be the xDocAuthor
							$xDocAuthor = $value->CustomDocMetaData->property[27]->value;
							if ($xDocAuthor == '')
								$xDocAuthor = '&nbsp';
						}
						// Parse out the prefix part of the ICMS document name so that are only passing a number for 'value'
						$dDocName_number = str_replace("APS_","",$dDocName);

						$id = $header.$count;
						$checkbox_array[$count] = "<label for='".$id."'>".($count + 1 ).".<label> "."<input type='checkbox' id='".$id."' value='".$dDocName_number."' onclick=\"openResults('".$header."', '".$dDocName_number."', '".$count."');\"><label for='".$id."'>".$dDocTitle."</label><BR>";

						$count++;

					}
				}

			}
			elseif ($minorCategoryID == 'TITLE')
			{

				// $wsdl = "https://icmsdocs.aps.anl.gov/new_docs/groups/secure/wsdl/custom/Search.wsdl";
				$wsdl = "https://icmsdocs.aps.anl.gov/docs/idcplg?IdcService=DISPLAY_URL&dDocName=SEARCH";

				// include("db.inc");

				$clientTabData = new SoapClient($wsdl, array('login' => $icms_user_name,'password' => $icms_user_passwd, 'trace' => 1));

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

				$retVal = $clientTabData->AdvancedSearch($param);

				$count = 0;

				$search_results = $retVal->AdvancedSearchResult->SearchResults;

				if(isset($search_results)) {

					foreach ($search_results as $key => $value) {

						if ($done)
							break;

						if (gettype($key) != 'integer') { // then we have one and only one result

							$dDocType = $search_results->dDocType;
							$dDocTitle = $search_results->dDocTitle;
							$dDocName = $search_results->dDocName;
							$dInDate = $search_results->dInDate;

							//$dDocAuthor = $search_results->dDocAuthor; // apparently dDocAuthor is not the author - xDocAuthor is!
							// property #27 happens to be the xDocAuthor
							$xDocAuthor = $search_results->CustomDocMetaData->property[27]->value;
							if ($xDocAuthor == '')
								$xDocAuthor = '&nbsp';

							$done = true;
						}

						else {

							$dDocType = $value->dDocType;
							$dDocTitle = $value->dDocTitle;
							$dDocName = $value->dDocName;
							$dInDate = $value->dInDate;

							//$dDocAuthor = $value->dDocAuthor; // apparently dDocAuthor is not the author - xDocAuthor is!
							// property #27 happens to be the xDocAuthor
							$xDocAuthor = $value->CustomDocMetaData->property[27]->value;
							if ($xDocAuthor == '')
								$xDocAuthor = '&nbsp';
						}
						// Parse out the prefix part of the ICMS document name so that are only passing a number for 'value'
						$dDocName_number = str_replace("APS_","",$dDocName);

						$id = $header.$count;
				    	$checkbox_array[$count] = "<label for='".$id."'>".($count + 1 ).".<label> "."<input type='checkbox' id='".$id."' value='".$dDocName_number."' onclick=\"openResults('".$header."', '".$dDocName_number."', '".$count."');\"><label for='".$id."'>".$dDocTitle."</label><BR>";

						$count++;

					}
				}

			}
			elseif ($minorCategoryID == 'AUTHOR')
			{

				// $wsdl = "https://icmsdocs.aps.anl.gov/new_docs/groups/secure/wsdl/custom/Search.wsdl";
				$wsdl = "https://icmsdocs.aps.anl.gov/docs/idcplg?IdcService=DISPLAY_URL&dDocName=SEARCH";

				// include("db.inc");

				$clientTabData = new SoapClient($wsdl, array('login' => $icms_user_name,'password' => $icms_user_passwd, 'trace' => 1));

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

				$retVal = $clientTabData->AdvancedSearch($param);

				$count = 0;

				$search_results = $retVal->AdvancedSearchResult->SearchResults;

				if(isset($search_results)) {

					foreach ($search_results as $key => $value) {

						if ($done)
							break;

						if (gettype($key) != 'integer') { // then we have one and only one result

							$dDocType = $search_results->dDocType;
							$dDocTitle = $search_results->dDocTitle;
							$dDocName = $search_results->dDocName;
							$dInDate = $search_results->dInDate;

							//$dDocAuthor = $search_results->dDocAuthor; // apparently dDocAuthor is not the author - xDocAuthor is!
							// property #27 happens to be the xDocAuthor
							$xDocAuthor = $search_results->CustomDocMetaData->property[27]->value;
							if ($xDocAuthor == '')
								$xDocAuthor = '&nbsp';

							$done = true;
						}

						else {

							$dDocType = $value->dDocType;
							$dDocTitle = $value->dDocTitle;
							$dDocName = $value->dDocName;
							$dInDate = $value->dInDate;

							//$dDocAuthor = $value->dDocAuthor; // apparently dDocAuthor is not the author - xDocAuthor is!
							// property #27 happens to be the xDocAuthor
							$xDocAuthor = $value->CustomDocMetaData->property[27]->value;
							if ($xDocAuthor == '')
								$xDocAuthor = '&nbsp';
						}

						// Parse out the prefix part of the ICMS document name so that are only passing a number for 'value'
						$dDocName_number = str_replace("APS_","",$dDocName);

						$id = $header.$count;
				    	$checkbox_array[$count] = "<label for='".$id."'>".($count + 1 ).".<label> "."<input type='checkbox' id='".$id."' value='".$dDocName_number."' onclick=\"openResults('".$header."', '".$dDocName_number."', '".$count."');\"><label for='".$id."'>".$dDocTitle."</label><BR>";

						$count++;

					}
				}

			}

			if ($minorCategoryID == 'COMMENTS')
			{

				// $wsdl = "https://icmsdocs.aps.anl.gov/new_docs/groups/secure/wsdl/custom/Search.wsdl";
				$wsdl = "https://icmsdocs.aps.anl.gov/docs/idcplg?IdcService=DISPLAY_URL&dDocName=SEARCH";

				// include("db.inc");

				$clientTabData = new SoapClient($wsdl, array('login' => $icms_user_name,'password' => $icms_user_passwd, 'trace' => 1));

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

				$retVal = $clientTabData->AdvancedSearch($param);

				$count = 0;

				$search_results = $retVal->AdvancedSearchResult->SearchResults;

				if(isset($search_results)) {

					foreach ($search_results as $key => $value) {

						if ($done)
							break;

						if (gettype($key) != 'integer') { // then we have one and only one result

							$dDocType = $search_results->dDocType;
							$dDocTitle = $search_results->dDocTitle;
							$dDocName = $search_results->dDocName;
							$dInDate = $search_results->dInDate;

							//$dDocAuthor = $search_results->dDocAuthor; // apparently dDocAuthor is not the author - xDocAuthor is!
							// property #27 happens to be the xDocAuthor
							$xDocAuthor = $search_results->CustomDocMetaData->property[27]->value;
							if ($xDocAuthor == '')
								$xDocAuthor = '&nbsp';

							$done = true;
						}

						else {

							$dDocType = $value->dDocType;
							$dDocTitle = $value->dDocTitle;
							$dDocName = $value->dDocName;
							$dInDate = $value->dInDate;

							//$dDocAuthor = $value->dDocAuthor; // apparently dDocAuthor is not the author - xDocAuthor is!
							// property #27 happens to be the xDocAuthor
							$xDocAuthor = $value->CustomDocMetaData->property[27]->value;
							if ($xDocAuthor == '')
								$xDocAuthor = '&nbsp';
						}

						// Parse out the prefix part of the ICMS document name so that are only passing a number for 'value'
						$dDocName_number = str_replace("APS_","",$dDocName);

						$id = $header.$count;
				    	$checkbox_array[$count] = "<label for='".$id."'>".($count + 1 ).".<label> "."<input type='checkbox' id='".$id."' value='".$dDocName_number."' onclick=\"openResults('".$header."', '".$dDocName_number."', '".$count."');\"><label for='".$id."'>".$dDocTitle."</label><BR>";

						$count++;

					}
				}
			}

			break;

	}
	mysql_close();

    $doc = new DOMDocument();
    $root = $doc->createElement('search_response');
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
    $child = $doc->createElement('searchString');
    $child = $root->appendChild($child);

    $value = $doc->createTextNode($originalSearchString);
    $value = $child->appendChild($value);

    //
    $child = $doc->createElement('header');
    $child = $root->appendChild($child);

    $value = $doc->createTextNode($header);
    $value = $child->appendChild($value);

    //
	$child = $doc->createElement('checkboxes');
    $child = $root->appendChild($child);

	$value = $doc->createTextNode(implode('', $checkbox_array));
    $value = $child->appendChild($value);

    //
	$child = $doc->createElement('numberResultsTabData');
    $child = $root->appendChild($child);

	$value = $doc->createTextNode($count);
    $value = $child->appendChild($value);

	$xml_string = $doc->saveXML();
	echo $xml_string;
?>

