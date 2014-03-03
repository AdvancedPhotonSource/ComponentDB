<?php
/*
 * Defines business class AOIBasicList which contains an array of AOIBasicEntity.
 */


class AOIBasicEntity {
  var $aoiID;
  var $aoiName;
  var $aoiCognizant1FirstName;
  var $aoiCognizant1LastName;
  var $aoiCognizant1UserID;
  var $aoiCognizant2FirstName;
  var $aoiCognizant2LastName;
  var $aoiCognizant2UserID;
  var $aoiCustomerContactFirstName;
  var $aoiCustomerContactLastName;
  var $aoiCustomerContactUserID;
  var $aoiStatus;
  var $aoiDescription;
  var $aoiWorklog;
  var $aoiCriticality;
  var $aoiTechnicalSystem;
  var $aoiMachine;
  var $aoiPlcName;
  var $aoiIocName;
  var $aoiKeyword;
  var $aoiCriticalityClassification;
  var $aoiCustomerGroup;
  var $aoiCriticalityLevel;

  function AOIBasicEntity(
                     $aoiID,
                     $aoiName,
                     $aoiCognizant1FirstName,
                     $aoiCognizant1LastName,
                     $aoiCognizant1UserID,
			   		 $aoiCognizant2FirstName,
			   		 $aoiCognizant2LastName,
			   		 $aoiCognizant2UserID,
 			   		 $aoiCriticality,
			   		 $aoiTechnicalSystem,
	                 $aoiMachine,
                     $aoiKeyword,
			   		 $aoiDescription,
                     $aoiWorklog,
                     $aoiCustomerContactFirstName,
                     $aoiCustomerContactLastName,
                     $aoiCustomerContactUserID,
			   		 $aoiStatus,
			   		 $aoiCriticalityClassification,
			   		 $aoiCustomerGroup,
					 $aoiCriticalityLevel
                     ) {

    $this->aoiID = $aoiID;
    $this->aoiName = $aoiName;
    $this->aoiCognizant1FirstName = $aoiCognizant1FirstName;
    $this->aoiCognizant1LastName = $aoiCognizant1LastName;
    $this->aoiCognizant1UserID = $aoiCognizant1UserID;
    $this->aoiCognizant2FirstName = $aoiCognizant2FirstName;
    $this->aoiCognizant2LastName = $aoiCognizant2LastName;
    $this->aoiCognizant2UserID = $aoiCognizant2UserID;
    $this->aoiCustomerContactFirstName = $aoiCustomerContactFirstName;
    $this->aoiCustomerContactLastName = $aoiCustomerContactLastName;
    $this->aoiCustomerContactUserID = $aoiCustomerContactUserID;
    $this->aoiStatus = $aoiStatus;
    $this->aoiDescription = $aoiDescription;
    $this->aoiWorklog = $aoiWorklog;
    $this->aoiCriticality = $aoiCriticality;
    $this->aoiTechnicalSystem = $aoiTechnicalSystem;
    $this->aoiMachine = $aoiMachine;
    $this->aoiPlcName = $aoiPlcName;
    $this->aoiIocName = $aoiIocName;
    $this->aoiKeyword = $aoiKeyword;
    $this->aoiCriticalityClassification = $aoiCriticalityClassification;
    $this->aoiCustomerGroup = $aoiCustomerGroup;
    $this->aoiCriticalityLevel = $aoiCriticalityLevel;
  }
  function getAOIID() {
    return $this->aoiID;
  }
  function getAOIName() {
    return $this->aoiName;
  }
  function getAOICognizant1FirstName() {
    return $this->aoiCognizant1FirstName;
  }

  function getAOICognizant1LastName() {
      return $this->aoiCognizant1LastName;
  }

  function getAOICognizant1UserID() {
  	  return $this->aoiCognizant1UserID;
  }

  function getAOICognizant2FirstName() {
  	return $this->aoiCognizant2FirstName;
  }

  function getAOICognizant2LastName() {
    return $this->aoiCognizant2LastName;
  }

  function getAOICognizant2UserID() {
  	return $this->aoiCognizant2UserID;
  }

  function getAOICriticality() {
    return $this->aoiCriticality;
  }
  function getAOICriticalityClassification() {
  	return $this->aoiCriticalityClassification;
  }

  function getAOICriticalityLevel() {
  	return $this->aoiCriticalityLevel;
  }
  function getAOITechnicalSystem() {
    return $this->aoiTechnicalSystem;
  }
  function getAOIMachine() {
    return $this->aoiMachine;
  }

  function getAOICustomerContactFirstName() {
    return $this->aoiCustomerContactFirstName;
  }

  function getAOICustomerContactLastName() {
  	return $this->aoiCustomerContactLastName;
  }

  function getAOICustomerContactUserID() {
  	return $this->aoiCustomerContactUserID;
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
  function getAOICustomerGroup() {
  	return $this->aoiCustomerGroup;
  }

}


/*
 * AOIBasicList is a business object which contains a collection of information
 * representing the list of all current AOI's in the IRMIS database. It is
 * essentially a wrapper for an array of AOIBasicEntity, along with the standard
 * "loadFromDB()" function and other utility functions.
 */
class AOIBasicList {
  // an array of AOIBasicEntity
  var $aoiBasicEntities;

  function AOIBasicList() {
    $this->aoiBasicEntities = array();
  }

  function getElement($idx) {
    return $this->aoiBasicEntities[$idx];
  }

  // return size of array
  function length() {
    if ($this->aoiBasicEntities == null)
      return 0;
    else
      return count($this->aoiBasicEntities);
  }

