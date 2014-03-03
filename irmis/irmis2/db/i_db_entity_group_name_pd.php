<?php
/*
 * Defines business class groupNameList which contains an array of groupNameEntity.
 */

/*
 * groupNameEntity corresponds to a single row from the IRMIS groupName table.
 */
class groupNameEntity {
  var $group_name;
  var $group_name_id;

  function groupNameEntity($group_name,$group_name_id) {
    $this->group_name = $group_name;
    $this->group_name_id = $group_name_id;

  }
  function getGroupName() {
    return $this->group_name;
  }

  function getGroupNameID() {
  	return $this->group_name_id;
  }
}

/*
 * groupNameList is a business object which contains a collection of information
 * representing the list of all current APS group names in the IRMIS database. It is
 * essentially a wrapper for an array of groupNameEntity, along with the standard
 * "loadFromDB()" function and other utility functions.
 */

class groupNameList {
  // an array of groupNameEntity
  var $groupNameEntities;

  function groupNameList() {
    $this->groupNameEntities = array();
  }

  function getElement($idx) {
    return $this->groupNameEntities[$idx];
  }

  // return size of array
  function length() {
    if ($this->groupNameEntities == null)
      return 0;
    else
      return count($this->groupNameEntities);
  }

  /*
   * getArray() returns an array of groupNameEntity (assuming loadFromDB has
   * been called already).
   */
  function getArray() {
    return $this->groupNameEntities;
  }

  // Returns getElementForGroupName equal to $group_name

  function getElementForGroupName($group_name) {
    foreach ($this->groupNameEntities as $groupNameEntity) {
  	  if ($groupNameEntity->getGroupName() == $group_name) {
  	    return $groupNameEntity;
  	  }
  	}
  	 logEntry('debug',"Unable to find groupNameEntity for APS group name $group_name");
  	 return null;
  }

  /*
   * Conducts MySQL db transactions to initialize groupNameList from the
   * groupName table. Returns false if db error occurs, true
   * otherwise. */

  function loadFromDB($dbConn) {
    global $errno;
    global $error;

    $conn = $dbConn->getConnection();
    $tableNamePrefix = $dbConn->getTableNamePrefix();

    // Get groupName info from groupName table
    $qb = new DBQueryBuilder($tableNamePrefix);
    $qb->addColumn("group_name");
    $qb->addColumn("group_name_id");
    $qb->addTable("group_name");
	$qb->addSuffix("order by group_name");
    $groupNameQuery = $qb->getQueryString();

    if (!$groupNameResult = mysql_query($groupNameQuery, $conn)) {
      $errno = mysql_errno();
      $error = "groupNameList.loadFromDB(): ".mysql_error();
      return false;
    }

    $idx = 0;
    if ($groupNameResult) {
      while ($groupNameRow = mysql_fetch_array($groupNameResult)) {
        $groupNameEntity = new groupNameEntity($groupNameRow['group_name'],$groupNameRow['group_name_id']);
        $this->groupNameEntities[$idx] = $groupNameEntity;
        $idx = $idx +1;
      }
    }
    return true;
  }
}

?>
