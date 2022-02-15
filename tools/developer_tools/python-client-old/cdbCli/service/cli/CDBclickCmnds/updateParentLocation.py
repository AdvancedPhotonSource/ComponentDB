#!/usr/bin/env python3

import sys
import re
import csv
import click

from cdbApi import SimpleLocationInformation
from cdbApi import LogEntryEditInformation
from cdbApi import ItemStatusBasicObject
from cdbApi import ApiException

from cdbApi.models.new_location_information import NewLocationInformation

from CdbApiFactory import CdbApiFactory

from cdbClick.common.cli.cliBase import CliBase
from cdbClick.service.cli.CDBclickCmnds.setItemLogById import set_item_log_by_id_help


################################################################################################
#                                                                                              #
#                                 Set new location of item                                     #
#                                                                                              #
################################################################################################
def update_parent_location_help(location_item_id,
                         parent_item_id,
                         cli):
    """
    This function updates the parent location for a child location. 

        :param location_item_id: The ID of the Parent
        :param parent_item_id : New Parent
        :param cli: necessary CliBase objectio
    """

    factory = cli.require_authenticated_api()
    item_api = factory.getItemApi()
    locationItem_api = factory.getLocationItemApi()
    try:
        result = locationItem_api.update_location_parent(location_item_id,parent_item_id)
        print(result)
    except Exception as e:
        print("Error :",str(e))

        

@click.command()
@click.option('--inputfile',help='Input csv file with new location parameters, see help, default is STDIN',
              type=click.File('r'),default=sys.stdin)
@click.option('--item-id-type', default = 'id',
              type=click.Choice(["id","qr_id"],case_sensitive=False),
              help = "Allowed values are 'id'(default) or 'qr_id'")
@click.option('--dist', help='Change the CDB distribution (as provided in cdb.conf)')

def update_parent_location(inputfile, item_id_type, dist=None):
    """Essentially moves one location under another (parent).
       CSV input is on STDIN(default) or a file and the csv format is
       File has the format
       <location ID>,<parent id>"""
    
    
    cli = CliBase(dist)
    factory = cli.require_authenticated_api()
    item_api = factory.getItemApi()
    
    reader = csv.reader(inputfile)
    for row in reader:
        location_id = item_api.get_item_by_qr_id(row[0]).id if item_id_type == "qr_id" else row[0] 
        parent_id = row[1]
        print(location_id," ",parent_id)
        
        update_parent_location_help(location_id,
                                   parent_id,
                                   cli)

if __name__ == "__main__":
    update_parent_location()

