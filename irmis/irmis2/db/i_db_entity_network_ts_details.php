<?php   
/*
 * Defines business class systemList which contains an array of systemEntity.
 */

/*
 * tsDetailsEntity corresponds to a single row from the IRMIS aps_ioc table.
 */
class tsDetailsEntity {
  var $tsPort;
  var $tsFiberConvCh;
  var $tsFiberConvPort;
  var $tsConnection;
  var $tsID;
 
  function tsDetailsEntity($tsPort, $tsFiberConvCh, $tsFiberConvPort, $tsConnection, $tsID) {
    $this->tsPort = $tsPort;
	$this->tsFiberConvCh = $tsFiberConvCh;
	$this->tsFiberConvPort = $tsFiberConvPort;
	$this->tsConnection = $tsConnection;
	$this->tsID = $tsID;

  }
  function gettsPort() {
    return $this->tsPort;
  }
  
  function gettsFiberConvCh() {
    return $this->tsFiberConvCh;
  }
  
  function gettsFiberConvPort() {
    return $this->tsFiberConvPort;
  }
  
  function gettsConnection() {
    return $this->tsConnection;
  }	
  
  function gettsID() {
    return $this->tsID;
  }
}

/*
 * systemList is a business object which contains a collection of information
 * representing the list of all current system's in the IRMIS database. It is
 * essentially a wrapper for an array of systemEntity, along with the standard
 * "loadFromDB()" function and other utility functions.
 */
class tsDetailsList {
  // an array of systemEntity
  var $tsDetailsEntities;

  function tsDetailsList() {
    $this->tsDetailsEntities = array();
  }

  function getElement($idx) {
    return $this->tsDetailsEntities[$idx];
  }

  // return size of array
  function length() {
    if ($this->tsDetailsEntities == null)
      return 0;
    else 
      return count($this->tsDetailsEntities);
  }
  
  /*
   * getArray() returns an array of systemEntity (assuming loadFromDB has
   * been called already).
   */
  function getArray() {
    return $this->tsDetailsEntities;
  }
    
//  function getElementForTSID($tsID) {
//    foreach ($this->tsDetailsEntities as $tsDetailsEntity) {
//	  if ($tsDetailsEntity->gettsID() == $tsID) {
//	    return $tsDetailsEntity;
//	  }
//	 }
//	} 

  /*
   * Conducts MySQL db transactions to initialize systemList from the
   * ioc table. Returns false if db error occurs, true
   * otherwise. */
  function loadFromDB($dbConn, $tsID) {
    global $errno;
    global $error;
        
    $conn = $dbConn->getConnection();
    $tableNamePrefix = $dbConn->getTableNamePrefix();
        
    // Get core ioc info from ioc table
    $qb = new DBQueryBuilder($tableNamePrefix);
    $qb->addColumn("TermServPort");
	$qb->addColumn("TermServName");
	$qb->addColumn("TermServFiberConvCh");
	$qb->addColumn("TermServFiberConvPort");
	$qb->addColumn("ioc_nm");
    $qb->addTable("aps_ioc");
	$qb->addTable("ioc");
	$qb->addWhere("TermServName like '". $tsID."'");
	$qb->addWhere("aps_ioc.ioc_id=ioc.ioc_id");
	$qb->addSuffix("order by TermServPort, TermServFiberConvPort");
    $tsDetailsQuery = $qb->getQueryString();
    logEntry('debug',$tsDetailsQuery);

    if (!$tsDetailsResult = mysql_query($tsDetailsQuery, $conn)) {
      $errno = mysql_errno();
      $error = "tsDetailsList.loadFromDB(): ".mysql_error();
      logEntry('critical',$error);
      return false;                 
    }

    $idx = 0;
    if ($tsDetailsResult) {
      while ($tsDetailsRow = mysql_fetch_array($tsDetailsResult)) {
	    $tsPort = $tsDetailsRow['TermServPort'];
		$tsFiberConvCh = $tsDetailsRow['TermServFiberConvCh'];
		$tsFiberConvPort = $tsDetailsRow['TermServFiberConvPort'];
		$tsConnection = $tsDetailsRow['ioc_nm'];
		$tsID = $tsDetailsRow['TermServName'];
        $tsDetailsEntity = new tsDetailsEntity($tsPort, $tsFiberConvCh, $tsFiberConvPort, $tsConnection, $tsID); 
        $this->tsDetailsEntities[$idx] = $tsDetailsEntity;
        $idx = $idx +1;
      }
    }
    return true;
  }
}

?>
