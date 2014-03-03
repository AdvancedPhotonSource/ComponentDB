<?php   
/*
 * Defines business class IOCList which contains an array of IOCEntity.
 */

/*
 * IOCEntity corresponds to a single row from the IRMIS ioc table.
 */
class IOCContentsEntity {
  var $componentTypeName;
  var $description;
  var $logicalDesc;
  var $logicalOrder;
  var $ID;
  var $component_id;
  
  function IOCContentsEntity($componentTypeName, $description, $logicalDesc, $logicalOrder, $ID, $component_id) {
	$this->componentTypeName = $componentTypeName;
	$this->description = $description;
	$this->logicalDesc = $logicalDesc;
	$this->logicalOrder = $logicalOrder;
	$this->ID = $ID;
	$this->component_id = $component_id;
  }
  function getComponentTypeName() {
    return $this->componentTypeName;
  }
  function getDescription() {
    return $this->description;
  }
  function getLogicalDesc() {
    return $this->logicalDesc;
  }
  function getLogicalOrder() {
    return $this->logicalOrder;
  }
  function getID() {
    return $this->ID;
  }
  function getComponentID() {
	return $this->component_id;
  }
}


/*
 * IOCContentsList is a business object which contains a collection of information
 * representing the list of all current IOC's in the IRMIS database. It is
 * essentially a wrapper for an array of IOCEntity, along with the standard
 * "loadFromDB()" function and other utility functions.
 */
class IOCContentsList {
  // an array of IOCContentsEntity
  var $iocContentsEntities;

  function IOCContentsList() {
    $this->iocContentsEntities = array();
  }

  function getElement($idx) {
    return $this->iocContentsEntities[$idx];
  }

  // return size of array
  function length() {
    if ($this->iocContentsEntities == null)
      return 0;
    else 
      return count($this->iocContentsEntities);
  }
  
  /*
   * getArray() returns an array of IOCContentsEntity (assuming loadFromDB has
   * been called already).
   */
  function getArray() {
    return $this->iocContentsEntities;
  }
  
  // Returns getElementForIocContents equal to $iocName	
  function getElementForIocContents($iocName) {
    foreach ($this->iocContentsEntities as $iocContentsEntity) {
	  if ($iocContentsEntity->getIocName() == $iocName) {
	    return $iocContentsEntity;
	  }
	 }
	 logEntry('debug',"Unable to find IocContentsEntity for iocName $iocName");
	 return null;
  }
	
