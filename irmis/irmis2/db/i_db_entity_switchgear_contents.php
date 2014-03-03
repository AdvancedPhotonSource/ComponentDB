<?php   
/*
 * Defines business class RackContentsList which contains an array of RackContentsEntity.
 */

class SGContentsEntity {
  var $componentTypeName;
  var $componentInstanceName;
  var $componentTypeID;
  var $description;
  var $manufacturer;
  var $logicalDesc;
  var $logicalOrder;
  var $ID;
  var $sum;
  var $parent_component_id;
  var $parentRack;
  var $parentRackid;
  var $ParentRoom;
  var $ParentBldg;
  
  function sgContentsEntity($componentTypeName,$componentInstanceName,$componentTypeID,
                        $description,$manufacturer,$logicalDesc,$logicalOrder,$ID,$sum, $parentComponentID, $ParentRack, $parentRackid, $ParentRoom, $ParentBldg) {
	$this->componentTypeName = $componentTypeName;
	$this->componentInstanceName = $componentInstanceName;
	$this->componentTypeID = $componentTypeID;
	$this->description = $description;
	$this->manufacturer = $manufacturer;
	$this->logicalDesc = $logicalDesc;
	$this->logicalOrder = $logicalOrder;
	$this->ID = $ID;
	$this->sum = $sum;
	$this->parentComponentID = $parentComponentID;
	$this->ParentRack = $ParentRack;
	$this->ParentRackid = $parentRackid;
	$this->ParentRoom = $ParentRoom;
	$this->ParentBldg = $ParentBldg;
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
  function getParentComponentID() {
	return $this->parentComponentID;
  }
  function getParentRack() {
    return $this->ParentRack;  
  }  
  function getParentRackid() {
    return $this->ParentRackid;  
  }    
  function getParentRoom() {
    return $this->ParentRoom;  
  }
  function getParentBldg() {
   return $this->ParentBldg;  
  }

}


/*
 * IOCContentsList is a business object which contains a collection of information
 * representing the list of all current IOC's in the IRMIS database. It is
 * essentially a wrapper for an array of IOCEntity, along with the standard
 * "loadFromDB()" function and other utility functions.
 */
class SGContentsList {
  // an array of IOCContentsEntity
  var $sgContentsEntities;

  function sgContentsList() {
    $this->sgContentsEntities = array();
  }

  function getElement($idx) {
    return $this->sgContentsEntities[$idx];
  }

  // return size of array
  function length() {
    if ($this->sgContentsEntities == null)
      return 0;
    else 
      return count($this->sgContentsEntities);
  }
  
