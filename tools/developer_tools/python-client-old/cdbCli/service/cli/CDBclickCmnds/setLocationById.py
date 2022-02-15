#!/usr/bin/env python3

import re
import click

from cdbApi import SimpleLocationInformation
from cdbApi import LogEntryEditInformation
from cdbApi import ItemStatusBasicObject
from cdbApi import ApiException

from CdbApiFactory import CdbApiFactory

from cdbClick.common.cli.cliBase import CliBase
from cdbClick.service.cli.CDBclickCmnds.setItemLogById import set_item_log_by_id_help


################################################################################################
#                                                                                              #
#                                 Set new location of item                                     #
#                                                                                              #
################################################################################################
def set_location_by_id_help(item_id, location_id, cli):
    """
    This function sets a new location for a given inventory item

        :param item_id: The ID of the inventory item
        :param location_id: The ID of the location item
        :param cli: necessary CliBase object
    """

    factory = cli.require_authenticated_api()
    item_api = factory.getItemApi()

    _ids = item_id.split(",")
    for _id in _ids:
        location = SimpleLocationInformation(locatable_item_id=_id.strip(" "), location_item_id=location_id)
        try:
            item_api.update_item_location(simple_location_information=location)
        except ApiException as e:
            p = r'"localizedMessage.*'
            matches = re.findall(p, e.body)
            if matches:
                error = "Error updating location: " + matches[0][:-2]
                click.echo(error)
            exit(1)

    log = "Location updated by CDB CLI"
    set_item_log_by_id_help(item_id, log, cli, effective_date=None)


@click.command()
@click.option('--item-id', required=True, prompt='Item ID',
              help='ID of inventory item - single id or comma separated list')
@click.option('--location-id', required=True, prompt='Item Location ID', help='New location ID for item')
@click.option('--dist', help='Change the CDB distribution (sandbox, dev, prod)')
def set_location_by_id(item_id, location_id, dist=None):
    """Set new location for single or multiple items using location ID

        \b
        * For list of IDs: enter as "ID1, ID2, ID3, ..."

        \b
        Example (single item): set-location-by-id --item-id 2160 --location-id 6565
        Example (multi-item): set-location-by-id --item-id "2160, 3604, 3605" --location-id 6565"""
    cli = CliBase(dist)
    set_location_by_id_help(item_id, location_id, cli)


if __name__ == "__main__":
    set_location_by_id()

