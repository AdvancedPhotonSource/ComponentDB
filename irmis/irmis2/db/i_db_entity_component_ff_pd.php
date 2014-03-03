<?php
/*
 * Defines business class systemList which contains an array of systemEntity.
 */

/*
 * systemEntity corresponds to a single row from the IRMIS ioc table.
 */
class ffEntity {
  var $ff;

  function ffEntity($ff) {
    $this->ff = $ff;

  }
  function getFf() {
    return $this->ff;
  }
}


/*
 * systemList is a business object which contains a collection of information
 * representing the list of all current system's in the IRMIS database. It is
 * essentially a wrapper for an array of systemEntity, along with the standard
 * "loadFromDB()" function and other utility functions.
 */
class ffList {
  // an array of systemEntity
  var $ffEntities;

  function ffList() {
    $this->ffEntities = array();
  }

  function getElement($idx) {
    return $this->ffEntities[$idx];
  }

  // return size of array
  function length() {
    if ($this->ffEntities == null)
      return 0;
    else
      return count($this->ffEntities);
  }

  /*
   * getArray() returns an array of systemEntity (assuming loadFromDB has
   * been called already).
   */
  function getArray() {
    return $this->ffEntities;
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
    $qb->addColumn("distinct form_factor");
    $qb->addTable("form_factor");
	$qb->addSuffix("order by form_factor");
    $ffQuery = $qb->getQueryString();
    logEntry('debug',$ffQuery);

    if (!$ffResult = mysql_query($ffQuery, $conn)) {
      $errno = mysql_errno();
      $error = "ffList.loadFromDB(): ".mysql_error();
      logEntry('critical',$error);
      return false;
    }

    $idx = 0;
    if ($ffResult) {
      while ($ffRow = mysql_fetch_array($ffResult)) {
        $ffEntity = new ffEntity($ffRow['form_factor']);
        $this->ffEntities[$idx] = $ffEntity;
        $idx = $idx +1;
      }
    }
    return true;
  }
}

?>