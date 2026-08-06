#!/usr/bin/env python3

from email.policy import default
import time
import click

from rich.console import Console
from rich.tree import Tree

from CdbApiFactory import CdbApiFactory

from cdbApi.api.item_api import ItemApi
from cdbApi.models.item_hierarchy import ItemHierarchy
from cdbApi.models.item import Item
from cdbApi.models.item_location_information import ItemLocationInformation
from cdbApi.models.machine_design_connector_list_object import MachineDesignConnectorListObject
from cdbCli.common.cli import cliBase
from cdbCli.common.cli.cliBase import CliBase

CMD_HELP_MESSAGE="Displays Information about a specific CDB component, including basic details, properties, logs, relationships, etc."

CABLE_DESIGN_PROPERTY_TYPE_NAME="cable_design_internal_property_type"

INVENTORY_FULL_OPT = "Full"
INVENTORY_SPARES_OPT = "Spare"
INVENTORY_LIST_OPTS = [INVENTORY_SPARES_OPT, INVENTORY_FULL_OPT]

@cliBase.cli_command_api_exception_handler
def cdbInfo_helper(factory: CdbApiFactory, 
                all=False, 
                inventory_mode=INVENTORY_SPARES_OPT, 
                log_limit=-1,                 
                item_id=None, 
                qr=None,
                # Optional, but used for printing exception
                console=None,
                format=None):
    
    item_api = factory.getItemApi()
    machine_api = factory.getMachineDesignItemApi() 
    cable_design_api = factory.getCableDesignItemApi()
    domain_api = factory.getDomainApi()
    location_api = factory.getLocationItemApi()
    property_value_api = factory.getPropertyValueApi()
    
    if qr:
        item = item_api.get_item_by_qr_id(qr)
        item_id = item.id
    else:
        item = item_api.get_item_by_id(item_id)

    domain = domain_api.get_domain_by_id(item.domain_id)

    result_obj = {} 
    header_style = {}
    
    # Load Details
    item_details = []
    result_obj["Item Details"] = item_details
    header_style["Item Details"] = "green"

    item_details.append({"Id": item.id})
    item_details.append({"Name": item.name})
    if domain.item_identifier1_label:
        item_details.append({domain.item_identifier1_label: item.item_identifier1})
    if all:            
        if domain.item_identifier2_label:
            item_details.append({domain.item_identifier2_label: item.item_identifier2})
        if domain.item_category_label:
            item_category_str = cliBase.simple_obj_list_to_str(item.item_category_list)
            item_details.append({domain.item_category_label: item_category_str})
        if domain.item_type_label:
            item_type_str = cliBase.simple_obj_list_to_str(item.item_type_list)
            item_details.append({domain.item_type_label: item_type_str})
        if item.item_project_list:
            project_str = cliBase.simple_obj_list_to_str(item.item_project_list)
            item_details.append({"Project": project_str})
        item_details.append({"Domain": domain.name})
        item_details.append({"Description": item.description})
    url = factory.generateCDBUrlForItemId(item_id)
    item_details.append({"URL": url})

    # Inventory or cable inventory attributes
    if item.domain_id == factory.INVENTORY_DOMAIN_ID or item.domain_id == factory.CABLE_INVENTORY_DOMAIN_ID:
        status = item_api.get_item_status(item_id)
        status_str = status.value
        item_details.append({"Status": status_str})

        item_details.append({"Catalog Item": item.derived_from_item.name})
        item_details.append({"Catalog Id": item.derived_from_item.id})

        # Fetch Location
        location = __get_inventory_location(item_api, item.id)
        item_details.append({"Location/Housing:": location})            
        
    if item.domain_id == factory.INVENTORY_DOMAIN_ID or item.domain_id == factory.MACHINE_DESIGN_DOMAIN_ID:
        if all:
            if item.domain_id == factory.INVENTORY_DOMAIN_ID:
                machine = __get_machine_inventory_installed_in(item_api, item)
            else:
                machine = item

            if machine:
                hierarchies = machine_api.get_control_hierarchy_for_machine_element(machine.id)
                for i, hierarchy in enumerate(hierarchies):
                    control_hierarchy_str = ""
                    if hierarchy.child_item:
                        while hierarchy:
                            machine_name = hierarchy.machine_item.name
                            if format and format == cliBase.FORMAT_RICH_OPT:
                                if machine_name == item.name:
                                    machine_name = "[green]%s[/green]" % machine_name
                            
                            control_hierarchy_str += "%s" + machine_name
                            if hierarchy.child_item:
                                control_hierarchy_str += " ➜ "
                            if hierarchy.interface_to_parent: 
                                interface_addon = "(%s) ➜ " % hierarchy.interface_to_parent
                                control_hierarchy_str = control_hierarchy_str % interface_addon
                            else:
                                control_hierarchy_str = control_hierarchy_str % ""; 

                            hierarchy = hierarchy.child_item

                    item_details.append({"Control %d" % (i + 1): control_hierarchy_str})
                housing_hierarchy : ItemHierarchy = machine_api.get_housing_hierarchy_by_id(machine.id)
                if housing_hierarchy:
                    housing_hierarchy_str = ""                    
                    hh = housing_hierarchy
                    while hh is not None: 
                        machine_name = hh.item.name
                        if format and format == cliBase.FORMAT_RICH_OPT:
                            if machine_name == item.name:
                                housing_hierarchy_str += "[green]%s[/green]" % machine_name
                            else:
                                housing_hierarchy_str += machine_name
                        else:
                            housing_hierarchy_str += machine_name
                        
                        if hh.child_items:
                            housing_hierarchy_str += " ➜ "
                            hh = hh.child_items[0]
                        else:
                            hh = None

                    item_details.append({"Housing": housing_hierarchy_str})
                location : ItemLocationInformation = item_api.get_item_location(machine.id)
                if location:
                    item_details.append({"Location": location.location_string})                    

    if all and item.domain_id == factory.MACHINE_DESIGN_DOMAIN_ID:
        machine_item = machine_api.get_machine_design_item_by_id(item_id)
        machine_conn_list : list[MachineDesignConnectorListObject] = machine_api.get_machine_design_connector_list(item_id)
        conn_list = []
        result_obj['Cable Connections'] = conn_list
        header_style['Cable Connections'] = 'magenta'
        
        for machine_conn in machine_conn_list:
            if len(machine_conn.connected_cables) > 0:
                cable: Item = machine_conn.connected_cables[0]

                conn = {}
                conn['Cable'] = cable.name
                conn['Cable Id'] = cable.id
                conn['Connected Machine(s)'] = machine_conn.connected_to_items_string                    
                conn['Port Name'] = machine_conn.connector_name

                conn_list.append(conn)        

        if machine_item.assigned_item:
            assigned_item = machine_item.assigned_item
            catalog_item = None
            catalog_item_str = ""
            inventory_item_str = ""
            if assigned_item:
                if assigned_item.derived_from_item:
                    inventory_item_str = "%s/%s" % (assigned_item.name, assigned_item.qr_id)
                    catalog_item = assigned_item.derived_from_item
                else:
                    catalog_item = assigned_item                    

                catalog_item_str =  "%s/%s" % (catalog_item.name, catalog_item.id)
                
            item_details.append({"Catalog Item (Name/ID)": catalog_item_str})
            item_details.append({"Inventory Item (Tag/QRID)": inventory_item_str})

            if inventory_item_str != "":
                install_state = "Installed"
                if not machine_item.is_housed:
                    install_state = "Planned"

                item_details.append({"Install Status": install_state})                    

    if all and item.domain_id == factory.CABLE_DESIGN_DOMAIN_ID:
        cable_conn_list = cable_design_api.get_cable_design_connection_list(item_id)
        conn_list = []
        result_obj['Cable Endpoints'] = conn_list
        header_style['Cable Endpoints'] = 'magenta'

        for cable_conn in cable_conn_list:
            conn = {} 
            conn['Machine'] = cable_conn.md_item_name
            conn['Machine Id'] = cable_conn.md_item.id
            conn['Connector'] = cable_conn.md_connector_name

            conn_list.append(conn)


    # Load Logs
    if all:
        logs = []
        item_logs = item_api.get_logs_for_item(item_id)
        for i, item_log in enumerate(item_logs):
            if log_limit != -1 and i == log_limit:
                break

            log = {}
            log['Id'] = item_log.id
            log['Text'] = item_log.text
            log["User"] = item_log.entered_by_username
            if item_log.effective_from_date_time:
                log['Date'] = item_log.effective_from_date_time.date()
            else:
                log['Date'] = item_log.entered_on_date_time.date()

            logs.append(log)

        result_obj["Logs"] = logs
        header_style["Logs"] = 'yellow'

    # Load Properties 
    if all:
        properties = []
        item_properties = item_api.get_properties_for_item(item_id)
        for item_property in item_properties:
            if item_property.property_type.is_internal:
                continue
            property = {}
            property_type = item_property.property_type
            property_type_name = property_type.name
            property['Type'] = property_type_name
            property['Tag'] = item_property.tag
            property['Value'] = item_property.value
            property['Description'] = item_property.description

            properties.append(property)

        result_obj["Properties"] = properties
        header_style["Properties"] = "blue"

        if item.domain_id == factory.CABLE_DESIGN_DOMAIN_ID:
            for item_property in item_properties:
                if item_property.property_type.name == CABLE_DESIGN_PROPERTY_TYPE_NAME:
                    metadata_list = property_value_api.get_property_value_metadata(item_property.id)
                    for metadata in metadata_list:
                        if (metadata.metadata_value):
                            new_detail = {}
                            new_detail[metadata.metadata_key] = metadata.metadata_value

                            item_details.append(new_detail)

    # Load Inventory & Catalog specific attributes
    if item.domain_id == factory.CATALOG_DOMAIN_ID or item.domain_id == factory.CABLE_CATALOG_DOMAIN_ID:
        inventories = []
        item_inventories = item_api.get_items_derived_from_item_by_item_id(item_id)
        item_details.append({"# Inventory": len(item_inventories)})
        spare_ctr = 0
        mds = []
        __update_in_machine_list(item_api, item, mds)
        add_all_inventory = inventory_mode == INVENTORY_FULL_OPT
                    
        if item_inventories.__len__():
            inventory_domain_id = item_inventories[0].domain_id
            inventory_domain = domain_api.get_domain_by_id(inventory_domain_id)

            for item_inventory in item_inventories:
                __update_in_machine_list(item_api, item_inventory, mds)
                status = item_api.get_item_status(item_inventory.id)
                status_str = status.value
                spare = False
                if 'spare' in status_str.lower():
                    spare = True
                    spare_ctr += 1

                if all and (add_all_inventory or spare):
                    inventory = {}
                    inventory['Id'] = item_inventory.id
                    inventory['Tag'] = item_inventory.name    
                    inventory['QrId'] = item_inventory.qr_id
                    id1_label = inventory_domain.item_identifier1_label
                    if id1_label:
                        inventory[id1_label] = item_inventory.item_identifier1
                    inventory['Status'] = status_str

                    inventory["Location"] = __get_inventory_location(item_api, item_inventory.id)
                    
                    inventories.append(inventory)

        item_details.append({'# Spare': spare_ctr})
        if item.domain_id == factory.CATALOG_DOMAIN_ID:
            item_details.append({"# MD Occurrences": len(mds)})
            if all:
                result_obj["Machine Occurrences"] = mds
                header_style["Machine Occurrences"] = "magenta"

        if all:
            result_obj["%s Inventory" % inventory_mode] = inventories
    
    # Load location specific attributes
    if all and item.domain_id == factory.LOCATION_DOMAIN_ID:
        inventory_items = location_api.get_inventory_located_here(item_id)

        items_here = []
        result_obj['Inventory Here'] = items_here
        header_style['Inventory Here'] = "magenta"

        for inventory_item in inventory_items:
            item_here = {}
            item_here["Id"] = inventory_item.id
            item_here['Item'] = __get_inventory_to_string(inventory_item)
            domain = 'Inventory'
            if inventory_item.domain_id == factory.CABLE_INVENTORY_DOMAIN_ID:
                domain = "Cable %s" % domain

            item_here['Domain'] = domain
            item_here['QrId'] = inventory_item.qr_id
            items_here.append(item_here)            
            
    return (result_obj, header_style)

