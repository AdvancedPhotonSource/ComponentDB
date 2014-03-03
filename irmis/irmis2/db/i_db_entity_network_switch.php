<?php   
/*
 * Defines business class systemList which contains an array of systemEntity.
 */
logEntry('debug',"from - i_db_entity_switch".print_r($_POST,true));
/*
 * systemEntity corresponds to a single row from the IRMIS ioc table.
 */
class swEntity {
  var $sw;
  var $swLocation;
 
  function swEntity($sw, $swLocation) {
    $this->sw = $sw;
	$this->swLocation = $swLocation;

  }
  function getsw() {
    return $this->sw;
  }
  
  function getswLocation() {
    return $this->swLocation;
  }
}

/*
 * systemList is a business object which contains a collection of information
 * representing the list of all current system's in the IRMIS database. It is
 * essentially a wrapper for an array of systemEntity, along with the standard
 * "loadFromDB()" function and other utility functions.
 */
class swList {
  // an array of swEntity
  var $swEntities;

  function swList() {
    $this->swEntities = array();
  }

  function getElement($idx) {
    return $this->swEntities[$idx];
  }

  // return size of array
  function length() {
    if ($this->swEntities == null)
      return 0;
    else 
      return count($this->swEntities);
  }
  
  /*
   * getArray() returns an array of systemEntity (assuming loadFromDB has
   * been called already).
   */
  function getArray() {
    return $this->swEntities;
  }
    

  /*
   * Conducts MySQL db transactions to initialize systemList from the
   * ioc table. Returns false if db error occurs, true
   * otherwise. */
  function loadFromDB($dbConn) {
    global $errno;
    global $error;
        
    $conn = $dbConn->getConnection();
    $tableNamePrefix = $dbConn->getTableNamePrefix();
        
    // Get core ioc info from ioc table
    $qb = new DBQueryBuilder($tableNamePrefix);
    $qb->addColumn("distinct PrimEnetSwitch");
	$qb->addColumn("PrimEnetSwRackNo");
    $qb->addTable("aps_ioc");
	$qb->addWhere("PrimEnetSwitch like '%hub%' or PrimEnetSwitch like 'FESwitch%' or PrimEnetSwitch like 'sw%'");
	$qb->addSuffix("group by PrimEnetSwitch");
	$qb->addSuffix("order by PrimEnetSwitch");
    $swQuery = $qb->getQueryString();
    logEntry('debug',$swQuery);

    if (!$swResult = mysql_query($swQuery, $conn)) {
      $errno = mysql_errno();
      $error = "swList.loadFromDB(): ".mysql_error();
      logEntry('critical',$error);
      return false;                 
    }

    $idx = 0;
    if ($swResult) {
      while ($swRow = mysql_fetch_array($swResult)) {
	    $swName = $swRow['PrimEnetSwitch'];
		$swLocation = $swRow['PrimEnetSwRackNo'];
        $swEntity = new swEntity($swName, $swLocation); 
        $this->swEntities[$idx] = $swEntity;
        $idx = $idx +1;
      }
    }
    return true;
  }
}

?>
