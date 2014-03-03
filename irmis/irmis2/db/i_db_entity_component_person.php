<?php   
/*
 * Defines business class PersonList which contains an array of PersonEntity
 * 
 */

class PersonEntity {
  var $personId;
  var $first;
  var $last;

  function PersonEntity($personId,$first,$last) {
	$this->personId = $personId;
	$this->first = $first;
	$this->last = $last;
  }
  function getPersonId() {
    return $this->personId;
  }
  function getFirst() {
    return $this->first;
  }
  function getLast() {
    return $this->last;
  }
}

/*
 * Represents a list of people  (and some secondary info).
 */
class PersonList {
  // an array of PersonEntity
  var $personEntities;
  
  function PersonList() {
    $this->personEntities = array();
  }
  
  // length of full result set
  function length() {
    if ($this->personEntities == null)
      return 0;
    else 
      return count($this->personEntities);
  }
  
  // get a particular PersonEntity by sequential index
  function getElement($idx) {
    return $this->personEntities[$idx];
  }
  
  // get full result set as an array of PersonEntity
  function getArray() {
    return $this->personEntities;
  }
  
  /*
   * Gets all known persons for person pull-down.
   */
   function loadFromDB ($dbConn) {
     global $errno;
	 global $error;
	 
	 $conn = $dbConn->getConnection();
	 $tableNamePrefix = $dbConn->getTableNamePrefix();
	 
	 $qb = new DBQueryBuilder ($tableNamePrefix);
	 $qb->addColumn("distinct person_id");
	 $qb->addColumn("first_nm");
	 $qb->addColumn("last_nm");
	 $qb->addTable("person");
	 $qb->addSuffix("order by last_nm");
	 
	 $personQuery = $qb->getQueryString();
	 // logEntry('debug',$personQuery);
	 
	 if (!$personResult = mysql_query($personQuery, $conn)) {
      $errno = mysql_errno();
      $error = "PersonList.loadFromDB(): ".mysql_error();
      logEntry('critical',$error);
      return false;                 
    }
	
	$idx = 0;
    if ($personResult) {
      while ($personRow = mysql_fetch_array($personResult)) {
        $personEntity = new PersonEntity($personRow['person_id'], $personRow['first_nm'], $personRow['last_nm']); 
        $this->personEntities[$idx] = $personEntity;
        $idx = $idx +1;
      }
    }
    return true;
  }
   
  function loadFromDBForPerson($dbConn, $ID) {
    global $errno;
    global $error;
    
    $conn = $dbConn->getConnection();
    $tableNamePrefix = $dbConn->getTableNamePrefix();
    
    // Begin building query for person table
    $qb = new DBQueryBuilder($tableNamePrefix);
	$qb->addColumn("person.person_id");
	$qb->addColumn("person.first_nm");
	$qb->addColumn("person.last_nm");
	$qb->addTable("component_type_person ctp");
	$qb->addTable("person");
    $qb->addWhere("ctp.component_type_id=".$ID);
	$qb->addWhere("ctp.person_id=person.person_id");
	
    $personQuery = $qb->getQueryString();
    
    // logEntry('debug',$personQuery);
    
    if (!$personResult = mysql_query($personQuery, $conn)) {
      $errno = mysql_errno();
      $error = "PersonList.loadFromDBForPerson(): ".mysql_error();
      logEntry('critical',$error);
      return false;                 
    }
    $idx = 0;
    if ($personResult) {
      while ($personRow = mysql_fetch_array($personResult)) {
		$personId = $personRow['person_id'];
		$first = $personRow['first_nm'];
		$last = $personRow['last_nm'];
        $personEntity = new personEntity($personId, $first, $last);
        $this->personEntities[$idx] = $personEntity;
		$idx = $idx +1;
      }
    }  
    
    return true;
  }
}
?>