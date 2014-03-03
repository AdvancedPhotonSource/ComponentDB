<?php   

class CtlParent {

  var $ctlParentEntity;
 
  function CtlParent() {
    $this->ctlParentEntity = array();
  }
  
  function getCtlParent() {
    return $this->ctlParentEntity;
  }
  
  
  /*
   * Conducts MySQL db transactions to initialize CtlParent from the
   * device table. Returns false if db error occurs, true
   * otherwise. */
  function loadFromDB($dbConn, $compNameConstraint) {
    global $errno;
    global $error;
        
    $conn = $dbConn->getConnection();
    $tableNamePrefix = $dbConn->getTableNamePrefix();
        
    // Get core plc info from device table
    
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
    logEntry('debug',$idQuery);

    $idResult = mysql_query($idQuery, $conn);

    if ($idResult) {
      while ($idEntity = mysql_fetch_array($idResult)) {
         $compIdConstraint = $idEntity[0];
      }
    
      // Got PLC component_id. Now get plc parent info; specifically in the ctl 
      // hierarchy, till we find an ioc
    
      $qb = new DBQueryBuilder($tableNamePrefix);

      $v=$compIdConstraint;
      $safety_count=0;

      while ($v != '1' && $safety_count<10)
      { 
        $qb = new DBQueryBuilder($tableNamePrefix);
        $qb->addColumn('parent_component_id');
        $qb->addColumn('logical_desc');
        $qb->addTable('component_rel');
        $qb->addWhere('child_component_id = '.$v);
        $qb->addWhere('component_rel_type_id = 1');
        $parentQuery = $qb->getQueryString();
        logEntry('debug',$parentQuery);

        if (!$parentResult = mysql_query($parentQuery, $conn)) {
          $errno = mysql_errno();
          $error = "CtlParent.loadFromDB(): ".mysql_error();
          logEntry('critical',$error);
          return false;                 
        }

        if ($parentResult) {
          $ctlParent = mysql_fetch_array($parentResult);
	  $v=$ctlParent[0];
	  $this->ctlParentEntity=$ctlParent[1];
//        logEntry('debug','parent is '.$ctlParent[0]);
        }

        $safety_count++;
      }
    }
    return true;
  }
}

?>
