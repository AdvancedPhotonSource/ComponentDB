<?php

/*
 * Written by Dawn Clemons
 * Specialized step in creating a printable report. Specific information
 * that applies only to the IOC PHP viewer is displayed in a look and feel
 * similar to the viewer's.
 */

//Get the data

   $iocList = $_SESSION['iocList'];
   $iocEntities = $iocList->getArray();
   

   echo '<div class="sectionTable"><table width="100%" border="1" cellspacing="0" cellpadding="2">';
   echo '<tr><th colspan="11" class="value">"'.$iocList->length().'" IOC\'s Found</th></tr>';
   echo '<tr><th>IOC Name</th><th>Status</th><th>System</th><th>Location</th><th>Cognizant Developer</th><th>Cognizant Technician</th><th>EPICS Release</th><th>SwitchGear</th></tr>';

   
    
   foreach($iocEntities as $iocEntity)
   {
      //Display the data
      echo '<tr>';
      echo '<td class="primary">'.$iocEntity->getIocName().'</td>';
	  if ($iocEntity->getStatus() == 1) {
	    $iocStatus="Production";
	  } elseif ($iocEntity->getStatus() == 2) {
	    $iocStatus="Inactive";
	  } elseif ($iocEntity->getStatus() == 3) {
	    $iocStatus="Ancillary";
	  } elseif ($iocEntity->getStatus() == 4) {
	    $iocStatus="Development";
	  } 
      echo '<td class="results">'.$iocStatus.'</td>';
	  
	  // System
			echo '<td class=center>'.$iocEntity->getSystem().'</td>';
			
	  // Location
			/*$component_id = $iocEntity->getComponentID();
			  if ($component_id) {
			  $room = $iocEntity->getParentRoom($conn, $component_id);
			  $rack = $iocEntity->getParentRack($conn, $component_id);
			  echo '<td class="resulttext">'.$room.' <b>-</b> '.$rack.'</td>';
			} elseif ($component_id == null) {
			  echo '<td class="resulttext"><font color="red">Not in IDT'.$iocEntity->getLocation().'</font></td>';
			} else {
			  echo '<td><i>'.$iocEntity->getLocation().'</i>&nbsp;<b>-</b>&nbsp;<font color="red">From Location Field</font></td>';
			}*/
			
			echo '<td><i>'.$iocEntity->getLocation().'</i>&nbsp;<b>-</b>&nbsp;<font color="red">From ioc table Location Field</font></td>';
			
	  // Cognizant Developer
			if ($iocEntity->getCogDeveloper()) {
			  echo '<td class="resulttext">'.$iocEntity->getCogDeveloper().', '.$iocEntity->getCogDeveloper_first().'</td>';
			} else {
			  echo '<td>&nbsp;</td>';
			}
			
	  // Cognizant Technician
			if ($iocEntity->getCogtech()) {
			  echo '<td class="results">'.$iocEntity->getCogtech().', '.$iocEntity->getCogtech_first().'</td>';
			} else {
			  echo '<td class="results">None Specified</td>';
			}
	  
	  //EPICS Release
		$iocInfoDir = "/usr/local/iocapps/iocinfo/bootparams";
        $bootParamsHandle = opendir($iocInfoDir);
        if(!$bootParamsHandle) {
           print "could not open $iocInfoDir with $bootParamsHandle<br>";
         }
	    while (($iocBootFile = readdir($bootParamsHandle))) {
          if($iocBootFile == $iocEntity->getIocName()) {
            $iocBootFileHandle = fopen($iocInfoDir."/".$iocBootFile,"r");
          if ($iocBootFileHandle) {
		    $contents = fread ($iocBootFileHandle, filesize ($iocInfoDir."/".$iocBootFile));
		}}}
		
		// extract Startup Script s= in bootparams file
		$flash=0;
		$loc=strpos($contents,"s=")+2;
		$loce=strpos($contents," ",$loc);
		if ($loce === false) {
		  $bootpath=substr($contents,$loc);
        }else {
		  $bootpath=substr($contents, $loc, $loce - $loc);
		}
		if (strstr($contents, "/flash/"))
		{
		  $flash=1;
		}
		
	    $iocapps = strstr($bootpath, "R3.");
	    $dirs = split("/", $iocapps);

	    $release_s = $dirs[0];

	    $home_s = substr($bootpath, 0, strpos($bootpath, "/R3."));
	    if ((strcmp($home_s, "/usr/local/iocapps") == 0) ||
	       (strcmp($home_s, "/net/helios/iocapps") == 0) ||
		   (strcmp($home_s, "/private/var/auto.net/helios/iocapps") == 0)) {
		     $home_t = "http://controlsweb.aps.anl.gov/iocapps/";
		     $release_t = $home_t.$release_s."/";
		} elseif (!$release_s) {
		    $release_s = '<font color="red">No Release Specified'; // In case it's booting from someones working dir, and the version was not specified in the normal path 
	    } else {
		    $workdirectory = $bootpath;
	  }
	  echo '<td class="results">'.$release_s.'</td>';
	  
	  //SwitchGear
	       // if ($iocEntity->getac1()) {
		if ($iocEntity->getac1() == "Unspecified Power") {
			echo '<td class="center"><acronym title="Used for Rover iocs or non stationary equipment.">'.$iocEntity->getac1().'</acronym></td>';
		} else if ($iocEntity->getac1() != "Unspecified Power") {
			echo '<td class="center">'.$iocEntity->getac1().'</td>';
		} else {
			echo '<td class="center">&nbsp;</td>';
			}
	  
	  
      echo '</tr>';
   }
   echo '</table></div><br>';
?>