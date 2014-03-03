<?php   
/*
 * Defines business class PLCRecList which contains an array of PlcRecEntity.
 */

class PlcRecEntity {
  var $recName;
  var $recType;
  var $lnkVal;
  
  function PlcRecEntity($recName, $recType, $lnkVal) {
    $this->recName = $recName;
    $this->recType = $recType;
    $this->lnkVal = $lnkVal;
  }
  function getRecName() {
    return $this->recName;
  }
  function getRecType() {
    return $this->recType;
  }
  function getLnkVal() {
    return $this->lnkVal;
  }
}

/*
 * PLCRecList is a business object which contains a collection of information
 * representing the list of records belonging to a particular PLC.
 */
class PLCRecList {
  // an array of PlcRecEntity
  var $plcRecEntities;

  function PLCRecList() {
    $this->plcRecEntities = array();
  }

  function getElement($idx) {
    return $this->plcRecEntities[$idx];
  }

  // return size of array
  function length() {
    if ($this->plcRecEntities == null)
      return 0;
    else 
      return count($this->plcRecEntities);
  }
  
  /*
   * getArray() returns an array of PlcRecEntity (assuming loadFromDB has
   * been called already).
   */
  function getArray() {
    return $this->plcRecEntities;
  }
  
  // Returns getElementForPlcName equal to $recName	
  function getElementForRecName($recName) {
    foreach ($this->plcRecEntities as $PlcRecEntity) {
      if ($PlcRecEntity->getRecName() == $recName) {
        return $PlcRecEntity;
      }
    }
    logEntry('debug',"Unable to find PlcRecEntity for recName $recName");
    return null;
  }
    
  /*
   * Conducts MySQL db transactions to initialize PLCRecList from the
   * device table. Returns false if db error occurs, true
   * otherwise. */
  function loadFromDB($dbConn, $iocNameConstraint, $lnkConstraint) {
    global $errno;
    global $error;
        
    $conn = $dbConn->getConnection();
    $tableNamePrefix = $dbConn->getTableNamePrefix();
        
    // Get core plc info from device table
    $qb = new DBQueryBuilder($tableNamePrefix);
    $qb->addColumn("rec.rec_nm");
    $qb->addColumn("rec_type.rec_type");
    $qb->addColumn("fld.fld_val");
    $qb->addTable("rec");
    $qb->addTable("rec_type");
    $qb->addTable("fld");
    $qb->addTable("fld_type");
    $qb->addTable("ioc");
    $qb->addTable("ioc_boot");
    $qb->addWhere("ioc.ioc_nm = '".$iocNameConstraint."'");
    $qb->addWhere("fld.fld_val like '%".$lnkConstraint."%'");
    $qb->addWhere("(fld_type.fld_type = 'INP' or fld_type.fld_type = 'OUT')");
    $qb->addWhere("rec.ioc_boot_id = ioc_boot.ioc_boot_id");
    $qb->addWhere("ioc_boot.ioc_id = ioc.ioc_id");
    $qb->addWhere("ioc_boot.current_load = 1");
    $qb->addWhere("rec.rec_id = fld.rec_id");
    $qb->addWhere("rec.rec_type_id = rec_type.rec_type_id");
    $qb->addWhere("fld.fld_type_id = fld_type.fld_type_id");
    $qb->addSuffix("order by fld_val limit 500");
    
    $plcRecQuery = $qb->getQueryString();
    // logEntry('debug',$plcRecQuery);

    if (!$plcRecResult = mysql_query($plcRecQuery, $conn)) {
      $errno = mysql_errno();
      $error = "PLCRecList.loadFromDB(): ".mysql_error();
      logEntry('critical',$error);
      return false;                 
    }

    $idx = 0;
    if ($plcRecResult) {
      while ($plcRecRow = mysql_fetch_array($plcRecResult)) {
        $PlcRecEntity = new PlcRecEntity($plcRecRow['rec_nm'], $plcRecRow['rec_type'], $plcRecRow['fld_val']);
        $this->plcRecEntities[$idx] = $PlcRecEntity;
        $idx = $idx +1;
      }
    }
    return true;
  }
}

?>
