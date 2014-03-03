<?php
    /* AOI Edit Refresh Action handler
     * Reload AOI List object and render results page.
     */
	// include_once('i_common.php');

	echo "<br>made it to file action_aoi_edit_refresh</br>";

   	$aoiList = new AOIList();

   	$aoi_name_constraint = $_SESSION['aoiNameConstraint'];
/*
   	if (!$aoiList->loadFromDB($conn, $aoi_name_constraint)) {
    	    
		echo "<br>Could not load aoiList from database.</br>";
    	    	// exit();
   	}

   	$searchresultslength = & $aoiList->length();

   	// stuff the loaded aoiList in the session, replacing the one
   	//  that was there before
   	$_SESSION['aoiList'] = $aoiList;

   	// store num results separately in session
   	$_SESSION['aoiListSize'] = $aoiList->length();

   	echo "about to include aoi_edit_search_results.php";
*/
   	//include_once('aoi_edit_search_results.php');
?>
