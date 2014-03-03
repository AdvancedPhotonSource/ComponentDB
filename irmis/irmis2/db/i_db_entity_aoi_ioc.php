<?php
/*
 * Defines business class AOIIOCList which contains an array of AOIIOCEntity.
 */


class AOIIOCEntity {
  var $aoiID;
  var $iocName;
  var $aoiName;

  function AOIIOCEntity(
                     $aoiID,
                     $aoiName,
                     $iocName
                     ) {

    $this->aoiID = $aoiID;
    $this->aoiName = $aoiName;
    $this->iocName = $iocName;
  }
  function getAOIID() {
    return $this->aoiID;
  }
  function getIOCName() {
    return $this->iocName;
  }
  function getAOIName() {
      return $this->aoiName;
  }
}


/*
 * AOIIOCList is a business object which contains a collection of information
 * representing the list of all current IOC's in the IRMIS database for a specific aoi. It is
 * essentially a wrapper for an array of AOIIOCEntity, along with the standard
 * "loadFromDB()" function and other utility functions.
 */
class AOIIOCList {
  // an array of AOIIOCEntity
  var $aoiIocEntities;

  function AOIIOCList() {
    $this->aoiIocEntities = array();
  }

  function getElement($idx) {
    return $this->aoiIocEntities[$idx];
  }

  // return size of array
  function length() {
    if ($this->aoiIocEntities == null)
      return 0;
    else
      return count($this->aoiIocEntities);
  }

  /*
   * getArray() returns an array of AOIIocEntity (assuming loadFromDB has
   * been called already).
   */
  function getArray() {
    return $this->aoiIocEntities;
  }

  // Returns getElementForAOIName equal to $aoiName

  function getElementForAOIName($aoiName) {
       foreach ($this->aoiIocEntities as $aoiIocEntity) {
	  if ($aoiIocEntity->getAOIName() == $aoiName) {
	    return $aoiIocEntity;
	  }
	 }
	 logEntry('debug',"Unable to find aoiIocEntity for aoiName $aoiName");
	 return null;
  }

  /*
   * Conducts MySQL db transactions to initialize AOIIOCList from the
   * aoi table. Returns false if db error occurs, true
   * otherwise. */

  function loadFromDB($dbConn, $aoiIDConstraint) {
    global $errno;
    global $error;

    $conn = $dbConn->getConnection();
    $tableNamePrefix = $dbConn->getTableNamePrefix();

    // Get core aoi info from aoi table
    $qb = new DBQueryBuilder($tableNamePrefix);

    $qb->addDistinct(true);

    $qb->addColumn("aoi.aoi_id");
    $qb->addColumn("aoi.aoi_name");
    $qb->addColumn("ioc.ioc_nm");

    $qb->addTable("aoi");
    $qb->addTable("ioc");
    $qb->addTable("aoi_ioc_stcmd_line");
    $qb->addTable("ioc_stcmd_line");
    $qb->addWhere("aoi.aoi_id = '".$aoiIDConstraint."'");
    $qb->addWhere("aoi_ioc_stcmd_line.aoi_id = aoi.aoi_id and aoi_ioc_stcmd_line.ioc_stcmd_line_id = ioc_stcmd_line.ioc_stcmd_line_id");
    $qb->addWhere("ioc_stcmd_line.ioc_id = ioc.ioc_id");

    $aoiQuery = $qb->getQueryString();
    logEntry('debug',$aoiQuery);

    if (!$aoiResult = mysql_query($aoiQuery, $conn)) {
      $errno = mysql_errno();
      $error = "AOIIOCList.loadFromDB(): ".mysql_error();
      logEntry('critical',$error);
      return false;
    }

    $idx = 0;

    if ($aoiResult) {
      while ($aoiRow = mysql_fetch_array($aoiResult)) {

        	$aoiIocEntity = new AOIIOCEntity($aoiRow['aoi_id'], $aoiRow['aoi_name'], $aoiRow['ioc_nm']);

        	$this->aoiIocEntities[$idx] = $aoiIocEntity;
        	$idx = $idx +1;

      }
    }
    return true;
  }
}

?>
