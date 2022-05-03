#!/usr/bin/env python3

import sys
import re
import csv
import click

from cdbApi import ApiException
from cdbCli.common.cli import cliBase

from cdbCli.common.cli.cliBase import CliBase
from cdbCli.service.cli.cdbCliCmnds.setItemLogById import set_item_log_by_id_helper


################################################################################################
#                                                                                              #
#                                 Set new location of item                                     #
#                                                                                              #
################################################################################################
def set_parent_location_helper(
    item_api,
    loc_item_api,
    location_item_id,
    parent_item_id,
    add_log_to_item=False
):
    """
    This function updates the parent location for a child location.

        :param item_api: Item Api Object
        :param loc_item_api: Location Item Api Object
        :param location_item_id: The ID of the Parent
        :param parent_item_id : New Parent
    """

    try:
        loc_item_api.update_location_parent(location_item_id, parent_item_id)
    except Exception as e:
        click.echo("Error with updating location of item: " + str(location_item_id))
    else:
        if add_log_to_item:
            log = (
                "Location item: "
                + str(location_item_id)
                + " has parent updated to "
                + str(parent_item_id)
            )
            set_item_log_by_id_helper(
                item_api=item_api, item_id=location_item_id, log_entry=log
            )        


@click.command()
@click.option(
    "--input-file",
    help="Input csv file with new location parameters, see help, default is STDIN",
    type=click.File("r"),
    default=sys.stdin,
)
@click.option(
    "--item-id-type",
    default="id",
    type=click.Choice(["id", "qr_id"], case_sensitive=False),
    help="Allowed values are 'id'(default) or 'qr_id'",
)
@cliBase.wrap_common_cli_click_options
@click.pass_obj
def set_parent_location(cli, input_file, item_id_type, add_log_to_item):
    """Essentially moves one location under another (parent).

    \b
    Example (file): set-parent-location --input-file=filename.csv --item-id-type=qr_id
    Example (pipe): cat filename.csv | set-parent-location
    Example (terminal): set-parent-location
                        header
                        <Insert Location ID>,<Insert Parent ID>

    CSV input is on STDIN(default) or a file and the csv format is
    File has the format
    <location ID>,<parent id>"""
    
    try:
        factory = cli.require_authenticated_api()
    except ApiException:
        click.echo("Unauthorized User/ Wrong Username or Password. Try again.")
        return

    item_api = factory.getItemApi()
    loc_item_api = factory.getLocationItemApi()

    stdin_msg = "Entry per line: <item_%s>,<location_parent_id>" % (item_id_type)
    reader, stdin_tty_mode = cli.prepare_cli_input_csv_reader(input_file, stdin_msg)    

    # Parse lines of csv
    for row in reader:
        if row.__len__() == 0 and stdin_tty_mode:
            break
        if not row[0]:
            continue

        loc_id = int(row[0])
        parent_id = int(row[1])

        # Get ids if we were given QR Codes
        if item_id_type == "qr_id":
            loc_id = item_api.get_item_by_qr_id(int(loc_id)).id

        set_parent_location_helper(item_api, loc_item_api, loc_id, parent_id, add_log_to_item)


if __name__ == "__main__":
    set_parent_location()
