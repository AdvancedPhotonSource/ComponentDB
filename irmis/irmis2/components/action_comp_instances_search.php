<?php
    /* Component Instance Search Action handler
     * Get additional component details from database.
     */
    include_once('i_common.php');
    
    // log get data here to help debugging 
    // logEntry('debug',"action_comp_details_search.php: GET DATA ".print_r($_GET,true));
	
    //***************************************** new
	
    $ID = $_GET['ID'];
	//$ComponentTypeList = $_SESSION['ComponentTypeList'];
	//$ctEntity=$ComponentTypeList->getElementForComponentID($ID);
	$ctEntity = &$_SESSION['selectedComponentType'];
	$cList = &$ctEntity->getComponentList();
	$compArray = &$cList->getArray();
	$idx = 0;
	  foreach ($compArray as $cEntity) {
	    $cEntity->loadDetailsFromDB($conn);
		$cList->setElement($idx, $cEntity);
		$idx++;
	  }
	
    //******************************************
	
    //$ComponentTypeList = $_SESSION['ComponentTypeList'];
	
    // put selected component type into session by itself (easier)
    //$_SESSION['selectedComponentType'] = $compType;
	
    $path = $_GET['path'];
    if ($path == "expanded") {
      include_once('comp_instances.php');
    } elseif ($path == "short") {
      include_once('comp_instances_short.php');
    }
?>
