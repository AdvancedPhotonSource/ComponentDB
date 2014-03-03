<div class="searchResults">
<?php
   // null list in session implies that result size was exceeded
	$serverList = $_SESSION['serverList'];
	echo '<a name="top"></a>';


   echo '<table width="100%" border="1" cellspacing="0" cellpadding="2"><tr>';
   echo '<th colspan="5" class="value">'.$serverList->length().' Servers Found';
   if ($serverList->length() != 0)
   {
      echo '<font class="valueright">&nbsp;&nbsp;&nbsp;&nbsp;<a class="hyper" href="#report">Report Tools</a></th>';
   }
   echo '<tr>';
   echo '<th nowrap>Server Name</th>';
   echo '<th>Location</th>';
   echo '<th>Description</th>';
   echo '<th>Operating System</th>';
   echo '<th>Cognizant</th>';
   echo '</tr>';
   if ($serverList == null)
   {
      echo '<tr><td class="warning bold" colspan="5">WARNING: Search produced invalid results.</tr>';

   }
   else if ($serverList->length() == 0)
   {
      echo '<tr><td class="warning bold" colspan="5">No servers found: please try another search.</td></tr>';

   }
   else
   {
      $serverEntities = $serverList->getArray();
      foreach ($serverEntities as $serverEntity)
      {
		 $op_sys = "";
		 $server_name = "";
		 $cognizant = "";
		 $server_description = "";
		 $location = "";
		 $component_id = 0;
		 $server_room = "";
		 $server_rack = "";

      	 $op_sys = $serverEntity->getOperatingSystem();

      	 $server_name = $serverEntity->getServerName();

      	 $cognizantFirstName = $serverEntity->getCognizantFirstName();
      	 $cognizantLastName = $serverEntity->getCognizantLastName();
      	 $cognizant = $cognizantFirstName." ".$cognizantLastName;
      	 $server_description = $serverEntity->getServerDescription();
      	 $component_id = $serverEntity->getComponentID();

		 if ($component_id > 0) {

		 	$server_room = $serverEntity->getServerRoom($conn,$component_id);
		 	$server_rack = $serverEntity->getServerRack($conn,$component_id);

		 	$location = $server_room;

		 	if ($server_rack != "") {
		 		$location = $location."; ".$server_rack;
		 	}
		 }

         	if ($server_name != ""){
         		echo '<tr>';
         		echo '<td class="primary">'.$server_name.'</td>';

         		if ($location != "") {
					echo '<td class="results">'.$location.'</td>';
				} else {
					echo '<td>&nbsp;</td>';
				}

				if ($server_description != "") {
					echo '<td class="results">'.$server_description.'</td>';
				} else {
					echo '<td>&nbsp;</td>';
				}

				if ($op_sys != "") {
					echo '<td class="results" nowrap="nowrap">'.$op_sys.'</td>';
				} else {
					echo '<td>&nbsp;</td>';
				}

				if ($cognizant != "") {
			  		echo '<td class="results" nowrap>'.$cognizant.'</td>';
				} else {
			  		echo '<td>&nbsp;</td>';
				}
				echo '</tr>';
			}
	}

   echo '</table><table width="100%"  border="1" cellspacing="0" cellpadding="2"><tr>';

   echo '<a name="report"></a>';

   include_once('../report/report_startform.php');
   include_once('../report/report_submit_server.php');

   echo '</tr></table>';
   }
?>

</div>