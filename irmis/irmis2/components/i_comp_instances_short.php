<div class="searchResults">
  <table width="100%"  border="1" cellspacing="0" cellpadding="2">
<?php
    // null list in session implies that result size was exceeded


   $ctEntity = $_SESSION['selectedComponentType'];
   $ctList = $ctEntity->getComponentList();

    echo '<a name="top"></a>';

	echo '<tr>';
	echo '<th colspan="9"class=value>"'.$ctList->length().'" Component\'s Installed';
	   if ($ctList->length() != 0)
   {
      echo '<font class="valueright">&nbsp;&nbsp;&nbsp;&nbsp;<a class="hyper" href="#report">Report Tools</a></th>';
   }
	echo '</tr>';
	echo '<tr>';
    echo '<th nowrap>Component Type</th>';
	echo '<th>Ports</th>';
	echo '<th nowrap>Name</th>';
	echo '<th nowrap>Locator</th>';
	echo '<th nowrap>Owner</th>';
	echo '<th>IOC Parent</th>';
	echo '<th>Housing Parent</th>';
	echo '<th>Room Parent</th>';
	echo '<th>Building Parent</th>';
    echo '</tr>';

   if ($ctList == null) {
      echo '<tr><td class="warning bold" colspan=6>Search produced too many results to display.<br>';
      echo 'Limit is 5000. Try using the Additional<br>';
      echo 'Search Terms to constrain your search.</td></tr>';
   }

   else if ($ctList->length() == 0) {
      echo '<tr><td class="warning bold" colspan=9>No Component\'s found: please try another search.</td></tr>';
   }

   else {
      $ctArray = $ctList->getArray();

      foreach ($ctArray as $ctList) {
       echo '<tr>';
         //COMPONENT TYPE
		   if ($ctList->getImage()) {
				 $image_array=$ctList->getImage();
				 $images=explode( ";", $image_array);
				 echo '<td nowrap="nowrap" class=primary>'.$ctEntity->getctName().' (<acronym title="Component ID">'.$ctList->getID().'</acronym>)&nbsp;&nbsp;';
				 foreach ($images as $pics) {
					 echo '<a class="imagepopup" href="'.$pics.'" target="_blank"><img src="../common/images/bullet_picture.png" class="middle" width="16" height="16" />
					 <span><img src="'.$pics.'"></a>';
				 }
				 echo '</td>';
			 } else {
				 echo '<td nowrap="nowrap" class=primary>'.$ctEntity->getctName().' (<acronym title="Component ID">'.$ctList->getID().'</acronym>)</td>';
			 }
		   
		   
		 //PORTS
		   echo '<td class=resulttext>';
		   echo '<a class="hyper" href="../cable/action_ports_search.php?ID='.$ctList->getID().'&ct='.$ctEntity->getctName().'&room='.$ctList->getcomParentRoom().'&rack='.$ctList->getcomParentRack().'&ioc='.$ctList->                 getcomParentioc().'&iName='.$ctList->getInstanceName().'">Ports</a></td>';

         //NAME
		   if ($ctList->getInstanceName()) {
			  echo '<td class=resulttext>';
			  echo $ctList->getInstanceName();
		   } else {
			  echo '<td>&nbsp;</td>';
		   }
		   
		   
		 //LOGICAL DESCRIPTION  
		   $logdesc = $ctList->getlogicalDesc();
		   if ($logdesc) {
		   echo '<td class=resulttext>';
		   echo $logdesc;
		   echo '</td>';
		   } else {
			   echo '<td class=resulttext>&nbsp;</td>';
		   }
		   
		 //GROUP
		 $group = $ctList->getGroup();
		 if ($group == "Unknown") {
			 echo '<td class=dim>';
		     echo $group;
		 } else if ($group == "No Group"){
		     echo '<td class=dim>';
		     echo $group;
		 } else {
		     echo '<td class=resulttext>';
		     echo $group;
		 }

         //IOC PARENT
		   if ($ctList->getcomParentIOC()) {
			  echo '</td><td class="resulttext">';
			  echo '<a class="hyper" href="../ioc/action_ioc_search.php?iocName='.$ctList->getcomParentIOC().'">'.$ctList->getcomParentIOC().'</a>';
           } else {
		      echo '<td>&nbsp;</td>';
		   }

         //RACK PARENT
		   if ($ctList->getcomParentRack()) {
		    echo '</td><td class="resulttext">';
			echo $ctList->getcomParentRack();
		   } else {
			   echo '<td>&nbsp;</td>';
		   }
			
         //ROOM PARENT
		   if ($ctList->getcomParentRoom()) {
			echo '</td><td class="resulttext">';
			echo $ctList->getcomParentRoom();
		   } else {
			   echo '<td>&nbsp;</td>';
		   }

         //BUILDING PARENT
		   if ($ctList->getcomParentBldg()) {
			echo '</td><td class="resulttext">';
			echo $ctList->getcomParentBldg();
		   } else {
			   echo '<td>&nbsp;';
		   }

			echo '</td></tr>';
		}

      echo '</table><table width="100%" border="1" cellspacing="0" cellpadding="2"><tr><a name="report"></a>';
      include('../report/report_startform.php');
      include('../report/report_submit_comp_instances_short.php');
   }
   echo '</tr></table>';



?>