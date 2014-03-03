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
   $SpareList = $_SESSION['SpareList'];

   fwrite($fp, "\r\n");
   fwrite($fp, $SpareList->length()." Components Found\r\n");
   //Column headers
   fwrite($fp, "Component Type".$delim."Description".$delim."Installed Qty".$delim."Spare Qty".$delim."Spare Location".$delim."Stock Qty"."\r\n");
   //blank row
   fwrite($fp, ",,,,\r\n");

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
      foreach($spEntities as $spEntity)
      {
         fwrite($fp, "\"".$spEntity->getCtName()."\"".$delim);
		 fwrite($fp, "\"".$spEntity->getCtDesc()."\"".$delim);
		 fwrite($fp, "\"".$spEntity->getInsQty()."\"".$delim);
		 fwrite($fp, "\"".$spEntity->getSpareQty()."\"".$delim);
		 fwrite($fp, "\"".$spEntity->getSpareLoc()."\"".$delim);
		 fwrite($fp, "\"".$spEntity->getStockQty()."\""."\r\n");
	  }
   }

?>