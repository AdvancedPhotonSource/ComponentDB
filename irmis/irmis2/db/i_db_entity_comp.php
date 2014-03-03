<?php   
/*
 * Defines business class IOCList which contains an array of IOCEntity.
 */

/*
 * IOCEntity corresponds to a single row from the IRMIS ioc table.
 */
class IOCEntity {
  var $iocID;
  var $iocName;
  var $active;
  var $system;
  var $location;
  var $general_functions;
  var $sys_boot_line;
  var $ioc_boot_date;
  var $cog_developer;
  var $cog_tech;
  var $pre_boot_instr;
  var $post_boot_instr;
  var $power_cycle_caution;
  var $sysreset_reqd;
  var $inhibit_auto_reboot;
  var $PrimEnetSwRackNo;
  var $PrimEnetSwitch;
  var $PrimEnetBlade;
  var $PrimEnetPort;
  var $PrimEnetMedConvCh;
  var $PrimMediaConvPort;
  var $SecEnetSwRackNo;
  var $SecEnetSwitch;
  var $SecEnetBlade;
  var $SecEnetPort;
  var $SecEnetMedConvCh;
  var $SecMedConvPort;
  var $TermServRackNo;
  var $TermServName;
  var $TermServPort;
  var $TermServFiberConvCh;
  var $TermServFiberConvPort;
  
  
  function IOCEntity($iocID, $iocName, $active, $system, $location, 
           $general_functions, $sys_boot_line, $ioc_boot_date, $cog_developer, 
		   $cog_tech, $pre_boot_instr, $post_boot_instr, $power_cycle_caution, 
		   $sysreset_reqd, $inhibit_auto_reboot, $PrimEnetSwRackNo, $PrimEnetSwitch,
		   $PrimEnetBlade, $PrimEnetPort, $PrimEnetMedConvCh, $PrimMediaConvPort,
		   $SecEnetSwRackNo, $SecEnetSwitch, $SecEnetBlade, $SecEnetPort, $SecEnetMedConvCh,
		   $SecMedConvPort, $TermServRackNo, $TermServName, $TermServPort, $TermServFiberConvCh,
		   $TermServFiberConvPort) {
    $this->iocID = $iocID;
    $this->iocName = $iocName;
    $this->active = $active;
    $this->system = $system;
	$this->location = $location;
	$this->general_functions = $general_functions;
	$this->sys_boot_line = $sys_boot_line;
	$this->ioc_boot_date = $ioc_boot_date;
	$this->cog_developer = $cog_developer;
	$this->cog_tech = $cog_tech;
	$this->pre_boot_instr = $pre_boot_instr;
	$this->post_boot_instr = $post_boot_instr;
	$this->power_cycle_caution = $power_cycle_caution;
	$this->sysreset_reqd = $sysreset_reqd;
	$this->inhibit_auto_reboot = $inhibit_auto_reboot;
	$this->PrimEnetSwRackNo = $PrimEnetSwRackNo;
	$this->PrimEnetSwitch = $PrimEnetSwitch;
	$this->PrimEnetBlade = $PrimEnetBlade;
	$this->PrimEnetPort = $PrimEnetPort;
	$this->PrimEnetMedConvCh = $PrimEnetMedConvCh;
	$this->PrimMediaConvPort = $PrimMediaConvPort;
	$this->SecEnetSwRackNo = $SecEnetSwRackNo;
	$this->SecEnetSwitch = $SecEnetSwitch;
	$this->SecEnetBlade = $SecEnetBlade;
	$this->SecEnetPort = $SecEnetPort;
	$this->SecEnetMedConvCh = $SecEnetMedConvCh;
	$this->SecMedConvPort = $SecMedConvPort;
	$this->TermServRackNo = $TermServRackNo;
	$this->TermServName = $TermServName;
	$this->TermServPort = $TermServPort;
	$this->TermServFiberConvCh = $TermServFiberConvCh;
	$this->TermServFiberConvPort = $TermServFiberConvPort;
  }
  function getIocID() {
    return $this->iocID;
  }
  function getIocName() {
    return $this->iocName;
  }
  function getActive() {
    return $this->active;
  }
  function getSystem() {
    return $this->system;
  }
  function getLocation() {
    return $this->location;
  }
  function getGeneralFunctions() {
    return $this->general_functions;
  }
  function getSysBootLine()  {
    return $this->sys_boot_line;
  }
  function getIocBootDate()  {
    return $this->ioc_boot_date;
  }
  function getCogDeveloper() {
    return $this->cog_developer;
  }
  function getCogTech() {
    return $this->cog_tech;
  }
  function getPreBoot() {
    return $this->pre_boot_instr;
  }
  function getPostBoot() {
    return $this->post_boot_instr;
  }
  function getPowerCycleCaution() {
    return $this->power_cycle_caution;
  }
  function getSysResetReqd() {
    return $this->sysreset_reqd;
  }
  function getInhibitAutoReboot() {
    return $this->inhibit_auto_reboot;
  }
  function getPrimEnetSwRackNo() {
    return $this->PrimEnetSwRackNo;
  }
  function getPrimEnetSwitch() {
    return $this->PrimEnetSwitch;
  }
  function getPrimEnetBlade() {
    return $this->PrimEnetBlade;
  }
  function getPrimEnetPort() {
    return $this->PrimEnetPort;
  }
  function getPrimEnetMedConvCh() {
    return $this->PrimEnetMedConvCh;
  }
  function getPrimMediaConvPort() {
    return $this->PrimMediaConvPort;
  }
  function getSecMedConvPort() {
    return $this->SecMedConvPort;
  }
  function getSecEnetSwitch() {
    return $this->SecEnetSwitch;
  }
  function getSecEnetBlade() {
    return $this->SecEnetBlade;
  }
  function getSecEnetPort() {
    return $this->SecEnetPort;
  }
  function getSecEnetMedConvCh() {
    return $this->SecEnetMedConvCh;
  }
  function getSecEnetSwRackNo() {
    return $this->SecEnetSwRackNo;
  }
  function getTermServRackNo() {
    return $this->TermServRackNo;
  }
  function getTermServName() {
    return $this->TermServName;
  }
  function getTermServPort() {
    return $this->TermServPort;
  }
  function getTermServFiberConvCh() {
    return $this->TermServFiberConvCh;
  }
  function getTermServFiberConvPort() {
    return $this->TermServFiberConvPort;
  }
}


