#!/usr/bin/env python3

import re
import click
import csv
import sys

from cdbApi import ItemStatusBasicObject
from cdbApi import ApiException


##############################################################################################
#                                                                                            #
#                              Update item status given item ID                              #
#                                                                                            #
##############################################################################################
def set_item_status_by_id_helper(item_api, prop_type_api, item_id, status):
    """
    This function updates the status of a given item.

        :param item_api: item api object
        :param prop_type_api: property type api
        :param item_id: ID of the inventory item. Multiple IDs can be given as a comma separated string
        :param status: new status of the item. Only statuses from "statusDict.json" are allowed as parameters
    """

    status_prop = prop_type_api.get_inventory_status_property_type()

    status_list = [
        status.to_dict()["value"]
        for status in status_prop.sorted_allowed_property_value_list
    ]

    if status == "?":
        click.echo("Status Options:")
        click.echo("----------------")
        for stat in status_list:
            click.echo(stat)
        return

    try:
        item_status = ItemStatusBasicObject(status=status)

        try:
            item_api.update_item_status(
                item_id=item_id, item_status_basic_object=item_status
            )
        except ApiException as e:
            p = r'"localizedMessage.*'
            matches = re.findall(p, e.body)
            if matches:
                error = "Error updating status: " + matches[0][:-2]
                click.echo(error)
            else:
                click.echo("Error updating status")
            if status not in status_list:
                click.echo("Please enter valid status from status list:")
                for stat in status_list:
                    click.echo(stat)
            exit(1)

    except KeyError:
        click.echo("Error: invalid status entered")


@click.command()
@click.option(
    "--input-file",
    help="Input csv file with item id default is STDIN",
    type=click.File("r"),
    default=sys.stdin,
)
@click.option(
    "--status",
    required=True,
    prompt="Item Status",
    help="New Status of Item",
)
@click.pass_obj
def set_item_status_by_id(cli, input_file, status):
    """Updates item status of item with the given ID and updates item log

    \b
    Example (file): set-item-status-by-id --input-file filename.csv --status='Ready For Use'
    Example (pipe): cat filename.csv | set-item-status-by-id --status="Planned"
    Example (terminal): set-item-status-by-id --status="Planned"
                        header
                        <Insert Item ID>
    Input is either through a named csv file or through STDIN.   Default is STDIN
    The format of the input data is an intended row to be removed followed by
    <Item ID>.
    """
    try:
        factory = cli.require_authenticated_api()
    except ApiException:
        click.echo("Unauthorized User/ Wrong Username or Password. Try again.")
        return

    item_api = factory.getItemApi()
    prop_type_api = factory.getPropertyTypeApi()

    stdin_msg = "Entry per line: <item_id>"
    reader, stdin_tty_mode = cli.prepare_cli_input_csv_reader(input_file, stdin_msg)    

    # Parse lines of csv
    for row in reader:
        if row.__len__() == 0 and stdin_tty_mode:
            break
        if not row[0]:
            continue

        item_id = row[0]

    set_item_status_by_id_helper(item_api, prop_type_api, item_id, status)


if __name__ == "__main__":
    set_item_status_by_id()