  /*
   * getArray() returns an array of IOCContentsEntity (assuming loadFromDB has
   * been called already).
   */
  function getArray() {
    return $this->sgContentsEntities;
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
  function loadFromDB($dbConn, $SGName) {
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
	$qb->addColumn("cr.parent_component_id");
	$qb->addTable("component c");
	$qb->addTable("component_type ct");
	$qb->addTable("component_rel cr");
	$qb->addTable("mfg");
	$qb->addWhere("cr.component_rel_type_id = '3'");
	$qb->addWhere("ct.mfg_id = mfg.mfg_id");
	$qb->addWhere("c.component_id = cr.child_component_id");
	$qb->addWhere("c.component_type_id = ct.component_type_id");
	$qb->addWhere("cr.parent_component_id = '$SGName'");
    $qb->addWhere("cr.mark_for_delete = '0'");	
	$qb->addSuffix("order by cr.logical_order");										 
    $sgContentsQuery = $qb->getQueryString();
    //logEntry('debug',$rackContentsQuery);

    if (!$sgContentsResult = mysql_query($sgContentsQuery, $conn)) {
      $errno = mysql_errno();
      $error = "SGContentsList.loadFromDB(): ".mysql_error();
      logEntry('critical',$error);
      return false;                 
    }




    $idx = 0;
    if ($sgContentsResult) {
      while ($sgContentsRow = mysql_fetch_array($sgContentsResult)) {
		  
		  
//---------------------------------------------------------------------------


	     $parentCount=0;
         $count = 0;
		 $component_id = $sgContentsRow[1];
	     $locationPath = array();
         while ($count != 1) {
         //$conn = $dbConn->getConnection();
         //$tableNamePrefix = $dbConn->getTableNamePrefix();

		
		$qb1 = new DBQueryBuilder($tableNamePrefix);
		$qb1->addColumn("c.component_instance_name");
		$qb1->addColumn("c.component_id");
		$qb1->addColumn("ct.component_type_name");
		
		$qb1->addTable("component c");
		$qb1->addTable("component_rel cr");
		$qb1->addTable("component_type ct");
		
		$qb1->addWhere("cr.child_component_id = '$component_id'");
		$qb1->addWhere("cr.component_rel_type_id = '2'");
		$qb1->addWhere("c.component_type_id = ct.component_type_id");
		$qb1->addWhere("cr.parent_component_id = c.component_id");
		$qb1->addWhere("cr.mark_for_delete = '0'");
		$rackParentQuery = $qb1->getQueryString();
		
		if (!$rackParentResult = mysql_query($rackParentQuery, $conn)) {
	      $errno = mysql_errno();
	      $error = "SGContentsList.loadFromDB(): ".mysql_error();
	      logEntry('critical',$error);
          return false;                 
       }
	   
	   if ($rackParentResult&&$parentCount < 12) {
	   if ($rackParentRow = mysql_fetch_array($rackParentResult)) {
	     $component_instance_name = $rackParentRow['component_instance_name']; 
		 $component_id = $rackParentRow['component_id']; 
		 $component_type_name = $rackParentRow['component_type_name']; 
	   }
       if (strstr($component_type_name, "Rack")) {
		  $count=0;
		  $parentCount++;
		  array_push($locationPath, $component_instance_name);
		  array_push($locationPath, $component_id);
		  $sgContentsRow['ParentRackid'] = array_pop($locationPath);  
		  $sgContentsRow['ParentRack'] = array_pop($locationPath); 
	   } else if (strstr($component_type_name, "Room")) {
		  $count=0;
		  $parentCount++;
		  array_push($locationPath, $component_instance_name);
		  $sgContentsRow['ParentRoom'] = array_pop($locationPath);
	   } else if (strstr($component_type_name, "Building")) {
		  $count=1;
		  $parentCount++;
		  array_push($locationPath, $component_instance_name);
		  $sgContentsRow['ParentBldg'] = array_pop($locationPath);
	   } else {
		  $count=0;
		  $parentCount++;
		  $sgContentsRow['ParentRack'] = "";
		  $sgContentsRow['ParentRoom'] = "";
		  $sgContentsRow['ParentBldg'] = "";
	   }  //end if
	 }  //end if
   }  //end while
//return $locationPath;

//--------------------------------------------------------------------------
/*
	     $iocCount=0;
         $count = 0;
		 $component_id = $sgContentsRow[1];
	     $locationPath = array();
         while ($count != 1) {
         //$conn = $dbConn->getConnection();
         //$tableNamePrefix = $dbConn->getTableNamePrefix();

		
		$qb2 = new DBQueryBuilder($tableNamePrefix);
		$qb2->addColumn("c.component_instance_name");
		$qb2->addColumn("c.component_id");
		$qb2->addColumn("ct.component_type_name");
		$qb2->addColumn("ct.description");
		$qb2->addColumn("cr.logical_desc");
		$qb2->addColumn("cr.logical_order");
		$qb2->addColumn("mfg.mfg_name");
		
		$qb2->addTable("component c");
		$qb2->addTable("component_rel cr");
		$qb2->addTable("component_type ct");
		$qb2->addTable("mfg");
		
		$qb2->addWhere("ct.component_rel_type_id = '3'");
		$qb2->addWhere("ct.mfg_id = mfg.mfg_id");
		$qb2->addWhere("c.component_id = cr.child_component_id");
		$qb2->addWhere("c.component_type_id = ct.component_type_id");
		$qb2->addWhere("cr.parent_component_id = '$component_id'");
		$qb2->addWhere("cr.mark_for_delete = '0'");
		$iocQuery = $qb2->getQueryString();
		
		if (!$iocResult = mysql_query($iocQuery, $conn)) {
	      $errno = mysql_errno();
	      $error = "RackList.loadFromDB(): ".mysql_error();
	      logEntry('critical',$error);
          return false;                 
       }
	   
	   if ($iocResult&&$parentCount < 12) {
	   if ($iocRow = mysql_fetch_array($iocResult)) {
	     $component_instance_name = $iocRow['component_instance_name']; 
		 $component_id = $iocRow['component_id']; 
		 $component_type_name = $iocRow['component_type_name']; 
	   }
       if (strstr($component_instance_name, "%ioc%")) {
		  $count=0;
		  $parentCount++;
		  array_push($locationPath, $component_instance_name);
		  $sgContentsRow['ParentRack'] = array_pop($locationPath);	   
	   }// else if (strstr($component_type_name, "Room")) {
		//  $count=0;
		//  $parentCount++;
		//  array_push($locationPath, $component_instance_name);
		//  $sgContentsRow['ParentRoom'] = array_pop($locationPath);
	   } else if (strstr($component_type_name, "Switch Gear")) {
		  $count=1;
		  $parentCount++;
		  array_push($locationPath, $component_instance_name);
		  $sgContentsRow['ParentBldg'] = array_pop($locationPath);
	   } else {
		  $count=0;
		  $iocCount++;
		  $sgContentsRow['ParentRack'] = "";
		  $sgContentsRow['ParentRoom'] = "";
		  $sgContentsRow['ParentBldg'] = "";
	   }  //end if
	 }  //end if
   }  //end while
//return $locationPath;
*/

//-------------------------------------------------------------------------

	  $child = $sgContentsRow[1];
	  
	// Get child components sum.  This section takes the component_id of the 
	// previous query, and counts the number of children it has.  If it has
	// no children, $rackContentsRow['sum'] is equal to 0.
    $qb3 = new DBQueryBuilder($tableNamePrefix);
    $qb3->addColumn("sum(cr.component_rel_type_id='3') as sum");
	$qb3->addTable("component c");
	$qb3->addTable("component_rel cr");
	$qb3->addWhere("c.component_id = cr.parent_component_id");
	$qb3->addWhere("c.component_id = '$child'");
	$qb3->addWhere("cr.mark_for_delete = '0'");
	$qb3->addSuffix("group by cr.parent_component_id");
	$sgSumQuery = $qb3->getQueryString();
	//logEntry('debug',$rackSumQuery);
	
	if (!$sgSumResult = mysql_query($sgSumQuery, $conn)) {
	   $errno = mysql_errno();
	   $error = "SGContentsList.loadFromDB(): ".mysql_error();
	   logEntry('critical',$error);
      return false;                 
    }
	
	//put the child sum into sgContentsRow['sum']
	if ($sgSumResult) {
	  if ($sgSumRow = mysql_fetch_array($sgSumResult)) {
          $sgContentsRow['sum'] = $sgSumRow['sum'];
		  } else {
		  $sgContentsRow['sum'] = '0';
		  }
		  
	  }

    
		$sgContentsEntity = new SGContentsEntity($sgContentsRow['component_type_name'], $sgContentsRow['component_instance_name'], 
		                                             $sgContentsRow['component_type_id'], $sgContentsRow['description'], $sgContentsRow['mfg_name'],
												     $sgContentsRow['logical_desc'],$sgContentsRow['logical_order'], $sgContentsRow['component_id'],
													 $sgContentsRow['sum'], $sgContentsRow['parent_component_id'], $sgContentsRow['ParentRack'], $sgContentsRow['ParentRackid'],
													 $sgContentsRow['ParentRoom'], $sgContentsRow['ParentBldg']);
		
        $this->sgContentsEntities[$idx] = $sgContentsEntity;
        $idx = $idx +1;
      }
    }
    return true;
  }
}

?>
