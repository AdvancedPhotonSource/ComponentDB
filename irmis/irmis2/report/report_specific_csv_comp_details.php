<?php

/*
 * Written by Dawn Clemons
 * Specialized step in creating a csv report. Specific information
 * that applies only to the Component Types (Details) PHP viewer is displayed in a look
 * and feel similar to the viewer's. This output is buffered.
 */

   $ctEntity = $_SESSION['selectedComponentType'];
   $cList = &$ctEntity->getComponentList();

   $delim = ",";

   fwrite($fp, "Component Type Details"."\r\n\r\n\r\n");


   //Component Type
   fwrite($fp, "Component Type".$delim.$ctEntity->getCtName()."\r\n");


   //Description
   fwrite($fp, "Description".$delim.$ctEntity->getCtDesc()."\r\n");


   //Manufacturer
   fwrite($fp, "Manufacturer".$delim.$ctEntity->getMfgName()."\r\n");


   //Form Factor
	fwrite($fp, "Form Factor".$delim.$ctEntity->getffName()."\r\n");


   //Functions
	$result = $_SESSION['PersonList'];
	$PersonArray = $result->getArray();

   fwrite($fp, "Functions".$delim);
   $functionList = &$ctEntity->getFunctionList();
	$fArray = &$functionList->getArray();
	$funcs = 0;
   fwrite($fp, "\"");//write quote
   foreach ($fArray as $functionEntity){
		$PersonEntity =$PersonArray[0];
			if ($PersonEntity->getLast()){
				$funcs = 1;
				fwrite($fp, $functionEntity->getFunction() .", ");
			}
			if ($funcs != 1){
				fwrite($fp, "Person Not Available");
			}
	}
   fwrite($fp, "\"\r\n");


   //Cognizant Person

	fwrite($fp, "Cognizant Person".$delim);
	$PersonList = &$ctEntity->getPersonList();
	$pArray = &$PersonList->getArray();
	$peeps = 0;
      foreach ($pArray as $PersonEntity) {
		if ($PersonEntity->getLast()) {
		  $peeps = 1;
          fwrite($fp, $PersonEntity->getFirst() ." ".$PersonEntity->getLast() );
        }
      }
	  if ($peeps != 1) {
	    fwrite($fp, "Person Not Available");
	    }

       fwrite($fp, "\r\n");


   //Engineering Documentation

	fwrite($fp, "Engineering Documentation".$delim);
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
		  fwrite($fp, "Documentation Not Available");;
	  }
      fwrite($fp, "\r\n");


   //Component Instances

	fwrite($fp, "Component Instances".$delim." \"".$cList->length()."\" Components Installed");
   fwrite($fp, "\r\n");


   //Control IFR

	fwrite($fp, "Control IFR".$delim);
	$IFList = &$ctEntity->getIFList();
	  $ifArray = &$IFList->getArray();
	  $ifcount = 0;
     fwrite($fp, "\"");
	  foreach ($ifArray as $IFEntity) {
	    if (($IFEntity->getRequired() == 1) && ($IFEntity->getComponentRelTypeID() == 1)) {
		  $ifcount = 1;
		  fwrite($fp, $IFEntity->getIFType().", ");
	    }
	  }
	  if ($ifcount != 1) {
		  fwrite($fp, "Interface Not Provided");
	  }
     fwrite($fp, "\"");
     fwrite($fp, "\r\n");


   //Control IFP

	fwrite($fp, "Control IFP".$delim);
	$IFList = &$ctEntity->getIFList();
	  $ifArray = &$IFList->getArray();
	  $ifcount = 0;
     fwrite($fp, "\"");
	  foreach ($ifArray as $IFEntity) {
	    if (($IFEntity->getPresented() == 1) && ($IFEntity->getComponentRelTypeID() == 1)) {
		  $ifcount = 1;
		  fwrite($fp, $IFEntity->getIFType().", ");
	    }
	  }
	  if ($ifcount != 1) {
		  fwrite($fp, "Interface Not Provided");
	  }
     fwrite($fp, "\"");
     fwrite($fp, "\r\n");


   //Housing IFR

	fwrite($fp, "Housing IFR".$delim);
	$IFList = &$ctEntity->getIFList();
	  $ifArray = &$IFList->getArray();
	  $ifcount = 0;
     fwrite($fp, "\"");
	  foreach ($ifArray as $IFEntity) {
	    if (($IFEntity->getRequired() == 1) && ($IFEntity->getComponentRelTypeID() == 2)) {
		  $ifcount = 1;
		  fwrite($fp, $IFEntity->getIFType().", ");
	    }
	  }
	  if ($ifcount != 1) {
		  fwrite($fp, "Interface Not Provided");
	  }
     fwrite($fp, "\"");
     fwrite($fp, "\r\n");


   //Housing IFP

	fwrite($fp, "Housing IFP".$delim);
	$IFList = &$ctEntity->getIFList();
	  $ifArray = &$IFList->getArray();
	  $ifcount = 0;
     fwrite($fp, "\"");
	  foreach ($ifArray as $IFEntity) {
	    if (($IFEntity->getPresented() == 1) && ($IFEntity->getComponentRelTypeID() == 2)) {
		  $ifcount = 1;
		  fwrite($fp, $IFEntity->getIFType().", ");
	    }
	  }
	  if ($ifcount != 1) {
		  fwrite($fp, "Interface Not Provided");
	  }
     fwrite($fp, "\"");
     fwrite($fp, "\r\n");


   //Power IFR

	fwrite($fp, "Power IFR".$delim);
	$IFList = &$ctEntity->getIFList();
	  $ifArray = &$IFList->getArray();
	  $ifcount = 0;
     fwrite($fp, "\"");
	  foreach ($ifArray as $IFEntity) {
	    if (($IFEntity->getRequired() == 1) && ($IFEntity->getComponentRelTypeID() == 3)) {
		  $ifcount = 1;
		  fwrite($fp, $IFEntity->getIFType().", ");
	    }
	  }
	  if ($ifcount != 1) {
		  fwrite($fp, "Interface Not Provided");
	  }
     fwrite($fp, "\"");
     fwrite($fp, "\r\n");


   //Power IFP

	fwrite($fp, "Power IFP".$delim);
	$IFList = &$ctEntity->getIFList();
	  $ifArray = &$IFList->getArray();
	  $ifcount = 0;
     fwrite($fp, "\"");
	  foreach ($ifArray as $IFEntity) {
	    if (($IFEntity->getPresented() == 1) && ($IFEntity->getComponentRelTypeID() == 3)) {
		  $ifcount = 1;
		  fwrite($fp, $IFEntity->getIFType().", ") ;
	    }
	  }
	  if ($ifcount != 1) {
		  fwrite($fp, "Interface Not Provided");
	  }
     fwrite($fp, "\"");
     fwrite($fp, "\r\n");


?>