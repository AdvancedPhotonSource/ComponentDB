<?php
/*
 * Defines business class AOIList which contains an array of AOIEntity.
 */


class AOIEntity {
  var $aoiID;
  var $aoiName;
  var $aoiCognizant1;
  var $aoiCognizant2;
  var $aoiCustomerContact;
  var $aoiStatus;
  var $aoiDescription;
  var $aoiWorklog;
  var $aoiCriticality;
  var $aoiTechnicalSystem;
  var $aoiMachine;
  var $aoiPlcName;
  var $aoiIocName;
  var $aoiKeyword;
  var $aoiPvSearch;

  function AOIEntity(
               $aoiID,
               $aoiName,
               $aoiCognizant1,
			   //$aoiCongnizant2,
 			   $aoiCriticality,
			   $aoiTechnicalSystem,
	           $aoiMachine,
               $aoiKeyword,
			   $aoiDescription,
               $aoiWorklog,
               $aoiCustomerContact,
			   $aoiStatus
         ) {

    $this->aoiID = $aoiID;
    $this->aoiName = $aoiName;
    $this->aoiCognizant1 = $aoiCognizant1;
    //$this->aoiCognizant2 = $aoiCognizant2;
    $this->aoiCustomerContact = $aoiCustomerContact;
    $this->aoiStatus = $aoiStatus;
    $this->aoiDescription = $aoiDescription;
    $this->aoiWorklog = $aoiWorklog;
    $this->aoiCriticality = $aoiCriticality;
    $this->aoiTechnicalSystem = $aoiTechnicalSystem;
    $this->aoiMachine = $aoiMachine;
    $this->aoiPlcName = $aoiPlcName;
    $this->aoiIocName = $aoiIocName;
    $this->aoiKeyword = $aoiKeyword;
  }
  function getAOIID() {
    return $this->aoiID;
  }
  function getAOIName() {
    return $this->aoiName;
  }
  function getAOICognizant1() {
    return $this->aoiCognizant1;
  }
  function getAOICriticality() {
    return $this->aoiCriticality;
  }
  function getAOITechnicalSystem() {
    return $this->aoiTechnicalSystem;
  }
  function getAOIMachine() {
    return $this->aoiMachine;
  }


  //function getAOICognizant2() {
  //  return $this->aoiCognizant2;
  //}

  function getAOICustomerContact() {
    return $this->aoiCustomerContact;
  }
  function getAOIStatus() {
    return $this->aoiStatus;
  }
  function getAOIDescription() {
    return $this->aoiDescription;
  }
  function getAOIWorklog() {
    return $this->aoiWorklog;
  }
  function getAOIKeyword() {
    return $this->aoiKeyword;
  }

}


/*
 * AOIList is a business object which contains a collection of information
 * representing the list of all current AOI's in the IRMIS database. It is
 * essentially a wrapper for an array of AOIEntity, along with the standard
 * "loadFromDB()" function and other utility functions.
 */
class AOIList {
  // an array of AOIEntity
  var $aoiEntities;

  function AOIList() {
    $this->aoiEntities = array();
  }

  function getElement($idx) {
    return $this->aoiEntities[$idx];
  }

  // return size of array
  function length() {
    if ($this->aoiEntities == null)
      return 0;
    else
      return count($this->aoiEntities);
  }

  /*
   * getArray() returns an array of AOIEntity (assuming loadFromDB has
   * been called already).
   */
  function getArray() {
    return $this->aoiEntities;
  }

  // Returns getElementForAOIName equal to $aoiName

  function getElementForAOIName($aoiName) {
     foreach ($this->aoiEntities as $aoiEntity) {
	  if ($aoiEntity->getAOIName() == $aoiName) {
	    return $aoiEntity;
	  }
	 }
	 logEntry('debug',"Unable to find aoiEntity for aoiName $aoiName");
	 return null;
  }

  /*
   * Conducts MySQL db transactions to initialize AOIList from the
   * device table. Returns false if db error occurs, true
   * otherwise. */

