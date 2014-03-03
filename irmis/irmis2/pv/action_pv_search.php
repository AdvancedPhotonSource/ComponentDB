<?php
    /* PV Viewer Search Action handler
     * Conduct specified search, and then render results page.
     */
    include_once('i_common.php');
    
    // This is the maximum number of records that will be displayed
    $MAX_RESULTS = 5000;
    
    // validate post data here
    logEntry('debug',"action_pv_search.php: POST DATA ".print_r($_POST,true));
    
    // save search choices in session
    $searchChoices = new PVSearchChoices($_POST['iocDropDown'],$_POST['dbFileDropDown'],
                                         $_POST['recTypeDropDown'],$_POST['pvname'],$_POST['pvfieldname'],
                                         $_POST['pvfieldvalue']);
    $_SESSION['PVSearchChoices'] = $searchChoices;
    
    // get ioc selections as array of IOCEntity
    $iocChoices = $searchChoices->getIOCChoices();
    $iocBootList = $_SESSION['iocBootList'];
    $iocBootEntities = array();
    $idx = 0;
    $allIocFlag = false;
    foreach ($iocChoices as $choiceIdx) {
        if ($choiceIdx == '-1') {
            $allIocFlag = true;
            $iocBootEntities = $iocBootList->getArray();
            break;
        }
        // lookup actual IOCEntity based on select index
        $iocBootEntity = $iocBootList->getElement($choiceIdx);
        $iocBootEntities[$idx++] = $iocBootEntity;
    }
    
    // get db file selections as array of ioc resource id's
    $dbFileChoices = $searchChoices->getDBFileChoices();
    $dbFileIDs = array();
    $idx = 0;
    foreach ($dbFileChoices as $choiceIdx) {
        if ($choiceIdx == '-1') {
            $dbFileIDs = null;
            break;
        }
        $dbFileIDs[$idx++] = $choiceIdx; // choice is actual db id
    }
    
    // get record type selections as array of strings
    $recordTypeSelections = array();
    $recTypeChoices = $searchChoices->getRecTypeChoices();
    $recTypeEnum = new RecordTypeEnum();
    $idx = 0;
    foreach ($recTypeChoices as $choiceIdx) {
        if ($choiceIdx == '-1') {
            $recordTypeSelections = null;
            break;
        }
        $recordTypeSelections[$idx++] = $recTypeEnum->find($choiceIdx);
    }
    
    // perform record search
    $recList = new RecList();
    logEntry('debug',"Performing record search");
    
    if (!$recList->loadFromDB($conn,$iocBootEntities,$allIocFlag, $dbFileIDs, $recordTypeSelections,
                              $searchChoices->getPVName(),$searchChoices->getPVFieldName(),
                              $searchChoices->getPVFieldValue())) {
        include('pv_error.php');
        exit;    
    }               
    logEntry('debug',"Search gave ".$recList->length()." results.");
         
    // save search result in session (if it's not too big)
    if ($recList->length() <= $MAX_RESULTS)
        $_SESSION['PVSearchResults'] = $recList;
    else
        $_SESSION['PVSearchResults'] = null;
        
    // store num results separately in session (still need num if big)
    $_SESSION['NumPVSearchResults'] = $recList->length();
   
    include_once('pv_search_results.php');
?>
