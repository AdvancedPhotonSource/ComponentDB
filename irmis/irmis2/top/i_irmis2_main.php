<div class="container">
<style type="text/css">

#irmis {
	position:absolute;
	left:570px;
	top:382px;
	width:145px;
	height:63px;
	z-index:4;
}
#pv {
	position:absolute;
	left:316px;
	top:126px;
	width:200px;
	height:99px;
	z-index:4;
}
#aoi {
	position:absolute;
	left:685px;
	top:115px;
	width:157px;
	height:157px;
	z-index:4;
}
#installed_components {
	position:absolute;
	left:196px;
	top:368px;
	width:179px;
	height:181px;
	z-index:4;
}
#component_types {
	position:absolute;
	left:832px;
	top:368px;
	width:179px;
	height:119px;
	z-index:4;
}
#cable {
	position:absolute;
	left:545px;
	top:591px;
	width:157px;
	height:111px;
	z-index:4;
}
#searchResults {
	position:absolute;
	left:185px;
	top:96px;
	width:900px;
	height:650px;
	z-index:1;
	background-color: #EEEEEE;
}

</style>
<link href="irmis2.css" rel="stylesheet" type="text/css">
<style type="text/css">

#pv_title {
	position:absolute;
	left:326px;
	top:108px;
	width:252px;
	height:7px;
	z-index:4;
}
#pv_find {
	position:absolute;
	left:325px;
	top:246px;
	width:23px;
	height:11px;
	z-index:4;
}
#ioc {
	position:absolute;
	left:228px;
	top:565px;
	width:22px;
	height:9px;
	z-index:4;
}
#network {
	position:absolute;
	left:260px;
	top:582px;
	width:41px;
	height:11px;
	z-index:4;
}
#components {
	position:absolute;
	left:320px;
	top:565px;
	width:29px;
	height:11px;
	z-index:4;
}
#racks {
	position:absolute;
	left:328px;
	top:582px;
	width:27px;
	height:6px;
	z-index:4;
}
#plcs {
	position:absolute;
	left:274px;
	top:565px;
	width:27px;
	height:6px;
	z-index:4;
}
#installed_components_title {
	position:absolute;
	left:240px;
	top:349px;
	width:171px;
	height:11px;
	z-index:4;
}
#component_types_title {
	position:absolute;
	left:890px;
	top:349px;
	width:128px;
	height:12px;
	z-index:4;
}
#search {
	position:absolute;
	left:830px;
	top:565px;
	width:40px;
	height:12px;
	z-index:4;
}
#component_types_viewer {
	position:absolute;
	left:890px;
	top:565px;
	width:140px;
	height:13px;
	z-index:4;
}
#spares {
	position:absolute;
	left:1024px;
	top:565px;
	width:39px;
	height:13px;
	z-index:4;
}
#cables_title {
	position:absolute;
	left:574px;
	top:572px;
	width:150px;
	height:11px;
	z-index:4;
}
#Layer20 {
	position:absolute;
	left:354px;
	top:162px;
	width:264px;
	height:226px;
	z-index:2;
}
#elipse {
	position:absolute;
	left:290px;
	top:137px;
	width:708px;
	height:486px;
	z-index:2;
}
-->
</style>
<link href="../common/irmis2.css" rel="stylesheet" type="text/css">
<style type="text/css">
<!--
#examine {
	position:absolute;
	left:511px;
	top:246px;
	width:62px;
	height:13px;
	z-index:4;
}
#installed_component_lines {
	position:absolute;
	left:275px;
	top:170px;
	width:424px;
	height:371px;
	visibility: visible;
}
#component_type_lines {
	position:absolute;
	left:275px;
	top:170px;
	width:424px;
	height:371;
	z-index:3;
	visibility: visible;
}
#pvlines {
	position:absolute;
	left:350px;
	top:170px;
	width:424px;
	height:371px;
	z-index:3;
	visibility: visible;
}
#aoilines {
	position:absolute;
	left:310px;
	top:161px;
	width:659px;
	height:582px;
	z-index:3;
}
#lines {
	position:absolute;
	left:294px;
	top:134px;
	width:578px;
	height:522px;
	z-index:3;
}
-->
</style>
<body>

<?
	include_once('db.inc');
	echo '<a href="http://'.$db_host.'/irmis2/idt/irmis-component.jnlp">Components</a>';
?>


<div id="irmis"><img src="../common/images/irmis2Logo.png" alt="IRMIS2" width="153" height="88"></div>
<div id="pv" onMouseOver="MM_showHideLayers('pvlines','','show')" onMouseOut="MM_showHideLayers('pvlines','','hide')"><img src="../common/images/pvs.jpg" alt="PV's" width="267" height="116"></div>
<div id="aoi"><a href="../aoi/aoi_edit.php"><img src="../common/images/aoi_logo_site.png" alt="Applications of Interest" width="200" height="200"></a></div>
<div id="installed_components" onMouseOver="MM_showHideLayers('installed_component_lines','','show')" onMouseOut="MM_showHideLayers('installed_component_lines','','hide')"><img src="../common/images/installed_equip.jpg" alt="Installed Equipment" width="240" height="193"></div>
<div id="component_types" onMouseOver="MM_showHideLayers('component_type_lines','','show')" onMouseOut="MM_showHideLayers('component_type_lines','','hide')"><img src="../common/images/Component_Types.jpg" alt="Component Types" width="240" height="193"></div>

<div id="cable">
<?
	include_once('db.inc');
	echo '<a href="http://'.$app_host.'/irmis2/idt/irmis-cable.jnlp"><img src="../common/images/cable.jpg" alt="Cables" width="203" height="141" border="0"></a>';
?>
</div>

<div id="searchResults"></div>
<div id="pv_title">
  <div align="center" class="titlecenter">Process Variables </div>
</div>

<div id="pv_find"><div align="left"><span class="style2">
<?
    include_once('db.inc');
	echo '<a href="http://'.$app_host.'/irmis2/idt/irmis.jnlp" class="titlecenter">Find</a>';
?>
</span></div></div>

<div id="installed_components_title"><span class="titlecenter">Installed Components </span></div>
<div id="ioc"><a href="../ioc/ioc.php" class="titlecenter"><span class="style2">IOC's</span></a></div>
<div id="network"><a href="../net/net.php" class="titlecenter"><span class="style2">Network</span></a></div>

<div id="components"><div align="left"><span class="style2">
<?
    include_once('db.inc');
    echo '<a href="http://'.$app_host.'/irmis2/idt/irmis-component.jnlp" class="titlecenter">Components</a>';
//<div id="components"><a href="../components/comp.php" class="titlecenter"><span class="style2">Components</span></a></div>
?>
</span></div></div>

<div id="racks"><a href="../racks/racks.php" class="titlecenter"><span class="style2">Racks</span></a></div>
<div id="plcs"><a href="../plc/plc.php" class="titlecenter"><span class="style2">PLC's</span></a></div>

</div>
<div id="component_types_title"><span class="titlecenter">Component Types </span></div>
<div id="search"><span class="titlecenter"><a href="../components/comp.php">Search</a></span></div>
<div id="component_types_viewer"><span class="titlecenter"><a href="../components/comp.php">Component Types</a></span></div>
<div id="spares"><span class="titlecenter"><a href="../spares/spares.php">Spares</a></span></div>
<div id="cables_title"><span class="titlecenter">Cables / Connections </span></div>
<div id="elipse"><img src="../common/images/elipse.png" width="700" height="570"></div>
<!--<div class="titlecenter" id="examine">Examine</div> -->
<div id="lines"><img src="../common/images/lines.png" width="700" height="570"></div>
</div>