<?php
/*
 * Written by Dawn Clemons
 * This file is common to all report tool files. It includes needed code
 * to obtain results information, resumes the current session, and initializes
 * a global variable.
 */

if($_POST['viewName'] == "AOI")
{
   include_once ('../db/i_db_entity_aoi.php');
   include_once ('../db/i_db_entity_aoi_pv.php');
   include_once ('../db/i_db_entity_aoi_plc.php');
   include_once ('../db/i_db_entity_aoi_ioc.php');
   include_once ('../db/i_db_entity_aoi_topdisplay.php');
   include_once ('../db/i_db_entity_aoi_document.php');
   include_once ('../db/i_db_entity_aoi_revhistory.php');
   include_once ('../db/i_db_entity_aoi_note.php');
}

if($_POST['viewName'] == "IOC")
{
   include_once ('../db/i_db_entity_ioc.php');
}

if($_POST['viewName'] == "SpareTypes")
{
   include_once ('../db/i_db_entity_spares.php');
}

if($_POST['viewName'] == "Rack")
{
   include_once ('../db/i_db_entity_rack.php');
   include_once ('../db/i_db_entity_room_pd.php');
}

if($_POST['viewName'] == "RackContents")
{
   include_once ('../db/i_db_entity_rack_contents.php');
   include_once ('../db/i_db_entity_room_pd.php');
}

if($_POST['viewName'] == "RackCC")
{
   include_once ('../db/i_db_entity_rack_component_contents.php');
   include_once ('../db/i_db_entity_room_pd.php');
}


if($_POST['viewName'] == "IOCContents")
{
   include_once ('../db/i_db_entity_ioc_contents.php');
}


if($_POST['viewName'] == "ComponentTypes")
{
   include_once ('../db/i_db_entity_componenttype.php');
}

if($_POST['viewName'] == "ComponentTypeDetails")
{
include_once ('../db/i_db_entity_componenttype.php');
include_once ('../db/i_db_entity_component.php');
include_once ('../db/i_db_entity_component_person.php');
include_once ('../db/i_db_entity_component_mfg_pd.php');
include_once ('../db/i_db_entity_component_ff_pd.php');
include_once ('../db/i_db_entity_component_function_pd.php');
include_once ('../db/i_db_entity_component_doc.php');
include_once ('../db/i_db_entity_component_if.php');
}

if($_POST['viewName'] == "ComponentTypeInstances")
{
   include_once ('../db/i_db_entity_componenttype.php');
   include_once ('../db/i_db_entity_component.php');
   include_once ('../db/i_db_entity_component_person.php');
   include_once ('../db/i_db_entity_component_mfg_pd.php');
   include_once ('../db/i_db_entity_component_ff_pd.php');
   include_once ('../db/i_db_entity_component_function_pd.php');
   include_once ('../db/i_db_entity_component_doc.php');
   include_once ('../db/i_db_entity_component_if.php');
}

if($_POST['viewName'] == "ComponentTypeInstancesShort")
{
   include_once ('../db/i_db_entity_componenttype.php');
   include_once ('../db/i_db_entity_component.php');
   include_once ('../db/i_db_entity_component_person.php');
   include_once ('../db/i_db_entity_component_mfg_pd.php');
   include_once ('../db/i_db_entity_component_ff_pd.php');
   include_once ('../db/i_db_entity_component_function_pd.php');
   include_once ('../db/i_db_entity_component_doc.php');
   include_once ('../db/i_db_entity_component_if.php');
}


if($_POST['viewName'] == "AOIViewerEditor")
{
   include_once ('../db/i_db_entity_aoi_name.php');
   include_once ('../db/i_db_entity_aoi.php');
   include_once ('../db/i_db_entity_aoi_basic.php');
   include_once ('../db/i_db_entity_aoi_pv.php');
   include_once ('../db/i_db_entity_aoi_plc.php');
   include_once ('../db/i_db_entity_aoi_ioc.php');
   include_once ('../db/i_db_entity_aoi_topdisplay.php');
   include_once ('../db/i_db_entity_aoi_document.php');
   include_once ('../db/i_db_entity_aoi_revhistory.php');
   include_once ('../db/i_db_entity_aoi_note.php');
   include_once ('../db/i_db_entity_aoi_stcmd_line.php');
}

if($_POST['viewName'] == "IOCGeneral")
{
   include_once ('../util/i_logging.php');
   include_once ('../db/i_db_entity_ioc.php');
   include_once ('../db/i_db_entity_ioc_system_pd.php');
   include_once ('../db/i_db_entity_ioc_developer_pd.php');
   include_once ('../db/i_db_entity_ioc_tech_pd.php');
}

if($_POST['viewName'] == "IOCNetwork")
{
   include_once ('../db/i_db_connect.php');
   include_once ('../db/i_db_querybuilder.php');
   include_once ('../db/i_db_entity_ioc.php');
   include_once ('../db/i_db_entity_ioc_system_pd.php');
   include_once ('../db/i_db_entity_ioc_developer_pd.php');
   include_once ('../db/i_db_entity_ioc_tech_pd.php');
   include_once ('../util/i_logging.php');
}

if($_POST['viewName'] == "SpareTypes")
{
   include_once ('../db/i_db_entity_spares.php');
}

if($_POST['viewName'] == "Server")
{
   include_once ('../db/i_db_entity_server.php');
}

if($_POST['viewName'] == "Power")
{
   include_once ('../db/i_db_entity_power_contents.php');
   
}
if($_POST['viewName'] == "SG")
{
   include_once ('../db/i_db_entity_switchgear.php');
   
}

if($_POST['viewName'] == "SGContents")
{
   include_once ('../db/i_db_entity_switchgear_contents.php');
   
}

if($_POST['viewName'] == "Cable")
{
   include_once ('../db/i_db_entity_cable.php');
   
}

//All php applications need this
include_once ('../common/i_common_common.php');

//Initialize global variable
if(!isset($_SESSION['genFileName']))
{
   $_SESSION['genFileName'] = "";
}
?>
