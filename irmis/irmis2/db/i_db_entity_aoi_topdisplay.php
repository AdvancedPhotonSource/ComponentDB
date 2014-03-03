<?php
/*
 * Defines business class AOITopdisplayList which contains an array of AOITopdisplayEntity.
 */


class AOITopdisplayEntity {
  var $aoiID;
  var $uri;

  function AOITopdisplayEntity(
                     $aoiID,
                     $uri
                     ) {

    $this->aoiID = $aoiID;
    $this->uri = $uri;
  }
  function getAOIID() {
    return $this->aoiID;
  }
  function getURI() {
    return $this->uri;
  }

}


/*
 * AOITopdisplayList is a business object which contains a collection of information
 * representing the list of AOI top MEDM displays in the IRMIS database for a specific aoi. It is
 * essentially a wrapper for an array of AOITopdisplayEntity, along with the standard
 * "loadFromDB()" function and other utility functions.
 */
class AOITopdisplayList {
  // an array of AOITopdisplayEntity
  var $aoiTopdisplayEntities;

  function AOITopdisplayList() {
    $this->aoiTopdisplayEntities = array();
  }

  function getElement($idx) {
    return $this->aoiTopdisplayEntities[$idx];
  }

  // return size of array
  function length() {
    if ($this->aoiTopdisplayEntities == null)
      return 0;
    else
      return count($this->aoiTopdisplayEntities);
  }

  /*
   * getArray() returns an array of AOITopdisplayEntity (assuming loadFromDB has
   * been called already).
   */
  function getArray() {
    return $this->aoiTopdisplayEntities;
  }

  // Returns getElementForAOIName equal to $aoiName

  //function getElementForAOIName($aoiName) {
  //     foreach ($this->aoiPlcEntities as $aoiPlcEntity) {
//	  if ($aoiPlcEntity->getAOIName() == $aoiName) {
//	    return $aoiPlcEntity;
//	  }
//	 }
//	 logEntry('debug',"Unable to find aoiPlcEntity for aoiName $aoiName");
	// return null;

  //}

  /*
   * Conducts MySQL db transactions to initialize AOITopdisplayList from the
   * aoi table. Returns false if db error occurs, true
   * otherwise. */

  function loadFromDB($dbConn, $aoiIDConstraint) {
    global $errno;
    global $error;

    $conn = $dbConn->getConnection();
    $tableNamePrefix = $dbConn->getTableNamePrefix();

    // Get MEDM display info from aoi_topdisplay table
    $qb = new DBQueryBuilder($tableNamePrefix);
    $qb->addColumn("aoi_topdisplay.aoi_id");
    $qb->addColumn("aoi_topdisplay.uri");

    $qb->addTable("aoi");
    $qb->addTable("aoi_topdisplay");
    $qb->addWhere("aoi.aoi_id = aoi_topdisplay.aoi_id");

    if ($aoiIDConstraint) {   
	$qb->addWhere("aoi.aoi_id = '".$aoiIDConstraint."'");
    }

    $aoiQuery = $qb->getQueryString();
    logEntry('debug',$aoiQuery);

    if (!$aoiResult = mysql_query($aoiQuery, $conn)) {
      $errno = mysql_errno();
      $error = "AOITopdisplayList.loadFromDB(): ".mysql_error();
      logEntry('critical',$error);
      return false;
    }

    $idx = 0;
    if ($aoiResult) {
      while ($aoiRow = mysql_fetch_array($aoiResult)) {

        $aoiTopdisplayEntity = new AOITopdisplayEntity($aoiRow['aoi_id'], $aoiRow['uri']);

        $this->aoiTopdisplayEntities[$idx] = $aoiTopdisplayEntity;
        $idx = $idx +1;
      }
    }
    return true;
  }
}

?>