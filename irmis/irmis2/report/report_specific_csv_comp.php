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
   $ComponentTypeList = $_SESSION['ComponentTypeList'];

   fwrite($fp, "\r\n");
   fwrite($fp, $ComponentTypeList->length()." Components Found\r\n");
   //Column headers
   fwrite($fp, "Component Type".$delim."Description".$delim."Manufacturer"."\r\n");
   //blank row
   fwrite($fp, ",,,,\r\n");

   //Too many results, so report will not be finished
   if($ComponentTypeList == null){
      fwrite($fp, "Could not create report! \r\n");
      fwrite($fp, "Search produced too many results to display. \r\n");
      fwrite($fp, "Limit is 5000. Try using the Additional. \r\n");
      fwrite($fp, "Search Terms to constrain your search. \r\n");
   }
   //No results, so report will not be finished
   elseif($ComponentTypeList->length() == 0){
      fwrite($fp, "Could not create report! \r\n");
      fwrite($fp, "No components found. \r\n");
      fwrite($fp, "Please try another search. \r\n");
   }
   //Report will be finished, so write data
   else{
      $ctEntities = $ComponentTypeList->getArray();
      foreach($ctEntities as $ctEntity)
      {
         fwrite($fp, "\"".$ctEntity->getCtName()."\"".$delim."\"".$ctEntity->getCtDesc()."\"".$delim."\"".$ctEntity->getMfgName()."\""."\r\n");
      }
   }

?>