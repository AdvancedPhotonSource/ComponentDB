<?php   
/*
 * Defines business class buildingList which contains an array of buildingEntity.
 */

/*
 * buildingEntity corresponds to a single row from the IRMIS component_rel table.
 */
class switchgearEntity {
  var $logical_desc;
  var $component_id;
 
  function switchgearEntity($logical_desc, $component_id) {
    $this->logical_desc = $logical_desc;
	$this->component_id = $component_id;

  }
  
  function getLogicalDesc() {
    return $this->logical_desc;
  }
  
  function getComponent_id() {
    return $this->component_id;
  }
}

/*
 * buildingList is a business object which contains a collection of information
 * representing the list of all current buildings in the IRMIS database. It is
 * essentially a wrapper for an array of buildingEntity, along with the standard
 * "loadFromDB()" function and other utility functions.
 */
class switchgearList {
  // an array of roomEntity
  var $switchgearEntities;

  function switchgearList() {
    $this->switchgearEntities = array();
  }

  function getElement($idx) {
    return $this->switchgearEntities[$idx];
  }

  // return size of array
  function length() {
    if ($this->switchgearEntities == null)
      return 0;
    else 
      return count($this->switchgearEntities);
  }
  
  /*
   * getArray() returns an array of roomEntity (assuming loadFromDB has
   * been called already).
   */
  function getArray() {
    return $this->switchgearEntities;
  }
    
  // Returns getElementForGroupName equal to $group_name

  function getElementForSwitchgear($logical_desc) {
    foreach ($this->switchgearEntities as $switchgearEntity) {
  	  if ($switchgearEntity->getLogicalDesc() == $logical_desc) {
  	    return $switchgearEntity;
  	  }
  	}
  	 logEntry('debug',"Unable to find switchgearEntity for APS group name $group_name");
  	 return null;
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
    $qb->addColumn("component_rel.logical_desc");
	$qb->addColumn("component.component_id");
    $qb->addTable("component");
	$qb->addTable("component_rel");
	$qb->addTable("component_type");
	$qb->addWhere("component.component_type_id = component_type.component_type_id");
	$qb->addWhere("component_rel.child_component_id = component.component_id");
	$qb->addWhere("component_rel.component_rel_type_id = '3'");
	$qb->addWhere("component_type.component_type_id = '816'");
	$qb->addWhere("component.mark_for_delete = '0'");
	$qb->addSuffix("order by logical_desc");
    $switchgearQuery = $qb->getQueryString();
    //logEntry('debug',$roomQuery);
	

    if (!$switchgearResult = mysql_query($switchgearQuery, $conn)) {
      $errno = mysql_errno();
      $error = "switchgearList.loadFromDB(): ".mysql_error();
      logEntry('critical',$error);
      return false;                 
    }

    $idx = 0;
    if ($switchgearResult) {
      while ($switchgearRow = mysql_fetch_array($switchgearResult)) {
        $switchgearEntity = new switchgearEntity($switchgearRow['logical_desc'], $switchgearRow['component_id']); 
		$this->switchgearEntities[$idx] = $switchgearEntity;
        $idx = $idx +1;
      }
    }
    return true;
  }
}

?>
