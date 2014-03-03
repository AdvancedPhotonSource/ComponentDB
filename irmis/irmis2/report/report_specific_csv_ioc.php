<?php

 /*
  * Written by Dawn Clemons
  * Specialized step in creating a CSV report. Information that applies only
  * to the IOC PHP viewer is written in the same order as it appeared in the
  * viewer.
  */

   //The file pointer $fp was already declared in report_generic_csv.php

   $delim = ",";

   //Get the data
   $iocList = $_SESSION['iocList'];
   $iocEntities = $iocList->getArray();


   //Write column titles
   fwrite($fp, "\r\n");
   fwrite($fp, "IOC Name".$delim."Status".$delim."System".$delim."Location".$delim."Cognizant Developer".$delim."Cognizant Technician".$delim."EPICS Release"."\r\n");
   fwrite($fp, ",,,,\r\n");

   foreach($iocEntities as $iocEntity)
   {
      //Write the data
      //each piece of data is in quotes in case the data should have a comma in it
      fwrite($fp, "\"".$iocEntity->getIocName()."\"".$delim);
	  if ($iocEntity->getStatus() == 1) {
	    $iocStatus="Production";
	  } elseif ($iocEntity->getStatus() == 2) {
	    $iocStatus="Inactive";
	  } elseif ($iocEntity->getStatus() == 3) {
	    $iocStatus="Ancillary";
	  } elseif ($iocEntity->getStatus() == 4) {
	    $iocStatus="Development";
	  } 
	  fwrite($fp, "\"".$iocStatus."\"".$delim);
      fwrite($fp, "\"".$iocEntity->getSystem()."\"".$delim);
      fwrite($fp, "\"".$iocEntity->getLocation()."\"".$delim);
      fwrite($fp, "\"".$iocEntity->getCogDeveloper().", ".$iocEntity->getCogDeveloper_first()."\"".$delim);
      fwrite($fp, "\"".$iocEntity->getCogtech().", ".$iocEntity->getCogtech_first()."\"".$delim);
	  
	  
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
		    $release_s = 'No Release Specified'; // In case it's booting from someones working dir, and the version was not specified in the normal path 
	    } else {
		    $workdirectory = $bootpath;
	  }
	  fwrite($fp, "\"".$release_s."\"\r\n");
	  
   }
?>
