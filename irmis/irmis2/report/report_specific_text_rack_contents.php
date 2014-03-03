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
   $rackContentsList = $_SESSION['rackContentsList'];
   $rackContentsEntities = $rackContentsList->getArray();

   //Write the column headers
   fwrite($fp, "\r\n\r\n\r\n");
   fwrite($fp, "Enclosure Name"."\t"."Component Type"."\t"."Comp Name"."\t"."Locator"."\t"."Description"."\t"."Manufacturer"."\r\n");
   fwrite($fp, "--------------------------------------------------------------------------------------------------------\r\n\r\n");


   foreach($rackContentsEntities as $rackContentsEntity)
   {

      //Write the Enclosure Name------------------------------------------------------------
	  if ($_SESSION['rack']) {
	  fwrite($fp, $_SESSION['rack']."\t\t");
	  } else {
	  fwrite($fp, $_SESSION['compName']."\t\t");
	  }
	  
      //Write the IOC Name------------------------------------------------------------------
      fwrite($fp, $rackContentsEntity->getcomponentTypeName()."\t\t");

      //Write the System--------------------------------------------------------------------
      fwrite($fp, $rackContentsEntity->getcomponentInstanceName()."\t");
	  
	  //Write the Locator-------------------------------------------------------------------
	  fwrite($fp, $rackContentsEntity->getLogicalDesc()."\t");

      //Write the Description---------------------------------------------------------------
      fwrite($fp, $rackContentsEntity->getDescription()."\t");

      //Write the Cognizant Developer-------------------------------------------------------
      fwrite($fp, $rackContentsEntity->getManufacturer()."\t");
	  

   }
?>