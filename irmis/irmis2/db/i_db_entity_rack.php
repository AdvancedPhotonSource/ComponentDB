<?php
/*
 * Defines business class RackList which contains an array of ComponentEntity
 *
 */

class RackEntity {
  var $ID;
  var $InstanceName;
  var $rackType;
  var $description;
  var $componentTypeID;
  var $comParentIOC;
  var $comParentRack;
  var $comParentRoom;
  var $comParentBldg;
  var $ParentRoom;
  var $group;
  var $image;
  var $logical_desc;

  function RackEntity($ID,$InstanceName,$rackType,$description,$componentTypeID,$ParentRoom, $group, $image, $logical_desc) {
    $this->ID = $ID;
    $this->InstanceName = $InstanceName;
	$this->rackType = $rackType;
	$this->description = $description;
	$this->componentTypeID = $componentTypeID;
	$this->comParentIOC = $comParentIOC;
	$this->comParentRack = $comParentRack;
	$this->comParentRack = $comParentRoom;
	$this->comParentBldg = $comParentBldg;
	$this->ParentRoom = $ParentRoom;
	$this->group = $group;
	$this->image = $image;
	$this->logical_desc = $logical_desc;
  }
  function getID() {
    return $this->ID;
  }
  function getInstanceName() {
    return $this->InstanceName;
  }
  function getrackType() {
    return $this->rackType;
  }
  function getDescription() {
    return $this->description;
  }
  function getcomponentTypeID() {
    return $this->componentTypeID;
  }
  function getcomParentIOC() {
    return $this->comParentIOC;
  }
  function getcomParentRack() {
    return $this->comParentRack;
  }
  function getcomParentRoom() {
    return $this->comParentRoom;
  }
  function getcomParentBldg() {
    return $this->comParentBldg;
  }
  function getParentRoom() {
    return $this->ParentRoom;
  }
  function getGroup() {
    return $this->group;
  }
  function getImage() {
	return $this->image;
  }
  function getLogical_desc() {
	return $this->logical_desc;
  }
  function loadDetailsFromDB($dbConn) {
    $this->comParentIOC = $this->findParentIoc($dbConn, $this->ID);
	$this->comParentRack = $this->findParentRack($dbConn, $this->ID);
	$this->comParentRoom = $this->findParentRoom($dbConn, $this->ID);
	$this->comParentBldg = $this->findParentBuilding($dbConn, $this->ID);
  }

  function findParentIoc($dbConn, $component_id) {
    global $errno;
    global $error;

    $conn = $dbConn->getConnection();
    $tableNamePrefix = $dbConn->getTableNamePrefix();

    // Begin building query for component_type table
    $qb = new DBQueryBuilder($tableNamePrefix);
    $qb->appendSemicolon(false);
    $qb->addColumn("c.component_instance_name");
    $qb->addColumn("ct.component_type_name");
    $qb->addColumn("cr.parent_component_id");
    $qb->addColumn("cr.logical_desc");

    $qb->addTable("component c");
    $qb->addTable("component_type ct");
	$qb->addTable("component_rel cr");

	$qb->addWhere("c.component_type_id = ct.component_type_id");
	$qb->addWhere("cr.child_component_id = c.component_id");
    $qb->addWhere("cr.component_rel_type_id = 1");
	$qb->addWhere("cr.child_component_id = ");  // tack on id below
    $piocQuery = $qb->getQueryString(false);

    $child_component_id = $component_id;
    $found = false;

    while (!$found && $child_component_id != 0) {
      logEntry('debug',"find parent ioc for $child_component_id");
      $query = $piocQuery . $child_component_id . ';';

      if (!$piocResult = mysql_query($query, $conn)) {
        $errno = mysql_errno();
        $error = "findParentIoc(): ".mysql_error();
        logEntry('critical',$error);
        return null;
      }
      if ($piocResult) {
        if ($row = mysql_fetch_array($piocResult)) {
          $component_name = $row['component_instance_name'];
          $component_type_name = $row['component_type_name'];
          $parent_id = $row['parent_component_id'];
          $logical_desc = $row['logical_desc'];

          if ($parent_id == 1) {
            $found = true;
            break;
          }
          $child_component_id = $parent_id;
        } else {
          return "";
        }
      } else {
        return "";
      }
    }
    // need this check during transition
    return $logical_desc;

  }

