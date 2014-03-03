<?php
/*
 * Defines business class ComponentList which contains an array of CableEntity
 *
 */

class cableEntity {
  var $ID;
  var $ComponentPortName;
  var $ComponentPortID;
  var $ComponentPortType;
  var $ComponentPortGroup;
  var $ComponentPortPinCount;
  var $comParentIOC;
  var $comParentRack;
  var $comParentRoom;
  var $comParentBldg;
  var $comLocationPath;
  var $cableInfo;
  var $oppPort;

  function cableEntity($ID,$ComponentPortName,$ComponentPortID,$ComponentPortType,$ComponentPortGroup,$ComponentPortPinCount) {
    $this->ID = $ID;
    $this->ComponentPortName = $ComponentPortName;
	$this->ComponentPortID = $ComponentPortID;
	$this->ComponentPortType = $ComponentPortType;
	$this->ComponentPortGroup = $ComponentPortGroup;
	$this->ComponentPortPinCount = $ComponentPortPinCount;
	$this->comParentIOC = $comParentIOC;
	$this->comParentRack = $comParentRack;
	$this->comParentRoom = $comParentRoom;
	$this->comParentBldg = $comParentBldg;
	$this->comLocationPath = $comLocationPath;
	$this->cableInfo = $cableInfo;
	$this->oppPort = $oppPort;
  }
  function getID() {
    return $this->ID;
  }
  function getComponentPortName() {
    return $this->ComponentPortName;
  }
  function getComponentPortID() {
    return $this->ComponentPortID;
  }
  function getComponentPortType() {
	return $this->ComponentPortType;
  }
  function getComponentPortGroup() {
	return $this->ComponentPortGroup;
  }
  function getComponentPortPinCount() {
	return $this->ComponentPortPinCount;
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
  function getcableInfo() {
	return $this->cableInfo;
  }
  function getOppPort() {
	return $this->oppPort;
  }
  function loadDetailsFromDB($dbConn) {
	$this->cableInfo = $this->findCableInfo($dbConn, $this->ComponentPortID);
	$this->oppPort = $this->findOppositePort($dbConn);
    $this->comParentIOC = $this->findParentIoc($dbConn, $this->ID);
	$this->comParentRack = $this->findParentRack($dbConn, $this->ID);
	$this->comParentRoom = $this->findParentRoom($dbConn);
	$this->comParentBldg = $this->findParentBuilding($dbConn, $this->ID);
	$this->comLocationPath = $this->findLocationPath($dbConn, $this->ID);
  }

  function findParentIoc($dbConn) {
    global $errno;
    global $error;

    $oppCompID = $_SESSION['oppCompID'];
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

    $child_component_id = $oppCompID;
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

  function findParentRack($dbConn) {
    global $errno;
    global $error;

    $oppCompID = $_SESSION['oppCompID'];
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

    $child_component_id = $oppCompID;
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

  
  function findParentRoom($dbConn) {
    global $errno;
    global $error;

    $oppCompID = $_SESSION['oppCompID'];
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

    $child_component_id = $oppCompID;
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
		  array_push($locationPath, "<b>".$component_type_name."</b>&nbsp;".$component_instance_name);
		} else {
		  $count=0;
		  $parentCount++;
		  array_push($locationPath, "<b>".$component_type_name."</b>&nbsp;".$component_instance_name);
		}
      } // end if
    } // end if
  } // end while
  $_SESSION['parentCount'] = $parentCount;
  return $locationPath;
  
  } // end function
  
  
  
  function findCableInfo($dbConn,$ComponentPortID) {             // this function finds the cable info from the cable table and
                                                                 // displays it for each port listed on the selected component
	global $errno;                                               
    global $error;
	
	$cableInfo = array();
	
	$conn = $dbConn->getConnection();
    $tableNamePrefix = $dbConn->getTableNamePrefix();
	
	$qb = new DBQueryBuilder($tableNamePrefix);
	$qb->addColumn("label");
    $qb->addColumn("color");
	$qb->addColumn("dest_desc");
	$qb->addColumn("component_port_a_id");
	$qb->addColumn("component_port_b_id");
	$qb->addTable("cable");
	$qb->addWhere("(component_port_a_id = ".$ComponentPortID.") or (component_port_b_id = ".$ComponentPortID.")");
	$cpQuery = $qb->getQueryString();
	
    if (!$cpResult = mysql_query($cpQuery, $conn)) {             // do not leave this section out after any DBQueryBuilder query.
      $errno = mysql_errno();                                    // this puts the results of the query (in this case $cpQuery)
      $error = "cableList.findCableInfo(): ".mysql_error();      // into $cpResult
      logEntry('critical',$error);
      return false;                 
    }
	
	logEntry('debug',$cpQuery);
	
	if ($cpResult) {
	  if ($cpRow = mysql_fetch_array($cpResult)) {
		$cableInfo[0] = $cpRow['label'];
		$cableInfo[1] = $cpRow['color'];
		$cableInfo[2] = $cpRow['dest_desc'];
		$port_A_id = $cpRow['component_port_a_id'];
		$port_B_id = $cpRow['component_port_b_id'];
    //  }
	//}
	$_SESSION['portEnd'] = " ";
	if ($port_A_id == $ComponentPortID) {
		$portEnd = $port_B_id;
	} elseif ($port_B_id == $ComponentPortID) {
		$portEnd = $port_A_id;
	} else {
		$portEnd = "No Cable Found";
	}}
	    logEntry('debug',$portEnd);
  }
    $_SESSION['portEnd'] = $portEnd;
	return $cableInfo;
}


