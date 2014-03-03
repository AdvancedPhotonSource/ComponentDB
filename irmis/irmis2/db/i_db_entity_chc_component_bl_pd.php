<?php
/*
 * Defines business class systemList which contains an array of systemEntity.
 */

/*
 * systemEntity corresponds to a single row from the IRMIS ioc table.
 */
class blEntity {
  var $bl;
  var $blid;

  function blEntity($bl) {
    $this->bl = $bl;
	$this->blid = $blid;

  }
  function getbl() {
    return $this->bl;
  }
  function getblid() {
    return $this->blid;
  }
  
}


/*
 * systemList is a business object which contains a collection of information
 * representing the list of all current system's in the IRMIS database. It is
 * essentially a wrapper for an array of systemEntity, along with the standard
 * "loadFromDB()" function and other utility functions.
 */
class blList {
  // an array of systemEntity
  var $blEntities;

  function blList() {
    $this->blEntities = array();
  }

  function getElement($idx) {
    return $this->blEntities[$idx];
  }

  // return size of array
  function length() {
    if ($this->blEntities == null)
      return 0;
    else
      return count($this->blEntities);
  }

  /*
   * getArray() returns an array of systemEntity (assuming loadFromDB has
   * been called already).
   */
  function getArray() {
    return $this->blEntities;
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
	$qb->addColumn("interest");
	$qb->addColumn("chc_beamline_interest_id");
    $qb->addTable("chc_beamline_interest");
	$qb->addSuffix("order by interest");
    $blQuery = $qb->getQueryString();
    logEntry('debug',$blQuery);

    if (!$blResult = mysql_query($blQuery, $conn)) {
      $errno = mysql_errno();
      $error = "blList.loadFromDB(): ".mysql_error();
      logEntry('critical',$error);
      return false;
    }

    $idx = 0;
    if ($blResult) {
      while ($blRow = mysql_fetch_array($blResult)) {
        $blEntity = new blEntity($blRow['interest'], $blRow['chc_beamline_interest_id']);
        $this->blEntities[$idx] = $blEntity;
        $idx = $idx +1;
      }
    }
    return true;
  }
}

?>