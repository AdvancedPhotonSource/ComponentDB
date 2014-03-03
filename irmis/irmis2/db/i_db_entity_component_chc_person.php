<?php   
/*
 * Defines business class PersonList which contains an array of PersonEntity
 * 
 */

class chcPersonEntity {
  var $personId;
  var $first;
  var $last;

  function chcPersonEntity($personId,$first,$last) {
	$this->personId = $personId;
	$this->first = $first;
	$this->last = $last;
  }
  function getchcPersonId() {
    return $this->personId;
  }
  function getchcFirst() {
    return $this->first;
  }
  function getchcLast() {
    return $this->last;
  }
}

/*
 * Represents a list of people  (and some secondary info).
 */
class chcPersonList {
  // an array of PersonEntity
  var $chcPersonEntities;
  
  function chcPersonList() {
    $this->chcPersonEntities = array();
  }
  
  // length of full result set
  function length() {
    if ($this->chcPersonEntities == null)
      return 0;
    else 
      return count($this->chcPersonEntities);
  }
  
  // get a particular PersonEntity by sequential index
  function getElement($idx) {
    return $this->chcPersonEntities[$idx];
  }
  
  // get full result set as an array of PersonEntity
  function getArray() {
    return $this->chcPersonEntities;
  }
  
  /*
   * Gets all known persons for person pull-down.
   */
   function loadFromDB ($dbConn) {
     global $errno;
	 global $error;
	 
	 $conn = $dbConn->getConnection();
	 $tableNamePrefix = $dbConn->getTableNamePrefix();
	 
    $qb = new DBQueryBuilder($tableNamePrefix);
    $qb->addColumn("distinct person.first_nm");
    $qb->addColumn("person.last_nm");
    $qb->addColumn("person.userid");
    $qb->addColumn("person_id");
	$qb->addColumn("component_type.chc_contact_id");
    $qb->addTable("person");
	$qb->addTable("component_type");
	$qb->addWhere("component_type.chc_contact_id=person.person_id");
    $qb->addSuffix("order by last_nm");
	 
	 $chcPersonQuery = $qb->getQueryString();
	 // logEntry('debug',$personQuery);
	 
	 if (!$chcPersonResult = mysql_query($chcPersonQuery, $conn)) {
      $errno = mysql_errno();
      $error = "chcPersonList.loadFromDB(): ".mysql_error();
      logEntry('critical',$error);
      return false;                 
    }
	
	$idx = 0;
    if ($chcPersonResult) {
      while ($chcPersonRow = mysql_fetch_array($chcPersonResult)) {
        $chcPersonEntity = new chcPersonEntity($chcPersonRow['person_id'], $chcPersonRow['first_nm'], $chcPersonRow['last_nm']); 
        $this->chcPersonEntities[$idx] = $chcPersonEntity;
        $idx = $idx +1;
      }
    }
    return true;
  }
   
  function loadFromDBForchcPerson($dbConn, $ID) {
    global $errno;
    global $error;
    
    $conn = $dbConn->getConnection();
    $tableNamePrefix = $dbConn->getTableNamePrefix();
    
    // Begin building query for person table
    $qb = new DBQueryBuilder($tableNamePrefix);
	$qb->addColumn("person.person_id");
	$qb->addColumn("person.first_nm");
	$qb->addColumn("person.last_nm");
	$qb->addTable("component_type ct");
	$qb->addTable("person");
    $qb->addWhere("ct.component_type_id=".$ID);
	$qb->addWhere("ct.chc_contact_id=person.person_id");
    $chcPersonQuery = $qb->getQueryString();
    logEntry('debug',$chcPersonQuery);
    
    if (!$chcPersonResult = mysql_query($chcPersonQuery, $conn)) {
      $errno = mysql_errno();
      $error = "chcPersonList.loadFromDBForchcPerson(): ".mysql_error();
      logEntry('critical',$error);
      return false;                 
    }
    $idx = 0;
    if ($chcPersonResult) {
      while ($chcPersonRow = mysql_fetch_array($chcPersonResult)) {
        $chcPersonEntity = new chcPersonEntity($chcPersonRow['person_id'], $chcPersonRow['first_nm'], $chcPersonRow['last_nm']);
        $this->chcPersonEntities[$idx] = $chcPersonEntity;
        $idx = $idx +1;
      }
    }  
    
    return true;
  }
}
?>