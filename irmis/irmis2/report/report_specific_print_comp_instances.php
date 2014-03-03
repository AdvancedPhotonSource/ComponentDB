<?php

/*
 * Written by Dawn Clemons
 * Specialized step in creating a printable report. Specific information
 * that applies only to the Component Types (Instances) PHP viewer is displayed in a look
 * and feel similar to the viewer's. This output is buffered.
 */

   echo '<div class="sectionTable">
  <table width="100%"  border="1" cellspacing="0" cellpadding="2">';

   // null list in session implies that result size was exceeded


   $ctEntity = $_SESSION['selectedComponentType'];
	$ctList = $ctEntity->getComponentList();

   //Display column headers
    echo '<tr>';
	echo '<td colspan="8"class=value>'.$ctList->length().' Component\'s Installed</td></tr>';
    echo '<tr>';
    echo '<th>Component Type</th>';
	echo '<th>Name</th>';
	echo '<th>Owner</th>';
	echo '<th>IOC Parent</th>';
	echo '<th>Housing Parent Path</th>';
    echo '</tr>';

   //If there were too many results, report will not be finished
   if ($ctList == null) {
      echo '<tr><td class="warning bold" colspan=8>Search produced too many results to display.<br>';
      echo 'Limit is 5000. Try using the Additional<br>';
      echo 'Search Terms to constrain your search.</td></tr>';
   }
   //If there aren't any results, report will not be finished
   else if ($ctList->length() == 0) {
      echo '<tr><td class="warning bold" colspan=8>No Component\'s found: please try another search.</td></tr>';
   }
   //The report will be finished, so display data
   else {
      $ctArray = $ctList->getArray();
      foreach ($ctArray as $ctList) {
		    echo '<tr><td class=primary>';
		    echo $ctEntity->getctName().'&nbsp;('.$ctList->getID().')';
			echo '</td><td class=results>';
		if (!$ctList->getInstanceName()) {
			echo '&nbsp;';
		} else {
			echo $ctList->getInstanceName();
		}
		//GROUP
		 $group = $ctList->getGroup();
		 if ($group == "Unknown") {
			 echo '<td class=dim>';
		     echo $group;
		 } else if ($group == "No Group"){
		     echo '<td class="dim">';
		     echo $group;
		 } else {
		     echo '<td class=results>';
		     echo $group;
		 }
			echo '</td><td class="results">';
			echo $ctList->getcomParentIOC();
			echo '</td><td class="results">';
			$locationPath=array_values($ctList->getcomLocationPath());
			if ($locationPath[0]) {
			  echo $locationPath[0];
			if ($locationPath[1]) {
			  echo "<span class='divider'> | </span>".$locationPath[1];
		    if ($locationPath[2]) {
			  echo "<span class='divider'> | </span>".$locationPath[2];
			if ($locationPath[3]) {
			  echo "<span class='divider'> | </span>".$locationPath[3];
			if ($locationPath[4]) {
			  echo "<span class='divider'> | </span>".$locationPath[4];
			if ($locationPath[5]) {
			  echo "<span class='divider'> | </span>".$locationPath[5];
			if ($locationPath[6]) {
			  echo "<span class='divider'> | </span>".$locationPath[6];
			if ($locationPath[7]) {
			  echo "<span class='divider'> | </span>".$locationPath[7];
			if ($locationPath[8]) {
			  echo "<span class='divider'> | </span>".$locationPath[8];
			if ($locationPath[9]) {
			  echo "<span class='divider'> | </span>".$locationPath[9];
			  }}}}}}}}}}

			echo '</td></tr>';
			
	  }
	}
echo '</table></div>';
echo '<br>';
?>