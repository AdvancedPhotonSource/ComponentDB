<?php

/*
 * Written by Dawn Clemons
 * First step in creating a text report. General information that would
 * apply to any report regardless of PHP viewer is written.
 */

   $viewName = $_POST['viewName'] ;

   //Create server-side filename. . . report will be created in this file
   //temporarily before transferring to user
   $_SESSION['genFileName'] = "/tmp/reportResultToSend.txt";

	//Open server-side file for writing generic information
   $fp = fopen($_SESSION['genFileName'], "w") or die("Could not open file.");

   //Create a timestamp in this format:     YYYY-MM-DD HH:MM:SS
   $timestamp = date(Y)."-".date(m)."-".date(d)." ".date(H).":".date(i).":".date(s);
   //Write generic information that applies to all query views
   fwrite($fp, "==IRMIS2: Integrated Relational Model of Installed Systems\r\n");
   fwrite($fp, "==$viewName Viewer Report\r\n");
   fwrite($fp, "==Date Generated: $timestamp\r\n");

   //Divert to write data specific to a particular viewer based on the viewer's type
   if($viewName == "AOI")
   {
      include_once('report_specific_text_aoi.php');
   }
   if($viewName == "IOC")
   {
      include_once('report_specific_text_ioc.php');
   }
   if($viewName == "IOCContents")
   {
      include_once('report_specific_text_ioc_contents.php');
   }
   if($viewName == "ComponentTypes")
   {
      include_once('report_specific_text_comp.php');
   }
   if($viewName == "ComponentTypeDetails")
   {
      include_once('report_specific_text_comp_details.php');
   }
   if($_POST['viewName'] == "ComponentTypeInstances")
   {
      include_once('report_specific_text_comp_instances.php');
   }
   if($_POST['viewName'] == "AOIViewerEditor")
   {
      include_once('report_specific_text_aoi_editor.php');
   }
   if($_POST['viewName'] == "IOCGeneral")
   {
      include_once('report_specific_text_ioc_general.php');
   }
   if($_POST['viewName'] == "IOCNetwork")
   {
      include_once('report_specific_text_ioc_network.php');
   }
   if($_POST['viewName'] == "SpareTypes")
   {
      include_once('report_specific_text_spares.php');
   }
   if($_POST['viewName'] == "Rack")
   {
      include_once('report_specific_text_rack.php');
   }
   if($_POST['viewName'] == "RackContents")
   {
      include_once('report_specific_text_rack_contents.php');
   }
   if($_POST['viewName'] == "RackCC")
   {
      include_once('report_specific_text_rack_component_contents.php');
   }
	if($_POST['viewName'] == "Server")
   {
      include_once('report_specific_text_server.php');
   }
	if($_POST['viewName'] == "Power")
   {
      include_once('report_specific_text_power.php');
   }
	if($_POST['viewName'] == "Cable")
   {
      include_once('report_specific_text_cable.php');
   }   

   //
   //(ADD MORE IF-STATEMENTS HERE FOR OTHER PHP VIEWERS' SPECIFIC REPORTS)
   //


   //Write the user's comments (if any) to the file
   if($_POST['comments'])
   {
      //Remove backslashes in the comment string that were automatically added
      //i.e. "Don't worry." -> "Don\'t worry." -> "Don't worry."
      $comments = stripslashes($_POST['comments']);
      fwrite($fp, "\r\n\r\n=======Comments=======\r\n\r\n$comments");
   }

   //Close the file pointer, since the report is now complete
   fclose($fp);

   //Initiates a "Save As" dialog box to prompt user to download file
	include_once('report_savedialog_text.php');


?>