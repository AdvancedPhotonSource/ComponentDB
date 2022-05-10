#!/usr/bin/env python3

import sys
import re
import csv
import click

from cdbApi import ItemStatusBasicObject
from cdbApi import ApiException
from cdbCli.common.cli import cliBase

from cdbCli.common.cli.cliBase import CliBase
from cdbCli.service.cli.cdbCliCmnds.setItemLogById import set_item_log_by_id_helper


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


################################################################################################
#                                                                                              #
#                                 Set new location of item                                     #
#                                                                                              #
################################################################################################
def set_item_details_helper(
    item_api, prop_type_api, item_id, detail_type, new_detail_value, add_log_to_item=False
):
    """
    This function updates fields on a CDB item

        :param item_api: Item Api object
        :param prop_type_api: Property Type Api object
        :param item_id: The ID of the inventory item
        :param detail_type: The item field to be updated
        :param new_detail_value: The new value for the field (will be cast to string)
    """

    try:
        item = item_api.get_item_by_id(item_id)
        if detail_type == "serial":
            old_detail_value = item.item_identifier1
            item.item_identifier1 = str(new_detail_value)
            item_api.update_item_details(item)
        elif detail_type == "description":
            old_detail_value = item.description
            item.description = str(new_detail_value)
            item_api.update_item_details(item)
        elif detail_type == "status":
            old_detail_value = item_api.get_item_status(item_id).value
            set_item_status_by_id_helper(
                item_api, prop_type_api, item_id, status=new_detail_value
            )
        else:
            click.echo("Invalid item property: " + detail_type)
            return
    except ApiException as e:
        p = r'"localizedMessage.*'
        matches = re.findall(p, e.body)
        if matches:
            error = (
                "ItemId:,"
                + str(item_id)
                + " Error: Error updating detail: "
                + matches[0][:-2]
            )
            click.echo(error)
    else:
        response = "Item Id: " + str(item_id) + ", Old (" + detail_type + "): "
        response += (
            str(old_detail_value)
            + ", New ("
            + detail_type
            + "): "
            + str(new_detail_value)
        )
        if add_log_to_item:
            set_item_log_by_id_helper(item_api, item_id, log_entry=response)
            click.echo(response)


@click.command()
@click.option(
    "--input-file",
    help="Input csv file with id,new detail value, default is STDIN",
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
    "--detail-type",
    default="description",
    type=click.Choice(["description", "serial", "status"], case_sensitive=False),
    help="Allowed values are description(default), 'serial', or 'status; ",
)
@cliBase.wrap_common_cli_click_options
@click.pass_obj
def set_item_details(cli, input_file, item_id_type, detail_type, add_log_to_item):
    """Updates select item details (e.g. description, serial number )

    \b
    Example (file): set-item-details --input-file=filename.csv --item-id-type=qr_id --detail-type=description
    Example (pipe): cat filename.csv | set-item-details -detail-type=serial
    Example (terminal): set-item-details -detail-type=description
                        header
                        <Insert Item ID>,<insert description here>

    Input is either through a named file or through STDIN.   Default is STDIN
    The format of the input data is
    <Item ID>,<New Detail Value>   where the ID is by the type specified by the commandline."""

    try:
        factory = cli.require_authenticated_api()
    except ApiException:
        click.echo("Unauthorized User/ Wrong Username or Password. Try again.")
        return

    item_api = factory.getItemApi()
    prop_type_api = factory.getPropertyTypeApi()

    stdin_msg = "Entry per line: <item_%s>,<%s>" % (item_id_type, detail_type)
    reader, stdin_tty_mode = cli.prepare_cli_input_csv_reader(input_file, stdin_msg)    

    # Parse lines of csv
    for row in reader:
        if row.__len__() == 0 and stdin_tty_mode:
            break
        if not row[0]:
            continue

        item_id = row[0]
        new_detail_value = row[1]

        # Get ids if we were given QR Codes
        if item_id_type == "qr_id":
            item_id = str(item_api.get_item_by_qr_id(int(item_id)).id)

        set_item_details_helper(
            item_api, prop_type_api, item_id, detail_type, new_detail_value, add_log_to_item
        )


if __name__ == "__main__":
    set_item_details()
