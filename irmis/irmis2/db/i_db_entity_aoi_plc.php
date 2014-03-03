<?php
/*
 * Defines business class AOIPLCList which contains an array of AOIPLCEntity.
 */


class AOIPLCEntity {
  var $aoiID;
  var $plcName;
  var $aoiName;

  function AOIPLCEntity(
                     $aoiID,
                     $aoiName,
                     $plcName
                     ) {

    $this->aoiID = $aoiID;
    $this->aoiName = $aoiName;
    $this->plcName = $plcName;
  }
  function getAOIID() {
    return $this->aoiID;
  }
  function getPLCName() {
    return $this->plcName;
  }
  function getAOIName() {
      return $this->aoiName;
  }
}


/*
 * AOIPLCList is a business object which contains a collection of information
 * representing the list of all current PLC's in the IRMIS database for a specific aoi. It is
 * essentially a wrapper for an array of AOIPLCEntity, along with the standard
 * "loadFromDB()" function and other utility functions.
 */
class AOIPLCList {
  // an array of AOIPLCEntity
  var $aoiPlcEntities;

  function AOIPLCList() {
    $this->aoiPlcEntities = array();
  }

  function getElement($idx) {
    return $this->aoiPlcEntities[$idx];
  }

  // return size of array
  function length() {
    if ($this->aoiPlcEntities == null)
      return 0;
    else
      return count($this->aoiPlcEntities);
  }

  /*
   * getArray() returns an array of AOIPlcEntity (assuming loadFromDB has
   * been called already).
   */
  function getArray() {
    return $this->aoiPlcEntities;
  }

  // Returns getElementForAOIName equal to $aoiName

  function getElementForAOIName($aoiName) {
       foreach ($this->aoiPlcEntities as $aoiPlcEntity) {
	  if ($aoiPlcEntity->getAOIName() == $aoiName) {
	    return $aoiPlcEntity;
	  }
	 }
	 logEntry('debug',"Unable to find aoiPlcEntity for aoiName $aoiName");
	 return null;
  }

  /*
   * Conducts MySQL db transactions to initialize AOIPLCList from the
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
    $qb->addColumn("component.component_instance_name as plc_name");

    $qb->addTable("aoi");
    $qb->addTable("plc");
    $qb->addTable("aoi_plc_stcmd_line");
    $qb->addTable("component");
    $qb->addWhere("aoi.aoi_id = '".$aoiIDConstraint."'");
    $qb->addWhere("aoi_plc_stcmd_line.aoi_id = aoi.aoi_id and aoi_plc_stcmd_line.plc_id = plc.plc_id and plc.component_id = component.component_id");

    $aoiQuery = $qb->getQueryString();
    logEntry('debug',$aoiQuery);

    if (!$aoiResult = mysql_query($aoiQuery, $conn)) {
      $errno = mysql_errno();
      $error = "AOIPLCList.loadFromDB(): ".mysql_error();
      logEntry('critical',$error);
      return false;
    }

    $idx = 0;
    if ($aoiResult) {
      while ($aoiRow = mysql_fetch_array($aoiResult)) {

        $aoiPlcEntity = new AOIPLCEntity($aoiRow['aoi_id'], $aoiRow['aoi_name'], $aoiRow['plc_name']);

        $this->aoiPlcEntities[$idx] = $aoiPlcEntity;
        $idx = $idx +1;
      }
    }
    return true;
  }
}

?>