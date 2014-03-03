<?php
/*
 * Defines business class AOINoteList which contains an array of AOINoteEntity.
 */


class AOINoteEntity {
  var $aoiID;
  var $aoi_note_date;
  var $aoi_note;

  function AOINoteEntity(
                     $aoiID,
                     $aoi_note_date,
                     $aoi_note
                     ) {

    $this->aoiID = $aoiID;
    $this->aoi_note_date = $aoi_note_date;
    $this->aoi_note = $aoi_note;
  }
  function getAOIID() {
    return $this->aoiID;
  }
  function getNoteDate() {
    return $this->aoi_note_date;
  }
  function getNote() {
    return $this->aoi_note;
  }

}


/*
 * AOINoteList is a business object which contains a collection of information
 * representing the list of AOI notes in the IRMIS database for a specific aoi. It is
 * essentially a wrapper for an array of AOINoteEntity, along with the standard
 * "loadFromDB()" function and other utility functions.
 */
class AOINoteList {
  // an array of AOINoteEntity
  var $aoiNoteEntities;

  function AOINoteList() {
    $this->aoiNoteEntities = array();
  }

  function getElement($idx) {
    return $this->aoiNoteEntities[$idx];
  }

  // return size of array
  function length() {
    if ($this->aoiNoteEntities == null)
      return 0;
    else
      return count($this->aoiNoteEntities);
  }

  /*
   * getArray() returns an array of AOINoteEntity (assuming loadFromDB has
   * been called already).
   */
  function getArray() {
    return $this->aoiNoteEntities;
  }

  /*
   * Conducts MySQL db transactions to initialize AOINoteList from the
   * aoi_note table. Returns false if db error occurs, true
   * otherwise. */

  function loadFromDB($dbConn, $aoiIDConstraint) {
    global $errno;
    global $error;

    $conn = $dbConn->getConnection();
    $tableNamePrefix = $dbConn->getTableNamePrefix();

    // Get notes from aoi_note table
    $qb = new DBQueryBuilder($tableNamePrefix);
    $qb->addColumn("aoi_note.aoi_id");
    $qb->addColumn("aoi_note.aoi_note_date");
    $qb->addColumn("aoi_note.aoi_note");

    $qb->addTable("aoi");
    $qb->addTable("aoi_note");
    $qb->addWhere("aoi.aoi_id = '".$aoiIDConstraint."'");
    $qb->addWhere("aoi_note.aoi_id = aoi.aoi_id");

    $aoiQuery = $qb->getQueryString();
    logEntry('debug',$aoiQuery);

    if (!$aoiResult = mysql_query($aoiQuery, $conn)) {
      $errno = mysql_errno();
      $error = "AOINoteList.loadFromDB(): ".mysql_error();
      logEntry('critical',$error);
      return false;
    }

    $idx = 0;
    if ($aoiResult) {
      while ($aoiRow = mysql_fetch_array($aoiResult)) {

        $aoiNoteEntity = new AOINoteEntity($aoiRow['aoi_id'], $aoiRow['aoi_note_date'], $aoiRow['aoi_note']);

        $this->aoiNoteEntities[$idx] = $aoiNoteEntity;
        $idx = $idx +1;
      }
    }
    return true;
  }
}

?>