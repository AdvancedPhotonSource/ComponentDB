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
  var $ac1;
  var $ac2;
  var $ac3;
  var $ac4;
  var $ac5;
  var $ac6;
  var $ac7;
  var $ac8;
  var $ac9;
  var $ac10;
  var $ac11;
  var $ac12;
  var $ac13;
  var $ac14;
  var $ac15;
  var $ac16;
  var $ac17;
  var $ac18;
  var $ac19;
  var $ac20;
  var $ac21;
  var $ac22;
  var $ac23;
  var $ac24;
  
  function rackContentsEntity($rackName,$componentTypeName,$componentInstanceName,
                              $componentTypeID,$description,$manufacturer,$logicalDesc,$logicalOrder,$ID,$sum,
							  $ac1, $ac2, $ac3, $ac4, $ac5, $ac6, $ac7, $ac8, $ac9, $ac10, $ac11, $ac12, $ac13,
							  $ac14, $ac15, $ac16, $ac17, $ac18, $ac19, $ac20, $ac21, $ac22, $ac23, $ac24) {
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
	$this->ac1 = $ac1;
	$this->ac2 = $ac2;
	$this->ac3 = $ac3;
	$this->ac4 = $ac4;
	$this->ac5 = $ac5;
	$this->ac6 = $ac6;
	$this->ac7 = $ac7;
	$this->ac8 = $ac8;
	$this->ac9 = $ac9;
	$this->ac10 = $ac10;
	$this->ac11 = $ac11;
	$this->ac12 = $ac12;
	$this->ac13 = $ac13;
	$this->ac14 = $ac14;
	$this->ac15 = $ac15;
	$this->ac16 = $ac16;
	$this->ac17 = $ac17;
	$this->ac18 = $ac18;
	$this->ac19 = $ac19;
	$this->ac20 = $ac20;
	$this->ac21 = $ac21;
	$this->ac22 = $ac22;
	$this->ac23 = $ac23;
	$this->ac24 = $ac24;
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
  function getAC1() {
    return $this->ac1;
  }
  function getAC2() {
    return $this->ac2;
  }
  function getAC3() {
    return $this->ac3;
  }
  function getAC4() {
    return $this->ac4;
  }
  function getAC5() {
    return $this->ac5;
  }
  function getAC6() {
    return $this->ac6;
  }
  function getAC7() {
    return $this->ac7;
  }
  function getAC8() {
    return $this->ac8;
  }
  function getAC9() {
    return $this->ac9;
  }
  function getAC10() {
    return $this->ac10;
  }
  function getAC11() {
    return $this->ac11;
  }
  function getAC12() {
    return $this->ac12;
  }
  function getAC13() {
    return $this->ac13;
  }
  function getAC14() {
    return $this->ac14;
  }
  function getAC15() {
    return $this->ac15;
  }
  function getAC16() {
    return $this->ac16;
  }
  function getAC17() {
    return $this->ac17;
  }
  function getAC18() {
    return $this->ac18;
  }
  function getAC19() {
    return $this->ac19;
  }
  function getAC20() {
    return $this->ac20;
  }
  function getAC21() {
    return $this->ac21;
  }
  function getAC22() {
    return $this->ac22;
  }
  function getAC23() {
    return $this->ac23;
  }
  function getAC24() {
    return $this->ac24;
  }
  
  
 // function loadDetailsFromDB($dbConn) {
    //$this->comParentIOC = $this->findParentIoc($dbConn, $this->ID);
	//$this->comParentRack = $this->findParentRack($dbConn, $this->ID);
	//$this->comParentRoom = $this->findParentRoom($dbConn, $this->ID);
	//$this->comParentBldg = $this->findParentBuilding($dbConn, $this->ID);
	//$this->locationPath = $this->circuitPath($dbConn);
  //}  
  

/*
 * RackContentsList is a object which contains a collection of information
 * representing the list of all current rack's in the IRMIS database. It is
 * essentially a wrapper for an array of RackContentsEntity, along with the standard
 * "loadFromDB()" function and other utility functions.
 */}
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
	 //logEntry('debug',"Unable to find IocContentsEntity for rackName $rackName");
	 return null;
  }
	
  // Returns RackContentsEntity with primary key $id
  function getElementForId($id) {
    foreach ($this->rackContentsEntities as $rackContentsEntity) {
      if ($rackContentsEntity->getRackID() == $id) {
        return $rackContentsEntity;
      }    
    }
    //logEntry('debug',"Unable to find RackEntity for id $id");
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
	$qb->addColumn("ct.component_type_id");
	$qb->addColumn("c.component_id");
	$qb->addColumn("cr.logical_desc");
	$qb->addColumn("mfg.mfg_name");
	
	$qb->addTable("component c");
	$qb->addTable("component_type ct");
	$qb->addTable("component_rel cr");
	$qb->addTable("mfg");
	$qb->addTable("component_type_function ctf");
	
	$qb->addWhere("cr.component_rel_type_id = '2'");
	$qb->addWhere("ctf.function_id = '50'");
	$qb->addWhere("ct.mfg_id = mfg.mfg_id");
	$qb->addWhere("c.component_id = cr.child_component_id");
	$qb->addWhere("c.component_type_id = ct.component_type_id");
	$qb->addWhere("ct.component_type_id = ctf.component_type_id");
	$qb->addWhere("cr.parent_component_id = '$rackName'");
    $qb->addWhere("cr.mark_for_delete = '0'");											 
	$qb->addSuffix("order by cr.logical_order");
    $rackContentsQuery = $qb->getQueryString();
    //logEntry('debug',$rackContentsQuery);

    if (!$rackContentsResult = mysql_query($rackContentsQuery, $conn)) {
      $errno = mysql_errno();
      $error = "RackContentsList.loadFromDB(): ".mysql_error();
      //logEntry('critical',$error);
      return false;                 
    }

    $idx = 0;
    if ($rackContentsResult) {
    while ($rackContentsRow = mysql_fetch_array($rackContentsResult)) {
	  
	  $compID = $rackContentsRow[3];
	  logEntry('debug',"component number is ".$compID);
	  
	  
/*----------------------------------------------------------------------------------------------------------	*/  
	  
    $count = 0;
	$iteration = 0;
	$locationPath = array();
    while ($count != 1) {	  

    $qb1 = new DBQueryBuilder($tableNamePrefix);
	$qb1->addColumn("ct.component_type_name");
    $qb1->addColumn("description");
	$qb1->addColumn("ct.component_type_id");
	$qb1->addColumn("c.component_instance_name");
	$qb1->addColumn("cr.logical_desc");
	$qb1->addColumn("cr.child_component_id");
	
	$qb1->addTable("component_type ct");
	$qb1->addTable("component c");
	$qb1->addTable("component_rel cr");
	// the subselect finds the parent id of the child that was currently found
	$qb1->addWhere("cr.component_rel_type_id = '3'");
	$qb1->addWhere("c.component_id = cr.child_component_id");
	$qb1->addWhere("c.component_type_id = ct.component_type_id");
	$qb1->addWhere("cr.mark_for_delete = '0'");
	$qb1->addWhere("cr.child_component_id = (select cr.parent_component_id                             
	                                         from component_type ct, component c, component_rel cr
											 where cr.component_rel_type_id = '3'
											 and c.component_id = cr.parent_component_id
											 and c.component_type_id = ct.component_type_id
											 and cr.child_component_id = '$compID'
											 and cr.mark_for_delete = '0')");
    $locationQuery = $qb1->getQueryString();
    //logEntry('debug',$locationQuery);


    if (!$locationResult = mysql_query($locationQuery, $conn)) {
      $errno = mysql_errno();
      $error = "IOCContentsList.loadFromDB(): ".mysql_error();
      logEntry('critical',$error);
      return false;                 
    }


   if ($locationResult && $iteration < 24) {
     if ($row = mysql_fetch_array($locationResult)) {
	    $component_type_name = $row['component_type_name'];
		$compID = $row['child_component_id'];
		$component_instance_name = $row['component_instance_name'];
		$logical_desc = $row['logical_desc'];
	
		if (strstr($component_type_name, "Switch Gear")) {
		  $count=1;
		  array_push($locationPath, $component_type_name);      // pushes the component_type_name into the $locationpath array
		  array_push($locationPath, $logical_desc);             // pushes the logical_desc into the $locationpath array
		  $rackContentsRow['ac1'] = array_pop($locationPath);   // 
		  $rackContentsRow['ac2'] = array_pop($locationPath);   //
		  $rackContentsRow['ac3'] = array_pop($locationPath);   //
		  $rackContentsRow['ac4'] = array_pop($locationPath);   //
		  $rackContentsRow['ac5'] = array_pop($locationPath);   //
		  $rackContentsRow['ac6'] = array_pop($locationPath);   //
		  $rackContentsRow['ac7'] = array_pop($locationPath);   //
		  $rackContentsRow['ac8'] = array_pop($locationPath);   //
		  $rackContentsRow['ac9'] = array_pop($locationPath);   //
		  $rackContentsRow['ac10'] = array_pop($locationPath);  //
		  $rackContentsRow['ac11'] = array_pop($locationPath);  //
		  $rackContentsRow['ac12'] = array_pop($locationPath);  // pops out each element of the array into pieces that are accessable to search pages
		  $rackContentsRow['ac13'] = array_pop($locationPath);  //
		  $rackContentsRow['ac14'] = array_pop($locationPath);  //
		  $rackContentsRow['ac15'] = array_pop($locationPath);  //
		  $rackContentsRow['ac16'] = array_pop($locationPath);  //
		  $rackContentsRow['ac17'] = array_pop($locationPath);  //
		  $rackContentsRow['ac18'] = array_pop($locationPath);  //
		  $rackContentsRow['ac19'] = array_pop($locationPath);  //
		  $rackContentsRow['ac20'] = array_pop($locationPath);  //
		  $rackContentsRow['ac21'] = array_pop($locationPath);  //
		  $rackContentsRow['ac22'] = array_pop($locationPath);  //
		  $rackContentsRow['ac23'] = array_pop($locationPath);  //
		  $rackContentsRow['ac24'] = array_pop($locationPath);  //
		} else {
		  $count=0;
		  $iteration = +1;
		  array_push($locationPath, $component_type_name);
		  array_push($locationPath, $logical_desc);
		  
		}
      } // end if
    } // end if
} // end while

	  
        $rackContentsEntity = new RackContentsEntity($rackContentsRow['rack_name'], $rackContentsRow['component_type_name'], $rackContentsRow['component_instance_name'], 
		                                             $rackContentsRow['component_type_id'], $rackContentsRow['description'], $rackContentsRow['mfg_name'],
												     $rackContentsRow['logical_desc'], $rackContentsRow['logical_order'], $rackContentsRow['component_id'],
													 $rackContentsRow['sum'], $rackContentsRow['ac1'], $rackContentsRow['ac2'], $rackContentsRow['ac3'], 
													 $rackContentsRow['ac4'], $rackContentsRow['ac5'], $rackContentsRow['ac6'], $rackContentsRow['ac7'],
													 $rackContentsRow['ac8'], $rackContentsRow['ac9'], $rackContentsRow['ac10'], $rackContentsRow['ac11'],
													 $rackContentsRow['ac12'], $rackContentsRow['ac13'], $rackContentsRow['ac14'], $rackContentsRow['ac15'],
													 $rackContentsRow['ac16'], $rackContentsRow['ac17'], $rackContentsRow['ac18'], $rackContentsRow['ac19'],
													 $rackContentsRow['ac20'], $rackContentsRow['ac21'], $rackContentsRow['ac22'], $rackContentsRow['ac23'],
													 $rackContentsRow['ac24']);
		
        $this->rackContentsEntities[$idx] = $rackContentsEntity;
        $idx = $idx +1;
      }
    }
    return true;
  }
}

?>
