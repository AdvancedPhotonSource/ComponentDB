<div class="searchResults">
<?php
   // null list in session implies that result size was exceeded
   $iocList = $_SESSION['iocList'];
   echo '<a name="top"></a>';

   echo '<table width="100%"  border="1" cellspacing="0" cellpadding="2"><tr>';
   echo '<th colspan="11" class="value">"'.$iocList->length().'" IOC\'s Found';
   if ($iocList->length() != 0)
   {
      echo '<font class="valueright">&nbsp;&nbsp;&nbsp;&nbsp;<a class="hyper" href="#report">Report Tools</a></th>';
   }
   echo '<tr>';
   echo '<th nowrap>IOC Name</th>';
   echo '<th>Status</th>';
   echo '<th>System</th>';
   echo '<th>Location</th>';
   echo '<th>Cognizant<br \>Developer</th>';
   echo '<th>Cognizant<br \>Technician</th>';
   echo '<th>EPICS Release</th>';
   echo '<th>SwitchGear</th>';
   echo '<th>General</th>';
   echo '<th>Network</th>';
   echo '<th>VME/VXI Contents</th>';
   echo '</tr>';
   if ($iocList == null)
   {
      echo '<tr><td class="warning bold" colspan=11>Search produced too many results to display.<br>';
      echo 'Limit is 5000. Try using the Additional<br>';
      echo 'Search Terms to constrain your search.</td></tr>';

   }
   else if ($iocList->length() == 0)
   {
      echo '<tr><td class="warning bold" colspan=11>No IOC\'s found: please try another search.</td></tr>';

   }
   else
   {
      $iocEntities = $iocList->getArray();
      foreach ($iocEntities as $iocEntity)
	  // Status
       {
         echo '<tr>';
         if ($iocEntity->getStatus() == 2)
         {
            echo '<td class="valuedim">'.$iocEntity->getIocName().'&nbsp;(<acronym title="Component ID">'.$iocEntity->getComponentID().'</acronym>)</td>';
			}
			else
         {
            echo '<td class="primary">'.$iocEntity->getIocName().'&nbsp;(<acronym title="Component ID">'.$iocEntity->getComponentID().'</acronym>)</td>';
			}
            
			if ($iocEntity->getStatus() == 1) {
	         $iocStatus="Production";
	         } elseif ($iocEntity->getStatus() == 2) {
	         $iocStatus="Inactive";
	         } elseif ($iocEntity->getStatus() == 3) {
	         $iocStatus="Ancillary";
	         } elseif ($iocEntity->getStatus() == 4) {
	         $iocStatus="Development";
	        } 
	        echo '<td class="center">'.$iocStatus.'</td>';
	  // System
			echo '<td class=center>'.$iocEntity->getSystem().'</td>';
	  // Location
			$component_id = $iocEntity->getComponentID();
			  if ($component_id) {
			  $room = $iocEntity->getParentRoom($conn, $component_id);
			  $rack = $iocEntity->getParentRack($conn, $component_id);
			  echo '<td class="resulttext">'.$room.' <b>-</b> '.$rack.'</td>';
			} elseif ($component_id == null) {
			  echo '<td class="resulttext"><font color="red">Not in IDT'.$iocEntity->getLocation().'</font></td>';
			} else {
			  echo '<td><i>'.$iocEntity->getLocation().'</i>&nbsp;<b>-</b>&nbsp;<font color="red">From Location Field</font></td>';
			}
	  // Cognizant Developer
			if ($iocEntity->getCogDeveloper()) {
			  echo '<td class="resulttext">'.$iocEntity->getCogDeveloper().', '.$iocEntity->getCogDeveloper_first().'</td>';
			} else {
			  echo '<td>&nbsp;</td>';
			}
	  // Cognizant Technician
			if ($iocEntity->getCogtech()) {
			  echo '<td class="resulttext">'.$iocEntity->getCogtech().', '.$iocEntity->getCogtech_first().'</td>';
			} else {
			  echo '<td class="resulttext">None Specified</td>';
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
		if ($iocEntity->getac1() == "Unspecified Power") {
			echo '<td class="center"><acronym title="Used for Rover iocs or non stationary equipment that does not always get power from the same circuit.">'.$iocEntity->getac1().'</acronym></td>';
		} else if ($iocEntity->getac1() != "Unspecified Power" && $iocEntity->getac1() != "No Power") {
			echo '<td class="center"><a class="hyper" href="../power/action_power_search.php?SG='.$iocEntity->getac1().'">'.$iocEntity->getac1().'</a></td>';
		} elseif ($iocEntity->getac1() == "No Power") {
			echo '<td class="center"><font color="red">'.$iocEntity->getac1().'</font></td>';
		} else {
			echo '<td class="center">&nbsp;</td>';
			}
	  
	  //General
            echo '<td class="center"><a class="hyper" href="iocgeneral_search_results.php?iocName='.$iocEntity->getIocName().'">General</td>';
			
	  //Network		
		    echo '<td class="center"><a class="hyper" href="iocnetwork_search_results.php?iocName='.$iocEntity->getIocName().'">Network</td>';
				
	  // Contents
			if ($iocEntity->getStatus() == 2) {
			  echo '<td class="center">&nbsp;</td></tr>';
			
			} elseif (!$iocEntity->getComponentID()) {
			  echo '<td class="center"><font color="red">No Contents</td></tr>';
			  
			} else {
			  $zeke = $iocEntity->getComponentID();
			  echo '<td class="center"><a class="hyper" href="action_ioc_contents_search.php?iocName='.$iocEntity->getIocName().'&componentID='.$zeke.'&room='.$room.'&rack='.$rack.'">Contents</a></td></tr>';
			}
			  
	  }

   echo '</table><table width="100%"  border="1" cellspacing="0" cellpadding="2"><tr>';
   echo '<a name="report"></a>';
   include_once('../report/report_startform.php');
   include_once('../report/report_submit_ioc.php');
   echo '</tr>';
   }
?>

</table></div>