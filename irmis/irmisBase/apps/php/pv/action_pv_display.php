<?php
    /* PV Viewer Display Action handler
     * Page forward or backwards in pv result set.
     */
    include_once('i_common.php');
       
    // validate post data here
    logEntry('debug',"action_pv_display.php: GET DATA ".print_r($_GET,true));
    
    $prevAction = $_GET['prevDisplayIndices'];
    $nextAction = $_GET['nextDisplayIndices'];
    $sourcePage = $_GET['sourcePage'];
    
    $recList = $_SESSION['PVSearchResults'];
    
    if (!is_null($prevAction))
        $recList->prevDisplayIndices();
        
    if (!is_null($nextAction))
        $recList->nextDisplayIndices();
        
    $_SESSION['PVSearchResults'] = $recList;
   
    include_once($sourcePage.".php");
?>
