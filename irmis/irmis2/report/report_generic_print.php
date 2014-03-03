<html>
<head>
<title>Print Results</title>
<link rel="icon" type="image/png" href="../common/images/irmisiconLogo.png" />
<link href="../report/printable_report.css" rel="stylesheet" type="text/css">
<?php include('../top/analytics.php'); ?>
</head>
<body>
<?php

/*
 * Written by Dawn Clemons
 * First step in creating a printable report. General information that would
 * apply to any report regardless of PHP viewer is added to the html page.
 */


   //Printable web page has been started, now start buffering output
   //so there is only a single output operation to the browser
	ob_start();

   //Display "generic" information that applies to all reports regardless of view type

   //Display the IRMIS logo
   echo '<img src="../common/images/irmis2Logo.jpg" name="irmis2Logo" align="absmiddle" id="irmisLogo">';
   echo '<img src="../common/images/irmis2Text.jpg" name="irmis2Text" align="absmiddle" id="irmisText">';


   echo '<div class = "genericInfo">';
   //Create a timestamp in this format:     YYYY-MM-DD HH:MM:SS
   $timestamp =  date(Y)."-".date(m)."-".date(d)." ".date(H).":".date(i).":".date(s);
   //Display the view name and time
   echo "Date Generated: $timestamp<br><br></div>";



   ///Divert to display data specific to a particular viewer based on the viewer's type
   if($_POST['viewName'] == "AOI")
   {
      include_once('report_specific_print_aoi.php');
   }
   if($_POST['viewName'] == "IOC")
   {
      include_once('report_specific_print_ioc.php');
   }
   if ($_POST['viewName'] == "IOCContents")
   {
      include_once('report_specific_print_ioc_contents.php');
   }
   if($_POST['viewName'] == "ComponentTypes")
   {
      include_once('report_specific_print_comp.php');
   }
   if($_POST['viewName'] == "ComponentTypeDetails")
   {
      include_once('report_specific_print_comp_details.php');
   }
   if($_POST['viewName'] == "ComponentTypeInstancesShort")
   {
      include_once('report_specific_print_comp_instances_short.php');
   }
   if($_POST['viewName'] == "ComponentTypeInstances")
   {
      include_once('report_specific_print_comp_instances.php');
   }
   if($_POST['viewName'] == "AOIViewerEditor")
   {
      include_once('report_specific_print_aoi_editor.php');
   }
   if($_POST['viewName'] == "IOCGeneral")
   {
      include_once('report_specific_print_ioc_general.php');
   }
   if($_POST['viewName'] == "IOCNetwork")
   {
      include_once('report_specific_print_ioc_network.php');
   }
   if($_POST['viewName'] == "SpareTypes")
   {
      include_once('report_specific_print_spares.php');
   }
   if($_POST['viewName'] == "Rack")
   {
      include_once('report_specific_print_rack.php');
   }
   if($_POST['viewName'] == "RackContents")
   {
      include_once('report_specific_print_rack_contents.php');
   }
   if($_POST['viewName'] == "RackCC")
   {
      include_once('report_specific_print_rack_component_contents.php');
   }
	if($_POST['viewName'] == "Server")
   {
   	  include_once('report_specific_print_server.php');
   }
	if($_POST['viewName'] == "Power")
   {
   	  include_once('report_specific_print_power.php');
   }
	if($_POST['viewName'] == "SG")
   {
   	  include_once('report_specific_print_sg.php');
   }  
	if($_POST['viewName'] == "SGContents")
   {
   	  include_once('report_specific_print_sg_contents.php');
   }     
	if($_POST['viewName'] == "Cable")
   {
   	  include_once('report_specific_print_cable.php');
   }

   //(ADD MORE IF-STATEMENTS HERE FOR OTHER PHP VIEWERS' SPECIFIC REPORTS)




   //Display the user's comments (if any) to the file
   if($_POST['comments'])
   {
      echo '<div class="sectionTable">';
      //Remove backslashes in the comment string that were automatically added
      //i.e. "Don't worry." -> "Don\'t worry." -> "Don't worry."
      $comments = stripslashes($_POST['comments']);
      echo '<table width="100%"  border="1" cellspacing="0" cellpadding="2">';
      echo '<tr><th colspan="2">Comments</th></tr>';
      echo '<tr><td>'."$comments".'</td></tr>';
      echo '</table><br /></div>';
   }

   //A button to print the page. Mouse cursor should be a hand when hovering over button
   echo '<input type = "button" value = "Print Page" onClick = "window.print()">';

   //Send buffer's contents to browser and clear buffer
   ob_end_flush();
   //Now end the web page
?>
</body></html>