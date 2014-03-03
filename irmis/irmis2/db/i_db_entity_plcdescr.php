<?php   

class PlcDesc {
  var $plcDescEntity;

  function PlcDesc() {
    $this->plcDescEntity = array();
  }
  function getPlcDesc() {
    return $this->plcDescEntity;
  }
  function setPlcDesc($desc) {
    $this->plcDescEntity=$desc;
  }
  /*
   * Conducts MySQL db transactions to initialize PlcDesc from the
   * plc table. Returns false if db error occurs, true
   * otherwise. */

  function loadFromDB($dbConn, $plcIDConstraint) {
    global $errno;
    global $error;
        
    $conn = $dbConn->getConnection();
    $tableNamePrefix = $dbConn->getTableNamePrefix();
        
    // Get plc id from device table
    $qb = new DBQueryBuilder($tableNamePrefix);
    
    $qb->addColumn('plc_description');
    $qb->addTable('plc');
    $qb->addWhere('component_id = "'.$plcIDConstraint.'"');
    $descQuery = $qb->getQueryString();

    $descResult = mysql_query($descQuery, $conn);
    if ($descResult) {
      while ($descEntity = mysql_fetch_array($descResult)) {
         $this->plcDescEntity = $descEntity[0];
      }
    }
    logEntry('debug','PLC: '.$plcIDConstraint.': '.$descEntity[0]);
    return true;
  }

  function insertDB($dbConn, $plcIDConstraint) {
    global $errno;
    global $error;
      
    $conn = $dbConn->getConnection();
    if ($conn){
      // Look for PLC name. If it doesn't exist, create entry
      $plc_query = "select plc_id from plc where component_id = '".$plcIDConstraint."'";
      $result = mysql_query($plc_query,$conn);
      if ($result) {
        while ($descEntity = mysql_fetch_array($result)) {
           $plc_id = $descEntity[0];
        }
      }
      if (!$plc_id){
        logEntry('debug','PLC not found in plc table, creating PLC entry');	 
        $plc_add = "insert into plc (plc_id, component_id) values (NULL, '".$plcIDConstraint."')";
        $result = mysql_query($plc_add, $conn);
        if (!result)
          logEntry('debug','PLC new entry failed !');	 
        else 
          logEntry('debug','created PLC entry for '.$plcIDConstraint);
      }
    }
    return true;
  }

  function updateDB($dbConn, $plcIDConstraint) {
    global $errno;
    global $error;
        
    $conn = $dbConn->getConnection();
    $desc_update="update plc set plc_description = '".$this->plcDescEntity."'
    where component_id = '".$plcIDConstraint."'";
    $result = mysql_query($desc_update, $conn);

//    if ($result) DO SOMETHING

    logEntry('debug','updated PLC description: '.$plcIDConstraint);
    return true;
  }
}

?>