  function findParentRack($dbConn, $component_id) {
    global $errno;
    global $error;

    $conn = $dbConn->getConnection();
    $tableNamePrefix = $dbConn->getTableNamePrefix();

    // Begin building query for component_type table
	// SELECT c.component_instance_name, ct.component_type_name, cr.parent_component_id, cr.logical_desc
	// FROM component c, component_type ct, component_rel cr
	// WHERE c.component_type_id = ct.component_type_id
	// AND cr.child_component_id = c.component_id
	// AND cr.component_rel_type_id = 2
	// AND cr.child_component_id =
    $qb = new DBQueryBuilder($tableNamePrefix);
    $qb->appendSemicolon(false);
    $qb->addColumn("c.component_instance_name");
    $qb->addColumn("ct.component_type_name");
    $qb->addColumn("cr.parent_component_id");
    $qb->addColumn("cr.logical_desc");

    $qb->addTable("component c");
    $qb->addTable("component_type ct");
	$qb->addTable("component_rel cr");

	$qb->addWhere("c.component_type_id = ct.component_type_id");
	$qb->addWhere("cr.child_component_id = c.component_id");
    $qb->addWhere("cr.component_rel_type_id = 2");
	$qb->addWhere("cr.child_component_id = ");  // tack on id below
    $piocQuery = $qb->getQueryString(false);

    $child_component_id = $component_id;
    $found = false;

    $logical_desc = $previous_logical_desc = "";
    $component_type_name = $previous_component_type_name = "";
    $num_hops = 0;
    while (!$found && $child_component_id != 0) {
      logEntry('debug',"find parent rack for $child_component_id");
      $query = $piocQuery . $child_component_id . ';';

      if (!$piocResult = mysql_query($query, $conn)) {
        $errno = mysql_errno();
        $error = "findParentRack(): ".mysql_error();
        logEntry('critical',$error);
        return null;
      }
      if ($piocResult) {
        if ($row = mysql_fetch_array($piocResult)) {
          $previous_logical_desc = $logical_desc;
          $previous_component_type_name = $component_type_name;
          $component_name = $row['component_instance_name'];
          $component_type_name = $row['component_type_name'];
          $parent_id = $row['parent_component_id'];
          $logical_desc = $row['logical_desc'];
          
          if ($component_type_name == 'Room') {
            if ($num_hops == 1) {  # component's immediate parent is Room
              return "";
            } else {
              $found = true;
              break;
            }
          }
          $child_component_id = $parent_id;
          $num_hops++;
        } else {
          return "";
        }
      } else {
        return "";
      }
    }
    return $previous_component_type_name . ' ' . $previous_logical_desc;
  }

  function findParentRoom($dbConn, $component_id) {
    global $errno;
    global $error;

    $conn = $dbConn->getConnection();
    $tableNamePrefix = $dbConn->getTableNamePrefix();

    // Begin building query for component_type table
	// SELECT c.component_instance_name, ct.component_type_name, cr.parent_component_id, cr.logical_desc
	// FROM component c, component_type ct, component_rel cr
	// WHERE c.component_type_id = ct.component_type_id
	// AND cr.child_component_id = c.component_id
	// AND cr.component_rel_type_id = 2
	// AND cr.child_component_id =
    $qb = new DBQueryBuilder($tableNamePrefix);
    $qb->appendSemicolon(false);
    $qb->addColumn("c.component_instance_name");
    $qb->addColumn("ct.component_type_name");
    $qb->addColumn("cr.parent_component_id");
    $qb->addColumn("cr.logical_desc");

    $qb->addTable("component c");
    $qb->addTable("component_type ct");
	$qb->addTable("component_rel cr");

	$qb->addWhere("c.component_type_id = ct.component_type_id");
	$qb->addWhere("cr.child_component_id = c.component_id");
    $qb->addWhere("cr.component_rel_type_id = 2");
	$qb->addWhere("cr.child_component_id = ");  // tack on id below
    $piocQuery = $qb->getQueryString(false);

    $child_component_id = $component_id;
    $found = false;

    while (!$found && $child_component_id != 0) {
      logEntry('debug',"find parent room for $child_component_id");
      $query = $piocQuery . $child_component_id . ';';

      if (!$piocResult = mysql_query($query, $conn)) {
        $errno = mysql_errno();
        $error = "findParentRoom(): ".mysql_error();
        logEntry('critical',$error);
        return null;
      }
      if ($piocResult) {
        if ($row = mysql_fetch_array($piocResult)) {
          $component_name = $row['component_instance_name'];
          $component_type_name = $row['component_type_name'];
          $parent_id = $row['parent_component_id'];
          $logical_desc = $row['logical_desc'];

          if ($component_type_name == 'roomConstraint') {
            $found = true;
            break;
          }
          $child_component_id = $parent_id;
        } else {
          return "";
        }
      } else {
        return "";
      }
    }
    return $logical_desc;
  }


