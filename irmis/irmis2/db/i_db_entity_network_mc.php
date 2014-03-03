<?php   
/*
 * Defines business class systemList which contains an array of systemEntity.
 */
//logEntry('debug',"from - i_db_entity_switch".print_r($_POST,true));
/*
 * systemEntity corresponds to a single row from the IRMIS ioc table.
 */
class mcEntity {
  var $mc;
  var $mcLocation;
 
  function mcEntity($mc, $mcLocation) {
    $this->mc = $mc;
	$this->mcLocation = $mcLocation;

  }
  function getmc() {
    return $this->mc;
  }
  
  function getmcLocation() {
    return $this->mcLocation;
  }
}

/*
 * systemList is a business object which contains a collection of information
 * representing the list of all current system's in the IRMIS database. It is
 * essentially a wrapper for an array of systemEntity, along with the standard
 * "loadFromDB()" function and other utility functions.
 */
class mcList {
  // an array of swEntity
  var $mcEntities;

  function mcList() {
    $this->mcEntities = array();
  }

  function getElement($idx) {
    return $this->mcEntities[$idx];
  }

  // return size of array
  function length() {
    if ($this->mcEntities == null)
      return 0;
    else 
      return count($this->mcEntities);
  }
  
  /*
   * getArray() returns an array of systemEntity (assuming loadFromDB has
   * been called already).
   */
  function getArray() {
    return $this->mcEntities;
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
/*  $qb->addColumn("distinct PrimEnetMedConvCh"); */
    $qb->addColumn("distinct SecEnetMedConvCh");
	$qb->addColumn("SecEnetSwRackNo");
    $qb->addTable("aps_ioc");
/*      $qb->addWhere("PrimEnetMedConvCh like '%mc%'"); */
	$qb->addWhere("SecEnetMedConvCh like '%mc%'");
/*      $qb->addSuffix("order by PrimEnetMedConvCh"); */
	$qb->addSuffix("order by SecEnetMedConvCh");
    $mcQuery = $qb->getQueryString();
    logEntry('debug',$mcQuery);

    if (!$mcResult = mysql_query($mcQuery, $conn)) {
      $errno = mysql_errno();
      $error = "mcList.loadFromDB(): ".mysql_error();
      logEntry('critical',$error);
      return false;                 
    }

    $idx = 0;
    if ($mcResult) {
      while ($mcRow = mysql_fetch_array($mcResult)) {
/*          $mcName = $mcRow['PrimEnetMedConvCh']; */
	    $mcName = $mcRow['SecEnetMedConvCh'];
/*          $mcLocation = $mcRow['PrimEnetSwRackNo']; */
	    $mcLocation = $mcRow['SecEnetSwRackNo'];
        $mcEntity = new mcEntity($mcName, $mcLocation); 
        $this->mcEntities[$idx] = $mcEntity;
        $idx = $idx +1;
      }
    }
    return true;
  }
}

?>
