<?php   
/*
 * Defines business class PersonList which contains an array of PersonEntity
 * 
 */

class IFEntity {
  var $ifType;
  var $required;
  var $presented;
  var $componentRelTypeID;

  function IFEntity($ifType, $required, $presented, $componentRelTypeID) {
	$this->ifType = $ifType;
	$this->required = $required;
	$this->presented = $presented;
	$this->componentRelTypeID = $componentRelTypeID;
  }
  function getIFType() {
    return $this->ifType;
  }
  function getRequired() {
    return $this->required;
  }
  function getPresented() {
    return $this->presented;
  }
  function getComponentRelTypeID() {
    return $this->componentRelTypeID;
  }
}

/*
 * Represents a list of people  (and some secondary info).
 */
class IFList {
  // an array of PersonEntity
  var $ifEntities;
  
  function IFList() {
    $this->ifEntities = array();
  }
  
  // length of full result set
  function length() {
    if ($this->ifEntities == null)
      return 0;
    else 
      return count($this->ifEntities);
  }
  
  // get a particular PersonEntity by sequential index
  function getElement($idx) {
    return $this->ifEntities[$idx];
  }
  
  // get full result set as an array of PersonEntity
  function getArray() {
    return $this->ifEntities;
  }
  
  
   
  function loadFromDBForIF($dbConn, $ID) {
    global $errno;
    global $error;
    
    $conn = $dbConn->getConnection();
    $tableNamePrefix = $dbConn->getTableNamePrefix();
    
    // Begin building query for documentation table
    $qb = new DBQueryBuilder($tableNamePrefix);
	$qb->addColumn("ctit.component_rel_type_id");
	$qb->addColumn("ct.component_type_name");
	$qb->addColumn("crt.rel_name");
	$qb->addColumn("ctit.if_type");
	$qb->addColumn("ct.component_type_id");
	$qb->addColumn("cti.component_type_if_type_id");
	$qb->addColumn("cti.required");
	$qb->addColumn("cti.presented");
	$qb->addTable("component_type ct");
	$qb->addTable("component_type_if_type ctit");
	$qb->addTable("component_type_if cti");
	$qb->addTable("component_rel_type crt");
    $qb->addWhere("ct.component_type_id=".$ID);
	$qb->addWhere("ct.component_type_id=cti.component_type_id");
	$qb->addWhere("ctit.component_type_if_type_id=cti.component_type_if_type_id");
	$qb->addWhere("crt.component_rel_type_id=cti.component_rel_type_id");
	$qb->addSuffix("order by rel_name");
	
    $ifQuery = $qb->getQueryString();
    
    logEntry('debug',$ifQuery);
    
    if (!$ifResult = mysql_query($ifQuery, $conn)) {
      $errno = mysql_errno();
      $error = "IFList.loadFromDBForIF(): ".mysql_error();
      logEntry('critical',$error);
      return false;                 
    }
    $idx = 0;
    if ($ifResult) {
      while ($ifRow = mysql_fetch_array($ifResult)) {
		$ifType = $ifRow['if_type'];
		$required = $ifRow['required'];
		$presented = $ifRow['presented'];
		$componentRelTypeID = $ifRow['component_rel_type_id'];
		logEntry('debug',"here i am, componentRelTypeID is: ".print_r($componentRelTypeID,true));
        $ifEntity = new IFEntity($ifType, $required, $presented, $componentRelTypeID);
        $this->ifEntities[$idx] = $ifEntity;
		$idx = $idx +1;
      }
    }  
    
    return true;
  }
}
?>