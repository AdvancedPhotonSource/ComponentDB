<?php

 /*
  * Written by Dawn Clemons
  * Specialized step in creating a text report. Specific information
  * that applies only to the Component Type PHP viewer are written in the same order appeared
  * in the viewer.
  *
  * There is no tabbing with the goal of lining up columns in this file. A tab delimits each
  * column entry, but no attempt has been made to line up the columns visually. (This
  * esesntially creates a tab-delimited file.
  */

   //The file pointer $fp was already declared in report_generic_csv.php

   $delim = ",";

   //Get the data
   $ServerList = $_SESSION['serverList'];

   fwrite($fp, "\r\n");
   fwrite($fp, $ServerList->length()." Servers Found\r\n\r\n");
   //Column headers
   fwrite($fp, "Server Name"."\t"."Description"."\t"."Operating System"."\t"."Cognizant"."\r\n\r\n");

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
      foreach($serverEntities as $serverEntity){

      	 $description = $serverEntity->getServerDescription();

         fwrite($fp, $serverEntity->getServerName()."\t");

         if($description) {
         	fwrite($fp, $description."\t");
         }else {
         	fwrite($fp, "\t\t");
         }

         fwrite($fp, $serverEntity->getOperatingSystem()."\t\t\t");
         fwrite($fp, $serverEntity->getCognizantFirstName()." ".$serverEntity->getCognizantLastName()."\t");
         fwrite($fp, "\r\n");
      }
   }

?>