  // Returns IOCEntity with primary key $id from ioc table
  function getElementForId($id) {
    foreach ($this->iocContentsEntities as $iocContentsEntity) {
      if ($iocContentsEntity->getIocID() == $id) {
        return $iocContentsEntity;
      }    
    }
    logEntry('debug',"Unable to find IocEntity for id $id");
    return null;
  }


function loadFromDB($dbConn, $iocName, $componentID) { //find parent of ioc that is housed in a rack
    global $errno;
    global $error;
   
	$count = 0;
	while (!$ParentComponentID) {	
    $conn = $dbConn->getConnection();
    $tableNamePrefix = $dbConn->getTableNamePrefix();
	
	// Assemble the query to get the housing parent ID and component name, given an ioc component id
	$qb = new DBQueryBuilder($tableNamePrefix);
	$qb->addColumn("ct.component_type_name");
	$qb->addColumn("c.component_id");
	$qb->addColumn("c.component_instance_name");
	
	
	$qb->addTable("component_rel cr");
	$qb->addTable("component_type ct");
	$qb->addTable("component c");
	
	$qb->addWhere("cr.child_component_id = '$componentID'");
	$qb->addWhere("cr.component_rel_type_id = '2'");
	$qb->addWhere("c.component_type_id = ct.component_type_id");
	$qb->addWhere("cr.parent_component_id = c.component_id");
	$qb->addWhere("cr.mark_for_delete = '0'");
	$piocContentsQuery = $qb->getQueryString();
	//logEntry('debug',$piocIFQuery);
	
    if (!$piocContentsResult = mysql_query($piocContentsQuery, $conn)) {
      $errno = mysql_errno();
      $error = "IOCContentsList.loadFromDB(): ".mysql_error();
      logEntry('critical',$error);
      return false;                 
    }


	if ($piocContentsResult) {
	  if ($row = mysql_fetch_array($piocContentsResult)) {
	    $component_type_name = $row['component_type_name'];
		$component_id = $row['component_id'];
		$component_instance_name = $row['component_instance_name'];

		if (strstr($component_type_name, "Rack")) {
		  $ParentComponentID = $prevCompID;
		  $_SESSION['prevCompID'] = $prevCompID;
	      $_SESSION['prevCompName'] = $prevCompName;
	      $_SESSION['prevCompInstName'] = $prevCompInstName;
		  
		} elseif (strstr($component_type_name, "Rack" )&& $count == 0) { // if the direct parent is a rack
		  $ParentComponentID = $component_id;
		  $prevCompName = $component_type_name;
		  $prevCompInstName = $component_instance_name;
		  
		} elseif (strstr($component_type_name, "VME Chassis - Shared/Virtual")) { // This case is for the unique case of a virtual ioc
		  $ParentComponentID = $component_id;
		  $_SESSION['prevCompName'] = "VME Chassis";
		  
		} else {
		  $componentID = $component_id;
		  $prevCompName = $component_type_name;
		  $prevCompID = $component_id;
		  $prevCompInstName = $component_instance_name;
		  $count++;
		  //logentry ('Debug',"IF is ".$prevIFType.$prevCompName.$prevCompID);
		}
	  }
	}
	
   }
	
	// Assemble a query that uses the ParentComponentID from the 
	// above query, to get the parents' housing interface provided.
	// This will be used to choose the format of the contents results page.
    $tableNamePrefix = $dbConn->getTableNamePrefix();
	
	$qb = new DBQueryBuilder($tableNamePrefix);
	$qb->addColumn("ctift.if_type");
	
	$qb->addTable("component_type_if_type ctift");
	$qb->addTable("component_type_if ctif");
	$qb->addTable("component_type ct");
	$qb->addTable("component c");
	
	$qb->addWhere("c.component_id = '$ParentComponentID'");
	$qb->addWhere("c.component_type_id = ct.component_type_id");
	$qb->addWhere("ct.component_type_id = ctif.component_type_id");
	$qb->addWhere("ctif.component_type_if_type_id = ctift.component_type_if_type_id");
	$qb->addWhere("ctift.component_rel_type_id = '2'");
	$qb->addWhere("ctif.presented = '1'");
	$qb->addWhere("((ctift.component_type_if_type_id = '3')||(ctift.component_type_if_type_id = '7')||(ctift.component_type_if_type_id = '31')||(ctift.component_type_if_type_id = '275')||(ctift.component_type_if_type_id = '2'))");
	$piocIFQuery = $qb->getQueryString();
	
	 if (!$piocIFResult = mysql_query($piocIFQuery, $conn)) {
      $errno = mysql_errno();
      $error = "IOCContentsList.loadFromDB(): ".mysql_error();
      logEntry('critical',$error);
      return false;                 
    }

    if ($piocIFResult) {
	  if ($row = mysql_fetch_array($piocIFResult)) {
	    $if_type = $row['if_type'];
        $_SESSION['prevIF'] = $if_type;
	  }
	}
	
	
	if ($ParentComponentID) {
	
    $conn = $dbConn->getConnection();
    $tableNamePrefix = $dbConn->getTableNamePrefix();	
	
	// Assemble the query to get the children of the housing parent ID, given an parent component ID from above
	$qb = new DBQueryBuilder($tableNamePrefix);
	$qb->addColumn("ct.component_type_name");
	$qb->addColumn("ct.component_type_id");
	$qb->addColumn("ct.description");
	$qb->addColumn("cr.logical_desc");
	$qb->addColumn("cr.logical_order");
	$qb->addColumn("c.component_id");
	
	$qb->addTable("component c");
	$qb->addTable("component_type ct");
	$qb->addTable("component_rel cr");
	
	$qb->addWhere("cr.component_rel_type_id = '2'");
	$qb->addWhere("c.component_id = cr.child_component_id");
	$qb->addWhere("c.component_type_id = ct.component_type_id");
	$qb->addWhere("cr.parent_component_id = '$ParentComponentID'");  // the $ParentComponentID is from the if statement above if the parent is a VME or VXI chassis
    $qb->addWhere("cr.mark_for_delete = '0'");
	$iocContentsQuery = $qb->getQueryString();
	
	
    if (!$iocContentsResult = mysql_query($iocContentsQuery, $conn)) {
      $errno = mysql_errno();
      $error = "IOCContentsList.loadFromDB(): ".mysql_error();
      logEntry('critical',$error);
      return false;                 
    }


    $idx = 0;
    if ($iocContentsResult) {
      while ($iocContentsRow = mysql_fetch_array($iocContentsResult)) {
        $iocContentsEntity = new IOCContentsEntity($iocContentsRow['component_type_name'], 
		                                           $iocContentsRow['description'], $iocContentsRow['logical_desc'],
												   $iocContentsRow['logical_order'], $iocContentsRow['component_type_id'], $iocContentsRow['component_id']);
		
        $this->iocContentsEntities[$idx] = $iocContentsEntity;
        $idx = $idx +1;
      }
    }
    return true;
  }
 }
}
?>
