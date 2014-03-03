<?php
/*
 * Defines business class plcnameList which contains an array of plcnameEntity.
 */

/*
 * plcnameEntity corresponds to a single row from the IRMIS plc table.
 */
class plcnameEntity {
  var $plcname;

  function plcnameEntity($plcname) {
    $this->plcname = $plcname;

  }
  function getPlcName() {
    return $this->plcname;
  }
}

/*
 * plcnameList is a business object which contains information
 * representing the list of all current plcs in the IRMIS database. It is
 * essentially a wrapper for an array of plcnameEntity, along with the standard
 * "loadFromDB()" function and other utility functions.
 */
class plcnameList {
  // an array of plcnameEntity
  var $plcnameEntities;

  function plcnameList() {
    $this->plcnameEntities = array();
  }

  function getElement($idx) {
    return $this->plcnameEntities[$idx];
  }

  // return size of array
  function length() {
    if ($this->plcnameEntities == null)
      return 0;
    else
      return count($this->plcnameEntities);
  }

  /*
   * getArray() returns an array of plcnameEntity (assuming loadFromDB has
   * been called already).
   */
  function getArray() {
    return $this->plcnameEntities;
  }


  /*
   * Conducts MySQL db transactions to initialize plcList from the
   * plc table. Returns false if db error occurs, true
   * otherwise. */

  function loadFromDB($dbConn) {
    global $errno;
    global $error;

    $conn = $dbConn->getConnection();
    $tableNamePrefix = $dbConn->getTableNamePrefix();

    // Get plc_name from plc table
    $qb = new DBQueryBuilder($tableNamePrefix);
    $qb->addColumn("component.component_instance_name");
    $qb->addTable("plc");
    $qb->addTable("component");
    $qb->addWhere("plc.component_id = component.component_id");
	$qb->addSuffix("order by component.component_instance_name");
    $plcnameQuery = $qb->getQueryString();
    // logEntry('debug',$plcnameQuery);

    if (!$plcnameResult = mysql_query($plcnameQuery, $conn)) {
      $errno = mysql_errno();
      $error = "plcnameList.loadFromDB(): ".mysql_error();
      logEntry('critical',$error);
      return false;
    }

    $idx = 0;
    if ($plcnameResult) {
      while ($plcRow = mysql_fetch_array($plcnameResult)) {
        $plcnameEntity = new plcnameEntity($plcRow['plc_name']);
        $this->plcnameEntities[$idx] = $plcnameEntity;
        $idx = $idx +1;
      }
    }
    return true;
  }
}

?>
