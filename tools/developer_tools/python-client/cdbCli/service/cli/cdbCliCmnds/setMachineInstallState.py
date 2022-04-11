#!/usr/bin/env python3

import sys
import csv
import click

from cdbApi import UpdateMachineAssignedItemInformation
from cdbApi import ApiException

from rich import print

from cdbCli.common.cli.cliBase import CliBase
from cdbCli.service.cli.cdbCliCmnds.setItemLogById import set_item_log_by_id_helper

################################################################################################
#                                                                                              #
#                          Set status if machine child matches item code                       #
#                                                                                              #
################################################################################################
def set_machine_install_state_helper(
    factory, item_api, machine_api, machine_item, item_id, install_state
):

    machine_item_child_id = machine_item.assigned_item.id
    if machine_item_child_id == int(item_id):
        try:
            machine_item_info = UpdateMachineAssignedItemInformation(
                md_item_id=machine_item.id,
                assigned_item_id=item_id,
                is_installed=install_state,
            )
            machine_api.update_assigned_item(machine_item_info)
        except ApiException as ex:
            exObj = factory.parseApiException(ex)
            raise Exception("%s - %s" % (exObj.simple_name, exObj.message))
        else:
            log = (
                "Machine item: "
                + str(machine_item.id)
                + " has install state set to "
                + str(install_state)
            )
            set_item_log_by_id_helper(
                item_api=item_api, item_id=machine_item.id, log_entry=log
            )
    else:
        print(
            "Machine item: "
            + str(machine_item.id)
            + " does not have child id: "
            + item_id
        )


@click.command()
@click.option(
    "--input-file",
    help="Input csv file with design_name,qr_id default is STDIN",
    type=click.File("r"),
    default=sys.stdin,
)
@click.option(
    "--item-id-type",
    default="id",
    type=click.Choice(["id", "qr_id"], case_sensitive=False),
    help="Allowed values are 'id'(default) or 'qr_id'",
)
@click.option("--dist", help="Change the CDB distribution (as provided in cdb.conf)")
def set_machine_install_state(input_file, item_id_type, dist=None):

    """Set new status for items if id matches child of machine design, otherwise print mismatch
    to console. Id is specified by type

    \b
    Example (file): set-machine-install-statuses --input-file filename.csv --item-id-type=qr_id
    Example (stdin): cat filename.csv | set-machine-install-statuses
    Example (terminal): set-machine-install-statuses
                        header
                        <Machine Design Name>,<Item ID>"""
    """

        Input is either through a named csv file or through STDIN.   Default is STDIN
        The format of the input data is an intended row to be removed followed by
        <Machine Design Name>,<Item ID>   where the ID is by the type specified by the command line."""

    cli = CliBase(dist)

    try:
        factory = cli.require_authenticated_api()
    except ApiException:
        print("Unauthorized User/ Wrong Username or Password. Try again.")
        return

    item_api = factory.getItemApi()
    machine_api = factory.getMachineDesignItemApi()

    reader = csv.reader(input_file)

    # Removes header located in first row
    next(reader)

    # Parse machine design names and item codes
    for row in reader:
        if not row[0]:
            continue

        design_name = row[0]
        item_id = row[1]
        machine_item = machine_api.get_machine_design_items_by_name(
            design_name
        ).__getitem__(0)

        # Get ids if we were given QR Codes
        if item_id_type == "qr_id":
            item_id = str(item_api.get_item_by_qr_id(int(item_id)).id)

        # Update corresponding item statuses, or print potential mismatch
        set_machine_install_state_helper(
            factory, item_api, machine_api, machine_item, item_id, False
        )


if __name__ == "__main__":
    set_machine_install_state()
