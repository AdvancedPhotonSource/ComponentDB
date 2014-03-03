<?php   
/*
 * Defines business class RackContentsList which contains an array of RackContentsEntity.
 */

class RackCCEntity {
  var $componentTypeName;
  var $componentInstanceName;
  var $componentTypeID;
  var $description;
  var $manufacturer;
  var $logicalDesc;
  var $logicalOrder;
  var $ID;
  var $sum;
  
  function rackCCEntity($componentTypeName,$componentInstanceName,$componentTypeID,
                        $description,$manufacturer,$logicalDesc,$logicalOrder,$ID,$sum) {
	$this->componentTypeName = $componentTypeName;
	$this->componentInstanceName = $componentInstanceName;
	$this->componentTypeID = $componentTypeID;
	$this->description = $description;
	$this->manufacturer = $manufacturer;
	$this->logicalDesc = $logicalDesc;
	$this->logicalOrder = $logicalOrder;
	$this->ID = $ID;
	$this->sum = $sum;
  }
  function getComponentTypeName() {
    return $this->componentTypeName;
  }
  function getComponentInstanceName() {
    return $this->componentInstanceName;
  }
  function getComponentTypeID() {
    return $this->componentTypeID;
  }
  function getDescription() {
    return $this->description;
  }
  function getManufacturer() {
    return $this->manufacturer;
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
  function getSum() {
    return $this->sum;
  }
}


/*
 * IOCContentsList is a business object which contains a collection of information
 * representing the list of all current IOC's in the IRMIS database. It is
 * essentially a wrapper for an array of IOCEntity, along with the standard
 * "loadFromDB()" function and other utility functions.
 */
class RackCCList {
  // an array of IOCContentsEntity
  var $rackCCEntities;

  function rackCCList() {
    $this->rackCCEntities = array();
  }

  function getElement($idx) {
    return $this->rackCCEntities[$idx];
  }

  // return size of array
  function length() {
    if ($this->rackCCEntities == null)
      return 0;
    else 
      return count($this->rackCCEntities);
  }
  
  /*
   * getArray() returns an array of IOCContentsEntity (assuming loadFromDB has
   * been called already).
   */
  function getArray() {
    return $this->rackCCEntities;
  }
/*
  // Returns getElementForRackContents equal to $rackName	
  function getElementForRackCC($ID) {
    foreach ($this->rackCCEntities as $rackCCEntity) {
	  if ($rackCCEntity->getComponentTypeName() == $ComponentTypeName) {
	    return $rackCCEntity;
	  }
	 }
	 logEntry('debug',"Unable to find IocContentsEntity for rackName $rackName");
	 return null;
  }
	
  // Returns IOCEntity with primary key $id from ioc table
  function getElementForId($id) {
    foreach ($this->rackCCEntities as $rackCCEntity) {
      if ($rackCCEntity->getComponentTypeID() == $id) {
        return $rackCCEntity;
      }    
    }
    logEntry('debug',"Unable to find RackEntity for id $id");
    return null;
  }
*/
  /*
   * Conducts MySQL db transactions to initialize IOCContentsList from the
   * ioc table. Returns false if db error occurs, true
   * otherwise. */
  function loadFromDBCC($dbConn, $compID) {
    global $errno;
    global $error;
        
    $conn = $dbConn->getConnection();
    $tableNamePrefix = $dbConn->getTableNamePrefix();
//-        
    // Get core rack info
    $qb = new DBQueryBuilder($tableNamePrefix);
    $qb->addColumn("ct.component_type_name");
	$qb->addColumn("c.component_id");
    $qb->addColumn("ct.component_type_id");
	$qb->addColumn("ct.description");
	$qb->addColumn("cr.logical_desc");
	$qb->addColumn("cr.logical_order");
	$qb->addColumn("c.component_instance_name");
	$qb->addColumn("mfg.mfg_name");
	$qb->addTable("component c");
	$qb->addTable("component_type ct");
	$qb->addTable("component_rel cr");
	$qb->addTable("mfg");
	$qb->addWhere("cr.component_rel_type_id = '2'");
	$qb->addWhere("ct.mfg_id = mfg.mfg_id");
	$qb->addWhere("c.component_id = cr.child_component_id");
	$qb->addWhere("c.component_type_id = ct.component_type_id");
	$qb->addWhere("cr.parent_component_id = '$compID'");
    $qb->addWhere("cr.mark_for_delete = '0'");	
	$qb->addSuffix("order by cr.logical_order");										 
    $rackCCQuery = $qb->getQueryString();
    //logEntry('debug',$rackContentsQuery);

    if (!$rackCCResult = mysql_query($rackCCQuery, $conn)) {
      $errno = mysql_errno();
      $error = "RackCCList.loadFromDB(): ".mysql_error();
      logEntry('critical',$error);
      return false;                 
    }

    $idx = 0;
    if ($rackCCResult) {
      while ($rackCCRow = mysql_fetch_array($rackCCResult)) {
        
	  $child = $rackCCRow[1];
	  
	// Get child components sum.  This section takes the component_id of the 
	// previous query, and counts the number of children it has.  If it has
	// no children, $rackContentsRow['sum'] is equal to 0.
    $qb2 = new DBQueryBuilder($tableNamePrefix);
    $qb2->addColumn("sum(cr.component_rel_type_id='2') as sum");
	$qb2->addTable("component c");
	$qb2->addTable("component_rel cr");
	$qb2->addWhere("c.component_id = cr.parent_component_id");
	$qb2->addWhere("c.component_id = '$child'");
	$qb2->addWhere("cr.mark_for_delete = '0'");
	$qb2->addSuffix("group by cr.parent_component_id");
	$rackSumQuery = $qb2->getQueryString();
	//logEntry('debug',$rackSumQuery);
	
	if (!$rackSumResult = mysql_query($rackSumQuery, $conn)) {
	   $errno = mysql_errno();
	   $error = "RackContentsList.loadFromDB(): ".mysql_error();
	   logEntry('critical',$error);
      return false;                 
    }
	
	//put the child sum into rackCCRow['sum']
	if ($rackSumResult) {
	  if ($rackSumRow = mysql_fetch_array($rackSumResult)) {
          $rackCCRow['sum'] = $rackSumRow['sum'];
		  } else {
		  $rackCCRow['sum'] = '0';
		  }
		  
	  }
    
		$rackCCEntity = new RackCCEntity($rackCCRow['component_type_name'], $rackCCRow['component_instance_name'], 
		                                             $rackCCRow['component_type_id'], $rackCCRow['description'], $rackCCRow['mfg_name'],
												     $rackCCRow['logical_desc'],$rackCCRow['logical_order'], $rackCCRow['component_id'],
													 $rackCCRow['sum']);
		
        $this->rackCCEntities[$idx] = $rackCCEntity;
        $idx = $idx +1;
      }
    }
    return true;
  }
}

?>
