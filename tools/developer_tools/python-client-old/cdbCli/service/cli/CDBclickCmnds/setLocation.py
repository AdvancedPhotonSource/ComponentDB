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
def set_location_help(item_id, location_id, cli):
    """
    This function sets a new location for a given inventory item

        :param item_id: The ID of the inventory item
        :param location_id: The ID of the location item
        :param cli: necessary CliBase object
    """

    # Note:  Parameter validation is was done by click
    
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
              help='ID of inventory item - single id or comma separated list by item-type')
@click.option('--item-id-type', default = 'id',
              type=click.Choice(["id","qr_id"],case_sensitive=False),
              help = "Allowed values are 'id'(default) or 'qr_id'")
@click.option('--location-id', required=True, prompt='Item Location ID',
              help='New location ID by location-type for item')
@click.option('--location-id-type', default = "name",
              type=click.Choice(["name","id","qr_id"],case_sensitive=False),
              help = "Allowed values are name(default) 'id' or 'qr_id'")
@click.option('--dist', help='Change the CDB distribution (sandbox, dev, prod)')
def set_location(item_id, item_id_type, location_id, location_id_type, dist=None):
    """Set new location for single or multiple items.  Locations can be specified
       with ids(default) or QRCodes and locations can be specified by name(default), 
       QRCodes or ids.

        \b
        * For list of Item IDs: enter as "ID1, ID2, ID3, ..."

        \b
        Example (single item): set-location-by-id --item-id 2160 --location-id 6565
        Example (multi-item): set-location-by-id --item-id "2160, 3604, 3605" --location-id 6565"""
    cli = CliBase(dist)

    factory = cli.require_authenticated_api()
    item_api = factory.getItemApi()
    locationItem_api = factory.getLocationItemApi()
    
    # Get ids if we were given QR Codes
    ids = item_id.split(",")
    if item_id_type == "qr_id":
        ids_from_qr_ids = [str(item_api.get_item_by_qr_id(int(qr_id)).id) for qr_id in ids]
        initialized_str = ","
        item_id = initialized_str.join(ids_from_qr_ids)


    # Get the location id if we are given the qr code
    if location_id_type == "qr_id":
        location_id = item_api.get_item_by_qr_id(int(location_id)).id
    if location_id_type == "name":
        location_id =  locationItem_api.get_location_items_by_name(location_id)[0].id
        
    set_location_help(item_id, location_id,  cli)


if __name__ == "__main__":
    set_location()

