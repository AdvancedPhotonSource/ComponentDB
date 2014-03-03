<?php

 /*
  * Written by Dawn Clemons
  * Modified by Scott Benes
  * Specialized step in creating a CSV report. Information that applies only
  * to the Component Types PHP viewer is written in the same
  * order as it appeared in the viewer.
  */

   //The file pointer $fp was already declared in report_generic_csv.php

   $delim = ",";

   //Get the data
   $SGList = $_SESSION['SGList'];
   $SGEntities = $SGList->getArray();

   fwrite($fp, "\r\n");
   fwrite($fp, $SGList->length()." Power Components Found in Switch Gear ".$_SESSION['switchgearConstraint']."\r\n");
   //Column headers
   fwrite($fp, "Switch Gear Component".$delim."Power Component Type".$delim."Description".$delim."Room".$delim."Building\r\n");
   //blank row
   fwrite($fp, ",,,,\r\n");

      foreach($SGEntities as $SGEntity)
      {
	//Switch Gear Component
         fwrite($fp, "\"".$SGEntity->getlogicalDesc()." (".$SGEntity->getID().")\"".$delim);
		 
	//Power Component Type
		 fwrite($fp, "\"".$SGEntity->getSGType()."\"".$delim);
		 
	//Description
		 fwrite($fp, "\"".$SGEntity->getDescription()."\"".$delim);
		 
	//Room
		 if ($SGEntity->getParentRoom()) {
			fwrite($fp, "\"".$SGEntity->getParentRoom()."\"".$delim);
			} else {
				fwrite($fp, "".$delim);
			}	
		 
	//Building
		 if ($SGEntity->getParentBldg()) {
				fwrite($fp, "\"".$SGEntity->getParentBLDG()."\"".$delim);
			} else {
			    fwrite($fp, "".$delim);
			}
		 
		 fwrite($fp, "\r\n");
      }
   

?>