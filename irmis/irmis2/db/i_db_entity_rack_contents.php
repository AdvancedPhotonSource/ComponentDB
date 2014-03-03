<?php   
/*
 * Defines class RackContentsList which contains an array of RackContentsEntity.
 */

class RackContentsEntity {
  var $rackName;
  var $componentTypeName;
  var $componentInstanceName;
  var $componentTypeID;
  var $description;
  var $manufacturer;
  var $logicalDesc;
  var $logicalOrder;
  var $ID;
  var $sum;
  
  function rackContentsEntity($rackName,$componentTypeName,$componentInstanceName,
                              $componentTypeID,$description,$manufacturer,$logicalDesc,$logicalOrder,$ID,$sum) {
    $this->rackName = $rackName;
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
  function getRackName() {
    return $this->rackName;
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
  function getManufacturer () {
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
 * RackContentsList is a object which contains a collection of information
 * representing the list of all current rack's in the IRMIS database. It is
 * essentially a wrapper for an array of RackContentsEntity, along with the standard
 * "loadFromDB()" function and other utility functions.
 */
class RackContentsList {
  // an array of RackContentsEntity
  var $rackContentsEntities;

  function rackContentsList() {
    $this->rackContentsEntities = array();
  }

  function getElement($idx) {
    return $this->rackContentsEntities[$idx];
  }

  // return size of array
  function length() {
    if ($this->rackContentsEntities == null)
      return 0;
    else 
      return count($this->rackContentsEntities);
  }
  
  /*
   * getArray() returns an array of RackContentsEntity (assuming loadFromDB has
   * been called already).
   */
  function getArray() {
    return $this->rackContentsEntities;
  }
  
  // Returns getElementForRackContents equal to $rackName	
  function getElementForRackContents($rackName) {
    foreach ($this->rackContentsEntities as $rackContentsEntity) {
	  if ($rackContentsEntity->getRackName() == $rackName) {
	    return $rackContentsEntity;
	  }
	 }
	 logEntry('debug',"Unable to find IocContentsEntity for rackName $rackName");
	 return null;
  }
	
  // Returns RackContentsEntity with primary key $id
  function getElementForId($id) {
    foreach ($this->rackContentsEntities as $rackContentsEntity) {
      if ($rackContentsEntity->getRackID() == $id) {
        return $rackContentsEntity;
      }    
    }
    logEntry('debug',"Unable to find RackEntity for id $id");
    return null;
  }

  /*
   * Conducts MySQL db query. Returns false if db error occurs, true
   * otherwise. */
  function loadFromDB($dbConn, $rackName) {
    global $errno;
    global $error;
        
    $conn = $dbConn->getConnection();
    $tableNamePrefix = $dbConn->getTableNamePrefix();
        
    // Get core rack info
    $qb = new DBQueryBuilder($tableNamePrefix);
    $qb->addColumn("ct.component_type_name");
	$qb->addColumn("c.component_instance_name");
    $qb->addColumn("ct.description");
	$qb->addColumn("ct.component_type_id");
	$qb->addColumn("c.component_id");
	$qb->addColumn("cr.logical_desc");
	$qb->addColumn("cr.logical_order");
	$qb->addColumn("mfg.mfg_name");
	$qb->addTable("component c");
	$qb->addTable("component_type ct");
	$qb->addTable("component_rel cr");
	$qb->addTable("mfg");
	$qb->addWhere("cr.component_rel_type_id = '2'");
	$qb->addWhere("ct.mfg_id = mfg.mfg_id");
	$qb->addWhere("c.component_id = cr.child_component_id");
	$qb->addWhere("c.component_type_id = ct.component_type_id");
	/*$qb->addWhere("cr.parent_component_id = (select c.component_id
	                                         from component_rel cr, component c
											 where cr.logical_desc = c.component_instance_name
											 and c.component_id = '$rackName' limit 1)"); */
	$qb->addWhere("cr.parent_component_id = (select c.component_id
	                                         from component_rel cr, component c
											 where c.component_id = '$rackName' limit 1)");											 
    $qb->addWhere("cr.mark_for_delete = '0'");											 
	//$qb->addSuffix("order by ct.component_type_name");
	$qb->addSuffix("order by cr.logical_order");
    $rackContentsQuery = $qb->getQueryString();
    //logEntry('debug',$rackContentsQuery);

    if (!$rackContentsResult = mysql_query($rackContentsQuery, $conn)) {
      $errno = mysql_errno();
      $error = "RackContentsList.loadFromDB(): ".mysql_error();
      logEntry('critical',$error);
      return false;                 
    }

    $idx = 0;
    if ($rackContentsResult) {
      while ($rackContentsRow = mysql_fetch_array($rackContentsResult)) {
	  
	  $child = $rackContentsRow[4];
	  
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
	
	//put the child sum into rackContentsRow['sum']
	if ($rackSumResult) {
	  if ($rackSumRow = mysql_fetch_array($rackSumResult)) {
          $rackContentsRow['sum'] = $rackSumRow['sum'];
		  } else {
		  $rackContentsRow['sum'] = '0';
		  }
		  
	  }
	  
        $rackContentsEntity = new RackContentsEntity($rackContentsRow['rack_name'], $rackContentsRow['component_type_name'], $rackContentsRow['component_instance_name'], 
		                                             $rackContentsRow['component_type_id'], $rackContentsRow['description'], $rackContentsRow['mfg_name'],
												     $rackContentsRow['logical_desc'],$rackContentsRow['logical_order'], $rackContentsRow['component_id'],
													 $rackContentsRow['sum']);
		
        $this->rackContentsEntities[$idx] = $rackContentsEntity;
        $idx = $idx +1;
      }
    }
    return true;
  }
}

?>
