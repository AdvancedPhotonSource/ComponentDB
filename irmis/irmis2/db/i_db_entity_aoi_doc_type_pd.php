<?php
/*
 * Defines business class aoiDocTypeList which contains an array of aoiDocTypeEntity.
 */

/*
 * aoiDocTypeEntity corresponds to a single row from the IRMIS doc_type table.
 */
class aoiDocTypeEntity {
  var $doc_type;
  var $doc_type_id;

  function aoiDocTypeEntity($doc_type, $doc_type_id) {
	$this->doc_type = $doc_type;
	$this->doc_type_id = $doc_type_id;
  }
  function getDocType() {
    return $this->doc_type;
  }
  function getDocTypeID() {
    return $this->doc_type_id;
  }
}


/*
 * aoiDocTypeList is a business object which contains information
 * representing the list of the AOI document types in the IRMIS database. It is
 * essentially a wrapper for an array of aoiDocTypeEntity, along with the standard
 * "loadFromDB()" function and other utility functions.
 */
class aoiDocTypeList {
  // an array of aoiDocTypeEntity
  var $aoiDocTypeEntities;

  function aoiDocTypeList() {
    $this->aoiDocTypeEntities = array();
  }

  function getElement($idx) {
    return $this->aoiDocTypeEntities[$idx];
  }

  // return size of array
  function length() {
    if ($this->aoiDocTypeEntities == null)
      return 0;
    else
      return count($this->aoiDocTypeEntities);
  }

  /*
   * getArray() returns an array of aoiDocTypeEntity (assuming loadFromDB has
   * been called already).
   */
  function getArray() {
    return $this->aoiDocTypeEntities;
  }


  /*
   * Conducts MySQL db transactions to initialize aoiDocTypeList from the
   * doc_type table. Returns false if db error occurs, true
   * otherwise. */
  function loadFromDB($dbConn) {
    global $errno;
    global $error;

    $conn = $dbConn->getConnection();
    $tableNamePrefix = $dbConn->getTableNamePrefix();

    // Get doc_type info from doc_type table
    $qb = new DBQueryBuilder($tableNamePrefix);
    $qb->addColumn("doc_type");
    $qb->addColumn("doc_type_id");
    $qb->addTable("doc_type");
    $aoiDocTypeQuery = $qb->getQueryString();
    logEntry('debug',$aoiDocTypeQuery);

    if (!$aoiDocTypeResult = mysql_query($aoiDocTypeQuery, $conn)) {
      $errno = mysql_errno();
      $error = "aoiDocTypeList.loadFromDB(): ".mysql_error();
      logEntry('critical',$error);
      return false;
    }

    $idx = 0;
    if ($aoiDocTypeResult) {
      while ($aoiDocTypeRow = mysql_fetch_array($aoiDocTypeResult)) {
        $aoiDocTypeEntity = new aoiDocTypeEntity($aoiDocTypeRow['doc_type'],$aoiDocTypeRow['doc_type_id']);
        $this->aoiDocTypeEntities[$idx] = $aoiDocTypeEntity;
        $idx = $idx +1;
      }
    }
    return true;
  }
}

?>
