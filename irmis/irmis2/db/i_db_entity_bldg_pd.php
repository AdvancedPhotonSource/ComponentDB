<?php   
/*
 * Defines business class buildingList which contains an array of buildingEntity.
 */

/*
 * buildingEntity corresponds to a single row from the IRMIS component_rel table.
 */
class buildingEntity {
  var $building;
 
  function buildingEntity($building) {
    $this->building = $building;

  }
  function getBuilding() {
    return $this->building;
  }
}

/*
 * buildingList is a business object which contains a collection of information
 * representing the list of all current buildings in the IRMIS database. It is
 * essentially a wrapper for an array of buildingEntity, along with the standard
 * "loadFromDB()" function and other utility functions.
 */
class buildingList {
  // an array of buildingEntity
  var $buildingEntities;

  function buildingList() {
    $this->buildingEntities = array();
  }

  function getElement($idx) {
    return $this->buildingEntities[$idx];
  }

  // return size of array
  function length() {
    if ($this->buildingEntities == null)
      return 0;
    else 
      return count($this->buildingEntities);
  }
  
  /*
   * getArray() returns an array of buildingEntity (assuming loadFromDB has
   * been called already).
   */
  function getArray() {
    return $this->buildingEntities;
  }
    

  /*
   * Conducts MySQL db transactions to initialize buildingList from the
   * machine table. Returns false if db error occurs, true
   * otherwise. */

  function loadFromDB($dbConn) {
    global $errno;
    global $error;
        
    $conn = $dbConn->getConnection();
    $tableNamePrefix = $dbConn->getTableNamePrefix();
        
    // Get building info from component_rel table
    $qb = new DBQueryBuilder($tableNamePrefix);
    $qb->addColumn("logical_desc");
    $qb->addTable("component_rel");
	$qb->addWhere("parent_component_id ='2'");
	$qb->addWhere("mark_for_delete = '0'");
	$qb->addSuffix("order by logical_desc");
    $buildingQuery = $qb->getQueryString();
    logEntry('debug',$buildingQuery);

    if (!$buildingResult = mysql_query($buildingQuery, $conn)) {
      $errno = mysql_errno();
      $error = "buildingList.loadFromDB(): ".mysql_error();
      logEntry('critical',$error);
      return false;                 
    }

    $idx = 0;
    if ($buildingResult) {
      while ($buildingRow = mysql_fetch_array($buildingResult)) {
        $buildingEntity = new buildingEntity($buildingRow['logical_desc']); 
        $this->buildingEntities[$idx] = $buildingEntity;
        $idx = $idx +1;
      }
    }
    return true;
  }
}

?>
