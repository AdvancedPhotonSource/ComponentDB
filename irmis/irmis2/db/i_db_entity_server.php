<?php
/*
 * Defines business class ServerList which contains an array of ServerEntity.
 */

/*
 * ServerEntity corresponds to a single row from the IRMIS server table.
 */
class ServerEntity {
    var $serverName;
    var $operatingSystem;
    var $cognizantFirstName;
    var $cognizantLastName;
    var $serverDescription;


  function ServerEntity($serverName, $operatingSystem, $cognizantFirstName, $cognizantLastName, $serverDescription, $componentID) {

    $this->serverName = $serverName;
    $this->operatingSystem = $operatingSystem;
  	$this->cognizantFirstName = $cognizantFirstName;
  	$this->cognizantLastName = $cognizantLastName;
  	$this->serverDescription = $serverDescription;
  	$this->componentID = $componentID;

  }

  function getServerName() {
    return $this->serverName;
  }
  function getOperatingSystem() {
    return $this->operatingSystem;
  }
  function getCognizantFirstName() {
    return $this->cognizantFirstName;
  }
  function getCognizantLastName() {
      return $this->cognizantLastName;
  }
  function getServerDescription() {
        return $this->serverDescription;
  }
  function getComponentID() {
  	return $this->componentID;
  }

  function getServerRoom($dbConn, $component_id) {
      global $errno;
      global $error;

      $conn = $dbConn->getConnection();

    // Begin building query for component_type table

  	// SELECT c.component_instance_name, ct.component_type_name, cr.parent_component_id, cr.logical_desc
  	// FROM component c, component_type ct, component_rel cr
  	// WHERE c.component_type_id = ct.component_type_id
  	// AND cr.child_component_id = c.component_id
  	// AND cr.component_rel_type_id = 2
  	// AND cr.child_component_id =


      $qb = new DBQueryBuilder();
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
      // return $logical_desc;  Sometimes this field is empty!!!

      return $component_name;
  }

  function getServerRack($dbConn, $component_id) {
        global $errno;
        global $error;


		$conn = $dbConn->getConnection();


        // Begin building query for component_type table
    	// SELECT c.component_instance_name, ct.component_type_name, cr.parent_component_id, cr.logical_desc
    	// FROM component c, component_type ct, component_rel cr
    	// WHERE c.component_type_id = ct.component_type_id
    	// AND cr.child_component_id = c.component_id
    	// AND cr.component_rel_type_id = 2
    	// AND cr.child_component_id =
        $qb = new DBQueryBuilder();
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
        $component_name = $previous_component_name = "";

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
              $previous_component_instance_name = $component_name;

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

		/// return $previous_component_type_name.' '.$previous_component_instance_name;  This definition may be used in the future when
		///			data is cleaned up in IRMIS for the idt editor component "locator" text box entry field.   D. Quock 2/3/09

      }

}


/*
 * ServerList is a business object which contains a collection of information
 * representing the list of all current servers in the IRMIS database. It is
 * essentially a wrapper for an array of ServerEntity, along with the standard
 * "loadFromDB()" function and other utility functions.
 */
class ServerList {
  // an array of ServerEntity
  var $serverEntities;

  function ServerList() {
    $this->serverEntities = array();
  }

  function getElement($idx) {
    return $this->serverEntities[$idx];
  }

  // return size of array
  function length() {
    if ($this->serverEntities == null)
      return 0;
    else
      return count($this->serverEntities);
  }

  /*
   * getArray() returns an array of ServerEntity (assuming loadFromDB has
   * been called already).
   */
  function getArray() {
    return $this->serverEntities;
  }

  // Returns getElementForServerName equal to $serverName

  function getElementForServerName($serverName) {
    foreach ($this->serverEntities as $serverEntity) {
	  if ($serverEntity->getServerName() == $serverName) {
	    return $serverEntity;
	  }
	 }
	 logEntry('debug',"Unable to find ServerEntity for serverName $serverName");
	 return null;
  }

  // Returns ServerEntity with primary key $id from server table
  function getElementForId($id) {
    foreach ($this->serverEntities as $serverEntity) {
      if ($serverEntity->getServerID() == $id) {
        return $serverEntity;
      }
    }
    logEntry('debug',"Unable to find ServerEntity for id $id");
    return null;
  }

  /*
   * Conducts MySQL db transactions to initialize ServerList from the
   * server table. Returns false if db error occurs, true
   * otherwise. */
  function loadFromDB($dbConn, $serverNameConstraint) {
    global $errno;
    global $error;

    $conn = $dbConn->getConnection();
    $tableNamePrefix = $dbConn->getTableNamePrefix();

    // Get core server info from server table
    $qb = new DBQueryBuilder($tableNamePrefix);
    // $qb->addColumn("server.server_description");

    $qb->addColumn("component.component_instance_name");
    $qb->addColumn("server.operating_system");
    $qb->addColumn("person.first_nm");
	$qb->addColumn("person.last_nm");
	$qb->addColumn("server.server_description");
	$qb->addColumn("server.component_id");

	$qb->addTable("server");
	$qb->addTable("person");
	$qb->addTable("component");
	$qb->addWhere("server.component_id=component.component_id");
	$qb->addWhere("server.cognizant_id=person.person_id");

	if ($serverNameConstraint) {
		$qb->addWhere("component.component_instance_name like '%".$serverNameConstraint."%'");
	}

    // only add this if given
	$operating_SystemConstraint = $_SESSION['operating_system'];
	if ($operating_SystemConstraint) {
      $qb->addWhere("server.operating_system like '%".$operating_SystemConstraint."%'");
    }

	// only add this if given
	$cognizantConstraint = $_SESSION['server_cognizant'];
	if ($cognizantConstraint) {
      $qb->addWhere("person.last_nm like '%".$cognizantConstraint."%'");
	}

	$qb->addSuffix("order by component_instance_name");
    $serverQuery = $qb->getQueryString();
    logEntry('debug',$serverQuery);

    if (!$serverResult = mysql_query($serverQuery, $conn)) {
      $errno = mysql_errno();
      $error = "ServerList.loadFromDB(): ".mysql_error();
      logEntry('critical',$error);
      return false;
    }

    $idx = 0;
    if ($serverResult) {
      while ($serverRow = mysql_fetch_array($serverResult)) {
        $serverEntity = new ServerEntity($serverRow['component_instance_name'], $serverRow['operating_system'], $serverRow['first_nm'], $serverRow['last_nm'], $serverRow['server_description'], $serverRow['component_id']);
        $this->serverEntities[$idx] = $serverEntity;
        $idx = $idx +1;
      }
    }
    return true;
  }
}

?>