<?php
/*
 * AOI Application Common Include
 * Common file for header of all top-level php pages of AOI application.
 * Defines any needed classes and functions, and establishes needed default data
 * in session if it does not already exist.
 */

// logging util
include_once ('../util/i_logging.php');

// db access layer
include_once ('../db/i_db_connect.php');
include_once ('../db/i_db_querybuilder.php');
include_once ('../db/i_db_entity_aoi_name.php');
include_once ('../db/i_db_entity_aoi.php');
include_once ('../db/i_db_entity_aoi_basic.php');
include_once ('../db/i_db_entity_technical_system_pd.php');
include_once ('../db/i_db_entity_machine_pd.php');
include_once ('../db/i_db_entity_group_name_pd.php');
include_once ('../db/i_db_entity_ioc_pd.php');
include_once ('../db/i_db_entity_plc_pd.php');
include_once ('../db/i_db_entity_criticality_pd.php');
include_once ('../db/i_db_entity_aoi_doc_type_pd.php');
include_once ('../db/i_db_entity_aoi_status_pd.php');
include_once ('../db/i_db_entity_person_pd.php');
include_once ('../db/i_db_entity_aoi_pv.php');
include_once ('../db/i_db_entity_aoi_plc.php');
include_once ('../db/i_db_entity_aoi_ioc.php');
include_once ('../db/i_db_entity_aoi_topdisplay.php');
include_once ('../db/i_db_entity_aoi_document.php');
include_once ('../db/i_db_entity_aoi_revhistory.php');
include_once ('../db/i_db_entity_aoi_stcmd_line.php');
include_once ('../db/i_db_entity_aoi_editor.php');


// all php applications need this
include_once ('../common/i_common_common.php');

	if(isset($_SESSION['SESSION_NAME'])){
 		session_name($_SESSION['SESSION_NAME']);
 	}

 	$test = substr(session_name(),0,4);

 	if ($test == "PHPS" || $test == ""){

		$my_session_name = 'SESS'.uniqid();

		session_name($my_session_name);
 	}

 	$_SESSION['SESSION_NAME'] = session_name();


// establish connection to db (note: actually re-uses pooled connections)
// args are: host, port, dbname, user, passwd, tableNamePrefix

include_once ('db.inc');

// Make sure that we always have an initialized
//  aoiList object in the session. Note that this
//  does not create one if it is already set in the
//  session.

if (!isset ($_SESSION['aoiList'])) {
  $initializedAOIList = new aoiList();
  $_SESSION['aoiList'] = $initializedAOIList;
}

if (!isset ($_SESSION['aoiNameList'])) {
  $initializedAOINameList = new aoiNameList();
  $_SESSION['aoiNameList'] = $initializedAOINameList;
}

if (!isset ($_SESSION['aoiBasicList'])) {
  $initializedAOIBasicList = new aoiBasicList();
  $_SESSION['aoiBasicList'] = $initializedAOIBasicList;
}

if (!isset ($_SESSION['aoiPvList'])) {
  $initializedAOIPVList = new aoiPvList();
  $_SESSION['aoiPvList'] = $initializedAOIPVList;
}

if (!isset ($_SESSION['aoiStCmdLineList'])) {
  $initializedAOIStCmdLineList = new aoiStCmdLineList();
  $_SESSION['aoiStCmdLineList'] = $initializedAOIStCmdLineList;
}

if (!isset ($_SESSION['aoiTopdisplayList'])) {
  $initializedAOITopdisplayList = new aoiTopdisplayList();
  $_SESSION['aoiTopdisplayList'] = $initializedAOITopdisplayList;
}

if (!isset ($_SESSION['aoiDocumentList'])) {
  $initializedAOIDocumentList = new aoiDocumentList();
  $_SESSION['aoiDocumentList'] = $initializedAOIDocumentList;
}

if (!isset ($_SESSION['aoiRevhistoryList'])) {
  $initializedAOIRevhistoryList = new aoiRevhistoryList();
  $_SESSION['aoiRevhistoryList'] = $initializedAOIRevhistoryList;
}

