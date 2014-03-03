<?php
/*
 * Defines business class systemList which contains an array of systemEntity.
 */

/*
 * systemEntity corresponds to a single row from the IRMIS ioc table.
 */
class techEntity {
  var $cog_technician_id;
  var $person_id;
  var $last_nm;
  var $first_nm;

  function techEntity($cog_technician_id, $person_id, $last_nm, $first_nm) {
    $this->cog_technician_id = $cog_technician_id;
	$this->person_id = $person_id;
	$this->last_nm = $last_nm;
	$this->first_nm = $first_nm;
  }
  function getCog_technician_id() {
    return $this->cog_technician_id;
  }
  function getPerson_id() {
    return $this->person_id;
  }
  function getLast_nm() {
    return $this->last_nm;
  }
  function getFirst_nm() {
    return $this->first_nm;
  }
}


/*
 * systemList is a business object which contains a collection of information
 * representing the list of all current system's in the IRMIS database. It is
 * essentially a wrapper for an array of systemEntity, along with the standard
 * "loadFromDB()" function and other utility functions.
 */
class techList {
  // an array of developerEntity
  var $techEntities;

  function techList() {
    $this->techEntities = array();
  }

  function getElement($idx) {
    return $this->techEntities[$idx];
  }

  // return size of array
  function length() {
    if ($this->techEntities == null)
      return 0;
    else
      return count($this->techEntities);
  }

  /*
   * getArray() returns an array of systemEntity (assuming loadFromDB has
   * been called already).
   */
  function getArray() {
    return $this->techEntities;
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
    $qb->addColumn("distinct person.last_nm");
	$qb->addColumn("person.first_nm");
	$qb->addColumn("person.person_id");
	$qb->addColumn("aps_ioc.cog_technician_id");
    $qb->addTable("person");
	$qb->addTable("aps_ioc");
	$qb->addWhere("person_id=cog_technician_id");
	$qb->addSuffix("order by last_nm");
    $techQuery = $qb->getQueryString();
    logEntry('debug',$techQuery);

    if (!$techResult = mysql_query($techQuery, $conn)) {
      $errno = mysql_errno();
      $error = "techList.loadFromDB(): ".mysql_error();
      logEntry('critical',$error);
      return false;
    }

    $idx = 0;
    if ($techResult) {
      while ($techRow = mysql_fetch_array($techResult)) {
        $techEntity = new techEntity($techRow['cog_technician_id'], $techRow['person_id'], $techRow['last_nm'], $techRow['first_nm']);
        $this->techEntities[$idx] = $techEntity;
        $idx = $idx +1;
      }
    }
    return true;
  }
}

?>