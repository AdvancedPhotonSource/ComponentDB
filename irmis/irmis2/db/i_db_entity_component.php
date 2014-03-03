<?php
/*
 * Defines business class ComponentList which contains an array of ComponentEntity
 *
 */

class ComponentEntity {
  var $ID;
  var $InstanceName;
  var $ctName;
  var $group;
  var $image;
  var $logicalDesc;
  var $comParentIOC;
  var $comParentRack;
  var $comParentRoom;
  var $comParentBldg;
  var $comLocationPath;
  

  function ComponentEntity($ID,$InstanceName,$ctName,$group,$image,$logicalDesc) {
    $this->ID = $ID;
    $this->InstanceName = $InstanceName;
	$this->ctName = $ctName;
	$this->comParentIOC = $comParentIOC;
	$this->comParentRack = $comParentRack;
	$this->comParentRack = $comParentRoom;
	$this->comParentBldg = $comParentBldg;
	$this->comLocationPath = $comLocationPath;
	$this->group = $group;
	$this->image = $image;
	$this->logicalDesc = $logicalDesc;
  }
  function getID() {
    return $this->ID;
  }
  function getInstanceName() {
    return $this->InstanceName;
  }
  function getctName() {
    return $this->ctName;
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
  function getcomLocationPath() {
    return $this->comLocationPath;
  }
  function getGroup() {
	return $this->group;
  }
  function getImage() {
	  return $this->image;
  }
  function getlogicalDesc() {
	  return $this->logicalDesc;
  }
  function loadDetailsFromDB($dbConn) {
    $this->comParentIOC = $this->findParentIoc($dbConn, $this->ID);
	$this->comParentRack = $this->findParentRack($dbConn, $this->ID);
	$this->comParentRoom = $this->findParentRoom($dbConn, $this->ID);
	$this->comParentBldg = $this->findParentBuilding($dbConn, $this->ID);
	$this->comLocationPath = $this->findLocationPath($dbConn, $this->ID);
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
	$qb->addWhere("cr.mark_for_delete = 0");
	$qb->addWhere("cr.child_component_id = ");  // tack on id below
	//$qb->addWhere("cr.mark_for_delete = 0");
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
	$component_name = $previous_component_name = "";
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
		  $previous_component_name = $component_name;
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
    return $previous_component_type_name . ' ' . $previous_component_name;
  }

  function findParentRoom($dbConn, $component_id) {
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

          if ($component_type_name == 'Room') {
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
    return $component_name;
	//return $logical_desc;
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
	return $component_name;
    //return $logical_desc;
  }

function findLocationPath($dbConn, $component_id) {
    global $errno;
    global $error;
    
	$parentCount=0;
    $count = 0;
	$locationPath = array();
    while ($count != 1) {
    $conn = $dbConn->getConnection();
    $tableNamePrefix = $dbConn->getTableNamePrefix();

    // Begin building query for component_type expanded location
    $qb = new DBQueryBuilder($tableNamePrefix);
	$qb->addColumn("ct.component_type_name");
	$qb->addColumn("c.component_id");
	$qb->addColumn("c.component_instance_name");
	
	$qb->addTable("component_rel cr");
	$qb->addTable("component_type ct");
	$qb->addTable("component c");
	
	$qb->addWhere("cr.child_component_id = '$component_id'");
	$qb->addWhere("cr.component_rel_type_id = '2'");
	$qb->addWhere("c.component_type_id = ct.component_type_id");
	$qb->addWhere("cr.parent_component_id = c.component_id");
	$qb->addWhere("cr.mark_for_delete = '0'");
	
    $locationQuery = $qb->getQueryString();

    if (!$locationResult = mysql_query($locationQuery, $conn)) {
      $errno = mysql_errno();
      $error = "IOCContentsList.loadFromDB(): ".mysql_error();
      logEntry('critical',$error);
      return false;                 
    }

   if ($locationResult) {
     if ($row = mysql_fetch_array($locationResult)) {
	    $component_type_name = $row['component_type_name'];
		$component_id = $row['component_id'];
		$component_instance_name = $row['component_instance_name'];
	
		if (strstr($component_type_name, "Building")) {
		  $count=1;
		  $parentCount++;
		  array_push($locationPath, $component_type_name." ".$component_instance_name); // add a blank space in between the type_name and the instance_name
		} else {
		  $count=0;
		  $parentCount++;
		  array_push($locationPath, $component_type_name." ". $component_instance_name); // add a blank space in between the type_name and the instance_name
		}
      } // end if
    } // end if
  } // end while
  $_SESSION['parentCount'] = $parentCount;
  return $locationPath;
  
  } // end function
} // end class

/*
 * Represents a list of components (and some secondary info).
 */
class ComponentList {
  // an array of ComponentEntity
  var $cEntities;

  function ComponentList() {
    $this->cEntities = array();
  }

  // length of full result set
  function length() {
    if ($this->cEntities == null)
      return 0;
    else
      return count($this->cEntities);
  }

  // get a particular ComponentEntity by sequential index
  function getElement($idx) {
    return $this->cEntities[$idx];
  }

  function setElement($idx,$cEntity) {
    return $this->cEntities[$idx]=$cEntity;
  }

  // get full result set as an array of ComponentEntity
  function& getArray() {
    return $this->cEntities;
  }

  /*
   * Load this object from the database. Basically, it finds all components
   * of the component type given by the componentTypeID argument. Returns
   * false if db error occurs, true otherwise.
   *
   * $dbConn - db connection identifier
   * $componentTypeID - component type ID
   */
  function loadFromDBForComponent($dbConn,$ID) {
    global $errno;
    global $error;

    $conn = $dbConn->getConnection();
    $tableNamePrefix = $dbConn->getTableNamePrefix();

    // Begin building query for component_type table
    $qb = new DBQueryBuilder($tableNamePrefix);
    $qb->addColumn("c.component_id");
    $qb->addColumn("c.component_instance_name");
	$qb->addColumn("c.image_uri");
	$qb->addColumn("ct.component_type_name");
	$qb->addColumn("gn.group_name");
	$qb->addColumn("cr.logical_desc");
	
    $qb->addTable("component c");
	$qb->addTable("component_type ct");
	$qb->addTable("aps_component apsc");
	$qb->addTable("group_name gn");
	$qb->addTable("component_rel cr");
	
    $qb->addWhere("c.component_type_id = ". $ID);
	$qb->addWhere("c.component_type_id = ct.component_type_id");
	$qb->addWhere("c.mark_for_delete = '0'");
	$qb->addWhere("apsc.component_id = c.component_id");
	$qb->addWhere("c.component_id = cr.child_component_id");
	$qb->addWhere("cr.component_rel_type_id = '2'");
	$qb->addWhere("apsc.group_name_id = gn.group_name_id");
	$qb->addWhere("cr.mark_for_delete = '0'");
	

    $cQuery = $qb->getQueryString();

    logEntry('debug',$cQuery);

    if (!$cResult = mysql_query($cQuery, $conn)) {
      $errno = mysql_errno();
      $error = "ComponentList.loadFromDB(): ".mysql_error();
      logEntry('critical',$error);
      return false;
    }
    $idx = 0;
    if ($cResult) {
      while ($cRow = mysql_fetch_array($cResult)) {
        $ID = $cRow['component_id'];
        $InstanceName = $cRow['component_instance_name'];
		$ctName = $cRow['component_type_name'];
		$group = $cRow['group_name'];
		$image = $cRow['image_uri'];
		$logicalDesc = $cRow['logical_desc'];

        $cEntity = new ComponentEntity($ID, $InstanceName, $ctName, $group, $image, $logicalDesc);
        $this->cEntities[$idx++] = $cEntity;
      }
    }

    return true;
  }

}

?>