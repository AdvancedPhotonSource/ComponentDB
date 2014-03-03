<?php
/*
 * Defines business class AOIStCmdLineList which contains an array of AOIStCmdLineEntity.
 */


class AOIStCmdLineEntity {
  var $aoiID;
  var $iocName;
  var $iocStCmdLine;

  function AOIStCmdLineEntity(
                     $aoiID,
                     $iocName,
                     $iocStCmdLine
                     ) {

    $this->aoiID = $aoiID;
    $this->iocName = $iocName;
    $this->iocStCmdLine = $iocStCmdLine;
  }

  function getAOIID() {
    return $this->aoiID;
  }

  function getIOCName() {
    return $this->iocName;
  }

  function getIOCStCmdLine() {
    return $this->iocStCmdLine;
  }
}


/*
 * AOIStCmdLineList is a business object which contains a collection of information
 * representing the list of all ioc startup command file lines for a specific aoi. It is
 * essentially a wrapper for an array of AOIStCmdEntity, along with the standard
 * "loadFromDB()" function and other utility functions.
 */
class AOIStCmdLineList {
  // an array of AOIStCmdLineEntity
  var $aoiStCmdLineEntities;

  function AOIStCmdLineList() {
    $this->aoiStCmdLineEntities = array();
  }

  function getElement($idx) {
    return $this->aoiStCmdLineEntities[$idx];
  }

  // return size of array
  function length() {
    if ($this->aoiStCmdLineEntities == null)
      return 0;
    else
      return count($this->aoiStCmdLineEntities);
  }

  /*
   * getArray() returns an array of AOIStCmdLineEntity (assuming loadFromDB has
   * been called already).
   */
  function getArray() {
    return $this->aoiStCmdLineEntities;
  }


  /*
   * Conducts MySQL db transactions to initialize AOIStCmdLineList from the
   * device table. Returns false if db error occurs, true
   * otherwise. */

  function loadFromDB($dbConn, $aoiIdConstraint) {
    global $errno;
    global $error;

    $conn = $dbConn->getConnection();
    $tableNamePrefix = $dbConn->getTableNamePrefix();

    // Get startup command file info from aoi_ioc_stcmd_line, ioc_stcmd_line, and ioc tables
    $qb = new DBQueryBuilder($tableNamePrefix);

    $qb->addColumn("aoi_ioc_stcmd_line.aoi_id");
    $qb->addColumn("ioc.ioc_nm");
    $qb->addColumn("ioc_stcmd_line.ioc_stcmd_line");

    $qb->addTable("aoi_ioc_stcmd_line");
    $qb->addTable("ioc_stcmd_line");
    $qb->addTable("ioc");

    $qb->addWhere("aoi_ioc_stcmd_line.aoi_id = '".$aoiIdConstraint."' and aoi_ioc_stcmd_line.ioc_stcmd_line_id = ioc_stcmd_line.ioc_stcmd_line_id");
    $qb->addWhere("ioc_stcmd_line.ioc_id = ioc.ioc_id");

    $aoiStCmdQuery = $qb->getQueryString();

    //logEntry('debug',$aoiQuery);

    if (!$aoiStCmdResult = mysql_query($aoiStCmdQuery, $conn)) {
      $errno = mysql_errno();
      $error = "AOIStCmdList.loadFromDB(): ".mysql_error();
      logEntry('critical',$error);
      return false;
    }

    $idx = 0;

    if ($aoiStCmdResult) {
      while ($aoiRow = mysql_fetch_array($aoiStCmdResult)) {

        	$aoiStCmdLineEntity = new AOIStCmdLineEntity($aoiRow['aoi_id'], $aoiRow['ioc_nm'], $aoiRow['ioc_stcmd_line']);

        	$this->aoiStCmdLineEntities[$idx] = $aoiStCmdLineEntity;
        	$idx = $idx +1;
      }
    }
    return true;
  }
}

?>