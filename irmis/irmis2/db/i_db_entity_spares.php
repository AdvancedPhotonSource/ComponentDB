<?php   
/*
 * Defines business class SparesList which contains an array of SparesEntity
 * 
 */

class SpareEntity {
  var $ID;
  var $ctName;
  var $ctDesc;
  var $mfgName;
  var $mfgId;
  var $insQty;
  var $spareQty;
  var $spareLoc;
  var $stockQty;
  var $PersonList;
  var $ComponentList;

  function SpareEntity($ID, $ctName, $ctDesc, $mfgName, $mfgId, $insQty, $spareQty, $spareLoc, $stockQty, $PersonList) {
    $this->ID = $ID;
    $this->ctName = $ctName;
    $this->ctDesc = $ctDesc;
	$this->mfgName = $mfgName;
	$this->mfgId = $mfgId;
	$this->insQty = $insQty;
	$this->spareQty = $spareQty;
	$this->spareLoc = $spareLoc;
	$this->stockQty = $stockQty;
	$this->PersonList = $PersonList;
  }
  function getID() {
    return $this->ID;
  }
  function getCtName() {
    return $this->ctName;
  }
  function getCtDesc() {
    return $this->ctDesc;
  }
  function getMfgName() {
    return $this->mfgName;
  }
  function getMfgId() {
    return $this->mfgId;
  }
  function getInsQty() {
    return $this->insQty;
  }
  function getSpareQty() {
    return $this->spareQty;
  }
  function getSpareLoc() {
    return $this->spareLoc;
  }
  function getStockQty() {
    return $this->stockQty;
  }
  function getPersonList() {
    return $this->PersonList;
  }
  
  //function getDocList() {
  //  return $this->DocList;
  //} 
  function getComponentList() {
    return $this->ComponentList;
  }
  

  /*
   * Load component type details for this component type.
   * Returns false if db error occurs, true otherwise.
   * 
   * $dbConn - db connection identifier
   */
  function loadDetailsFromDB($dbConn) {
    global $errno;
    global $error;
    
    $dList = new DocList();
    $dList->loadFromDBForDoc($dbConn, $this->ID);
	logEntry('debug',"here i am, dList is: ".print_r($dList,true));
    $this->DocList = $dList;

    
	$cList = new ComponentList();
    $cList->loadFromDBForComponent($dbConn, $this->ID);
	logEntry('debug',"here i am, cList is: ".print_r($cList,true));
    $this->ComponentList = $cList;
	
	/*
	$ifList = new IFList();
    $ifList->loadFromDBForIF($dbConn, $this->ID);
	logEntry('debug',"here i am, ifList is: ".print_r($ifList,true));
    $this->IFList = $ifList;
	*/
    return true;
  }
  
}

/*
 * Represents a list of component types (and some secondary info).
 */
class SpareList {
  // an array of SparesEntity
  var $spEntities;
  
  function SpareList() {
    $this->spEntities = array();
  }
  
  // length of full result set
  function length() {
    if ($this->spEntities == null)
      return 0;
    else 
      return count($this->spEntities);
  }
  
  // get a particular SparesEntity by sequential index
  function getElement($idx) {
    return $this->spEntities[$idx];
  }
  
  // get full result set as an array of SparesEntity
  function getArray() {
    return $this->spEntities;
  }
  
  // get a particular SparesEntity by component type name
  function findByName($name) {
    foreach ($this->spEntities as $spEntity) {
      if ($spEntity->getSpName() == $name)
        return $spEntity;    
    }    
    return null;
  }
  
    // Returns getElementForSparesName equal to $ctName	
  function getElementForSpareID($ID) {
    foreach ($this->spEntities as $spEntity) {
	  if ($spEntity->getID() == $ID) {
	    return $spEntity;
	  }
	 }
	 logEntry('debug',"Unable to find CtEntity for ID $ID");
	 return null;
 }

