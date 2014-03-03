<?php   
/*
 * Defines business class buildingList which contains an array of buildingEntity.
 */

/*
 * buildingEntity corresponds to a single row from the IRMIS component_rel table.
 */
class roomEntity {
  var $room;
  var $parent_id;
 
  function roomEntity($room, $parent_id) {
    $this->room = $room;
	$this->parent_id = $parent_id;

  }
  
  function getRoom() {
    return $this->room;
  }
  
  function getParent_id() {
    return $this->parent_id;
  }
}

/*
 * buildingList is a business object which contains a collection of information
 * representing the list of all current buildings in the IRMIS database. It is
 * essentially a wrapper for an array of buildingEntity, along with the standard
 * "loadFromDB()" function and other utility functions.
 */
class roomList {
  // an array of roomEntity
  var $roomEntities;

  function roomList() {
    $this->roomEntities = array();
  }

  function getElement($idx) {
    return $this->roomEntities[$idx];
  }

  // return size of array
  function length() {
    if ($this->roomEntities == null)
      return 0;
    else 
      return count($this->roomEntities);
  }
  
  /*
   * getArray() returns an array of roomEntity (assuming loadFromDB has
   * been called already).
   */
  function getArray() {
    return $this->roomEntities;
  }
    

  /*
   * Conducts MySQL db transactions to initialize roomList from the
   * machine table. Returns false if db error occurs, true
   * otherwise. */

  function loadFromDB($dbConn) {
    global $errno;
    global $error;
        
    $conn = $dbConn->getConnection();
    $tableNamePrefix = $dbConn->getTableNamePrefix();
        
    // Get building info from component_rel table
    $qb = new DBQueryBuilder($tableNamePrefix);
    $qb->addColumn("component.component_instance_name");
	$qb->addColumn("component.component_id");
    $qb->addTable("component");
	$qb->addTable("component_type");
	$qb->addWhere("component.component_type_id = component_type.component_type_id");
	$qb->addWhere("component_type.component_type_id = '9'");
	$qb->addWhere("component.mark_for_delete = '0'");
	$qb->addSuffix("order by component.component_instance_name");
    $roomQuery = $qb->getQueryString();
    //logEntry('debug',$roomQuery);
	

    if (!$roomResult = mysql_query($roomQuery, $conn)) {
      $errno = mysql_errno();
      $error = "roomList.loadFromDB(): ".mysql_error();
      logEntry('critical',$error);
      return false;                 
    }

    $idx = 0;
    if ($roomResult) {
      while ($roomRow = mysql_fetch_array($roomResult)) {
        $roomEntity = new roomEntity($roomRow['component_instance_name'], $roomRow['component_id']); 
		$this->roomEntities[$idx] = $roomEntity;
        $idx = $idx +1;
      }
    }
    return true;
  }
}

?>
