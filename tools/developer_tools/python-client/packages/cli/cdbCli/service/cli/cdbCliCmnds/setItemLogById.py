#!/usr/bin/env python3

import sys
import re
import click
import csv

from cdbApi import LogEntryEditInformation
from cdbApi import ApiException
from datetime import datetime

from cdbCli.common.cli.cliBase import CliBase


##############################################################################################
#                                                                                            #
#                           Add log to item given the item's ID                              #
#                                                                                            #
##############################################################################################
def set_item_log_by_id_helper(item_api, item_id, log_entry, effective_date=None):
    """Helper function to set a log for an item in CDB

    :param item_api: Necessary item api object
    :param item_id: item ID of the object which the log is being written for
    :param log_entry: the log entry to be written
    :param effective_date: optional date of log"""

    if effective_date:
        effective_date = datetime.strptime(effective_date, "%Y-%m-%d")
    try:
        log_entry_obj = LogEntryEditInformation(
            item_id=item_id, log_entry=log_entry, effective_date=effective_date
        )
        item_api.add_log_entry_to_item(log_entry_edit_information=log_entry_obj)
    except ApiException as e:
        p = r'"localizedMessage.*'
        matches = re.findall(p, e.body)
        if matches:
            error = "Error uploading log entry: " + matches[0][:-2]
            click.echo(error)
        else:
            click.echo("Error uploading log entry")
        exit(1)


@click.command()
@click.option(
    "--input-file",
    help="Input csv file with item_id,log_data,effective_date default is STDIN",
    type=click.File("r"),
    default=sys.stdin,
)
@click.option(
    "--effective-date", is_flag=True, help="Set if effective date is listed in input"
)
@click.pass_obj
def set_item_log_by_id(cli, input_file, effective_date=False):
    """Adds a log entry to the given item ids with optional effective date

    \b
    Example (file): set-item-log-by-id --input-file filename.csv --effective-date='yes'
    Example (pipe): cat filename.csv | set-item-log-by-id
    Example (terminal): set-item-log-by-id
                        header
                        <Insert Item ID>,<example log text>
    Input is either through a named csv file or through STDIN.   Default is STDIN
    The format of the input data is an intended row to be removed followed by
    <Item ID>,<Log Data>,<Effective Date>.
    """
    
    try:
        factory = cli.require_authenticated_api()
    except ApiException:
        click.echo("Unauthorized User/ Wrong Username or Password. Try again.")
        return

    item_api = factory.getItemApi()

    stdin_msg = "Entry per line: <item_id>,<log_text>"
    reader, stdin_tty_mode = cli.prepare_cli_input_csv_reader(input_file, stdin_msg)    

    # Parse lines of csv
    for row in reader:
        if row.__len__() == 0 and stdin_tty_mode:
            break
        if not row[0]:
            continue

        item_id = row[0]
        log_entry = row[1]
        if effective_date:
            effective_date = row[2]
        else:
            effective_date = None

        set_item_log_by_id_helper(item_api, item_id, log_entry, effective_date)


if __name__ == "__main__":
    set_item_log_by_id()