  /*
   * Load this object from the database, using constraints supplied in
   * the arguments. Note that null argument means "all". Returns
   * false if db error occurs, true otherwise.
   * 
   * $dbConn - db connection identifier
   * $nameDesc - component type name or description
   * $mfg - manufacturer
   * $formFactor - form factor
   * $function - function name (just one)
   */
  function loadFromDB($dbConn,$nameDesc,$mfg,$person) {
    global $errno;
    global $error;
    
    $conn = $dbConn->getConnection();
    $tableNamePrefix = $dbConn->getTableNamePrefix();
    
    // add wildcards to nameDesc
    $nameDesc = '%'.$nameDesc.'%';
	$noSpare = $_SESSION['noSpare'];
	$criticalSpare = $_SESSION['criticalSpare'];
	$spareLocation = $_SESSION['spareLocation'];
    // Begin building query for component_type table
    $qb = new DBQueryBuilder($tableNamePrefix);
    $qb->addColumn("ct.component_type_id");
    $qb->addColumn("count(c.component_id) as component_id");
    $qb->addColumn("ct.component_type_name");
	$qb->addColumn("ct.description");
	$qb->addColumn("cts.spare_qty");
	$qb->addColumn("cts.spare_loc");
	$qb->addColumn("cts.stock_qty");
	$qb->addColumn("mfg.mfg_name");
	#$qb->addColumn("ctf.function_id");
    $qb->addTable("component_type ct left join component c using (component_type_id)");
	$qb->addTable("component_type_status cts");
	$qb->addTable("mfg");
	#$qb->addTable("component_type_function ctf");
	$qb->addTable("component_rel cr");
	$qb->addWhere("cts.component_type_id = ct.component_type_id");
	#$qb->addWhere("ctf.component_type_id = ct.component_type_id");
	$qb->addWhere("mfg.mfg_id = ct.mfg_id");
	$qb->addWhere("cr.mark_for_delete=0");
	$qb->addWhere("cr.child_component_id = c.component_id");
	$qb->addWhere("cr.component_rel_type_id = '2'");
	//$qb->addWhere("((c.mark_for_delete = '0')||(c.mark_for_delete is NULL))");
    
	$qb->addSuffix("group by component_type_name");
	$qb->addSuffix("order by component_type_name");
    // only add this if given
	if ($nameDesc) {
      $qb->addWhere("(ct.component_type_name like '".$nameDesc."' or ct.description like '".$nameDesc."')");
	}
    // only add this if given
	if ($mfg) {
      $qb->addWhere("mfg_name like '".$mfg."'"); 
    }
	// only add this if given
	if (isset($noSpare)) {
	  $qb->addWhere("spare_qty = '0'");
	}
	
	// only add this if given
	if (isset($criticalSpare)) {
	  $qb->addTable("component_type_function ctf");
	  $qb->addWhere("ctf.component_type_id = ct.component_type_id");
	  $qb->addWhere("ctf.function_id = '49'");
	}	

	// only add this if given
	if (isset($spareLocation)) {
	  $qb->addWhere("cts.spare_loc like '%spares cage%'");
	}		

    // only add this if given
	//if ($formFactor) {
    //  $qb->addWhere("form_factor like '".$formFactor."'"); 
    //}
	
    $spQuery = $qb->getQueryString();
    
    // logEntry('debug',$ctQuery);
    
    if (!$spResult = mysql_query($spQuery, $conn)) {
      $errno = mysql_errno();
      $error = "SpareList.loadFromDB(): ".mysql_error();
      logEntry('critical',$error);
      return false;                 
    }
    $idx = 0;
    if ($spResult) {
      while ($spRow = mysql_fetch_array($spResult)) {
        $ID = $spRow['component_type_id'];
        $ctName = $spRow['component_type_name'];
        $ctDesc = $spRow['description'];
		$mfgName = $spRow['mfg_name'];
		$mfgId = $spRow['mfg_id'];
		$insQty = $spRow['component_id'];
		$spareQty = $spRow['spare_qty'];
		$spareLoc = $spRow['spare_loc'];
		$stockQty = $spRow['stock_qty'];
		

        $skip = false;

        // need to get array of functions for this component type
        //$functionList = new functionList();
        //if (!$functionList->loadFromDBForComponentTypeID($dbConn, $ID)) {
        //  logEntry('critical',"Unable to get functions for component type");
        //  return false;
        //}

        //if ($function != null) {
        //  $fEntities = $functionList->getArray();
        //  $skip = true;
          // check to make sure $function is in fArray
        //  foreach ($fEntities as $fEntity) {
        //    if ($fEntity->getFunction() == $function) {
        //      $skip = false;
		//	 }
        //  }
        //}
		
		if ($skip != true) {
			// need to get array of persons for this component type
        	$PersonList = new PersonList();
        	if (!$PersonList->loadFromDBForPerson($dbConn, $ID)) {
          		logEntry('critical',"Unable to get persons for component type");
          		return false;
        	}

        	if ($person != null) {
         		$pEntities = $PersonList->getArray();
          		$skip = true;
          		// check to make sure $function is in fArray
          		foreach ($pEntities as $pEntity) {
            	  if ($pEntity->getLast() == $person)
             	    $skip = false;
          		}
        	}
		}
		
/*
        if ($skip != true) {
			// need to get array of documents for this component type
        	$DocList = new DocList();
        	if (!$DocList->loadFromDBForDoc($dbConn, $ID)) {
          		logEntry('critical',"Unable to get docs for component type");
          		return false;
        	}

		}
	  
	    if ($skip != true) {
			// need to get array of documents for this component type
        	$ComponentList = new ComponentList();
        	if (!$ComponentList->loadFromDBForComponent($dbConn, $ID)) {
          		logEntry('critical',"Unable to get component info");
          		return false;
        	}

		}
*/	  
        if ($skip == false) {
          $spEntity = new SpareEntity($ID, $ctName, $ctDesc, $mfgName, $mfgId, $insQty, $spareQty,
		                                      $spareLoc, $stockQty, $PersonList);
          $this->spEntities[$idx++] = $spEntity;
        }
      }
    }
    
    return true;
  }
}

?>