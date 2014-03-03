<div class="searchResults">
   <form action="action_edit_form.php" method="POST">
   <table width="100%"  border="1" cellspacing="0" cellpadding="2">
<?php
   $plcName = $_GET['plcName'];
   $iocName = $_GET['iocName'];
   $plcVerPVName = $_GET['plcVerPVName'];
   $ref_pv = $plcVerPVName;
   $cID = $_GET['cID'];

  // This is the plc description search
   $plcDescr = new PlcDesc();
   logEntry('debug',"Performing plc description search");
   if (!$plcDescr->loadFromDB($conn_2, $cID)) {
     include('demo_error.php');
     exit;
   }


//   $plcName = $plcDescr->getPlcName();
   $_SESSION['plcName']=$plcName;
   $_SESSION['iocName']=$iocName;
   $_SESSION['cID']=$cID;
   $_SESSION['ref_pv']=$ref_pv;

   if ($_SESSION['plcDescEntity'])
     unset($_SESSION['plcDescEntity']);
   if ($_SESSION['plcDescr'])
     unset($_SESSION['plcDescr']);

   define('ERR_BOOTLOG',           1);
   define('ERR_PV_NOT_FOUND',      2);
   define('ERR_PLCNAME_NOT_FOUND', 3);
   define('ERR_LINE_NOT_FOUND',    4);
   define('ERR_CA_CONNECT',        5);

   $errList = array (  ' ',
                       'Could not open iocBootLog with iocBootLogHandle',   // $ref_pv
		       'PV not found in PLC line from st.cmd file',         // $ref_pv
                       'Found no line starting with PLC= in st.cmd file',   // $ref_pv
                       'PLC line not available in st.cmd file',             // $ref_pv
                       'Channel access failed to connect to pv'             // $codeVerStat
		    );


   // PLC data to acquire - initial values

   //$ref_pv = 'Not Available';

   $hwLinkStat = 'Not Available';
   $codeVerStat = 'Not Available';
   $epicsDataStat = 'Not Available';

   if ($ref_pv == null || $ref_pv == ""){
      $err = ERR_PV_NOT_FOUND;
   }
   else {
   	  $err = 0;
   }

   // If pv found, do a caget of code version

   if (!$err) {
       $ca_request = '/bin/sh my_caget.sh '.$ref_pv.'.VAL';
       exec($ca_request, $codeVer, $ca_err);
       if (!$ca_err){
           // if integer, add trailer zero
           if (!strstr($codeVer[0],"."))
               $codeVerStat = $codeVer[0].'.0';
       }
       else $err=ERR_CA_CONNECT;
   }

   switch ($err) {

       case ERR_BOOTLOG:
       case ERR_PV_NOT_FOUND:
       case ERR_PLCNAME_NOT_FOUND:
       case ERR_LINE_NOT_FOUND:
            $ref_pv = $errList[$err];
	    break;

       case ERR_CA_CONNECT:
            $codeVerStat = $errList[$err];
	    break;

       default:
            break;
   }

   $plcDescStr= $plcDescr->getPlcDesc();
   if ($plcDescStr)
     $_SESSION['plcDescr'] = $plcDescStr;
   $_SESSION['plcDescEntity'] = $plcDescr;
   // else: insert new entry in descr table - done while opening edit window

   // This is the plc record search
   $plcLnk = new PlcLink();
   logEntry('debug',"Performing plc link search");
   if (!$plcLnk->loadFromDB($conn_2, $plcName)){
     include('demo_error.php');
     exit;
   }
   $hwLinkStat = $plcLnk->getPlcLink();

