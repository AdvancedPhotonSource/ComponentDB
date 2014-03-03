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
   $ServerList = $_SESSION['serverList'];

   fwrite($fp, "\r\n");
   fwrite($fp, $ServerList->length()." Servers Found\r\n");
   //Column headers
   fwrite($fp, "Server Name".$delim."Description".$delim."Operating System".$delim."Cognizant"."\r\n");
   //blank row
   fwrite($fp, ",,,,\r\n");

   //Too many results, so report will not be finished
   if($ServerList == null){
      fwrite($fp, "Could not create report! \r\n");
      fwrite($fp, "Search produced too many results to display. \r\n");
      fwrite($fp, "Limit is 5000. Try using the Additional. \r\n");
      fwrite($fp, "Search Terms to constrain your search. \r\n");
   }
   //No results, so report will not be finished
   elseif($ServerList->length() == 0){
      fwrite($fp, "Could not create report! \r\n");
      fwrite($fp, "No components found. \r\n");
      fwrite($fp, "Please try another search. \r\n");
   }
   //Report will be finished, so write data
   else{
      $serverEntities = $ServerList->getArray();
      foreach($serverEntities as $serverEntity)
      {
      	 $cognizant = $serverEntity->getCognizantFirstName()." ".$serverEntity->getCognizantLastName();

         fwrite($fp, "\"".$serverEntity->getServerName()."\"".$delim."\"".$serverEntity->getServerDescription()."\"".$delim."\"".$serverEntity->getOperatingSystem()."\"".$delim."\"".$cognizant."\""."\r\n");
      }
   }

?>