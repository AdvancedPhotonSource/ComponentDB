#!/usr/bin/env python3

import sys
import re
import csv
from tkinter.messagebox import NO
import click

from cdbApi import SimpleLocationInformation
from cdbApi import ApiException
from rich import print
from cdbCli.common.cli import cliBase

from cdbCli.common.cli.cliBase import CliBase
from cdbCli.service.cli.cdbCliCmnds.setItemLogById import set_item_log_by_id_helper


################################################################################################
#                                                                                              #
#                                 Set new location of item                                     #
#                                                                                              #
################################################################################################
def set_item_location_helper(item_api, item_id, location_id, add_log_to_item=False):
    """
    This function sets a new location for a given inventory item

        :param item_api: Item Api object
        :param item_id: The ID of the inventory item
        :param location_id: The ID of the location item
    """

    # Note:  Parameter validation is was done by click

    # Do not attempt to change a location if we are already at that location
    current_location = item_api.get_item_location(item_id)    
    if current_location.location_item:
        current_location_id = current_location.location_item.id
    else:
        current_location_id = -1

    if current_location_id == location_id:
        print(
            "ItemId: "
            + str(item_id)
            + ", PriorLocationID: "
            + str(current_location_id)
            + ", NewLocationID: "
            + str(location_id)
        )
        return
    # Get the new location and attempt to move the item to that location.
    location = SimpleLocationInformation(
        locatable_item_id=item_id, location_item_id=location_id
    )
    try:
        item_api.update_item_location(simple_location_information=location)
    except ApiException as e:
        p = r'"localizedMessage.*'
        matches = re.findall(p, e.body)
        if matches:
            error = (
                "ItemId: ,"
                + str(item_id)
                + ". Error: Error updating location: "
                + matches[0][:-2]
            )
            print(error)
    else:
        if add_log_to_item:
            log = (
                "ItemId: "
                + str(item_id)
                + ", PriorLocationID: "
                + str(current_location_id)
                + ", NewLocationID: "
                + str(location_id)
            )
            set_item_log_by_id_helper(item_api=item_api, item_id=item_id, log_entry=log)


@click.command()
@click.option(
    "--input-file",
    help="Input csv file with id,location, default is STDIN",
    type=click.File("r"),
    default=sys.stdin,
)
@click.option(
    "--item-id-type",
    default="id",
    type=click.Choice(["id", "qr_id"], case_sensitive=False),
    help="Allowed values are 'id'(default) or 'qr_id'",
)
@click.option(
    "--location-id-type",
    default="name",
    type=click.Choice(["name", "id", "qr_id"], case_sensitive=False),
    help="Allowed values are name(default) 'id' or 'qr_id'",
)
@cliBase.wrap_common_cli_click_options
@click.pass_obj
def set_item_location(cli, input_file, item_id_type, location_id_type, add_log_to_item):
    """Set new location for single or multiple items.  Locations can be specified
    with ids(default) or QRCodes and locations can be specified by name(default),
    QRCodes or ids.

    \b
    Example (file): set-item-location --input-file=filename.csv --item-id-type=qr_id --location-id-type=name
    Example (pipe): cat filename.csv | set-item-location --location-id-type=id
    Example (terminal): set-item-location --location-id-type=qr_id
                        header
                        <insert item id>,<insert location qr_id>

    Input is either through a named file or through STDIN.   Default is STDIN
    The format of the input data is
    <Item ID>,<Location ID>   where the ID is by the type specified by the commandline."""
    
    try:
        factory = cli.require_authenticated_api()
    except ApiException:
        click.echo("Unauthorized User/ Wrong Username or Password. Try again.")
        return

    item_api = factory.getItemApi()
    location_item_api = factory.getLocationItemApi()

    stdin_msg = "Entry per line: <item_%s>,<location_%s>" % (item_id_type, location_id_type)
    reader, stdin_tty_mode = cli.prepare_cli_input_csv_reader(input_file, stdin_msg)    

    # Parse lines of csv
    for row in reader:
        if row.__len__() == 0 and stdin_tty_mode:
            break
        item_id = row[0]
        location_id = row[1]

        # Get ids if we were given QR Codes
        if item_id_type == "qr_id":
            item_id = str(item_api.get_item_by_qr_id(int(item_id)).id)

        # Get the location id if we are given the qr code
        if location_id_type == "qr_id":
            location_id = item_api.get_item_by_qr_id(int(location_id)).id
        if location_id_type == "name":
            location_id = location_item_api.get_location_items_by_name(location_id)[
                0
            ].id

        set_item_location_helper(item_api, item_id, location_id, add_log_to_item)


if __name__ == "__main__":
    set_item_location()
