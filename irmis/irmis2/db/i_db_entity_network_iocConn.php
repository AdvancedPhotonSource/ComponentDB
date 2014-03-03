<?php   
/*
 * Defines business class iocConnectionsList which contains an array of iocConnectionsEntity.
 */
/*
 * iocConnectionsEntity corresponds to a single row from the IRMIS aps_ioc table.
 */
class iocConnectionsEntity {
  var $iocConniocName;
  var $iocConnTermServName;
  var $iocConnTermServPort;
  var $iocConnPrimEnetSwitch;
  var $iocConnPrimEnetBlade;
  var $iocConnPrimEnetPort;
  var $iocConnSecEnetSwitch;
  var $iocConnSecEnetBlade;
  var $iocConnSecEnetPort;
  var $iocConnStatus;
 
  function iocConnectionsEntity($iocConniocName, $iocConnTermServName, $iocConnTermServPort, $iocConnPrimEnetSwitch, $iocConnPrimEnetBlade,
                                $iocConnPrimEnetPort, $iocConnSecEnetSwitch, $iocConnSecEnetBlade, $iocConnSecEnetPort, $iocConnStatus) {
    $this->iocConniocName = $iocConniocName;
	$this->iocConnTermServName = $iocConnTermServName;
	$this->iocConnTermServPort = $iocConnTermServPort;
	$this->iocConnPrimEnetSwitch = $iocConnPrimEnetSwitch;
	$this->iocConnPrimEnetBlade = $iocConnPrimEnetBlade;
	$this->iocConnPrimEnetPort = $iocConnPrimEnetPort;
	$this->iocConnSecEnetSwitch = $iocConnSecEnetSwitch;
	$this->iocConnSecEnetBlade = $iocConnSecEnetBlade;
	$this->iocConnSecEnetPort = $iocConnSecEnetPort;
	$this->iocConnStatus = $iocConnStatus;


  }
  function getiocConniocName() {
    return $this->iocConniocName;
  }
  
  function getiocConnTermServName() {
    return $this->iocConnTermServName;
  }
  
  function getiocConnTermServPort() {
    return $this->iocConnTermServPort;
  }
  
  function getiocConnPrimEnetSwitch() {
    return $this->iocConnPrimEnetSwitch;
  }	
  
  function getiocConnPrimEnetBlade() {
    return $this->iocConnPrimEnetBlade;
  }
  
  function getiocConnPrimEnetPort() {
    return $this->iocConnPrimEnetPort;
  }
  
  function getiocConnSecEnetSwitch() {
    return $this->iocConnSecEnetSwitch;
  }
  
  function getiocConnSecEnetBlade() {
    return $this->iocConnSecEnetBlade;
  }
  
  function getiocConnSecEnetPort() {
    return $this->iocConnSecEnetPort;
  }
  
  function getiocConnStatus() {
    return $this->iocConnStatus;
  }
    
}

/*
 * swList is a business object which contains a collection of information
 * representing the list of all current ioc's's in the IRMIS database. It is
 * essentially a wrapper for an array of iocConnectionsEntity, along with the standard
 * "loadFromDB()" function and other utility functions.
 */
class iocConnectionsList {
  // an array of iocConnectionsEntity
  var $iocConnectionsEntities;

  function iocConnectionsList() {
    $this->iocConnectionsEntities = array();
  }

  function getElement($idx) {
    return $this->iocConnectionsEntities[$idx];
  }

  // return size of array
  function length() {
    if ($this->iocConnectionsEntities == null)
      return 0;
    else 
      return count($this->iocConnectionsEntities);
  }
  
  /*
   * getArray() returns an array of iocConnectionsEntity (assuming loadFromDB has
   * been called already).
   */
  function getArray() {
    return $this->iocConnectionsEntities;
  }
    
  /*
   * Conducts MySQL db transactions to initialize systemList from the
   * aps_ioc table. Returns false if db error occurs, true
   * otherwise. */
  function loadFromDB($dbConn) {
    global $errno;
    global $error;
        
    $conn = $dbConn->getConnection();
    $tableNamePrefix = $dbConn->getTableNamePrefix();
        
    // Get core ioc info from aps_ioc table
    $qb = new DBQueryBuilder($tableNamePrefix);
    $qb->addColumn("ioc_nm");
	$qb->addColumn("TermServName");
	$qb->addColumn("TermServPort");
	$qb->addColumn("PrimEnetSwitch");
	$qb->addColumn("PrimEnetBlade");
	$qb->addColumn("PrimEnetPort");
	$qb->addColumn("SecEnetSwitch");
	$qb->addColumn("SecEnetBlade");
	$qb->addColumn("SecEnetPort");
	$qb->addColumn("ioc_status.ioc_status_id");
	//$qb->addColumn("active");
    $qb->addTable("aps_ioc left join ioc using (ioc_id)");
	$qb->addTable("ioc_status");
	$qb->addWhere("ioc.ioc_status_id = ioc_status.ioc_status_id");
	$qb->addSuffix("order by ioc_nm");
	$iocConnectionsQuery = $qb->getQueryString();
    logEntry('debug',$iocConnectionsQuery);

    if (!$iocConnectionsResult = mysql_query($iocConnectionsQuery, $conn)) {
      $errno = mysql_errno();
      $error = "iocConnectionsList.loadFromDB(): ".mysql_error();
      logEntry('critical',$error);
      return false;                 
    }

    $idx = 0;
    if ($iocConnectionsResult) {
      while ($iocConnectionsRow = mysql_fetch_array($iocConnectionsResult)) {
	    $iocConniocName = $iocConnectionsRow['ioc_nm'];
		$iocConnTermServName = $iocConnectionsRow['TermServName'];
		$iocConnTermServPort = $iocConnectionsRow['TermServPort'];
		$iocConnPrimEnetSwitch = $iocConnectionsRow['PrimEnetSwitch'];
		$iocConnPrimEnetBlade = $iocConnectionsRow['PrimEnetBlade'];
		$iocConnPrimEnetPort = $iocConnectionsRow['PrimEnetPort'];
		$iocConnSecEnetSwitch = $iocConnectionsRow['SecEnetSwitch'];
		$iocConnSecEnetBlade = $iocConnectionsRow['SecEnetBlade'];
		$iocConnSecEnetPort = $iocConnectionsRow['SecEnetPort'];
		$iocConnStatus = $iocConnectionsRow['ioc_status_id'];
		
        $iocConnectionsEntity = new iocConnectionsEntity($iocConniocName, $iocConnTermServName, $iocConnTermServPort, $iocConnPrimEnetSwitch, $iocConnPrimEnetBlade,
		                                       $iocConnPrimEnetPort, $iocConnSecEnetSwitch, $iocConnSecEnetBlade, $iocConnSecEnetPort, $iocConnStatus); 
        $this->iocConnectionsEntities[$idx] = $iocConnectionsEntity;
        $idx = $idx +1;
      }
    }
    return true;
  }
}

?>
