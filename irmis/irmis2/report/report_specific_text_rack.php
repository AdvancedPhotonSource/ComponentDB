<?php

 /*
  * Written by Dawn Clemons
  * Specialized step in creating a text report. Specific information
  * that applies only to the IOC PHP viewer are written in the same order appeared
  * in the viewer.
  *
  * Of important note: The text file resulting from this code is best viewed in
  * landscape orientation. It is very possible that the tabbing used to line up
  * columns will need to be altered should any of the data change much. Tabbing
  * needed is dependent on horizontal factors (i.e. the character length of data)
  * so any change in character lengths may very well require a tabbing change.
  */

   //The file pointer $fp was already declared in report_generic_text.php

      //Get the data
   $RackList = $_SESSION['RackList'];
   $rackEntities = $RackList->getArray();

   //Write the column headers
   fwrite($fp, "\r\n\r\n\r\n");
   fwrite($fp, "Enclosure Name"."\t\t"."Enclosure Type"."\t"."Owner"."\t\t\t"."Parent Room"."\t\t"."ID"."\t"."\r\n");
   fwrite($fp, "-------------------------------------------------------------------------------------------------\r\n\r\n");


   foreach($rackEntities as $rackEntity)
   {

      //Write the Enclosure Name-----------------------------------------------------------
      fwrite($fp, $rackEntity->getInstanceName()."\t\t\t");

      //Write the Enclosure Type-----------------------------------------------------------
      fwrite($fp, $rackEntity->getrackType()."\t\t");

      //Write the Enclosure Owner----------------------------------------------------------
	  fwrite($fp, $rackEntity->getGroup()."\t\t");
	  
	  //Write the Enclosure Parent Room----------------------------------------------------
      fwrite($fp, $rackEntity->getParentRoom()."\t");
	  
      //Write the Enclosure ID-------------------------------------------------------------
      fwrite($fp, $rackEntity->getID()."\t\r\n");

   }
?>