  function findParentBuilding($dbConn, $component_id) {
    global $errno;
    global $error;

    $conn = $dbConn->getConnection();
    $tableNamePrefix = $dbConn->getTableNamePrefix();

    // Begin building query for component_type table
    $qb = new DBQueryBuilder($tableNamePrefix);
    $qb->appendSemicolon(false);
    $qb->addColumn("c.component_instance_name");
    $qb->addColumn("ct.component_type_name");
    $qb->addColumn("cr.parent_component_id");
    $qb->addColumn("cr.logical_desc");

    $qb->addTable("component c");
    $qb->addTable("component_type ct");
	$qb->addTable("component_rel cr");

	$qb->addWhere("c.component_type_id = ct.component_type_id");
	$qb->addWhere("cr.child_component_id = c.component_id");
    $qb->addWhere("cr.component_rel_type_id = 2");
	$qb->addWhere("cr.child_component_id = ");  // tack on id below
    $piocQuery = $qb->getQueryString(false);

    $child_component_id = $component_id;
    $found = false;

    while (!$found && $child_component_id != 0) {
      logEntry('debug',"find parent building for $child_component_id");
      $query = $piocQuery . $child_component_id . ';';

      if (!$piocResult = mysql_query($query, $conn)) {
        $errno = mysql_errno();
        $error = "findParentIoc(): ".mysql_error();
        logEntry('critical',$error);
        return null;
      }
      if ($piocResult) {
        if ($row = mysql_fetch_array($piocResult)) {
          $component_name = $row['component_instance_name'];
          $component_type_name = $row['component_type_name'];
          $parent_id = $row['parent_component_id'];
          $logical_desc = $row['logical_desc'];

          if ($component_type_name == 'Building') {
            $found = true;
            break;
          }
          $child_component_id = $parent_id;
        } else {
          return "";
        }
      } else {
        return "";
      }
    }
    return $logical_desc;
  }
}

/*
 * Represents a list of racks (and some secondary info).
 */
class RackList {
  // an array of RackEntity
  var $rEntities;

  function RackList() {
    $this->rEntities = array();
  }

  // length of full result set
  function length() {
    if ($this->rEntities == null)
      return 0;
    else
      return count($this->rEntities);
  }

  // get a particular RackEntity by sequential index
  function getElement($idx) {
    return $this->rEntities[$idx];
  }

  function setElement($idx,$rEntity) {
    return $this->rEntities[$idx]=$rEntity;
  }

  // get full result set as an array of RackEntity
  function getArray() {
    return $this->rEntities;
  }

  /*
   * Load this object from the database. Basically, it finds all components
   * of the component type given by the componentTypeID argument. Returns
   * false if db error occurs, true otherwise.
   *
   * $dbConn - db connection identifier
   * $componentTypeID - component type ID
   */
   

