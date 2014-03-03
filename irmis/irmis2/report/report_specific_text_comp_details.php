<?php

 /*
  * Written by Dawn Clemons
  * Specialized step in creating a text report. Specific groupings of information
  * that apply only to the Component Type (Details) PHP viewer are written in the
  * same order as they appeared in the viewer.
  */

   //The file pointer $fp was already declared in report_generic_text.php

   fwrite($fp, "\r\n\r\n");

   $ctEntity = $_SESSION['selectedComponentType'];
   $cList = &$ctEntity->getComponentList();

   //Component Type
	fwrite($fp, "Component Type: ".$ctEntity->getCtName()."\r\n\r\n");

   //Description
	fwrite($fp, "Description: ".$ctEntity->getCtDesc()."\r\n\r\n");

   //Manufacturer
	fwrite($fp, "Manufacturer: ".$ctEntity->getMfgName()."\r\n\r\n");

   //Form Factor
	fwrite($fp, "Form Factor: ".$ctEntity->getffName()."\r\n\r\n");

   //Functions
	$result = $_SESSION['PersonList'];
	$PersonArray = $result->getArray();
	fwrite($fp, "Functions: ");
   $functionList = &$ctEntity->getFunctionList();
	$fArray = &$functionList->getArray();
	$funcs = 0;
   foreach ($fArray as $functionEntity){
		$PersonEntity =$PersonArray[0];
		if ($PersonEntity->getLast()){
			$funcs = 1;
			fwrite($fp, $functionEntity->getFunction() .',');
		}
		if ($funcs != 1){
			fwrite($fp, "Person Not Available");
		}
	}
	fwrite($fp, "\r\n\r\n");

   //Cognizant Person
	fwrite($fp, "Cognizant Person: ");
	$PersonList = &$ctEntity->getPersonList();
	$pArray = &$PersonList->getArray();
	$peeps = 0;
   foreach ($pArray as $PersonEntity) {
      if ($PersonEntity->getLast()) {
         $peeps = 1;
         fwrite($fp, $PersonEntity->getFirst() ." ");
         fwrite($fp, $PersonEntity->getLast());
      }
   }
   if ($peeps != 1) {
      fwrite($fp, "Person Not Available");
      }
   fwrite($fp, "\r\n\r\n");

   //Engineering Documentation
	fwrite($fp, "Engineering Documentation: ");
	$DocList = &$ctEntity->getDocList();
	$dArray = &$DocList->getArray();
	$engdoc = 0;
	foreach ($dArray as $DocEntity) {
	   if ($DocEntity->getdocType() == 'eng doc') {
         $engdoc = 1;
         fwrite($fp, "Documentation Available");
	   }
	}
	if ($engdoc != 1) {
		fwrite($fp, "Documentation Not Available");
	}
	fwrite($fp, "\r\n\r\n");

   //Component Instances
	fwrite($fp, "Component Instances: ".$cList->length()." Components Installed"."\r\n");

   //Control IFR
	fwrite($fp, "Control IFR: ");
	$IFList = &$ctEntity->getIFList();
	$ifArray = &$IFList->getArray();
	$ifcount = 0;
	foreach ($ifArray as $IFEntity) {
	   if (($IFEntity->getRequired() == 1) && ($IFEntity->getComponentRelTypeID() == 1)) {
         $ifcount = 1;
         fwrite($fp, $IFEntity->getIFType().", ");
	   }
	}
	if ($ifcount != 1) {
		fwrite($fp, "Interface Not Provided");
	}
	fwrite($fp, "\r\n\r\n");

   //Control IFP
	fwrite($fp, "Control IFP: ");
	$IFList = &$ctEntity->getIFList();
	$ifArray = &$IFList->getArray();
	$ifcount = 0;
	foreach ($ifArray as $IFEntity) {
	   if (($IFEntity->getPresented() == 1) && ($IFEntity->getComponentRelTypeID() == 1)) {
         $ifcount = 1;
         fwrite($fp, $IFEntity->getIFType().", ");
	   }
	}
	if ($ifcount != 1) {
		fwrite($fp, "Interface Not Provided");
	}
	fwrite($fp, "\r\n\r\n");

   //Housing IFR
	fwrite($fp, "Housing IFR: ");
	$IFList = &$ctEntity->getIFList();
	$ifArray = &$IFList->getArray();
	$ifcount = 0;
	foreach ($ifArray as $IFEntity) {
	   if (($IFEntity->getRequired() == 1) && ($IFEntity->getComponentRelTypeID() == 2)) {
         $ifcount = 1;
         fwrite($fp, $IFEntity->getIFType().", ");
	   }
	}
	if ($ifcount != 1) {
		fwrite($fp, "Interface Not Provided");
	}
	fwrite($fp, "\r\n\r\n");

   //Housing IFP
	fwrite($fp, "Housing IFP: ");
	$IFList = &$ctEntity->getIFList();
	$ifArray = &$IFList->getArray();
	$ifcount = 0;
	foreach ($ifArray as $IFEntity) {
	   if (($IFEntity->getPresented() == 1) && ($IFEntity->getComponentRelTypeID() == 2)) {
         $ifcount = 1;
         fwrite($fp, $IFEntity->getIFType().", ");
	   }
	}
	if ($ifcount != 1) {
		fwrite($fp, "Interface Not Provided");
	}
	fwrite($fp, "\r\n\r\n");

   //Power IFR
	fwrite($fp, "Power IFR: ");
	$IFList = &$ctEntity->getIFList();
	$ifArray = &$IFList->getArray();
	$ifcount = 0;
	foreach ($ifArray as $IFEntity) {
	   if (($IFEntity->getRequired() == 1) && ($IFEntity->getComponentRelTypeID() == 3)) {
         $ifcount = 1;
         fwrite($fp, $IFEntity->getIFType().", ");
	   }
	}
   if ($ifcount != 1) {
      fwrite($fp, "Interface Not Provided");
   }
	fwrite($fp, "\r\n\r\n");

   //Power IFP
	fwrite($fp, "Power IFP: ");
	$IFList = &$ctEntity->getIFList();
	$ifArray = &$IFList->getArray();
	$ifcount = 0;
	foreach ($ifArray as $IFEntity) {
	   if (($IFEntity->getPresented() == 1) && ($IFEntity->getComponentRelTypeID() == 3)) {
         $ifcount = 1;
         fwrite($fp, $IFEntity->getIFType().", ");
	   }
	}
	if ($ifcount != 1) {
		fwrite($fp, "Interface Not Provided");
	}
	fwrite($fp, "\r\n\r\n");
?>