<?php
/*
 * Written by Dawn Clemons
 * Form action that ensures at least one selection has been made,
 * then determines which type of report the user wants.
 */

//Make sure user has made a report selection
if(!$_POST['selected'])
{
   echo "<script language='javascript'> ";
   echo "alert ('You must make a selection first! Go back to try again.')";
   echo "</script>";
   die;
}
include_once('../report/i_common.php');

//User wants to save the results to a text file
if($_POST['text'] )
{
   include_once('../report/report_generic_text.php');
}
//User wants to view a printable version of the results
if($_POST['print'] )
{
   include_once('../report/report_generic_print.php');
}

//User wants to save the results as a CSV file
if($_POST['csv'] )
{
   include_once('../report/report_generic_csv.php');
}

//(Add more processing "functions" here
//one for each form submit button on query page)

//**Indirection based on report type desired**
?>