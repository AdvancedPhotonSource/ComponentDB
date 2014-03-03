<?php

 /*
  * Written by Dawn Clemons
  * Specialized step in creating a csv report.
  */

      $iocList = $_SESSION['iocList'];
      $iocName = $_SESSION['iocName'];
      $iocEntity = $iocList->getElementForIocName($iocName);

      $iocInfoDir = "/usr/local/iocapps/iocinfo/bootparams";
      $bootParamsHandle = opendir($iocInfoDir);

      //The file pointer $fp was already declared in report_generic_csv.php

      $delim = ",";

      if(!$bootParamsHandle)
      {
         print "could not open $iocInfoDir with $bootParamsHandle<br>";
      }

      while (($iocBootFile = readdir($bootParamsHandle)))
      {
         if($iocBootFile == $iocEntity->getIocName())
         {
            $iocBootFileHandle = fopen($iocInfoDir."/".$iocBootFile,"r");
            if ($iocBootFileHandle)
            {
               $contents = fread ($iocBootFileHandle, filesize ($iocInfoDir."/".$iocBootFile));
               $loc=strpos($contents,"s=")+2;
               $loce =strpos($contents," ",$loc);
               if ($loce === false)
               {
                  $bootpath = substr($contents,$loc);
               }
               else
               {
                  $bootpath = substr($contents, $loc, $loce-$loc);
               }

		// extract IP#

	            if (strstr($contents, "e="))
	            {
	               $ipnum=strpos($contents,"e=")+2;
	               $ip=substr($contents,$ipnum);
	               $ip_array=explode(":",$ip);
	               $ipnum = $ip_array[0];
	            }
	            else if (strstr($contents,"hn="))
	            {
	               $text = "This Soft IOC is hosted on ";
	               $ipnum = strpos($contents,"hn=")+3;
	               $ip=substr($contents,$ipnum);
	               $ip_array=explode(" ",$ip);
	               $ipnum = $ip_array[0];
	               $ipnumpid = $ip_array[1];
	            }
			}
	   		else
      		{
         		print"could not open $iocBootFile with $iocBootFileHandle<br>";
        	};
	     }
     }

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
		$home_t = "http://phoebus.aps.anl.gov/iocapps/";
	  }


///////////////////////////////////////////////////////////////////////////////////////////////////////


      //IOC NAME
      fwrite($fp, "IOC Name".$delim.$iocEntity->getIocName()."\r\n\r\n");


      //IP NUMBER
      fwrite($fp, "IP Number".$delim.$text.''.$ipnum.$ipnumpid."\r\n\r\n");

      //STATUS
      fwrite($fp, "Status".$delim);
      if ($iocEntity->getActive() == 1){
			fwrite($fp, "Active\r\n\r\n");
		}
		else {
			fwrite($fp, "Not Active\r\n\r\n");
		}

      //IOC BOOT LINE
	  fwrite($fp, "IOC Boot Line".$delim);
	  if (strlen($home_t) == 0)
     {
         fwrite($fp, $bootpath."\r\n\r\n");
	  }
     else
     {
         fwrite($fp, $home_s.'/'.$release_s.'/'.$iocd_s.'/'.$top_s.'/'.$tag_s.'/'.$boot_s.'/'.$ioc_s.'/'.$st_s."\r\n\r\n");
     }

      //SYSTEM
  	  fwrite($fp, "System".$delim.$iocEntity->getSystem()."\r\n\r\n");

      //LOCATION
	  fwrite($fp, "Location".$delim.$iocEntity->getLocation()."\r\n\r\n");

      //COGNIZANT DEVELOPER
	  fwrite($fp, "Cognizant Developer".$delim.$iocEntity->getCogDeveloper()."\r\n\r\n");

      //COGNIZANT TECHNICIAN
	  fwrite($fp, "Cognizant Technician".$delim.$iocEntity->getCogtech()."\r\n\r\n");

      //GENERAL FUNCTIONS
	  fwrite($fp, "General Functions".$delim."\"".$iocEntity->getGeneralFunctions()."\""."\r\n\r\n");

      //PRE-BOOT INSTRUCTIONS
      $preboot_array=explode("|",$iocEntity->getPreBoot());
      $preboot_count=count($preboot_array);
		fwrite($fp, "Pre Boot Instructions".$delim);
      if($preboot_count >1){
         fwrite($fp, $preboot_array[0]."\r\n ");
         fwrite($fp, $preboot_array[1]."\r\n ");
         fwrite($fp, $preboot_array[2]."\r\n");
      }
      else {
         fwrite($fp, $iocEntity->getPreBoot()."\r\n");
      }

      //POST-BOOT INSTRUCTIONS
	  fwrite($fp, "Post Boot Instructions".$delim.$iocEntity->getPostBoot()."\r\n\r\n");

      //POWER CYCLE CAUTION
	  fwrite($fp, "Power Cycle Caution".$delim.$iocEntity->getPowerCycleCaution()."\r\n\r\n");

      //IOC BOOT DATE
	  fwrite($fp, "IOC Boot Date".$delim.$iocEntity->getIocBootDate()."\r\n\r\n");

      //SYSTEM RESET REQUIRED
	  fwrite($fp, "System Reset Required".$delim);
	   if ($iocEntity->getSysResetReqd() == 1){
         fwrite($fp, "Yes\r\n\r\n");
      }
	   else {
         fwrite($fp, "No\r\n\r\n");
      }

      //INHIBIT AUTO REBOOT
      fwrite($fp, "Inhibit Auto Reboot".$delim);
	   if ($iocEntity->getInhibitAutoReboot() == 1){
			fwrite($fp, "Yes\r\n\r\n");
		}
	   else {
			fwrite($fp, "No\r\n\r\n");
      }

      //BOOT LOG
	  fwrite($fp, "Boot Log\r\n");
     fwrite($fp, $contents);



?>