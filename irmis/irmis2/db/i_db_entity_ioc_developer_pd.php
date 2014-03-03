<?php
/*
 * Defines business class systemList which contains an array of systemEntity.
 */

/*
 * systemEntity corresponds to a single row from the IRMIS ioc table.
 */
class developerEntity {
  var $cog_developer_id;
  var $person_id;
  var $last_nm;
  var $first_nm;

  function developerEntity($cog_developer_id, $person_id, $last_nm, $first_nm) {
    $this->cog_developer_id = $cog_developer_id;
	$this->person_id = $person_id;
	$this->last_nm = $last_nm;
	$this->first_nm = $first_nm;
  }
  function getCog_developer_id() {
    return $this->cog_developer_id;
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
class developerList {
  // an array of developerEntity
  var $developerEntities;

  function developerList() {
    $this->developerEntities = array();
  }

  function getElement($idx) {
    return $this->developerEntities[$idx];
  }

  // return size of array
  function length() {
    if ($this->developerEntities == null)
      return 0;
    else
      return count($this->developerEntities);
  }

  /*
   * getArray() returns an array of systemEntity (assuming loadFromDB has
   * been called already).
   */
  function getArray() {
    return $this->developerEntities;
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
	$qb->addColumn("aps_ioc.cog_developer_id");
    $qb->addTable("person");
	$qb->addTable("aps_ioc");
	$qb->addWhere("person_id=cog_developer_id");
	$qb->addSuffix("order by last_nm");
    $developerQuery = $qb->getQueryString();
    logEntry('debug',$developerQuery);

    if (!$developerResult = mysql_query($developerQuery, $conn)) {
      $errno = mysql_errno();
      $error = "developerList.loadFromDB(): ".mysql_error();
      logEntry('critical',$error);
      return false;
    }

    $idx = 0;
    if ($developerResult) {
      while ($developerRow = mysql_fetch_array($developerResult)) {
        $developerEntity = new developerEntity($developerRow['cog_developer_id'], $developerRow['person_id'], $developerRow['last_nm'], $developerRow['first_nm']);
        $this->developerEntities[$idx] = $developerEntity;
        $idx = $idx +1;
      }
    }
    return true;
  }
}

?>