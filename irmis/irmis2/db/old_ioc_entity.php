<?php   
/*
 * Defines business class IOCList which contains an array of IOCEntity.
 */

/*
 * IOCEntity corresponds to a single row from the IRMIS ioc table.
 */
class IOCEntity {
  var $iocID;
  var $iocName;
  var $active;
  var $system;
  var $iocResourceEntities;
  
  function IOCEntity($iocID, $iocName, $active, $system) {
    $this->iocID = $iocID;
    $this->iocName = $iocName;
    $this->active = $active;
    $this->system = $system;
  }
  function getIocID() {
    return $this->iocID;
  }
  function getIocName() {
    return $this->iocName;
  }
  function getActive() {
    return $this->active;
  }
  function getSystem() {
    return $this->system;
  }
}


/*
 * IOCList is a business object which contains a collection of information
 * representing the list of all current IOC's in the IRMIS database. It is
 * essentially a wrapper for an array of IOCEntity, along with the standard
 * "loadFromDB()" function and other utility functions.
 */
class IOCList {
  // an array of IOCEntity
  var $iocEntities;

  function IOCList() {
    $this->iocEntities = array();
  }

  function getElement($idx) {
    return $this->iocEntities[$idx];
  }

  // return size of array
  function length() {
    if ($this->iocEntities == null)
      return 0;
    else 
      return count($this->iocEntities);
  }
  
  /*
   * getArray() returns an array of IOCEntity (assuming loadFromDB has
   * been called already).
   */
  function getArray() {
    return $this->iocEntities;
  }
    
  // Returns IOCEntity with primary key $id from ioc table
  function getElementForId($id) {
    foreach ($this->iocEntities as $iocEntity) {
      if ($iocEntity->getIocID() == $id) {
        return $iocEntity;
      }    
    }
    logEntry('debug',"Unable to find IocEntity for id $id");
    return null;
  }

  /*
   * Conducts MySQL db transactions to initialize IOCList from the
   * ioc table. Returns false if db error occurs, true
   * otherwise. */
  function loadFromDB($dbConn, $iocNameConstraint) {
    global $errno;
    global $error;
        
    $conn = $dbConn->getConnection();
    $tableNamePrefix = $dbConn->getTableNamePrefix();
        
    // Get core ioc info from ioc table
    $qb = new DBQueryBuilder($tableNamePrefix);
    $qb->addColumn("ioc_id");
    $qb->addColumn("ioc_nm");
    $qb->addColumn("active");
    $qb->addColumn("system");
    $qb->addTable("ioc");
    // only add this if given
    if ($iocNameConstraint) {
      // convert the * to %
      $iocNameConstraint = str_replace("*","%",$iocNameConstraint);      
      $qb->addWhere("ioc_nm like '".$iocNameConstraint."'");
    }
    $qb->addSuffix("order by ioc_nm");
    $iocQuery = $qb->getQueryString();
    logEntry('debug',$iocQuery);

    if (!$iocResult = mysql_query($iocQuery, $conn)) {
      $errno = mysql_errno();
      $error = "IOCList.loadFromDB(): ".mysql_error();
      logEntry('critical',$error);
      return false;                 
    }

    $idx = 0;
    if ($iocResult) {
      while ($iocRow = mysql_fetch_array($iocResult)) {
        $iocEntity = new IOCEntity($iocRow['ioc_id'], $iocRow['ioc_nm'], $iocRow['active'], $iocRow['system']);
        $this->iocEntities[$idx] = $iocEntity;
        $idx = $idx +1;
      }
    }
    return true;
  }
}

?>