  function loadFromDB($dbConn, $aoiNameConstraint) {
    global $errno;
    global $error;

    // temporary comment out $aoiPlcConstraint = $_SESSION['plcname'];

    $aoiIocConstraint = $_SESSION['iocname'];
    $aoiPvSearchConstraint = $_SESSION['pv_search'];
    $aoiDescSearchConstraint = $_SESSION['desc_search'];

    $conn = $dbConn->getConnection();
    $tableNamePrefix = $dbConn->getTableNamePrefix();

    // Get core aoi info from device table
    $qb = new DBQueryBuilder($tableNamePrefix);

    $qb->addDistinct(true);

    $qb->addColumn("aoi.aoi_id");
    $qb->addColumn("aoi.aoi_name");
    $qb->addColumn("person.last_nm");
    $qb->addColumn("criticality_type.criticality_level");
    $qb->addColumn("technical_system.technical_system");
    $qb->addColumn("machine.machine");
    $qb->addColumn("aoi.aoi_keyword");
    $qb->addColumn("aoi.aoi_description");
    $qb->addColumn("aoi.aoi_worklog");
    $qb->addColumn("person_customer.last_nm");
    $qb->addColumn("aoi_status.aoi_status");
    $qb->addTable("aoi");
    $qb->addTable("person");
    $qb->addTable("person as person_customer");
    $qb->addTable("criticality_type");
    $qb->addTable("aoi_criticality");
    $qb->addTable("aoi_techsys");
    $qb->addTable("technical_system");
    $qb->addTable("aoi_machine");
    $qb->addTable("machine");
    $qb->addTable("aoi_status");


    if ($aoiPvSearchConstraint || $aoiIocConstraint){
    		$qb->addTable("aoi_ioc_stcmd_line");
    		$qb->addTable("ioc_stcmd_line");
    }
    if ($aoiPvSearchConstraint) {
    		$qb->addTable("aoi_epics_record");
    }

    // temporary comment out  if ($aoiPlcConstraint) {
    //		$qb->addTable("aoi_plc_stcmd_line");
    //    	$qb->addTable("plc");
    // }

    if ($aoiIocConstraint) {
    		$qb->addTable("ioc");
    }

    $qb->addWhere("person.person_id = aoi.aoi_cognizant1_id");
    $qb->addWhere("person_customer.person_id = aoi.aoi_customer_contact_id");
    $qb->addWhere("aoi.aoi_id = aoi_criticality.aoi_id and aoi_criticality.criticality_id = criticality_type.criticality_id");
    $qb->addWhere("aoi.aoi_id = aoi_techsys.aoi_id and aoi_techsys.technical_system_id = technical_system.technical_system_id");
    $qb->addWhere("aoi.aoi_id = aoi_machine.aoi_id and aoi_machine.machine_id = machine.machine_id");
    $qb->addWhere("aoi.aoi_status_id = aoi_status.aoi_status_id");

    // Combine search for aoi.aoi_keyword with the aoi name constraint string supplied by user
    $aoiNameConstraint = $_SESSION['aoiNameConstraint'];
    if ($aoiNameConstraint) {
      // convert the * to %
      // $qb->addWhere("aoi.aoi_name like '%".$aoiNameConstraint."%'");
      $qb->addWhere("(aoi.aoi_name like '%".$aoiNameConstraint."%' OR aoi.aoi_keyword like '%".$aoiNameConstraint."%') ");
    }

    // temporary comment out  $aoiKeywordConstraint = $_SESSION['aoi_keyword'];
    // if ($aoiKeywordConstraint){
    //   $qb->addWhere("aoi.aoi_keyword like '%".$aoiKeywordConstraint."%' ");
    // }

	if ($aoiPvSearchConstraint || $aoiIocConstraint){
		$qb->addWhere("aoi.aoi_id = aoi_ioc_stcmd_line.aoi_id");
	}

	if ($aoiPvSearchConstraint){
		$qb->addWhere("aoi_ioc_stcmd_line.aoi_ioc_stcmd_line_id = aoi_epics_record.aoi_ioc_stcmd_line_id");
	}

    if ($aoiPvSearchConstraint) {
		$qb->addWhere("aoi_epics_record.rec_nm like '%".$aoiPvSearchConstraint."%' ");
    }

    if ($aoiDescSearchConstraint) {
		$qb->addWhere("aoi.aoi_description like '%".$aoiDescSearchConstraint."%' ");
    }

    $aoiTechnicalSystemConstraint = $_SESSION['techsystem'];
    if ($aoiTechnicalSystemConstraint) {
    	$qb->addWhere("technical_system.technical_system like '%".$aoiTechnicalSystemConstraint."%'");
    }


    $aoiMachineConstraint = $_SESSION['machine'];
    if ($aoiMachineConstraint) {
    	$qb->addWhere("machine.machine like '%".$aoiMachineConstraint."%'");
    }


    $aoiPersonConstraint = $_SESSION['person'];
    if ($aoiPersonConstraint) {

            // extract out last name and first name from person constraint information

	        $personNameArray = explode(', ',$aoiPersonConstraint);
	        if ($personNameArray[0])
	    	   $qb->addWhere("person.last_nm like '%".$personNameArray[0]."%'");

	    	if ($personNameArray[1])
    	       $qb->addWhere("person.first_nm like '%".$personNameArray[1]."%'");

    	// $qb->addWhere("person.last_nm like '%".$aoiPersonConstraint."%'");

    }

    if ($aoiIocConstraint) {
        $qb->addWhere("aoi_ioc_stcmd_line.ioc_stcmd_line_id = ioc_stcmd_line.ioc_stcmd_line_id");
    	$qb->addWhere("ioc_stcmd_line.ioc_id = ioc.ioc_id");
    	$qb->addWhere("ioc.ioc_nm = '".$aoiIocConstraint."'");
    }


    // if ($aoiPlcConstraint) {
    //	$qb->addWhere("aoi.aoi_id = aoi_plc_stcmd_line.aoi_id and aoi_plc_stcmd_line.plc_id = plc.plc_id");
    // 	$qb->addWhere("plc.plc_name like '%".$aoiPlcConstraint."%'");
    // }

    $aoiCriticalityConstraint = $_SESSION['criticality'];
    if ($aoiCriticalityConstraint) {
    	$qb->addWhere("aoi.aoi_id = aoi_criticality.aoi_id and aoi_criticality.criticality_id = criticality_type.criticality_id");
    	$qb->addWhere("criticality_type.criticality_level = $aoiCriticalityConstraint");
    }

	$qb->addSuffix("order by aoi.aoi_name");

    $aoiQuery = $qb->getQueryString();

    //echo "aoi query is: $aoiQuery";

    logEntry('debug',$aoiQuery);

    $aoiResult = 0;

	$aoiResult = mysql_query($aoiQuery, $conn);
	//echo mysql_error();

    $idx = 0;

    $aoi_name_results = array();
    $aoi_name_previous = "";

    if ($aoiResult) {

      //echo "about to retrieve all queried aoi results...";

      while ($aoiRow = mysql_fetch_array($aoiResult)) {

         //echo "results: aoi name = ".$aoiRow['aoi_name'];

         if ($aoiRow['aoi_name'] != $aoi_name_previous){

           	$aoiEntity = new AOIEntity($aoiRow['aoi_id'], $aoiRow['aoi_name'], $aoiRow['last_nm'],
        					 $aoiRow['criticality_level'], $aoiRow['technical_system'],
        					 $aoiRow['machine'], $aoiRow['aoi_keyword'], $aoiRow['aoi_description'],
        					 $aoiRow['aoi_func_criteria'], $aoiRow['last_nm'], $aoiRow['aoi_status']);

        	$this->aoiEntities[$idx] = $aoiEntity;

        	$aoi_name_results[$idx] = $aoiRow['aoi_name'];

        	//echo "aoi_id results: aoi_id is ".$aoiRow['aoi_id'];

        	$idx = $idx +1;
        	$aoi_name_previous = $aoiRow['aoi_name'];
        	//echo "aoi_name_previous is: ".$aoi_name_previous;
         }
      }
    }


    // if the user selected "include relatives" as a query option,
    // then perform additional query execution to retrieve all aoi relatives
    // and add them to $aoiEntity

    // Come back later to add relatives option...

    return true;
  }
}

?>
