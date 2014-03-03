<?php
    /* PV Viewer PV Details Action handler
     * Get details (ie. field info) on a given PV
     */
    include_once('i_common.php');
    
    // validate get data here
    logEntry('debug',"action_pv_details.php: GET DATA ".print_r($_GET,true));
    $pvName = urldecode($_GET['pvName']);
    
    // Decide whether we need to re-query for pv info, or whether
    // data is already in prior search results.
    if ($_GET['singlePV'] == "true") {
        $recList = new RecList();
        if (!$recList->loadFromDB($conn,$_SESSION['iocBootList']->getArray(),true,null,null,$pvName,null,null)) {
            include('pv_error.php');
            exit;
        }
        $recEntity = $recList->getElement(0); // only one pv in result set
        // now explicitly lookup related IocEntity
        
    } else {
        // find related RecEntity in prior pv search results
        $recList = $_SESSION['PVSearchResults'];
        $recEntity = $recList->findByPVName($pvName);
    }
    // put selected pv (RecEntity) into session
    $_SESSION['PVSelectedResult'] = $recEntity;
    
    // put related ioc (IocBootEntity) into session
    $iocBootID = $recEntity->getIocBootID();
    $iocBootList = $_SESSION['iocBootList'];
    $iocBootEntity = $iocBootList->getElementForId($iocBootID);

    $_SESSION['PVRelatedIOC'] = $iocBootEntity;
        
    // perform field search for given record (RecEntity)
    $fldList = new FldList();
    logEntry('debug',"Performing field search");
    if (!is_null($recEntity)) {
        if (!$fldList->loadFromDB($conn,$recEntity)) {
            include('pv_error.php');
            exit;
        }
    }
    
    // perform search for all links that use given record
    
    $linkList = new LinkList();
    logEntry('debug',"Performing associated pv link search");
    if (!is_null($recEntity)) {
        if (!$linkList->loadFromDB($conn,$recEntity)) {
            include('pv_error.php');
            exit;    
        }
    }
    
    // save search result in session
    $_SESSION['FieldSearchResults'] = $fldList;
    $_SESSION['LinkSearchResults'] = $linkList;
                         
    include_once('pv_search_results_with_details.php');
?>