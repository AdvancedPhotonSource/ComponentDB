<?php
/*
 * Defines business class AOIEditorList which contains an array of AOIEditorEntity.
 */


class AOIEditorEntity {
  var $personID;
  var $roleNameID;
  var $roleName;
  var $personLastName;
  var $personUserID;

  function AOIEditorEntity(
                     $personID,
                     $roleNameID,
                     $roleName,
                     $personLastName,
                     $personUserID
                     ) {

    $this->personID = $personID;
    $this->roleNameID = $roleID;
    $this->roleName = $roleName;
    $this->personLastName = $personLastName;
    $this->personUserID = $personUserID;
  }
  function getPersonID() {
    return $this->personID;
  }
  function getRoleNameID() {
    return $this->roleNameID;
  }
  function getRoleName() {
    return $this->roleName;
  }
  function getPersonLastName() {
  	return $this->personLastName;
  }
  function getPersonUserID() {
  	return $this->personUserID;
  }

}


/*
 * AOIEditorList is a business object which contains a collection of information
 * representing the list of all current users in the IRMIS database that have editing
 * privileges for AOI tables.
 * It is essentially a wrapper for an array of AOIEditorEntity, along with the standard
 * "loadFromDB()" function and other utility functions.
 */

class AOIEditorList {
  // an array of AOIEditorEntity
  var $aoiEditorEntities;

  function AOIEditorList() {
    $this->aoiEditorEntities = array();
  }

  function getElement($idx) {
    return $this->aoiEditorEntities[$idx];
  }

  // return size of array
  function length() {
    if ($this->aoiEditorEntities == null)
      return 0;
    else
      return count($this->aoiEditorEntities);
  }

  /*
   * getArray() returns an array of AOIEditorEntity (assuming loadFromDB has
   * been called already).
   */
  function getArray() {
    return $this->aoiEditorEntities;
  }

  // Returns getElementForAOIEditorName equal to $aoiEditorName

  function getElementForAOIEditorName($personLastName) {
     foreach ($this->aoiEditorEntities as $aoiEditorEntity) {
	  if ($aoiEditorEntity->getPersonLastName() == $personLastName) {
	    return $aoiEditorEntity;
	  }
	 }
	 logEntry('debug',"Unable to find aoiEditorEntity for personLastName $personLastName");
	 return null;
  }

  /*
   * Conducts MySQL db transactions to initialize AOIEditorList from the
   * role and role_name tables. Returns false if db error occurs, true
   * otherwise. */

  function loadFromDB($dbConn, $aoiEditorConstraint) {
    global $errno;
    global $error;

    $conn = $dbConn->getConnection();
    $tableNamePrefix = $dbConn->getTableNamePrefix();

    // Get aoi editor info from person, role, and role_name tables

    $qb = new DBQueryBuilder($tableNamePrefix);

    $qb->addDistinct(true);

    $qb->addColumn("role.person_id");
    $qb->addColumn("role.role_name_id");
    $qb->addColumn("role_name.role_name");
    $qb->addColumn("person.last_nm");
    $qb->addColumn("person.userid");

    $qb->addTable("role");
    $qb->addTable("role_name");
    $qb->addTable("person");

    $qb->addWhere("role_name.role_name = '".$aoiEditorConstraint."'");
    $qb->addWhere("role_name.role_name_id = role.role_name_id");
    $qb->addWhere("person.person_id = role.person_id");

    $aoiEditorQuery = $qb->getQueryString();
    //logEntry('debug',$aoiEditorQuery);

    if (!$aoiResult = mysql_query($aoiEditorQuery, $conn)) {
      $errno = mysql_errno();
      $error = "AOIEditorList.loadFromDB(): ".mysql_error();
      //logEntry('critical',$error);
      return false;
    }

    $idx = 0;

    if ($aoiResult) {
      while ($aoiRow = mysql_fetch_array($aoiResult)) {

        	$aoiEditorEntity = new AOIEditorEntity($aoiRow['person_id'], $aoiRow['role_name_id'], $aoiRow['role_name'], $aoiRow['last_nm'], $aoiRow['userid']);

        	$this->aoiEditorEntities[$idx] = $aoiEditorEntity;
        	$idx = $idx +1;

      }
    }
    return true;
  }
}

?>
