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
   $sgContentsList = $_SESSION['sgContentsList'];
   $sgContentsEntities = $sgContentsList->getArray();

   fwrite($fp, "\r\n");
   fwrite($fp, $sgContentsList->length()." AC Distribution Components Powered by: ".$_SESSION['SG']."  ".$_SESSION['SGType']."  Originating In Switch Gear: ".$_SESSION['switchgearConstraint']."  In: ".$_SESSION['SGRoom']."\r\n");
   
   //Column headers
   fwrite($fp, "This Power Component".$delim."Feeds Power To".$delim."Description".$delim."Manufacturer".$delim."Rack".$delim."Room".$delim."Building\r\n");
   
   //blank row
   fwrite($fp, ",,,,\r\n");

      foreach($sgContentsEntities as $sgContentsEntity)
      {
	//This Power Component
         fwrite($fp, "\"".$_SESSION['SG']."  ".$_SESSION['SGType']."  (".$_SESSION['SGName'].")\"".$delim);
		 
	//Feeds Power To
		 if ($sgContentsEntity->getLogicalDesc()&&$sgContentsEntity->getComponentInstanceName()) {
		     fwrite($fp, "\"".$sgContentsEntity->getComponentTypeName()."  ".$sgContentsEntity->getLogicalDesc()."\"".$delim);
			 
		 } elseif ($sgContentsEntity->getLogicalDesc()&&!$sgContentsEntity->getComponentInstanceName()) {
			 fwrite($fp, "\"".$sgContentsEntity->getComponentTypeName()."  ".$sgContentsEntity->getLogicalDesc()."  (".$sgContentsEntity->getID().")\"".$delim);
			 
		 } elseif ($sgContentsEntity->getComponentInstanceName()&&!$sgContentsEntity->getLogicalDesc()) {
			 fwrite($fp, "\"".$sgContentsEntity->getComponentTypeName().$sgContentsEntity->getComponentInstanceName()."  (".$sgContentsEntity->getID().")\"".$delim);
			 
		 } elseif (!$sgContentsEntity->getComponentInstanceName()&&!$sgContentsEntity->getLogicalDesc()&&$sgContentsEntity->getComponentTypeName()) {
			 fwrite($fp, "\"".$sgContentsEntity->getComponentTypeName()."  ".$sgContentsEntity->getID()."\"".$delim);
		 }
		 
	//Description
		 fwrite($fp, "\"".$sgContentsEntity->getDescription()."\"".$delim);
		 
	//Manufacturer
		 if ($sgContentsEntity->getManufacturer()) {
			fwrite($fp, "\"".$sgContentsEntity->getManufacturer()."\"".$delim);
			} else {
				fwrite($fp, "".$delim);
			}	
		 
	//Rack
		 if ($sgContentsEntity->getParentRack()) {
				fwrite($fp, "\"".$sgContentsEntity->getParentRack()."\"".$delim);
			} else {
			    fwrite($fp, "".$delim);
			}
		 
	//Room
		 if ($sgContentsEntity->getParentRoom()) {
				fwrite($fp, "\"".$sgContentsEntity->getParentRoom()."\"".$delim);
			} else {
			    fwrite($fp, "".$delim);
			}

	//Building
		 if ($sgContentsEntity->getParentBldg()) {
				fwrite($fp, "\"".$sgContentsEntity->getParentBldg()."\"".$delim);
			} else {
			    fwrite($fp, "".$delim);
			}
		 
	  fwrite($fp, "\r\n");
      }
   

?>