<?php

 /*
  * Written by Dawn Clemons
  * Modified by Scott Benes
  * Specialized step in creating a CSV report. Information that applies only
  * to the Component Types PHP viewer is written in the same
  * order as it appeared in the viewer.
  */

   //The file pointer $fp was already declared in report_generic_csv.php

   $delim = ",";

   //Get the data
   $RackContentsList = $_SESSION['rackContentsList'];
   $rackContentsEntities = $RackContentsList->getArray();

   fwrite($fp, "\r\n");
   if ($RackContentsList->length() > 1) {
       fwrite($fp, $RackContentsList->length()." AC Distribution Components found in: ".$_SESSION['rackType']." ".$_SESSION['rack']."  In: ".$_SESSION['roomConstraint']."\r\n");
   } else {
	   fwrite($fp, $RackContentsList->length()." AC Distribution Component found in: ".$_SESSION['rackType']." ".$_SESSION['rack']." ".$_SESSION['roomConstraint']."\r\n");
   }
   
   //Column headers
   fwrite($fp, "Enclosure Name".$delim."Name / Location".$delim."Enclosure Power / Manufacturer".$delim."AC Power Path From Rack To Switch Gear\r\n");
   
   //blank row
   fwrite($fp, ",,,,\r\n");

   //Too many results, so report will not be finished
   if($RackContentsList == null){
      fwrite($fp, "Could not create report! \r\n");
      fwrite($fp, "Search produced too many results to display. \r\n");
      fwrite($fp, "Limit is 5000. Try using the Additional. \r\n");
      fwrite($fp, "Search Terms to constrain your search. \r\n");
   }
   //No results, so report will not be finished
   //elseif($RackContentsList->length() == 0){
   //   fwrite($fp, "Could not create report! \r\n");
   //   fwrite($fp, "No enclosures found. \r\n");
   //   fwrite($fp, "Please try another search. \r\n");
   //}
   //Report will be finished, so write data
   //else{
   
	  $RackContentsList = $_SESSION['rackContentsList'];
      
	  foreach($rackContentsEntities as $rackContentsEntity)
      {
		//Enclosure Name  
         fwrite($fp, "\"".$_SESSION['rack']."\"".$delim);
		 
		 //Name / Location
		 	if ($rackContentsEntity->getLogicalDesc()&&$rackContentsEntity->getComponentInstanceName()) {
				fwrite($fp, $rackContentsEntity->getLogicalDesc()." ".$rackContentsEntity->getComponentInstanceName()."\"".$delim);
		      } elseif ($rackContentsEntity->getLogicalDesc()&&!$rackContentsEntity->getComponentInstanceName()) {
				fwrite($fp, "- / ".$rackContentsEntity->getLogicalDesc().$delim);
			  } elseif ($rackContentsEntity->getComponentInstanceName()&&!$rackContentsEntity->getLogicalDesc()) {
				fwrite($fp, $rackContentsEntity->getComponentInstanceName()." / -".$delim);
			  } else {
				fwrite($fp, "".$delim);
			}

		 
		 //Enclosure Power / Manufacturer
		 if ($rackContentsEntity->getManufacturer() != 'None') {
			  fwrite($fp, "\"".$rackContentsEntity->getcomponentTypeName()." ".$rackContentsEntity->getManufacturer()."\"".$delim);
			} else {
				fwrite($fp, "\"".$rackContentsEntity->getcomponentTypeName()."\"".$delim);
			  }
		 
		 //AC Power Path From Rack To Switch Gear
		    if ($rackContentsEntity->getAC24()) {
					if (strstr($rackContentsEntity->getAC23(), ",")) {
					    $putz = str_replace(",", " ", $rackContentsEntity->getAC23());
				        fwrite($fp, $rackContentsEntity->getAC24()." ".$putz." <-- ");
					} else {
						fwrite($fp, $rackContentsEntity->getAC24()." ".$rackContentsEntity->getAC23()." <-- ");
					}
				} else {
					fwrite($fp, "");
				}
				//-----
				if ($rackContentsEntity->getAC22()) {
					if (strstr($rackContentsEntity->getAC21(), ",")) {
					    $putz = str_replace(",", " ", $rackContentsEntity->getAC21());
				        fwrite($fp, $rackContentsEntity->getAC22()." ".$putz." <-- ");
					} else {
						fwrite($fp, $rackContentsEntity->getAC22()." ".$rackContentsEntity->getAC21()." <-- ");
					}
				} else {
					fwrite($fp, "");
				}
				//-----
				if ($rackContentsEntity->getAC20()) {
					if (strstr($rackContentsEntity->getAC19(), ",")) {
					    $putz = str_replace(",", " ", $rackContentsEntity->getAC19());
				        fwrite($fp, $rackContentsEntity->getAC20()." ".$putz." <-- ");
					} else {
						fwrite($fp, $rackContentsEntity->getAC20()." ".$rackContentsEntity->getAC19()." <-- ");
					}
				} else {
					fwrite($fp, "");
				}
				//-----
				if ($rackContentsEntity->getAC18()) {
					if (strstr($rackContentsEntity->getAC17(), ",")) {
					    $putz = str_replace(",", " ", $rackContentsEntity->getAC17());
				        fwrite($fp, $rackContentsEntity->getAC18()." ".$putz." <-- ");
					} else {
						fwrite($fp, $rackContentsEntity->getAC18()." ".$rackContentsEntity->getAC17()." <-- ");
					}
				} else {
					fwrite($fp, "");
				}
				//-----
				if ($rackContentsEntity->getAC16()) {
					if (strstr($rackContentsEntity->getAC15(), ",")) {
					    $putz = str_replace(",", " ", $rackContentsEntity->getAC15());
				        fwrite($fp, $rackContentsEntity->getAC16()." ".$putz." <-- ");
					} else {
						fwrite($fp, $rackContentsEntity->getAC16()." ".$rackContentsEntity->getAC15()." <-- ");
					}
				} else {
					fwrite($fp, "");
				}
				//-----
				if ($rackContentsEntity->getAC14()) {
					if (strstr($rackContentsEntity->getAC13(), ",")) {
					    $putz = str_replace(",", " ", $rackContentsEntity->getAC13());
				        fwrite($fp, $rackContentsEntity->getAC14()." ".$putz." <-- ");
					} else {
						fwrite($fp, $rackContentsEntity->getAC14()." ".$rackContentsEntity->getAC13()." <-- ");
					}
				} else {
					fwrite($fp, "");
				}
				//-----
				if ($rackContentsEntity->getAC12()) {
					if (strstr($rackContentsEntity->getAC11(), ",")) {
					    $putz = str_replace(",", " ", $rackContentsEntity->getAC11());
				        fwrite($fp, $rackContentsEntity->getAC12()." ".$putz." <-- ");
					} else {
						fwrite($fp, $rackContentsEntity->getAC12()." ".$rackContentsEntity->getAC11()." <-- ");
					}
				} else {
					fwrite($fp, "");
				}
				//-----
				if ($rackContentsEntity->getAC10()) {
					if (strstr($rackContentsEntity->getAC9(), ",")) {
					    $putz = str_replace(",", " ", $rackContentsEntity->getAC9());
				        fwrite($fp, $rackContentsEntity->getAC10()." ".$putz." <-- ");
					} else {
						fwrite($fp, $rackContentsEntity->getAC10()." ".$rackContentsEntity->getAC9()." <-- ");
					}
				} else {
					fwrite($fp, "");
				}
				//-----
				if ($rackContentsEntity->getAC8()) {
					if (strstr($rackContentsEntity->getAC7(), ",")) {
					    $putz = str_replace(",", " ", $rackContentsEntity->getAC7());
				        fwrite($fp, $rackContentsEntity->getAC8()." ".$putz." <-- ");
					} else {
						fwrite($fp, $rackContentsEntity->getAC8()." ".$rackContentsEntity->getAC7()." <-- ");
					}
				} else {
					fwrite($fp, "");
				}
				//-----
				if ($rackContentsEntity->getAC6()) {
					if (strstr($rackContentsEntity->getAC5(), ",")) {
					    $putz = str_replace(",", " ", $rackContentsEntity->getAC5());
				        fwrite($fp, $rackContentsEntity->getAC6()." ".$putz." <-- ");
					} else {
						fwrite($fp, $rackContentsEntity->getAC6()." ".$rackContentsEntity->getAC5()." <-- ");
					}
				} else {
					fwrite($fp, "");
				}
				//-----
				if ($rackContentsEntity->getAC4()) {
					if (strstr($rackContentsEntity->getAC3(), ",")) {
					    $putz = str_replace(",", " ", $rackContentsEntity->getAC3());
				        fwrite($fp, $rackContentsEntity->getAC4()." ".$putz." <-- ");
					} else {
						fwrite($fp, $rackContentsEntity->getAC4()." ".$rackContentsEntity->getAC3()." <-- ");
					}
				} else {
					fwrite($fp, "");
				}
				//-----
				if ($rackContentsEntity->getAC2()) {
					if (strstr($rackContentsEntity->getAC1(), ",")) {
					    $putz = str_replace(",", " ", $rackContentsEntity->getAC1());
				        fwrite($fp, $rackContentsEntity->getAC2()." ".$putz." <-- ");
					} else {
						fwrite($fp, $rackContentsEntity->getAC2()." ".$rackContentsEntity->getAC1().$delim);
					}
				} else {
					fwrite($fp, "");
				}

		 fwrite($fp, "\r\n");
      }
   

?>