#!/usr/bin/env python3

import sys
import re
import csv
import click

from cdbApi import SimpleLocationInformation
from cdbApi import LogEntryEditInformation
from cdbApi import ItemStatusBasicObject
from cdbApi import ApiException

from CdbApiFactory import CdbApiFactory

from cdbCli.common.cli.cliBase import CliBase
from cdbCli.service.cli.CDBclickCmnds.setItemLogById import set_item_log_by_id_help


################################################################################################
#                                                                                              #
#                                 Set new location of item                                     #
#                                                                                              #
################################################################################################
def set_item_location_help(item_id, location_id, cli):
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

    response_list = []
    for _id in _ids:
    # Do not attempt to change a location if we are already at that location
        current_location = item_api.get_item_location(_id).location_item.id
        if current_location == location_id:
            response_list.append("ItemId:"+str(_id)+",PriorLocationID:"+
                                 str(current_location)+",NewLocationID:"+str(location_id))
            break
    # Get the new location and attempt to move the item to that location.
        location = SimpleLocationInformation(locatable_item_id=_id.strip(" "), location_item_id=location_id)
        try:
            item_api.update_item_location(simple_location_information=location)
            log = "Location updated by CDB CLI"
            set_item_log_by_id_help(item_id, log, cli, effective_date=None)
            response_list.append("ItemId:"+str(_id)+",PriorLocationID:"+
                             str(current_location)+",NewLocationID:"+str(location_id))
        except ApiException as e:
            p = r'"localizedMessage.*'
            matches = re.findall(p, e.body)
            if matches:
                error = "ItemId:,"+str(_id)+"Error:Error updating location: " + matches[0][:-2]
                response_list.append(error)

    return(response_list)
        

@click.command()
@click.option('--inputfile',help='Input csv file with id,location, default is STDIN',
              type=click.File('r'),default=sys.stdin)
@click.option('--item-id-type', default = 'id',
              type=click.Choice(["id","qr_id"],case_sensitive=False),
              help = "Allowed values are 'id'(default) or 'qr_id'")
@click.option('--location-id-type', default = "name",
              type=click.Choice(["name","id","qr_id"],case_sensitive=False),
              help = "Allowed values are name(default) 'id' or 'qr_id'")
@click.option('--dist', help='Change the CDB distribution (as provided in cdb.conf)')

def set_item_location(inputfile, item_id_type, location_id_type, dist=None):
    """Set new location for single or multiple items.  Locations can be specified
       with ids(default) or QRCodes and locations can be specified by name(default), 
       QRCodes or ids.

        \b
       Input is either through a named file or through STDIN.   Default is STDIN
       The format of the input data is
       <Item ID>,<Location ID>   where the ID is by the type specified by the commandline."""
    
    cli = CliBase(dist)
    factory = cli.require_authenticated_api()
    item_api = factory.getItemApi()
    locationItem_api = factory.getLocationItemApi()
    
    reader = csv.reader(inputfile)
    for row in reader:
        item_id = row[0]
        location_id = row[1]
        
    # Get ids if we were given QR Codes.  Note, we could have multiple ids specified as the first element of
    # the csv.   This is a holdover from the old code.
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

        response_list = set_item_location_help(item_id, location_id,  cli)
        for response in response_list:
            click.echo(response)

if __name__ == "__main__":
    set_item_location()

