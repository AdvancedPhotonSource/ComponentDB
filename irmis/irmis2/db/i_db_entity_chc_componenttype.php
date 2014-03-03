<?php   
/*
 * Defines business class ComponentTypeList which contains an array of ComponentTypeEntity
 * 
 */

class ComponentTypeEntity {
  var $ID;
  var $ctName;
  var $ctDesc;
  var $ctvDesc;
  var $mfgName;
  var $mfgId;
  var $ffId;
  var $ffName;
  var $functionList;
  var $PersonList;
  var $chcPersonList;
  var $DocList;
  var $ComponentList;
  var $IFList;
  var $nrtlStatus;
  var $nrtlAgency;
  var $beamlineInterest;

  function ComponentTypeEntity($ID, $ctName, $ctDesc, $ctvDesc, $mfgName, $mfgId, $ffId, $ffName, $functionList, $PersonList, $chcPersonList, $nrtlStatus, $nrtlAgency, $beamlineInterest) {
    $this->ID = $ID;
    $this->ctName = $ctName;
    $this->ctDesc = $ctDesc;
	$this->ctvDesc = $ctvDesc;
	$this->mfgName = $mfgName;
	$this->mfgId = $mfgId;
	$this->ffId = $ffId;
	$this->ffName = $ffName;
	$this->functionList = $functionList;
	$this->PersonList = $PersonList;
	$this->chcPersonList = $chcPersonList;
	$this->nrtlStatus = $nrtlStatus;
	$this->nrtlAgency = $nrtlAgency;
	$this->beamlineInterest = $beamlineInterest;
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
  function getCtvDesc() {
    return $this->ctvDesc;
  }
  function getMfgName() {
    return $this->mfgName;
  }
  function getMfgId() {
    return $this->mfgId;
  }
  function getFfId() {
    return $this->ffId;
  }
  function getFfName() {
    return $this->ffName;
  }
  function getFunctionList() {
    return $this->functionList;
  }
  function getPersonList() {
    return $this->PersonList;
  }
  function getchcPersonList() {
    return $this->chcPersonList;
  }  
  function getDocList() {
    return $this->DocList;
  } 
  function& getComponentList() {
    return $this->ComponentList;
  }
  function getIFList() {
    return $this->IFList;
  }
  function getnrtlStatus() {
    return $this->nrtlStatus;
  }
  function getnrtlAgency() {
    return $this->nrtlAgency;
  }  
  function getbeamlineInterest() {
    return $this->beamlineInterest;
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
	
	
	$ifList = new IFList();
    $ifList->loadFromDBForIF($dbConn, $this->ID);
	logEntry('debug',"here i am, ifList is: ".print_r($ifList,true));
    $this->IFList = $ifList;
	
    return true;
  }
  
}

/*
 * Represents a list of component types (and some secondary info).
 */
class ComponentTypeList {
  // an array of ComponentTypeEntity
  var $ctEntities;
  
  function ComponentTypeList() {
    $this->ctEntities = array();
  }
  
  // length of full result set
  function length() {
    if ($this->ctEntities == null)
      return 0;
    else 
      return count($this->ctEntities);
  }
  
  // get a particular ComponentTypeEntity by sequential index
  function getElement($idx) {
    return $this->ctEntities[$idx];
  }
  
  // get full result set as an array of ComponentTypeEntity
  function& getArray() {
    return $this->ctEntities;
  }
  
  // get a particular ComponentTypeEntity by component type name
  function findByName($name) {
    foreach ($this->ctEntities as $ctEntity) {
      if ($ctEntity->getCtName() == $name)
        return $ctEntity;    
    }    
    return null;
  }
  
