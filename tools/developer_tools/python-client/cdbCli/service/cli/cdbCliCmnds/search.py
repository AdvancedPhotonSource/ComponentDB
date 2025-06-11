#!/usr/bin/env python3

from ast import Interactive
from tkinter.messagebox import NO
from unittest import case
import click

from rich import print
from rich.table import Table
from rich.console import Console
from CdbApiFactory import CdbApiFactory
from cdbApi import ApiException

from cdbApi.api.search_api import SearchApi
from cdbApi.models.search_entities_options import SearchEntitiesOptions
from cdbApi.models.search_entities_results import SearchEntitiesResults
from cdbCli.common.cli import cliBase
from cdbCli.service.cli.cdbCliCmnds.info import cdbInfo_helper

from cdbCli.common.cli.cliBase import CliBase


CMD_HELP_MESSAGE="Seraches CDB for a list of matching components with options for domains and interacitve mode that automatically can fetch cdbInfo."

DOMAIN_ALL_OPT = "All"
DOMAIN_CATALOG_OPT = "Catalog"
DOMAIN_CABLE_CATALOG_OPT = "Cable Catalog"
DOMAIN_INVENTORY_OPT = "Inventory"
DOMAIN_CABLE_INVENTORY_OPT = "Cable Inventory"
DOMAIN_MACHINE_OPT = "Machine Design"
DOMAIN_CABLE_DESIGN_OPT = "Cable Design"
DOMAIN_LOCATION_OPT = "Location"
DOMAIN_MAARC_OPT = "MAARC"

DOMAIN_OPTS = [
    DOMAIN_ALL_OPT, 
    DOMAIN_CATALOG_OPT,
    DOMAIN_CABLE_CATALOG_OPT, 
    DOMAIN_INVENTORY_OPT, 
    DOMAIN_CABLE_INVENTORY_OPT,
    DOMAIN_MACHINE_OPT,
    DOMAIN_CABLE_DESIGN_OPT,
    DOMAIN_LOCATION_OPT, 
    DOMAIN_MAARC_OPT]

RESULT_BACK_OPT = "Back"
RESULT_SELECT_OPT = "Select for details"
RESULT_SELECT_W_ALL_OPT = "Select for details /w all opt"
RESULT_RELOAD_OPT = "Reload"
RESULT_OPTS = [RESULT_BACK_OPT, RESULT_SELECT_OPT, RESULT_SELECT_W_ALL_OPT, RESULT_RELOAD_OPT]

TABLE_STYLE = [None, 'green', 'magenta', 'cyan']

