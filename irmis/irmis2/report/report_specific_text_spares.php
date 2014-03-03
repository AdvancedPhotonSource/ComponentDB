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
   $SpareList = $_SESSION['SpareList'];

   fwrite($fp, "\r\n");
   fwrite($fp, $SpareList->length()." Components Found\r\n\r\n");
   //Column headers
   fwrite($fp, "Component Type"."\t"."Description"."\t"."Manufacturer"."\r\n\r\n");

   //Too many results, so report will not be finished
   if($SpareList == null){
      fwrite($fp, "Could not create report! \r\n");
      fwrite($fp, "Search produced too many results to display. \r\n");
      fwrite($fp, "Limit is 5000. Try using the Additional. \r\n");
      fwrite($fp, "Search Terms to constrain your search. \r\n");
   }
   //No results, so report will not be finished
   elseif($SpareList->length() == 0){
      fwrite($fp, "Could not create report! \r\n");
      fwrite($fp, "No components found. \r\n");
      fwrite($fp, "Please try another search. \r\n");
   }
   //Report will be finished, so write data
   else{
      $spEntities = $SpareList->getArray();
      foreach($spEntities as $spEntity){
         fwrite($fp, $spEntity->getCtDesc()."\t");
         fwrite($fp, $spEntity->getInsQty()."\t");
		 fwrite($fp, $spEntity->getSpareQty()."\t");
		 fwrite($fp, $spEntity->getSpareLoc()."\t");
		 fwrite($fp, $spEntity->getStockQty()."\t");
         fwrite($fp, "\r\n");
      }
   }

?>