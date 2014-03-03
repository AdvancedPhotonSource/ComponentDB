<?php

/*
 * Written by Dawn Clemons
 * Specialized step in creating a CSV report. Specific groupings of
 * information that apply only to the AOI PHP viewer (editor) are displayed
 * in a look and feel similar to the viewer's. Only sections
 * selected by the user are displayed. The sections are: Names, General
 * Information, UPCs, PVs, MEDM Top Displays, Documents, Revision History,
 * and Notes.
 */

   //Displays specific information from the selected individual AOI report sections

   $reportsToCreate = $_POST['selected'];
   $delim = ",";

   //Cycle through each item in the $reportsToCreate associative array, because it
   //contains the desired report sections to be printed. A section selection is
   //determined by the existence of its corresponding value in the associative array.
   //Selected sections' data is then displayed accordingly.
   foreach($reportsToCreate as $selection)
   {
      //Section: AOI NAMES
      if($selection == "Names")
      {
         fwrite($fp, "\r\n\r\n\r\n");
         fwrite($fp, "AOI ID".$delim."AOI Name\r\n\r\n");
         //Get the data
         if ($_SESSION['searchAOIs'] == "View All AOIs"){
	   		$aoiList = $_SESSION['aoiNameList'];
         }
         else{
 	   		$aoiList = $_SESSION['aoiList'];
         }
         $aoiEntities = $aoiList->getArray();

         foreach ($aoiEntities as $aoiEntity){
				$_SESSION['aoi_name'] = $aoiEntity->getAOIName();
				if ($aoiEntity->getAOIName() != $aoiNamePrevious){

               fwrite($fp, $aoiEntity->getAOIId().$delim.$aoiEntity->getAOIName()."\r\n");

					$aoiNamePrevious = $aoiEntity->getAOIName();
				}
			}
      }


      //Section: GENERAL INFORMATION
      if($selection == "General Information")
      {
         fwrite($fp, "\r\n\r\n\r\n");
         fwrite($fp, "AOI $selection\r\n\r\n");

         $aoiBasicList = $_SESSION['aoiBasicList'];
		   $aoiName = $_SESSION['aoi_name'];

         fwrite($fp, "AOI Name: ".$aoiName."\r\n");

		   if ($aoiBasicList->length() == 0) {
			   fwrite($fp, "AOI General Information Is Incomplete\r\n");
		   }
		   else {
		      $aoiBasicEntities = $aoiBasicList->getArray();
		      foreach ($aoiBasicEntities as $aoiBasicEntity) {

               if ($aoiBasicEntity == null) {
                  fwrite($fp, "ERROR:  Cannot open display for AOI General Information.\r\n");
               }

               else {
                  fwrite($fp, "Cognizant 1: ".$delim."\"".$aoiBasicEntity->getAOICognizant1()."\""."\r\n");
                  fwrite($fp, "Customer: ".$delim."\"".$aoiBasicEntity->getAOICustomerContact()."\""."\r\n");
                  fwrite($fp, "Criticality: ".$delim."\"".$aoiBasicEntity->getAOICriticality()."\""."\r\n");
                  fwrite($fp, "Technical System: ".$delim."\"".$aoiBasicEntity->getAOITechnicalSystem()."\""."\r\n");
                  fwrite($fp, "Machine: ".$delim."\"".$aoiBasicEntity->getAOIMachine()."\""."\r\n");
                  fwrite($fp, "Description: ".$delim."\"".$aoiBasicEntity->getAOIDescription()."\""."\r\n");
                  fwrite($fp, "Functional Criteria: ".$delim."\"".$aoiBasicEntity->getAOIFuncCriteria()."\""."\r\n");
                  fwrite($fp, "Keyword: ".$delim."\"".$aoiBasicEntity->getAOIKeyword()."\""."\r\n");
                  fwrite($fp, "Status: ".$delim."\"".$aoiBasicEntity->getAOIStatus()."\""."\r\n");
		        }
		     }
		   }
      }


      //Section: NOTES
      if($selection == "Notes")
      {
         fwrite($fp, "\r\n\r\n\r\n");
         fwrite($fp, "AOI $selection\r\n\r\n");

         //Get the data
			$aoiNoteList = $_SESSION['aoiNoteList'];
			if ($aoiNoteList->length() == 0){
				fwrite($fp, "No AOI Notes\r\n");

			}
			else{
            $aoiNoteEntities = $aoiNoteList->getArray();
				//Display the data
				foreach ($aoiNoteEntities as $aoiNoteEntity){
					fwrite($fp, $aoiNoteEntity->getNoteDate().$delim."\"".$aoiNoteEntity->getNote()."\""."\r\n");
				}
			}
      }


      //Section: REVISION HISTORY
      if($selection == "Revision History")
      {
         fwrite($fp, "\r\n\r\n\r\n");
         fwrite($fp, "AOI $selection\r\n\r\n");

         //Get the data
			$aoiRevhistoryList = $_SESSION['aoiRevhistoryList'];
			if ($aoiRevhistoryList->length() == 0){
				fwrite($fp, "No AOI Revision History\r\n");
			}
			else{
            $aoiRevhistoryEntities = $aoiRevhistoryList->getArray();
				//Display the data
				foreach ($aoiRevhistoryEntities as $aoiRevhistoryEntity){
					fwrite($fp, $aoiRevhistoryEntity->getRevhistoryDate().$delim."\"".$aoiRevhistoryEntity->getRevhistoryComment()."\""."\r\n");
				}
			}
      }


      //Section: DOCUMENTS
      if($selection == "Documents")
      {
         fwrite($fp, "\r\n\r\n\r\n");
         fwrite($fp, "AOI $selection\r\n\r\n");

         //Get the data
			$aoiDocumentList = $_SESSION['aoiDocumentList'];
			if ($aoiDocumentList->length() == 0){
				fwrite($fp, "No AOI Documents\r\n");
			}
			else{
            $aoiDocumentEntities = $aoiDocumentList->getArray();
				//Display the data
				foreach ($aoiDocumentEntities as $aoiDocumentEntity){
					fwrite($fp, $aoiDocumentEntity->getURI()."\r\n");
				}
			}
      }



      //Section: PVS
      if($selection == "PVs")
      {
         fwrite($fp, "\r\n\r\n\r\n");
         fwrite($fp, "AOI $selection\r\n\r\n");

         //Get the data
			$aoiPvList = $_SESSION['aoiPvList'];
         $aoiPvEntities = $aoiPvList->getArray();
			if ($aoiPvList->length() == 0){
				fwrite($fp, "No AOI PVs Found"."\r\n");
			}
         else{
            //Display the data
            fwrite($fp, "Record Name".$delim."IOC".$delim."st.cmd Load Line"."\r\n");
            foreach ($aoiPvEntities as $aoiPvEntity){
               fwrite($fp, $aoiPvEntity->getRecordName().$delim.$aoiPvEntity->getIOCName());

               $doubleDoubleQuotesStCmdLine = str_replace("\"", "\"\"",$aoiPvEntity->getIOCStCmdLine() );

               fwrite($fp, $delim."\"".$doubleDoubleQuotesStCmdLine."\""."\r\n\r\n");
            }
         }
      }


      //Section: UPCs
      if($selection == "UPCs")
      {
         fwrite($fp, "\r\n\r\n\r\n");
         fwrite($fp, "AOI $selection\r\n\r\n");

         $aoiPlcList = $_SESSION['aoiPlcList'];
         $aoiIocList = $_SESSION['aoiIocList'];


	    	if ($aoiPlcList->length() == 0 && $aoiIocList->length() == 0) {
		    	fwrite($fp, "No AOI UPCs Found\r\n");
	    	}

	        $aoiPlcEntities = $aoiPlcList->getArray();
	        $aoiIocEntities = $aoiIocList->getArray();

	        foreach ($aoiPlcEntities as $aoiPlcEntity) {
				fwrite($fp, "PLC".$delim.$aoiPlcEntity->getPLCName()."\r\n\r\n");
			}

	        foreach ($aoiIocEntities as $aoiIocEntity) {
				fwrite($fp, "IOC".$delim.$aoiIocEntity->getIOCName()."\r\n\r\n");
			}
      }


      //Section: MEDM TOP DISPLAYS
      if($selection == "MEDM Top Displays")
      {
         fwrite($fp, "\r\n\r\n\r\n");
         fwrite($fp, "AOI $selection\r\n\r\n");

         //Get the data
			$aoiTopdisplayList = $_SESSION['aoiTopdisplayList'];
         $aoiTopdisplayEntities = $aoiTopdisplayList->getArray();
			if ($aoiTopdisplayList->length() == 0){
				fwrite($fp, "No AOI MEDM Top Displays Found\r\n");
			}
         else{
            //Display the data
            foreach ($aoiTopdisplayEntities as $aoiTopdisplayEntity){
               fwrite($fp, $aoiTopdisplayEntity->getURI()."\r\n");
            }
         }
      }

      if($selection == "StCmdLines")
      {
         fwrite($fp, "\r\n\r\n\r\n");
         fwrite($fp, "AOI $selection\r\n\r\n");

         //Get the data
         $aoiStCmdLineList = $_SESSION['aoiStCmdLineList'];

         if($aoiStCmdLineList->length() == 0)
         {
         	fwrite($fp, "No st.cmd Lines Found\r\n");
         }
         else
         {
         	$aoiStCmdLineEntities = $aoiStCmdLineList->getArray();

         	fwrite($fp, "IOC Name".$delim."st.cmd Line"."\r\n");

         	foreach ($aoiStCmdLineEntities as $aoiStCmdLineEntity) {
            	fwrite($fp, $aoiStCmdLineEntity->getIOCName().$delim.$aoiStCmdLineEntity->getIOCStCmdLine()."\r\n");
         	}
        }



      }

   }
?>