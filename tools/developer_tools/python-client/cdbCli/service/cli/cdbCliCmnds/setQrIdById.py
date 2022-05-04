#!/usr/bin/env python3

import sys
import csv
import re
import click

from cdbApi import ApiException
from cdbCli.common.cli import cliBase


from cdbCli.common.cli.cliBase import CliBase
from cdbCli.service.cli.cdbCliCmnds.setItemLogById import set_item_log_by_id_helper


##############################################################################################
#                                                                                            #
#                             Set the QR ID of an inventory item                             #
#                                                                                            #
##############################################################################################
def set_qr_id_by_id_helper(item_api, item_id, qr_id, add_log_to_item):
    """
    This function sets a new QR ID for a given item

        :param item_api: Item Api object
        :param item_id: The ID of the item
        :param qr_id: The new desired QR ID of the item
    """

    try:
        item = item_api.get_item_by_id(item_id)

        old_qr_id = str(item.qr_id)
        item.qr_id = int(qr_id)
        item_api.update_item_details(item=item)

    except ApiException as e:
        p = r'"localizedMessage.*'
        matches = re.findall(p, e.body)
        if matches:
            error = "Error setting QR ID: " + matches[0][:-2]
            print(error)
        else:
            print("Error setting QR ID")
    else:
        if str(item.qr_id) == old_qr_id:
            print(
                "Item ID: " + str(item_id) + " has unchanged QR ID: " + str(item.qr_id)
            )
        else:
            echo_string = (
                "Item ID: "
                + str(item_id)
                + ", Old QRId: "
                + str(old_qr_id)
                + ", New QRId: "
                + str(qr_id)
            )
            if add_log_to_item:
                set_item_log_by_id_helper(
                    item_api=item_api, item_id=item_id, log_entry=echo_string
                )
            print(echo_string)


@click.command()
@click.option(
    "--input-file",
    help="Input csv file with new location parameters, see help, default is STDIN",
    type=click.File("r"),
    default=sys.stdin,
)
@cliBase.wrap_common_cli_click_options
@click.pass_obj
def set_qr_id_by_id(cli, input_file, add_log_to_item):
    """Assigns QR Ids to a set of Item IDs.  This will overwrite QR Codes if already assigned, but
    but throws an error if new QR Code is already assigned.

    \b
    Example (file): set-qr-id-by-id --input-file=filename.csv
    Example (pipe): cat filename.csv | set-qr-id-by-id
    Example (terminal): set-qr-id-by-id
                        header
                        <Insert Item ID>,<Insert QR Code>

    CSV input is on STDIN(default) or a file and the csv format is
    <Item ID>,<QR Code>"""
    
    try:
        factory = cli.require_authenticated_api()
        item_api = factory.getItemApi()
    except ApiException:
        click.echo("Unauthorized User/ Wrong Username or Password. Try again.")
        return

    stdin_msg = "Entry per line: <Item_ID>,<QR_Code>"
    reader, stdin_tty_mode = cli.prepare_cli_input_csv_reader(input_file, stdin_msg)    

    # Parse lines of csv
    for row in reader:
        if row.__len__() == 0 and stdin_tty_mode:
            break
        if not row[0]:
            continue

        item_id = row[0]
        qr_id = row[1]
        set_qr_id_by_id_helper(item_api, item_id, qr_id, add_log_to_item)


if __name__ == "__main__":
    set_qr_id_by_id()