@cliBase.cli_command_api_exception_handler
def search_helper(factory: CdbApiFactory, console: Console, search_string, search_domain, format, pager=False):    
    with console.status("Waiting for search results...", spinner="aesthetic"):        
        search_api: SearchApi = factory.getSearchApi()
        opts = SearchEntitiesOptions(search_text=search_string)

        if search_domain == DOMAIN_ALL_OPT:
            opts.include_catalog = True
            opts.include_inventory = True
            opts.include_machine_design = True
            opts.include_cable_catalog = True
            opts.include_cable_design = True
            opts.include_cable_inventory = True
            opts.include_item_location = True
            opts.include_maarc = True
        if search_domain == DOMAIN_CATALOG_OPT:
            opts.include_catalog = True
        if search_domain == DOMAIN_INVENTORY_OPT:
            opts.include_inventory = True
        if search_domain == DOMAIN_MACHINE_OPT:
            opts.include_machine_design = True
        if search_domain == DOMAIN_CABLE_CATALOG_OPT:
            opts.include_cable_catalog = True
        if search_domain == DOMAIN_CABLE_INVENTORY_OPT:
            opts.include_cable_inventory = True
        if search_domain == DOMAIN_CABLE_DESIGN_OPT:
            opts.include_cable_design = True
        if search_domain == DOMAIN_LOCATION_OPT:
            opts.include_item_location = True
        if search_domain == DOMAIN_MAARC_OPT:
            opts.include_maarc = True
        
        results: SearchEntitiesResults = search_api.search_entities(search_entities_options=opts)         

    resulting_print_obj_list = {}

    if search_domain == DOMAIN_ALL_OPT or search_domain == DOMAIN_CATALOG_OPT:
        resulting_print_obj_list["%s Results" % DOMAIN_CATALOG_OPT] = create_search_results_printout(
            result_list=results.item_domain_catalog_results,
            factory=factory
        )
    if search_domain == DOMAIN_ALL_OPT or search_domain == DOMAIN_INVENTORY_OPT:
        resulting_print_obj_list["%s Results" % DOMAIN_INVENTORY_OPT] = create_search_results_printout(
            result_list=results.item_domain_inventory_results,
                                        factory=factory
        )
    if search_domain == DOMAIN_ALL_OPT or search_domain == DOMAIN_MACHINE_OPT:
        resulting_print_obj_list["%s Results" % DOMAIN_MACHINE_OPT] = create_search_results_printout(
            result_list=results.item_domain_machine_design_results,
            factory=factory
        )
    if search_domain == DOMAIN_ALL_OPT or search_domain == DOMAIN_CABLE_CATALOG_OPT:
        resulting_print_obj_list["%s Results" % DOMAIN_CABLE_CATALOG_OPT] = create_search_results_printout(
            result_list=results.item_domain_cable_catalog_results,
            factory=factory
        )
    if search_domain == DOMAIN_ALL_OPT or search_domain == DOMAIN_CABLE_INVENTORY_OPT:
        resulting_print_obj_list["%s Results" % DOMAIN_CABLE_INVENTORY_OPT] = create_search_results_printout(
            result_list=results.item_domain_cable_inventory_results,
            factory=factory
        )
    if search_domain == DOMAIN_ALL_OPT or search_domain == DOMAIN_CABLE_DESIGN_OPT:
        resulting_print_obj_list["%s Results" % DOMAIN_CABLE_DESIGN_OPT] = create_search_results_printout(
            result_list=results.item_domain_cable_design_results,
            factory=factory
        )
    if search_domain == DOMAIN_ALL_OPT or search_domain == DOMAIN_LOCATION_OPT:
        resulting_print_obj_list["%s Results" % DOMAIN_LOCATION_OPT] = create_search_results_printout(
            result_list=results.item_domain_location_results,
            factory=factory
        )
    if search_domain == DOMAIN_ALL_OPT or search_domain == DOMAIN_MAARC_OPT:
        resulting_print_obj_list["%s Results" % DOMAIN_MAARC_OPT] = create_search_results_printout(
            result_list=results.item_domain_maarc_results,
            factory=factory
        )
    
    cliBase.print_results(console, resulting_print_obj_list, format, pager, table_style=TABLE_STYLE)

    return results


