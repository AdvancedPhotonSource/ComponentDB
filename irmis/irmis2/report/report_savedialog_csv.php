<?php
/*
 * Written by Dawn Clemons
 * Code snippet for a "Save As" dialog box with a proposed file name to pop up.
 * It allows the user to download a report file.
 */
   //Create a proposed filename for the user:     viewName-YYYY-MM-DD.txt
   $filename =$viewName."-".date(Y)."-".date(m)."-".date(d).".csv";


   //Initiate a "Save As" dialog box so the file can be downloaded to user
   header("Content-type: application/vnd.oasis.opendocument.spreadsheet");//Document type being returned
	header("Content-Disposition: attachment; filename = $filename");
	readfile($_SESSION['genFileName']); //Transfer server-side created file to user
?>