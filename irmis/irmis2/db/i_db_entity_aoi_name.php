<?php
/*
 * Defines business class AOINameList which contains an array of AOINameEntity.
 */


class AOINameEntity {
  var $aoiID;
  var $aoiName;

  function AOINameEntity(
                     $aoiID,
                     $aoiName) {

    $this->aoiID = $aoiID;
    $this->aoiName = $aoiName;
  }

  function getAOIID() {
    return $this->aoiID;
  }
  function getAOIName() {
    return $this->aoiName;
  }

}


/*
 * AOINameList is a business object which contains a collection of information
 * representing the list of all current AOI names in the IRMIS database. It is
 * essentially a wrapper for an array of AOINameEntity, along with the standard
 * "loadFromDB()" function and other utility functions.
 */
class AOINameList {
  // an array of AOINameEntity
  var $aoiNameEntities;

  function AOINameList() {
    $this->aoiNameEntities = array();
  }

  function getElement($idx) {
    return $this->aoiNameEntities[$idx];
  }

  // return size of array
  function length() {
    if ($this->aoiNameEntities == null)
      return 0;
    else
      return count($this->aoiNameEntities);
  }

  /*
   * getArray() returns an array of AOINameEntity (assuming loadFromDB has
   * been called already).
   */
  function getArray() {
    return $this->aoiNameEntities;
  }

  // Returns getElementForAOIName equal to $aoiName

  function getElementForAOIName($aoiName) {
       foreach ($this->aoiNameEntities as $aoiNameEntity) {
	  if ($aoiNameEntity->getAOIName() == $aoiName) {
	    return $aoiNameEntity;
	  }
	 }
	 logEntry('debug',"Unable to find aoiNameEntity for aoiName $aoiName");
	 return null;
  }

  /*
   * Conducts MySQL db transactions to initialize AOINameList from the
   * device table. Returns false if db error occurs, true
   * otherwise. */

  function loadFromDB($dbConn, $aoiNameConstraint) {
    global $errno;
    global $error;

    $conn = $dbConn->getConnection();
    $tableNamePrefix = $dbConn->getTableNamePrefix();

    // Get core aoi info from device table
    $qb = new DBQueryBuilder($tableNamePrefix);
    $qb->addColumn("aoi_id");
    $qb->addColumn("aoi_name");

    $qb->addTable("aoi");

    $aoiQuery = $qb->getQueryString();
    logEntry('debug',$aoiQuery);

    $aoiResult = 0;

    if (!$aoiResult = mysql_query($aoiQuery, $conn)) {
      $errno = mysql_errno();
      $error = "AOINameList.loadFromDB(): ".mysql_error();
      //echo "Query error for retrieving aoi names";
      logEntry('critical',$error);
      return false;
    }

    $idx = 0;
    $aoiNamePrevious = "";

    $aoi_name_results = array();

    if ($aoiResult) {
      while ($aoiRow = mysql_fetch_array($aoiResult)) {
        // filter out duplicate aoi names
        if ($aoiRow['aoi_name'] != $aoiNamePrevious) {

        	$aoiNameEntity = new AOINameEntity($aoiRow['aoi_id'], $aoiRow['aoi_name']);

        	$this->aoiNameEntities[$idx] = $aoiNameEntity;

        	$aoi_name_results[$idx] = $aoiRow['aoi_name'];

        	// echo " aoi name is: ".$aoiRow['aoi_name'];
        	// echo "aoi_id results: aoi_id is $temp_aoi_id";

        	$idx = $idx +1;
        	$aoiNamePrevious = $aoiRow['aoi_name'];
        }
      }
    }


    return true;
  }
}

?>