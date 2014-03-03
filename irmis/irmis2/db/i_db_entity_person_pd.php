<?php
/*
 * Defines business class personList which contains an array of personEntity.
 */

/*
 * personEntity corresponds to a single row from the IRMIS person table.
 */
class personEntity {
  var $first_nm;
  var $last_nm;
  var $userid;
  var $person_id;

  function personEntity($first_nm,$last_nm,$userid, $person_id) {
    $this->first_nm = $first_nm;
	$this->last_nm = $last_nm;
	$this->userid = $userid;
	$this->person_id = $person_id;
  }

  function getFirst_nm() {
  	return $this->first_nm;
  }

  function getLast_nm() {
    return $this->last_nm;
  }

  function getUserID() {
  	return $this->userid;
  }

  function getPersonID() {
  	return $this->person_id;
  }
}


/*
 * personList is a business object which contains information
 * representing the list of all current person's in the IRMIS database. It is
 * essentially a wrapper for an array of personEntity, along with the standard
 * "loadFromDB()" function and other utility functions.
 */
class personList {
  // an array of personEntity
  var $personEntities;

  function personList() {
    $this->personEntities = array();
  }

  function getElement($idx) {
    return $this->personEntities[$idx];
  }

  // return size of array
  function length() {
    if ($this->personEntities == null)
      return 0;
    else
      return count($this->personEntities);
  }

  /*
   * getArray() returns an array of personEntity (assuming loadFromDB has
   * been called already).
   */
  function getArray() {
    return $this->personEntities;
  }


  // Returns getElementForPersonLastName equal to $last_nm

  function getElementForPersonLastName($last_name) {
    foreach ($this->personEntities as $personEntity) {
  	  if ($personEntity->getLast_nm() == $last_name) {
  	    return $personEntity;
  	  }
  	}
  	 logEntry('debug',"Unable to find personEntity for APS person $last_name");
  	 return null;
  }

  function getElementForPersonUserID($userID) {
    foreach ($this->personEntities as $personEntity) {
  	  if ($personEntity->getUserID() == $userID) {
  	    return $personEntity;
  	  }
  	}
  	 logEntry('debug',"Unable to find personEntity for APS person user ID $userID");
  	 return null;
  }


  /*
   * Conducts MySQL db transactions to initialize personList from the
   * person table. Returns false if db error occurs, true
   * otherwise. */
  function loadFromDB($dbConn) {
    global $errno;
    global $error;

    $conn = $dbConn->getConnection();
    $tableNamePrefix = $dbConn->getTableNamePrefix();

    // Get person last name info from person table
    $qb = new DBQueryBuilder($tableNamePrefix);
    $qb->addColumn("person.first_nm");
    $qb->addColumn("person.last_nm");
    $qb->addColumn("person.userid");
    $qb->addColumn("person_id");
    $qb->addTable("person");
    $qb->addSuffix("order by last_nm");
    $personQuery = $qb->getQueryString();
    logEntry('debug',$personQuery);

    if (!$personResult = mysql_query($personQuery, $conn)) {
      $errno = mysql_errno();
      $error = "personList.loadFromDB(): ".mysql_error();
      logEntry('critical',$error);
      return false;
    }

    $idx = 0;
    if ($personResult) {
      while ($personRow = mysql_fetch_array($personResult)) {
        $personEntity = new personEntity($personRow['first_nm'],$personRow['last_nm'],$personRow['userid'],$personRow['person_id']);
        $this->personEntities[$idx] = $personEntity;
        $idx = $idx +1;
      }
    }
    return true;
  }
}

?>
