<?php

/*
 * Written by Dawn Clemons
 * Specialized step in creating a printable report. Specific information
 * that applies only to the IOC PHP viewer is displayed in a look and feel
 * similar to the viewer's.
 */

/*
   echo '<div class="sectionTable"><table width="100%" border="1" cellspacing="0" cellpadding="2">';
   echo '<tr><th>IOC Name</th><th>System</th><th>Location</th><th>Cognizant Developer</th><th>Cognizant Technician</th></tr>';

   //Get the data
   $iocList = $_SESSION['iocList'];
   $iocEntities = $iocList->getArray();

   foreach($iocEntities as $iocEntity)
   {
      //Display the data
      echo '<tr>';
      echo '<td>'.$iocEntity->getIocName().'</td>';
      echo '<td>'.$iocEntity->getSystem().'</td>';
      echo '<td>'.$iocEntity->getLocation().'</td>';
      echo '<td>'.$iocEntity->getCogDeveloper().'</td>';
      echo '<td>'.$iocEntity->getCogtech().'</td>';
      echo '</tr>';
   }
   echo '</table></div><br>';
?>
*/

	  $iocContentsList = $_SESSION['iocContentsList'];
	  echo '<div class="sectionTable"><table width="100%"  border="1" cellspacing="0" cellpadding="2">';
  
	  $iocContentsEntities = $iocContentsList->getArray();
      foreach ($iocContentsEntities as $iocContentsEntity) {
	  
      if ($iocContentsEntity->getLogicalDesc()=='0') {
		  $ctid0 = $iocContentsEntity->getComponentID();
	      $slot0 = $iocContentsEntity->getComponentTypeName();
		  $id0 = $iocContentsEntity->getID();
	  }
      if ($iocContentsEntity->getLogicalDesc()=='1') {
		  $ctid1 = $iocContentsEntity->getComponentID();
	      $slot1 = $iocContentsEntity->getComponentTypeName();
		  $id1 = $iocContentsEntity->getID();
	  }
	  if (($iocContentsEntity->getLogicalDesc()=='R1')||($iocContentsEntity->getLogicalDesc()=='1R')) {
		  $ctid1r = $iocContentsEntity->getComponentID();
	      $slot1r = $iocContentsEntity->getComponentTypeName();
		  $id1r = $iocContentsEntity->getID();
	  }
	  if ($iocContentsEntity->getLogicalDesc()=='2') {
		  $ctid2 = $iocContentsEntity->getComponentID();
	      $slot2 = $iocContentsEntity->getComponentTypeName();
		  $id2 = $iocContentsEntity->getID();
	  }
	  if (($iocContentsEntity->getLogicalDesc()=='R2')||($iocContentsEntity->getLogicalDesc()=='2R')) {
		  $ctid2r = $iocContentsEntity->getComponentID();
	      $slot2r = $iocContentsEntity->getComponentTypeName();
		  $id2r = $iocContentsEntity->getID();
	  }
	  if ($iocContentsEntity->getLogicalDesc()=='3') {
		  $ctid3 = $iocContentsEntity->getComponentID();
	      $slot3 = $iocContentsEntity->getComponentTypeName();
		  $id3 = $iocContentsEntity->getID();
	  }
	  if (($iocContentsEntity->getLogicalDesc()=='R3')||($iocContentsEntity->getLogicalDesc()=='3R')) {
		  $ctid3r = $iocContentsEntity->getComponentID();
	      $slot3r = $iocContentsEntity->getComponentTypeName();
		  $id3r = $iocContentsEntity->getID();
	  }
	  if ($iocContentsEntity->getLogicalDesc()=='4') {
		  $ctid4 = $iocContentsEntity->getComponentID();
	      $slot4 = $iocContentsEntity->getComponentTypeName();
		  $id4 = $iocContentsEntity->getID();
	  }
	  if (($iocContentsEntity->getLogicalDesc()=='R4')||($iocContentsEntity->getLogicalDesc()=='4R')) {
		  $ctid4r = $iocContentsEntity->getComponentID();
	      $slot4r = $iocContentsEntity->getComponentTypeName();
		  $id4r = $iocContentsEntity->getID();
	  }
	  if ($iocContentsEntity->getLogicalDesc()=='5') {
		  $ctid5 = $iocContentsEntity->getComponentID();
	      $slot5 = $iocContentsEntity->getComponentTypeName();
		  $id5 = $iocContentsEntity->getID();
	  }
	  if (($iocContentsEntity->getLogicalDesc()=='R5')||($iocContentsEntity->getLogicalDesc()=='5R')) {
		  $ctid5r = $iocContentsEntity->getComponentID();
	      $slot5r = $iocContentsEntity->getComponentTypeName();
		  $id5r = $iocContentsEntity->getID();
	  }
	  if ($iocContentsEntity->getLogicalDesc()=='6') {
		  $ctid6 = $iocContentsEntity->getComponentID();
	      $slot6 = $iocContentsEntity->getComponentTypeName();
		  $id6 = $iocContentsEntity->getID();
	  }
	  if (($iocContentsEntity->getLogicalDesc()=='R6')||($iocContentsEntity->getLogicalDesc()=='6R')) {
		  $ctid6r = $iocContentsEntity->getComponentID();
	      $slot6r = $iocContentsEntity->getComponentTypeName();
		  $id6r = $iocContentsEntity->getID();
	  }
	  if ($iocContentsEntity->getLogicalDesc()=='7') {
		  $ctid7 = $iocContentsEntity->getComponentID();
	      $slot7 = $iocContentsEntity->getComponentTypeName();
		  $id7 = $iocContentsEntity->getID();
	  }
	  if (($iocContentsEntity->getLogicalDesc()=='R7')||($iocContentsEntity->getLogicalDesc()=='7R')) {
		  $ctid7r = $iocContentsEntity->getComponentID();
	      $slot7r = $iocContentsEntity->getComponentTypeName();
		  $id7r = $iocContentsEntity->getID();
	  }
	  if ($iocContentsEntity->getLogicalDesc()=='8') {
		  $ctid8 = $iocContentsEntity->getComponentID();
	      $slot8 = $iocContentsEntity->getComponentTypeName();
		  $id8 = $iocContentsEntity->getID();
	  }
	  if (($iocContentsEntity->getLogicalDesc()=='R8')||($iocContentsEntity->getLogicalDesc()=='8R')) {
		  $ctid8r = $iocContentsEntity->getComponentID();
	      $slot8r = $iocContentsEntity->getComponentTypeName();
		  $id8r = $iocContentsEntity->getID();
	  }
	  if ($iocContentsEntity->getLogicalDesc()=='9') {
		  $ctid9 = $iocContentsEntity->getComponentID();
	      $slot9 = $iocContentsEntity->getComponentTypeName();
		  $id9 = $iocContentsEntity->getID();
	  }
	  if (($iocContentsEntity->getLogicalDesc()=='R9')||($iocContentsEntity->getLogicalDesc()=='9R')) {
		  $ctid9r = $iocContentsEntity->getComponentID();
	      $slot9r = $iocContentsEntity->getComponentTypeName();
		  $id9r = $iocContentsEntity->getID();
	  }
	  if ($iocContentsEntity->getLogicalDesc()=='10') {
		  $ctid10 = $iocContentsEntity->getComponentID();
	      $slot10 = $iocContentsEntity->getComponentTypeName();
		  $id10 = $iocContentsEntity->getID();
	  }
	  if (($iocContentsEntity->getLogicalDesc()=='R10')||($iocContentsEntity->getLogicalDesc()=='10R')) {
		  $ctid10r = $iocContentsEntity->getComponentID();
	      $slot10r = $iocContentsEntity->getComponentTypeName();
		  $id10r = $iocContentsEntity->getID();
	  }
	  if ($iocContentsEntity->getLogicalDesc()=='11') {
		  $ctid11 = $iocContentsEntity->getComponentID();
	      $slot11 = $iocContentsEntity->getComponentTypeName();
		  $id11 = $iocContentsEntity->getID();
	  }
	  if (($iocContentsEntity->getLogicalDesc()=='R11')||($iocContentsEntity->getLogicalDesc()=='11R')) {
		  $ctid11r = $iocContentsEntity->getComponentID();
	      $slot11r = $iocContentsEntity->getComponentTypeName();
		  $id11r = $iocContentsEntity->getID();
	  }
	  if ($iocContentsEntity->getLogicalDesc()=='12') {
		  $ctid12 = $iocContentsEntity->getComponentID();
	      $slot12 = $iocContentsEntity->getComponentTypeName();
		  $id12 = $iocContentsEntity->getID();
	  }
	  if (($iocContentsEntity->getLogicalDesc()=='R12')||($iocContentsEntity->getLogicalDesc()=='12R')) {
		  $ctid12r = $iocContentsEntity->getComponentID();
	      $slot12r = $iocContentsEntity->getComponentTypeName();
		  $id12r = $iocContentsEntity->getID();
	  }
	  if ($iocContentsEntity->getLogicalDesc()=='13') {
		  $ctid13 = $iocContentsEntity->getComponentID();
	      $slot13 = $iocContentsEntity->getComponentTypeName();
		  $id13 = $iocContentsEntity->getID();
	  }
	  if (($iocContentsEntity->getLogicalDesc()=='R13')||($iocContentsEntity->getLogicalDesc()=='13R')) {
		  $ctid13r = $iocContentsEntity->getComponentID();
	      $slot13r = $iocContentsEntity->getComponentTypeName();
		  $id13r = $iocContentsEntity->getID();
	  }
	  if ($iocContentsEntity->getLogicalDesc()=='14') {
		  $ctid14 = $iocContentsEntity->getComponentID();
	      $slot14 = $iocContentsEntity->getComponentTypeName();
		  $id14 = $iocContentsEntity->getID();
	  }
	  if (($iocContentsEntity->getLogicalDesc()=='R14')||($iocContentsEntity->getLogicalDesc()=='14R')) {
		  $ctid14r = $iocContentsEntity->getComponentID();
	      $slot14r = $iocContentsEntity->getComponentTypeName();
		  $id14r = $iocContentsEntity->getID();
	  }
	  if ($iocContentsEntity->getLogicalDesc()=='15') {
		  $ctid15 = $iocContentsEntity->getComponentID();
	      $slot15 = $iocContentsEntity->getComponentTypeName();
		  $id15 = $iocContentsEntity->getID();
	  }
	  if (($iocContentsEntity->getLogicalDesc()=='R15')||($iocContentsEntity->getLogicalDesc()=='15R')) {
		  $ctid15r = $iocContentsEntity->getComponentID();
	      $slot15r = $iocContentsEntity->getComponentTypeName();
		  $id15r = $iocContentsEntity->getID();
	  }
	  if ($iocContentsEntity->getLogicalDesc()=='16') {
		  $ctid16 = $iocContentsEntity->getComponentID();
	      $slot16 = $iocContentsEntity->getComponentTypeName();
		  $id16 = $iocContentsEntity->getID();
	  }
	  if (($iocContentsEntity->getLogicalDesc()=='R16')||($iocContentsEntity->getLogicalDesc()=='16R')) {
		  $ctid16r = $iocContentsEntity->getComponentID();
	      $slot16r = $iocContentsEntity->getComponentTypeName();
		  $id16r = $iocContentsEntity->getID();
	  }
	  if ($iocContentsEntity->getLogicalDesc()=='17') {
		  $ctid17 = $iocContentsEntity->getComponentID();
	      $slot17 = $iocContentsEntity->getComponentTypeName();
		  $id17 = $iocContentsEntity->getID();
	  }
	  if (($iocContentsEntity->getLogicalDesc()=='R17')||($iocContentsEntity->getLogicalDesc()=='17R')) {
		  $ctid17r = $iocContentsEntity->getComponentID();
	      $slot17r = $iocContentsEntity->getComponentTypeName();
		  $id17r = $iocContentsEntity->getID();
	  }
	  if ($iocContentsEntity->getLogicalDesc()=='18') {
		  $ctid18 = $iocContentsEntity->getComponentID();
	      $slot18 = $iocContentsEntity->getComponentTypeName();
		  $id18 = $iocContentsEntity->getID();
	  }
	  if (($iocContentsEntity->getLogicalDesc()=='R18')||($iocContentsEntity->getLogicalDesc()=='18R')) {
		  $ctid18r = $iocContentsEntity->getComponentID();
	      $slot18r = $iocContentsEntity->getComponentTypeName();
		  $id18r = $iocContentsEntity->getID();
	  }
	  if ($iocContentsEntity->getLogicalDesc()=='19') {
		  $ctid19 = $iocContentsEntity->getComponentID();
	      $slot19 = $iocContentsEntity->getComponentTypeName();
		  $id19 = $iocContentsEntity->getID();
	  }
	  if (($iocContentsEntity->getLogicalDesc()=='R19')||($iocContentsEntity->getLogicalDesc()=='19R')) {
		  $ctid19r = $iocContentsEntity->getComponentID();
	      $slot19r = $iocContentsEntity->getComponentTypeName();
		  $id19r = $iocContentsEntity->getID();
	  }
	  if ($iocContentsEntity->getLogicalDesc()=='20') {
		  $ctid20 = $iocContentsEntity->getComponentID();
	      $slot20 = $iocContentsEntity->getComponentTypeName();
		  $id20 = $iocContentsEntity->getID();
	  }
	  if (($iocContentsEntity->getLogicalDesc()=='R20')||($iocContentsEntity->getLogicalDesc()=='20R')) {
		  $ctid20r = $iocContentsEntity->getComponentID();
	      $slot20r = $iocContentsEntity->getComponentTypeName();
		  $id20r = $iocContentsEntity->getID();
	  }
	  if ($iocContentsEntity->getLogicalDesc()=='21') {
		  $ctid21 = $iocContentsEntity->getComponentID();
	      $slot21 = $iocContentsEntity->getComponentTypeName();
		  $id21 = $iocContentsEntity->getID();
	  }
	  if (($iocContentsEntity->getLogicalDesc()=='R21')||($iocContentsEntity->getLogicalDesc()=='21R')) {
		  $ctid21r = $iocContentsEntity->getComponentID();
	      $slot21r = $iocContentsEntity->getComponentTypeName();
		  $id21r = $iocContentsEntity->getID();
	  }
    }
	  
	  
	  $Embedded = "(Embedded IOC)";
	  $Softioc = "Soft IOC on Server";
	  if ($_SESSION['prevIF'] == Embedded) {
	    $clem = $Embedded;
	  } elseif ($_SESSION['prevIF'] == Soft_IOC) {
	    $clem = $Softioc;
	  }
	  echo '<tr>';
      echo '<th colspan="3" class="value">'.$_SESSION['prevCompName'].'&nbsp;'.$clem.'&nbsp;'.$_SESSION['prevCompInstName'].'</th>';
	  echo '</tr>';
	  echo '<tr>';
      echo '<th colspan="3" nowrap width="100%" class="value">'.$_SESSION['iocName'].'&nbsp;('.$_SESSION['componentID'].')</th>';
	  echo '</tr>';
	  echo '<tr>';
	  echo '<th width="40%">Front</th><th width="20%">Slot</th><th width="40%">Rear</th>';
	  echo '</tr>';
	  
	  if ($id0) {
	    echo '<tr><td class="results">'.$slot0.'&nbsp;&nbsp;('.$ctid0.')</td><th align="center">Slot 0</th>';
	    echo '<td align="center" class="bold">&nbsp;</td></tr>';
	  }

	  if ($slot1) {
	  echo '<tr><td class="results">'.$slot1.'&nbsp;&nbsp;('.$ctid1.')</td><th align="center">Slot 1</th>';
	  } else {
	  echo '<td>&nbsp</td><th align="center">Slot 1</th>';
	  }
	  if ($slot1r) {
	  echo '<td class="results">'.$slot1r.'&nbsp;&nbsp;('.$ctid1r.')</td></tr>';
	  } else { 
	  echo '<td>&nbsp</td></tr>';
	  }
	  
	  if ($slot2) {
	  echo '<tr><td class="results">'.$slot2.'&nbsp;&nbsp;('.$ctid2.')</td><th align="center">Slot 2</th>';
	  } else {
	  echo '<td>&nbsp</td><th align="center">Slot 2</th>';
	  }
	  if ($slot2r) {
	  echo '</td><td class="results">'.$slot2r.'&nbsp;&nbsp;('.$ctid2r.')</td></tr>';
	  } else {
	  echo '<td>&nbsp</td></tr>';
	  }
	  
	  if ($slot3) {
	  echo '<tr><td class="results">'.$slot3.'&nbsp;&nbsp;('.$ctid3.')</td><th align="center">Slot 3</th>';
	  } else {
	  echo '<td>&nbsp</td><th align="center">Slot 3</th>';
	  }
	  if ($slot1r) {
	  echo '<td class="results">'.$slot3r.'&nbsp;&nbsp;('.$ctid3r.')</td></tr>';
	  } else {
	  echo '<td>&nbsp</td></tr>';
	  }
	  
	  if ($slot4) {
	  echo '<tr><td class="results">'.$slot4.'&nbsp;&nbsp;('.$ctid4.')</td><th align="center">Slot 4</th>';
	  } else {
	  echo '<td>&nbsp</td><th align="center">Slot 4</th>';
	  }
	  if ($slot4r) {
	  echo '<td class="results">'.$slot4r.'&nbsp;&nbsp;('.$ctid4r.')</td></tr>';
	  } else {
	  echo '<td>&nbsp</td></tr>';
	  }
	  
	  if ($slot5) {
	  echo '<tr><td class="results">'.$slot5.'&nbsp;&nbsp;('.$ctid5.')</td><th align="center">Slot 5</th>';
	  } else {
	  echo '<td>&nbsp</td><th align="center">Slot 5</th>';
	  }
	  if ($slot5r) {
	  echo '<td class="results">'.$slot5r.'&nbsp;&nbsp;('.$ctid5r.')</td></tr>';
	  } else {
	  echo '<td>&nbsp</td></tr>';
	  }
	  
	  if ($slot6) {
	  echo '<tr><td class="results">'.$slot6.'&nbsp;&nbsp;('.$ctid6.')</td><th align="center">Slot 6</th>';
	  } else {
	  echo '<td>&nbsp</td><th align="center">Slot 6</th>';
	  }
	  if ($slot6r) {
	  echo '<td class="results">'.$slot6r.'&nbsp;&nbsp;('.$ctid6r.')</td></tr>';
	  } else {
	  echo '<td>&nbsp</td></tr>';
	  }
	  
	  if ($slot7) {
	  echo '<tr><td class="results">'.$slot7.'&nbsp;&nbsp;('.$ctid7.')</td><th align="center">Slot 7</th>';
	  } else {
	  echo '<td>&nbsp</td><th align="center">Slot 7</th>';
	  }
	  if ($slot7r) {
	  echo '<td class="results">'.$slot7r.'&nbsp;&nbsp;('.$ctid7r.')</td></tr>';
	  } else {
	  echo '<td>&nbsp</td></tr>';
	  }
	  
	  if ($slot8) {
	  echo '<tr><td class="results">'.$slot8.'&nbsp;&nbsp;('.$ctid8.')</td><th align="center">Slot 8</th>';
	  } else {
	  echo '<td>&nbsp</td><th align="center">Slot 8</th>';
	  }
	  if ($slot8r) {
	  echo '<td class="results">'.$slot8r.'&nbsp;&nbsp;('.$ctid8r.')</td></tr>';
	  } else {
	  echo '<td>&nbsp</td></tr>';
	  }
	  
	  if ($slot9) {
	  echo '<tr><td class="results">'.$slot9.'&nbsp;&nbsp;('.$ctid9.')</td><th align="center">Slot 9</th>';
	  } else {
	  echo '<td>&nbsp</td><th align="center">Slot 9</th>';
	  }
	  if ($slot9r) {
	  echo '<td class="results">'.$slot9r.'&nbsp;&nbsp;('.$ctid9r.')</td></tr>';
	  } else {
	  echo '<td>&nbsp</td></tr>';
	  }
	  
	  if ($slot10) {
	  echo '<tr><td class="results">'.$slot10.'&nbsp;&nbsp;('.$ctid10.')</td><th align="center">Slot 10</th>';
	  } else {
	  echo '<td>&nbsp</td><th align="center">Slot 10</th>';
	  }
	  if ($slot10r) {
	  echo '<td class="results">'.$slot10r.'&nbsp;&nbsp;('.$ctid10r.')</td></tr>';
	  } else {
	  echo '<td>&nbsp</td></tr>';
	  }
	  
	  if ($slot11) {
	  echo '<tr><td class="results">'.$slot11.'&nbsp;&nbsp;('.$ctid11.')</td><th align="center">Slot 11</th>';
	  } else {
	  echo '<td>&nbsp</td><th align="center">Slot 11</th>';
	  }
	  if ($slot11r) {
	  echo '<td class="results">'.$slot11r.'&nbsp;&nbsp;('.$ctid11r.')</td></tr>';
	  } else {
	  echo '<td>&nbsp</td></tr>';
	  }
	  
	  if ($slot12) {
	  echo '<tr><td class="results">'.$slot12.'&nbsp;&nbsp;('.$ctid12.')</td><th align="center">Slot 12</th>';
	  } else {
	  echo '<td>&nbsp</td><th align="center">Slot 12</th>';
	  }
	  if ($slot12r) {
	  echo '<td class="results">'.$slot12r.'&nbsp;&nbsp;('.$ctid12r.')</td></tr>';
	  } else {
	  echo '<td>&nbsp</td></tr>';
	  }
	  
	  if (!$id0) {
	  if ($slot13) {
	  echo '<tr><td class="results">'.$slot13.'&nbsp;&nbsp;('.$ctid13.')</td><th align="center">Slot 13</th>';
	  } else {
	  echo '<td>&nbsp</td><th align="center">Slot 13</th>';
	  }
	  if ($slot13r) {
	  echo '<td class="results">'.$slot13r.'&nbsp;&nbsp;('.$ctid13r.')</td></tr>';
	  } else {
	  echo '<td>&nbsp</td></tr>';
	  }
	  
	  if ($slot14) {
	  echo '<tr><td class="results">'.$slot14.'&nbsp;&nbsp;('.$ctid14.')</td><th align="center">Slot 14</th>';
	  } else {
	  echo '<td>&nbsp</td><th align="center">Slot 14</th>';
	  }
	  if ($slot14r) {
	  echo '<td class="results">'.$slot14r.'&nbsp;&nbsp;('.$ctid14r.')</td></tr>';
	  } else {
	  echo '<td>&nbsp</td></tr>';
	  }
	  
	  if ($slot15) {
	  echo '<tr><td class="results">'.$slot15.'&nbsp;&nbsp;('.$ctid15.')</td><th align="center">Slot 15</th>';
	  } else {
	  echo '<td>&nbsp</td><th align="center">Slot 15</th>';
	  }
	  if ($slot15r) {
	  echo '<td class="results">'.$slot15r.'&nbsp;&nbsp;('.$ctid15r.')</td></tr>';
	  } else {
	  echo '<td>&nbsp</td></tr>';
	  }
	  
	  if ($slot16) {
	  echo '<tr><td class="results">'.$slot16.'&nbsp;&nbsp;('.$ctid16.')</td><th align="center">Slot 16</th>';
	  } else {
	  echo '<td>&nbsp</td><th align="center">Slot 16</th>';
	  }
	  if ($slot16r) {
	  echo '<td class="results">'.$slot16r.'&nbsp;&nbsp;('.$ctid16r.')</td></tr>';
	  } else {
	  echo '<td>&nbsp</td></tr>';
	  }
	  
	  if ($slot17) {
	  echo '<tr><td class="results">'.$slot17.'&nbsp;&nbsp;('.$ctid17.')</td><th align="center">Slot 17</th>';
	  } else {
	  echo '<td>&nbsp</td><th align="center">Slot 17</th>';
	  }
	  if ($slot17r) {
	  echo '<td class="results">'.$slot17r.'&nbsp;&nbsp;('.$ctid17r.')</td></tr>';
	  } else {
	  echo '<td>&nbsp</td></tr>';
	  }
	  
	  if ($slot18) {
	  echo '<tr><td class="results">'.$slot18.'&nbsp;&nbsp;('.$ctid18.')</td><th align="center">Slot 18</th>';
	  } else {
	  echo '<td>&nbsp</td><th align="center">Slot 18</th>';
	  }
	  if ($slot18r) {
	  echo '<td class="results">'.$slot18r.'&nbsp;&nbsp;('.$ctid18r.')</td></tr>';
	  } else {
	  echo '<td>&nbsp</td></tr>';
	  }
	  
	  if ($slot19) {
	  echo '<tr><td class="results">'.$slot19.'&nbsp;&nbsp;('.$ctid19.')</td><th align="center">Slot 19</th>';
	  } else {
	  echo '<td>&nbsp</td><th align="center">Slot 19</th>';
	  }
	  if ($slot19r) {
	  echo '<td class="results">'.$slot19r.'&nbsp;&nbsp;('.$ctid19r.')</td></tr>';
	  } else {
	  echo '<td>&nbsp</td></tr>';
	  }
	  
	  if ($slot20) {
	  echo '<tr><td class="results">'.$slot20.'&nbsp;&nbsp;('.$ctid20.')</td><th align="center">Slot 20</th>';
	  } else {
	  echo '<td>&nbsp</td><th align="center">Slot 20</th>';
	  }
	  if ($slot20r) {
	  echo '<td class="results">'.$slot20r.'&nbsp;&nbsp;('.$ctid20r.')</td></tr>';
	  } else {
	  echo '<td>&nbsp</td></tr>';
	  }
	  
	  if ($slot21) {
	  echo '<tr><td class="results">'.$slot21.'&nbsp;&nbsp;('.$ctid21.')</td><th align="center">Slot 21</th>';
	  } else {
	  echo '<td>&nbsp</td><th align="center">Slot 21</th>';
	  }
	  if ($slot21r) {
	  echo '<td class="results">'.$slot21r.'&nbsp;&nbsp;('.$ctid21r.')</td></tr>';
	  } else {
	  echo '<td>&nbsp</td></tr>';
	  }
	  }
      echo '</table></div><br>';
?>
