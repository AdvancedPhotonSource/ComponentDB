<?php
    /* Demo IOC Search Action handler
     * Conduct specified search, and then render results page.
     */
    include_once('i_common.php');
    
    // log post data here to help debugging (note: we don't really have any for this demo)
    logEntry('debug',"action_ioc_search.php: POST DATA ".print_r($_POST,true));

    // put post data into session
    $_SESSION['iocNameConstraint'] = $_POST['iocNameConstraint'];
    
    // perform ioc search
    $iocList = new IOCList();
    logEntry('debug',"Performing ioc search");
    
    if (!$iocList->loadFromDB($conn, $_SESSION['iocNameConstraint'])) {
        include('demo_error.php');
        exit;    
    }               
    logEntry('debug',"Search gave ".$iocList->length()." results.");
         
    // stuff the loaded iocList in the session, replacing the one
    //  that was there before
    $_SESSION['iocList'] = $iocList;
        
    // store num results separately in session
    $_SESSION['iocListSize'] = $iocList->length();
   
    include_once('demo_search_results.php');
?>
