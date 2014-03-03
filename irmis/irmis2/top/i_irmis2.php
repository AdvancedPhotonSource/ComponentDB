<div class="irmis2ReportSelection">

<div id="main_criteria_1">
<span class="criteria">Web Desktop</span>
<a class="menu" href="../power/power.php" tabindex="1">AC Power Info</a>
<a class="menu" href="../aoi/aoi_edit.php" tabindex="2">AOI Info</a>
<a class="menu" href="../components/comp.php" tabindex="3">Component Type Info</a>
<a class="menu" href="../ioc/ioc.php" tabindex="4">IOC Info</a>
<a class="menu" href="../net/net.php" tabindex="5">Network Info</a>
<a class="menu" href="../plc/plc.php" tabindex="6">PLC Info</a>
<a class="menu" href="../racks/racks.php" tabindex="7">Racks Info</a>
<a class="menu" href="../server/server.php" tabindex="8">Server Info</a>
<a class="menu" href="../spares/spares.php" tabindex="9">Spares Info</a>
<a class="menu" href="https://ctlappsirmis.aps.anl.gov:8443/component_history/app" tabindex="10">Component History</a>

<a class="menu" href="wd_help.php" target="_blank" tabindex="12">Web Desktop Help</a>
</div>

<div id="main_criteria_2">
<span class="criteria">IRMIS Desktop (idt)</span>
<?php include_once ('db.inc');
echo '<a class="menu" href="http://'.$app_host.'/irmis2/idt/irmis-pv.jnlp" tabindex="13">idt: PV Info</a>';
echo '<a class="menu" href="http://'.$app_host.'/irmis2/idt/irmis-ioc.jnlp" tabindex="14">idt: IOC</a>';
echo '<a class="menu" href="http://'.$app_host.'/irmis2/idt/irmis-component.jnlp" tabindex="15">idt: Component</a>';
echo '<a class="menu" href="http://'.$app_host.'/irmis2/idt/irmis-componenttype.jnlp" tabindex="16">idt: Component-Type</a>';
echo '<a class="menu" href="http://'.$app_host.'/irmis2/idt/irmis-cable.jnlp" tabindex="17">idt: Cables</a>';
echo '<a class="menu" href="idt_help.php" tabindex="18">idt Help</a>' ?>
</div>

<div id="main_criteria_3">
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<a class="hyper" href="../gst/gst_irmis3.php" tabindex="19">Global Search Tool</a>
<form action='../gst/gst_irmis3.php' method='post'>
&nbsp;<input type='text' size='11' name='gst_search' tabindex="20">
<input class="gstbutton" type='submit' value='Search' tabindex="21">
</form>
</div>

<div id="servers">
  <?
    include_once ('db.inc');
    echo '<table><tr><td class="serverbackground"><b>Database Server:</b> <br />&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; '.$db_host.':'.$db_name_production_1.'</td></tr>';
	echo '<td class="serverbackground"><b>Application Server:</b> <br />&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;'.$app_host.'</td></tr></table>';
  ?>
</div>

</div>
