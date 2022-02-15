#!/usr/bin/env python3

import click

from cdbApi import LogEntryEditInformation
from cdbCli.common.cli.cliBase import CliBase


@click.command()
@click.option('--item-id', prompt='Item ID',
              help='Id of item to fetch.')
@click.option('--log-entry', prompt='Log Entry',
              help='Log entry text')
@click.option('--effective-date', help='Effective date of log entry')
def add_log_to_item_by_id(item_id, log_entry, effective_date=None):
    cli = CliBase()
    factory = cli.require_authenticated_api()
    itemApi = factory.getItemApi()

    log_entry_obj = LogEntryEditInformation(item_id=item_id, log_entry=log_entry, effective_date=effective_date)
    log = itemApi.add_log_entry_to_item(log_entry_edit_information=log_entry_obj)

    cli.print_cdb_obj(log)


if __name__ == "__main__":
    add_log_to_item_by_id()
