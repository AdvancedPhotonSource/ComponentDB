<?php
/*
 * Defines business class PLCList which contains an array of PLCEntity.
 */

class PLCEntity {
  var $plcName;
  var $plcCompID;
  var $plcGroup;
  var $plcDesc;
  var $plcVerPVName;

  function PLCEntity($plcName, $plcCompID, $plcGroup, $plcDesc, $plcVerPVName) {
    $this->plcName = $plcName;
    $this->plcCompID = $plcCompID;
    $this->plcGroup = $plcGroup;
    $this->plcDesc = $plcDesc;
    $this->plcVerPVName = $plcVerPVName;
  }
  function getPlcName() {
    return $this->plcName;
  }
  function getPlcCompID() {
    return $this->plcCompID;
  }
  function getPlcGroup() {
    return $this->plcGroup;
  }
  function getPlcDesc() {
    return $this->plcDesc;
  }
  function getPlcVerPVName() {
    return $this->plcVerPVName;
  }
}

/*
 * PLCList is a business object which contains a collection of information
 * representing the list of all current PLC's in the IRMIS database. It is
 * essentially a wrapper for an array of PLCEntity, along with the standard
 * "loadFromDB()" function and other utility functions.
 */

class PLCList {
  // an array of PLCEntity
  var $plcEntities;

  function PLCList() {
    $this->plcEntities = array();
  }

  function getElement($idx) {
    return $this->plcEntities[$idx];
  }

  // return size of array
  function length() {
    if ($this->plcEntities == null)
      return 0;
    else
      return count($this->plcEntities);
  }

  /*
   * getArray() returns an array of PLCEntity (assuming loadFromDB has
   * been called already).
   */
  function getArray() {
    return $this->plcEntities;
  }

  // Returns getElementForPlcName equal to $plcName
  function getElementForPlcName($plcName) {
    foreach ($this->plcEntities as $plcEntity) {
      if ($plcEntity->getPlcName() == $plcName) {
	return $plcEntity;
      }
    }
    logEntry('debug',"Unable to find plcEntity for plcName $plcName");
    return null;
  }

  /*
   * Conducts MySQL db transactions to initialize PLCList from the
   * device table. Returns false if db error occurs, true
   * otherwise. */

  function loadFromDB($dbConn, $plcNameConstraint) {
    global $errno;
    global $error;

    $conn = $dbConn->getConnection();
    $tableNamePrefix = $dbConn->getTableNamePrefix();

    // Get core plc info from device table
    $qb = new DBQueryBuilder($tableNamePrefix);

//  $qb->addColumn("component_instance_name");
//  $qb->addColumn("component.component_id");
//  $qb->addColumn("group_name");
//  $qb->addTable("component");
//  $qb->addTable("component_rel");
//  $qb->addTable("aps_component");
//  $qb->addTable("group_name");
//
//  if ($plcNameConstraint) {
//    // convert the * to %
//    $qb->addWhere("component.component_instance_name like '%".$plcNameConstraint."%'");
//  }
//  $qb->addWhere("component.component_instance_name like 'plc_%'");
//  $qb->addWhere("component.component_id = component_rel.child_component_id");
//  $qb->addWhere("component_rel.component_rel_type_id = 1");
//  $qb->addWhere("aps_component.component_id = component.component_id");
//  $qb->addWhere("aps_component.group_name_id = group_name.group_name_id");
//  $qb->addWhere('component_rel.mark_for_delete = 0');
//  $qb->addSuffix("order by group_name, component_instance_name");
//
// Provide a hard coded query string because I need joins ....
    $plcQuery = $qb->getQueryString();
    // logEntry('debug',$plcQuery);
    $plcQuery = "select component_instance_name, component.component_id, group_name, plc_description,plc_version_pv_name from component join component_rel on component.component_id=component_rel.child_component_id join aps_component using (component_id) left join plc using (component_id) join group_name using (group_name_id) where component_rel.component_rel_type_id=1 and component_rel.mark_for_delete=0 and component.component_instance_name like 'plc_%' and component.component_instance_name like'%".$plcNameConstraint."%' order by group_name, component_instance_name;";

    if (!$plcResult = mysql_query($plcQuery, $conn)) {
      $errno = mysql_errno();
      $error = "PLCList.loadFromDB(): ".mysql_error();
      logEntry('critical',$error);
      return false;
    }

    $idx = 0;
    if ($plcResult) {
      while ($plcRow = mysql_fetch_array($plcResult)) {
        $plcEntity = new PLCEntity($plcRow['component_instance_name'],$plcRow['component_id'],$plcRow['group_name'],$plcRow['plc_description'],$plcRow['plc_version_pv_name']);
        $this->plcEntities[$idx] = $plcEntity;
        $idx = $idx +1;
      }
    }
    return true;
  }
}

?>
