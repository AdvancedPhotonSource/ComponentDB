<?php
/*
 * Defines business class iocnameList which contains an array of iocnameEntity.
 */

/*
 * iocnameEntity corresponds to a single row from the IRMIS ioc table.
 */
class iocnameEntity {
  var $iocname;

  function iocnameEntity($iocname) {
    $this->iocname = $iocname;

  }
  function getIocName() {
    return $this->iocname;
  }
}

/*
 * iocnameList is a business object which contains information
 * representing the list of all iocs in the IRMIS database. It is
 * essentially a wrapper for an array of iocnameEntity, along with the standard
 * "loadFromDB()" function and other utility functions.
 */
class iocnameList {
  // an array of iocnameEntity
  var $iocnameEntities;

  function iocnameList() {
    $this->iocnameEntities = array();
  }

  function getElement($idx) {
    return $this->iocnameEntities[$idx];
  }

  // return size of array
  function length() {
    if ($this->iocnameEntities == null)
      return 0;
    else
      return count($this->iocnameEntities);
  }

  /*
   * getArray() returns an array of iocnameEntity (assuming loadFromDB has
   * been called already).
   */
  function getArray() {
    return $this->iocnameEntities;
  }


  /*
   * Conducts MySQL db transactions to initialize iocnameList from the
   * ioc table. Returns false if db error occurs, true
   * otherwise. */

  function loadFromDB($dbConn) {
    global $errno;
    global $error;

    $conn = $dbConn->getConnection();
    $tableNamePrefix = $dbConn->getTableNamePrefix();

    // Get ioc name from ioc table
    $qb = new DBQueryBuilder($tableNamePrefix);
    $qb->addColumn("ioc_nm");
    $qb->addTable("ioc");
	$qb->addSuffix("order by ioc_nm");
    $iocQuery = $qb->getQueryString();
    logEntry('debug',$iocQuery);

    if (!$iocResult = mysql_query($iocQuery, $conn)) {
      $errno = mysql_errno();
      $error = "iocnameList.loadFromDB(): ".mysql_error();
      logEntry('critical',$error);
      return false;
    }

    $idx = 0;
    if ($iocResult) {
      while ($iocRow = mysql_fetch_array($iocResult)) {
        $iocnameEntity = new iocnameEntity($iocRow['ioc_nm']);
        $this->iocnameEntities[$idx] = $iocnameEntity;
        $idx = $idx +1;
      }
    }
    return true;
  }
}

?>