if (!isset ($_SESSION['aoiIocList'])) {
  $initializedAOIIOCList = new aoiIOCList();
  $_SESSION['aoiIocList'] = $initializedAOIIOCList;
}

if (!isset ($_SESSION['aoiPlcList'])) {
  $initializedAOIPLCList = new aoiPLCList();
  $_SESSION['aoiPlcList'] = $initializedAOIPLCList;
}

if (!isset ($_SESSION['aoiEditorList'])) {
  $initializedAOIEditorList = new aoiEditorList();
  $_SESSION['aoiEditorList'] = $initializedAOIEditorList;
}

// make sure we have initial blank value for aoi name constraint
if (!isset ($_SESSION['aoiNameConstraint'])) {
  $_SESSION['aoiNameConstraint'] = "";
}

// make sure we have initial blank value for aoi id constraint
if (!isset ($_SESSION['aoiIDConstraint'])) {
  $_SESSION['aoiIDConstraint'] = "";
}

// make sure we have initial blank value for aoi editor constraint
if (!isset ($_SESSION['aoiEditorConstraint'])) {
  $_SESSION['aoiEditorConstraint'] = "";
}

// make sure we have initial blank value for php editor session agent
if (!isset ($_SESSION['agent'])) {
  $_SESSION['agent'] = "";
}

// make sure we have initial blank value for php editor user name
if (!isset ($_SESSION['user_name'])) {
  $_SESSION['user_name'] = "";
}

// make sure we have initial blank value for aoi keyword
if (!isset ($_SESSION['aoi_keyword'])) {
  $_SESSION['aoi_keyword'] = "";
}

// make sure we have initial blank value for aoi worklog
if (!isset ($_SESSION['aoi_worklog'])) {
  $_SESSION['aoi_worklog'] = "";
}

// make sure we have initial blank value for aoi PvSearch word
if (!isset ($_SESSION['pv_search'])) {
  $_SESSION['pv_search'] = "";
}

if (!isset ($_SESSION['desc_search'])) {
  $_SESSION['desc_search'] = "";
}

if (!isset ($_SESSION['aoiViewAll'])) {
  $_SESSION['aoiViewAll'] = "";
}

if (!isset ($_SESSION['create_new_aoi_from_blank'])) {
  $_SESSION['create_new_aoi_from_blank'] = "false";
}

if (!isset ($_SESSION['create_new_aoi_from_selected'])) {
  $_SESSION['create_new_aoi_from_selected'] = "false";
}

if (!isset ($_SESSION['modify_selected_aoi'])) {
  $_SESSION['modify_selected_aoi'] = "false";
}

if (!isset ($_SESSION['show_new_aoi'])) {
  $_SESSION['show_new_aoi'] = "false";
}

if (!isset ($_SESSION['aoi_selected'])) {
  $_SESSION['aoi_selected'] = "false";
}

if (!isset ($_SESSION['status_message'] )) {
  $_SESSION['status_message'] = "";
}

if (!isset ($_SESSION['techsystemList'])) {
   $initializedTechSystemList = new techsystemList();
   if (!$initializedTechSystemList->loadFromDB($conn)) {
       include('../common/db_error.php');
       exit;
   }
   $_SESSION['techsystemList'] = $initializedTechSystemList;
}

if (!isset ($_SESSION['machineList'])) {
   $initializedMachineList = new machineList();
   if (!$initializedMachineList->loadFromDB($conn)) {
       include('../common/db_error.php');
       exit;
   }
   $_SESSION['machineList'] = $initializedMachineList;
}

if (!isset ($_SESSION['groupNameList'])) {
   $initializedGroupNameList = new groupNameList();
   if (!$initializedGroupNameList->loadFromDB($conn)) {
       include('../common/db_error.php');
       exit;
   }
   $_SESSION['groupNameList'] = $initializedGroupNameList;
}

if (!isset ($_SESSION['plcnameList'])) {
   $initializedPlcNameList = new plcnameList();
   if (!$initializedPlcNameList->loadFromDB($conn)) {
       include('../common/db_error.php');
       exit;
   }
   $_SESSION['plcnameList'] = $initializedPlcNameList;
}

