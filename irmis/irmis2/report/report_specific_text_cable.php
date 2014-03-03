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
        $CableList = $_SESSION['CableList'];
        $cableEntities = $CableList->getArray();

   //Write the column headers
   fwrite($fp, "\r\n\r\n\r\n");
   fwrite($fp, "Port Name"."\t"."Port Type"."\t"."Pins"."\t"."Label"."\t"."Color"."\t"."Comments"."\t"."Location"."\t"."Port Name"."\t"."Component"."\t"."\r\n");
   fwrite($fp, "-----------------------------------------------------------------------------------------------------------\r\n\r\n");


   foreach ($cableEntities as $cableEntity)
   
   {
   $cableInfo=array_values($cableEntity->getcableInfo());
   $oppositePort=array_values($cableEntity->getOppPort());
      //Write the Port Name
      fwrite ($fp, $cableEntity->getComponentPortName()."\t");

      //Write the Port Type
      fwrite ($fp, $cableEntity->getComponentPortType()."\t");
	  
	  //Write the Number of Pins
      fwrite ($fp, $cableEntity->getComponentPortPinCount()."\t");
	  
	  //Write the Cable Label
	  if ($cableInfo[2] && $cableInfo[0]) { 
      fwrite ($fp, $cableInfo[0]."\t");
	  } elseif  ($cableInfo[2] && !$cableInfo[0]) {
		  fwrite ($fp, "-\t");
	  } else {
		  fwrite ($fp, "-\t");
	  }
	  
	  //Write the Cable Color
	  if ($cableInfo[2] && $cableInfo[1]) { 
      fwrite ($fp, $cableInfo[1]."\t");
	  } elseif ($cableInfo[2] && !$cableInfo[1]) {
		  fwrite ($fp, "-\t");
	  } else {
		  fwrite ($fp, "-\t");
	  }
	  
	  //Write the Cable Comments
	  if ($oppositePort[2]) { 
      fwrite ($fp, $cableInfo[2]."\t");
	  } else {
		  fwrite ($fp, "-\t");
	  }
	  
	  //Write the Location
	  if ($oppositePort[2]) { 
      fwrite ($fp, $cableEntity->getcomParentRoom()." | ".$cableEntity->getcomParentRack()." | ".$cableEntity->getcomParentIOC()."\t");
	  } else {
		  fwrite ($fp, "-\t");
	  }
	  
	  //Write the Port Name
	  if ($oppositePort[2]) { 
      fwrite ($fp, $oppositePort[3]."\t");
	  } else {
		  fwrite ($fp, "-\t");
	  }
	  
	  //Write the Component
	  if ($oppositePort[2]) { 
      fwrite ($fp, $oppositePort[0]." (".$oppositePort[2].")"."\t"."\r\n");
	  } else {
		  fwrite ($fp, "-\t\r\n");
	  }

   }
?>