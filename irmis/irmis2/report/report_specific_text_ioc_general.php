<?php

 /*
  * Written by Dawn Clemons
  * Specialized step in creating a text report.
  */

      $iocList = $_SESSION['iocList'];
      $iocName = $_SESSION['iocName'];
      $iocEntity = $iocList->getElementForIocName($iocName);

      //The file pointer $fp was already declared in report_generic_csv.php

      $iocInfoDir = "/usr/local/iocapps/iocinfo/bootparams";
      $bootParamsHandle = opendir($iocInfoDir);

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

      fwrite($fp, "\r\n\r\n\r\n\r\n");

      fwrite($fp, "==IOC Name==\r\n ".$iocEntity->getIocName()."\r\n\r\n");

      fwrite($fp, "==IP Number==\r\n ".$text.''.$ipnum.$ipnumpid."\r\n\r\n");

      fwrite($fp, "==Status==\r\n ");
      if ($iocEntity->getActive() == 1){
			fwrite($fp, "Active\r\n\r\n");
		}
		else {
			fwrite($fp, "Not Active\r\n\r\n");
		}

	  fwrite($fp, "==IOC Boot Line==\r\n ");
	  if (strlen($home_t) == 0)
     {
         fwrite($fp, $bootpath."\r\n\r\n");
	  }
     else
     {
         fwrite($fp, $home_s.'/'.$release_s.'/'.$iocd_s.'/'.$top_s.'/'.$tag_s.'/'.$boot_s.'/'.$ioc_s.'/'.$st_s."\r\n\r\n");
     }

  	  fwrite($fp, "==System==\r\n ".$iocEntity->getSystem()."\r\n\r\n");

	  fwrite($fp, "==Location==\r\n ".$iocEntity->getLocation()."\r\n\r\n");

	  fwrite($fp, "==Cognizant Developer==\r\n ".$iocEntity->getCogDeveloper()."\r\n\r\n");

	  fwrite($fp, "==Cognizant Technician==\r\n ".$iocEntity->getCogtech()."\r\n\r\n");

	  fwrite($fp, "==General Functions==\r\n ".$iocEntity->getGeneralFunctions()."\r\n\r\n");


      $preboot_array=explode("|",$iocEntity->getPreBoot());
      $preboot_count=count($preboot_array);
		fwrite($fp, "==Pre Boot Instructions==\r\n ");
      if($preboot_count >1){
         fwrite($fp, $preboot_array[0]."\r\n ");
         fwrite($fp, $preboot_array[1]."\r\n ");
         fwrite($fp, $preboot_array[2]."\r\n");
      }
      else {
         fwrite($fp, $iocEntity->getPreBoot()."\r\n");
      }


	  fwrite($fp, "==Post Boot Instructions==\r\n ".$iocEntity->getPostBoot()."\r\n\r\n");

	  fwrite($fp, "==Power Cycle Caution==\r\n ".$iocEntity->getPowerCycleCaution()."\r\n\r\n");

	  fwrite($fp, "==IOC Boot Date==\r\n ".$iocEntity->getIocBootDate()."\r\n\r\n");

	  fwrite($fp, "==System Reset Required==\r\n ");
	   if ($iocEntity->getSysResetReqd() == 1){
         fwrite($fp, "Yes\r\n\r\n");
      }
	   else {
         fwrite($fp, "No\r\n\r\n");
      }

      fwrite($fp, "==Inhibit Auto Reboot==\r\n ");
	   if ($iocEntity->getInhibitAutoReboot() == 1){
			fwrite($fp, "Yes\r\n\r\n");
		}
	   else {
			fwrite($fp, "No\r\n\r\n");
      }


	  fwrite($fp, "==Boot Log==\r\n ");
     $linebreak_contents = str_replace("\n", "\r\n", $contents);
	  fwrite($fp, $linebreak_contents);

?>