if (!isset ($_SESSION['iocnameList'])) {
   $initializedIocNameList = new iocnameList();
   if (!$initializedIocNameList->loadFromDB($conn)) {
       include('../common/db_error.php');
       exit;
   }
   $_SESSION['iocnameList'] = $initializedIocNameList;
}

if (!isset ($_SESSION['personList'])) {
   $initializedPersonList = new personList();
   if (!$initializedPersonList->loadFromDB($conn)) {
       include('../common/db_error.php');
       exit;
   }
   $_SESSION['personList'] = $initializedPersonList;
}

if (!isset ($_SESSION['aoiDocTypeList'])) {
   $initializedaoiDocTypeList = new aoiDocTypeList();
   if (!$initializedaoiDocTypeList->loadFromDB($conn)) {
       include('../common/db_error.php');
       exit;
   }
   $_SESSION['aoiDocTypeList'] = $initializedaoiDocTypeList;
}

if (!isset ($_SESSION['criticalityList'])) {
   $initializedCriticalityList = new criticalityList();
   if (!$initializedCriticalityList->loadFromDB($conn)) {
       include('../common/db_error.php');
       exit;
   }
   $_SESSION['criticalityList'] = $initializedCriticalityList;
}

if (!isset ($_SESSION['aoiStatusList'])) {
   $initializedAOIStatusList = new aoiStatusList();
   if (!$initializedAOIStatusList->loadFromDB($conn)) {
       include('../common/db_error.php');
       exit;
   }
   $_SESSION['aoiStatusList'] = $initializedAOIStatusList;
}

if (!isset ($_SESSION['ICMS_OK'])) {
   $_SESSION['ICMS_OK'] = true;
}

if (!isset ($_SESSION['wsdl_search'])) {
   $_SESSION['wsdl_search'] = 'https://icmsdocs.aps.anl.gov/docs/idcplg?IdcService=DISPLAY_URL&dDocName=SEARCH';

}

if (!isset ($_SESSION['wsdl_checkin'])) {
   $_SESSION['wsdl_checkin'] = 'https://icmsdocs.aps.anl.gov/docs/idcplg?IdcService=DISPLAY_URL&dDocName=CHECKIN';
}

// AOI specific functions and associated configuration data

$ctls_link = 'http://www.aps.anl.gov/asd/controls';

$wiki_link['Task'] = "$ctls_link/tman/tasks.php?/id=";

$wiki_link['Ctllog'] = "$ctls_link/newlog/entries.php?id=";

$wiki_link['IDlog'] = "$ctls_link/idlog/entries.php?id=";

$rmd_cgi = 'http://www.aps4.anl.gov/cgi-bin/ops/rmd-cgi';

$wiki_link['RMD'] = "$rmd_cgi/rmd_view.cgi?mode=manager&rmdNo=";

$wiki_link['RSP'] = "$rmd_cgi/response_view.cgi?mode=manager&responseNo=";

if (!isset($_SESSION['wiki_link'])){
	$_SESSION['wiki_link'] = $wiki_link;
}

function getHtml($my_text) {

	return htmlspecialchars(stripcslashes($my_text));

}

function getWikiHtml($my_text) {
	$pattern = array(
					'/&#39;/',
					'/&quot;/',
					'/&lt;/',
					'/&gt;/',
					'/&amp;/',
					'/\'\'(.+)\'\'/sU',
					'/__(.+)__/sU',
					'/%%%/',
					'/^----+\r?\n/m',
					'/^!!!([^\r\n]+)\r?\n/m',
					'/^!!([^\r\n]+)\r?\n/m',
					'/^!([^\r\n]+)\r?\n/m',
					'/^(( [^\r\n]*\r?\n)*( [^\r\n]*))/m',
					'/\r?\n\r?\n/');

	$replace = array(
					"'",
					'"',
					'<',
					'>',
					'&',
					'<i>$1</i>',
					'<b>$1</b>',
					'<br>',
					'</p><hr><p>',
					'</p><h2>$1</h2><p>',
					'</p><h3>$1</h3><p>',
					'</p><h4>$1</h4><p>',
					'<pre>$1</pre>',
					'</p><p>');

	$my_wiki_link = $_SESSION['wiki_link'];

	foreach ($my_wiki_link as $name => $url){

		array_unshift($pattern, "/$name#([1-9][0-9]*)/");
		array_unshift($replace, "<a href='$url\$1'>$name#\$1</a>");

	}

	array_unshift($pattern, '%https?://[-+.:@0-9A-Za-z_]+(/[^<> "()\r\n\t]*)%');

	array_unshift($replace, '<a href="$0">$0</a>');

	$html = preg_replace($pattern, $replace, getHtml($my_text));

	if (preg_match('/<p>/', $html)) $html = "<p>$html</p>\n";

	return $html;
}