/*
 * IOCList is a business object which contains a collection of information
 * representing the list of all current IOC's in the IRMIS database. It is
 * essentially a wrapper for an array of IOCEntity, along with the standard
 * "loadFromDB()" function and other utility functions.
 */
class IOCList {
  // an array of IOCEntity
  var $iocEntities;

  function IOCList() {
    $this->iocEntities = array();
  }

  function getElement($idx) {
    return $this->iocEntities[$idx];
  }

  // return size of array
  function length() {
    if ($this->iocEntities == null)
      return 0;
    else 
      return count($this->iocEntities);
  }
  
  /*
   * getArray() returns an array of IOCEntity (assuming loadFromDB has
   * been called already).
   */
  function getArray() {
    return $this->iocEntities;
  }
    
  // Returns getElementForIocName equal to $iocName	
  function getElementForIocName($iocName) {
    foreach ($this->iocEntities as $iocEntity) {
	  if ($iocEntity->getIocName() == $iocName) {
	    return $iocEntity;
	  }
	 }
	 logEntry('debug',"Unable to find IocEntity for iocName $iocName");
	 return null;
  }
	
  // Returns IOCEntity with primary key $id from ioc table
  function getElementForId($id) {
    foreach ($this->iocEntities as $iocEntity) {
      if ($iocEntity->getIocID() == $id) {
        return $iocEntity;
      }    
    }
    logEntry('debug',"Unable to find IocEntity for id $id");
    return null;
  }