def wrap_cli_specific_click_options(include_id=True, help_addition=''):
    def wrapper(f):
        if include_id:
            f=click.option(
                "--id",                
                help="id of the item. %s" % help_addition,
            )(f)
            f=click.option(
                "--qr",
                help="qrid of the item. %s" % help_addition,
            )(f)
            
        f=click.option(
            "--all",   
            help="Display all data about the item. %s" % help_addition,
            is_flag=True
        )(f)
        f=click.option(
            "--inventory-mode",
            type=click.Choice(INVENTORY_LIST_OPTS, case_sensitive=False),
            default=INVENTORY_SPARES_OPT,
            help="To be used with --all switch, the inventory list can be switched to a full list. %s" % help_addition
        )(f)
        f=click.option(
            "--log-limit",    
            default=5,
            help="How many logs can be shown at one time. Use -1 for all logs. %s" % help_addition
        )(f)
        return f
    return wrapper

@click.command(help=CMD_HELP_MESSAGE)
@wrap_cli_specific_click_options()
@click.option(
    "--pager",   
    help="Scrolling of results similar to opening a text file with less.",
    is_flag=True
)
@cliBase.wrap_print_format_cli_click_options
@click.pass_obj
def cdb_info(cli: CliBase, id, qr, pager, all, inventory_mode, log_limit, format):
    """Fetch Info about an item in CDB

    Example: cdbInfo --id=123  [--pager]
    """
    if not cli:
        cli = CliBase()

    if id is not None and qr is not None:
        raise click.UsageError("Only qrid or id can be specified not both.")
    elif id is None and qr is None:
        raise click.UsageError("Missing param, qrid or id must be specified.")

    console = Console()
    with console.status("Loading Item Details...", spinner="aesthetic"):
        factory = cli.require_api()
        result_obj, header_style = cdbInfo_helper(factory=factory, 
                                            all=all,
                                            item_id=id, 
                                            qr=qr, 
                                            inventory_mode=inventory_mode, 
                                            log_limit = log_limit,
                                            console=console,
                                            format=format)

    cliBase.print_results(console, result_obj, format, pager, header_style=header_style)

