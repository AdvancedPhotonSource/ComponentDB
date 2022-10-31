#!/usr/bin/env python3
from shutil import which

from rich.console import Console
from rich.table import Table
from rich.panel import Panel

from cdbCli.common.utility.configurationManager import ConfigurationManager
from cdbCli.service.cli.cdbCliCmnds.info import CMD_HELP_MESSAGE as cdbInfo_help_msg
from cdbCli.service.cli.cdbCliCmnds.search import CMD_HELP_MESSAGE as cdbSearch_help_msg

def create_help_rich_table(table=None):
    """
    Create a help tale for printing with rich.console Console.

    table: Two column table when this command is used for showing help for other CLI utilities that include this CLI. 
    """
    if not table:
        cm = ConfigurationManager.get_instance()
        deployment = cm.get_portal_address()

        table = Table.grid(padding=1, pad_edge=True)
        table.title = "CDB CLI Help [%s]" % deployment

        table.add_column("Command", no_wrap=True, justify="left", style="green", min_width=16)
        table.add_column("Description")

    table.add_row(
        "cdbInfo",
        cdbInfo_help_msg
    )   

    table.add_row(
        "cdbSearch", 
        cdbSearch_help_msg
    )

    table.add_row(
        "cdb-cli", 
        "Entry point for all of of the other cdb command line utilities."        
    )

    return table


def showHelp():
    table = create_help_rich_table()

    table.add_row(
        "cdbHelp",
        "Shows this screen and lists all cdb command line utilities."
    )

    console = Console()
    console.print(table)

if __name__ == '__main__':
    showHelp()