    // Returns getElementForComponentName equal to $ctName	
  function getElementForComponentID($ID) {
    foreach ($this->ctEntities as $ctEntity) {
	  if ($ctEntity->getID() == $ID) {
	    return $ctEntity;
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
   * $ID - component type ID
   */
  function loadFromDB($dbConn, $nameDesc, $mfg, $formFactor, $function, $bl, $person, $chcperson, $ctID) {
    global $errno;
    global $error;
    
    $conn = $dbConn->getConnection();
    $tableNamePrefix = $dbConn->getTableNamePrefix();
    
    // add wildcards to nameDesc
    $nameDesc = '%'.$nameDesc.'%';

    // Begin building query for component_type table
    $qb = new DBQueryBuilder($tableNamePrefix);
    $qb->addColumn("ct.component_type_id");
    $qb->addColumn("ct.component_type_name");
    $qb->addColumn("ct.description");
	$qb->addColumn("ct.vdescription");
	$qb->addColumn("ct.mfg_id");
	$qb->addColumn("ct.form_factor_id");
	$qb->addColumn("mfg.mfg_id");
	$qb->addColumn("mfg.mfg_name");
	$qb->addColumn("ff.form_factor");
	$qb->addColumn("cts.nrtl_status");
	$qb->addColumn("cts.nrtl_agency");
	$qb->addColumn("chcbi.chc_beamline_interest_id");
	$qb->addColumn("chcbi.interest");
    $qb->addTable("component_type ct");
	$qb->addTable("mfg");
	$qb->addTable("form_factor ff");
	$qb->addTable("component_type_status cts");
	$qb->addTable("chc_beamline_interest chcbi");
	$qb->addWhere("ct.mfg_id=mfg.mfg_id");
	$qb->addWhere("ct.form_factor_id=ff.form_factor_id");
	$qb->addWhere("ct.component_type_id=cts.component_type_id");
	$qb->addWhere("(chcbi.chc_beamline_interest_id=ct.chc_beamline_interest_id)");
    
	$qb->addSuffix("order by component_type_name");
    // only add this if given
	if ($bl) {
      $qb->addWhere("chcbi.interest like '".$bl."'"); 
    }
    // only add this if given
	//if ($chcPerson) {
    //  $qb->addWhere("chcbi.interest like '".$chcPerson."'"); 
    //}	
    // only add this if given
	if ($nameDesc) {
      $qb->addWhere("(ct.component_type_name like '".$nameDesc."' or ct.description like '".$nameDesc."' or ct.vdescription like '".$nameDesc."')");
	}
    // only add this if given
	if ($mfg) {
      $qb->addWhere("mfg_name like '".$mfg."'"); 
    }
	
    // only add this if given
	if ($formFactor) {
      $qb->addWhere("form_factor like '".$formFactor."'"); 
    }
	
	// only add this if given
	if ($ctID) {
	  $qb->addWhere("ct.component_type_id = '".$ctID."'");
	}
	
    $ctQuery = $qb->getQueryString();
    
    // logEntry('debug',$ctQuery);
    
    if (!$ctResult = mysql_query($ctQuery, $conn)) {
      $errno = mysql_errno();
      $error = "ComponentTypeList.loadFromDB(): ".mysql_error();
      logEntry('critical',$error);
      return false;                 
    }
    $idx = 0;
    if ($ctResult) {
      while ($ctRow = mysql_fetch_array($ctResult)) {
        $ID = $ctRow['component_type_id'];
        $ctName = $ctRow['component_type_name'];
        $ctDesc = $ctRow['description'];
		$ctvDesc = $ctRow['vdescription'];
		$mfgName = $ctRow['mfg_name'];
		$mfgId = $ctRow['mfg_id'];
		$ffId = $ctRow['form_factor_id'];
		$ffName = $ctRow['form_factor'];
		$nrtlStatus = $ctRow['nrtl_status'];
		$nrtlAgency = $ctRow['nrtl_agency'];
		$beamlineInterest = $ctRow['interest'];

        $skip = false;

        // need to get array of functions for this component type
        $functionList = new functionList();
        if (!$functionList->loadFromDBForComponentTypeID($dbConn, $ID)) {
          logEntry('critical',"Unable to get functions for component type");
          return false;
        }

        if ($function != null) {
          $fEntities = $functionList->getArray();
          $skip = true;
          // check to make sure $function is in fArray
          foreach ($fEntities as $fEntity) {
            if ($fEntity->getFunction() == $function) {
              $skip = false;
			 }
          }
        }
		
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
		
		if ($skip != true) {
			// need to get array of persons for this component type
        	$chcPersonList = new chcPersonList();
        	if (!$chcPersonList->loadFromDBForchcPerson($dbConn, $ID)) {
          		logEntry('critical',"Unable to get persons for component type");
          		return false;
        	}

        	if ($chcperson != null) {
         		$chcPersonEntities = $chcPersonList->getArray();
          		$skip = true;
          		// check to make sure $function is in fArray
          		foreach ($chcPersonEntities as $chcPersonEntity) {
            	  if ($chcPersonEntity->getchcLast() == $chcperson)
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
          $ctEntity = new ComponentTypeEntity($ID, $ctName, $ctDesc, $ctvDesc, $mfgName, $mfgId, $ffId, $ffName,
		                                      $functionList, $PersonList, $chcPersonList, $nrtlStatus, $nrtlAgency, $beamlineInterest);
          $this->ctEntities[$idx++] = $ctEntity;
        }
      }
    }  
    
    return true;
  }
}

?>