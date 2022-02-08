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
#                            Update Assembly Part Name                                         #
#                                                                                              #
################################################################################################
def update_item_assembly_help(item_id, part_name, assigned_item_id, cli):
    """
    This function updates fields on a CDB item

        :param item_id: The ID of the inventory item assembly
        :param part_name: The part_name of the field for the assignment
        :param assigned_item_id: The id of the item to be assigned to the part_name
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
            item_hierarchy = item_api.get_item_hierarchy_by_id(_id)
            element_dict = {item_hierarchy.child_items[i].element_name : item_hierarchy.child_items[i].element_id 
                            for i in range(len(item_hierarchy.child_items))}
            item_hierarchy_after_assignment = itemApi.update_contained_item(element_dict[part_name],assigned_item_id)
            element_dict_after = {item_hierarchy_after_assignment.child_items[i].element_name :
                                  item_hierarchy_after_assignment.child_items[i].element_id 
                                  for i in range(len(item_hierarchy_after_assignment.child_items))}
            response_list.append("ItemId:"+str(_id)+",New("+part_name+"):"+str(element_dict_after[part_name]))
        except ApiException as e:
            p = r'"localizedMessage.*'
            matches = re.findall(p, e.body)
            if matches:
                error = "ItemId:,"+str(_id)+"Error:Error updating assembly item: " + matches[0][:-2]
                response_list.append(error)

    return(response_list)
        

@click.command()
@click.option('--inputfile',help='Input csv file with id, part name, assigned id. default is STDIN',
              type=click.File('r'),default=sys.stdin)
@click.option('--item-id-type', default = 'id',
              type=click.Choice(["id","qr_id"],case_sensitive=False),
              help = "Allowed values are 'id'(default) or 'qr_id'")
@click.option('--assigned-item-id-type', default = 'id',
              type=click.Choice(["id","qr_id"],case_sensitive=False),
              help = "Allowed values are 'id'(default) or 'qr_id'")
@click.option('--dist', help='Change the CDB distribution (as provided in cdb.conf)')

def update_item_assembly(inputfile, item_id_type, assigned_item_id_type, dist=None):
    """Updates item hierarchy (e.g. assemblies )

        \b
       Input is either through a named file or through STDIN.   Default is STDIN
       The format of the input data is
       <Item ID>,<Part Name>,<Item ID to Assign to Part>   where the ID is by the type specified by the commandline."""
    
    cli = CliBase(dist)
    factory = cli.require_authenticated_api()
    item_api = factory.getItemApi()
    
    reader = csv.reader(inputfile)
    for row in reader:
        item_id = row[0]
        part_name = row[1]
        assigned_item_id = row[2]
        
        
    # Get ids if we were given QR Codes.  Note, we could have multiple ids specified as the first element of
    # the csv.   This is a holdover from the old code.
        ids = item_id.split(",")
        if item_id_type == "qr_id":
            ids_from_qr_ids = [str(item_api.get_item_by_qr_id(int(qr_id)).id) for qr_id in ids]
            initialized_str = ","
            item_id = initialized_str.join(ids_from_qr_ids)
        if assigned_item_id_type == "qr_id":
            assigned_item_id = item_api.get_item_by_qr_id(int(assigned_item_id)).id
            

        response_list = update_item_assembly_help(item_id, part_name, assigned_item_id, cli)
        for response in response_list:
            click.echo(response)

if __name__ == "__main__":
    update_item_assembly()

