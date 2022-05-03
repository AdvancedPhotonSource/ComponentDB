#!/usr/bin/env python3

import sys
import csv
import click

from cdbApi import UpdateMachineAssignedItemInformation
from cdbApi import ApiException

from rich import print
from cdbCli.common.cli import cliBase

from cdbCli.common.cli.cliBase import CliBase
from cdbCli.service.cli.cdbCliCmnds.setItemLogById import set_item_log_by_id_helper

################################################################################################
#                                                                                              #
#                          Set status if machine child matches item code                       #
#                                                                                              #
################################################################################################
def set_machine_install_state_helper(
    factory, item_api, machine_api, machine_item, item_id, install_state, stdout_done=False, add_item_log=False
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

            if stdout_done:
                print("Updated machine %s for assigned item %s to install state %r" % (machine_item.name, item_id, install_state))
        except ApiException as ex:
            exObj = factory.parseApiException(ex)
            raise Exception("%s - %s" % (exObj.simple_name, exObj.message))
        else:
            if add_item_log:
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
            "Machine item %s assigned item is not %s." 
                % (str(machine_item.name), item_id)
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
    help="Assigned item identifier provided.",
)
@click.option(
    "--installed",
    is_flag=True,
    help="Add this switch to set items as installed otherwise it defaults to planned.",
)
@cliBase.wrap_common_cli_click_options
@click.pass_obj
def set_machine_install_state(cli, input_file, item_id_type, installed, add_log_to_item=False):    
    """Set new status for items if id matches child of machine design, otherwise print mismatch
    to console. Id is specified by type

    \b
    Example (file): set-machine-install-statuses --input-file filename.csv --item-id-type=qr_id
    Example (stdin): cat filename.csv | set-machine-install-statuses
    Example (terminal): set-machine-install-statuses                        
                        <Machine Design Name>,<Item <item-id-type>>"""
    """

        Input is either through a named csv file or through STDIN.   Default is STDIN
        The format of the input data is an intended row to be removed followed by
        <Machine Design Name>,<Item ID>   where the ID is by the type specified by the command line."""
    
    try:
        factory = cli.require_authenticated_api()
    except ApiException:
        print("Unauthorized User/ Wrong Username or Password. Try again.")
        return

    item_api = factory.getItemApi()
    machine_api = factory.getMachineDesignItemApi()    
    
    stdin_msg = "Entry per line: <Machine_Design_Name>,<Assigned_Item_%s>" % item_id_type
    reader, stdin_tty_mode = cli.prepare_cli_input_csv_reader(input_file, stdin_msg)

    # Parse machine design names and item codes
    for row in reader:
        if row.__len__() == 0 and stdin_tty_mode:
            break
        if not row[0]:
            continue

        design_name = row[0]
        item_id = row[1]

        try:
            results = machine_api.get_machine_design_items_by_name(
                design_name
            )

            if results.__len__() > 1:
                print("Skipping machine %s, found %d results with this name" % (design_name, results.__len__()), file=sys.stderr)
                continue
            machine_item = results[0]

            # Get ids if we were given QR Codes
            if item_id_type == "qr_id":
                item_id = str(item_api.get_item_by_qr_id(int(item_id)).id)
        except ApiException as ex:
            exObj = factory.parseApiException(ex)
            print("Skipping machine %s, an error ocurred. %s" % (design_name, exObj.message), file=sys.stderr)
            continue        

        # Update corresponding item statuses, or print potential mismatch
        set_machine_install_state_helper(
            factory, item_api, machine_api, machine_item, 
            item_id, installed, stdout_done=True, add_item_log=add_log_to_item
        )


if __name__ == "__main__":
    set_machine_install_state()
