<?php
/*
 * Defines business class AOIPVList which contains an array of AOIPVEntity.
 */


class AOIPVEntity {
  var $aoiID;
  var $aoiName;
  var $recordName;
  var $iocName;

  function AOIPVEntity(
                     $aoiID,
                     $aoiName,
			   		 $recordName,
                     $iocStCmdLine,
			         $iocName,
			         $pvFilter
                     ) {

    $this->aoiID = $aoiID;
    $this->aoiName = $aoiName;
    $this->recordName = $recordName;
    $this->iocStCmdLine = $iocStCmdLine;
    $this->iocName = $iocName;
    $this->pvFilter = $pvFilter;

  }
  function getAOIID() {
    return $this->aoiID;
  }
  function getAOIName() {
    return $this->aoiName;
  }

  function getRecordName() {
    return $this->recordName;
  }
  function getIOCStCmdLine() {
    return $this->iocStCmdLine;
  }
  function getIOCName() {
    return $this->iocName;
  }
  function getPVFilter() {
  	return $this->pvFilter;
  }
}


/*
 * AOIPVList is a business object which contains a collection of information
 * representing the list of all current PV's in the IRMIS database for a specific aoi. It is
 * essentially a wrapper for an array of AOIEntity, along with the standard
 * "loadFromDB()" function and other utility functions.
 */
class AOIPVList {
  // an array of AOIPVEntity
  var $aoiPvEntities;

  function AOIPVList() {
    $this->aoiPvEntities = array();
  }

  function getElement($idx) {
    return $this->aoiPvEntities[$idx];
  }

  // return size of array
  function length() {
    if ($this->aoiPvEntities == null)
      return 0;
    else
      return count($this->aoiPvEntities);
  }

  /*
   * getArray() returns an array of AOIPvEntity (assuming loadFromDB has
   * been called already).
   */
  function getArray() {
    return $this->aoiPvEntities;
  }

  // Returns getElementForAOIName equal to $aoiName

  function getElementForAOIName($aoiName) {
       foreach ($this->aoiPvEntities as $aoiPvEntity) {
	  if ($aoiPvEntity->getAOIName() == $aoiName) {
	    return $aoiPvEntity;
	  }
	 }
	 logEntry('debug',"Unable to find aoiEntity for aoiName $aoiName");
	 return null;
  }

  /*
   * Conducts MySQL db transactions to initialize AOIPVList from the
   * device table. Returns false if db error occurs, true
   * otherwise. */

  function loadFromDB($dbConn, $aoiNameConstraint) {
    global $errno;
    global $error;

    $conn = $dbConn->getConnection();
    $tableNamePrefix = $dbConn->getTableNamePrefix();

    // Get core aoi info from aoi table
    $qb = new DBQueryBuilder($tableNamePrefix);
    $qb->addColumn("aoi.aoi_id");
    $qb->addColumn("aoi.aoi_name");
    $qb->addColumn("aoi_epics_record.rec_nm");
    $qb->addColumn("ioc_stcmd_line.ioc_stcmd_line");
    $qb->addColumn("ioc.ioc_nm");
    $qb->addColumn("aoi_ioc_stcmd_line.pv_filter");

    $qb->addTable("aoi");
    $qb->addTable("aoi_epics_record");
    $qb->addTable("aoi_ioc_stcmd_line");
    $qb->addTable("ioc_stcmd_line");
    $qb->addTable("ioc");
    $qb->addWhere("aoi.aoi_id = aoi_ioc_stcmd_line.aoi_id and aoi_ioc_stcmd_line.ioc_stcmd_line_id = ioc_stcmd_line.ioc_stcmd_line_id");
    $qb->addWhere("aoi_epics_record.aoi_ioc_stcmd_line_id = aoi_ioc_stcmd_line.aoi_ioc_stcmd_line_id");
    $qb->addWhere("ioc_stcmd_line.ioc_id = ioc.ioc_id");

    $my_aoi_id = $_SESSION['aoi_id'];

    // echo("aoi name is: " .$my_aoi_name." ");

    if ($my_aoi_id) {
      $qb->addWhere("aoi.aoi_id = $my_aoi_id");
    }


    $aoiQuery = $qb->getQueryString();
    logEntry('debug',$aoiQuery);

    if (!$aoiResult = mysql_query($aoiQuery, $conn)) {
      $errno = mysql_errno();
      $error = "AOIList.loadFromDB(): ".mysql_error();
      logEntry('critical',$error);
      return false;
    }

    $idx = 0;
    $aoi_rec_nm_previous = "";

    if ($aoiResult) {
      while ($aoiRow = mysql_fetch_array($aoiResult)) {
        // filter out duplicate pv's
        if ($aoiRow['rec_nm'] != $aoi_rec_nm_previous){
        	$aoiPvEntity = new AOIPVEntity($aoiRow['aoi_id'], $aoiRow['aoi_name'], $aoiRow['rec_nm'], $aoiRow['ioc_stcmd_line'], $aoiRow['ioc_nm'], $aoiRow['pv_filter']);

        	$this->aoiPvEntities[$idx] = $aoiPvEntity;
        	$idx = $idx +1;
        	$aoi_rec_nm_previous = $aoiRow['rec_nm'];
        }
      }
    }
    return true;
  }
}

?>