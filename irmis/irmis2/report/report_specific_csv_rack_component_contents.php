<?php

 /*
  * Written by Dawn Clemons
  * Specialized step in creating a CSV report. Information that applies only
  * to the Component Types PHP viewer is written in the same
  * order as it appeared in the viewer.
  */

   //The file pointer $fp was already declared in report_generic_csv.php

   $delim = ",";

   //Get the data
   $RackCCList = $_SESSION['rackCCList'];

   fwrite($fp, "\r\n");
   fwrite($fp, $RackCCList->length()." Components's Found\r\n");
   //Column headers
   fwrite($fp, "Enclosure Name".$delim."Component Type".$delim."Component Name".$delim."Locator".$delim."Description".$delim."Manufacturer".$delim."\r\n");
   //blank row
   fwrite($fp, ",,,,\r\n");

   //Too many results, so report will not be finished
   if($RackCCList == null){
      fwrite($fp, "Could not create report! \r\n");
      fwrite($fp, "Search produced too many results to display. \r\n");
      fwrite($fp, "Limit is 5000. Try using the Additional. \r\n");
      fwrite($fp, "Search Terms to constrain your search. \r\n");
   }
   //No results, so report will not be finished
   elseif($RackCCList->length() == 0){
      fwrite($fp, "Could not create report! \r\n");
      fwrite($fp, "No enclosures found. \r\n");
      fwrite($fp, "Please try another search. \r\n");
   }
   //Report will be finished, so write data
   else{
      $rackCCEntities = $RackCCList->getArray();
      foreach($rackCCEntities as $rackCCEntity)
      {
         fwrite($fp, "\"".$_SESSION['compName']."\"");
		 fwrite($fp, $delim."\"".$rackCCEntity->getcomponentTypeName()."\"");
		 fwrite($fp, $delim."\"".$rackCCEntity->getcomponentInstanceName()."\"");
		 fwrite($fp, $delim."\"".$rackCCEntity->getLogicalDesc()."\"");
		 fwrite($fp, $delim."\"".$rackCCEntity->getDescription()."\"");
		 fwrite($fp, $delim."\"".$rackCCEntity->getManufacturer()."\"\r\n");
      }
   }

?>