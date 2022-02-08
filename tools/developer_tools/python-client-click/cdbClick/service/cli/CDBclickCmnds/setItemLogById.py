#!/usr/bin/env python3

import re
import click

from cdbApi import SimpleLocationInformation
from cdbApi import LogEntryEditInformation
from cdbApi import ItemStatusBasicObject
from cdbApi import ApiException

from CdbApiFactory import CdbApiFactory

from cdbClick.common.cli.cliBase import CliBase



##############################################################################################
#                                                                                            #
#                           Add log to item given the item's ID                              #
#                                                                                            #
##############################################################################################
def set_item_log_by_id_help(item_id, log_entry, cli, effective_date):
    """Helper function to set a log for an item in CDB

        :param item_id: item ID of the object which the log is being written for
        :param log_entry: the log entry to be written
        :param cli: necessary CliBase object
        :param effective_date: optional date of log"""

    try:
        factory = cli.require_authenticated_api()
        itemApi = factory.getItemApi()
    except ApiException:
        click.echo("Unauthorized User/ Wrong Username or Password. Try again.")
        return
    for _id in item_id.split(','):
        try:
            log_entry_obj = LogEntryEditInformation(item_id=_id, log_entry=log_entry, effective_date=effective_date)
            itemApi.add_log_entry_to_item(log_entry_edit_information=log_entry_obj)
        except ApiException as e:
            p = r'"localizedMessage.*'
            matches = re.findall(p, e.body)
            if matches:
                error = "Error uploading log entry: " + matches[0][:-2]
                click.echo(error)
            else:
                click.echo("Error uploading log entry")
            exit(1)
#    click.echo("Log updated successfully")


@click.command()
@click.option('--item-id', required=True, prompt='Item ID', help='Id of item to fetch.')
@click.option('--log-entry', required=True, prompt='Log Entry', help='Log entry text')
@click.option('--effective-date', help='Effective date of log entry. Format: "yyy-mm-dd" Default to todays date')
@click.option('--dist', help='Change the CDB distribution (sandbox, dev, prod)')
def set_item_log_by_id(item_id, log_entry, dist=None, effective_date=None):
    """Adds a log entry to the item with the given ID

        \b
        Example (default date): add-log-to-item-by-id --item-id 2160 --log-entry "Test entry"
        Example (custom date): add-log-to-item-by-id --item-id 2160 --log-entry "Test entry" --effective-date 2020-06-22
        Example (multiple IDs): add-log-to-item-by-id --item-id "2160, 3604, 3605" --log-entry "Test entry"
    """
    cli = CliBase()
    set_item_log_by_id_help(item_id, log_entry, cli, effective_date)


if __name__ == "__main__":
    set_item_log_by_id()
