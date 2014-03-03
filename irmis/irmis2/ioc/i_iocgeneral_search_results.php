<div class="searchResults">
  <table width="100%"  border="1" cellspacing="0" cellpadding="2">
<?php
  	  $iocList = $_SESSION['iocList'];
	  $iocName = $_GET['iocName'];
      $_SESSION['iocName'] = $iocName;
	  $iocEntity = $iocList->getElementForIocName($iocName);

	  $iocInfoDir = "/usr/local/iocapps/iocinfo/bootparams";
      $bootParamsHandle = opendir($iocInfoDir);
        if(!$bootParamsHandle) {
         print "could not open $iocInfoDir with $bootParamsHandle<br>";
         }
        while (($iocBootFile = readdir($bootParamsHandle))) {
        //print "found $iocBootFile\n<br>";
        if($iocBootFile == $iocEntity->getIocName())
        {
        $iocBootFileHandle = fopen($iocInfoDir."/".$iocBootFile,"r");
        if ($iocBootFileHandle) {
		$contents = fread ($iocBootFileHandle, filesize ($iocInfoDir."/".$iocBootFile));


		// extract Boot Host Name after the (0,0) statement in the bootparams file
		  if (strstr($contents, ")")) {
		  $bootHost=strpos($contents,")")+1;
		  $bh=substr($contents,$bootHost);
		  $bh_array=explode(":",$bh);
		  $bootHost = $bh_array[0];
		}

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
		
		// extract IP Address e= in bootparams file
		  if (strstr($contents, "e=")) {
		    $ipnum=strpos($contents,"e=")+2;
			$ip=substr($contents,$ipnum);
			$ip_array=explode(" ",$ip);
		    $ipnum = $ip_array[0];
		  }

        //  extract Host Name hn= in bootparams file
		$bhnstart=strpos($contents,"hn=")+3;
		$bhnend=strpos($contents," ",$bhnstart);
		if ($bhnend === false) {
		  $bhname=substr($contents,$bhntart);
        }else {
		  $bhname=substr($contents, $bhnstart, $bhnend - $bhnstart);
		}

		// extract Boot Host IP Address h= in bootparams file
		  if (strstr($contents, "h=")) {
		    $bhipnum=strpos($contents,"h=")+2;
			$bhip=substr($contents,$bhipnum);
			$ip_array=explode(" ",$bhip);
		    $bhipnum = $ip_array[0];
		  }

		// extract Target Architecture ta= in bootparams file
	    $tastart=strpos($contents,"ta=")+3;
		$taend=strpos($contents," ",$tastart);
		if ($taend === false) {
		  $tapath=substr($contents,$tastart);
        }else {
		  $tapath=substr($contents, $tastart, $taend - $tastart);
		}
		
		// extract PID pid= in bootparams file
		  if (strstr($contents, "pid=")) {
		    $pidnum=strpos($contents,"pid=")+4;
			$pid=substr($contents,$pidnum);
			$pid_array=explode(" ",$pid);
		    $pidnum = $pid_array[0];
		  }

		// extract Launch Script ls= in bootparams file
		$lsstart=strpos($contents,"ls=")+3;
		$lsend=strpos($contents," ",$lsstart);
		if ($lsend === false) {
		  $lspath=substr($contents,$lsstart);
        }else {
		  $lspath=substr($contents, $lsstart, $lsend - $lsstart);
		}
		    }
	        else {print"could not open $iocBootFile with $iocBootFileHandle<br>";};
	        }
        }


//----------------------------------------------------------Boot Log section

	    $iocLogDir = "/usr/local/iocapps/iocinfo/bootlog";
        $iocLogHandle = opendir($iocLogDir);
          if(!$iocLogHandle)
           {
           print "could not open $iocLogDir with $iocLogHandle<br>";
           }
          while (($iocLogFile = readdir($iocLogHandle) ) )
           {
           if($iocLogFile == $iocEntity->getIocName())
           {
           $iocBootFileHandle = fopen($iocLogDir."/".$iocLogFile,"r");
           if ($iocBootFileHandle)
           {
           $fs = filesize ($iocLogDir."/".$iocLogFile);
           if ($fs > 0) {
		     $contents = fread ($iocBootFileHandle, $fs);
		   }
		   else
		   $contents = "No boot log available for this IOC.";
           }
         }
       }
