<?php
/*
 * Defines business class operating_systemList which contains an array of operating_systemEntity.
 */

/*
 * operating_systemEntity corresponds to a single row from the IRMIS server table.
 */
class operating_systemEntity {
  var $operating_system;

  function operating_systemEntity($operating_system) {
    $this->operating_system = $operating_system;

  }
  function getOperating_system() {
    return $this->operating_system;
  }
}


/*
 * operating_systemList is a business object which contains a collection of information
 * representing the list of all current operating systems in the IRMIS database. It is
 * essentially a wrapper for an array of operating_systemEntity, along with the standard
 * "loadFromDB()" function and other utility functions.
 */
class operating_systemList {
  // an array of operating_systemEntity
  var $operating_systemEntities;

  function operating_systemList() {
    $this->operating_systemEntities = array();
  }

  function getElement($idx) {
    return $this->operating_systemEntities[$idx];
  }

  // return size of array
  function length() {
    if ($this->operating_systemEntities == null)
      return 0;
    else
      return count($this->operating_systemEntities);
  }

  /*
   * getArray() returns an array of operating_systemEntity (assuming loadFromDB has
   * been called already).
   */
  function getArray() {
    return $this->operating_systemEntities;
  }


  /*
   * Conducts MySQL db transactions to initialize operating_systemList from the
   * server table. Returns false if db error occurs, true
   * otherwise. */
  function loadFromDB($dbConn) {
    global $errno;
    global $error;

    $conn = $dbConn->getConnection();
    $tableNamePrefix = $dbConn->getTableNamePrefix();

    // Get core server info from server table
    $qb = new DBQueryBuilder($tableNamePrefix);
    $qb->addColumn("distinct operating_system");
    $qb->addTable("server");
	$qb->addSuffix("order by operating_system");
    $operating_systemQuery = $qb->getQueryString();
    logEntry('debug',$operating_systemQuery);

    if (!$operating_systemResult = mysql_query($operating_systemQuery, $conn)) {
      $errno = mysql_errno();
      $error = "operating_systemList.loadFromDB(): ".mysql_error();
      logEntry('critical',$error);
      return false;
    }

    $idx = 0;
    if ($operating_systemResult) {
      while ($operating_systemRow = mysql_fetch_array($operating_systemResult)) {
        $operating_systemEntity = new operating_systemEntity($operating_systemRow['operating_system']);
        $this->operating_systemEntities[$idx] = $operating_systemEntity;
        $idx = $idx +1;
      }
    }
    return true;
  }
}

?>