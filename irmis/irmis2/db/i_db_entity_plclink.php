<?php   

class PlcLink {
  var $plcLinkEntity;

  function PlcLink() {
    $this->plcLinkEntity = array();
  }
  function getPlcLink() {
    return $this->plcLinkEntity;
  }
  /*
   * Conducts MySQL db transactions to initialize PlcLink from the
   * device table. Returns false if db error occurs, true
   * otherwise. */

  function loadFromDB($dbConn, $plcNameConstraint) {
    global $errno;
    global $error;
        
    $conn = $dbConn->getConnection();
    $tableNamePrefix = $dbConn->getTableNamePrefix();
        
    // Get plc id from device table

    $qb = new DBQueryBuilder($tableNamePrefix);
    
    $qb->addColumn("component_id");
    $qb->addTable("component");
    $qb->addTable("component_rel");
    $qb->addWhere('component_instance_name = "'.$plcNameConstraint.'"');
    $qb->addWhere("component.component_id = component_rel.child_component_id");
    $qb->addWhere("component_rel.component_rel_type_id = 1");
    $qb->addWhere('component_rel.mark_for_delete = 0');
    
    $idQuery = $qb->getQueryString();

    $idResult = mysql_query($idQuery, $conn);

    if ($idResult) {
      while ($idEntity = mysql_fetch_array($idResult)) {
         $compIdConstraint = $idEntity[0];
      }
    
      // Got PLC component_id. Now get plc parent info
    
      $qb = new DBQueryBuilder($tableNamePrefix);

      $v=$compIdConstraint;
      $safety_count=0;
      while ($v != '722' && $v != '480'  && $v != '726' && $safety_count < 10)
      {
        // implement u query
        $qb = new DBQueryBuilder($tableNamePrefix);
        $qb->addColumn('parent_component_id');
        $qb->addTable('component_rel');
        $qb->addWhere('child_component_id = '.$compIdConstraint);
        $qb->addWhere('component_rel_type_id = 1');
        $linkQuery = $qb->getQueryString();

        if (!$linkResult = mysql_query($linkQuery, $conn)) {
          $errno = mysql_errno();
          $error = "link.loadFromDB(): ".mysql_error();
          logEntry('critical',$error);
          return false;                 
        }

        if ($linkResult) {
          while ($plcLinkEntity = mysql_fetch_array($linkResult)) {
            $u = $plcLinkEntity[0];
          }
        }

        // implement v query
        $qb = new DBQueryBuilder($tableNamePrefix);
        $qb->addColumn('component_type_id');
        $qb->addColumn('component_instance_name');
        $qb->addTable('component');
        $qb->addWhere('component_id = '.$u);
        $linkQuery = $qb->getQueryString();

        if (!$linkResult = mysql_query($linkQuery, $conn)) {
          $errno = mysql_errno();
          $error = "link.loadFromDB(): ".mysql_error();
          logEntry('critical',$error);
          return false;                 
        }

        if ($linkResult) {
          while ($plcLinkEntity = mysql_fetch_array($linkResult)) {
            $v = $plcLinkEntity[0];
	    if ($v == '727' || ($v == '722' && !$j)){
                $j = $plcLinkEntity[1];
            }	    
          }
        }

        // implement z query
        $qb = new DBQueryBuilder($tableNamePrefix);
        $qb->addColumn('logical_desc');
        $qb->addTable('component_rel');
        $qb->addWhere('child_component_id = '.$u);
        $qb->addWhere('component_rel_type_id = 1');
        $linkQuery = $qb->getQueryString();

        if (!$linkResult = mysql_query($linkQuery, $conn)) {
          $errno = mysql_errno();
          $error = "link.loadFromDB(): ".mysql_error();
          logEntry('critical',$error);
          return false;                 
        }

        if ($linkResult) {
          while ($plcLinkEntity = mysql_fetch_array($linkResult)) {
            $z = $plcLinkEntity[0];
          }
        }

        if ($v == '722' || $v == '480') // this is a serial link
          $link=$j;
        else if ($v == '727') // this is a DirectNet link
          $link='P'.$z.' ';         
        else if ($v == '157') // this is a BUG500
          $link='N'.$z.' '.$link;
        else if ($v == '726') // this is a BBlink
          $link='L'.$z.' '.$link;
        //logEntry('debug','v = '.$v);

        $compIdConstraint = $u;
        $safety_count++;
      }
    } 
    $this->plcLinkEntity = $link;
    logEntry('debug','PLC: '.$plcNameConstraint.', link: '.$link);
    return true;
  }
}

?>