function findOppositePort($dbConn) {                             // this function finds the component and port connected to the 
	                                                             // opposite end of the cable.  it pulls the $portEnd out of the session
	global $errno;                                               // which was put into the session by the function above.
    global $error;
	logEntry('debug',"portEnd = ".$portEnd);
	
	$portEnd = $_SESSION['portEnd'];
	
	$oppositePort = array();
	$conn = $dbConn->getConnection();
    $tableNamePrefix = $dbConn->getTableNamePrefix();
	
	$qb = new DBQueryBuilder($tableNamePrefix);
	$qb->addColumn("component_type_name");
	$qb->addColumn("description");
	$qb->addColumn("c.component_id");
	$qb->addColumn("component_port_name");
	$qb->addColumn("ct.component_type_id");
	$qb->addColumn("c.component_instance_name");
	$qb->addTable("component_type ct");
	$qb->addTable("component c");
	$qb->addTable("component_port cp");
	$qb->addWhere("c.component_type_id = ct.component_type_id");
	$qb->addWhere("cp.component_id = c.component_id");
	$qb->addWhere("cp.component_port_id = '$portEnd'");
	$qb->addWhere("c.component_id = (select component_id from component_port where component_port_id = '$portEnd')");
	$qb->addSuffix("group by component_type_name");
	$qb->addSuffix("order by description");
	$opQuery = $qb->getQueryString();
	
	if (!$opResult = mysql_query($opQuery, $conn)) {              // do not leave this section out after any DBQueryBuilder query.
      $errno = mysql_errno();                                     // this puts the results of the query (in this case $cpQuery)
      $error = "cableList.findOppositePort(): ".mysql_error();    // into $cpResult
      logEntry('critical',$error);
      return false;                 
    }
	
	logEntry('debug',$opQuery);
	logEntry('debug',$opResult);
	
	if ($opResult) {
	  if ($opRow = mysql_fetch_array($opResult)) {
		$oppositePort[0] = $opRow['component_type_name'];
		$oppositePort[1] = $opRow['description'];
		$oppositePort[2] = $opRow['component_id'];
		$oppositePort[3] = $opRow['component_port_name'];
		$oppositePort[4] = $opRow['component_type_id'];
		$oppositePort[5] = $opRow['component_instance_name'];
		$_SESSION['oppCompID'] = $oppositePort[2]; 
      }
	}
	return $oppositePort;
 }   
} // end class


/*
 * Represents a list of components (and some secondary info).
 */
class CableList {
  // an array of CableEntity
  var $cableEntities;

  function CableList() {
    $this->cableEntities = array();
  }

  // length of full result set
  function length() {
    if ($this->cableEntities == null)
      return 0;
    else
      return count($this->cableEntities);
  }

  // get a particular ComponentEntity by sequential index
  function getElement($idx) {
    return $this->cableEntities[$idx];
  }

  function setElement($idx,$cableEntity) {
    return $this->cableEntities[$idx]=$cableEntity;
  }

  // get full result set as an array of CableEntity
  function& getArray() {
    return $this->cableEntities;
  }


  /*
   * Load this object from the database. Basically, it finds all ports
   * of the component type given by the ID argument. Returns
   * false if db error occurs, true otherwise.
   *
   * $dbConn - db connection identifier
   * $componentTypeID - component type ID
   */
  function loadFromDB($dbConn,$label,$ID) {
    global $errno;
    global $error;

    $conn = $dbConn->getConnection();
    $tableNamePrefix = $dbConn->getTableNamePrefix();

    // Begin building query for component_type table
    $qb = new DBQueryBuilder($tableNamePrefix);
    $qb->addColumn("component_id");
    $qb->addColumn("component_port_name");
	$qb->addColumn("component_port_id");
	$qb->addColumn("component_port_type");
	$qb->addColumn("component_port_group");
	$qb->addColumn("component_port_pin_count");
    $qb->addTable("component_port cp");
	$qb->addTable("component_port_type cpt");
    $qb->addWhere("cp.component_port_type_id = cpt.component_port_type_id");
	$qb->addWhere("component_id =". $ID);
	$qb->addWhere("cp.mark_for_delete = '0'");
	$qb->addWhere("cpt.mark_for_delete = '0'");

    $cQuery = $qb->getQueryString();

    logEntry('debug',$cQuery);


    if (!$cResult = mysql_query($cQuery, $conn)) {
      $errno = mysql_errno();
      $error = "CableList.loadFromDB(): ".mysql_error();
      logEntry('critical',$error);
      return false;
    }
    $idx = 0;
    if ($cResult) {
      while ($cRow = mysql_fetch_array($cResult)) {
        $ID = $cRow['component_id'];
        $ComponentPortName = $cRow['component_port_name'];
		$ComponentPortID = $cRow['component_port_id'];
		$ComponentPortType = $cRow['component_port_type'];
		$ComponentPortGroup = $cRow['component_port_group'];
		$ComponentPortPinCount = $cRow['component_port_pin_count'];
		
        $cableEntity = new cableEntity($ID, $ComponentPortName, $ComponentPortID, $ComponentPortType, $ComponentPortGroup, $ComponentPortPinCount);
        $this->cableEntities[$idx++] = $cableEntity;
      }
    }

    return true;
  }
}

?>