def __get_machine_inventory_installed_in(item_api, item):
    mds = []
    __update_in_machine_list(item_api, item, mds, include_md_item=True)

    if len(mds) == 1:
        return mds[0]['md_item']
    return None

def __update_in_machine_list(item_api: ItemApi, item, mds, include_md_item=False):
    memberships = item_api.get_item_memberships(item.id)
    
    for membership in memberships:
        part_item = membership.part_of_item         
        if part_item.domain_id == CdbApiFactory.MACHINE_DESIGN_DOMAIN_ID:
            md_occurrence = {}
            if include_md_item:
                md_occurrence['md_item'] = part_item
            md_occurrence['Machine'] = part_item.name
            md_occurrence['Assigned Item'] = item.name
            mds.append(md_occurrence)                

def __get_inventory_location(item_api, inventory_id):
    location = item_api.get_item_location(inventory_id)
    if location.housing_item:
        housing_item = location.housing_item
        if housing_item.derived_from_item:
            return __get_inventory_to_string(housing_item)
        else:
            return housing_item.name
    else:
        return location.location_string

def __get_inventory_to_string(inventory_item):
    housing_format_str = "%s - [%s]"
    return housing_format_str % (inventory_item.derived_from_item.name, inventory_item.name)

if __name__ == '__main__':
    cdb_info()
                                             