// REMOVED RECORD REPORT TEMPORARILY
//   $plcRecList = new PLCRecList();
//   logEntry('debug',"Performing plc record search");
//   if ($hwLinkStat) {
//       if (!$plcRecList->loadFromDB($conn_2, $iocName, $hwLinkStat)) {
//           include('demo_error.php');
//           exit;
//       }
//       logEntry('debug',"Search gave ".$plcRecList->length()." results.");
//  }


   // Beginning display code :
   //SB$ICMSquery = "https://icmsdocs.aps.anl.gov/new_docs/idcplg?IdcService=GET_SEARCH_RESULTS_FORCELOGIN&QueryText=%28+%28+%28xComments+%3Csubstring%3E+%60".$plcName."%60%29+%29+and+not+dDocType+%3Cmatches%3E+%27CAD_Dependency%27+%29+and++not+dDocType+%3Cmatches%3E+%27Model_Drawing%27&ftx=&AdvSearch=True&search_docs_cb=x&ResultCount=20&SortField=SCORE&SortOrder=Desc";
	$ICMSquery = "xComments <substring> `$plcName` <and> <not> dDocType <matches> `CAD_Dependency` <and> <not> dDocType <matches> `Model_Drawing`";
	$ICMSqueryDBUG = "xComments &lt;substring&gt; `$plcName` and not dDocType &lt;matches&gt; 'CAD_Dependency' and not dDocType &lt;matches&gt; 'Model_Drawing'";

   echo '<tr>';
   echo '<th colspan="3" class="center">PLC General Information</th>';
   echo '</tr>';
   echo '<tr>';
   echo '<th>PLC Name</th><th colspan="2" nowrap class="value">'.$plcName.'</th>';
   echo '</tr>';
   echo '<th nowrap class="primary">IOC</th><td colspan="2" class="results">'.$iocName.'</td>';
   echo '</tr>';
   echo '</tr>';
   echo '<th nowrap class="primary">Reference PV</th><td colspan="2" class="results">'.$ref_pv.'</td>';
   echo '</tr>';
   echo '<tr>';
   echo '<th nowrap class="primary">Hardware link</th><td colspan="2" class="results">'.$hwLinkStat.'</td>';
   echo '</tr>';
   echo '<tr>';
   echo '<th nowrap class="primary">PLC code version </th><td colspan="2"  class="results">'.$codeVerStat.'</td>';
   echo '</tr>';

   echo '<tr>';
      echo '<th nowrap class="primary">Description<td colspan="2" class="results"><div>'.$plcDescr->getPlcDesc().'</div><div align = "right"><input type ="submit" name =
      "text" value = "Edit"></div></td></th>';
   echo '</tr>';

	echo '</table>';
	echo '<table width="100%"  border="1" cellspacing="0" cellpadding="2">';

   // $wsdl = "https://icmsdocs.aps.anl.gov/new_docs/groups/secure/wsdl/custom/Search.wsdl";
   $wsdl = "https://icmsdocs.aps.anl.gov/docs/idcplg?IdcService=DISPLAY_URL&dDocName=SEARCH";

   $client = "";

   $client = new SoapClient($wsdl, array('login' => $icms_user_name,'password' => $icms_user_passwd));

   if ($client == "") {
      include('soap_failure.php'); // redirects user to soap failure page and exits
      exit;
   }

   //echo "<br />ICMS search string is: $ICMSqueryDBUG<br />";

   // resultCount is 1 million because this is a magic number for "more aois than we will ever have"

   // $param = array('queryText' => $ICMSquery, 'resultCount' => 1000000);

   $param = array('queryText' => $ICMSquery, 'sortField' => 'dInDate', 'sortOrder' => 'desc', 'resultCount' => 1000000);

   $retVal = $client->AdvancedSearch($param);

	echo '<tr><th class="center" colspan="5">ICMS - PLC Code and related documents</td></tr>';
   	echo '<tr><td class="value">Document Type</td><td class="value">Title</td><td class="value">URI</td><td class="value">Author</td><td class="value">Check-In Date</td></tr>';

	$search_results = $retVal->AdvancedSearchResult->SearchResults;

	if (isset($search_results)) {

		foreach ($search_results as $key => $value) {

			if ($done)
				break;

			if (gettype($key) != 'integer') { // then we have one and only one result

				$dDocType = $search_results->dDocType;
				$dDocTitle = $search_results->dDocTitle;
				$dDocName = "<a href=\"https://icmsdocs.aps.anl.gov/new_docs/idcplg?IdcService=DISPLAY_URL&dDocName={$search_results->dDocName}\">{$search_results->dDocName}</a>";
				$dInDate = $search_results->dInDate;

				//$dDocAuthor = $search_results->dDocAuthor; // apparently dDocAuthor is not the author - xDocAuthor is!
				// property #28 happens to be the xDocAuthor
				$xDocAuthor = $search_results->CustomDocMetaData->property[28]->value;
				if ($xDocAuthor == '')
					$xDocAuthor = '&nbsp';

				$done = true;

			}

			else {

				$dDocType = $value->dDocType;
				$dDocTitle = $value->dDocTitle;
				$dDocName = "<a href=\"https://icmsdocs.aps.anl.gov/new_docs/idcplg?IdcService=DISPLAY_URL&dDocName={$value->dDocName}\">{$value->dDocName}</a>";
				$dInDate = $value->dInDate;

				//$dDocAuthor = $value->dDocAuthor; // apparently dDocAuthor is not the author - xDocAuthor is!
				// property #28 happens to be the xDocAuthor
				$xDocAuthor = $value->CustomDocMetaData->property[28]->value;
				if ($xDocAuthor == '')
					$xDocAuthor = '&nbsp';

			}

			echo "<tr>";
			echo '<td align="center">'.$dDocType.'</td><td>'.$dDocTitle.'</td><td>'.$dDocName.'</td><td>'.$xDocAuthor.'</td><td>'.$dInDate.'</td>';
			echo "</tr>\n";

		}

	} else
		echo '<tr><td colspan="5" align="center">No ICMS documents found.</td></tr>';

	echo '</table>';


// REMOVED RECORD REPORT TEMPORARILY
//   echo '<tr>';

   // run this if no error
//   echo '<th colspan="3" class="value">PLC to Epics Data: '.$plcRecList->length().' PVs Found </th>';
//   echo '</tr>';
//   echo '<th nowrap>PV Name</th>';
//   echo '<th>Type</th>';
//   echo '<th>HW Link</th>';
//   echo '</tr>';
//   if ($plcRecList->length() == 0) {
//        echo '<tr><td class="warning bold" colspan=8>No PVs\'s found: please try another search.</td></tr>';
//   } else {
//        $plcRecEntities = $plcRecList->getArray();
//        foreach ($plcRecEntities as $plcRecEntity) {
//            echo '<tr>';
//            echo '<td width="30%">'.$plcRecEntity->getRecName().'</td>';
//            echo '<td width="20%">'.$plcRecEntity->getRecType().'</td>';
//	    echo '<td width="50%">'.$plcRecEntity->getLnkVal().'</td>';
//	}
//   }
//   echo '</tr>';

 ?>
