<?php
/*
 * Defines business class systemList which contains an array of systemEntity.
 */

/*
 * systemEntity corresponds to a single row from the IRMIS ioc table.
 */
class functionEntity {
  var $ID;
  var $function;

  function functionEntity($ID, $function) {
    $this->ID = $ID;
    $this->function = $function;
  }

  function getID() {
    return $this->ID;
  }

  function getFunction() {
    return $this->function;
  }
}


/*
 * systemList is a business object which contains a collection of information
 * representing the list of all current system's in the IRMIS database. It is
 * essentially a wrapper for an array of systemEntity, along with the standard
 * "loadFromDB()" function and other utility functions.
 */
class functionList {
  // an array of systemEntity
  var $functionEntities;

  function functionList() {
    $this->functionEntities = array();
  }

  function getElement($idx) {
    return $this->functionEntities[$idx];
  }

  // return size of array
  function length() {
    if ($this->functionEntities == null)
      return 0;
    else
      return count($this->functionEntities);
  }

  /*
   * getArray() returns an array of systemEntity (assuming loadFromDB has
   * been called already).
   */
  function getArray() {
    return $this->functionEntities;
  }


  /*
   * Gets all known functions from function table.
   */
  function loadFromDB($dbConn) {
    global $errno;
    global $error;

    $conn = $dbConn->getConnection();
    $tableNamePrefix = $dbConn->getTableNamePrefix();

    // Get core ioc info from ioc table
    $qb = new DBQueryBuilder($tableNamePrefix);
    $qb->addColumn("distinct function");
    $qb->addColumn("function_id");
    $qb->addTable("function");
	$qb->addSuffix("order by function");
    $functionQuery = $qb->getQueryString();
    logEntry('debug',$functionQuery);

    if (!$functionResult = mysql_query($functionQuery, $conn)) {
      $errno = mysql_errno();
      $error = "functionList.loadFromDB(): ".mysql_error();
      logEntry('critical',$error);
      return false;
    }

    $idx = 0;
    if ($functionResult) {
      while ($functionRow = mysql_fetch_array($functionResult)) {
        $functionEntity = new functionEntity($functionRow['function_id'], $functionRow['function']);
        $this->functionEntities[$idx] = $functionEntity;
        $idx = $idx +1;
      }
    }
    return true;
  }

  /*
   * A different load which will get all functions for a given component type.
   */
  function loadFromDBForComponentTypeID($dbConn, $ctID) {
    global $errno;
    global $error;

    $conn = $dbConn->getConnection();
    $tableNamePrefix = $dbConn->getTableNamePrefix();

    // Get core ioc info from ioc table
    $qb = new DBQueryBuilder($tableNamePrefix);
    $qb->addColumn("f.function_id");
    $qb->addColumn("f.function");
    $qb->addTable("function f");
    $qb->addTable("component_type_function ctf");
    $qb->addWhere("ctf.function_id=f.function_id");
    $qb->addWhere("ctf.component_type_id=".$ctID);

    $functionQuery = $qb->getQueryString();
    //logEntry('debug',$functionQuery);

    if (!$functionResult = mysql_query($functionQuery, $conn)) {
      $errno = mysql_errno();
      $error = "functionList.loadFromDBForComponentTypeID(): ".mysql_error();
      logEntry('critical',$error);
      return false;
    }

    $idx = 0;
    if ($functionResult) {
      while ($functionRow = mysql_fetch_array($functionResult)) {
        $functionEntity = new functionEntity($functionRow['function_id'], $functionRow['function']);
        $this->functionEntities[$idx] = $functionEntity;
        $idx = $idx +1;
      }
    }
    return true;

  }
}

?>