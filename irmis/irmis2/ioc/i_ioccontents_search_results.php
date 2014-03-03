<div class="searchResults">
  <table width="100%"  border="1" cellspacing="0" cellpadding="2" rules="rows">
  
  <colgroup span="5" >
    <col span="2">
    <col class="slot">
    <col span="2">
  </colgroup>
      
<?php 
	$iocContentsList = $_SESSION['iocContentsList'];
	  
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

	// table header
	  $Embedded = "(Embedded IOC)";
	  $Softioc = "Soft IOC on Server";
	  if ($_SESSION['prevIF'] == Embedded) {
	    $clem = $Embedded;
	  } elseif ($_SESSION['prevIF'] == Soft_IOC) {
	    $clem = $Softioc;
	  }
	  
	  echo '<tr>';
      echo '<th colspan="5" class="value">'.$_SESSION['prevCompName'].'&nbsp;'.$clem.'&nbsp;'.$_SESSION['prevCompInstName'].'<br>'
      .$_SESSION['iocName'].'&nbsp;(<acronym title="Component ID">'.$_SESSION['componentID'].'</acronym>)&nbsp;in Room&nbsp;'.$_SESSION['room'].',&nbsp;'.$_SESSION['rack'].'</th>';
	  echo '</tr>';
	  echo '<tr>';
	  echo '<th width="40%" colspan=2>Front</th><th width="20%">Slot</th><th width="40%" colspan=2>Rear</th>';
	  echo '</tr>';
	  
	  
	// Slot 0
	  if ($id0) {
		
		echo '<tr><td class="resulttext"><a class="hyper" href="../components/action_comp_search.php?ctID='.$id0.'&amp;jmp=1"><acronym title="More information about a '.$slot0.'">'.$slot0.
		'</a>&nbsp;&nbsp;(<acronym title="Component ID">'.$ctid0.'</acronym>)&nbsp;&nbsp;</td><td align=center><a class="hyper" href="../cable/action_ports_search.php?ID='.$ctid0.'&ct='.$slot0.
		'&room='.$_SESSION['room'].'&rack='.$_SESSION['rack'].'&ioc='.$_SESSION['iocName'].'"><acronym title="Find the port information for this '
		 .$slot0.'">Ports</a>&nbsp;</td>';
		
	    echo '<td align="center" class="bold">Slot 0</td>';
		echo '<td>&nbsp;</td></tr>';
	  }
	  
	// Slot 1
	  if ($id1) {
		echo '<tr><td class="resulttext"><a class="hyper" href="../components/action_comp_search.php?ctID='.$id1.'&amp;jmp=1"><acronym title="More information about a '.$slot1.'">'.$slot1.
		'</a>&nbsp;&nbsp;(<acronym title="Component ID">'.$ctid1.'</acronym>)&nbsp;&nbsp;</td><td align=center><a class="hyper" href="../cable/action_ports_search.php?ID='.$ctid1.'&ct='.$slot1.
		'&room='.$_SESSION['room'].'&rack='.$_SESSION['rack'].'&ioc='.$_SESSION['iocName'].'"><acronym title="Find the port information for this '
		 .$slot1.'">Ports</a>&nbsp;</td>';
		
	  } else {
	    echo '<tr><td>&nbsp;</td><td>&nbsp;</td>';
	  }
	  echo '<td align="center" class="slot">Slot 1</td>';
	  if ($id1r) {
		
		echo '<td class="resulttext"><a class="hyper" href="../components/action_comp_search.php?ctID='.$id1r.'&amp;jmp=1"><acronym title="More information about a '.$slot1r.'">'.$slot1r.
		'</a>&nbsp;&nbsp;(<acronym title="Component ID">'.$ctid1r.'</acronym>)&nbsp;&nbsp;</td><td align=center><a class="hyper" href="../cable/action_ports_search.php?ID='.$ctid1r.'&ct='.$slot1r.
		'&room='.$_SESSION['room'].'&rack='.$_SESSION['rack'].'&ioc='.$_SESSION['iocName'].'"><acronym title="Find the port information for this '
		 .$slot1r.'">Ports</a>&nbsp;</td></tr>';
		
	  } else {
	    echo '<td>&nbsp;</td><td>&nbsp;</td></tr>';
	  }
	  
	// Slot 2  
	  if ($id2) {
	  
	  echo '<tr><td class="resulttext"><a class="hyper" href="../components/action_comp_search.php?ctID='.$id2.'&amp;jmp=1"><acronym title="More information about a '.$slot2.'">'.$slot2.
		'</a>&nbsp;&nbsp;(<acronym title="Component ID">'.$ctid2.'</acronym>)&nbsp;&nbsp;</td><td align=center><a class="hyper" href="../cable/action_ports_search.php?ID='.$ctid2.'&ct='.$slot2.
		'&room='.$_SESSION['room'].'&rack='.$_SESSION['rack'].'&ioc='.$_SESSION['iocName'].'"><acronym title="Find the port information for this '
		 .$slot2.'">Ports</a>&nbsp;</td>';
	  
	  } else {
	    echo '<tr><td>&nbsp;</td><td>&nbsp;</td>';
	  }
	  echo '<td align="center" class="slot">Slot 2</td>';
	  if ($id2r) {  
		
		echo '<td class="resulttext"><a class="hyper" href="../components/action_comp_search.php?ctID='.$id2r.'&amp;jmp=1"><acronym title="More information about a '.$slot2r.'">'.$slot2r.
		'</a>&nbsp;&nbsp;(<acronym title="Component ID">'.$ctid2r.'</acronym>)&nbsp;&nbsp;</td><td align=center><a class="hyper" href="../cable/action_ports_search.php?ID='.$ctid2r.'&ct='.$slot2r.
		'&room='.$_SESSION['room'].'&rack='.$_SESSION['rack'].'&ioc='.$_SESSION['iocName'].'"><acronym title="Find the port information for this '
		 .$slot2r.'">Ports</a>&nbsp;</td></tr>';
		
	  } else {
	    echo '<td>&nbsp;</td><td>&nbsp;</td></tr>';
	  }
	  
	// Slot 3  
	  if ($id3) {
		
		echo '<tr><td class="resulttext"><a class="hyper" href="../components/action_comp_search.php?ctID='.$id3.'&amp;jmp=1"><acronym title="More information about a '.$slot3.'">'.$slot3.
		'</a>&nbsp;&nbsp;(<acronym title="Component ID">'.$ctid3.'</acronym>)&nbsp;&nbsp;</td><td align=center><a class="hyper" href="../cable/action_ports_search.php?ID='.$ctid3.'&ct='.$slot3.
		'&room='.$_SESSION['room'].'&rack='.$_SESSION['rack'].'&ioc='.$_SESSION['iocName'].'"><acronym title="Find the port information for this '
		 .$slot3.'">Ports</a>&nbsp;</td>';
		
	  } else {
	    echo '<tr><td>&nbsp;</td><td>&nbsp;</td>';
	  }
	  echo '<td align="center" class="slot">Slot 3</td>';
	  if ($id3r) {
		
		echo '<td class="resulttext"><a class="hyper" href="../components/action_comp_search.php?ctID='.$id3r.'&amp;jmp=1"><acronym title="More information about a '.$slot3r.'">'.$slot3r.
		'</a>&nbsp;&nbsp;(<acronym title="Component ID">'.$ctid3r.'</acronym>)&nbsp;&nbsp;</td><td align=center><a class="hyper" href="../cable/action_ports_search.php?ID='.$ctid3r.'&ct='.$slot3r.
		'&room='.$_SESSION['room'].'&rack='.$_SESSION['rack'].'&ioc='.$_SESSION['iocName'].'"><acronym title="Find the port information for this '
		 .$slot3r.'">Ports</a>&nbsp;</td></tr>';
		
	  } else {
	    echo '<td>&nbsp;</td><td>&nbsp;</td></tr>';
      }
	  
	// Slot 4  
	  if ($id4) {
		
		echo '<tr><td class="resulttext"><a class="hyper" href="../components/action_comp_search.php?ctID='.$id4.'&amp;jmp=1"><acronym title="More information about a '.$slot4.'">'.$slot4.
		'</a>&nbsp;&nbsp;(<acronym title="Component ID">'.$ctid4.'</acronym>)&nbsp;&nbsp;</td><td align=center><a class="hyper" href="../cable/action_ports_search.php?ID='.$ctid4.'&ct='.$slot4.
		'&room='.$_SESSION['room'].'&rack='.$_SESSION['rack'].'&ioc='.$_SESSION['iocName'].'"><acronym title="Find the port information for this '
		 .$slot4.'">Ports</a>&nbsp;</td>';
		
	  } else {
	    echo '<tr><td>&nbsp;</td><td>&nbsp;</td>';
	  }
	  echo '<td align="center" class="slot">Slot 4</td>';
	  if ($id4r) {
		
		echo '<td class="resulttext"><a class="hyper" href="../components/action_comp_search.php?ctID='.$id4r.'&amp;jmp=1"><acronym title="More information about a '.$slot4r.'">'.$slot4r.
		'</a>&nbsp;&nbsp;(<acronym title="Component ID">'.$ctid4r.'</acronym>)&nbsp;&nbsp;</td><td align=center><a class="hyper" href="../cable/action_ports_search.php?ID='.$ctid4r.'&ct='.$slot4r.
		'&room='.$_SESSION['room'].'&rack='.$_SESSION['rack'].'&ioc='.$_SESSION['iocName'].'"><acronym title="Find the port information for this '
		 .$slot4r.'">Ports</a>&nbsp;</td></tr>';
		
	  } else {
	    echo '<td>&nbsp;</td><td>&nbsp;</td></tr>';
      }
	// Slot 5  
	  if ($id5) {
		
		echo '<tr><td class="resulttext"><a class="hyper" href="../components/action_comp_search.php?ctID='.$id5.'&amp;jmp=1"><acronym title="More information about a '.$slot5.'">'.$slot5.
		'</a>&nbsp;&nbsp;(<acronym title="Component ID">'.$ctid5.'</acronym>)&nbsp;&nbsp;</td><td align=center><a class="hyper" href="../cable/action_ports_search.php?ID='.$ctid5.'&ct='.$slot5.
		'&room='.$_SESSION['room'].'&rack='.$_SESSION['rack'].'&ioc='.$_SESSION['iocName'].'"><acronym title="Find the port information for this '
		 .$slot5.'">Ports</a>&nbsp;</td>';
		
	  } else {
	    echo '<tr><td>&nbsp;</td><td>&nbsp;</td>';
	  }
	  echo '<td align="center" class="slot">Slot 5</td>';
	  if ($id5r) {
		
		echo '<td class="resulttext"><a class="hyper" href="../components/action_comp_search.php?ctID='.$id5r.'&amp;jmp=1"><acronym title="More information about a '.$slot5r.'">'.$slot5r.
		'</a>&nbsp;&nbsp;(<acronym title="Component ID">'.$ctid5r.'</acronym>)&nbsp;&nbsp;</td><td align=center><a class="hyper" href="../cable/action_ports_search.php?ID='.$ctid5r.'&ct='.$slot5r.
		'&room='.$_SESSION['room'].'&rack='.$_SESSION['rack'].'&ioc='.$_SESSION['iocName'].'"><acronym title="Find the port information for this '
		 .$slot5r.'">Ports</a>&nbsp;</td></tr>';
		
	  } else {
	    echo '<td>&nbsp;</td><td>&nbsp;</td></tr>';
      }
	// Slot 6  
	  if ($id6) {
		
		echo '<tr><td class="resulttext"><a class="hyper" href="../components/action_comp_search.php?ctID='.$id6.'&amp;jmp=1"><acronym title="More information about a '.$slot6.'">'.$slot6.
		'</a>&nbsp;&nbsp;(<acronym title="Component ID">'.$ctid6.'</acronym>)&nbsp;&nbsp;</td><td align=center><a class="hyper" href="../cable/action_ports_search.php?ID='.$ctid6.'&ct='.$slot6.
		'&room='.$_SESSION['room'].'&rack='.$_SESSION['rack'].'&ioc='.$_SESSION['iocName'].'"><acronym title="Find the port information for this '
		 .$slot6.'">Ports</a>&nbsp;</td>';
		
	  } else {
	    echo '<tr><td>&nbsp;</td><td>&nbsp;</td>';
	  }
	  echo '<td align="center" class="slot">Slot 6</td>';
	  if ($id6r) {
		
		echo '<td class="resulttext"><a class="hyper" href="../components/action_comp_search.php?ctID='.$id6r.'&amp;jmp=1"><acronym title="More information about a '.$slot6r.'">'.$slot6r.
		'</a>&nbsp;&nbsp;(<acronym title="Component ID">'.$ctid6r.'</acronym>)&nbsp;&nbsp;</td><td align=center><a class="hyper" href="../cable/action_ports_search.php?ID='.$ctid6r.'&ct='.$slot6r.
		'&room='.$_SESSION['room'].'&rack='.$_SESSION['rack'].'&ioc='.$_SESSION['iocName'].'"><acronym title="Find the port information for this '
		 .$slot6r.'">Ports</a>&nbsp;</td></tr>';
		
	  } else {
	    echo '<td>&nbsp;</td><td>&nbsp;</td></tr>';
      }
	// Slot 7  
	  if ($id7) {
		
		echo '<tr><td class="resulttext"><a class="hyper" href="../components/action_comp_search.php?ctID='.$id7.'&amp;jmp=1"><acronym title="More information about a '.$slot7.'">'.$slot7.
		'</a>&nbsp;&nbsp;(<acronym title="Component ID">'.$ctid7.'</acronym>)&nbsp;&nbsp;</td><td align=center><a class="hyper" href="../cable/action_ports_search.php?ID='.$ctid7.'&ct='.$slot7.
		'&room='.$_SESSION['room'].'&rack='.$_SESSION['rack'].'&ioc='.$_SESSION['iocName'].'"><acronym title="Find the port information for this '
		 .$slot7.'">Ports</a>&nbsp;</td>';
		
	  } else {
	    echo '<tr><td>&nbsp;</td><td>&nbsp;</td>';
	  }
	  echo '<td align="center" class="slot">Slot 7</td>';
	  if ($id7r) {
		
		echo '<td class="resulttext"><a class="hyper" href="../components/action_comp_search.php?ctID='.$id7r.'&amp;jmp=1"><acronym title="More information about a '.$slot7r.'">'.$slot7r.
		'</a>&nbsp;&nbsp;(<acronym title="Component ID">'.$ctid7r.'</acronym>)&nbsp;&nbsp;</td><td align=center><a class="hyper" href="../cable/action_ports_search.php?ID='.$ctid7r.'&ct='.$slot7r.
		'&room='.$_SESSION['room'].'&rack='.$_SESSION['rack'].'&ioc='.$_SESSION['iocName'].'"><acronym title="Find the port information for this '
		 .$slot7r.'">Ports</a>&nbsp;</td></tr>';
		
	  } else {
	    echo '<td>&nbsp;</td><td>&nbsp;</td></tr>';
      }
	// Slot 8  
	  if ($id8) {
		
		echo '<tr><td class="resulttext"><a class="hyper" href="../components/action_comp_search.php?ctID='.$id8.'&amp;jmp=1"><acronym title="More information about a '.$slot8.'">'.$slot8.
		'</a>&nbsp;&nbsp;(<acronym title="Component ID">'.$ctid8.'</acronym>)&nbsp;&nbsp;</td><td align=center><a class="hyper" href="../cable/action_ports_search.php?ID='.$ctid8.'&ct='.$slot8.
		'&room='.$_SESSION['room'].'&rack='.$_SESSION['rack'].'&ioc='.$_SESSION['iocName'].'"><acronym title="Find the port information for this '
		 .$slot8.'">Ports</a>&nbsp;</td>';
		
	  } else {
	    echo '<tr><td>&nbsp;</td><td>&nbsp;</td>';
	  }
	  echo '<td align="center" class="slot">Slot 8</td>';
	  if ($id8r) {
		
		echo '<td class="resulttext"><a class="hyper" href="../components/action_comp_search.php?ctID='.$id8r.'&amp;jmp=1"><acronym title="More information about a '.$slot8r.'">'.$slot8r.
		'</a>&nbsp;&nbsp;(<acronym title="Component ID">'.$ctid8r.'</acronym>)&nbsp;&nbsp;</td><td align=center><a class="hyper" href="../cable/action_ports_search.php?ID='.$ctid8r.'&ct='.$slot8r.
		'&room='.$_SESSION['room'].'&rack='.$_SESSION['rack'].'&ioc='.$_SESSION['iocName'].'"><acronym title="Find the port information for this '
		 .$slot8r.'">Ports</a>&nbsp;</td></tr>';
		
	  } else {
	    echo '<td>&nbsp;</td><td>&nbsp;</td></tr>';
      }
	// Slot 9  
	  if ($id9) {
		
		echo '<tr><td class="resulttext"><a class="hyper" href="../components/action_comp_search.php?ctID='.$id9.'&amp;jmp=1"><acronym title="More information about a '.$slot9.'">'.$slot9.
		'</a>&nbsp;&nbsp;(<acronym title="Component ID">'.$ctid9.'</acronym>)&nbsp;&nbsp;</td><td align=center><a class="hyper" href="../cable/action_ports_search.php?ID='.$ctid9.'&ct='.$slot9.
		'&room='.$_SESSION['room'].'&rack='.$_SESSION['rack'].'&ioc='.$_SESSION['iocName'].'"><acronym title="Find the port information for this '
		 .$slot9.'">Ports</a>&nbsp;</td>';
		
	  } else {
	    echo '<tr><td>&nbsp;</td><td>&nbsp;</td>';
	  }
	  echo '<td align="center" class="slot">Slot 9</td>';
	  if ($id9r) {
		
		echo '<td class="resulttext"><a class="hyper" href="../components/action_comp_search.php?ctID='.$id9r.'&amp;jmp=1"><acronym title="More information about a '.$slot9r.'">'.$slot9r.
		'</a>&nbsp;&nbsp;(<acronym title="Component ID">'.$ctid9r.'</acronym>)&nbsp;&nbsp;</td><td align=center><a class="hyper" href="../cable/action_ports_search.php?ID='.$ctid9r.'&ct='.$slot9r.
		'&room='.$_SESSION['room'].'&rack='.$_SESSION['rack'].'&ioc='.$_SESSION['iocName'].'"><acronym title="Find the port information for this '
		 .$slot9r.'">Ports</a>&nbsp;</td></tr>';
		
	  } else {
	    echo '<td>&nbsp;</td><td>&nbsp;</td></tr>';
      }
	// Slot 10  
	  if ($id10) {
		
		echo '<tr><td class="resulttext"><a class="hyper" href="../components/action_comp_search.php?ctID='.$id10.'&amp;jmp=1"><acronym title="More information about a '.$slot10.'">'.$slot10.
		'</a>&nbsp;&nbsp;(<acronym title="Component ID">'.$ctid10.'</acronym>)&nbsp;&nbsp;</td><td align=center><a class="hyper" href="../cable/action_ports_search.php?ID='.$ctid10.'&ct='.$slot10.
		'&room='.$_SESSION['room'].'&rack='.$_SESSION['rack'].'&ioc='.$_SESSION['iocName'].'"><acronym title="Find the port information for this '
		 .$slot10.'">Ports</a>&nbsp;</td>';
		
	  } else {
	    echo '<tr><td>&nbsp;</td><td>&nbsp;</td>';
	  }
	  echo '<td align="center" class="slot">Slot 10</td>';
	  if ($id10r) {
		
		echo '<td class="resulttext"><a class="hyper" href="../components/action_comp_search.php?ctID='.$id10r.'&amp;jmp=1"><acronym title="More information about a '.$slot10r.'">'.$slot10r.
		'</a>&nbsp;&nbsp;(<acronym title="Component ID">'.$ctid10r.'</acronym>)&nbsp;&nbsp;</td><td align=center><a class="hyper" href="../cable/action_ports_search.php?ID='.$ctid10r.'&ct='.$slot10r.
		'&room='.$_SESSION['room'].'&rack='.$_SESSION['rack'].'&ioc='.$_SESSION['iocName'].'"><acronym title="Find the port information for this '
		 .$slot10r.'">Ports</a>&nbsp;</td></tr>';
		
	  } else {
	    echo '<td>&nbsp;</td><td>&nbsp;</td></tr>';
      }
	// Slot 11  
	  if ($id11) {
		
		echo '<tr><td class="resulttext"><a class="hyper" href="../components/action_comp_search.php?ctID='.$id11.'&amp;jmp=1"><acronym title="More information about a '.$slot11.'">'.$slot11.
		'</a>&nbsp;&nbsp;(<acronym title="Component ID">'.$ctid11.'</acronym>)&nbsp;&nbsp;</td><td align=center><a class="hyper" href="../cable/action_ports_search.php?ID='.$ctid11.'&ct='.$slot11.
		'&room='.$_SESSION['room'].'&rack='.$_SESSION['rack'].'&ioc='.$_SESSION['iocName'].'"><acronym title="Find the port information for this '
		 .$slot11.'">Ports</a>&nbsp;</td>';
		
	  } else {
	    echo '<tr><td>&nbsp;</td><td>&nbsp;</td>';
	  }
	  echo '<td align="center" class="slot">Slot 11</td>';
	  if ($id11r) {
		
		echo '<td class="resulttext"><a class="hyper" href="../components/action_comp_search.php?ctID='.$id11r.'&amp;jmp=1"><acronym title="More information about a '.$slot11r.'">'.$slot11r.
		'</a>&nbsp;&nbsp;(<acronym title="Component ID">'.$ctid11r.'</acronym>)&nbsp;&nbsp;</td><td align=center><a class="hyper" href="../cable/action_ports_search.php?ID='.$ctid11r.'&ct='.$slot11r.
		'&room='.$_SESSION['room'].'&rack='.$_SESSION['rack'].'&ioc='.$_SESSION['iocName'].'"><acronym title="Find the port information for this '
		 .$slot11r.'">Ports</a>&nbsp;</td></tr>';
		
	  } else {
	    echo '<td>&nbsp;</td><td>&nbsp;</td></tr>';
      }
	// Slot 12  
	  if ($id12) {
		
		echo '<tr><td class="resulttext"><a class="hyper" href="../components/action_comp_search.php?ctID='.$id12.'&amp;jmp=1"><acronym title="More information about a '.$slot12.'">'.$slot12.
		'</a>&nbsp;&nbsp;(<acronym title="Component ID">'.$ctid12.'</acronym>)&nbsp;&nbsp;</td><td align=center><a class="hyper" href="../cable/action_ports_search.php?ID='.$ctid12.'&ct='.$slot12.
		'&room='.$_SESSION['room'].'&rack='.$_SESSION['rack'].'&ioc='.$_SESSION['iocName'].'"><acronym title="Find the port information for this '
		 .$slot12.'">Ports</a>&nbsp;</td>';
		
	  } else {
	    echo '<tr><td>&nbsp;</td><td>&nbsp;</td>';
	  }
	  echo '<td align="center" class="slot">Slot 12</td>';
	  if ($id12r) {
		
		echo '<td class="resulttext"><a class="hyper" href="../components/action_comp_search.php?ctID='.$id12r.'&amp;jmp=1"><acronym title="More information about a '.$slot12r.'">'.$slot12r.
		'</a>&nbsp;&nbsp;(<acronym title="Component ID">'.$ctid12r.'</acronym>)&nbsp;&nbsp;</td><td align=center><a class="hyper" href="../cable/action_ports_search.php?ID='.$ctid12r.'&ct='.$slot12r.
		'&room='.$_SESSION['room'].'&rack='.$_SESSION['rack'].'&ioc='.$_SESSION['iocName'].'"><acronym title="Find the port information for this '
		 .$slot12r.'">Ports</a>&nbsp;</td></tr>';
		
	  } else {
	    echo '<td>&nbsp;</td><td>&nbsp;</td></tr>';
      }
	  
	  if (!$id0) {
	// Slot 13  
	  if ($id13) {
		
		echo '<tr><td class="resulttext"><a class="hyper" href="../components/action_comp_search.php?ctID='.$id13.'&amp;jmp=1"><acronym title="More information about a '.$slot13.'">'.$slot13.
		'</a>&nbsp;&nbsp;(<acronym title="Component ID">'.$ctid13.'</acronym>)&nbsp;&nbsp;</td><td align=center><a class="hyper" href="../cable/action_ports_search.php?ID='.$ctid13.'&ct='.$slot13.
		'&room='.$_SESSION['room'].'&rack='.$_SESSION['rack'].'&ioc='.$_SESSION['iocName'].'"><acronym title="Find the port information for this '
		 .$slot13.'">Ports</a>&nbsp;</td>';
		
	  } else {
	    echo '<tr><td>&nbsp;</td><td>&nbsp;</td>';
	  }
	  echo '<td align="center" class="slot">Slot 13</td>';
	  if ($id13r) {
		
		echo '<td class="resulttext"><a class="hyper" href="../components/action_comp_search.php?ctID='.$id13r.'&amp;jmp=1"><acronym title="More information about a '.$slot13r.'">'.$slot13r.
		'</a>&nbsp;&nbsp;(<acronym title="Component ID">'.$ctid13r.'</acronym>)&nbsp;&nbsp;</td><td align=center><a class="hyper" href="../cable/action_ports_search.php?ID='.$ctid13r.'&ct='.$slot13r.
		'&room='.$_SESSION['room'].'&rack='.$_SESSION['rack'].'&ioc='.$_SESSION['iocName'].'"><acronym title="Find the port information for this '
		 .$slot13r.'">Ports</a>&nbsp;</td></tr>';
		
	  } else {
	    echo '<td>&nbsp;</td><td>&nbsp;</td></tr>';
      }
	// Slot 14  
	  if ($id14) {
		
		echo '<tr><td class="resulttext"><a class="hyper" href="../components/action_comp_search.php?ctID='.$id14.'&amp;jmp=1"><acronym title="More information about a '.$slot14.'">'.$slot14.
		'</a>&nbsp;&nbsp;(<acronym title="Component ID">'.$ctid14.'</acronym>)&nbsp;&nbsp;</td><td align=center><a class="hyper" href="../cable/action_ports_search.php?ID='.$ctid14.'&ct='.$slot14.
		'&room='.$_SESSION['room'].'&rack='.$_SESSION['rack'].'&ioc='.$_SESSION['iocName'].'"><acronym title="Find the port information for this '
		 .$slot14.'">Ports</a>&nbsp;</td>';
		
	  } else {
	    echo '<tr><td>&nbsp;</td><td>&nbsp;</td>';
	  }
	  echo '<td align="center" class="slot">Slot 14</td>';
	  if ($id14r) {
		
		echo '<td class="resulttext"><a class="hyper" href="../components/action_comp_search.php?ctID='.$id14r.'&amp;jmp=1"><acronym title="More information about a '.$slot14r.'">'.$slot14r.
		'</a>&nbsp;&nbsp;(<acronym title="Component ID">'.$ctid14r.'</acronym>)&nbsp;&nbsp;</td><td align=center><a class="hyper" href="../cable/action_ports_search.php?ID='.$ctid14r.'&ct='.$slot14r.
		'&room='.$_SESSION['room'].'&rack='.$_SESSION['rack'].'&ioc='.$_SESSION['iocName'].'"><acronym title="Find the port information for this '
		 .$slot14r.'">Ports</a>&nbsp;</td></tr>';
		
	  } else {
	    echo '<td>&nbsp;</td><td>&nbsp;</td></tr>';
      }
	// Slot 15  
	  if ($id15) {
		
		echo '<tr><td class="resulttext"><a class="hyper" href="../components/action_comp_search.php?ctID='.$id15.'&amp;jmp=1"><acronym title="More information about a '.$slot15.'">'.$slot15.
		'</a>&nbsp;&nbsp;(<acronym title="Component ID">'.$ctid15.'</acronym>)&nbsp;&nbsp;</td><td align=center><a class="hyper" href="../cable/action_ports_search.php?ID='.$ctid15.'&ct='.$slot15.
		'&room='.$_SESSION['room'].'&rack='.$_SESSION['rack'].'&ioc='.$_SESSION['iocName'].'"><acronym title="Find the port information for this '
		 .$slot15.'">Ports</a>&nbsp;</td>';
		
	  } else {
	    echo '<tr><td>&nbsp;</td><td>&nbsp;</td>';
	  }
	  echo '<td align="center" class="slot">Slot 15</td>';
	  if ($id15r) {
		
		echo '<td class="resulttext"><a class="hyper" href="../components/action_comp_search.php?ctID='.$id15r.'&amp;jmp=1"><acronym title="More information about a '.$slot15r.'">'.$slot15r.
		'</a>&nbsp;&nbsp;(<acronym title="Component ID">'.$ctid15r.'</acronym>)&nbsp;&nbsp;</td><td align=center><a class="hyper" href="../cable/action_ports_search.php?ID='.$ctid15r.'&ct='.$slot15r.
		'&room='.$_SESSION['room'].'&rack='.$_SESSION['rack'].'&ioc='.$_SESSION['iocName'].'"><acronym title="Find the port information for this '
		 .$slot15r.'">Ports</a>&nbsp;</td></tr>';
		
	  } else {
	    echo '<td>&nbsp;</td><td>&nbsp;</td></tr>';
      }
	// Slot 16  
	  if ($id16) {
		
		echo '<tr><td class="resulttext"><a class="hyper" href="../components/action_comp_search.php?ctID='.$id16.'&amp;jmp=1"><acronym title="More information about a '.$slot16.'">'.$slot16.
		'</a>&nbsp;&nbsp;(<acronym title="Component ID">'.$ctid16.'</acronym>)&nbsp;&nbsp;</td><td align=center><a class="hyper" href="../cable/action_ports_search.php?ID='.$ctid16.'&ct='.$slot16.
		'&room='.$_SESSION['room'].'&rack='.$_SESSION['rack'].'&ioc='.$_SESSION['iocName'].'"><acronym title="Find the port information for this '
		 .$slot16.'">Ports</a>&nbsp;</td>';
		
	  } else {
	    echo '<tr><td>&nbsp;</td><td>&nbsp;</td>';
	  }
	  echo '<td align="center" class="slot">Slot 16</td>';
	  if ($id16r) {
		
		echo '<td class="resulttext"><a class="hyper" href="../components/action_comp_search.php?ctID='.$id16r.'&amp;jmp=1"><acronym title="More information about a '.$slot16r.'">'.$slot16r.
		'</a>&nbsp;&nbsp;(<acronym title="Component ID">'.$ctid16r.'</acronym>)&nbsp;&nbsp;</td><td align=center><a class="hyper" href="../cable/action_ports_search.php?ID='.$ctid16r.'&ct='.$slot16r.
		'&room='.$_SESSION['room'].'&rack='.$_SESSION['rack'].'&ioc='.$_SESSION['iocName'].'"><acronym title="Find the port information for this '
		 .$slot16r.'">Ports</a>&nbsp;</td></tr>';
		
	  } else {
	    echo '<td>&nbsp;</td><td>&nbsp;</td></tr>';
      }
	// Slot 17  
	  if ($id17) {
		
		echo '<tr><td class="resulttext"><a class="hyper" href="../components/action_comp_search.php?ctID='.$id17.'&amp;jmp=1"><acronym title="More information about a '.$slot17.'">'.$slot17.
		'</a>&nbsp;&nbsp;(<acronym title="Component ID">'.$ctid17.'</acronym>)&nbsp;&nbsp;</td><td align=center><a class="hyper" href="../cable/action_ports_search.php?ID='.$ctid17.'&ct='.$slot17.
		'&room='.$_SESSION['room'].'&rack='.$_SESSION['rack'].'&ioc='.$_SESSION['iocName'].'"><acronym title="Find the port information for this '
		 .$slot17.'">Ports</a>&nbsp;</td>';
		
	  } else {
	    echo '<tr><td>&nbsp;</td><td>&nbsp;</td>';
	  }
	  echo '<td align="center" class="slot">Slot 17</td>';
	  if ($id17r) {
		
		echo '<td class="resulttext"><a class="hyper" href="../components/action_comp_search.php?ctID='.$id17r.'&amp;jmp=1"><acronym title="More information about a '.$slot17r.'">'.$slot17r.
		'</a>&nbsp;&nbsp;(<acronym title="Component ID">'.$ctid17r.'</acronym>)&nbsp;&nbsp;</td><td align=center><a class="hyper" href="../cable/action_ports_search.php?ID='.$ctid17r.'&ct='.$slot17r.
		'&room='.$_SESSION['room'].'&rack='.$_SESSION['rack'].'&ioc='.$_SESSION['iocName'].'"><acronym title="Find the port information for this '
		 .$slot17r.'">Ports</a>&nbsp;</td></tr>';
		
	  } else {
	    echo '<td>&nbsp;</td><td>&nbsp;</td></tr>';
      }
	// Slot 18  
	  if ($id18) {
		
		echo '<tr><td class="resulttext"><a class="hyper" href="../components/action_comp_search.php?ctID='.$id18.'&amp;jmp=1"><acronym title="More information about a '.$slot18.'">'.$slot18.
		'</a>&nbsp;&nbsp;(<acronym title="Component ID">'.$ctid18.'</acronym>)&nbsp;&nbsp;</td><td align=center><a class="hyper" href="../cable/action_ports_search.php?ID='.$ctid18.'&ct='.$slot18.
		'&room='.$_SESSION['room'].'&rack='.$_SESSION['rack'].'&ioc='.$_SESSION['iocName'].'"><acronym title="Find the port information for this '
		 .$slot18.'">Ports</a>&nbsp;</td>';
		
	  } else {
	    echo '<tr><td>&nbsp;</td><td>&nbsp;</td>';
	  }
	  echo '<td align="center" class="slot">Slot 18</td>';
	  if ($id18r) {
		
		echo '<td class="resulttext"><a class="hyper" href="../components/action_comp_search.php?ctID='.$id18r.'&amp;jmp=1"><acronym title="More information about a '.$slot18r.'">'.$slot18r.
		'</a>&nbsp;&nbsp;(<acronym title="Component ID">'.$ctid18r.'</acronym>)&nbsp;&nbsp;</td><td align=center><a class="hyper" href="../cable/action_ports_search.php?ID='.$ctid18r.'&ct='.$slot18r.
		'&room='.$_SESSION['room'].'&rack='.$_SESSION['rack'].'&ioc='.$_SESSION['iocName'].'"><acronym title="Find the port information for this '
		 .$slot18r.'">Ports</a>&nbsp;</td></tr>';
		
	  } else {
	    echo '<td>&nbsp;</td><td>&nbsp;</td></tr>';
      }
	// Slot 19  
	  if ($id19) {
		
		echo '<tr><td class="resulttext"><a class="hyper" href="../components/action_comp_search.php?ctID='.$id19.'&amp;jmp=1"><acronym title="More information about a '.$slot19.'">'.$slot19.
		'</a>&nbsp;&nbsp;(<acronym title="Component ID">'.$ctid19.'</acronym>)&nbsp;&nbsp;</td><td align=center><a class="hyper" href="../cable/action_ports_search.php?ID='.$ctid19.'&ct='.$slot19.
		'&room='.$_SESSION['room'].'&rack='.$_SESSION['rack'].'&ioc='.$_SESSION['iocName'].'"><acronym title="Find the port information for this '
		 .$slot19.'">Ports</a>&nbsp;</td>';
		
	  } else {
	    echo '<tr><td>&nbsp;</td><td>&nbsp;</td>';
	  }
	  echo '<td align="center" class="slot">Slot 19</td>';
	  if ($id19r) {
		
		echo '<td class="resulttext"><a class="hyper" href="../components/action_comp_search.php?ctID='.$id19r.'&amp;jmp=1"><acronym title="More information about a '.$slot19r.'">'.$slot19r.
		'</a>&nbsp;&nbsp;(<acronym title="Component ID">'.$ctid19r.'</acronym>)&nbsp;&nbsp;</td><td align=center><a class="hyper" href="../cable/action_ports_search.php?ID='.$ctid19r.'&ct='.$slot19r.
		'&room='.$_SESSION['room'].'&rack='.$_SESSION['rack'].'&ioc='.$_SESSION['iocName'].'"><acronym title="Find the port information for this '
		 .$slot19r.'">Ports</a>&nbsp;</td></tr>';
		
	  } else {
	    echo '<td>&nbsp;</td><td>&nbsp;</td></tr>';
      }
	// Slot 20  
	  if ($id20) {
		
		echo '<tr><td class="resulttext"><a class="hyper" href="../components/action_comp_search.php?ctID='.$id20.'&amp;jmp=1"><acronym title="More information about a '.$slot20.'">'.$slot20.
		'</a>&nbsp;&nbsp;(<acronym title="Component ID">'.$ctid20.'</acronym>)&nbsp;&nbsp;</td><td align=center><a class="hyper" href="../cable/action_ports_search.php?ID='.$ctid20.'&ct='.$slot20.
		'&room='.$_SESSION['room'].'&rack='.$_SESSION['rack'].'&ioc='.$_SESSION['iocName'].'"><acronym title="Find the port information for this '
		 .$slot20.'">Ports</a>&nbsp;</td>';
		
	  } else {
	    echo '<tr><td>&nbsp;</td><td>&nbsp;</td>';
	  }
	  echo '<td align="center" class="slot">Slot 20</td>';
	  if ($id20r) {
		
		echo '<td class="resulttext"><a class="hyper" href="../components/action_comp_search.php?ctID='.$id20r.'&amp;jmp=1"><acronym title="More information about a '.$slot20r.'">'.$slot20r.
		'</a>&nbsp;&nbsp;(<acronym title="Component ID">'.$ctid20r.'</acronym>)&nbsp;&nbsp;</td><td align=center><a class="hyper" href="../cable/action_ports_search.php?ID='.$ctid20r.'&ct='.$slot20r.
		'&room='.$_SESSION['room'].'&rack='.$_SESSION['rack'].'&ioc='.$_SESSION['iocName'].'"><acronym title="Find the port information for this '
		 .$slot20r.'">Ports</a>&nbsp;</td></tr>';
		
	  } else {
	    echo '<td>&nbsp;</td><td>&nbsp;</td></tr>';
      }
	// Slot 21  
	  if ($id21) {
		
		echo '<tr><td class="resulttext"><a class="hyper" href="../components/action_comp_search.php?ctID='.$id21.'&amp;jmp=1"><acronym title="More information about a '.$slot21.'">'.$slot21.
		'</a>&nbsp;&nbsp;(<acronym title="Component ID">'.$ctid21.'</acronym>)&nbsp;&nbsp;</td><td align=center><a class="hyper" href="../cable/action_ports_search.php?ID='.$ctid21.'&ct='.$slot21.
		'&room='.$_SESSION['room'].'&rack='.$_SESSION['rack'].'&ioc='.$_SESSION['iocName'].'"><acronym title="Find the port information for this '
		 .$slot21.'">Ports</a>&nbsp;</td>';
		
	  } else {
	    echo '<tr><td>&nbsp;</td><td>&nbsp;</td>';
	  }
	  echo '<td align="center" class="slot">Slot 21</td>';
	  if ($id21r) {
		
		echo '<td class="resulttext"><a class="hyper" href="../components/action_comp_search.php?ctID='.$id21r.'&amp;jmp=1"><acronym title="More information about a '.$slot21r.'">'.$slot21r.
		'</a>&nbsp;&nbsp;(<acronym title="Component ID">'.$ctid21r.'</acronym>)&nbsp;&nbsp;</td><td align=center><a class="hyper" href="../cable/action_ports_search.php?ID='.$ctid21r.'&ct='.$slot21r.
		'&room='.$_SESSION['room'].'&rack='.$_SESSION['rack'].'&ioc='.$_SESSION['iocName'].'"><acronym title="Find the port information for this '
		 .$slot21r.'">Ports</a>&nbsp;</td></tr>';
		
	  } else {
	    echo '<td>&nbsp;</td></tr>';
      }
      }
	  
    // Reports
   echo '</table><table width="100%"  border="1" cellspacing="0" cellpadding="2"><tr>';
   echo '<a name="report"></a>';
   include_once('../report/report_startform.php');
   include_once('../report/report_submit_ioc_contents.php');
   echo '</tr>';

?>
</table>
</div>
