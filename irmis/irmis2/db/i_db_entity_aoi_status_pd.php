<?php
/*
 * Defines business class aoiStatusList which contains an array of aoiStatusEntity.
 */

/*
 * aoiStatusEntity corresponds to a single row from the IRMIS aoi_status table.
 */
class aoiStatusEntity {
  var $aoi_status;
  var $aoi_status_id;

  function aoiStatusEntity($aoi_status,$aoi_status_id) {
	$this->aoi_status = $aoi_status;
	$this->aoi_status_id = $aoi_status_id;
  }
  function getAOIStatus() {
    return $this->aoi_status;
  }

  function getAOIStatusID() {
  	return $this->aoi_status_id;
  }
}


/*
 * aoiStatusList is a business object which contains information
 * representing the list of aoi status categories in the IRMIS database. It is
 * essentially a wrapper for an array of aoiStatusEntity
 */
class aoiStatusList {
  // an array of aoiStatusEntity

  var $aoiStatusEntities;

  function aoiStatusList() {
    $this->aoiStatusEntities = array();
  }

  function getElement($idx) {
    return $this->aoiStatusEntities[$idx];
  }

  // return size of array
  function length() {
    if ($this->aoiStatusEntities == null)
      return 0;
    else
      return count($this->aoiStatusEntities);
  }

  /*
   * getArray() returns an array of aoiStatusEntity
   */
  function getArray() {
    return $this->aoiStatusEntities;
  }

  // Returns getElementForAOIStatus equal to $aoi_status

  function getElementForAOIStatus($aoi_status) {
     foreach ($this->aoiStatusEntities as $aoiStatusEntity) {
	  if ($aoiStatusEntity->getAOIStatus() == $aoi_status) {
	    return $aoiStatusEntity;
	  }
	 }
	 logEntry('debug',"Unable to find aoiStatusEntity for AOI Status $aoi_status");
	 return null;
  }

  /*
   * Conducts MySQL db transactions to initialize aoiStatusList from the
   * aoi_status table. Returns false if db error occurs, true
   * otherwise. */

  function loadFromDB($dbConn) {
    global $errno;
    global $error;

    $conn = $dbConn->getConnection();
    $tableNamePrefix = $dbConn->getTableNamePrefix();

    // Get aoi status from aoi_status table
    $qb = new DBQueryBuilder($tableNamePrefix);
    $qb->addColumn("distinct aoi_status.aoi_status");
    $qb->addColumn("aoi_status_id");
    $qb->addTable("aoi_status");
    $aoiStatusQuery = $qb->getQueryString();
    logEntry('debug',$aoiStatusQuery);

    if (!$aoiStatusResult = mysql_query($aoiStatusQuery, $conn)) {
      $errno = mysql_errno();
      $error = "aoiStatusList.loadFromDB(): ".mysql_error();
      logEntry('aoi_status',$error);
      return false;
    }

    $idx = 0;
    if ($aoiStatusResult) {
      while ($aoiStatusRow = mysql_fetch_array($aoiStatusResult)) {
        $aoiStatusEntity = new aoiStatusEntity($aoiStatusRow['aoi_status'], $aoiStatusRow['aoi_status_id']);
        $this->aoiStatusEntities[$idx] = $aoiStatusEntity;
        $idx = $idx +1;
      }
    }
    return true;
  }
}

?>
