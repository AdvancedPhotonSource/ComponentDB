<?php
/*
 * Defines business class systemList which contains an array of systemEntity.
 */

/*
 * systemEntity corresponds to a single row from the IRMIS ioc table.
 */
class mfgEntity {
  var $mfg;

  function mfgEntity($mfg) {
    $this->mfg = $mfg;

  }
  function getMfg() {
    return $this->mfg;
  }
}


/*
 * systemList is a business object which contains a collection of information
 * representing the list of all current system's in the IRMIS database. It is
 * essentially a wrapper for an array of systemEntity, along with the standard
 * "loadFromDB()" function and other utility functions.
 */
class mfgList {
  // an array of systemEntity
  var $mfgEntities;

  function mfgList() {
    $this->mfgEntities = array();
  }

  function getElement($idx) {
    return $this->mfgEntities[$idx];
  }

  // return size of array
  function length() {
    if ($this->mfgEntities == null)
      return 0;
    else
      return count($this->mfgEntities);
  }

  /*
   * getArray() returns an array of systemEntity (assuming loadFromDB has
   * been called already).
   */
  function getArray() {
    return $this->mfgEntities;
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
    $qb->addColumn("distinct mfg_name");
    $qb->addTable("mfg");
	$qb->addSuffix("order by mfg_name");
    $mfgQuery = $qb->getQueryString();
    logEntry('debug',$mfgQuery);

    if (!$mfgResult = mysql_query($mfgQuery, $conn)) {
      $errno = mysql_errno();
      $error = "mfgList.loadFromDB(): ".mysql_error();
      logEntry('critical',$error);
      return false;
    }

    $idx = 0;
    if ($mfgResult) {
      while ($mfgRow = mysql_fetch_array($mfgResult)) {
        $mfgEntity = new mfgEntity($mfgRow['mfg_name']);
        $this->mfgEntities[$idx] = $mfgEntity;
        $idx = $idx +1;
      }
    }
    return true;
  }
}

?>