//---------------------------------------------------------Startup Script

	  $iocapps = strstr($bootpath, "R3.");
	  $dirs = split("/", $iocapps);

	  $release_s = $dirs[0];
	  $iocd_s = $dirs[1];
	  $top_s = $dirs[2];
	  $tag_s = $dirs[3];
	  $boot_s = $dirs[4];
	  $ioc_s = $dirs[5];
	  $st_s = $dirs[6];

	  $home_s = substr($bootpath, 0, strpos($bootpath, "/R3."));
	  if ((strcmp($home_s, "/usr/local/iocapps") == 0) ||
	      (strcmp($home_s, "/net/helios/iocapps") == 0) ||
		  (strcmp($home_s, "/private/var/auto.net/helios/iocapps") == 0)){
		$home_t = "http://controlsweb.aps.anl.gov/iocapps/";
		$release_t = $home_t.$release_s."/";
		$iocd_t = $release_t.$iocd_s."/";
		$top_t = $iocd_t.$top_s."/";
		$tag_t = $top_t.$tag_s."/";
		$boot_t = $tag_t.$boot_s."/";
		$ioc_t = $boot_t.$ioc_s."/";
		$st_t = $ioc_t.$st_s;
	  } else {
		$workdirectory = $bootpath;
	  }
		
//-------------------------------------------------------------Launch Script
		
	  $lsiocapps = strstr($lspath, "R3.");
	  $dirs = split("/", $lsiocapps);

	  $lsrelease_s = $dirs[0];
	  $lsiocd_s = $dirs[1];
	  $lstop_s = $dirs[2];
	  $lstag_s = $dirs[3];
	  $lsboot_s = $dirs[4];
	  $lsioc_s = $dirs[5];
	  $lsst_s = $dirs[6];

	  $lshome_s = substr($lspath, 0, strpos($lspath, "/R3."));
	  if ((strcmp($lshome_s, "/usr/local/iocapps") == 0) ||
	      (strcmp($lshome_s, "/net/helios/iocapps") == 0) ||
		  (strcmp($lshome_s, "/private/var/auto.net/helios/iocapps") == 0)){
		$lshome_t = "http://controlsweb.aps.anl.gov/iocapps/";
		$lsrelease_t = $lshome_t.$lsrelease_s."/";
		$lsiocd_t = $lsrelease_t.$lsiocd_s."/";
		$lstop_t = $lsiocd_t.$lstop_s."/";
		$lstag_t = $lstop_t.$lstag_s."/";
		$lsboot_t = $lstag_t.$lsboot_s."/";
		$lsioc_t = $lsboot_t.$lsioc_s."/";
		$lsst_t = $lsioc_t.$lsst_s;
		} else {
		  $workdirectorylaunch = $lspath;
		}
		
