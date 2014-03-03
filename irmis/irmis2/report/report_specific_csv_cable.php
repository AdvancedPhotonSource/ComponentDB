<?php

/*
 * Written by Dawn Clemons
 * Specialized step in creating a csv report. Specific information
 * that applies only to the Component Types (Details) PHP viewer is displayed in a look
 * and feel similar to the viewer's. This output is buffered.
 */
 
        $CableList = $_SESSION['CableList'];
        $cableEntities = $CableList->getArray();
		
		fwrite($fp, "Cable Details"."\r\n\r\n\r\n");
		
		$delim = ",";
		fwrite($fp, "Port Name".$delim."Port Type".$delim."Pins".$delim."Label".$delim."Color".$delim."Comments".$delim."Location".$delim."Port Name".$delim."Component".$delim."\r\n");
		
        foreach ($cableEntities as $cableEntity) {
			
			$cableInfo=array_values($cableEntity->getcableInfo());
			$oppositePort=array_values($cableEntity->getOppPort());

                //Port Name
                fwrite ($fp, $cableEntity->getComponentPortName());
				
				//Port Type
				fwrite ($fp, $delim.$cableEntity->getComponentPortType());
				
				//Pins
				fwrite ($fp, $delim.$cableEntity->getComponentPortPinCount());
				
				//Label
				if ($cableInfo[2] && $cableInfo[0]) {
				fwrite ($fp, $delim.$cableInfo[0]);
				} elseif  ($cableInfo[2] && !$cableInfo[0]) {
					fwrite ($fp, $delim."-");
				} else {
					fwrite ($fp, $delim."-");
				}
				
				//Color
				if ($cableInfo[2] && $cableInfo[1]) {
				fwrite ($fp, $delim.$cableInfo[1]);
				} elseif ($cableInfo[2] && !$cableInfo[1]) {
					fwrite ($fp, $delim."-");
				} else {
					fwrite ($fp, $delim."-");
				}
				
				//Comments
				if ($oppositePort[2]) {
				fwrite ($fp, $delim.$cableInfo[2]);
				} else {
					fwrite ($fp, $delim."-");
				}
					
                //Location
				if ($oppositePort[2]) { 
				    fwrite ($fp, $delim.$cableEntity->getcomParentRoom()." | ".$cableEntity->getcomParentRack()." | ".$cableEntity->getcomParentIOC());
				} else {
					fwrite ($fp, $delim."-");
				}
				
				//Port Name
				if ($oppositePort[2]) {
				fwrite ($fp, $delim.$oppositePort[3]);
				} else {
					fwrite ($fp, $delim."-");
				}
				
				//Component
				if ($oppositePort[2]) {
					fwrite ($fp, $delim.$oppositePort[0]." (".$oppositePort[2].")");
				} else {
					fwrite ($fp, $delim."-");
				}
					
                fwrite ($fp, "\r\n");

}
?>