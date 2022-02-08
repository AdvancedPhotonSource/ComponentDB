#!/usr/bin/env python3

import sys
import re
import csv
import click

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
def update_item_details_help(item_id, detail_type, new_detail_value, cli):
    """
    This function updates fields on a CDB item

        :param item_id: The ID of the inventory item
        :param detail_type: The item field to be updated
        :param new_detail_value: The new value for the field (will be cast to string)
        :param cli: necessary CliBase object
    """

    # Note:  Parameter validation is was done by click
    
    factory = cli.require_authenticated_api()
    item_api = factory.getItemApi()
    
    _ids = item_id.split(",")

    response_list = []
    for _id in _ids:
        try:
            item = item_api.get_item_by_id(_id)
            if detail_type == "serial":
                old_detail_value = item.item_identifier1
                item.item_identifier1 = str(new_detail_value)
            elif detail_type == "description":
                old_detail_value = item.description
                item.description = str(new_detail_value)
            result = item_api.update_item_details(item)    
            response_list.append("ItemId:"+str(_id)+",Old("+detail_type+"):"+
                                 str(old_detail_value)+",New("+detail_type+"):"+str(new_detail_value))
        except ApiException as e:
            p = r'"localizedMessage.*'
            matches = re.findall(p, e.body)
            if matches:
                error = "ItemId:,"+str(_id)+"Error:Error updating detail: " + matches[0][:-2]
                response_list.append(error)

    return(response_list)
        

@click.command()
@click.option('--inputfile',help='Input csv file with id,new detail value, default is STDIN',
              type=click.File('r'),default=sys.stdin)
@click.option('--item-id-type', default = 'id',
              type=click.Choice(["id","qr_id"],case_sensitive=False),
              help = "Allowed values are 'id'(default) or 'qr_id'")
@click.option('--detail-type', default = "description",
              type=click.Choice(["description","serial"],case_sensitive=False),
              help = "Allowed values are description(default) or 'serial' ")
@click.option('--dist', help='Change the CDB distribution (as provided in cdb.conf)')

def update_item_details(inputfile, item_id_type, detail_type, dist=None):
    """Updates select item details (e.g. description, serial number )

        \b
       Input is either through a named file or through STDIN.   Default is STDIN
       The format of the input data is
       <Item ID>,<New Detail Value>   where the ID is by the type specified by the commandline."""
    
    cli = CliBase(dist)
    factory = cli.require_authenticated_api()
    item_api = factory.getItemApi()
    
    reader = csv.reader(inputfile, delimiter=',')
    design_name, item_qr_id = next(reader)
    for row in reader:
        item_id = row[0]
        new_detail_value = row[1]
        
    # Get ids if we were given QR Codes.  Note, we could have multiple ids specified as the first element of
    # the csv.   This is a holdover from the old code.
        ids = item_id.split(",")
        if item_id_type == "qr_id":
            ids_from_qr_ids = [str(item_api.get_item_by_qr_id(int(qr_id)).id) for qr_id in ids]
            initialized_str = ","
            item_id = initialized_str.join(ids_from_qr_ids)


        response_list = update_item_details_help(item_id, detail_type, new_detail_value, cli)
        for response in response_list:
            click.echo(response)

if __name__ == "__main__":
    update_item_details()

