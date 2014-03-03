<?php

/*
 * Written by Dawn Clemons
 * Specialized step in creating a text report. Specific groupings of
 * information that apply only to the AOI PHP viewer (editor) are displayed
 * in a look and feel similar to the viewer's. Only sections
 * selected by the user are displayed. The sections are: Names, General
 * Information, UPCs, PVs, MEDM Top Displays, Documents, Revision History,
 * and Notes.
 */

   //Displays specific information from the selected individual AOI report sections

   $reportsToCreate = $_POST['selected'];

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
         fwrite($fp, "=== AOI ID   ===   AOI Name ===\r\n\r\n");
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

               fwrite($fp, "\t".$aoiEntity->getAOIId()."  \t".$aoiEntity->getAOIName()."\r\n");

					$aoiNamePrevious = $aoiEntity->getAOIName();
				}
			}
      }


      //Section: GENERAL INFORMATION
      if($selection == "General Information")
      {
         fwrite($fp, "\r\n\r\n\r\n");
         fwrite($fp, "=======AOI $selection=======\r\n\r\n");

         $aoiBasicList = $_SESSION['aoiBasicList'];
		   $aoiName = $_SESSION['aoi_name'];

         fwrite($fp, "AOI Name: ".$aoiName."\r\n\r\n");

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
                  fwrite($fp, "Cognizant 1: ".$aoiBasicEntity->getAOICognizant1()."\r\n\r\n");
                  fwrite($fp, "Customer: ".$aoiBasicEntity->getAOICustomerContact()."\r\n\r\n");
                  fwrite($fp, "Criticality: ".$aoiBasicEntity->getAOICriticality()."\r\n\r\n");
                  fwrite($fp, "Technical System: ".$aoiBasicEntity->getAOITechnicalSystem()."\r\n\r\n");
                  fwrite($fp, "Machine: ".$aoiBasicEntity->getAOIMachine()."\r\n\r\n");
                  fwrite($fp, "Description: ".$aoiBasicEntity->getAOIDescription()."\r\n\r\n");
                  fwrite($fp, "Functional Criteria: ".$aoiBasicEntity->getAOIFuncCriteria()."\r\n\r\n");
                  fwrite($fp, "Keyword: ".$aoiBasicEntity->getAOIKeyword()."\r\n\r\n");
                  fwrite($fp, "Status: ".$aoiBasicEntity->getAOIStatus()."\r\n\r\n");
		        }
		     }
		   }
      }


      //Section: NOTES
      if($selection == "Notes")
      {
         fwrite($fp, "\r\n\r\n\r\n");
         fwrite($fp, "=======AOI $selection=======\r\n\r\n");

         //Get the data
			$aoiNoteList = $_SESSION['aoiNoteList'];
			if ($aoiNoteList->length() == 0){
				fwrite($fp, "No AOI Notes\r\n");

			}
			else{
            $aoiNoteEntities = $aoiNoteList->getArray();
				//Display the data
				foreach ($aoiNoteEntities as $aoiNoteEntity){
					fwrite($fp, $aoiNoteEntity->getNoteDate()."\r\n ".$aoiNoteEntity->getNote()."\r\n\r\n");
				}
			}
      }


      //Section: REVISION HISTORY
      if($selection == "Revision History")
      {
         fwrite($fp, "\r\n\r\n\r\n");
         fwrite($fp, "=======AOI $selection=======\r\n\r\n");

         //Get the data
			$aoiRevhistoryList = $_SESSION['aoiRevhistoryList'];
			if ($aoiRevhistoryList->length() == 0){
				fwrite($fp, "No AOI Revision History\r\n");
			}
			else{
            $aoiRevhistoryEntities = $aoiRevhistoryList->getArray();
				//Display the data
				fwrite($fp, "Revision Date:\r\n Comment:\r\n\r\n");
				foreach ($aoiRevhistoryEntities as $aoiRevhistoryEntity){
					fwrite($fp, $aoiRevhistoryEntity->getRevhistoryDate()."\r\n ".$aoiRevhistoryEntity->getRevhistoryComment()."\r\n");
				}
			}
      }


      //Section: DOCUMENTS
      if($selection == "Documents")
      {
         fwrite($fp, "\r\n\r\n\r\n");
         fwrite($fp, "=======AOI $selection=======\r\n\r\n");

         //Get the data
			$aoiDocumentList = $_SESSION['aoiDocumentList'];
			if ($aoiDocumentList->length() == 0){
				fwrite($fp, "No AOI Documents\r\n");
			}
			else{
            $aoiDocumentEntities = $aoiDocumentList->getArray();
				//Display the data
				fwrite($fp, "URIs:\r\n");
				foreach ($aoiDocumentEntities as $aoiDocumentEntity){
					fwrite($fp, " ".$aoiDocumentEntity->getURI()."\r\n");
				}
			}
      }



      //Section: PVS
      if($selection == "PVs")
      {
         fwrite($fp, "\r\n\r\n\r\n");
         fwrite($fp, "=======AOI $selection=======\r\n\r\n");

         //Get the data
			$aoiPvList = $_SESSION['aoiPvList'];
         $aoiPvEntities = $aoiPvList->getArray();
			if ($aoiPvList->length() == 0){
				fwrite($fp, "No AOI PVs Found"."\r\n");
			}
         else{
            //Display the data
            fwrite($fp, "Record Name:\r\n IOC:\r\n st.cmd Load Line:\r\n\r\n");
            foreach ($aoiPvEntities as $aoiPvEntity){
               fwrite($fp, $aoiPvEntity->getRecordName()."\r\n ".$aoiPvEntity->getIOCName()."\r\n ".$aoiPvEntity->getIOCStCmdLine()."\r\n");
            }
         }
      }


      //Section: UPCs
      if($selection == "UPCs")
      {
         fwrite($fp, "\r\n\r\n\r\n");
         fwrite($fp, "=======AOI $selection=======\r\n\r\n");

         $aoiList = $_SESSION['aoiList'];
         $aoiPlcEntity = $_SESSION['aoiPlcEntity'];
         $aoiIocEntity = $_SESSION['aoiIocEntity'];

         $aoiPlcList = $_SESSION['aoiPlcList'];
         $aoiIocList = $_SESSION['aoiIocList'];


	    	if ($aoiPlcList->length() == 0 && $aoiIocList->length() == 0) {
		    	fwrite($fp, "No AOI UPCs Found\r\n");
	    	}

	        $aoiPlcEntities = $aoiPlcList->getArray();
	        $aoiIocEntities = $aoiIocList->getArray();

	        foreach ($aoiPlcEntities as $aoiPlcEntity) {
				fwrite($fp, "PLC: ".$aoiPlcEntity->getPLCName()."\r\n\r\n");
			}

	        foreach ($aoiIocEntities as $aoiIocEntity) {
				fwrite($fp, "IOC: ".$aoiIocEntity->getIOCName()."\r\n\r\n");
			}
      }


      //Section: MEDM TOP DISPLAYS
      if($selection == "MEDM Top Displays")
      {
         fwrite($fp, "\r\n\r\n\r\n");
         fwrite($fp, "=======AOI $selection=======\r\n\r\n");

         //Get the data
			$aoiTopdisplayList = $_SESSION['aoiTopdisplayList'];
         $aoiTopdisplayEntities = $aoiTopdisplayList->getArray();
			if ($aoiTopdisplayList->length() == 0){
				fwrite($fp, "No AOI MEDM Top Displays Found\r\n");
			}
         else{
            //Display the data
            fwrite($fp, "URIs:\r\n");
            foreach ($aoiTopdisplayEntities as $aoiTopdisplayEntity){
               fwrite($fp, " ".$aoiTopdisplayEntity->getURI()."\r\n");
            }
         }
      }

		//Section: ST.CMD LINES
      if($selection == "StCmdLines")
      {

         fwrite($fp, "\r\n\r\n\r\n");
         fwrite($fp, "=======AOI $selection=======\r\n\r\n");

         //Get the data
         $aoiStCmdLineList = $_SESSION['aoiStCmdLineList'];

         if($aoiStCmdLineList->length() == 0)
         {
         	fwrite($fp, "No AOI st.cmd Lines Found\r\n");
         }
         else
         {

         	$aoiStCmdLineEntities = $aoiStCmdLineList->getArray();

			//Display the data
			fwrite($fp, "IOC Name:\r\n st.cmdLine:\r\n\r\n");
         	foreach ($aoiStCmdLineEntities as $aoiStCmdLineEntity) {
            	fwrite($fp, $aoiStCmdLineEntity->getIOCName()."\r\n ".$aoiStCmdLineEntity->getIOCStCmdLine()."\r\n");
         	}
         	fwrite($fp, "\r\n\r\n");
         }

      }

   }
?>