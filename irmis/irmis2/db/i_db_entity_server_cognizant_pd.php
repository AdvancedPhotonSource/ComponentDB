<?php
/*
 * Defines business class cognizantList which contains an array of cognizantEntity.
 */

/*
 *
 */
class cognizantEntity {
  var $cognizant_id;
  var $person_id;
  var $last_nm;

  function cognizantEntity($cognizant_id, $person_id, $last_nm) {
    $this->cognizant_id = $cognizant_id;
	$this->person_id = $person_id;
	$this->last_nm = $last_nm;
  }
  function getCognizant_id() {
    return $this->cognizant_id;
  }
  function getPerson_id() {
    return $this->person_id;
  }
  function getLast_nm() {
    return $this->last_nm;
  }
}


/*
 * cognizantList is a business object which contains a collection of information
 * representing the list of all current cognizants in the IRMIS database. It is
 * essentially a wrapper for an array of cognizantEntity, along with the standard
 * "loadFromDB()" function and other utility functions.
 */
class cognizantList {
  // an array of cognizantEntity
  var $cognizantEntities;

  function cognizantList() {
    $this->cognizantEntities = array();
  }

  function getElement($idx) {
    return $this->cognizantEntities[$idx];
  }

  // return size of array
  function length() {
    if ($this->cognizantEntities == null)
      return 0;
    else
      return count($this->cognizantEntities);
  }

  /*
   * getArray() returns an array of cognizantEntity (assuming loadFromDB has
   * been called already).
   */
  function getArray() {
    return $this->cognizantEntities;
  }


  /*
   * Conducts MySQL db transactions to initialize cognizantList from the
   * server table. Returns false if db error occurs, true
   * otherwise. */
  function loadFromDB($dbConn) {
    global $errno;
    global $error;

    $conn = $dbConn->getConnection();
    $tableNamePrefix = $dbConn->getTableNamePrefix();

    // Get core server info from server table
    $qb = new DBQueryBuilder($tableNamePrefix);
    $qb->addColumn("distinct person.last_nm");
	$qb->addColumn("person.person_id");
	$qb->addColumn("server.cognizant_id");
    $qb->addTable("person");
	$qb->addTable("server");
	$qb->addWhere("person_id=cognizant_id");
	$qb->addSuffix("order by last_nm");
    $cognizantQuery = $qb->getQueryString();
    logEntry('debug',$cognizantQuery);

    if (!$cognizantResult = mysql_query($cognizantQuery, $conn)) {
      $errno = mysql_errno();
      $error = "cognizantList.loadFromDB(): ".mysql_error();
      logEntry('critical',$error);
      return false;
    }

    $idx = 0;
    if ($cognizantResult) {
      while ($cognizantRow = mysql_fetch_array($cognizantResult)) {
        $cognizantEntity = new cognizantEntity($cognizantRow['cognizant_id'], $cognizantRow['person_id'], $cognizantRow['last_nm']);
        $this->cognizantEntities[$idx] = $cognizantEntity;
        $idx = $idx +1;
      }
    }
    return true;
  }
}

?>