<?php
/*
 * Defines business class machineList which contains an array of machineEntity.
 */

/*
 * machineEntity corresponds to a single row from the IRMIS machine table.
 */
class machineEntity {
  var $machine;
  var $machineID;
  var $machineDescription;

  function machineEntity($machine,$machineID,$machineDescription) {
    $this->machine = $machine;
    $this->machineID = $machineID;
    $this->machineDescription = $machineDescription;
  }

  function getMachine() {
    return $this->machine;
  }

  function getMachineID() {
  	return $this->machineID;
  }

  function getMachineDescription() {
  	return $this->machineDescription;
  }
}

/*
 * machineList is a business object which contains a collection of information
 * representing the list of all current machines in the IRMIS database. It is
 * essentially a wrapper for an array of machineEntity, along with the standard
 * "loadFromDB()" function and other utility functions.
 */

class machineList {
  // an array of machineEntity
  var $machineEntities;

  function machineList() {
    $this->machineEntities = array();
  }

  function getElement($idx) {
    return $this->machineEntities[$idx];
  }

  // return size of array
  function length() {
    if ($this->machineEntities == null)
      return 0;
    else
      return count($this->machineEntities);
  }

  /*
   * getArray() returns an array of machineEntity (assuming loadFromDB has
   * been called already).
   */
  function getArray() {
    return $this->machineEntities;
  }

  // Returns getElementForMachine equal to $machine

  function getElementForMachine($machine) {
       foreach ($this->machineEntities as $machineEntity) {
	  if ($machineEntity->getMachine() == $machine) {
	    return $machineEntity;
	  }
	 }
	 logEntry('debug',"Unable to find machineEntity for APS machine $machine");
	 return null;
  }


  /*
   * Conducts MySQL db transactions to initialize machineList from the
   * machine table. Returns false if db error occurs, true
   * otherwise. */

  function loadFromDB($dbConn) {
    global $errno;
    global $error;

    $conn = $dbConn->getConnection();
    $tableNamePrefix = $dbConn->getTableNamePrefix();

    // Get machine info from machine table
    $qb = new DBQueryBuilder($tableNamePrefix);
    $qb->addColumn("machine");
    $qb->addColumn("machine_id");
    $qb->addColumn("description");
    $qb->addTable("machine");
	$qb->addSuffix("order by machine");
    $machineQuery = $qb->getQueryString();
    logEntry('debug',$machineQuery);

    if (!$machineResult = mysql_query($machineQuery, $conn)) {
      $errno = mysql_errno();
      $error = "machineList.loadFromDB(): ".mysql_error();
      logEntry('critical',$error);
      return false;
    }

    $idx = 0;
    if ($machineResult) {
      while ($machineRow = mysql_fetch_array($machineResult)) {
        $machineEntity = new machineEntity($machineRow['machine'],$machineRow['machine_id'],$machineRow['description']);
        $this->machineEntities[$idx] = $machineEntity;
        $idx = $idx +1;
      }
    }
    return true;
  }
}

?>
