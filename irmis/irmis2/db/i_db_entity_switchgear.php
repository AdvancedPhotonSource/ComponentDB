<?php
/*
 * Defines business class RackList which contains an array of ComponentEntity
 *
 */

class SGEntity {
  var $ID;
  var $InstanceName;
  var $SGType;
  var $description;
  var $componentTypeID;
  var $comParentIOC;
  var $comParentRack;
  var $comParentRoom;
  var $comParentBldg;
  var $ParentRack;
  var $ParentRoom;
  var $ParentBldg;
  //var $group;
  var $image;
  var $logicalDesc;
  var $comLocationPath;

  function SGEntity($ID,$InstanceName,$SGType,$description,$componentTypeID,$ParentRack,$ParentRoom,$ParentBldg,/* $group,*/ $image,$logicalDesc) {
    $this->ID = $ID;
    $this->InstanceName = $InstanceName;
	$this->SGType = $SGType;
	$this->description = $description;
	$this->componentTypeID = $componentTypeID;
	$this->comParentIOC = $comParentIOC;
	$this->comParentRack = $comParentRack;
	$this->comParentRack = $comParentRoom;
	$this->comParentBldg = $comParentBldg;
	$this->ParentRack = $ParentRack;
	$this->ParentRoom = $ParentRoom;
	$this->ParentBldg = $ParentBldg;
	//$this->group = $group;
	$this->image = $image;
	$this->logicalDesc = $logicalDesc;
	$this->comLocationPath = $comLocationPath;
  }
  function getID() {
    return $this->ID;
  }
  function getInstanceName() {
    return $this->InstanceName;
  }
  function getSGType() {
    return $this->SGType;
  }
  function getDescription() {
    return $this->description;
  }
  function getcomponentTypeID() {
    return $this->componentTypeID;
  }
  //function getcomParentIOC() {
  //  return $this->comParentIOC;
  //}
  //function getcomParentRack() {
  //  return $this->comParentRack;
  //}
  function getcomParentRoom() {
    return $this->comParentRoom;
  }
  function getcomParentBldg() {
    return $this->comParentBldg;
  }
  function getParentRack() {
    return $this->ParentRack;
  }  
  function getParentRoom() {
    return $this->ParentRoom;
  }
  function getParentBldg() {
    return $this->ParentBldg;
  }  
  //function getGroup() {
  //  return $this->group;
  //}
  function getImage() {
	return $this->image;
  }
  function getlogicalDesc() {
	return $this->logicalDesc;
  }
  function getcomLocationPath() {
    return $this->comLocationPath;
  }
  
  function loadDetailsFromDB($dbConn) {
  //  $this->comParentIOC = $this->findParentIoc($dbConn, $this->ID);
  //	$this->comParentRack = $this->findParentRack($dbConn, $this->ID);
	$this->comParentRoom = $this->findParentRoom($dbConn, $this->ID);
	$this->comParentBldg = $this->findParentBuilding($dbConn, $this->ID);
	$this->comLocationPath = $this->findLocationPath($dbConn, $this->ID);
  }
/*
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
*/
/*
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
*/
/*
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
*/
/*
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

*/




/*

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

}

*/

}


/*
 * Represents a list of racks (and some secondary info).
 */
class SGList {
  // an array of SGEntity
  var $rEntities;