  /*
   * getArray() returns an array of AOIBasicEntity (assuming loadFromDB has
   * been called already).
   */
  function getArray() {
    return $this->aoiBasicEntities;
  }

  // Returns getElementForAOIName equal to $aoiName

  function getElementForAOIName($aoiName) {
       foreach ($this->aoiBasicEntities as $aoiBasicEntity) {
	  if ($aoiBasicEntity->getAOIName() == $aoiName) {
	    return $aoiBasicEntity;
	  }
	 }
	 logEntry('debug',"Unable to find aoiBasicEntity for aoiName $aoiName");
	 return null;
  }

  /*
   * Conducts MySQL db transactions to initialize AOIList from the
   * device table. Returns false if db error occurs, true
   * otherwise. */

  function loadFromDB($dbConn, $aoiIDConstraint) {
    global $errno;
    global $error;

    $conn = $dbConn->getConnection();
    $tableNamePrefix = $dbConn->getTableNamePrefix();

    // Get core aoi info from aoi table

    $qb = new DBQueryBuilder($tableNamePrefix);
    $qb->addColumn("aoi.aoi_id");
    $qb->addColumn("aoi.aoi_name");
    $qb->addColumn("person.first_nm");
    $qb->addColumn("person.last_nm");
    $qb->addColumn("person.userid");
    $qb->addColumn("cognizant2.first_nm");
    $qb->addColumn("cognizant2.last_nm");
    $qb->addColumn("cognizant2.userid");
    $qb->addColumn("criticality_type.criticality_level");
    $qb->addColumn("technical_system.technical_system");
    $qb->addColumn("machine.machine");
    $qb->addColumn("aoi.aoi_keyword");
    $qb->addColumn("aoi.aoi_description");
    $qb->addColumn("aoi.aoi_worklog");
    $qb->addColumn("person_customer.first_nm");
    $qb->addColumn("person_customer.last_nm");
    $qb->addColumn("person_customer.userid");
    $qb->addColumn("aoi_status.aoi_status");
    $qb->addColumn("criticality_type.criticality_classification");
    $qb->addColumn("group_name");
    $qb->addColumn("criticality_type.criticality_level");
    $qb->addTable("aoi");
    $qb->addTable("person");
    $qb->addTable("person as person_customer");
    $qb->addTable("person as cognizant2");
    $qb->addTable("criticality_type");
    $qb->addTable("aoi_criticality");
    $qb->addTable("aoi_techsys");
    $qb->addTable("technical_system");
    $qb->addTable("aoi_machine");
    $qb->addTable("machine");
    $qb->addTable("aoi_status");
	$qb->addTable("group_name");

    $qb->addWhere("person.person_id = aoi.aoi_cognizant1_id");
    $qb->addWhere("cognizant2.person_id = aoi.aoi_cognizant2_id");
    $qb->addWhere("person_customer.person_id = aoi.aoi_customer_contact_id");
    $qb->addWhere("aoi.aoi_id = '".$aoiIDConstraint."'");
    $qb->addWhere("aoi_criticality.aoi_id = aoi.aoi_id and aoi_criticality.criticality_id = criticality_type.criticality_id");
    $qb->addWhere("aoi.aoi_id = aoi_techsys.aoi_id and aoi_techsys.technical_system_id = technical_system.technical_system_id");
    $qb->addWhere("aoi.aoi_id = aoi_machine.aoi_id and aoi_machine.machine_id = machine.machine_id");
    $qb->addWhere("aoi.aoi_status_id = aoi_status.aoi_status_id");
    $qb->addWhere("aoi.aoi_customer_group_id = group_name.group_name_id");

    $aoiQuery = $qb->getQueryString();
    logEntry('debug',$aoiQuery);

    $aoiResult = 0;

    if (!$aoiResult = mysql_query($aoiQuery, $conn)) {
      $errno = mysql_errno();
      $error = "AOIBasicList.loadFromDB(): ".mysql_error();
      logEntry('critical',$error);
      return false;
    }

    $idx = 0;
    $aoiNamePrevious = "";

    $aoi_name_results = array();

    if ($aoiResult) {
      while ($aoiRow = mysql_fetch_array($aoiResult)) {
        // filter out duplicate aoi names

        // echo "first name ".$aoiRow['first_nm']." last name ".$aoiRow['last_nm']." user id ".$aoiRow['userid']." ";

        if ($aoiRow['aoi_name'] != $aoiNamePrevious) {

        	$aoiBasicEntity = new AOIBasicEntity($aoiRow[0],
        	$aoiRow[1],
        	$aoiRow[2], $aoiRow[3], $aoiRow[4],
        	$aoiRow[5], $aoiRow[6], $aoiRow[7],
        	$aoiRow[8],
        	$aoiRow[9],
        	$aoiRow[10],
        	$aoiRow[11],
        	$aoiRow[12],
        	$aoiRow[13],
        	$aoiRow[14], $aoiRow[15], $aoiRow[16],
        	$aoiRow[17],
        	$aoiRow[18],
        	$aoiRow[19],
            $aoiRow[20]);

        	$this->aoiBasicEntities[$idx] = $aoiBasicEntity;


        	$aoi_name_results[$idx] = $aoiRow['aoi_name'];

        	// echo "person first name is: ".$aoiRow[2]." ";
		// echo "customer first name is: ".$aoiRow[14]." ";

        	$idx = $idx +1;
        	$aoiNamePrevious = $aoiRow['aoi_name'];
        }
      }



    }



    return true;
  }
}

?>
