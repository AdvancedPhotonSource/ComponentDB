<?php

/*
 * Written by Dawn Clemons
 * Modified by Scott Benes
 * First step in creating a text report. General information that would
 * apply to any report regardless of PHP viewer is written.
 */

   $viewName = $_POST['viewName'] ;

   //Create server-side filename. . . report will be created in this file
   //temporarily before transferring to user
   $_SESSION['genFileName'] = "/tmp/reportResultToSend.csv";

	//Open server-side file for writing generic information
   $fp = fopen($_SESSION['genFileName'], "w") or die("Could not open file.");

   //Create a timestamp in this format:     YYYY-MM-DD HH:MM:SS
   $timestamp = date(Y)."-".date(m)."-".date(d)." ".date(H).":".date(i).":".date(s);
   //Write generic information that applies to all query views
   fwrite($fp, "IRMIS2\r\n");
   fwrite($fp, "$viewName Viewer Report\r\n");
   fwrite($fp, "$timestamp\r\n");





   //Divert to write data specific to a particular viewer based on the viewer's type
   if($viewName == "IOC")
   {
      include_once('report_specific_csv_ioc.php');
   }
   if($viewName == "IOCContents")
   {
      include_once('report_specific_csv_ioc_contents.php');
   }
    if($_POST['viewName'] == "ComponentTypes")
   {
      include_once('report_specific_csv_comp.php');
   }
   if($_POST['viewName'] == "ComponentTypeInstancesShort")
   {
      include_once('report_specific_csv_comp_instances_short.php');
   }
   if($_POST['viewName'] == "ComponentTypeInstances")
   {
      include_once('report_specific_csv_comp_instances.php');
   }
   if($_POST['viewName'] == "AOIViewerEditor")
   {
      include_once('report_specific_csv_aoi_editor.php');
   }
   if($_POST['viewName'] == "IOCGeneral")
   {
      include_once('report_specific_csv_ioc_general.php');
   }
   if($_POST['viewName'] == "IOCNetwork")
   {
      include_once('report_specific_csv_ioc_network.php');
   }
   if($_POST['viewName'] == "ComponentTypeDetails")
   {
      include_once('report_specific_csv_comp_details.php');
   }
   if($_POST['viewName'] == "SpareTypes")
   {
      include_once('report_specific_csv_spares.php');
   }
   if($_POST['viewName'] == "Rack")
   {
      include_once('report_specific_csv_rack.php');
   }
   if($_POST['viewName'] == "RackContents")
   {
      include_once('report_specific_csv_rack_contents.php');
   }
   if($_POST['viewName'] == "RackCC")
   {
      include_once('report_specific_csv_rack_component_contents.php');
   }
 	if($_POST['viewName'] == "Server")
   {
      include_once('report_specific_csv_server.php');
   }
 	if($_POST['viewName'] == "Power")
   {
      include_once('report_specific_csv_power.php');
   }
	if($_POST['viewName'] == "SG")
   {
   	  include_once('report_specific_csv_sg.php');
   }  
	if($_POST['viewName'] == "SGContents")
   {
   	  include_once('report_specific_csv_sg_contents.php');
   }      
 	if($_POST['viewName'] == "Cable")
   {
      include_once('report_specific_csv_cable.php');
   }   
   
   //
   //(ADD MORE IF-STATEMENTS HERE FOR OTHER PHP VIEWERS' SPECIFIC REPORTS)
   //





   //Close the file pointer, since the report is now complete
   fclose($fp);

   //Initiates a "Save As" dialog box to prompt user to download file
	include_once('report_savedialog_csv.php');


?>