  function SGList() {
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
   

  function loadFromDBForSG($dbConn,$switchgearConstraint) {
    global $errno;
    global $error;

    $conn = $dbConn->getConnection();
    $tableNamePrefix = $dbConn->getTableNamePrefix();
	//$rackName = $_SESSION['nameDesc'];
	//$groupNameConstraint = $_SESSION['groupNameConstraint'];
	$SG = $_SESSION['switchgearConstraint'];
	
	//logEntry('debug',"Criteria page passed ".$roomConstraint."  (db_entity)");
	//logEntry('debug',"Criteria page passed ".$namedesc."  (db_entity)");
	

    // Builds the switchgear query from the criteria entered on the power viewer
    $qb = new DBQueryBuilder($tableNamePrefix);
    $qb->addColumn("ct.component_type_name");
    $qb->addColumn("c.component_instance_name");
	$qb->addColumn("ct.description");
	$qb->addColumn("c.component_id");
	$qb->addColumn("ct.component_type_id");
	$qb->addColumn("c.image_uri");
	$qb->addColumn("cr.logical_desc");
	//$qb->addColumn("gn.group_name");
	
    $qb->addTable("component_rel cr");
	$qb->addTable("component c");
	$qb->addTable("component_type ct");
	//$qb->addTable("aps_component apsc");
	//$qb->addTable("group_name gn");
	
    //$qb->addWhere("cr.parent_component_id = '$switchgearConstraint'");
	$qb->addWhere("c.component_id = cr.child_component_id");
	//$qb->addWhere("gn.group_name_id = apsc.group_name_id");
	$qb->addWhere("c.component_type_id = ct.component_type_id");
	$qb->addWhere("cr.component_rel_type_id = '3'");
	
	$qb->addWhere("cr.parent_component_id = (select cr.child_component_id
                                          from component_rel cr
                                          where cr.logical_desc like '$switchgearConstraint'
                                          and cr.parent_component_id = '3'
                                          limit 1)");
	
	
	// only add this if given
	//if ($rackName) {
	//$qb->addWhere("c.component_instance_name like '%".$rackName."%'");
	//}
	//$qb->addWhere("((c.component_type_id = '10')||(c.component_type_id = '11')||(c.component_type_id = '15')||(c.component_type_id = '20'))");
	
	// only add this if given
	//if ($groupNameConstraint) {
	//$qb->addWhere("gn.group_name like '%".$groupNameConstraint."%'");
	//}
	
	// only add this if given
	//if ($switchgearConstraint) {
	//$qb->addWhere("cr.parent_component_id = (select c.component_id
    //                                      from component c, component_rel cr
    //                                      where cr.logical_desc = c.component_instance_name
    //                                      and c.component_instance_name like '$switchgearConstraint'
    //                                      limit 1)");
	//}
	$qb->addWhere("c.mark_for_delete = '0'");
    $qb->addSuffix("order by cr.logical_order, c.component_instance_name");
	//$rackQuery holds the assembled mysql query 
    $SGQuery = $qb->getQueryString();

    //logEntry('debug',$SGQuery);

    if (!$SGResult = mysql_query($SGQuery, $conn)) {
      $errno = mysql_errno();
      $error = "SGList.loadFromDB(): ".mysql_error();
      logEntry('critical',$error);
      return false;
    }
    $idx = 0;
    if ($SGResult) {
      while ($SGRow = mysql_fetch_array($SGResult)) {
	  
	  
//------------------------------------------------------------------------------	  
	     $parentCount=0;
         $count = 0;
		 $component_id = $SGRow[3];
	     $locationPath = array();
         while ($count != 1) {
		
		$qb1 = new DBQueryBuilder($tableNamePrefix);
		$qb1->addColumn("c.component_instance_name");
		$qb1->addColumn("c.component_id");
		$qb1->addColumn("ct.component_type_name");
		
		$qb1->addTable("component c");
		$qb1->addTable("component_rel cr");
		$qb1->addTable("component_type ct");
		
		$qb1->addWhere("cr.child_component_id = '$component_id'");
		$qb1->addWhere("cr.component_rel_type_id = '2'");
		$qb1->addWhere("c.component_type_id = ct.component_type_id");
		$qb1->addWhere("cr.parent_component_id = c.component_id");
		$qb1->addWhere("cr.mark_for_delete = '0'");
		$rackParentQuery = $qb1->getQueryString();
		
		if (!$rackParentResult = mysql_query($rackParentQuery, $conn)) {
	      $errno = mysql_errno();
	      $error = "RackList.loadFromDB(): ".mysql_error();
	      logEntry('critical',$error);
          return false;                 
       }
	   
	   if ($rackParentResult&&$parentCount < 12) {
	   if ($rackParentRow = mysql_fetch_array($rackParentResult)) {
	     $component_instance_name = $rackParentRow['component_instance_name']; 
		 $component_id = $rackParentRow['component_id']; 
		 $component_type_name = $rackParentRow['component_type_name']; 
	   }
       if (strstr($component_type_name, "Rack")) {
		  $count=0;
		  $parentCount++;
		  array_push($locationPath, $component_instance_name);
		  $SGRow['ParentRack'] = array_pop($locationPath);	   
	   } else if (strstr($component_type_name, "Room")) {
		  $count=0;
		  $parentCount++;
		  array_push($locationPath, $component_instance_name);
		  $SGRow['ParentRoom'] = array_pop($locationPath);
	   } else if (strstr($component_type_name, "Building")) {
		  $count=1;
		  $parentCount++;
		  array_push($locationPath, $component_instance_name);
		  $SGRow['ParentBldg'] = array_pop($locationPath);
	   } else {
		  $count=0;
		  $parentCount++;
		  $SGRow['ParentRack'] = "";
		  $SGRow['ParentRoom'] = "";
		  $SGRow['ParentBldg'] = "";
	   }  //end if
	 }  //end if
   }  //end while
//return $locationPath;

//------------------------------------------------------------------------------	  
	  
	  
	  
//------- This section finds the parent room using the component from above----------------------------------

        $pRoom = $SGRow[3];
		
		/*$qb2 = new DBQueryBuilder($tableNamePrefix);
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
	     $SGRow['ParentRoom'] = $rackParentRow['ParentRoom']; 
	   }*/

//----------------------------------------------------------------------------------------------------------
        $ID = $SGRow['component_id'];
        $InstanceName = $SGRow['component_instance_name'];
		$description = $SGRow['description'];
		$SGType = $SGRow['component_type_name'];
		$component_type_id = $SGRow['component_type_id'];
		$ParentRack = $SGRow['ParentRack'];
		$ParentRoom = $SGRow['ParentRoom'];
		$ParentBldg = $SGRow['ParentBldg'];
		//$group = $SGRow['group_name'];
		$image = $SGRow['image_uri'];
		$logicalDesc = $SGRow['logical_desc'];

        $SGEntity = new SGEntity($ID, $InstanceName, $SGType, $description, $component_type_id, $ParentRack, $ParentRoom, $ParentBldg, /* $group,*/ $image, $logicalDesc);
        $this->rEntities[$idx++] = $SGEntity;
      }
    }
	logEntry('debug',"Search gave ".$this->length()." results. (db_entity)");
    return true;
  }

}

?>