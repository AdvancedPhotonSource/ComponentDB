<?php   
/*
 * Defines business class swList which contains an array of swEntity.
 */
/*
 * swDetailsEntity corresponds to a single row from the IRMIS aps_ioc table.
 */
class swDetailsEntity {
  var $swPrimEnetSwitch;
  var $swPrimEnetBlade;
  var $swPrimEnetPort;
  var $swPrimEnetMedConvCh;
  var $swPrimMediaConvPort;
  var $swSecEnetSwitch;
  var $swSecEnetBlade;
  var $swSecEnetPort;
  var $swSecEnetMedConvCh;
  var $swSecMedConvPort;
  var $swConnection;
  var $swLocation;
  var $swID;
 
  function swDetailsEntity($swPrimEnetSwitch, $swPrimEnetBlade, $swPrimEnetPort, $swPrimEnetMedConvCh, $swPrimMediaConvPort,
                           $swSecEnetSwitch, $swSecEnetBlade, $swSecEnetPort, $swSecEnetMedConvCh, $swSecMedConvPort, 
						   $swConnection, $swLocation) {
    $this->swPrimEnetSwitch = $swPrimEnetSwitch;
	$this->swPrimEnetBlade = $swPrimEnetBlade;
	$this->swPrimEnetPort = $swPrimEnetPort;
	$this->swPrimEnetMedConvCh = $swPrimEnetMedConvCh;
	$this->swPrimMediaConvPort = $swPrimMediaConvPort;
	$this->swSecEnetSwitch = $swSecEnetSwitch;
	$this->swSecEnetBlade = $swSecEnetBlade;
	$this->swSecEnetPort = $swSecEnetPort;
	$this->swSecEnetMedConvCh = $swSecEnetMedConvCh;
	$this->swSecMedConvPort = $swSecMedConvPort;
	$this->swConnection = $swConnection;
	$this->swLocation = $swLocation;
    $this->swID = $swID;


  }
  function getswPrimEnetSwitch() {
    return $this->swPrimEnetSwitch;
  }
  
  function getswPrimEnetBlade() {
    return $this->swPrimEnetBlade;
  }
  
  function getswPrimEnetPort() {
    return $this->swPrimEnetPort;
  }
  
  function getswPrimEnetMedConvCh() {
    return $this->swPrimEnetMedConvCh;
  }	
  
  function getswPrimMediaConvPort() {
    return $this->swPrimMediaConvPort;
  }
  
  function getswSecEnetSwitch() {
    return $this->swSecEnetSwitch;
  }
  
  function getswSecEnetBlade() {
    return $this->swSecEnetBlade;
  }
  
  function getswSecEnetPort() {
    return $this->swSecEnetPort;
  }
  
  function getswSecEnetMedConvCh() {
    return $this->swSecEnetMedConvCh;
  }
  
  function getswSecMedConvPort() {
    return $this->swSecMedConvPort;
  }
  
  function getswConnection() {
    return $this->swConnection;
  }
  
  function getswLocation() {
	  return $this->swLocation;
  }
  
  function getswID() {
    return $this->swID;
  }
    
}

/*
 * swList is a business object which contains a collection of information
 * representing the list of all current system's in the IRMIS database. It is
 * essentially a wrapper for an array of systemEntity, along with the standard
 * "loadFromDB()" function and other utility functions.
 */
class swDetailsList {
  // an array of swEntity
  var $swDetailsEntities;

  function swDetailsList() {
    $this->swDetailsEntities = array();
  }

  function getElement($idx) {
    return $this->swDetailsEntities[$idx];
  }

  // return size of array
  function length() {
    if ($this->swDetailsEntities == null)
      return 0;
    else 
      return count($this->swDetailsEntities);
  }
  
  /*
   * getArray() returns an array of swEntity (assuming loadFromDB has
   * been called already).
   */
  function getArray() {
    return $this->swDetailsEntities;
  }
    
  /*
   * Conducts MySQL db transactions to initialize systemList from the
   * aps_ioc table. Returns false if db error occurs, true
   * otherwise. */
  function loadFromDB($dbConn, $swID) {
    global $errno;
    global $error;
        
    $conn = $dbConn->getConnection();
    $tableNamePrefix = $dbConn->getTableNamePrefix();
        
    // Get core ioc info from aps_ioc table
    $qb = new DBQueryBuilder($tableNamePrefix);
    $qb->addColumn("PrimEnetSwitch");
	$qb->addColumn("PrimEnetBlade");
	$qb->addColumn("PrimEnetPort");
	$qb->addColumn("PrimEnetMedConvCh");
	$qb->addColumn("PrimMediaConvPort");
	$qb->addColumn("SecEnetSwitch");
	$qb->addColumn("SecEnetBlade");
	$qb->addColumn("SecEnetPort");
	$qb->addColumn("SecEnetMedConvCh");
	$qb->addColumn("SecMedConvPort");
	$qb->addColumn("ioc_nm");
	$qb->addColumn("location");
    $qb->addTable("aps_ioc left join ioc using (ioc_id)");
	$qb->addWhere("PrimEnetSwitch like '". $swID."' || SecEnetSwitch like '". $swID."'");
	$qb->addSuffix("order by PrimEnetBlade, SecEnetBlade, PrimEnetPort, SecEnetPort");
	$swDetailsQuery = $qb->getQueryString();
    logEntry('debug',$swDetailsQuery);

    if (!$swDetailsResult = mysql_query($swDetailsQuery, $conn)) {
      $errno = mysql_errno();
      $error = "swDetailsList.loadFromDB(): ".mysql_error();
      logEntry('critical',$error);
      return false;                 
    }

    $idx = 0;
    if ($swDetailsResult) {
      while ($swDetailsRow = mysql_fetch_array($swDetailsResult)) {
	    $swPrimEnetSwitch = $swDetailsRow['PrimEnetSwitch'];
		$swPrimEnetBlade = $swDetailsRow['PrimEnetBlade'];
		$swPrimEnetPort = $swDetailsRow['PrimEnetPort'];
		$swPrimEnetMedConvCh = $swDetailsRow['PrimEnetMedConvCh'];
		$swPrimMediaConvPort = $swDetailsRow['PrimMediaConvPort'];
		$swSecEnetSwitch = $swDetailsRow['SecEnetSwitch'];
		$swSecEnetBlade = $swDetailsRow['SecEnetBlade'];
		$swSecEnetPort = $swDetailsRow['SecEnetPort'];
		$swSecEnetMedConvCh = $swDetailsRow['SecEnetMedConvCh'];
		$swSecMedConvPort = $swDetailsRow['SecMedConvPort'];
		$swConnection = $swDetailsRow['ioc_nm'];
		$swLocation = $swDetailsRow['location'];
		
        $swDetailsEntity = new swDetailsEntity($swPrimEnetSwitch, $swPrimEnetBlade, $swPrimEnetPort, $swPrimEnetMedConvCh, $swPrimMediaConvPort,
		                                       $swSecEnetSwitch, $swSecEnetBlade, $swSecEnetPort, $swSecEnetMedConvCh, $swSecMedConvPort,
											   $swConnection, $swLocation); 
        $this->swDetailsEntities[$idx] = $swDetailsEntity;
        $idx = $idx +1;
      }
    }
    return true;
  }
}

?>
