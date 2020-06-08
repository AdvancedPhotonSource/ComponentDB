#!/usr/bin/env python3

import click

from cdbCli.common.cli.cliBase import CliBase


@click.command()
@click.option('--item-id', prompt='Item ID',
              help='Id of item to fetch.')
def get_item_by_id(item_id):
    cli = CliBase()
    factory = cli.require_api()
    itemApi = factory.getItemApi()

    item = itemApi.get_item_by_id(item_id)

    cli.print_cdb_obj(item)


if __name__ == "__main__":
    get_item_by_id()
