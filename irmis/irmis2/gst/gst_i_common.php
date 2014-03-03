<?php
/*
 * Global Search Application Common Include
 * Common file for header of all top-level php pages of Global Search application.
 * Defines any needed classes and functions, and establishes needed default data
 * in session if it does not already exist.
 */

function getAOINameList() {

				$aoi_name_list = array();

				// Obtain list of all aoi names as derived from table 'aoi'

				$query_aoi_name = "SELECT aoi_name, aoi_id FROM aoi";
				$result_aoi_name = mysql_query($query_aoi_name);

				if ($result_aoi_name)
				{

					while ($aoi_name_row = mysql_fetch_array($result_aoi_name))
					{
						$index = $aoi_name_row[1];
						$aoi_name_list[$index] = $aoi_name_row[0];
					}

				}
				return $aoi_name_list;
}

function getAOIPVNameList($aoi_name) {

				$pv_name_list = array();

				// Obtain a list of EPICS PVs (record names) that are associated with an individual AOI

				$query_pv_name = "SELECT DISTINCT aoi_epics_record.rec_nm FROM aoi, aoi_ioc_stcmd_line, aoi_epics_record WHERE aoi.aoi_name = '".$aoi_name."' AND aoi.aoi_id = aoi_ioc_stcmd_line.aoi_id AND aoi_epics_record.aoi_ioc_stcmd_line_id = aoi_ioc_stcmd_line.aoi_ioc_stcmd_line_id";
				$result_pv_name = mysql_query($query_pv_name);

				if ($result_pv_name)
				{
					$index = 0;
					while ($pv_name_row = mysql_fetch_array($result_pv_name))
					{
						$pv_name_list[$index] = $pv_name_row[0];
						$index++;
					}
				}
				return $pv_name_list;
}

function getAOIIOCNames($aoi_name) {

				$ioc_name_list = array();

				// Obtain a list of IOCs that are associated with an individual AOI

				$query_ioc_name = "SELECT DISTINCT ioc.ioc_nm FROM aoi, ioc, aoi_ioc_stcmd_line, ioc_stcmd_line WHERE aoi.aoi_name = '".$aoi_name."' AND aoi.aoi_id = aoi_ioc_stcmd_line.aoi_id AND aoi_ioc_stcmd_line.ioc_stcmd_line_id = ioc_stcmd_line.ioc_stcmd_line_id AND ioc_stcmd_line.ioc_id = ioc.ioc_id";
				$result_ioc_name = mysql_query($query_ioc_name);

				if ($result_ioc_name)
				{

					$index = 0;
					while ($ioc_name_row = mysql_fetch_array($result_ioc_name))
					{
						$ioc_name_list[$index] = $ioc_name_row[0];
						$index++;
					}

				}
				return $ioc_name_list;
}

function getIOCNameList() {

				$ioc_name_list = array();

				// Obtain list of all ioc names as derived from table 'ioc'

				$query_ioc_name = "SELECT DISTINCT ioc.ioc_nm, ioc.ioc_id FROM ioc";
				$result_ioc_name = mysql_query($query_ioc_name);

				if ($result_ioc_name)
				{
					while ($ioc_name_row = mysql_fetch_array($result_ioc_name))
					{
						$index = $ioc_name_row[1];
						$ioc_name_list[$index] = $ioc_name_row[0];
					}
				}
				return $ioc_name_list;
}

function getIOCContents($ioc_name) {

				$ioc_contents_list = array();

				// Obtain a list of an IOCs contents, i.e. modules in each slot in an individual IOC

				$parent_component_id = 0;
				$query_parent_component_id = "SELECT component_rel.parent_component_id FROM component_rel, component, component_type, ioc WHERE component_rel.child_component_id = component.component_id AND component.component_type_id = component_type.component_type_id AND component_rel.component_rel_type_id = '2' AND ioc.component_id = component.component_id AND ioc.ioc_nm like '".$ioc_name."'";

				$result_parent_component_id = mysql_query($query_parent_component_id);

				$parent_component_id_row = mysql_fetch_array($result_parent_component_id);
				$parent_component_id = $parent_component_id_row[0];

				$query_ioc_contents = "SELECT component_type.component_type_name FROM component, component_type, component_rel WHERE component_rel.component_rel_type_id = '2' AND component.component_id = component_rel.child_component_id AND component.component_type_id = component_type.component_type_id AND component_rel.parent_component_id = '".$parent_component_id."'";


				$result_ioc_contents = mysql_query($query_ioc_contents);

				if ($result_ioc_contents)
				{
					$index = 0;
					while ($ioc_contents_row = mysql_fetch_array($result_ioc_contents))
					{
						$ioc_contents_list[$index] = $ioc_contents_row[0];
						$index++;
					}

				}
				return $ioc_contents_list;
}

