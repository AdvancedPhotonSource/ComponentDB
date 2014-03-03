<?php

 /*
  * Written by Dawn Clemons
  * Specialized step in creating a text report. Specific information
  * that applies only to the Component Type PHP viewer are written in the same order appeared
  * in the viewer.
  *
  * There is no tabbing with the goal of lining up columns in this file. A tab delimits each
  * column entry, but no attempt has been made to line up the columns visually. This essentially
  * creates a tab-delimited file.
  */

   //The file pointer $fp was already declared in report_generic_csv.php

   $delim = ",";

   $ctEntity = $_SESSION['selectedComponentType'];
   $ctList = $ctEntity->getComponentList();

   //Write column headers
	fwrite($fp, "\r\n\r\n");
	fwrite($fp, $ctList->length()." Components Installed");
	fwrite($fp, "\r\n");
   fwrite($fp, "Component Type"."\t"."Name"."\t"."ID"."\t"."IOC Parent"."\t"."Housing Parent"."\t"."Room Parent"."\t"."Building Parent"."\r\n\r\n");

   //There were too many results, so report will not be finished
   if ($ctList == null){
      fwrite($fp, "Could not create report!\r\n");
      fwrite($fp, "Search produced too many results to display.\r\n");
      fwrite($fp, "Limit is 5000. Try using the Additional\r\n");
      fwrite($fp, "Search Terms to constrain your search.\r\n");
   }
   //There were no results so report will not be finished
   else if ($ctList->length() == 0){
      fwrite($fp, "Could not create report!\r\n");
      fwrite($fp, "No Components found: please try another search.");
   }
   //Report will be finished, so write data
   else{
      $ctArray = $ctList->getArray();
      foreach ($ctArray as $ctList)
      {
         fwrite($fp, $ctEntity->getctName()."\t");
         fwrite($fp, $ctList->getInstanceName()."\t");
         fwrite($fp, $ctList->getID()."\t");
         fwrite($fp, $ctList->getcomParentIOC()."\t");
         fwrite($fp, $ctList->getcomParentRack()."\t");
         fwrite($fp, $ctList->getcomParentRoom()."\t");
         fwrite($fp, $ctList->getcomParentBldg()."\r\n");
      }
   }


?>