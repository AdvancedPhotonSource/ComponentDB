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
from cdbApi.models.property_value import PropertyValue



################################################################################################
#                                                                                              #
#                           Add http handler property to item                                  #
#                                                                                              #
################################################################################################
def add_http_property_help(item_id,
                           prop_name,
                           unique_flag,
                           tag,
                           prop_value,
                           display_value,
                           description,
                           cli):
    """
    This function updates fields on a CDB item

        :param item_id: The ID of the CDB item to add property to
        :param prop_name: CDB property name 
        :param tag : Tag field for the CDB Property
        :param prop_value : property value for the Property
        :param display_value : display value for CDB Property
        :param new_detail_value: The new value for the field (will be cast to string)
        :param cli: necessary CliBase object
    """

    # Note:  Parameter validation is was done by click
    
    factory = cli.require_authenticated_api()
    item_api = factory.getItemApi()
    property_type_api = factory.getPropertyTypeApi()
    
    _ids = item_id.split(",")
    http_property_type = property_type_api.get_property_type_by_name(prop_name)
    

    response_list = []
    for _id in _ids:
        try:
# Check to see if property exists and must be unique
            break_needed = False
            if unique_flag:
                properties = item_api.get_properties_for_item(_id)
                for prop in properties:
                    if prop.property_type == http_property_type and prop.tag == tag:
                        error = "Item Id:,"+str(_id)+",Tag "+prop.tag+" is already used"
                        response_list.append(error)
                        break_needed = True
            if break_needed:
                continue
                    
# Go ahead and add the property
            property_value = PropertyValue(property_type=http_property_type,
                                           tag = tag,
                                           value = prop_value,
                                           display_value = display_value,
                                           description = description)
            property = item_api.add_item_property_value(_id,property_value = property_value)
            response_list.append("ItemId:"+str(_id)+",Property,"+prop_name+",Tag,"+tag)
        except ApiException as e:
            p = r'"localizedMessage.*'
            matches = re.findall(p, e.body)
            if matches:
                error = "ItemId:,"+str(_id)+"Error:Error creating property: " + matches[0][:-2]
                response_list.append(error)

    return(response_list)



@click.command()
@click.option('--inputfile',help='Input csv file with id,new detail value, default is STDIN',
              type=click.File('r'),default=sys.stdin)
@click.option('--item-id-type', default = 'id',
              type=click.Choice(["id","qr_id"],case_sensitive=False),
              help = "Allowed values are 'id'(default) or 'qr_id'")
@click.option('--prop', default = "web_documentation",
              type=click.Choice(["web_documentation","related_cdb_item"],case_sensitive=False),
              help = "Allowed values are web_documentation(default) or 'related_cdb_item' ")
@click.option('--unique-flag/--no-unique-flag',default=True,help="Prevent duplicate tags [True]")
@click.option('--dist', help='Change the CDB distribution (as provided in cdb.conf)')

def add_http_property(inputfile, item_id_type, prop, unique_flag,dist=None):
    """Adds a Property with an http link handler to a CDB Item.   Property Type
       is selected via the doc_type flag.   If the unique flag is true,
       then the property is not added if there is already a document propety
       with the same tag.

        \b
       Input is either through a named file or through STDIN.   Default is STDIN
       The format of the input data is
       <Item ID>.<Tag>,<Url>,<URL Display Value>,<Description>
       where the ID is by the type specified on the commandline."""
    
    cli = CliBase(dist)
    factory = cli.require_authenticated_api()
    item_api = factory.getItemApi()
    print(prop)
    if prop == "web_documentation":
        prop_name = "Documentation (Web)"
        print("Setting prop_name")
    elif prop == "related_cdb_item":
        prop_name = "Related CDB Item"
            
    

    reader = csv.reader(inputfile)
    for row in reader:
        item_id = row[0]
        tag = row[1]
        url = row[2]
        display_value = row[3]
        description = row[4]
        
    # Get ids if we were given QR Codes.  Note, we could have multiple ids specified as the first element of
    # the csv.   This is a holdover from the old code.
        ids = item_id.split(",")
        if item_id_type == "qr_id":
            ids_from_qr_ids = [str(item_api.get_item_by_qr_id(int(qr_id)).id) for qr_id in ids]
            initialized_str = ","
            item_id = initialized_str.join(ids_from_qr_ids)


        response_list = add_http_property_help(item_id,
                                               prop_name,
                                               unique_flag,
                                               tag,
                                               url,
                                               display_value,
                                               description,
                                               cli)
        for response in response_list:
            click.echo(response)

if __name__ == "__main__":
    add_http_property()

