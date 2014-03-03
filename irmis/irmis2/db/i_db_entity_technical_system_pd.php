<?php
/*
 * Defines business class techsystemList which contains an array of techsystemEntity.
 */

/*
 * techsystemEntity corresponds to a single row from the IRMIS technical_system table.
 */
class techsystemEntity {
  var $techsystem;
  var $technical_system_id;
  var $description;

  function techsystemEntity($techsystem,$technical_system_id,$description) {
    $this->techsystem = $techsystem;
    $this->technical_system_id = $technical_system_id;
    $this->description = $description;

  }
  function getTechSystem() {
    return $this->techsystem;
  }

  function getTechnicalSystemID() {
  	return $this->technical_system_id;
  }

  function getDescription() {
  	return $this->description;
  }
}


/*
 * techsystemList is a business object which contains a collection of information
 * representing the list of all current technical systems in the IRMIS database. It is
 * essentially a wrapper for an array of techsystemEntity, along with the standard
 * "loadFromDB()" function and other utility functions.
 */
class techsystemList {
  // an array of techsystemEntity
  var $techsystemEntities;

  function techsystemList() {
    $this->techsystemEntities = array();
  }

  function getElement($idx) {
    return $this->techsystemEntities[$idx];
  }

  // return size of array
  function length() {
    if ($this->techsystemEntities == null)
      return 0;
    else
      return count($this->techsystemEntities);
  }

  /*
   * getArray() returns an array of techsystemEntity (assuming loadFromDB has
   * been called already).
   */
  function getArray() {
    return $this->techsystemEntities;
  }

  // Returns getElementForTechsystem equal to $techsystem

  function getElementForTechsystem($techsystem) {
       foreach ($this->techsystemEntities as $techsystemEntity) {
	  if ($techsystemEntity->getTechSystem() == $techsystem) {
	    return $techsystemEntity;
	  }
	 }
	 logEntry('debug',"Unable to find techsystemEntity for technical system $techsystem");
	 return null;
  }

  /*
   * Conducts MySQL db transactions to initialize techsystemList from the
   * technical_system table. Returns false if db error occurs, true
   * otherwise. */

  function loadFromDB($dbConn) {
    global $errno;
    global $error;

    $conn = $dbConn->getConnection();
    $tableNamePrefix = $dbConn->getTableNamePrefix();

    // Get technical system info from technical_system table
    $qb = new DBQueryBuilder($tableNamePrefix);
    $qb->addColumn("technical_system");
    $qb->addColumn("technical_system_id");
    $qb->addColumn("description");
    $qb->addTable("technical_system");
	$qb->addSuffix("order by technical_system");
    $techsystemQuery = $qb->getQueryString();
    logEntry('debug',$techsystemQuery);

    if (!$techsystemResult = mysql_query($techsystemQuery, $conn)) {
      $errno = mysql_errno();
      $error = "techsystemList.loadFromDB(): ".mysql_error();
      logEntry('critical',$error);
      return false;
    }

    $idx = 0;
    if ($techsystemResult) {
      while ($techsystemRow = mysql_fetch_array($techsystemResult)) {
        $techsystemEntity = new techsystemEntity($techsystemRow['technical_system'],$techsystemRow['technical_system_id'],$techsystemRow['description']);
        $this->techsystemEntities[$idx] = $techsystemEntity;
        $idx = $idx +1;
      }
    }
    return true;
  }
}

?>
