<?php   
/*
 * Defines business class systemList which contains an array of systemEntity.
 */

/*
 * systemEntity corresponds to a single row from the IRMIS ioc table.
 */
class tsEntity {
  var $ts;
  var $tsLocation;
 
  function tsEntity($ts, $tsLocation) {
    $this->ts = $ts;
	$this->tsLocation = $tsLocation;

  }
  function getts() {
    return $this->ts;
  }
  
  function gettsLocation() {
    return $this->tsLocation;
  }
}

/*
 * systemList is a business object which contains a collection of information
 * representing the list of all current system's in the IRMIS database. It is
 * essentially a wrapper for an array of systemEntity, along with the standard
 * "loadFromDB()" function and other utility functions.
 */
class tsList {
  // an array of systemEntity
  var $tsEntities;

  function tsList() {
    $this->tsEntities = array();
  }

  function getElement($idx) {
    return $this->tsEntities[$idx];
  }

  // return size of array
  function length() {
    if ($this->tsEntities == null)
      return 0;
    else 
      return count($this->tsEntities);
  }
  
  /*
   * getArray() returns an array of systemEntity (assuming loadFromDB has
   * been called already).
   */
  function getArray() {
    return $this->tsEntities;
  }
    

  /*
   * Conducts MySQL db transactions to initialize systemList from the
   * ioc table. Returns false if db error occurs, true
   * otherwise. */
  function loadFromDB($dbConn) {
    global $errno;
    global $error;
        
    $conn = $dbConn->getConnection();
    $tableNamePrefix = $dbConn->getTableNamePrefix();
        
    // Get core ioc info from ioc table
    $qb = new DBQueryBuilder($tableNamePrefix);
    $qb->addColumn("distinct TermServName");
	$qb->addColumn("TermServRackNo");
    $qb->addTable("aps_ioc");
	$qb->addWhere("TermServName like '%ts%'");
	$qb->addSuffix("order by TermServName");
    $tsQuery = $qb->getQueryString();
    logEntry('debug',$tsQuery);

    if (!$tsResult = mysql_query($tsQuery, $conn)) {
      $errno = mysql_errno();
      $error = "tsList.loadFromDB(): ".mysql_error();
      logEntry('critical',$error);
      return false;                 
    }

    $idx = 0;
    if ($tsResult) {
      while ($tsRow = mysql_fetch_array($tsResult)) {
	    $tsName = $tsRow['TermServName'];
		$tsLocation = $tsRow['TermServRackNo'];
        $tsEntity = new tsEntity($tsName, $tsLocation); 
        $this->tsEntities[$idx] = $tsEntity;
        $idx = $idx +1;
      }
    }
    return true;
  }
}

?>