function getPLCNameList() {

				$plc_name_list = array();

			    // Obtain list of all plc names and plc IDs as derived from tables 'component' and 'plc'

		   		$query_plc_component_instance_name = "select distinct component.component_instance_name, component.component_id from component, plc where plc.component_id = component.component_id";
		   		$result_plc_component_instance_name = mysql_query($query_plc_component_instance_name);

		   		if ($result_plc_component_instance_name)
		   		{

		   			while ($plc_component_instance_name_row = mysql_fetch_array($result_plc_component_instance_name))
		   			{
		   		  		$index = $plc_component_instance_name_row[1];
		   		  		$plc_name_list[$index] = $plc_component_instance_name_row[0];
		   		  	}
		   		}

		   		return $plc_name_list;
}

function getPLCLocation($plc_name) {

				$query_plc_component_id = "SELECT distinct component.component_id FROM component, plc WHERE component_instance_name = '".$plc_name."' and plc.component_id = component.component_id";
		   		$result_plc_component_id = mysql_query($query_plc_component_id);

		   		$plc_location_array = array();

		   		if ($result_plc_component_id)
		   		{
		   			    $plc_component_id_row = mysql_fetch_array($result_plc_component_id);

		   		  		$plc_component_id = $plc_component_id_row[0];

		   				// Obtain location that matches to plc component id using parent/child hierarchy in component_rel table

		   				$child_component_id = $plc_component_id;
		   				$safety_count = 0;
		   				$idx = 0;
		   				$v = '0';
		   				$u = '0';
		   				$plc_location_string = '';
		   				$plc_location_array = array();

		   				while ($v != '9' && $safety_count < 10)
		   				{
		   					$query_plc_parent_component = "SELECT parent_component_id  FROM component_rel WHERE child_component_id = ".$child_component_id." AND component_rel_type_id = 2";
		   					$result_plc_parent_component = mysql_query($query_plc_parent_component);
		   					$result_plc_parent_row = mysql_fetch_array($result_plc_parent_component);
		   					$u = $result_plc_parent_row[0];

							$query_component_type_id = "SELECT component_type_id FROM component WHERE component_id = ".$u;
							$result_component_type_id = mysql_query($query_component_type_id);
							$result_component_type_row = mysql_fetch_array($result_component_type_id);
							$v = $result_component_type_row[0];

							if ($v == '9' || $v == '10' || $v == '11')
							{
								$query_component_instance_name = "SELECT component_instance_name FROM component WHERE component_id = ".$u;
								$result_component_instance_name = mysql_query($query_component_instance_name);

								if ($result_component_instance_name)
								{
									while ($result_component_instance_name_row = mysql_fetch_array($result_component_instance_name))
									{
										if ($v == '9')
										{
											$plc_location_string = 'room '.$result_component_instance_name_row[0];
										}
										else if ($v == '10')
										{
											$plc_location_string = 'rack '.$result_component_instance_name_row[0];
										}
										else
										{
											$plc_location_string = 'enclosure '.$result_component_instance_name_row[0];
										}
										$plc_location_array[$idx] = $plc_location_string;
										$idx++;
									}
								}
							}
							else
							{
								$query_component_type_name = "SELECT component_type_name, description FROM component_type WHERE component_type_id = ".$v;
								$result_component_type_name = mysql_query($query_component_type_name);

								if ($result_component_type_name)
								{
									while ($result_component_type_name_row = mysql_fetch_array($result_component_type_name))
									{
										$plc_location_array[$idx] = $result_component_type_name_row[0].', '.$result_component_type_name_row[1];
										$idx++;
									}
								}
							}

							// Update component id for next iteration loop
							$child_component_id = $u;
							$safety_count++;

		   				}  // end while loop on $v


				}  // end if test on valid plc component id query results

	return $plc_location_array;
}