  /*
   * Conducts MySQL db transactions to initialize IOCList from the
   * ioc table. Returns false if db error occurs, true
   * otherwise. */
  function loadFromDB($dbConn, $iocNameConstraint) {
    global $errno;
    global $error;
        
    $conn = $dbConn->getConnection();
    $tableNamePrefix = $dbConn->getTableNamePrefix();
        
    // Get core ioc info from ioc table
    $qb = new DBQueryBuilder($tableNamePrefix);
    $qb->addColumn("ioc.*");
    $qb->addColumn("aps_ioc.*");
	$qb->addColumn("ioc_boot.sys_boot_line");
	$qb->addColumn("ioc_boot.ioc_boot_date");
	$qb->addColumn("pt1.last_nm cog_developer");
	$qb->addColumn("pt2.last_nm cog_tech");
    $qb->addTable("ioc");
	$qb->addTable("ioc_boot");
	$qb->addTable("aps_ioc left join person pt1 on aps_ioc.cog_developer_id=pt1.person_id left join person pt2 on aps_ioc.cog_technician_id=pt2.person_id");
	//$qb->addTable("person pt2");
	$qb->addWhere("aps_ioc.ioc_id = ioc.ioc_id");
	$qb->addWhere("ioc.ioc_id=ioc_boot.ioc_id"); 
    // only add this if given
	if ($iocNameConstraint) {
      // convert the * to %
      $iocNameConstraint = str_replace("*","%",$iocNameConstraint);      
      $qb->addWhere("ioc_nm like '".$iocNameConstraint."'");
	}
	// only add this if given
	$iocSystemConstraint = $_SESSION['iocSystemConstraint'];
	if ($iocSystemConstraint) {
      $qb->addWhere("system like '".$iocSystemConstraint."'"); 
    }
	// only add this if given
	$cogDeveloper = $_SESSION['cogDeveloper'];
	if ($cogDeveloper) {  
      $qb->addWhere("pt1.last_nm like '".$cogDeveloper."'");
	}
	// only add this if given
	$cogTech = $_SESSION['cogTech'];
	if ($cogTech) {  
      $qb->addWhere("pt2.last_nm like '".$cogTech."'"); 
    }
	$qb->addSuffix("group by ioc_nm");
	$qb->addSuffix("order by ioc_nm");
    $iocQuery = $qb->getQueryString();
    logEntry('debug',$iocQuery);

    if (!$iocResult = mysql_query($iocQuery, $conn)) {
      $errno = mysql_errno();
      $error = "IOCList.loadFromDB(): ".mysql_error();
      logEntry('critical',$error);
      return false;                 
    }

    $idx = 0;
    if ($iocResult) {
      while ($iocRow = mysql_fetch_array($iocResult)) {
        $iocEntity = new IOCEntity($iocRow['ioc_id'], $iocRow['ioc_nm'], $iocRow['active'], 
		$iocRow['system'], $iocRow['location'], $iocRow{'general_functions'}, 
		$iocRow['sys_boot_line'], $iocRow['ioc_boot_date'], $iocRow['cog_developer'], $iocRow['cog_tech'],
		$iocRow['pre_boot_instr'], $iocRow['post_boot_instr'], $iocRow['power_cycle_caution'],
		$iocRow['sysreset_reqd'], $iocRow['inhibit_auto_reboot'], $iocRow['PrimEnetSwRackNo'],
		$iocRow['PrimEnetSwitch'], $iocRow['PrimEnetBlade'], $iocRow['PrimEnetPort'], $iocRow['PrimEnetMedConvCh'],
		$iocRow['PrimMediaConvPort'], $iocRow['SecEnetSwRackNo'], $iocRow['SecEnetSwitch'], $iocRow['SecEnetBlade'],
		$iocRow['SecEnetPort'], $iocRow['SecEnetMedConvCh'], $iocRow['SecMedConvPort'], $iocRow['TermServRackNo'],
		$iocRow['TermServName'], $iocRow['TermServPort'], $iocRow['TermServFiberConvCh'], $iocRow['TermServFiberConvPort']);
        $this->iocEntities[$idx] = $iocEntity;
        $idx = $idx +1;
      }
    }
    return true;
  }
}

?>
