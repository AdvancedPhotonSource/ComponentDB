<?php
/*
 * Defines business class iocnameList which contains an array of iocnameEntity.
 */

/*
 * iocnameEntity corresponds to a single row from the IRMIS ioc table.
 */
class CCSumEntity {
  var $ccsum;

  function CCSumEntity($ccsum) {
    $this->ccsum = $ccsum;

  }
  function getCCSum() {
    return $this->ccsum;
  }
}

/*
 * iocnameList is a business object which contains information
 * representing the list of all iocs in the IRMIS database. It is
 * essentially a wrapper for an array of iocnameEntity, along with the standard
 * "loadFromDB()" function and other utility functions.
 */
class CCSumList {
  // an array of iocnameEntity
  var $CCSumEntities;

  function CCSumList() {
    $this->CCSumEntities = array();
  }

  function getElement($idx) {
    return $this->CCSumEntities[$idx];
  }

  // return size of array
  function length() {
    if ($this->CCSumEntities == null)
      return 0;
    else
      return count($this->CCSumEntities);
  }

  /*
   * getArray() returns an array of iocnameEntity (assuming loadFromDB has
   * been called already).
   */
  function getArray() {
    return $this->CCSumEntities;
  }


  /*
   * Conducts MySQL db transactions to initialize iocnameList from the
   * ioc table. Returns false if db error occurs, true
   * otherwise. */

  function loadCCSumFromDB($dbConn, $compID) {
    global $errno;
    global $error;

    $conn = $dbConn->getConnection();
    $tableNamePrefix = $dbConn->getTableNamePrefix();

    // Get the quantity of children for a given component ID
    $qb = new DBQueryBuilder($tableNamePrefix);
    $qb->addColumn("sum(cr.component_rel_type_id = '2') as sum");
    $qb->addTable("component c");
	$qb->addTable("component_rel cr");
	$qb->addWhere("c.component_id = cr.parent_component_id");
	$qb->addWhere("c.component_id = '$compID'");
	$qb->addWhere("cr.mark_for_delete = '0'");
	$qb->addSuffix("group by cr.parent_component_id");
    $CCSumQuery = $qb->getQueryString();
    logEntry('debug',$CCSumQuery);

    if (!$CCSumResult = mysql_query($CCSumQuery, $conn)) {
      $errno = mysql_errno();
      $error = "CCSumList.loadFromDB(): ".mysql_error();
      logEntry('critical',$error);
      return false;
    }

    $idx = 0;
    if ($CCSumResult) {
      while ($CCSumRow = mysql_fetch_array($CCSumResult)) {
        $CCSumEntity = new CCSumEntity($CCSumRow['sum']);
        $this->CCSumEntities[$idx] = $CCSumEntity;
        $idx = $idx +1;
      }
    }
    return true;
  }
}

?>