  function loadFromDBForRack($dbConn,$roomConstraint) {
    global $errno;
    global $error;

    $conn = $dbConn->getConnection();
    $tableNamePrefix = $dbConn->getTableNamePrefix();
	$rackName = $_SESSION['nameDesc'];
	$groupNameConstraint = $_SESSION['groupNameConstraint'];
	$switchgearConstraint = $_SESSION['switchgearConstraint'];
	
	//logEntry('debug',"Criteria page passed ".$roomConstraint."  (db_entity)");
	//logEntry('debug',"Criteria page passed ".$namedesc."  (db_entity)");
	

    // Builds the rack query from the criteria entered on the rack viewer
    $qb = new DBQueryBuilder($tableNamePrefix);
    $qb->addColumn("ct.component_type_name");
    $qb->addColumn("c.component_instance_name");
	$qb->addColumn("ct.description");
	$qb->addColumn("c.component_id");
	$qb->addColumn("ct.component_type_id");
	$qb->addColumn("c.image_uri");
	$qb->addColumn("gn.group_name");
	$qb->addColumn("cr.logical_desc");
	
    $qb->addTable("component_rel cr");
	$qb->addTable("component c");
	$qb->addTable("component_type ct");
	$qb->addTable("aps_component apsc");
	$qb->addTable("group_name gn");
	
    $qb->addWhere("cr.child_component_id = c.component_id");
	$qb->addWhere("apsc.component_id = c.component_id");
	$qb->addWhere("gn.group_name_id = apsc.group_name_id");
	$qb->addWhere("c.component_type_id = ct.component_type_id");
	$qb->addWhere("cr.component_rel_type_id = '2'");
	
	// only add this if given
	if ($rackName) {
	$qb->addWhere("c.component_instance_name like '%".$rackName."%'");
	}
	$qb->addWhere("((c.component_type_id = '10')||(c.component_type_id = '11')||(c.component_type_id = '15')||(c.component_type_id = '20'))");
	
	// only add this if given
	if ($groupNameConstraint) {
	$qb->addWhere("gn.group_name like '%".$groupNameConstraint."%'");
	}
	
	// only add this if given
	if ($roomConstraint) {
	$qb->addWhere("cr.parent_component_id = (select c.component_id
                                          from component c, component_rel cr
                                          where cr.logical_desc = c.component_instance_name
                                          and c.component_instance_name like '$roomConstraint'
                                          limit 1)");
	}
	$qb->addWhere("cr.mark_for_delete = '0'");
    $qb->addSuffix("order by cr.logical_order, c.component_instance_name");
	//$rackQuery holds the assembled mysql query 
    $rackQuery = $qb->getQueryString();

    //logEntry('debug',$rackQuery);

    if (!$rackResult = mysql_query($rackQuery, $conn)) {
      $errno = mysql_errno();
      $error = "RackList.loadFromDB(): ".mysql_error();
      logEntry('critical',$error);
      return false;
    }
    $idx = 0;
    if ($rackResult) {
      while ($rackRow = mysql_fetch_array($rackResult)) {
	  
	  
//------- This section finds the parent room using the component from above

        $pRoom = $rackRow[3];
		
		$qb2 = new DBQueryBuilder($tableNamePrefix);
		$qb2->addColumn("c.component_instance_name as ParentRoom");
		$qb2->addTable("component c");
		$qb2->addTable("component_rel cr");
		$qb2->addWhere("c.component_id = cr.child_component_id");
		$qb2->addWhere("cr.component_rel_type_id = '2'");
		$qb2->addWhere("cr.mark_for_delete = '0'");
		$qb2->addWhere("c.component_id = (select cr.parent_component_id
		                                  from component c, component_rel cr
                                          where c.component_id = '$pRoom'
                                          and c.component_id = cr.child_component_id
                                          and cr.component_rel_type_id = '2')");
		$rackParentQuery = $qb2->getQueryString();
		
		if (!$rackParentResult = mysql_query($rackParentQuery, $conn)) {
	      $errno = mysql_errno();
	      $error = "RackList.loadFromDB(): ".mysql_error();
	      logEntry('critical',$error);
          return false;                 
       }
	   
	   if ($rackParentRow = mysql_fetch_array($rackParentResult)) {
	     $rackRow['ParentRoom'] = $rackParentRow['ParentRoom']; 
	   }

//-------	  
        $ID = $rackRow['component_id'];
        $InstanceName = $rackRow['component_instance_name'];
		$description = $rackRow['description'];
		$rackType = $rackRow['component_type_name'];
		$component_type_id = $rackRow['component_type_id'];
		$ParentRoom = $rackRow['ParentRoom'];
		$group = $rackRow['group_name'];
		$image = $rackRow['image_uri'];
		$logical_desc = $rackRow['logical_desc'];

        $rackEntity = new RackEntity($ID, $InstanceName, $rackType, $description, $component_type_id, $ParentRoom, $group, $image, $logical_desc);
        $this->rEntities[$idx++] = $rackEntity;
      }
    }
	logEntry('debug',"Search gave ".$this->length()." results. (db_entity)");
    return true;
  }

}

?>