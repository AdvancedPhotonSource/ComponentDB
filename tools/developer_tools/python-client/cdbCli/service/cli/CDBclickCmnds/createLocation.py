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

from cdbCli.common.cli.cliBase import CliBase
from cdbCli.service.cli.CDBclickCmnds.setItemLogById import set_item_log_by_id_help


################################################################################################
#                                                                                              #
#                                 Set new location of item                                     #
#                                                                                              #
################################################################################################
def create_location_help(parent_location_id,
                             location_name,
                             location_qr_id,
                             location_type,
                             location_description,
                             cli):
    """
    This function creates a new location 

        :param parent_location_id: The parent ID iunder which this location is created
        :param location_name: New Location's Name
        :param locaton_qr_id: QR Code of the location
        :param location_type: Type of the Location tion
        :param location_description: New location's description
        :param cli: necessary CliBase objectio
    """

    factory = cli.require_authenticated_api()
    item_api = factory.getItemApi()
    try:
        new_location = NewLocationInformation(parent_location_id=parent_location_id,
                                              location_name = location_name,
                                              location_qr_id = location_qr_id,
                                              location_type = location_type,
                                              location_description = location_description)
        result = item_api.create_location(new_location)
        print(result)
    except Exception as e:
        print("Error :",str(e))

        

@click.command()
@click.option('--inputfile',help='Input csv file with new location parameters, see help, default is STDIN',
              type=click.File('r'),default=sys.stdin)
@click.option('--dist', help='Change the CDB distribution (as provided in cdb.conf)')

def create_location(inputfile, dist=None):
    """Create new location from csv on STDIN(default) or a file
       File has the format
       <Parent Location ID>,<location_name>,<location qr code>,<location_type>,<location description>"""
    
    
    cli = CliBase(dist)
    factory = cli.require_authenticated_api()
    item_api = factory.getItemApi()
    
    reader = csv.reader(inputfile)
    for row in reader:
        create_location_help(row[0],
                             row[1],
                             row[2],
                             row[3],
                             row[4],
                             cli)

if __name__ == "__main__":
    create_location()