@click.command(help=CMD_HELP_MESSAGE)
@click.argument('search_string', required=False)
@click.option(
    "--pager",   
    help="Scrolling of results similar to opening a text file with less.",
    is_flag=True
)
@click.option(
    "--interactive",   
    help="Allows user to interactively select domain and select item for details. '--search-domain' is ignored with this option.",
    is_flag=True
)
@click.option(
    "--search-domain",
    default=None,
    type=click.Choice(DOMAIN_OPTS, case_sensitive=False),
    help="Domain to search.",
)
@cliBase.wrap_print_format_cli_click_options
@click.pass_obj
def cdb_search(cli: CliBase, search_string, search_domain, pager, interactive, format):
    """Search CDB for items

    \b
    * For multi-word entries enclose entry in ""
    * Wildcard (?): Use ? as any single character
    * Wildcard (*): Use * to capture any amount of characters


    Example: cdbSearch ”<stringWithWildCards>”  [--pager]
    """        
    if not cli:
        cli = CliBase()

    console = Console()
    factory = cli.require_api()

    if search_string is None:        
        search_string = click.prompt("Search String")

    proceed = None
    search_res = None

    while interactive:
        if search_domain is None:
            search_domain = select_item([(domain, domain) for domain in DOMAIN_OPTS + ["Exit"]], console)
            if search_domain is None:
                search_domain = DOMAIN_ALL_OPT
            if search_domain == "Exit":
                exit()        

        if search_res is None:
            search_res : SearchEntitiesResults = search_helper(factory=factory, console=console, search_string=search_string, search_domain=search_domain, format=format, pager=pager)

        if isinstance(search_res, Exception):
            # Standard CLI exception handled and displayed to user. 
            exit(1)

        if proceed is None:
            proceed = select_item([(opt, opt) for opt in RESULT_OPTS + ["Exit"]], console)
            if proceed == "Exit":
                exit()
        
        if proceed == RESULT_BACK_OPT:
                search_res = None
                search_domain = None
                proceed = None
                continue
        elif proceed == RESULT_RELOAD_OPT:
            search_res = None
            proceed = None
            continue
        
        searched_items = []
        if search_domain == DOMAIN_ALL_OPT or search_domain == DOMAIN_CATALOG_OPT:
            cat_items = search_res.item_domain_catalog_results
            searched_items += cat_items
        if search_domain == DOMAIN_ALL_OPT or search_domain == DOMAIN_INVENTORY_OPT:
            inv_items = search_res.item_domain_inventory_results
            searched_items += inv_items
        if search_domain == DOMAIN_ALL_OPT or search_domain == DOMAIN_MACHINE_OPT:
            machine_items = search_res.item_domain_machine_design_results
            searched_items += machine_items
        if search_domain == DOMAIN_ALL_OPT or search_domain == DOMAIN_CABLE_CATALOG_OPT:
            items = search_res.item_domain_cable_catalog_results
            searched_items += items
        if search_domain == DOMAIN_ALL_OPT or search_domain == DOMAIN_CABLE_INVENTORY_OPT:
            items = search_res.item_domain_cable_inventory_results
            searched_items += items
        if search_domain == DOMAIN_ALL_OPT or search_domain == DOMAIN_CABLE_DESIGN_OPT:
            items = search_res.item_domain_cable_design_results
            searched_items += items
        if search_domain == DOMAIN_ALL_OPT or search_domain == DOMAIN_LOCATION_OPT:
            items = search_res.item_domain_location_results
            searched_items += items
        if search_domain == DOMAIN_ALL_OPT or search_domain == DOMAIN_MAARC_OPT:
            items = search_res.item_domain_maarc_results
            searched_items += items

        all_opt = proceed == RESULT_SELECT_W_ALL_OPT
                    
        item_choices = []
        for item in searched_items:
            choice = (str(item.object_id) + "- " + item.object_name, item)
            item_choices.append(choice)

        prev_inx = 0
        next_inx = 0
        item_selection = None

        while True:
            if item_selection is None:
                next_inx = prev_inx + 8
                itr_choices = item_choices[prev_inx:next_inx]
                prev_inx = next_inx
                if next_inx >= len(item_choices):
                    itr_choices.append(("Start Over", None))
                    prev_inx = 0
                    next_inx = 0                    
                else:
                    itr_choices.append(("Next Page", None))
        
            item_selection = select_item(itr_choices, console)

            if item_selection is not None:                
                (cdbInfo_result, cdbInfo_header) = cdbInfo_helper(factory=factory, item_id=item_selection.object_id, all=all_opt, format=format)
                cliBase.print_results(console, cdbInfo_result, format, pager, header_style=cdbInfo_header)
                if isinstance(cdbInfo_result, Exception):
                    # Standard CLI exception handled and displayed to user. 
                    exit(1)
    else:
        if search_domain == None:
            search_domain = DOMAIN_ALL_OPT
        search_helper(factory=factory, console=console, search_string=search_string, search_domain=search_domain, format=format, pager=pager)    

def select_item(itr_choices, console):
    for index, (name, _) in enumerate(itr_choices):
        console.print(f"[bold]{index}[/bold]: [default]{name}[/default]")
    item_index = click.prompt(
        "Select Item by Index",
        type=int,
        show_choices=False
    )

    item_selection = itr_choices[item_index][0] if 0 <= item_index < len(itr_choices) else None     

    return next((value for name, value in itr_choices if name == item_selection), None)

def create_search_results_printout(result_list, factory):   
    result_obj = []

    for result in result_list:  
        obj = {}
        url = factory.generateCDBUrlForItemId(result.object_id)
        match: str = result.display
        match = match.replace('; ', '\n')

        obj["Id"] = result.object_id
        obj['Name'] = result.object_name
        obj["Match Description"] = match
        obj["URL"] = url

        result_obj.append(obj)
    
    return  result_obj

if __name__ == "__main__":
    cdb_search()