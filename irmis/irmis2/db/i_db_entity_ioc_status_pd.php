<?php
/*
 * Defines business class statusList which contains an array of statusEntity.
 */

/*
 * statusEntity corresponds to a single row from the IRMIS ioc table.
 */
class statusEntity {
  var $status;

  function statusEntity($status) {
    $this->status = $status;

  }
  function getStatus() {
    return $this->status;
  }
}


/*
 * statusList is a business object which contains a collection of information
 * representing the list of all current status's in the IRMIS database. It is
 * essentially a wrapper for an array of statusEntity, along with the standard
 * "loadFromDB()" function and other utility functions.
 */
class statusList {
  // an array of statusEntity
  var $statusEntities;

  function statusList() {
    $this->statusEntities = array();
  }

  function getElement($idx) {
    return $this->statusEntities[$idx];
  }

  // return size of array
  function length() {
    if ($this->statusEntities == null)
      return 0;
    else
      return count($this->statusEntities);
  }

  /*
   * getArray() returns an array of statusEntity (assuming loadFromDB has
   * been called already).
   */
  function getArray() {
    return $this->statusEntities;
  }


  /*
   * Conducts MySQL db transactions to initialize statusList from the
   * ioc table. Returns false if db error occurs, true
   * otherwise. */
  function loadFromDB($dbConn) {
    global $errno;
    global $error;

    $conn = $dbConn->getConnection();
    $tableNamePrefix = $dbConn->getTableNamePrefix();

    // Get core ioc info from ioc table
    $qb = new DBQueryBuilder($tableNamePrefix);
    $qb->addColumn("*");
	$qb->addTable("ioc_status");
	$qb->addSuffix("order by ioc_status_id");
    $statusQuery = $qb->getQueryString();
    logEntry('debug',$statusQuery);

    if (!$statusResult = mysql_query($statusQuery, $conn)) {
      $errno = mysql_errno();
      $error = "statusList.loadFromDB(): ".mysql_error();
      logEntry('critical',$error);
      return false;
    }

    $idx = 0;
    if ($statusResult) {
      while ($statusRow = mysql_fetch_array($statusResult)) {
        $statusEntity = new statusEntity($statusRow['ioc_status']);
        $this->statusEntities[$idx] = $statusEntity;
        $idx = $idx +1;
      }
    }
    return true;
  }
}

?>