<div class="NetResultsLeft">
  <table width="100%"  border="1" cellspacing="0" cellpadding="2">
<?php 
    // null list in session implies that result size was exceeded
	$swList = $_SESSION['swList'];
	    echo '<tr><td colspan="4"class=value>"'.$swList->length().'" Network Switch\'s Found</td></tr>';
	    echo '<tr>';
        echo '<th nowrap>Switches</th>';
	    echo '<th>Switch Location</th>';
	    echo '<th>Ports</th>';
		echo '<th>Monitor</th>';
        echo '</tr>';
    if ($swList == null) {
        echo '<tr><td class="warning bold" colspan=4>Search produced too many results to display.<br>';
        echo 'Limit is 5000. Try using the Additional<br>';
        echo 'Search Terms to constrain your search.</td></tr>';      
         
    } else if ($swList->length() == 0) {
        echo '<tr><td class="warning bold" colspan=4>No Switch\'s found: please try another search.</td></tr>';
        
    } else {
	        
$swEntities = $swList->getArray();
foreach ($swEntities as $swEntity) {

$Sw = $swEntity->getsw();
if ($Sw==swsr02) {
	$bldg="400";
	$area="ACCEL";
}elseif ($Sw==swsr12) {
	$bldg="400";
	$area="ACCEL";
}elseif ($Sw==swsr22) {
	$bldg="400";
	$area="ACCEL";
}elseif ($Sw==swsr29) {
	$bldg="400";
	$area="ACCEL";
}elseif ($Sw==swsr31) {
	$bldg="400";
	$area="ACCEL";	
}elseif ($Sw==swsr32) {	
	$bldg="400";
	$area="ACCEL";
}elseif ($Sw==swsr33) {
	$bldg="400";
	$area="ACCEL";
}elseif ($Sw==swsr35) {
	$bldg="400";
	$area="ACCEL";
}elseif ($Sw==swsr37) {
	$bldg="400";
	$area="ACCEL";
}elseif ($Sw==swsr39) {
	$bldg="400";
	$area="ACCEL";
}elseif ($Sw==swinj) {
	$bldg="412";
	$area="ACCEL";
}elseif ($Sw==swrf) {
	$bldg="420";
	$area="ACCEL";
}elseif ($Sw==swacc1a) {
	$bldg="401";
	$area="ACCEL";
}elseif ($Sw==swsrdg0405) {
	$bldg="400";
	$area="ACCEL";
}elseif ($Sw==swicr) {
	$bldg="412";
	$area="CENTRAL";
}elseif ($Sw==swcol169) {
	$bldg="401";
	$area="CENTRAL";
}elseif ($Sw==hubmcrd) {
	$bldg="401";
	$area="ACCEL";
}elseif ($Sw==hubmcrc) {
	$bldg="401";
	$area="ACCEL";
}elseif ($Sw==hubleutl) {
	$bldg="412";
	$area="ACCEL";
}

echo '<td class="primary">'.$swEntity->getsw().'</td>';
if ($swEntity->getswLocation()) {
	echo '<td class="resulttext">'.$swEntity->getswLocation().'</td>';
} else {
	echo '<td>&nbsp;</td>';
}  
echo '<td class="resulttext"><a class="hyper" href="action_net_search.php?swID='.$swEntity->getsw().'">Ports</td>';
echo '<td class="resulttext"><a class="hyper" target="_blank" href="http://www4.aps.anl.gov/cgi-bin/routers2.cgi?rtr='.$area.'%2F'.$bldg.'%2F'.$Sw.'.cfg&bars=Cami&xgtype=d&page=graph&xgstyle=l2&xmtype=options&if='.$Sw.'.aps.anl.gov_CPU">Monitor</td></tr>';
    }
}
?>
</table>
</div>