function wikiHelp() { ?>

  <br></br>

  <p>&nbsp;&nbsp;<font size="4"<A name="worklog_help">&nbsp;AOI Worklog & Description: Rich Text Formatting</A></font></p>
  <p>&nbsp;&nbsp;The <b><A href="#worklog_top">Worklog</A></b> and <b><A href="#description_top">Description</A></b> fields may use these formatting commands (from the phpWiki application):</p>

  <blockquote>
  <table border='1' bgcolor='ghostwhite'>
  <tr><th>Entered Text</th><th>Resulting Output</th></tr>
  <tr><td><tt>Task#99</tt></td><td><a href='http://www.aps.anl.gov/asd/controls/tman/tasks.php?id=99'>Task#99</a></td></tr>
  <tr><td><tt>Ctllog#99</tt></td><td><a href='http://www.aps.anl.gov/asd/controls/newlog/entries.php?id=99'>Ctllog#99</a></td></tr>
  <tr><td><tt>IDlog#99</tt></td><td><a href='http://www.aps.anl.gov/asd/controls/idlog/entries.php?id=99'>IDlog#99</a></td></tr>
  <tr><td><tt>RMD#99</tt></td><td><a href='http://www.aps4.anl.gov/cgi-bin/ops/rmd-cgi/rmd_view.cgi?mode=manager&rmdNo=99'>RMD#99</a></td></tr>
  <tr><td><tt>RSP#99</tt></td><td><a href='http://www.aps4.anl.gov/cgi-bin/ops/rmd-cgi/response_view.cgi?mode=manager&responseNo=99'>RSP#99</a></td></tr>
  <tr><td><tt>http://www.aps.anl.gov/</tt></td>
  <td><a href='http://www.aps.anl.gov/'>http://www.aps.anl.gov/</a></td></tr>
  <tr><td><tt>__bold__</tt></td><td><b>bold</b></td></td></tr>
  <tr><td><tt>''italic''</tt></td><td><i>italic</i></td></tr>
  <tr><td><tt>__''bold italic''__</tt></td><td><i><b>bold italic</b></i></td></tr>
  <tr><td><tt>----</tt></td><td><hr></td></tr>
  <tr><td><tt>!!! Major Heading</tt></td><td><h1>Major Heading</h1></td></tr>
  <tr><td><tt>!! Medium Heading</tt></td><td><h2>Medium Heading</h2></td></tr>
  <tr><td><tt>! Heading</tt></td><td><h4>Heading</h4></td></tr>
  <tr><td><tt>Adjacent lines combine<br>into a single paragraph.</tt></td>
  <td><p>Adjacent lines combine into a single paragraph.</p></td></tr>
  <tr><td><tt>Line%%%break</tt></td><td><p>Line<br>break</p></td></tr>
  <tr><td><tt>Leave a blank line.<br>&nbsp;<br>Separate paragraphs.</tt></td>
  <td><p>Leave a blank line.</p><p>Separate paragraphs.</p></td></tr>
  <tr><td><tt>&nbsp;Start lines with a<br>&nbsp;space to maintain the<br>
  &nbsp;typed formatting.</tt></td><td><tt>&nbsp;Start lines with a<br>
  &nbsp;space to maintain the<br>&nbsp;typed formatting.</pre></td></tr>
  </table>
  </blockquote>
  <?php

}

?>
