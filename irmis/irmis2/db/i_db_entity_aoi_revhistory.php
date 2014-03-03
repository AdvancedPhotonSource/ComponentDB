<?php
/*
 * Defines business class AOIRevhistoryList which contains an array of AOIRevhistoryEntity.
 */


class AOIRevhistoryEntity {
  var $aoiID;
  var $aoi_revhistory_date;
  var $aoi_revhistory_comment;

  function AOIRevhistoryEntity(
                     $aoiID,
                     $aoi_revhistory_date,
                     $aoi_revhistory_comment
                     ) {

    $this->aoiID = $aoiID;
    $this->aoi_revhistory_date = $aoi_revhistory_date;
    $this->aoi_revhistory_comment = $aoi_revhistory_comment;
  }
  function getAOIID() {
    return $this->aoiID;
  }
  function getRevhistoryDate() {
    return $this->aoi_revhistory_date;
  }
  function getRevhistoryComment() {
    return $this->aoi_revhistory_comment;
  }

}


/*
 * AOIRevhistoryList is a business object which contains a collection of information
 * representing the list of AOI revision history comments in the IRMIS database for a specific aoi. It is
 * essentially a wrapper for an array of AOIRevhistoryEntity, along with the standard
 * "loadFromDB()" function and other utility functions.
 */
class AOIRevhistoryList {
  // an array of AOIRevhistoryEntity
  var $aoiRevhistoryEntities;

  function AOIRevhistoryList() {
    $this->aoiRevhistoryEntities = array();
  }

  function getElement($idx) {
    return $this->aoiRevhistoryEntities[$idx];
  }

  // return size of array
  function length() {
    if ($this->aoiRevhistoryEntities == null)
      return 0;
    else
      return count($this->aoiRevhistoryEntities);
  }

  /*
   * getArray() returns an array of AOIRevhistoryEntity (assuming loadFromDB has
   * been called already).
   */
  function getArray() {
    return $this->aoiRevhistoryEntities;
  }


  /*
   * Conducts MySQL db transactions to initialize AOIRevhistoryList from the
   * aoi table. Returns false if db error occurs, true
   * otherwise. */

  function loadFromDB($dbConn, $aoiIDConstraint) {
    global $errno;
    global $error;

    $conn = $dbConn->getConnection();
    $tableNamePrefix = $dbConn->getTableNamePrefix();

    // Get revhistory comments info from aoi_revhistory table
    $qb = new DBQueryBuilder($tableNamePrefix);
    $qb->addColumn("aoi_revhistory.aoi_id");

    $qb->addColumn("aoi_revhistory.aoi_revhistory_date");
    $qb->addColumn("aoi_revhistory.aoi_revhistory_comment");

    $qb->addTable("aoi");
    $qb->addTable("aoi_revhistory");
    $qb->addWhere("aoi.aoi_id = '".$aoiIDConstraint."'");
    $qb->addWhere("aoi_revhistory.aoi_id = aoi.aoi_id");

    $aoiQuery = $qb->getQueryString();
    logEntry('debug',$aoiQuery);

    if (!$aoiResult = mysql_query($aoiQuery, $conn)) {
      $errno = mysql_errno();
      $error = "AOIRevhistoryList.loadFromDB(): ".mysql_error();
      logEntry('critical',$error);
      return false;
    }

    $idx = 0;
    if ($aoiResult) {
      while ($aoiRow = mysql_fetch_array($aoiResult)) {

        $aoiRevhistoryEntity = new AOIRevhistoryEntity($aoiRow['aoi_id'], $aoiRow['aoi_revhistory_date'], $aoiRow['aoi_revhistory_comment']);

        $this->aoiRevhistoryEntities[$idx] = $aoiRevhistoryEntity;
        $idx = $idx +1;
      }
    }
    return true;
  }
}

?>