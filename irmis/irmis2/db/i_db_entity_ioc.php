<?php
/*
 * Defines business class IOCList which contains an array of IOCEntity.
 */

/*
 * IOCEntity corresponds to a single row from the IRMIS ioc table.
 */
class IOCEntity {
  var $iocID;
  var $iocName;
  var $status;
  var $system;
  var $location;
  var $general_functions;
  var $sys_boot_line;
  var $ioc_boot_date;
  var $cog_developer;
  var $cog_developer_first;
  var $cog_tech;
  var $cog_tech_first;
  var $pre_boot_instr;
  var $post_boot_instr;
  var $power_cycle_caution;
  var $sysreset_reqd;
  var $inhibit_auto_reboot;
  var $PrimEnetSwRackNo;
  var $PrimEnetSwitch;
  var $PrimEnetBlade;
  var $PrimEnetPort;
  var $PrimEnetMedConvCh;
  var $PrimMediaConvPort;
  var $SecEnetSwRackNo;
  var $SecEnetSwitch;
  var $SecEnetBlade;
  var $SecEnetPort;
  var $SecEnetMedConvCh;
  var $SecMedConvPort;
  var $TermServRackNo;
  var $TermServName;
  var $TermServPort;
  var $TermServFiberConvCh;
  var $TermServFiberConvPort;
  var $componentID; 
  var $form_factor_id;
  var $ac1;
  var $ac2;


  function IOCEntity($iocID, $iocName, $status, $system, $location,
           $general_functions, $sys_boot_line, $ioc_boot_date, $cog_developer, $cog_developer_first,
		   $cog_tech, $cog_tech_first, $pre_boot_instr, $post_boot_instr, $power_cycle_caution,
		   $sysreset_reqd, $inhibit_auto_reboot, $PrimEnetSwRackNo, $PrimEnetSwitch,
		   $PrimEnetBlade, $PrimEnetPort, $PrimEnetMedConvCh, $PrimMediaConvPort,
		   $SecEnetSwRackNo, $SecEnetSwitch, $SecEnetBlade, $SecEnetPort, $SecEnetMedConvCh,
		   $SecMedConvPort, $TermServRackNo, $TermServName, $TermServPort, $TermServFiberConvCh,
		   $TermServFiberConvPort, $componentID, $form_factor_id, $ac1, $ac2) { 
    $this->iocID = $iocID;
    $this->iocName = $iocName;
    $this->status = $status;
    $this->system = $system;
	$this->location = $location;
	$this->general_functions = $general_functions;
	$this->sys_boot_line = $sys_boot_line;
	$this->ioc_boot_date = $ioc_boot_date;
	$this->cog_developer = $cog_developer;
	$this->cog_developer_first = $cog_developer_first;
	$this->cog_tech = $cog_tech;
	$this->cog_tech_first = $cog_tech_first;
	$this->pre_boot_instr = $pre_boot_instr;
	$this->post_boot_instr = $post_boot_instr;
	$this->power_cycle_caution = $power_cycle_caution;
	$this->sysreset_reqd = $sysreset_reqd;
	$this->inhibit_auto_reboot = $inhibit_auto_reboot;
	$this->PrimEnetSwRackNo = $PrimEnetSwRackNo;
	$this->PrimEnetSwitch = $PrimEnetSwitch;
	$this->PrimEnetBlade = $PrimEnetBlade;
	$this->PrimEnetPort = $PrimEnetPort;
	$this->PrimEnetMedConvCh = $PrimEnetMedConvCh;
	$this->PrimMediaConvPort = $PrimMediaConvPort;
	$this->SecEnetSwRackNo = $SecEnetSwRackNo;
	$this->SecEnetSwitch = $SecEnetSwitch;
	$this->SecEnetBlade = $SecEnetBlade;
	$this->SecEnetPort = $SecEnetPort;
	$this->SecEnetMedConvCh = $SecEnetMedConvCh;
	$this->SecMedConvPort = $SecMedConvPort;
	$this->TermServRackNo = $TermServRackNo;
	$this->TermServName = $TermServName;
	$this->TermServPort = $TermServPort;
	$this->TermServFiberConvCh = $TermServFiberConvCh;
	$this->TermServFiberConvPort = $TermServFiberConvPort;
	$this->componentID = $componentID; 
	$this->form_factor_id = $form_factor_id;
	$this->ac1 = $ac1;
	$this->ac2 = $ac2;
	
  }
  function getIocID() {
    return $this->iocID;
  }
  function getIocName() {
    return $this->iocName;
  }
  function getStatus() {
    return $this->status;
  }
  function getSystem() {
    return $this->system;
  }
  function getLocation() {
    return $this->location;
  }
  function getGeneralFunctions() {
    return $this->general_functions;
  }
  function getSysBootLine()  {
    return $this->sys_boot_line;
  }
  function getIocBootDate()  {
    return $this->ioc_boot_date;
  }
  function getCogDeveloper() {
    return $this->cog_developer;
  }
  function getCogDeveloper_first() {
    return $this->cog_developer_first;
  }
  function getCogTech() {
    return $this->cog_tech;
  }
  function getCogTech_first() {
    return $this->cog_tech_first;
  }
  function getPreBoot() {
    return $this->pre_boot_instr;
  }
  function getPostBoot() {
    return $this->post_boot_instr;
  }
  function getPowerCycleCaution() {
    return $this->power_cycle_caution;
  }
  function getSysResetReqd() {
    return $this->sysreset_reqd;
  }
  function getInhibitAutoReboot() {
    return $this->inhibit_auto_reboot;
  }
  function getPrimEnetSwRackNo() {
    return $this->PrimEnetSwRackNo;
  }
  function getPrimEnetSwitch() {
    return $this->PrimEnetSwitch;
  }
  function getPrimEnetBlade() {
    return $this->PrimEnetBlade;
  }
  function getPrimEnetPort() {
    return $this->PrimEnetPort;
  }
  function getPrimEnetMedConvCh() {
    return $this->PrimEnetMedConvCh;
  }
  function getPrimMediaConvPort() {
    return $this->PrimMediaConvPort;
  }
  function getSecMedConvPort() {
    return $this->SecMedConvPort;
  }
  function getSecEnetSwitch() {
    return $this->SecEnetSwitch;
  }
  function getSecEnetBlade() {
    return $this->SecEnetBlade;
  }
  function getSecEnetPort() {
    return $this->SecEnetPort;
  }
  function getSecEnetMedConvCh() {
    return $this->SecEnetMedConvCh;
  }
  function getSecEnetSwRackNo() {
    return $this->SecEnetSwRackNo;
  }
  function getTermServRackNo() {
    return $this->TermServRackNo;
  }
  function getTermServName() {
    return $this->TermServName;
  }
  function getTermServPort() {
    return $this->TermServPort;
  }
  function getTermServFiberConvCh() {
    return $this->TermServFiberConvCh;
  }
  function getTermServFiberConvPort() {
    return $this->TermServFiberConvPort;
  }
  function getComponentID() { 
    return $this->componentID; 
  }
  function getFormFactorID() {
    return $this->form_factor_id;
  }
  function getac1() {
    return $this->ac1;
  }
  function getac2() {
    return $this->ac2;
  }
  

