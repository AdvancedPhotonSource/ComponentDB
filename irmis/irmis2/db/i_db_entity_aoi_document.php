<?php
/*
 * Defines business class AOIDocumentList which contains an array of AOIDocumentEntity.
 */


class AOIDocumentEntity {
  var $aoiID;
  var $uri;
  var $doc_type;

  function AOIDocumentEntity(
                     $aoiID,
                     $uri,
                     $doc_type,
                     $doc_type_id
                     ) {

    $this->aoiID = $aoiID;
    $this->uri = $uri;
    $this->doc_type = $doc_type;
    $this->doc_type_id = $doc_type_id;
  }
  function getAOIID() {
    return $this->aoiID;
  }
  function getURI() {
    return $this->uri;
  }
  function getDoctype() {
      return $this->doc_type;
  }
  function getDoctypeID() {
  	  return $this->doc_type_id;
  }
}


/*
 * AOIDocumentList is a business object which contains a collection of information
 * representing the list of all the documents in the IRMIS database for a specific aoi. It is
 * essentially a wrapper for an array of AOIDocumentEntity, along with the standard
 * "loadFromDB()" function and other utility functions.
 */
class AOIDocumentList {
  // an array of AOIDocumentEntity
  var $aoiDocumentEntities;

  function AOIDocumentList() {
    $this->aoiDocumentEntities = array();
  }

  function getElement($idx) {
    return $this->aoiDocumentEntities[$idx];
  }

  // return size of array
  function length() {
    if ($this->aoiDocumentEntities == null)
      return 0;
    else
      return count($this->aoiDocumentEntities);
  }

  /*
   * getArray() returns an array of AOIDocumentEntity (assuming loadFromDB has
   * been called already).
   */
  function getArray() {
    return $this->aoiDocumentEntities;
  }

  // Returns getElementForAOIName equal to $aoiName

  //function getElementForAOIName($aoiName) {
  //     foreach ($this->aoiPlcEntities as $aoiPlcEntity) {
	//  if ($aoiPlcEntity->getAOIName() == $aoiName) {
	//    return $aoiPlcEntity;
	//  }
	// }
	// logEntry('debug',"Unable to find aoiPlcEntity for aoiName $aoiName");
	// return null;
  //}

  /*
   * Conducts MySQL db transactions to initialize AOIDocumentList from the
   * aoi table. Returns false if db error occurs, true
   * otherwise. */

  function loadFromDB($dbConn, $aoiIDConstraint) {
    global $errno;
    global $error;

    $conn = $dbConn->getConnection();
    $tableNamePrefix = $dbConn->getTableNamePrefix();

    // Get core aoi info from aoi table
    $qb = new DBQueryBuilder($tableNamePrefix);
    $qb->addColumn("aoi_document.aoi_id");
    $qb->addColumn("aoi_document.uri");
    $qb->addColumn("doc_type.doc_type");
    $qb->addColumn("doc_type.doc_type_id");

    $qb->addTable("aoi");
    $qb->addTable("aoi_document");
    $qb->addTable("doc_type");
    $qb->addWhere("aoi.aoi_id = aoi_document.aoi_id and aoi_document.doc_type_id = doc_type.doc_type_id");

    if ($aoiIDConstraint) {
	$qb->addWhere("aoi.aoi_id = '".$aoiIDConstraint."'");
    }

    $aoiQuery = $qb->getQueryString();
    logEntry('debug',$aoiQuery);

    if (!$aoiResult = mysql_query($aoiQuery, $conn)) {
      $errno = mysql_errno();
      $error = "AOIDocumentList.loadFromDB(): ".mysql_error();
      logEntry('critical',$error);
      return false;
    }

    $idx = 0;
    if ($aoiResult) {
      while ($aoiRow = mysql_fetch_array($aoiResult)) {

        $aoiDocumentEntity = new AOIDocumentEntity($aoiRow['aoi_id'], $aoiRow['uri'], $aoiRow['doc_type'], $aoiRow['doc_type_id']);

        $this->aoiDocumentEntities[$idx] = $aoiDocumentEntity;
        $idx = $idx +1;
      }
    }
    return true;
  }
}

?>