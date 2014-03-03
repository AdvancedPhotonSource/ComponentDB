<?php

/*
 * Written by Dawn Clemons
 * Specialized step in creating a printable report. Specific groupings of
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


 /*  foreach($reportsToCreate as $selection)
   {
      echo $selection."   ";
   }*/


   foreach($reportsToCreate as $selection)
   {
      //Section: AOI NAMES
      if($selection == "Names")
      {
         echo '<div class = "sectionTable"><table width = "100%" border = "1" cellspacing = "0" cellpadding = "2">';
         echo '<tr><td>AOI ID</td><td>AOI Names</td></tr>';
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
               echo '<tr><td>';
               echo $aoiEntity->getAOIID();
               echo '</td><td>';
               echo $aoiEntity->getAOIName();
               echo '</td></tr>';
					$aoiNamePrevious = $aoiEntity->getAOIName();
				}
			}
         echo '</table></div><br>';
      }


      //Section: GENERAL INFORMATION
      if($selection == "General Information")
      {
         $aoiBasicList = $_SESSION['aoiBasicList'];
		   $aoiName = $_SESSION['aoi_name'];

         echo '<div class="sectionTable"><table width="100%"  border="1" cellspacing="0" cellpadding="2">';
         echo '<tr><th colspan="2" class="center">AOI General Information</th></tr>';
         echo '<tr><th class="center" width=20%>AOI Name</th><th>'.$aoiName.'</th></tr>';

		   if ($aoiBasicList->length() == 0) {
			   echo '<tr><td colspan = "2">AOI General Information Is Incomplete</td></tr>';
		   }
		   else {
		      $aoiBasicEntities = $aoiBasicList->getArray();
		      foreach ($aoiBasicEntities as $aoiBasicEntity) {

               if ($aoiBasicEntity == null) {
                  echo '<tr><td class="warning bold" colspan=8>ERROR:  Cannot open display for AOI General Information.<br>';
               }

               else {
               echo '<tr><td class="primary">Cognizant 1</td><td class="center">'.$aoiBasicEntity->getAOICognizant1().'</td></tr>';
               echo '<tr><td class="primary">Customer</td><td class="center">'.$aoiBasicEntity->getAOICustomerContact().'</td></tr>';
               echo '<tr><td class="primary">Criticality</td><td class="center">'.$aoiBasicEntity->getAOICriticality().'</td></tr>';
               echo '<tr><td class="primary">Technical System</td><td class="center">'.$aoiBasicEntity->getAOITechnicalSystem().'</td></tr>';
               echo '<tr><td class="primary">Machine</td><td class="center">'.$aoiBasicEntity->getAOIMachine().'</td></tr>';
               echo '<tr><td class="primary">Description</td><td class="center">'.$aoiBasicEntity->getAOIDescription().'</td></tr>';
               echo '<tr><td class="primary" width=25%>Functional Criteria</td><td class="center">'.$aoiBasicEntity->getAOIFuncCriteria().'</td></tr>';
               echo '<tr><td class="primary">Keyword</td><td class="center">'.$aoiBasicEntity->getAOIKeyword().'</td></tr>';
               echo '<tr><td class="primary">Status</td><td class="center">'.$aoiBasicEntity->getAOIStatus().'</td></tr>';
		        }
		     }
		   }
         echo '</table></div><br>';
      }


      //Section: NOTES
      if($selection == "Notes")
      {
         echo '<div class="sectionTable"><table width="100%"  border="1" cellspacing="0" cellpadding="2">';
         echo '<tr><th colspan="3">AOI Notes</th></tr>';
         //Get the data
			$aoiNoteList = $_SESSION['aoiNoteList'];
			$aoiNoteEntity = $_SESSION['aoiNoteEntity'];
			if ($aoiNoteList->length() == 0)
			{
				echo '<tr><td>No AOI Notes</td></tr>';

			}
			else
			{
				echo '<tr><td class="value" width = "25%">Note Date</td><td class="value">Note</td></tr>';
            $aoiNoteEntities = $aoiNoteList->getArray();
				//Display the data
				foreach ($aoiNoteEntities as $aoiNoteEntity)
				{
					echo '<tr> <td>'.$aoiNoteEntity->getNoteDate().'</td><td>'.$aoiNoteEntity->getNote().'</td></tr>';
				}
			}
			echo '</table></div><br>';
      }


      //Section: REVISION HISTORY
      if($selection == "Revision History")
      {
         echo '<div class="sectionTable"><table width="100%"  border="1" cellspacing="0" cellpadding="2">';
         echo '<tr><th colspan="3" >AOI Revision History</th></tr>';
         //Get the data
			$aoiRevhistoryList = $_SESSION['aoiRevhistoryList'];
			$aoiRevhistoryEntity = $_SESSION['aoiRevhistoryEntity'];
			if ($aoiRevhistoryList->length() == 0)
			{
				echo '<tr><td>No AOI Revision History</td></tr>';
			}
			else
			{
				echo '<tr> <td class="value" width = "25%">Revision Date</td><td class="value">Comment</td></tr>';
            $aoiRevhistoryEntities = $aoiRevhistoryList->getArray();
				//Display the data
				foreach ($aoiRevhistoryEntities as $aoiRevhistoryEntity)
				{
					echo '<tr> <td>'.$aoiRevhistoryEntity->getRevhistoryDate().'</td><td>'.$aoiRevhistoryEntity->getRevhistoryComment().'</td></tr>';
				}
			}
			echo '</table></div><br>';
      }


      //Section: DOCUMENTS
      if($selection == "Documents")
      {
         echo '<div class="sectionTable"><table width="100%"  border="1" cellspacing="0" cellpadding="2">';
         echo '<tr><th colspan="3">AOI Documents</th></tr>';
         //Get the data
			$aoiDocumentList = $_SESSION['aoiDocumentList'];
			if ($aoiDocumentList->length() == 0)
			{
				echo '<tr><td>No AOI Documents</td></tr>';
			}
			else
			{
				echo '<tr><td class="value">URI</td></tr>';
            $aoiDocumentEntities = $aoiDocumentList->getArray();
				//Display the data
				foreach ($aoiDocumentEntities as $aoiDocumentEntity)
				{
					echo '<tr> <td>'.$aoiDocumentEntity->getURI().'</td></tr>';
				}
			}
			echo '</table></div><br>';
      }



      //Section: PVS
      if($selection == "PVs")
      {
         echo '<div class="sectionTable"><table width="100%"  border="1" cellspacing="0" cellpadding="2">';
         echo '<tr><th colspan="3">AOI PVs</th></tr>';
         //Get the data
			$aoiPvList = $_SESSION['aoiPvList'];
         $aoiPvEntities = $aoiPvList->getArray();
			if ($aoiPvList->length() == 0)
			{
				echo '<tr><td>No AOI PVs Found</td></tr>';
			}
         else
         {
            echo '<tr> <td class="value">Record Name</td> <td class="value">IOC</td><td class="value">st.cmd Load Line</td></tr>';
            //Display the data
            foreach ($aoiPvEntities as $aoiPvEntity)
            {

               //Break up the st.cmd Line if necessary
               //put line breaks after dbLoadRecords(    and after . . . /watertest.db",
               echo '<tr> <td>'.$aoiPvEntity->getRecordName().'</td> <td>'.$aoiPvEntity->getIOCName().'</td> <td>';
               if(! ($IOCStCmdLineWithLineBreaks = str_replace("dbLoadRecords(", "dbLoadRecords(<br>",$aoiPvEntity->getIOCStCmdLine() ) ) ) {
                  echo $aoiPvEntity->getIOCStCmdLine().'</td></tr>';
               }

               else {
                  $IOCStCmdLineWithLineBreaks = str_replace("\",\"", "\",<br>\"", $IOCStCmdLineWithLineBreaks);
                  echo $IOCStCmdLineWithLineBreaks.'</td></tr>';
               }
            }
         }
         echo '</table></div><br>';
      }


      //Section: UPCs
      if($selection == "UPCs")
      {
         echo '<div class="sectionTable"><table width="100%"  border="1" cellspacing="0" cellpadding="2">';
         echo '<tr><th colspan="3" >AOI UPCs</th></tr>';

         $aoiPlcList = $_SESSION['aoiPlcList'];
         $aoiIocList = $_SESSION['aoiIocList'];
         $aoiPlcEntities = $aoiPlcList->getArray();
	      $aoiIocEntities = $aoiIocList->getArray();

	    	if ($aoiPlcList->length() == 0 && $aoiIocList->length() == 0){
		    	echo '<tr><td colspan = "2">No AOI UPCs Found</td></tr>';
	    	}
         else
         {
            echo '<tr> <td class = "value">UPC Type</td> <td class="value">UPC Name</td></tr>';

	        foreach ($aoiPlcEntities as $aoiPlcEntity) {
               echo '<tr> <td>PLC</td><td>'.$aoiPlcEntity->getPLCName().'</td> </tr>';
            }

	        foreach ($aoiIocEntities as $aoiIocEntity) {
               echo '<tr><td>IOC</td><td>'.$aoiIocEntity->getIOCName().'</td> </tr>';
            }
         }
         echo '</table></div><br>';
      }


      //Section: MEDM TOP DISPLAYS
      if($selection == "MEDM Top Displays")
      {
         echo '<div class="sectionTable"><table width="100%"  border="1" cellspacing="0" cellpadding="2">';
         echo '<tr><th colspan="3" >AOI MEDM Top Displays</th></tr>';
         //Get the data
			$aoiTopdisplayList = $_SESSION['aoiTopdisplayList'];
         $aoiTopdisplayEntities = $aoiTopdisplayList->getArray();
			if ($aoiTopdisplayList->length() == 0)
			{
				echo '<tr><td>No AOI MEDM Top Displays Found</td></tr>';
			}
         else
         {
            echo '<tr> <td class="value">URI</td></tr>';
            //Display the data
            foreach ($aoiTopdisplayEntities as $aoiTopdisplayEntity)
            {
               echo '<tr><td>'.$aoiTopdisplayEntity->getURI().'</td></tr>';
            }
         }
			echo '</table></div><br>';
      }

      if($selection == "StCmdLines")
      {

         echo '<div class="sectionTable"><table width="100%"  border="1" cellspacing="0" cellpadding="2">';
         echo '<tr><th colspan = "2">AOI st.cmd Lines</th></tr>';

         $aoiStCmdLineList = $_SESSION['aoiStCmdLineList'];

         if ($aoiStCmdLineList->length() == 0) {
		      echo '<tr><td>No AOI st.cmd Lines Found</td></tr>';
		 }
		 else{

      		echo '<tr><td class="value">IOC Name</td> <td class="value">st.cmd Line</td></tr>';

         	$aoiStCmdLineEntities = $aoiStCmdLineList->getArray();

         	foreach ($aoiStCmdLineEntities as $aoiStCmdLineEntity) {
               //Break up the st.cmd Line if necessary
               //put line breaks after dbLoadRecords(    and after . . . /watertest.db",
               echo '<tr> <td>'.$aoiStCmdLineEntity->getIOCName().'</td> <td>';
               if(! ($StCmdLineEntityWithLineBreaks = str_replace("dbLoadRecords(", "dbLoadRecords(<br>",$aoiStCmdLineEntity->getIOCStCmdLine() ) ) ) {
                  echo $StCmdLineEntityWithLineBreaks>getIOCStCmdLine().'</td></tr>';
               }

               else {
                  $StCmdLineEntityWithLineBreaks = str_replace("\",\"", "\",<br>\"", $StCmdLineEntityWithLineBreaks);
                  echo $StCmdLineEntityWithLineBreaks.'</td></tr>';
               }






         	}
		}
         echo '</table></div><br>';

      }

   }
?>