  function getParentRack($dbConn, $component_id) {
        global $errno;
        global $error;


		$conn = $dbConn->getConnection();


        // Begin building query for component_type table
    	// SELECT c.component_instance_name, ct.component_type_name, cr.parent_component_id, cr.logical_desc
    	// FROM component c, component_type ct, component_rel cr
    	// WHERE c.component_type_id = ct.component_type_id
    	// AND cr.child_component_id = c.component_id
    	// AND cr.component_rel_type_id = 2
    	// AND cr.child_component_id =
        $qb = new DBQueryBuilder();
        $qb->appendSemicolon(false);
        $qb->addColumn("c.component_instance_name");
        $qb->addColumn("ct.component_type_name");
        $qb->addColumn("cr.parent_component_id");
        $qb->addColumn("cr.logical_desc");

        $qb->addTable("component c");
        $qb->addTable("component_type ct");
    	$qb->addTable("component_rel cr");

    	$qb->addWhere("c.component_type_id = ct.component_type_id");
    	$qb->addWhere("cr.child_component_id = c.component_id");
        $qb->addWhere("cr.component_rel_type_id = 2");
    	$qb->addWhere("cr.child_component_id = ");  // tack on id below
        $piocQuery = $qb->getQueryString(false);

        $child_component_id = $component_id;
        $found = false;

        $logical_desc = $previous_logical_desc = "";
        $component_name = $previous_component_name = "";

        $component_type_name = $previous_component_type_name = "";

        $num_hops = 0;
        while (!$found && $child_component_id != 0) {
          logEntry('debug',"find parent rack for $child_component_id");
          $query = $piocQuery . $child_component_id . ';';

          if (!$piocResult = mysql_query($query, $conn)) {
            $errno = mysql_errno();
            $error = "findParentRack(): ".mysql_error();
            logEntry('critical',$error);
            return null;
          }
          if ($piocResult) {
            if ($row = mysql_fetch_array($piocResult)) {

              $previous_logical_desc = $logical_desc;
              $previous_component_instance_name = $component_name;

              $previous_component_type_name = $component_type_name;
              $component_name = $row['component_instance_name'];
              $component_type_name = $row['component_type_name'];
              $parent_id = $row['parent_component_id'];
              $logical_desc = $row['logical_desc'];

              if ($component_type_name == 'Room') {
                if ($num_hops == 1) {  # component's immediate parent is Room
                  return "";
                } else {
                  $found = true;
                  break;
                }
              }
			  if ($component_type_name == 'Beamline_Area') {
                if ($num_hops == 1) {  # component's immediate parent is Beamline_Area
                  return "";
                } else {
                  $found = true;
                  break;
                }
              }
			  if ($component_type_name == 'Hutch') {
                if ($num_hops == 1) {  # component's immediate parent is Hutch
                  return "";
                } else {
                  $found = true;
                  break;
                }
              }
			  if ($component_type_name == 'Sector') {
                if ($num_hops == 1) {  # component's immediate parent is Sector
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



  function getParentRoom($dbConn, $component_id) {
      global $errno;
      global $error;

      $conn = $dbConn->getConnection();

    // Begin building query for component_type table

  	// SELECT c.component_instance_name, ct.component_type_name, cr.parent_component_id, cr.logical_desc
  	// FROM component c, component_type ct, component_rel cr
  	// WHERE c.component_type_id = ct.component_type_id
  	// AND cr.child_component_id = c.component_id
  	// AND cr.component_rel_type_id = 2
  	// AND cr.child_component_id =


      $qb = new DBQueryBuilder();
      $qb->appendSemicolon(false);
      $qb->addColumn("c.component_instance_name");
      $qb->addColumn("ct.component_type_name");
      $qb->addColumn("cr.parent_component_id");
      $qb->addColumn("cr.logical_desc");

      $qb->addTable("component c");
      $qb->addTable("component_type ct");
  	  $qb->addTable("component_rel cr");

  	  $qb->addWhere("c.component_type_id = ct.component_type_id");
  	  $qb->addWhere("cr.child_component_id = c.component_id");
      $qb->addWhere("cr.component_rel_type_id = 2");
  	  $qb->addWhere("cr.child_component_id = ");  // tack on id below
      $piocQuery = $qb->getQueryString(false);

      $child_component_id = $component_id;
      $found = false;

      while (!$found && $child_component_id != 0) {
        logEntry('debug',"find parent room for $child_component_id");
        $query = $piocQuery . $child_component_id . ';';

        if (!$piocResult = mysql_query($query, $conn)) {
          $errno = mysql_errno();
          $error = "findParentRoom(): ".mysql_error();
          logEntry('critical',$error);
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
			elseif ($component_type_name == 'Sector') {
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
      // return $logical_desc;  Sometimes this field is empty!!!

      return $component_name;
  }


} 

/*
 * IOCList is a business object which contains a collection of information
 * representing the list of all current IOC's in the IRMIS database. It is
 * essentially a wrapper for an array of IOCEntity, along with the standard
 * "loadFromDB()" function and other utility functions.
 */
class IOCList {
  // an array of IOCEntity
  var $iocEntities;

  function IOCList() {
    $this->iocEntities = array();
  }

  function getElement($idx) {
    return $this->iocEntities[$idx];
  }

  // return size of array
  function length() {
    if ($this->iocEntities == null)
      return 0;
    else
      return count($this->iocEntities);
  }

  /*
   * getArray() returns an array of IOCEntity (assuming loadFromDB has
   * been called already).
   */
  function getArray() {
    return $this->iocEntities;
  }

  // Returns getElementForIocName equal to $iocName
  function getElementForIocName($iocName) {
    foreach ($this->iocEntities as $iocEntity) {
	  if ($iocEntity->getIocName() == $iocName) {
	    return $iocEntity;
	  }
	 }
	 logEntry('debug',"Unable to find IocEntity for iocName $iocName");
	 return null;
  }

  // Returns IOCEntity with primary key $id from ioc table
  function getElementForId($id) {
    foreach ($this->iocEntities as $iocEntity) {
      if ($iocEntity->getIocID() == $id) {
        return $iocEntity;
      }
    }
    logEntry('debug',"Unable to find IocEntity for id $id");
    return null;
  }

  /*
   * Conducts MySQL db transactions to initialize IOCList from the
   * ioc table. Returns false if db error occurs, true
   * otherwise. */
  function loadFromDB($dbConn, $iocNameConstraint) {
    global $errno;
    global $error;

    $conn = $dbConn->getConnection();
    $tableNamePrefix = $dbConn->getTableNamePrefix();

    // Get core ioc info from ioc table
    $qb = new DBQueryBuilder($tableNamePrefix);
    $qb->addColumn("ioc.*");
    $qb->addColumn("aps_ioc.*");
	$qb->addColumn("ioc.component_id");
	$qb->addColumn("ioc_boot.sys_boot_line");
	$qb->addColumn("ioc_boot.ioc_boot_date");
	$qb->addColumn("pt1.last_nm cog_developer");
	$qb->addColumn("pt1.first_nm cog_developer_first");
	$qb->addColumn("pt2.last_nm cog_tech");
	$qb->addColumn("pt2.first_nm cog_tech_first");
	$qb->addTable("aps_ioc left join person pt1 on aps_ioc.cog_developer_id=pt1.person_id left join person pt2 on aps_ioc.cog_technician_id=pt2.person_id");
	$qb->addTable("ioc left join ioc_boot using (ioc_id)");
	$qb->addTable("ioc_status");
	$qb->addWhere("aps_ioc.ioc_id = ioc.ioc_id");
	$qb->addWhere("ioc.ioc_status_id = ioc_status.ioc_status_id");
    // only add this if given
	if ($iocNameConstraint) {
      // convert the * to %
      // $iocNameConstraint = str_replace("*","%",$iocNameConstraint);
      $qb->addWhere("ioc_nm like '%".$iocNameConstraint."%'");
	}
	// only add this if given
	$iocSystemConstraint = $_SESSION['system'];
	if ($iocSystemConstraint) {
      $qb->addWhere("system like '".$iocSystemConstraint."'");
    }
	// only add this if given
	$Accioc = $_SESSION['Accioc'];
	$BLioc = $_SESSION['BLioc'];
	if (isset($Accioc) && isset($BLioc)) {
	  $qb->addWhere("system like '%'");
	} elseif (isset ($BLioc)) {
	  $qb->addWhere("system like 'BL%'");
	} elseif (isset($Accioc)) {
	  $qb->addWhere("system not like 'BL%'");
	}
	// only add this if given
	$iocStatusConstraint = $_SESSION['status'];
	if ($iocStatusConstraint == "---- All ----") {
	  $qb->addWhere("ioc_status like '%'");
	} elseif ($iocStatusConstraint) {
      $qb->addWhere("ioc_status like '".$iocStatusConstraint."'");
    } else {
	  $qb->addWhere("ioc_status not like 'Inactive'");
	}
	// only add this if given
	$cogDeveloper = $_SESSION['developer'];
	if ($cogDeveloper) {
      $qb->addWhere("pt1.last_nm like '".$cogDeveloper."'");
	}
	// only add this if given
	$cogTech = $_SESSION['tech'];
	if ($cogTech) {
      $qb->addWhere("pt2.last_nm like '".$cogTech."'");
    }
	$qb->addSuffix("group by ioc_nm");
	$qb->addSuffix("order by ioc_nm");
    $iocQuery = $qb->getQueryString();
    logEntry('debug',$iocQuery);

    if (!$iocResult = mysql_query($iocQuery, $conn)) {
      $errno = mysql_errno();
      $error = "IOCList.loadFromDB(): ".mysql_error();
      logEntry('critical',$error);
      return false;
    }

    $idx = 0;
    if ($iocResult) {
      while ($iocRow = mysql_fetch_array($iocResult)) {
		  
		  
/*----------------------------------------------------------------------------------------------------------	*/  
  if ($iocRow['active'] == 1 && $iocRow['component_id'] != "" && $iocRow['system'] != "BL-32ID") {

    $compID = $iocRow['component_id'];
	  logEntry('debug',"component number is ".$compID);
	  
    $count = 0;
	$iteration = 0;
	$locationPath = array();
    while ($count != 1) {	  

    $qb1 = new DBQueryBuilder($tableNamePrefix);
	$qb1->addColumn("ct.component_type_name");
    $qb1->addColumn("description");
	$qb1->addColumn("ct.component_type_id");
	$qb1->addColumn("c.component_instance_name");
	$qb1->addColumn("cr.logical_desc");
	$qb1->addColumn("cr.child_component_id");
	
	$qb1->addTable("component_type ct");
	$qb1->addTable("component c");
	$qb1->addTable("component_rel cr");
	
	// the subselect finds the parent id of the child that was currently found
	$qb1->addWhere("cr.component_rel_type_id = '3'");
	$qb1->addWhere("c.component_id = cr.child_component_id");
	$qb1->addWhere("c.component_type_id = ct.component_type_id");
	$qb1->addWhere("cr.mark_for_delete = '0'");
	$qb1->addWhere("cr.child_component_id = (select cr.parent_component_id                             
	                                         from component_type ct, component c, component_rel cr
											 where cr.component_rel_type_id = '3'
											 and c.component_id = cr.parent_component_id
											 and c.component_type_id = ct.component_type_id
											 and cr.child_component_id = '$compID'
											 and cr.mark_for_delete = '0')");
    $locationQuery = $qb1->getQueryString();
    //logEntry('debug',$locationQuery);

    //if (!mysql_query($locationQuery, $conn)) {
	//	exit;
	//}
    if (!$locationResult = mysql_query($locationQuery, $conn)) {
      $errno = mysql_errno();
      $error = "IOCContentsList.loadFromDB(): ".mysql_error();
      logEntry('critical',$error);
      return false;             
    }


   if ($locationResult && $iteration < 24) {
     if ($row = mysql_fetch_array($locationResult)) {
	    $component_type_name = $row['component_type_name'];
		$compID = $row['child_component_id'];
		$component_instance_name = $row['component_instance_name'];
		$logical_desc = $row['logical_desc'];
	
		if (strstr($component_type_name, "Switch Gear")) {
		  $count=1;
		  array_push($locationPath, $component_type_name);      // pushes the component_type_name into the $locationpath array
		  array_push($locationPath, $logical_desc);             // pushes the logical_desc into the $locationpath array
		  $iocRow['ac1'] = array_pop($locationPath);            // pops out each element of the array into pieces that are accessable to search pages
		  $iocRow['ac2'] = array_pop($locationPath);            //
		} else if (strstr($component_type_name, "Unspecified Power")) {
			$count=1;
			array_push($locationPath, $component_type_name);
			array_push($locationPath, $logical_desc);
			$iocRow['ac1'] = "Unspecified Power";
			$iocRow['ac2'] = "Unspecified Power";
		} else {
		  $count=0;
		  $iteration = +1;
		  //array_push($locationPath, $component_type_name);
		  //array_push($locationPath, $logical_desc);
		  
		}
      } // end if
    } // end if
} // end while
  } else if ($iocRow['component_id'] = "") {
	  $iocRow['ac1'] = "Not In IDT";
	  $iocRow['ac2'] = "Not In IDT";
  } else {
	  $iocRow['ac1'] = "No Power";
	  $iocRow['ac2'] = "No Power";
  }

//--------------------------------------------------------------------------------------------------------------------------
  
		  
        $iocEntity = new IOCEntity($iocRow['ioc_id'], $iocRow['ioc_nm'], $iocRow['ioc_status_id'],
		$iocRow['system'], $iocRow['location'], $iocRow{'general_functions'},
		$iocRow['sys_boot_line'], $iocRow['ioc_boot_date'], $iocRow['cog_developer'], $iocRow['cog_developer_first'], $iocRow['cog_tech'], $iocRow['cog_tech_first'],
		$iocRow['pre_boot_instr'], $iocRow['post_boot_instr'], $iocRow['power_cycle_caution'],
		$iocRow['sysreset_reqd'], $iocRow['inhibit_auto_reboot'], $iocRow['PrimEnetSwRackNo'],
		$iocRow['PrimEnetSwitch'], $iocRow['PrimEnetBlade'], $iocRow['PrimEnetPort'], $iocRow['PrimEnetMedConvCh'],
		$iocRow['PrimMediaConvPort'], $iocRow['SecEnetSwRackNo'], $iocRow['SecEnetSwitch'], $iocRow['SecEnetBlade'],
		$iocRow['SecEnetPort'], $iocRow['SecEnetMedConvCh'], $iocRow['SecMedConvPort'], $iocRow['TermServRackNo'],
		$iocRow['TermServName'], $iocRow['TermServPort'], $iocRow['TermServFiberConvCh'], $iocRow['TermServFiberConvPort'], $iocRow['component_id'], $iocRow['form_factor_id'],
		$iocRow['ac1'], $iocRow['ac2']);
        $this->iocEntities[$idx] = $iocEntity;
        $idx = $idx +1;
      }
    }
    return true;
  }
}

?>