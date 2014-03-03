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
        $searchString = $xml->searchString[0];
        $idValues = $xml->idValue;
    }
    else
    {
	    echo "ERROR could not parse xml string from sent data<br />";
    }

    include('gst_i_common.php');
	    include_once('db.inc');
	    $host = $db_host;
		$user = $db_user_read_name;
		$passwordsql = $db_user_read_passwd;
		$dmbname = $db_name_production_1;
	$dbc = mysql_connect($host, $user, $passwordsql);
	mysql_select_db($dmbname);

	$p = 0; // loop variable

	if ($idValues)
	{
		$nameArray = $idValues;
		$new = array();
		for ($i = 0; $i < count($nameArray); $i++)
		{
			switch ($majorCategoryID)
			{
				case 'AOI':
					// AOI minor categories include: AOI Name, Machine, Tech Sys, Description, Cognizant 1, Cognizant 2, Criticality, IOC Name(s), Customer Group, AOI Status, Keywords

			            $query_aoi_name = "SELECT DISTINCT aoi.aoi_id, aoi_name, aoi_description, person.first_nm, person.last_nm, cognizant2.first_nm, cognizant2.last_nm, criticality_type.criticality_classification, group_name, aoi_status, aoi_keyword FROM aoi, person, person as cognizant2, criticality_type, aoi_criticality, aoi_status, group_name WHERE aoi.aoi_id = '".$nameArray[$i]."' AND person.person_id = aoi.aoi_cognizant1_id AND cognizant2.person_id = aoi.aoi_cognizant2_id AND aoi_criticality.aoi_id = aoi.aoi_id AND aoi_criticality.criticality_id = criticality_type.criticality_id AND aoi.aoi_status_id = aoi_status.aoi_status_id AND aoi.aoi_customer_group_id = group_name.group_name_id order by aoi.aoi_name";
				    	$result_aoi_name_list = mysql_query($query_aoi_name);
				    	while ($aoi_name_row = mysql_fetch_row($result_aoi_name_list) )
				    	{
					    	$aoi_id = $aoi_name_row[0];
					    	$aoi_name = $aoi_name_row[1];
							$aoi_description = $aoi_name_row[2];
							$aoi_cognizant1 = $aoi_name_row[3]." ".$aoi_name_row[4];
							$aoi_cognizant2 = $aoi_name_row[5]." ".$aoi_name_row[6];
							$aoi_criticality_type = $aoi_name_row[7];
							$aoi_group_name = $aoi_name_row[8];
							$aoi_status = $aoi_name_row[9];
							$aoi_keyword = $aoi_name_row[10];

							// Retrieve the IOCs for this particular AOI, there may be possibly two or more IOCs
							$query_aoi_iocs = "SELECT DISTINCT ioc_nm FROM aoi, ioc, aoi_ioc_stcmd_line, ioc_stcmd_line WHERE aoi.aoi_id = '".$aoi_id."' AND aoi_ioc_stcmd_line.aoi_id = aoi.aoi_id AND aoi_ioc_stcmd_line.ioc_stcmd_line_id = ioc_stcmd_line.ioc_stcmd_line_id AND ioc_stcmd_line.ioc_id = ioc.ioc_id";
							$result_aoi_ioc_list = mysql_query($query_aoi_iocs);

							$aoi_ioc_names = "";
							$aoi_ioc_name_array = array();
							$h= 0;
							while ($aoi_ioc_row = mysql_fetch_row($result_aoi_ioc_list) )
							{
								$aoi_ioc_name_array[$h] = $aoi_ioc_row[0];
								$h++;
							}
							$aoi_ioc_names = implode(", ", $aoi_ioc_name_array);

							$new[$p] = $aoi_id.",,,".$aoi_name.",,,".$aoi_description.",,,".$aoi_cognizant1.",,,".$aoi_cognizant2.",,,".$aoi_criticality_type.",,,".$aoi_ioc_names.",,,".$aoi_group_name.",,,".$aoi_status.",,,".$aoi_keyword;

				      		$p++;
			      		}

				    break;

				case 'IOC':
					// IOC has minor categories: IOC Name, IOC Status, IOC System, IOC Location, IOC Cognizant Developer, IOC Cognizant Technician, PV Names, IOC Contents
			            $query_ioc_name = "SELECT DISTINCT ioc.ioc_id, ioc.ioc_nm, ioc.system, aps_ioc.location, person.first_nm, person.last_nm, technician.first_nm, technician.last_nm, ioc_status,ioc.component_id FROM ioc, aps_ioc, person, person as technician, ioc_status WHERE ioc.ioc_id = '".$nameArray[$i]."' AND ioc.ioc_id = aps_ioc.ioc_id AND aps_ioc.cog_technician_id = technician.person_id AND aps_ioc.cog_developer_id = person.person_id and ioc.ioc_status_id = ioc_status.ioc_status_id order by ioc.ioc_nm";
			            $result_ioc_name = mysql_query($query_ioc_name);

			            while ($ioc_name_row = mysql_fetch_row($result_ioc_name) )
			            {
				            $ioc_id = $ioc_name_row[0];
			            	$ioc_name = $ioc_name_row[1];
			            	$ioc_system = $ioc_name_row[2];
			            	$ioc_location = $ioc_name_row[3];

			            	$component_id = $ioc_name_row[9];

			            	$ioc_location_room = getComponentParentRoom($component_id);
			            	$ioc_location_rack = getComponentParentRack($component_id);
			            	$ioc_location = $ioc_location_room." - ".$ioc_location_rack;

			            	$ioc_cog_developer = $ioc_name_row[4]." ".$ioc_name_row[5];
			            	$ioc_cog_technician = $ioc_name_row[6]." ".$ioc_name_row[7];
			            	$ioc_status = $ioc_name_row[8];

			                $new[$p] = $ioc_id.",,,".$ioc_name.",,,".$ioc_status.",,,".$ioc_system.",,,".$ioc_location.",,,".$ioc_cog_developer.",,,".$ioc_cog_technician;

			                $p++;
			            }

		            break;

			case 'ICMS':
					// Major category ICMS
					// Will display ICMS 'attributes': Content ID, Title, Author, Comments

					$icmsDocName = trim($nameArray[$i]);

					include("db.inc");

					// $wsdl = "https://icmsdocs.aps.anl.gov/new_docs/groups/secure/wsdl/custom/Search.wsdl";

					$wsdl = "https://icmsdocs.aps.anl.gov/docs/idcplg?IdcService=DISPLAY_URL&dDocName=SEARCH";

					$clientTableData = new SoapClient($wsdl, array('login' => $icms_user_name,'password' => $icms_user_passwd));

					if (!$clientTableData) {

						$dDocTitle = "error...could not create wsdl soap client";
						$xDocAuthor = "quock";
						$xComments = "&nbsp;";
						$content_id = "APS_".$icmsDocName;
						$new[$p] = $icmsDocName.",,,".$content_id.",,,".$dDocTitle.",,,".$xDocAuthor.",,,".$xComments;
						$p++;
						break;

					}

					// perform ICMS search on the document's Content ID, should look like "APS_#######"

					$dDocTitle = "debugging";
					$xDocAuthor = "quock";
					$xComments = "testing";

					// build ICMS search string

					// $searchStringICMS = "not dDocType <matches> 'CAD_Dependency' and not dDocType <matches> 'Model_Drawing' and dDocName <contains> '$nameArray[$i]'";

					$searchStringICMS = "dDocName <substring> `%$icmsDocName%`";


					// resultCount is 1 million because this is a magic number for "more matched documents than possible"

					// $param = array('queryText' => $searchStringICMS, 'resultCount' => 1000000);

					$param = array("queryText" => $searchStringICMS, "sortField" => "dInDate", "sortOrder" => "desc", "resultCount" => 1000000);

					$retVal = $clientTableData->AdvancedSearch($param);

					$icms_search_results = $retVal->AdvancedSearchResult->SearchResults;

					$dDocTitle = $icms_search_results->dDocTitle;

					$xComments = $icms_search_results->CustomDocMetaData->property[32]->value;

					if($xComments == NULL) $xComments = "&nbsp;";

					$xDocAuthor = $icms_search_results->CustomDocMetaData->property[28]->value;

					if($xDocAuthor == NULL) $xDocAuthor = "&nbsp;";

					$content_id = "APS_".$icmsDocName;

					$new[$p] = $icmsDocName.",,,".$content_id.",,,".$dDocTitle.",,,".$xDocAuthor.",,,".$xComments;
					$p++;


					break;

				case 'PLC':
					// Major category 'PLC' currently has 5 minor categories: PLC NAME, DESCRIPTION, LOCATION, and IOC NAME
					$plc_location_array = array();

						$query_plc_name = "SELECT DISTINCT component_id, component_instance_name FROM component WHERE component_id = '".$nameArray[$i]."' order by component_instance_name";
						$result = mysql_query($query_plc_name);
						while ($result_plc_name_row = mysql_fetch_row($result))
						{
							$result_plc_component_id = $result_plc_name_row[0];
							$result_plc_name = $result_plc_name_row[1];

							// Get plc_description from table plc
							$result_plc_description = "";
							$query_plc_description = "SELECT distinct plc_description FROM plc WHERE plc.component_id = '".$result_plc_component_id."'";
							$result_plc_description_query = mysql_query($query_plc_description);
							while($result_plc_description_row = mysql_fetch_row($result_plc_description_query))
							{
								$result_plc_description = $result_plc_description_row[0];
							}

							$plc_location_array = getPLCLocation($result_plc_name);
							$plc_location_html_string = "";
							foreach ($plc_location_array as $value)
							{
								$plc_location_html_string = $plc_location_html_string.$value."<br />";
					      	}
					      	$plc_ioc_name = getPLCIOCName($result_plc_name);
					      	$new[$p] = $result_plc_component_id.",,,".$result_plc_name.",,,".$result_plc_description.",,,".$plc_location_html_string.",,,".$plc_ioc_name;
					      	$p++;
				      	}
				   	break;

				case 'COMPONENT TYPE':
					// Minor categories for COMPONENT TYPE include: Component Type Name, Description, Manufacturer, Form Factor, Function, Cognizant Person
			            $query_component_type = "SELECT DISTINCT component_type.component_type_id, component_type_name, component_type.description, mfg.mfg_name, form_factor.form_factor, person.first_nm, person.last_nm FROM component_type, mfg, form_factor, component_type_person, person WHERE component_type.component_type_id = '".$nameArray[$i]."' AND component_type.mfg_id = mfg.mfg_id AND component_type.form_factor_id = form_factor.form_factor_id AND component_type.component_type_id = component_type_person.component_type_id AND component_type_person.person_id = person.person_id order by component_type.component_type_name";
			            $result_component_type = mysql_query($query_component_type);
			            while ($component_type_row = mysql_fetch_row($result_component_type) )
			            {
				            $component_type_id = $component_type_row[0];
				            $component_type_name = $component_type_row[1];
				            $component_type_description = $component_type_row[2];
				            $component_type_manufacturer = $component_type_row[3];
				            $component_type_form_factor = $component_type_row[4];
				            $component_type_cognizant = $component_type_row[5]." ".$component_type_row[6];

			           		$query_function = "SELECT DISTINCT function.function FROM component_type, component_type_function, function WHERE component_type.component_type_id = '".$component_type_id."' AND component_type.component_type_id = component_type_function.component_type_id AND component_type_function.function_id = function.function_id";
			           		$result_function = mysql_query($query_function);
			           		$function_array = array();
			           		$h= 0;
							while ($function_row = mysql_fetch_row($result_function) )
							{
								$function_array[$h] = $function_row[0];
								$h++;
							}
							$component_type_function = implode(", ", $function_array);

				            $new[$p] = $component_type_id.",,,".$component_type_name.",,,".$component_type_description.",,,".$component_type_manufacturer.",,,".$component_type_form_factor.",,,".$component_type_function.",,,".$component_type_cognizant.",,,"."1";//Have to have the 1 here, goes with variable jmp to go to the webpage;
				            $p++;
			            }
				    break;

				case 'EPICS RECORD':
					// Major category EPICS RECORD has minor categories: PV NAME and ALIAS NAME

						// Will display PV 'attributes': PV Name, Alias Names, Technical System, IOC Name, EPICS Record Type, and AOI Name

						// First obtain match on PV Name and store list of PV Name results

						$query_pv_name = "SELECT DISTINCT rec.rec_id, rec.rec_nm, rec_type.rec_type, ioc.system, ioc.ioc_nm FROM rec, rec_type, ioc, ioc_boot WHERE rec_id = '".$nameArray[$i]."' AND rec.rec_type_id = rec_type.rec_type_id AND rec.ioc_boot_id = ioc_boot.ioc_boot_id and ioc_boot.ioc_id = ioc.ioc_id";
						$result_pv_name = mysql_query($query_pv_name);

						while ($pv_name_row = mysql_fetch_row($result_pv_name) )
						{
							$pv_id = $pv_name_row[0];
							$pv_name = $pv_name_row[1];
							$pv_record_type = $pv_name_row[2];
							$pv_name_system = $pv_name_row[3];
							$pv_name_ioc = $pv_name_row[4];

							// Retrieve any alias names that exist for this PV record id
							$pv_alias_names = "";

							$query_pv_alias = "SELECT DISTINCT rec_alias.alias_nm from rec_alias where rec_alias.rec_id = ".$pv_id." order by alias_nm";
							$result_alias_names = mysql_query($query_pv_alias);
							$i_count_alias = 0;
							$pv_alias_names = "";

							while ($pv_alias_row = mysql_fetch_row($result_alias_names) )
							{
								if ($i_count_alias){
									$pv_alias_names = $pv_alias_names.", ".$pv_alias_row[0];
								}else{
									$pv_alias_names = $pv_alias_row[0];
								}
								$i_count_alias++;
							}

							if ($pv_alias_names == "") $pv_alias_names = "None";

					   		$new[$p] = $pv_id.",,,".$pv_name.",,,".$pv_alias_names.",,,".$pv_record_type.",,,".$pv_name_system.",,,".$pv_name_ioc.",,,Click to get AOI's"; // This one is hard coded in because we couldn't run array_unique on it since the id's were different
					   		$p++;
				   		}

			   		break;

			    case 'HISTORY EPICS RECORD':

			   		// Major category HISTORY EPICS RECORD has minor categories: PV NAME and ALIAS NAME

			   		// Will display PV attributes: PV Name, Alias Names, IOC Name, First Boot Date for PV, Last Boot Date for PV, Subsequent IOC Boot Date, and AOI Name

					$pv_name_historical = "";
					$pv_id = $nameArray[$i];


			   		// First look in the history table rec_history for the rec_id that was sent to this php script

			   			$query_pv_name_historical = "select distinct rec_history.rec_nm, ioc.ioc_nm from rec_history, ioc, ioc_boot where rec_history.rec_history_id = '".$nameArray[$i]."' and rec_history.rec_nm like '".$searchString."%' and rec_history.ioc_boot_id = ioc_boot.ioc_boot_id and ioc_boot.ioc_id = ioc.ioc_id";
						$result_pv_name_historical = mysql_query($query_pv_name_historical);

						while ($pv_name_row = mysql_fetch_row($result_pv_name_historical) )
						{
							$pv_name_historical = $pv_name_row[0];
							$ioc_historical = $pv_name_row[1];

							$first_boot_date_pv = "";
							$last_boot_date_pv = "";
							$subsequent_boot_date_ioc = "N/A";
							$last_boot_current_boot = 0;
							$last_boot_current_load = 0;
							$pv_alias_names = "";

							$query_boot_date = "select ioc_boot.ioc_boot_date, ioc_boot.ioc_boot_id from rec_history, ioc_boot, ioc where rec_history.rec_nm = '".$pv_name_historical."' and rec_history.ioc_boot_id = ioc_boot.ioc_boot_id and ioc_boot.ioc_id = ioc.ioc_id and ioc.ioc_nm = '".$ioc_historical."' order by ioc_boot_date";
							$result_boot_date = mysql_query($query_boot_date);

							$i_boot_count = 0;

							while ($boot_date_row = mysql_fetch_row($result_boot_date) )
							{
								if ($i_boot_count < 1){

									$first_boot_date_pv = $boot_date_row[0];
									$first_boot_id = $boot_date_row[1];

								} else {

									$last_boot_date_pv = $boot_date_row[0];
									$last_boot_id_pv = $boot_date_row[1];

								}
								$i_boot_count++;
							}

							// Consider case where an IOC has only been booted once
							if ($i_boot_count == 1) {

								$last_boot_date_pv = $first_boot_date_pv;
								$last_boot_id = $first_boot_id;

							}

							// Get the subsequent IOC boot date (after this PV name went away) for comparison

							$query_ioc_boot_date = "select distinct ioc_boot_date from ioc, ioc_boot where ioc.ioc_nm = '".$ioc_historical."' and ioc.ioc_id = ioc_boot.ioc_id order by ioc_boot_date";
							$result_ioc_boot = mysql_query($query_ioc_boot_date);

							while ($ioc_boot_date_row = mysql_fetch_row($result_ioc_boot) )
							{

								if ($ioc_boot_date_row[0] > $last_boot_date_pv ) {
									$subsequent_boot_date_ioc = $ioc_boot_date_row[0];
									break;
								}
							}

							// Get PV Alias Names

								// Retrieve PV Alias Names from table "rec_alias_history"
								// Obtain from most recent boot for this PV Name

								$query_alias_names = "select distinct alias_nm from rec_alias_history where rec_alias_history.rec_history_id = ".$pv_id." order by alias_nm";
								$result_alias_names = mysql_query($query_alias_names);

								while ($alias_name_row = mysql_fetch_row($result_alias_names) )
								{
									$pv_alias_names = $pv_alias_names." ".$alias_name_row[0];
								}

								if ($pv_alias_names == "") $pv_alias_names = "None";

							$new[$p] = $pv_id.",,,".$pv_name_historical.",,,".$pv_alias_names.",,,".$ioc_historical.",,,".$first_boot_date_pv.",,,".$last_boot_date_pv.",,,".$subsequent_boot_date_ioc.",,,Click to get AOI's";
							$p++;
			   			}


			   		// Next, obtain match on a PV that has the PV name found in rec_history table
			   		// Need to consider case where a PV Name may have been intentionally moved from one IOC to another IOC
			   		// Remember that only one rec.rec_id (i.e., only one pv name) is sent in variable $nameArray[$i]
			   		// and that the user entered searchString may have been for a PV Alias Name

			   		// Look at table rec for existing PV Name

			   		$pv_name_current = "";

			   		$pv_id = $nameArray[$i];

			   		if ($pv_name_historical == ""){


			   		    // No historical PV match, so search on table rec: rec_id value

			   			$query_pv_name_current = "select distinct rec.rec_nm, rec.rec_id, ioc.ioc_nm, ioc.ioc_id from rec, ioc, ioc_boot where rec.rec_id = ".$pv_id." and rec.ioc_boot_id = ioc_boot.ioc_boot_id and ioc_boot.ioc_id = ioc.ioc_id";
						$result_pv_name_current = mysql_query($query_pv_name_current);
			   		}

			   		  while ($pv_name_row = mysql_fetch_row($result_pv_name_current) )
			   		  {
			   			$pv_name_current = $pv_name_row[0];
			   			$rec_id = $pv_name_row[1];
			   			$ioc_name_current = $pv_name_row[2];
			   			$ioc_id_current = $pv_name_row[3];

			   			$first_boot_date_pv = "";
						$last_boot_date_pv = "";
						$subsequent_boot_date_ioc = "N/A";

						$last_boot_current_boot = 0;
						$last_boot_current_load = 0;
						$pv_alias_names = "";

						// Get boot dates for this IOC and PV Name in history table rec_history

						$query_boot_date = "select distinct ioc_boot.ioc_boot_date, ioc_boot.ioc_boot_id from rec_history, ioc_boot, ioc where rec_history.rec_nm = '".$pv_name_current."' and rec_history.ioc_boot_id = ioc_boot.ioc_boot_id and ioc_boot.ioc_id = ioc.ioc_id and ioc.ioc_id = ".$ioc_id_current." order by ioc_boot_date";
						$result_boot_date = mysql_query($query_boot_date);
						$i_boot_count = 0;

						while ($boot_date_row = mysql_fetch_row($result_boot_date) )
						{

							if ($i_boot_count < 1){
								$first_boot_date_pv = $boot_date_row[0];
								$first_boot_id = $boot_date_row[1];
							} else {
								$last_boot_date_pv = $boot_date_row[0];
								$last_boot_id_pv = $boot_date_row[1];
							}
							$i_boot_count++;
						}


						// Get boot dates for this IOC and PV Name in current data table rec

						$query_boot_date = "select ioc_boot.ioc_boot_date, ioc_boot.ioc_boot_id from rec, ioc_boot, ioc where rec.rec_nm = '".$pv_name_current."' and rec.ioc_boot_id = ioc_boot.ioc_boot_id and ioc.ioc_id = ".$ioc_id_current." order by ioc_boot_date";
						$result_boot_date = mysql_query($query_boot_date);

						while ($boot_date_row = mysql_fetch_row($result_boot_date) )
						{

								if ($i_boot_count < 1){
									$first_boot_date_pv = $boot_date_row[0];
									$first_boot_id = $boot_date_row[1];
								} else {
									$last_boot_date_pv = $boot_date_row[0];
									$last_boot_id_pv = $boot_date_row[1];
								}
								$i_boot_count++;
						}

						// Consider case where an IOC has only been booted once
						if ($i_boot_count == 1) {

							$last_boot_date_pv = $first_boot_date_pv;
							$last_boot_id = $first_boot_id;

						}

						// Get the subsequent IOC boot date (if and when this PV name went away) for comparison

						$query_ioc_boot_date = "select ioc_boot_date from ioc, ioc_boot where ioc.ioc_id = ".$ioc_id_current." and ioc.ioc_id = ioc_boot.ioc_id order by ioc_boot_date";
						$result_ioc_boot = mysql_query($query_ioc_boot_date);

						while ($ioc_boot_date_row = mysql_fetch_row($result_ioc_boot) )
						{

							if (strtotime($ioc_boot_date_row[0]) > strtotime($last_boot_date_pv) ) {
								$subsequent_boot_date_ioc = $ioc_boot_date_row[0];
								break;
							}
						}

						// Get PV Alias Names

							// Retrieve PV Alias Names from table "rec_alias"
							// Obtain from most recent boot for this PV Name

							$query_alias_names = "select distinct alias_nm from rec_alias where rec_alias.rec_id = ".$rec_id." order by alias_nm";
							$result_alias_names = mysql_query($query_alias_names);

							while ($alias_name_row = mysql_fetch_row($result_alias_names) )
							{
								$pv_alias_names = $pv_alias_names." ".$alias_name_row[0];
							}

						if ($pv_alias_names == "") $pv_alias_names = "None";

						$new[$p] = $pv_id.",,,".$pv_name_current.",,,".$pv_alias_names.",,,".$ioc_name_current.",,,".$first_boot_date_pv.",,,".$last_boot_date_pv.",,,".$subsequent_boot_date_ioc.",,,Click to get AOI's";
			   			$p++;
			   		  }

			   		break;

				case 'INSTALLED COMPONENTS':
					// Major category INSTALLED COMPONENTS has minor category: INSTALLED COMPONENT NAME

						// Will display Installed Component 'attributes': Component Name, Description, IOC Parent, Housing Parent, Room Parent, Building Parent, Form Factor, and Function(s)
						// First obtain match on Installed Component Name
						$query_installed_component_name = "SELECT DISTINCT component.component_id, component.component_instance_name, component_type.component_type_name, component_type.description, form_factor.form_factor, component.component_type_id FROM component, component_type, form_factor WHERE component.component_id = '".$nameArray[$i]."' AND component.component_type_id = component_type.component_type_id AND component_type.form_factor_id = form_factor.form_factor_id order by component.component_instance_name";
						$result_installed_component_name = mysql_query($query_installed_component_name);
						while ($component_name_row = mysql_fetch_row($result_installed_component_name) )
						{
							$component_id = $component_name_row[0];
							$component_name = $component_name_row[1];
							$component_type_name = $component_name_row[2];
							$component_description = $component_name_row[3];
							// $component_manufacturer = $component_name_row[4];
							$component_form_factor = $component_name_row[4];
							$component_type_id = $component_name_row[5];

							// Retrieve list of functions for this component type
							$query_component_functions = "SELECT DISTINCT function.function FROM function, component_type_function WHERE component_type_function.component_type_id = ".$component_type_id." AND component_type_function.function_id = function.function_id";
							$result_component_functions = mysql_query($query_component_functions);
							$component_functions = "";
							while ($functions_row = mysql_fetch_row($result_component_functions) )
							{
								if ($component_functions != "")
								{
									$component_functions = $component_functions.", ";
								}
								$component_functions = $component_functions.$functions_row[0];
							}
							if (empty($component_name))
							{
								$component_name = "Not Specified";
							}

                            $ioc_parent = "";
                            $housing_parent = "";
                            $room_parent = "";
                            $building_parent = "";

							// Retrieve IOC Parent for this component
							$ioc_parent = getComponentParentIOC($component_id);

							// Retrieve Housing Rack Parent for this component
							$housing_parent = getComponentParentRack($component_id);

							// Retrieve Room Parent for this component
							$room_parent = getComponentParentRoom($component_id);

							// Retrieve Building Parent for this component
							$building_parent = getComponentParentBuilding($component_id);

							$new[$p] = $component_id.",,,".$component_name.",,,".$component_type_name.",,,".$ioc_parent.",,,".$housing_parent.",,,".$room_parent.",,,".$building_parent.",,,".$component_form_factor.",,,".$component_functions.",,,".$component_type_id.",,,"."1";//Have to have the 1 here, goes with variable jmp to go to the webpage
							$p++;
						}
					break;




			case 'IMS':
						// Major category IMS has minor categories: SERVICE NAME, COMMENTS and STATUS

						// Create connection to Nagios database
						$conn_nagios = mysql_connect($db_host_nagios, $db_user_name_nagios, $db_user_pswd_nagios);
						mysql_select_db($db_name_nagios);

						// Here, $nameArray[$i] is a Nagios service_object_id

						$query_ims_service_name = "SELECT DISTINCT nagios_services.service_object_id, display_name, nagios_servicestatus.output FROM nagios_services, nagios_servicestatus WHERE nagios_services.service_object_id = '".$nameArray[$i]."' and nagios_servicestatus.service_object_id = nagios_services.service_object_id";
						$result_ims_service_name = mysql_query($query_ims_service_name);

						while ($ims_service_name_row = mysql_fetch_row($result_ims_service_name) )
						{

							$ims_object_id = $ims_service_name_row[0];
							$service_display_name = $ims_service_name_row[1];
							$service_status_info = $ims_service_name_row[2];


							// Retrieve Nagios comments for this service object id, if any comments exist
							// There can be more than one Nagios comment for each Nagios service

							$query_nagios_comments = "SELECT DISTINCT comment_data FROM nagios_comments WHERE object_id = '".$ims_object_id."'";
							$result_nagios_comments = mysql_query($query_nagios_comments);
							$nagios_comments = "";

							while ($comment_row = mysql_fetch_row($result_nagios_comments) )
							{
								if ($nagios_comments != "")
								{
									$nagios_comments = $nagios_comments.", ";
								}
								$nagios_comments = $nagios_comments.$comment_row[0];
							}
							if (empty($service_display_name))
							{
								$service_display_name = "Not Specified";
							}
							if ($nagios_comments == "") $nagios_comments = "&nbsp;";

							$new[$p] = $ims_object_id.",,,".$service_display_name.",,,".$nagios_comments.",,,".$service_status_info;
							$p++;
						}

					mysql_close($conn_nagios);

					break;

			}
		}
	}
	mysql_close($dbc);

    $doc = new DOMDocument();
    $root = $doc->createElement('tableResponse');
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

    $value = $doc->createTextNode($searchString);
    $value = $child->appendChild($value);

	$searchInfo = $doc->createElement('searchInfo');
    $searchInfo = $root->appendChild($searchInfo);

	if ($new)
	{
		$new = array_unique($new);
		for ($i = 0; $i < count($new); $i++)
		{
			$child = $doc->createElement('row');
    		$child = $searchInfo->appendChild($child);
    		$value = $doc->createTextNode($new[$i]);
   	 		$value = $child->appendChild($value);
		}
	}

	$xml_string = $doc->saveXML();
	echo $xml_string;
?>