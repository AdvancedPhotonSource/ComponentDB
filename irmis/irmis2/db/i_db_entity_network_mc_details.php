<?php   
/*
 * Defines business class mcList which contains an array of mcEntity.
 */

/*
 * mcDetailsEntity corresponds to a single row from the IRMIS aps_ioc table.
 */
class mcDetailsEntity {
  var $mcPrimEnetMedConvCh;
  //var $mcPrimMediaConvPort;
  var $mcSecEnetMedConvCh;
  //var $mcSecMedConvPort;
  var $mcModule;
  var $mcConnection;
  var $mcID;
 
  function mcDetailsEntity($mcPrimEnetMedConvCh, $mcSecEnetMedConvCh, $mcModule, 
						   $mcConnection) {
	$this->mcPrimEnetMedConvCh = $mcPrimEnetMedConvCh;
	//$this->mcPrimMediaConvPort = $mcPrimMediaConvPort;
	$this->mcSecEnetMedConvCh = $mcSecEnetMedConvCh;
	//$this->mcSecMedConvPort = $mcSecMedConvPort;
	$this->mcModule = $mcModule;
	$this->mcConnection = $mcConnection;
	$this->mcID = $mcID;

  }
  
  function getmcPrimEnetMedConvCh() {
    return $this->mcPrimEnetMedConvCh;
  }	
  
  function getmcPrimMediaConvPort() {
    return $this->mcPrimMediaConvPort;
  }
    
  function getmcSecEnetMedConvCh() {
    return $this->mcSecEnetMedConvCh;
  }
  
  function getmcSecMedConvPort() {
    return $this->mcSecMedConvPort;
  }
  
  function getmcConnection() {
    return $this->mcConnection;
  }
  
  function getmcID() {
    return $this->mcID;
  }
  
  function getmcModule() {
    return $this->mcModule;
  }

}

/*
 * mcList is a business object which contains a collection of information
 * representing the list of all current system's in the IRMIS database. It is
 * essentially a wrapper for an array of systemEntity, along with the standard
 * "loadFromDB()" function and other utility functions.
 */
class mcDetailsList {
  // an array of mcEntity
  var $mcDetailsEntities;

  function mcDetailsList() {
    $this->mcDetailsEntities = array();
  }

  function getElement($idx) {
    return $this->mcDetailsEntities[$idx];
  }

  // return size of array
  function length() {
    if ($this->mcDetailsEntities == null)
      return 0;
    else 
      return count($this->mcDetailsEntities);
  }
  
  /*
   * getArray() returns an array of mcEntity (assuming loadFromDB has
   * been called already).
   */
  function getArray() {
    return $this->mcDetailsEntities;
  }
    
  /*
   * Conducts MySQL db transactions to initialize mcList from the
   * aps_ioc table. Returns false if db error occurs, true
   * otherwise. */
  function loadFromDB($dbConn, $mcID) {
    global $errno;
    global $error;
        
    $conn = $dbConn->getConnection();
    $tableNamePrefix = $dbConn->getTableNamePrefix();
        
    // Get core ioc info from ioc table
    //$qb = new DBQueryBuilder($tableNamePrefix);
	//$qb->addColumn("PrimEnetMedConvCh");
	//$qb->addColumn("PrimMediaConvPort");
	//$qb->addColumn("SecEnetMedConvCh");
	//$qb->addColumn("SecMedConvPort");
	//$qb->addColumn("ioc_nm");
    //$qb->addTable("aps_ioc left join ioc using (ioc_id)");
	//$qb->addWhere("PrimEnetMedConvCh like '". $mcID."' || SecEnetMedConvCh like '". $mcID."'");
	//$qb->addSuffix("order by PrimMediaConvPort || SecMedConvPort");
	
	$mcDetailsQuery = ("(select PrimEnetMedConvCh, 
	                     SecEnetMedConvCh, 
						 PrimMediaConvPort as module, 
						 ioc_nm 
	                     from aps_ioc left join ioc using (ioc_id) 
						 where PrimEnetMedConvCh like '". $mcID."') 
						 union 
						(select PrimEnetMedConvCh, 
						 SecEnetMedConvCh, 
						 SecMedConvPort as module, 
						 ioc_nm 
						 from aps_ioc left join ioc using (ioc_id) 
						 where SecEnetMedConvCh like '". $mcID."') 
						 order by module;");
	
	
    //$mcDetailsQuery = $qb->getQueryString();
    logEntry('debug',$mcDetailsQuery);

    if (!$mcDetailsResult = mysql_query($mcDetailsQuery, $conn)) {
      $errno = mysql_errno();
      $error = "mcDetailsList.loadFromDB(): ".mysql_error();
      logEntry('critical',$error);
      return false;                 
    }

    $idx = 0;
    if ($mcDetailsResult) {
      while ($mcDetailsRow = mysql_fetch_array($mcDetailsResult)) {
		$mcPrimEnetMedConvCh = $mcDetailsRow['PrimEnetMedConvCh'];
		//$mcPrimMediaConvPort = $mcDetailsRow['PrimMediaConvPort'];
		$mcSecEnetMedConvCh = $mcDetailsRow['SecEnetMedConvCh'];
		//$mcSecMedConvPort = $mcDetailsRow['SecMedConvPort'];
		$mcConnection = $mcDetailsRow['ioc_nm'];
		$mcModule = $mcDetailsRow['module'];
		//$swID = $swDetailsRow['PrimEnetSwitch || SecEnetSwitch'];
		
        $mcDetailsEntity = new mcDetailsEntity($mcPrimEnetMedConvCh, $mcSecEnetMedConvCh, $mcModule,
											   $mcConnection); 
        $this->mcDetailsEntities[$idx] = $mcDetailsEntity;
        $idx = $idx +1;
      }
    }
    return true;
  }
}

?>
