#!/usr/bin/env python3

import sys
import csv
import click

from cdbCli.common.cli.cliBase import CliBase
from cdbCli.service.cli.CDBclickCmnds.setItemStatusById import set_item_status_by_id_help

################################################################################################
#                                                                                              #
#                          Set status if machine child matches item code                       #
#                                                                                              #
################################################################################################
@click.command()
@click.option('--input-file', help='Input csv file with item name,qr_id default is STDIN',
              type=click.File('r'), default=sys.stdin)
@click.option('--item-id-type', default='id',
              type=click.Choice(["id", "qr_id"], case_sensitive=False),
              help="Allowed values are 'id'(default) or 'qr_id'")
@click.option('--status', help='Desired updated item status in CDB, default is "Planned"')
@click.option('--dist', help='Change the CDB distribution (as provided in cdb.conf)')
def set_machine_install_statuses(input_file, item_id_type, status="Planned", dist=None):

    """Set new status for items if id matches child of machine design, otherwise print mismatch
        to console. Id is specified by type

        \b
        Example (file): set_machine_install_statuses --input-file PlannedMachineElements.csv --item-id-type=qr_id"""
    """Gets the corresponding id from an item name

       Input is either through a named csv file or through STDIN.   Default is STDIN
       The format of the input data is an intended row to be removed followed by
       <Machine Design Name>,<Item ID>   where the ID is by the type specified by the command line."""

    cli = CliBase(dist)
    factory = cli.require_authenticated_api()
    item_api = factory.getItemApi()
    machine_api = factory.getMachineDesignItemApi()

    reader = csv.reader(input_file)

    # Removes header if located in first row
    next(reader)

    # Parse machine design names and item codes
    for row in reader:
        if not row[0]:
            continue

        design_name = row[0]
        item_id = row[1]
        machine_item = machine_api.get_machine_design_items_by_name(design_name).__getitem__(0)

        # Get ids if we were given QR Codes
        ids = item_id.split(",")
        if item_id_type == "qr_id":
            ids_from_qr_ids = [str(item_api.get_item_by_qr_id(int(qr_id)).id) for qr_id in ids]
            initialized_str = ","
            item_id = initialized_str.join(ids_from_qr_ids)

        # Update corresponding item statuses, or print potential mismatch
        if str(machine_item.assigned_item.id) == item_id:
            set_item_status_by_id_help(item_id, status, cli)

        else:
            print("Following machine design and " + item_id_type + "code does not match " + row)

    print("Statuses of desired items have be set to " + status)

if __name__ == "__main__":
    set_machine_install_statuses()

