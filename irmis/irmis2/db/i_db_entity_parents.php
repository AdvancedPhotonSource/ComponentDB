<?php   
/*
 * Defines business class ParentList which contains an array of parentEntity.
 * ParentList represens the "family tree" of all current PLCs. 
 */
class ParentList {
  // an array of parents, grandparents, etc
  var $parentEntities;

  function ParentList() {
    $this->parentEntities = array();
  }

  function getElement($idx) {
    return $this->parentEntities[$idx];
  }

  // return size of array
  function length() {
    if ($this->parentEntities == null)
      return 0;
    else 
      return count($this->parentEntities);
  }
  
  /*
   * getArray() returns an array of parentEntity (assuming loadFromDB has
   * been called already).
   */
  function getArray() {
    return $this->parentEntities;
  }
  
  /*
   * Conducts MySQL db transactions to initialize ParentList from the
   * device table. Returns false if db error occurs, true
   * otherwise. */

  function loadFromDB($dbConn, $compNameConstraint) {
    global $errno;
    global $error;
        
    $conn = $dbConn->getConnection();
    $tableNamePrefix = $dbConn->getTableNamePrefix();
        
    // Get plc_id from component table - this will be temporary, until 
    // this loadFromDB fc can be called by reliably passing the plc 
    // component_id to it (right now there's the possibility of component_id
    // mismatches between plc table in stage and bacchus

    $qb = new DBQueryBuilder($tableNamePrefix);

    $qb->addColumn("component_id");
    $qb->addTable("component");
    $qb->addTable("component_rel");
    $qb->addWhere('component_instance_name = "'.$compNameConstraint.'"');
    $qb->addWhere("component.component_id = component_rel.child_component_id");
    $qb->addWhere("component_rel.component_rel_type_id = 1");
    $qb->addWhere('component_rel.mark_for_delete = 0');

    $idQuery = $qb->getQueryString();
//    logEntry('debug',$idQuery);

    $idResult = mysql_query($idQuery, $conn);

    if ($idResult) {
      while ($idEntity = mysql_fetch_array($idResult)) {
        $compIdConstraint = $idEntity[0];
      }
    
      // Got PLC component_id. Now get plc parent info; specifically in the
      // housing hierarchy, till we find a room 
    
      $qb = new DBQueryBuilder($tableNamePrefix);   
      $idx = 0;
      $v='0';
      $safety_count=0;

      while ($v != '9' && $safety_count < 10)
      {
        //pseudo algorithm, as if it were to call a findParent fc recursively
	//
        //  function findParent ($component_id, &$family_tree) {
        //    u = SELECT parent_component_id from component_rel where child_component_id =
        //    $component_id and component_rel_type_id = 2;
        //    v = SELECT component_type_id FROM component where component_id = u;
        //    if v = 9 then (this is a room)
        //       w = SELECT component_instance_name FROM component where component_id = u;
        //       ? 'room= ', w;
        //       return $family_tree;
        //    else if v = 11 then(this is a enclosure)
        //       w = SELECT component_instance_name FROM component where component_id = u;
        //       ? 'enclosure= ', w;
        //       findParent ($u, &$family_tree);
        //    else if v = 10 then(this is a rack)
        //       w = SELECT component_instance_name FROM component where component_id = u;
        //       ? 'rack= ', w;
        //       findParent ($u, &$family_tree);
        //    else
        //       x = SELECT component_type_name, description FROM component_type where component_type_id = v;
        //       ? w, x ;
        //       findParent ($u, &$family_tree);
        //  }

        // implement u query

        $qb = new DBQueryBuilder($tableNamePrefix);
        $qb->addColumn('parent_component_id');
        $qb->addTable('component_rel');
        $qb->addWhere('child_component_id = '.$compIdConstraint);
        $qb->addWhere('component_rel_type_id = 2');
        $qb->addWhere('component_rel.mark_for_delete = 0');
        $parentQuery = $qb->getQueryString();
//      logEntry('debug',$parentQuery);

        if (!$parentResult = mysql_query($parentQuery, $conn)) {
          $errno = mysql_errno();
          $error = "ParentList.loadFromDB(): ".mysql_error();
          logEntry('critical',$error);
          return false;                 
        }

        if ($parentResult) {
          while ($parentEntity = mysql_fetch_array($parentResult)) {
            $u = $parentEntity[0];
          }
        }

        // implement v query
        $qb = new DBQueryBuilder($tableNamePrefix);
        $qb->addColumn('component_type_id');
        $qb->addTable('component');
        $qb->addWhere('component_id = '.$u);
        $parentQuery = $qb->getQueryString();
//      logEntry('debug',$parentQuery);

        if (!$parentResult = mysql_query($parentQuery, $conn)) {
          $errno = mysql_errno();
          $error = "ParentList.loadFromDB(): ".mysql_error();
          logEntry('critical',$error);
          return false;                 
        }

        if ($parentResult) {
          while ($parentEntity = mysql_fetch_array($parentResult)) {
            $v = $parentEntity[0];
          }
        }
  
        if ($v == '9' || $v == '10' || $v == '11')
        {
          // implement w query
          $qb = new DBQueryBuilder($tableNamePrefix);
          $qb->addColumn('component_instance_name');
          $qb->addTable('component');
          $qb->addWhere('component_id = '.$u);
          $parentQuery = $qb->getQueryString();
//        logEntry('debug',$parentQuery);

          if (!$parentResult = mysql_query($parentQuery, $conn)) {
            $errno = mysql_errno();
            $error = "ParentList.loadFromDB(): ".mysql_error();
            logEntry('critical',$error);
            return false;                 
          }
          if ($parentResult) {
            while ($parentEntity = mysql_fetch_array($parentResult)) {
              if ($v == '9') 		
                $parentEntityStr = 'room '.$parentEntity[0];
              else if ($v == '10') 		
                $parentEntityStr = 'rack '.$parentEntity[0];
              else 		
      	        $parentEntityStr = 'enclosure '.$parentEntity[0];
              $this->parentEntities[$idx] = $parentEntityStr;
//            logEntry('debug','parent is '.$parentEntityStr);
              $idx = $idx +1;
            }
          }
        }
        else
        {
          // implement w, x query
          $qb = new DBQueryBuilder($tableNamePrefix);
          $qb->addColumn('component_type_name');
          $qb->addColumn('description');
          $qb->addTable('component_type');
          $qb->addWhere('component_type_id = '.$v);
          $parentQuery = $qb->getQueryString();
//        logEntry('debug',$parentQuery);

          if (!$parentResult = mysql_query($parentQuery, $conn)) {
            $errno = mysql_errno();
            $error = "ParentList.loadFromDB(): ".mysql_error();
            logEntry('critical',$error);
            return false;                 
          }
          if ($parentResult) {
            while ($parentEntity = mysql_fetch_array($parentResult)) {
              $this->parentEntities[$idx] = $parentEntity[0].', '.$parentEntity[1];
//            logEntry('debug','parent is '.$parentEntity[0]);
              $idx = $idx +1;
            }
          }
        }
        $compIdConstraint = $u;
        $safety_count++;
      }
    }
    return true;
  }
}

?>