function getPLCIOCName($plc_name) {

				   $plc_ioc_name = "";

				   // Obtain plc component id

				   $query_plc_component_id = "SELECT distinct component.component_id FROM component, plc WHERE component_instance_name = '".$plc_name."' and plc.component_id = component.component_id";
				   $result_plc_component_id = mysql_query($query_plc_component_id);

				   if ($result_plc_component_id)
				   {
				        $plc_component_id_row = mysql_fetch_array($result_plc_component_id);

				   		$plc_component_id = $plc_component_id_row[0];

				   		// Obtain ioc name that matches to plc component id using parent/child hierarchy in component_rel table

				   		$child_component_id = $plc_component_id;
				   		$safety_count = 0;
				   		while ($child_component_id != '1' && $safety_count<10)
				   		{
				   			$safety_count ++;

				   			$query_plc_ioc_name = "SELECT parent_component_id, logical_desc FROM component_rel WHERE child_component_id = ".$child_component_id." AND component_rel_type_id = 1";
				   			$result_plc_ioc_name = mysql_query($query_plc_ioc_name);
				   			$ctlParent = mysql_fetch_array($result_plc_ioc_name);
				   			$child_component_id = $ctlParent[0];

				   			if ($child_component_id == 1)
				   			{
				   			 	// Have obtained the ioc as the component

				   				$plc_ioc_name = $ctlParent[1];
				   			}
				   		}

				  }

	return $plc_ioc_name;
}


  function getComponentParentIoc($component_id) {

    // Begin building query for component_type table

    $query_parent_ioc = "SELECT c.component_instance_name, ct.component_type_name, cr.parent_component_id, cr.logical_desc FROM component c, component_type ct, component_rel cr WHERE c.component_type_id = ct.component_type_id AND cr.child_component_id = c.component_id AND cr.component_rel_type_id = 1 AND cr.child_component_id = ";

    // Tack on component id later below

    // Initialize variables

    $child_component_id = $component_id;
    $found = false;

    while (!$found && $child_component_id != 0) {

      $query_complete = $query_parent_ioc . $child_component_id . ';';

      if (!$piocResult = mysql_query($query_complete)) {
        $errno = mysql_errno();
        $error = "findParentIoc(): ".mysql_error();

        // Write error to Global Search Tool log file

        return null;
      }
      if ($piocResult) {
        if ($row = mysql_fetch_array($piocResult)) {
          $component_name = $row['component_instance_name'];
          $component_type_name = $row['component_type_name'];
          $parent_id = $row['parent_component_id'];
          $logical_desc = $row['logical_desc'];

          if ($parent_id == 1) {
            $found = true;
            break;
          }
          $child_component_id = $parent_id;
        } else {
          return "";
        }
      } else {
        return "";
      }
    }
    // need this check during transition
    return $logical_desc;

  }

  function getComponentParentRack($component_id) {

    // Begin building query for component_type table

	$query_parent_rack = "SELECT c.component_instance_name, ct.component_type_name, cr.parent_component_id, cr.logical_desc FROM component c, component_type ct, component_rel cr WHERE c.component_type_id = ct.component_type_id AND cr.child_component_id = c.component_id AND cr.component_rel_type_id = 2 AND cr.child_component_id = ";

    // Tack on component id later below

    // Initialize variables

    $child_component_id = $component_id;
    $found = false;

    $logical_desc = $previous_logical_desc = "";
    $component_type_name = $previous_component_type_name = "";
    $component_name = $previous_component_name = "";

    $num_hops = 0;

    while (!$found && $child_component_id != 0) {

      $query_complete = $query_parent_rack . $child_component_id . ';';

      if (!$piocResult = mysql_query($query_complete)) {
        $errno = mysql_errno();
        $error = "findParentRack(): ".mysql_error();

        // Write error to Global Search Tool log file

        return null;
      }
      if ($piocResult) {
        if ($row = mysql_fetch_array($piocResult)) {
          $previous_logical_desc = $logical_desc;
          $previous_component_type_name = $component_type_name;
          $previous_component_instance_name = $component_name;

          $component_name = $row['component_instance_name'];
          $component_type_name = $row['component_type_name'];
          $parent_id = $row['parent_component_id'];
          $logical_desc = $row['logical_desc'];

          if ($component_type_name == 'Room') {
            if ($num_hops == 1) {
              // components immediate parent is Room
              return "";
            } else {
              $found = true;
              break;
            }
          }
		  if ($component_type_name == 'Beamline_Area') {
                if ($num_hops == 1) {  # components immediate parent is Beamline_Area
                  return "";
                } else {
                  $found = true;
                  break;
                }
          }
		  if ($component_type_name == 'Hutch') {
                if ($num_hops == 1) {  # components immediate parent is Hutch
                  return "";
                } else {
                  $found = true;
                  break;
                }
          }
		  if ($component_type_name == 'Sector') {
                if ($num_hops == 1) {  # components immediate parent is Sector
                  return "";
                } else {
                  $found = true;
                  break;
                }
          }

          $child_component_id = $parent_id;
          $num_hops++;
        } else {
          return "";
        }
      } else {
        return "";
      }
    }
    return $previous_component_type_name . ' ' . $previous_component_instance_name;
  }

  function getComponentParentRoom($component_id) {

    // Begin building query for component_type table

	$query_parent_room = "SELECT c.component_instance_name, ct.component_type_name, cr.parent_component_id, cr.logical_desc FROM component c, component_type ct, component_rel cr WHERE c.component_type_id = ct.component_type_id AND cr.child_component_id = c.component_id AND cr.component_rel_type_id = 2 AND cr.child_component_id = ";

    // Tack on component id below

    // Initialize variables

    $child_component_id = $component_id;
    $found = false;

    while (!$found && $child_component_id != 0) {

      $query_complete = $query_parent_room . $child_component_id . ';';

      if (!$piocResult = mysql_query($query_complete)) {
        $errno = mysql_errno();
        $error = "findParentRoom(): ".mysql_error();

        // Write error to the Global Search Tool log file...

        return null;
      }
      if ($piocResult) {
        if ($row = mysql_fetch_array($piocResult)) {
          $component_name = $row['component_instance_name'];
          $component_type_name = $row['component_type_name'];
          $parent_id = $row['parent_component_id'];
          $logical_desc = $row['logical_desc'];

          if ($component_type_name == 'Room') {
            $found = true;
            break;
          }
          $child_component_id = $parent_id;
        } else {
          return "";
        }
      } else {
        return "";
      }
    }
    return $logical_desc;
  }


  function getComponentParentBuilding($component_id) {

    // Begin building query for component_type table

    $query_parent_building = "SELECT component.component_instance_name, component_type.component_type_name, component_rel.parent_component_id, component_rel.logical_desc FROM component, component_type, component_rel WHERE component.component_type_id = component_type.component_type_id AND component_rel.child_component_id = component.component_id AND component_rel.component_rel_type_id = 2 AND component_rel.child_component_id = ";

    // Tack on component id below

    // Initialize variables

    $child_component_id = $component_id;
    $found = false;

    while (!$found && $child_component_id != 0) {

      $query_complete = $query_parent_building . $child_component_id . ';';

      if (!$piocResult = mysql_query($query_complete)) {
        $errno = mysql_errno();
        $error = "findParentIoc(): ".mysql_error();

        // Write error to global search tool log file...

        return null;
      }
      if ($piocResult) {
        if ($row = mysql_fetch_array($piocResult)) {
          $component_name = $row['component_instance_name'];
          $component_type_name = $row['component_type_name'];
          $parent_id = $row['parent_component_id'];
          $logical_desc = $row['logical_desc'];

          if ($component_type_name == 'Building') {
            $found = true;
            break;
          }
          $child_component_id = $parent_id;
        } else {
          return "";
        }
      } else {
        return "";
      }
    }
    return $logical_desc;
  }


?>
