<?php
/*
 * Defines business class servernameList which contains an array of servernameEntity.
 */

/*
 * servernameEntity corresponds to a single row from the IRMIS server table.
 */
class servernameEntity {
  var $servername;

  function servernameEntity($servername) {
    $this->servername = $servername;

  }
  function getServerName() {
    return $this->servername;
  }
}

/*
 * servernameList is a business object which contains information
 * representing the list of all servers in the IRMIS database. It is
 * essentially a wrapper for an array of servernameEntity, along with the standard
 * "loadFromDB()" function and other utility functions.
 */
class servernameList {
  // an array of servernameEntity
  var $servernameEntities;

  function servernameList() {
    $this->servernameEntities = array();
  }

  function getElement($idx) {
    return $this->servernameEntities[$idx];
  }

  // return size of array
  function length() {
    if ($this->servernameEntities == null)
      return 0;
    else
      return count($this->servernameEntities);
  }

  /*
   * getArray() returns an array of servernameEntity (assuming loadFromDB has
   * been called already).
   */
  function getArray() {
    return $this->servernameEntities;
  }


  /*
   * Conducts MySQL db transactions to initialize servernameList from the
   * server table. Returns false if db error occurs, true
   * otherwise. */

  function loadFromDB($dbConn) {
    global $errno;
    global $error;

    $conn = $dbConn->getConnection();
    $tableNamePrefix = $dbConn->getTableNamePrefix();

    // Get server name from server table
    $qb = new DBQueryBuilder($tableNamePrefix);
    $qb->addColumn("server_nm");
    $qb->addTable("server");
	$qb->addSuffix("order by server_nm");
    $serverQuery = $qb->getQueryString();
    logEntry('debug',$serverQuery);

    if (!$serverResult = mysql_query($serverQuery, $conn)) {
      $errno = mysql_errno();
      $error = "servernameList.loadFromDB(): ".mysql_error();
      logEntry('critical',$error);
      return false;
    }

    $idx = 0;
    if ($serverResult) {
      while ($serverRow = mysql_fetch_array($serverResult)) {
        $servernameEntity = new servernameEntity($serverRow['server_nm']);
        $this->servernameEntities[$idx] = $servernameEntity;
        $idx = $idx +1;
      }
    }
    return true;
  }
}

?>
