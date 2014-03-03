<?php
/*
 * Defines business class systemList which contains an array of systemEntity.
 */

/*
 * systemEntity corresponds to a single row from the IRMIS ioc table.
 */
class systemEntity {
  var $system;

  function systemEntity($system) {
    $this->system = $system;

  }
  function getSystem() {
    return $this->system;
  }
}


/*
 * systemList is a business object which contains a collection of information
 * representing the list of all current system's in the IRMIS database. It is
 * essentially a wrapper for an array of systemEntity, along with the standard
 * "loadFromDB()" function and other utility functions.
 */
class systemList {
  // an array of systemEntity
  var $systemEntities;

  function systemList() {
    $this->systemEntities = array();
  }

  function getElement($idx) {
    return $this->systemEntities[$idx];
  }

  // return size of array
  function length() {
    if ($this->systemEntities == null)
      return 0;
    else
      return count($this->systemEntities);
  }

  /*
   * getArray() returns an array of systemEntity (assuming loadFromDB has
   * been called already).
   */
  function getArray() {
    return $this->systemEntities;
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
    $qb->addColumn("distinct system");
    $qb->addTable("ioc");
	$qb->addSuffix("order by system");
    $systemQuery = $qb->getQueryString();
    logEntry('debug',$systemQuery);

    if (!$systemResult = mysql_query($systemQuery, $conn)) {
      $errno = mysql_errno();
      $error = "systemList.loadFromDB(): ".mysql_error();
      logEntry('critical',$error);
      return false;
    }

    $idx = 0;
    if ($systemResult) {
      while ($systemRow = mysql_fetch_array($systemResult)) {
        $systemEntity = new systemEntity($systemRow['system']);
        $this->systemEntities[$idx] = $systemEntity;
        $idx = $idx +1;
      }
    }
    return true;
  }
}

?>