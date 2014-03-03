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
   $RackContentsList = $_SESSION['rackContentsList'];
   $rackContentsEntities = $RackContentsList->getArray();

   //Write the column headers
   fwrite($fp, "\r\n\r\n\r\n");
   fwrite($fp, "Enclosure Name"."\t"."Component Type / Manufacturer"."\t"."Name / Location"."\t"."Power Strip"."\t"."AC Circuit"."\t"."AC Panel"."\t"."\r\n");
   fwrite($fp, "-----------------------------------------------------------------------------------------------------------\r\n\r\n");


   foreach($rackContentsEntities as $rackContentsEntity)
   {

      //Write the Enclosure Name-----------------------------------------------------------
      fwrite($fp, $_SESSION['rack']."\t");

      //Write the Enclosure Type-----------------------------------------------------------
      fwrite($fp, $rackContentsEntity->getcomponentTypeName()." / ".$rackContentsEntity->getManufacturer()."\t");


	  //Name / Location
	  if ($rackContentsEntity->getLogicalDesc()&&$rackContentsEntity->getComponentInstanceName()) {
		fwrite($fp, $rackContentsEntity->getLogicalDesc()." / ".$rackContentsEntity->getComponentInstanceName()."\t\t");
		  } elseif ($rackContentsEntity->getLogicalDesc()&&!$rackContentsEntity->getComponentInstanceName()) {
		  fwrite($fp, $rackContentsEntity->getLogicalDesc()."\t\t");
		  } elseif ($rackContentsEntity->getComponentInstanceName()&&!$rackContentsEntity->getLogicalDesc()) {
		  fwrite($fp, $rackContentsEntity->getComponentInstanceName()."\t\t");
		  }
		  
		  
      //Power Strip						
	  if ($rackContentsEntity->getStrip()) {
	      $strip = $rackContentsEntity->getStrip();
		  fwrite($fp, "\"".substr($strip,10,12)."\t\t");			
		  } else {
		  fwrite($fp, "-\t\t");
		  }  
      //AC Circuit
	  if ($rackContentsEntity->getCircuit()) {
	      $circuit = $rackContentsEntity->getCircuit();
		  fwrite($fp, "\"".substr($circuit,10,12)."\t");
		  } else {
		  fwrite($fp, "-\t\t");
		  }
	  //AC Panel
	  if ($rackContentsEntity->getPanel()) {
		  fwrite($fp, $rackContentsEntity->getPanel()."\r\n");
		  } else {
		  fwrite($fp, "-"."\r\n");
		  }
		//}
		  


      //Write the Enclosure Owner----------------------------------------------------------
	 /* fwrite($fp, $rackContentsEntity->getGroup()."\t");

      //Write the Enclosure Description----------------------------------------------------
      fwrite($fp, $rackContentsEntity->getDescription()."\t");
	  
	  //Write the Enclosure Parent Room----------------------------------------------------
      fwrite($fp, $rackContentsEntity->getParentRoom()."\t");
	  
      //Write the Enclosure ID-------------------------------------------------------------
      fwrite($fp, $rackContentsEntity->getID()."\t\r\n");*/

   }
?>