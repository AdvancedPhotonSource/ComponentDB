<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1" />
<title>Untitled Document</title>
<link href="../common/irmis2.css" rel="stylesheet" type="text/css">
</head>


<?
include('../common/i_irmis_header.php');
$iocName = $_GET['ioc'];

$SwP = $_GET['SwP'];
$BlP = $_GET['BlP'];
$PortP = $_GET['PortP'];

$SwS = $_GET['SwS'];
$BlS = $_GET['BlS'];
$PortS = $_GET['PortS'];

if ($SwP==swsr02) {
	$bldgP="400";
	$areaP="ACCEL";
}elseif ($SwP==swsr12) {
	$bldgP="400";
	$areaP="ACCEL";
}elseif ($SwP==swsr22) {
	$bldgP="400";
	$areaP="ACCEL";
}elseif ($SwP==swsr29) {
	$bldgP="400";
	$areaP="ACCEL";
}elseif ($SwP==swsr31) {
	$bldgP="400";	
	$areaP="ACCEL";
}elseif ($SwP==swsr32) {	
	$bldgP="400";
	$areaP="ACCEL";
}elseif ($SwP==swsr33) {
	$bldgP="400";
	$areaP="ACCEL";
}elseif ($SwP==swsr35) {
	$bldgP="400";
	$areaP="ACCEL";
}elseif ($SwP==swsr37) {
	$bldgP="400";
	$areaP="ACCEL";
}elseif ($SwP==swsr39) {
	$bldgP="400";
	$areaP="ACCEL";
}elseif ($SwP==swacc1a) {
	$bldgP="401";
	$areaP="ACCEL";
}elseif ($SwP==swinj) {
	$bldgP="412";
	$areaP="ACCEL";
}elseif ($SwP==swrf) {
	$bldgP="420";
	$areaP="ACCEL";
}elseif ($SwP==swsrdg0405) {
	$bldgP="400";
	$areaP="ACCEL";
}elseif ($SwP==swicr) {
	$bldgP="412";
	$areaP="CENTRAL";
}elseif ($SwP==swcol169) {
	$bldgP="401";
	$areaP="CENTRAL";
}elseif ($SwP==hubmcrd) {
	$bldgP="401";
	$areaP="ACCEL";
}elseif ($SwP==hubmcrc) {
	$bldgP="401";
	$areaP="ACCEL";
}elseif ($SwP==hubleutl) {
	$bldgP="412";
	$areaP="ACCEL";
}

if ($SwS==swsr02) {
	$bldgS="400";
	$areaS="ACCEL";
}elseif ($SwS==swsr12) {
	$bldgS="400";
	$areaS="ACCEL";
}elseif ($SwS==swsr22) {
	$bldgS="400";
	$areaS="ACCEL";
}elseif ($SwS==swsr29) {
	$bldgS="400";
	$areaS="ACCEL";
}elseif ($SwS==swsr31) {
	$bldgS="400";
	$areaS="ACCEL";
}elseif ($SwS==swsr32) {
	$bldgS="400";
	$areaS="ACCEL";
}elseif ($SwS==swsr33) {
	$bldgS="400";	
	$areaS="ACCEL";
}elseif ($SwS==swsr35) {
	$bldgS="400";
	$areaS="ACCEL";
}elseif ($SwS==swsr37) {
	$bldgS="400";
	$areaS="ACCEL";
}elseif ($SwS==swsr39) {
	$bldgS="400";
	$areaS="ACCEL";
}elseif ($SwS==swacc1a) {
	$bldgS="401";
	$areaS="ACCEL";	
}elseif ($SwS==swinj) {
	$bldgS="412";
	$areaS="ACCEL";
}elseif ($SwS==swrf) {
	$bldgS="420";
	$areaS="ACCEL";
}elseif ($SwS==swsrdg0405) {
	$bldgS="400";
	$areaS="ACCEL";
}elseif ($SwS==swicr) {
	$bldgS="412";
	$areaS="CENTRAL";
}elseif ($SwS==swcol169) {
	$bldgS="401";
	$areaS="CENTRAL";
}elseif ($SwS==hubmcrd) {
	$bldgS="401";
	$areaS="ACCEL";
}elseif ($SwS==hubmcrc) {
	$bldgS="401";
	$areaS="ACCEL";
}elseif ($SwS==hubleutl) {
	$bldgS="412";
	$areaS="ACCEL";
}

// assemble the url for the primary
if ($SwP == "No Permanent Assignment") {
	$merry='<div class="primwarning"><b>Roving IOC, No Permanent Port Assignment - Not Able To Display Primary Switch Connection Graph For '.$iocName.'</b></div>';
}elseif (($SwP != "")) {
	$urlP = "http://www4.aps.anl.gov/cgi-bin/routers2.cgi?rtr=".$areaP."%2F".$bldgP."%2F".$SwP.".cfg&bars=Cami&xgtype=d&page=graph&xgstyle=l2&xmtype=options&if=".$SwP.".aps.anl.gov_".$BlP.$PortP."";
	$merry='<iframe class="iocPrim" src='.$urlP.'></iframe>';
} else {
	$merry='<div class="primwarning"><b>Insufficient Port Information - Not Able To Display Primary Switch Connection Graph For '.$iocName.'</b></div>';
}

// assemble the url for the secondary
if ($SwS == "No Permanent Assignment") {
	$pippin='<div class="secwarning"><b>Roving IOC, No Permanent Port Assignment - Not Able To Display Secondary Connection Graph For '.$iocName.'</b></div>';
}elseif ((($SwS != "") && ($BlS != "") && ($PortS))) {
	$urlS = "http://www4.aps.anl.gov/cgi-bin/routers2.cgi?rtr=".$areaS."%2F".$bldgS."%2F".$SwS.".cfg&bars=Cami&xgtype=d&page=graph&xgstyle=l2&xmtype=options&if=".$SwS.".aps.anl.gov_".$BlS.$PortS."";
	$pippin='<iframe class="iocSec" src='.$urlS.'></iframe>';
} else {
	$pippin='<div class="secwarning"><b>Insufficient Port Information - Not Able To Display Secondary Switch Connection Graph For '.$iocName.'</b></div>';
	
}
	
echo '<table class="netCon"><tr><td class="netHeader" width="1323" align="center" colspan="2">'.$iocName.'</th></tr>';
echo '<tr><th width="660">Primary Network Connection</th><th>Secondary Network Connection</th></tr>';
echo '<tr><th width="660">Switch - '.$SwP.' | Port - '.$BlP.$PortP.'</th><th>Switch - '.$SwS.' | Port - '.$BlS.$PortS.'</th></tr></table>';
echo $merry.$pippin;

?>		  

</html>