//-----------------------------------------------------------------------
		

	  echo '<tr>';
	  echo '<th colspan="2" class="center">IOC General Information</th>';
	  echo '</tr>';
	  echo '<tr>';
	  if ($iocEntity->getStatus() == 1) {
	    $iocStatus="Production";
	  } elseif ($iocEntity->getStatus() == 2) {
	    $iocStatus="Inactive";
	  } elseif ($iocEntity->getStatus() == 3) {
	    $iocStatus="Ancillary";
	  } elseif ($iocEntity->getStatus() == 4) {
	    $iocStatus="Development";
	  }
      echo '<th>IOC Name / Status</th><th class="value">'.$iocEntity->getIocName().'&nbsp;&nbsp;&nbsp;('.$iocStatus.')</th>';
	  echo '</tr>';
      // General Functions
	  echo '<tr>';
	  if ($iocEntity->getGeneralFunctions()) {
	    echo '<th>General Functions</th><td class="results">'.$iocEntity->getGeneralFunctions().'</td>';
	  } else {
	    echo '<th>General Functions</th><td class="results">No General Functions Found</td>';
      }
      echo '</tr>';
      // AOI Information
      echo '<tr>';
	  echo '<th>AOI Information</th><td class="results"><a class="hyper" href="../aoi/action_aoi_edit_search.php?iocname='.$iocEntity->getIocName().'">AOI information for '.$iocEntity->getIocName().'</a></td>';
	  echo '</tr>';
      // Network Info
	  echo '<tr>';
	  echo '<th>Network Info</th><td class="results"><a class="hyper" href="iocnetwork_search_results.php?iocName='.$iocEntity->getIocName().'">Network Info</a>
	         - Primary, Secondary, and Terminal Server connection details.</td>';
	  echo '</tr>';
      // Network Status
	  echo '<tr>';
	  echo '<th>Network Status</th><td class="results"><a class="hyper" href="network_status.php?ioc='.$iocName.'&SwP='.$iocEntity->getPrimEnetSwitch().'&BlP='.$iocEntity->getPrimEnetBlade().'&PortP='.$iocEntity->getPrimEnetPort().'&SwS='.$iocEntity->getSecEnetSwitch().'&BlS='.$iocEntity->getSecEnetBlade().'&PortS='.$iocEntity->getSecEnetPort().'" target=_blank" class="bold">Network Switch Status</a> - Primary and Secondary Network Connection Status&nbsp;&nbsp;</td>';
	  echo '</tr>';
	  // IOC Contents
	  $zeke = $iocEntity->getComponentID();
	  echo '<tr>';
	  echo '<th>IOC Contents</th><td class="results"><a class="hyper" href="action_ioc_contents_search.php?iocName='.$iocEntity->getIocName().'&componentID='.$zeke.'">IOC Crate Contents</a>';
      echo '</tr>';
	  
	  
	  if ($bootHost)
	  {
	      echo '<tr>';
		  echo '<th nowrap class="params">Target Architecture</th>';
		  echo '<td class="results"><strong>VxWorks</strong></td>';
		  echo '</tr>';
		  echo '<tr>';
		  echo '<th nowrap class="params">IP Address</th>';
		  echo '<td class="results"><strong>'.$ipnum.'</strong></td>';
		  echo '</tr>';
		  echo '<tr>';
		  echo '<th nowrap class="params">Boot Host</th>';
		  echo '<td class="results">'.$bhipnum.'&nbsp;&nbsp;('.$bootHost.')</td>';
		  echo '</tr>';
		  echo '<tr>';
		  echo '<th nowrap class="params">Startup Script</th>';
		  if (!$workdirectory) {
		  echo '<td class="results"><a class="hyper" href="'.$home_t.'" target="_blank">'.$home_s.'</a>/'.'<a class="hyper" href="'.$release_t.'" target="_blank">'.$release_s.'
	       </a>/'.'<a class="hyper" href="'.$iocd_t.'" target="_blank">'.$iocd_s.'</a>/'.'<a class="hyper" href="'.$top_t.'" target="_blank">'.$top_s.'</a>/'.'<a class="hyper" href="'.$tag_t.'" target="_blank">'.$tag_s.'</a>/'.'<a class="hyper" href="'.$boot_t.'" target="_blank">'.$boot_s.'</a>/'.'<a class="hyper" href="'.$ioc_t.'" target="_blank">'.$ioc_s.'</a>/'.'<a class="hyper" href="'.$st_t.'" target="_blank">'.$st_s.'</td>';
	      } else {
	      echo '<td class="textwarning">'.$workdirectory.'</td>';
	      }
		  echo '</tr>';
	  }
	  elseif (strstr ($tapath, "RTEMS"))
	  {
	      echo '<tr>';
		  echo '<th nowrap class="params">Target Architecture</th>';
		  echo '<td class="results"><strong>'.$tapath.'</strong></td>';
	      echo '</tr>';
		  echo '<tr>';
		  echo '<th nowrap class="params">IP Address</th>';
		  echo '<td class="results"><strong>'.$ipnum.'</strong></td>';
		  echo '</tr>';
		  echo '<tr>';
		  echo '<th nowrap class="params">Boot Host</th>';
		  if ($flash == 1)
		  {
		    echo '<td class="results">Local Flash</td>';
		  } else {
		    echo '<td class="results">Helios (probably)</td>';
		  }
		  echo '</tr>';
		  echo '<tr>';
		  echo '<th nowrap class="params">Startup Script</th>';
		  if (!$workdirectory) {
		  echo '<td class="results"><a class="hyper" href="'.$home_t.'" target="_blank">'.$home_s.'</a>/'.'<a class="hyper" href="'.$release_t.'" target="_blank">'.$release_s.'
	       </a>/'.'<a class="hyper" href="'.$iocd_t.'" target="_blank">'.$iocd_s.'</a>/'.'<a class="hyper" href="'.$top_t.'" target="_blank">'.$top_s.'</a>/'.'<a class="hyper" href="'.$tag_t.'" target="_blank">'.$tag_s.'</a>/'.'<a class="hyper" href="'.$boot_t.'" target="_blank">'.$boot_s.'</a>/'.'<a class="hyper" href="'.$ioc_t.'" target="_blank">'.$ioc_s.'</a>/'.'<a class="hyper" href="'.$st_t.'" target="_blank">'.$st_s.'</td>';
	      } else {
	      echo '<td class="textwarning">'.$workdirectory.'</td>';
		  }
		  echo '</tr>';
	  }
	  elseif (strstr ($tapath, "solaris-sparc"))
	  {
	      echo '<tr>';
		  echo '<th nowrap class="params">Target Architecture</th>';
		  echo '<td class="results"><strong>'.$tapath.'</strong></td>';
	      echo '</tr>';
		  echo '<tr>';
		  echo '<th nowrap class="params">Hosted On</th>';
		  echo '<td class="results"><strong>'.$bhname.'</strong></td>';
		  echo '</tr>';
		  echo '<tr>';
		  echo '<th nowrap class="params">PID</th>';
		  echo '<td class="results"><strong>'.$pidnum.'</strong></td>';
		  echo '</tr>';
		  echo '<tr>';
		  echo '<th nowrap class="params">Startup Script</th>';
		  if (!$workdirectory) {
		  echo '<td class="results"><a class="hyper" href="'.$home_t.'" target="_blank">'.$home_s.'</a>/'.'<a class="hyper" href="'.$release_t.'" target="_blank">'.$release_s.'
	       </a>/'.'<a class="hyper" href="'.$iocd_t.'" target="_blank">'.$iocd_s.'</a>/'.'<a class="hyper" href="'.$top_t.'" target="_blank">'.$top_s.'</a>/'.'<a class="hyper" href="'.$tag_t.'" target="_blank">'.$tag_s.'</a>/'.'<a class="hyper" href="'.$boot_t.'" target="_blank">'.$boot_s.'</a>/'.'<a class="hyper" href="'.$ioc_t.'" target="_blank">'.$ioc_s.'</a>/'.'<a class="hyper" href="'.$st_t.'" target="_blank">'.$st_s.'</td>';
		  } else {
	      echo '<td class="textwarning">'.$workdirectory.'</td>';
		  }
		  echo '</tr>';
		  echo '<tr>';
		  echo '<th nowrap class="params">Launch Script</th>';
		  if (!$workdirectorylaunch) {
		  echo '<td class="results"><a class="hyper" href="'.$lshome_t.'" target="_blank">'.$lshome_s.'</a>/'.'<a class="hyper" href="'.$lsrelease_t.'" target="_blank">'.$lsrelease_s.'
	       </a>/'.'<a class="hyper" href="'.$lsiocd_t.'" target="_blank">'.$lsiocd_s.'</a>/'.'<a class="hyper" href="'.$lstop_t.'" target="_blank">'.$lstop_s.'</a>/'.'<a class="hyper" href="'.$lstag_t.'" target="_blank">'.$lstag_s.'</a>/'.'<a class="hyper" href="'.$lsboot_t.'" target="_blank">'.$lsboot_s.'</a>/'.'<a class="hyper" href="'.$lsioc_t.'" target="_blank">'.$lsioc_s.'</a>/'.'<a class="hyper" href="'.$lsst_t.'" target="_blank">'.$lsst_s.'</td>'; 
	      } else {
	      echo '<td class="textwarning">'.$workdirectorylaunch.'</td>';
		  }
		  echo '</tr>';
	  }
	  elseif (strstr ($tapath, "linux"))
	  {
	      echo '<tr>';
		  echo '<th nowrap class="params">Target Architecture</th>';
		  echo '<td class="results"><strong>'.$tapath.'</strong></td>';
	      echo '</tr>';
		  echo '<tr>';
		  echo '<th nowrap class="params">Hosted On</th>';
		  echo '<td class="results"><strong>'.$bhname.'</strong></td>';
		  echo '</tr>';
		  echo '<tr>';
		  echo '<th nowrap class="params">PID</th>';
		  echo '<td class="results"><strong>'.$pidnum.'</strong></td>';
		  echo '</tr>';
		  echo '<tr>';
		  echo '<th nowrap class="params">Startup Script</th>';
		  if (!$workdirectory) {
		  echo '<td class="results"><a class="hyper" href="'.$home_t.'" target="_blank">'.$home_s.'</a>/'.'<a class="hyper" href="'.$release_t.'" target="_blank">'.$release_s.'
	       </a>/'.'<a class="hyper" href="'.$iocd_t.'" target="_blank">'.$iocd_s.'</a>/'.'<a class="hyper" href="'.$top_t.'" target="_blank">'.$top_s.'</a>/'.'<a class="hyper" href="'.$tag_t.'" target="_blank">'.$tag_s.'</a>/'.'<a class="hyper" href="'.$boot_t.'" target="_blank">'.$boot_s.'</a>/'.'<a class="hyper" href="'.$ioc_t.'" target="_blank">'.$ioc_s.'</a>/'.'<a class="hyper" href="'.$st_t.'" target="_blank">'.$st_s.'</td>';
		  } else {
	      echo '<td class="textwarning">'.$workdirectory.'</td>';
		  }
		  echo '</tr>';
		  echo '<tr>';
		  echo '<th nowrap class="params">Launch Script</th>';
		  if (!$workdirectorylaunch) {
		  echo '<td class="results"><a class="hyper" href="'.$lshome_t.'" target="_blank">'.$lshome_s.'</a>/'.'<a class="hyper" href="'.$lsrelease_t.'" target="_blank">'.$lsrelease_s.'
	       </a>/'.'<a class="hyper" href="'.$lsiocd_t.'" target="_blank">'.$lsiocd_s.'</a>/'.'<a class="hyper" href="'.$lstop_t.'" target="_blank">'.$lstop_s.'</a>/'.'<a class="hyper" href="'.$lstag_t.'" target="_blank">'.$lstag_s.'</a>/'.'<a class="hyper" href="'.$lsboot_t.'" target="_blank">'.$lsboot_s.'</a>/'.'<a class="hyper" href="'.$lsioc_t.'" target="_blank">'.$lsioc_s.'</a>/'.'<a class="hyper" href="'.$lsst_t.'" target="_blank">'.$lsst_s.'</td>'; 
	      } else {
	      echo '<td class="textwarning">'.$workdirectorylaunch.'</td>';
		  }
		  echo '</tr>';
	  }
	  elseif (strstr ($tapath, "darwin"))
	  {
	      echo '<tr>';
		  echo '<th nowrap class="params">Target Architecture</th>';
		  echo '<td class="results"><strong>'.$tapath.'</strong></td>';
	      echo '</tr>';
		  echo '<tr>';
		  echo '<th nowrap class="params">Hosted On</th>';
		  echo '<td class="results"><strong>'.$bhname.'</strong></td>';
		  echo '</tr>';
		  echo '<tr>';
		  echo '<th nowrap class="params">PID</th>';
		  echo '<td class="results"><strong>'.$pidnum.'</strong></td>';
		  echo '</tr>';
		  echo '<tr>';
		  echo '<th nowrap class="params">Startup Script</th>';
		  if (!$workdirectory) {
		  echo '<td class="results"><a class="hyper" href="'.$home_t.'" target="_blank">'.$home_s.'</a>/'.'<a class="hyper" href="'.$release_t.'" target="_blank">'.$release_s.'
	       </a>/'.'<a class="hyper" href="'.$iocd_t.'" target="_blank">'.$iocd_s.'</a>/'.'<a class="hyper" href="'.$top_t.'" target="_blank">'.$top_s.'</a>/'.'<a class="hyper" href="'.$tag_t.'" target="_blank">'.$tag_s.'</a>/'.'<a class="hyper" href="'.$boot_t.'" target="_blank">'.$boot_s.'</a>/'.'<a class="hyper" href="'.$ioc_t.'" target="_blank">'.$ioc_s.'</a>/'.'<a class="hyper" href="'.$st_t.'" target="_blank">'.$st_s.'</td>';
		  } else {
	      echo '<td class="textwarning">'.$workdirectory.'</td>';
		  }
		  echo '</tr>';
		  echo '<tr>';
		  echo '<th nowrap class="params">Launch Script</th>';
		  if (!$workdirectorylaunch) {
		  echo '<td class="results"><a class="hyper" href="'.$lshome_t.'" target="_blank">'.$lshome_s.'</a>/'.'<a class="hyper" href="'.$lsrelease_t.'" target="_blank">'.$lsrelease_s.'
	       </a>/'.'<a class="hyper" href="'.$lsiocd_t.'" target="_blank">'.$lsiocd_s.'</a>/'.'<a class="hyper" href="'.$lstop_t.'" target="_blank">'.$lstop_s.'</a>/'.'<a class="hyper" href="'.$lstag_t.'" target="_blank">'.$lstag_s.'</a>/'.'<a class="hyper" href="'.$lsboot_t.'" target="_blank">'.$lsboot_s.'</a>/'.'<a class="hyper" href="'.$lsioc_t.'" target="_blank">'.$lsioc_s.'</a>/'.'<a class="hyper" href="'.$lsst_t.'" target="_blank">'.$lsst_s.'</td>'; 
	      } else {
	      echo '<td class="textwarning">'.$workdirectorylaunch.'</td>';
		  }
		  echo '</tr>';
	  }
	  else
	  {
	      echo '<tr>';
		  echo '<th nowrap class="params">Target Architecture</th>';
		  echo '<td class="results">Unable to determine Target Architecture</td>';
	      echo '</tr>';
	  }
	  
	  
	  // System
	  echo '<tr>';
	  echo '<th class="primary">System</th><td class="results">'.$iocEntity->getSystem().'</td>';
	  echo '</tr>';
	  // Location
	  $component_id = $iocEntity->getComponentID();
			  if ($component_id) {
			  echo '<th class="primary" nowrap>Location</th><td class="results">'.$iocEntity->getParentRoom($conn, $component_id).' <strong>-</strong> '.$iocEntity->getParentRack($conn, $component_id).'</td>';
			} elseif ($component_id == null) {
			  echo '<th class="primary" nowrap>Location</th><td class="results"><font color="red">Not in IDT'.$iocEntity->getLocation().'</font></td>';
			} else {
			  echo '<td><i>'.$iocEntity->getLocation().'<th class="primary" nowrap>Location</th></i>&nbsp;<strong>-</strong>&nbsp;<font color="red">From Location Field</font></td>';
			}
	  // Cognizant Developer
	  echo '<tr>';
	  if ($iocEntity->getCogDeveloper()) {
	    echo '<th class="primary" nowrap>Cognizant Developer</th><td class="results">'.$iocEntity->getCogDeveloper().', '.$iocEntity->getCogDeveloper_first().'</td>';
	  } else {
	    echo '<th class="primary" nowrap>Cognizant Developer</th><td class="results">No Cognizant Developer Specified</td>';
	  }
	  echo '</tr>';
	  // Cognizant Technician
	  echo '<tr>';
	  if ($iocEntity->getCogtech()) {
	    echo '<th nowrap class="primary">Cognizant Technician</th><td class="results">'.$iocEntity->getCogtech().', '.$iocEntity->getCogtech_first().'</td>';
	  } else {
	    echo '<th nowrap class="primary">Cognizant Technician</th><td class="results">No Cognizant Technician Specified</td>';
	  }
	  echo '</tr>';
      // Preboot Instructions
	  $preboot_array=explode("|",$iocEntity->getPreBoot());
      $preboot_count=count($preboot_array);
	  echo '<tr><th class="primary">Pre Boot Instructions</th>';
	    if($preboot_count >1){
         echo '<td class="results">'.$preboot_array[0].'<br>';
         echo '<a target="_blank" class="hyper" href="'.$preboot_array[1].'">'.$preboot_array[1].'</a>';
         echo $preboot_array[2].'</td>';
         }
        else if ($iocEntity->getPreBoot()){
         echo '<td class="results">'.$iocEntity->getPreBoot().'</td>';
		} else {
		 echo '<td>&nbsp;</td>';
        }
      echo '</tr>';
      // Postboot Instructions
	  echo '<tr>';
	  if ($iocEntity->getPostBoot()) {
	    echo '<th nowrap class="primary">Post Boot Instructions</th><td class="results">'.$iocEntity->getPostBoot().'</td>';
	  } else {
	    echo '<th nowrap class="primary">Post Boot Instructions</th><td>&nbsp;</td>';
	  }
      echo '</tr>';
	  // Power Cycle Caution
	  echo '<tr>';
	  if ($iocEntity->getPowerCycleCaution()) {
	    echo '<th class="primary">Power Cycle Caution</th><td class="results">'.$iocEntity->getPowerCycleCaution().'</td>';
	  } else {
	    echo '<th class="primary">Power Cycle Caution</th><td>&nbsp;</td>';
	  }
	  echo '</tr>';
	  
	  // System Reset Required
	  echo '<tr>';
	  echo '<th nowrap class="primary">System Reset Required</th>';
	      if ($iocEntity->getSysResetReqd() == 1){
			echo '<td class="results">Yes</td>';
			}
	      else {
			echo '<td class="results">No</td>';
			}
	  echo '</tr>';
	  // Inhibit Auto Reboot
	  echo '<tr>';
	  echo '<th nowrap class="primary">Inhibit Auto Reboot</th>';
	    if ($iocEntity->getInhibitAutoReboot() == 1){
			echo '<td class="results">Yes</td>';
			}
	      else {
			echo '<td class="results">No</td>';
			}
	  echo '</tr>';
	  // Boot Log
	  echo '<tr>';
	  echo '<th nowrap class="primary">Boot Log<br>(Read Only)</th>';
	  echo '<td class="two"><textarea NAME='.iocLog.' rows=10 cols=68 READONLY>'.$contents.'</textarea></td>';
      echo '</tr></table>';
      // Reports
      echo ' <table width="100%"  border="1" cellspacing="0" cellpadding="2"> <tr>';
        include('../report/report_startform.php');
        include('../report/report_submit_ioc_general.php');
      echo '</tr>';

?>
</table>
</div>
