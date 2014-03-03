<?php
/*
 * Defines business class criticalityList which contains an array of criticalityEntity.
 */

/*
 * criticalityEntity corresponds to a single row from the IRMIS criticality_type table.
 */
class criticalityEntity {
  var $criticality_level;
  var $criticality_classification;
  var $criticality_id;

  function criticalityEntity($criticality_level,$criticality_classification,$criticality_id) {
	$this->criticality_level = $criticality_level;
	$this->criticality_classification = $criticality_classification;
	$this->criticality_id = $criticality_id;
  }

  function getCriticalityLevel() {
    return $this->criticality_level;
  }
  function getCriticalityClassification() {
  	return $this->criticality_classification;
  }

  function getCriticalityID() {
  	return $this->criticality_id;
  }
}


/*
 * criticalityList is a business object which contains information
 * representing the list of criticality levels in the IRMIS database. It is
 * essentially a wrapper for an array of criticalityEntity
 */
class criticalityList {
  // an array of criticalityEntity

  var $criticalityEntities;

  function criticalityList() {
    $this->criticalityEntities = array();
  }

  function getElement($idx) {
    return $this->criticalityEntities[$idx];
  }

  // return size of array
  function length() {
    if ($this->criticalityEntities == null)
      return 0;
    else
      return count($this->criticalityEntities);
  }

  /*
   * getArray() returns an array of criticalityEntity
   */
  function getArray() {
    return $this->criticalityEntities;
  }


  // Returns getElementForCriticalityLevel equal to $criticality_level

  function getElementForCriticalityLevel($criticality_level) {
    foreach ($this->criticalityEntities as $criticalityEntity) {
  	  if ($criticalityEntity->getCriticalityLevel() == $criticality_level) {
  	    return $criticalityEntity;
  	  }
  	}
  	 logEntry('debug',"Unable to find criticalityEntity for AOI criticality level $criticality_level");
  	 return null;
  }

  /*
   * Conducts MySQL db transactions to initialize criticalityList from the
   * criticality table. Returns false if db error occurs, true
   * otherwise. */

  function loadFromDB($dbConn) {
    global $errno;
    global $error;

    $conn = $dbConn->getConnection();
    $tableNamePrefix = $dbConn->getTableNamePrefix();

    // Get criticality level from criticality level table
    $qb = new DBQueryBuilder($tableNamePrefix);
    $qb->addColumn("criticality_level");
    $qb->addColumn("criticality_classification");
    $qb->addColumn("criticality_id");
    $qb->addTable("criticality_type");
    $criticalityQuery = $qb->getQueryString();
    logEntry('debug',$criticalityQuery);

    if (!$criticalityResult = mysql_query($criticalityQuery, $conn)) {
      $errno = mysql_errno();
      $error = "criticalityList.loadFromDB(): ".mysql_error();
      logEntry('critical',$error);
      return false;
    }

    $idx = 0;
    if ($criticalityResult) {
      while ($criticalityRow = mysql_fetch_array($criticalityResult)) {
        $criticalityEntity = new criticalityEntity($criticalityRow['criticality_level'], $criticalityRow['criticality_classification'], $criticalityRow['criticality_id']);
        $this->criticalityEntities[$idx] = $criticalityEntity;
        $idx = $idx +1;
      }
    }
    return true;
  }
}

?>
