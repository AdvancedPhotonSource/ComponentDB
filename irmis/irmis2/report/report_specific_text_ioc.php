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
   $iocList = $_SESSION['iocList'];
   $iocEntities = $iocList->getArray();

   //Write the column headers
   fwrite($fp, "\r\n\r\n\r\n");
   fwrite($fp, "IOC Name"."\t"."Status"."\t"."System"."\t"."Location"."\t\t\t"."Cognizant Developer"."\t"."Cognizant Technician"."\r\n");
   fwrite($fp, "--------------------------------------------------------------------------------------------------------------\r\n\r\n");


   foreach($iocEntities as $iocEntity)
   {

      //Write the IOC Name------------------------------------------------------------------
      fwrite($fp, $iocEntity->getIocName()."\t");

	  //Write the System--------------------------------------------------------------------
	  if ($iocEntity->getStatus() == 1) {
	    $iocStatus="Production";
	  } elseif ($iocEntity->getStatus() == 2) {
	    $iocStatus="Inactive";
	  } elseif ($iocEntity->getStatus() == 3) {
	    $iocStatus="Ancillary";
	  } elseif ($iocEntity->getStatus() == 4) {
	    $iocStatus="Development";
	  }       
	  fwrite($fp, $iocStatus."\t");      
	  
	  //Write the System--------------------------------------------------------------------
      fwrite($fp, $iocEntity->getSystem()."\t");

      //Write the Location----------------------------------------------------------------
      fwrite($fp, $iocEntity->getLocation()."\t");

      //Write the Cognizant Developer-------------------------------------------------------
      fwrite($fp, $iocEntity->getCogDeveloper().', '.$iocEntity->getCogDeveloper_first()."\t");

      //Write the Cognizant Technician------------------------------------------------------
      fwrite($fp, $iocEntity->getCogtech()."\r\n");
   }
?>