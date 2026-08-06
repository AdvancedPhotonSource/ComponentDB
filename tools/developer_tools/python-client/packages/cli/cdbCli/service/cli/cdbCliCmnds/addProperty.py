#!/usr/bin/env python3

import sys
import re
import csv
import click

from cdbApi import LogEntryEditInformation
from cdbApi import ItemStatusBasicObject
from cdbApi import ApiException

from CdbApiFactory import CdbApiFactory

from cdbCli.common.cli.cliBase import CliBase
from cdbApi.models.property_value import PropertyValue


################################################################################################
#                                                                                              #
#                           Add property to item                                               #
#                                                                                              #
################################################################################################
def add_property_help(
    item_id, prop_name, unique_flag, tag, prop_value, display_value, description, cli
):
    """
    This function updates fields on a CDB item

        :param item_id: The ID of the CDB item to add property to
        :param prop_name: CDB property name
        :param tag : Tag field for the CDB Property
        :param prop_value : property value for the Property
        :param display_value : display value for CDB Property
        :param description: Descrription of the property value.
        :param cli: necessary CliBase object
    """

    # Note:  Parameter validation is was done by click

    factory = cli.require_authenticated_api()
    item_api = factory.getItemApi()
    property_type_api = factory.getPropertyTypeApi()

    _ids = item_id.split(",")
    new_property_type = property_type_api.get_property_type_by_name(prop_name)

    response_list = []
    for _id in _ids:
        try:
            # Check to see if property exists and must be unique
            break_needed = False
            if unique_flag:
                properties = item_api.get_properties_for_item(_id)
                for prop in properties:
                    if prop.property_type == new_property_type and prop.tag == tag:
                        error = (
                            "Item Id:,"
                            + str(_id)
                            + ",Tag "
                            + prop.tag
                            + " is already used"
                        )
                        response_list.append(error)
                        break_needed = True
            if break_needed:
                continue

            # Go ahead and add the property
            property_value = PropertyValue(
                property_type=new_property_type,
                tag=tag,
                value=prop_value,
                display_value=display_value,
                description=description,
            )
            property = item_api.add_item_property_value(
                _id, property_value=property_value
            )
            response_list.append(
                 str(_id)+","+ str(property.id) + "," + prop_name + ",Tag," + tag
            )
        except ApiException as e:
            p = r'"localizedMessage.*'
            matches = re.findall(p, e.body)
            if matches:
                error = (
                    "ItemId:,"
                    + str(_id)
                    + "Error:Error creating property: "
                    + matches[0][:-2]
                )
                response_list.append(error)

    return response_list


@click.command()
@click.option(
    "--inputfile",
    help="Input csv file with id detail value, default is STDIN",
    type=click.File("r"),
    default=sys.stdin,
)
@click.option(
    "--item-id-type",
    default="id",
    type=click.Choice(["id", "qr_id"], case_sensitive=False),
    help="Allowed values are 'id'(default) or 'qr_id'",
)
@click.option("--propname", required=True)
@click.option(
    "--unique-flag/--no-unique-flag", default=True, help="Prevent duplicate tags [True]"
)
@click.option("--dist", help="Change the CDB distribution (as provided in cdb.conf)")
def add_property(inputfile, propname, item_id_type, unique_flag, dist=None):
    """Adds a Property to the cdb item.   Property Type
    is selected via the required propname flag.   If the unique flag is true,
    then the property is not added if there is already an exising property of
    type propnamewith the same tag.

     \b
    Input is either through a named file or through STDIN.   Default is STDIN
    The format of the input data is
    <Item ID>.<Tag>,<Value>,< Display Value>,<Description>
    where the ID is by the type specified on the commandline."""

    cli = CliBase(dist)
    factory = cli.require_authenticated_api()
    item_api = factory.getItemApi()

    property_type_api = factory.getPropertyTypeApi()
    new_property_type = property_type_api.get_property_type_by_name(propname)

    # Confirm that the property type exists

    reader = csv.reader(inputfile)
    for row in reader:
        item_id = row[0]
        tag = row[1]
        url = row[2]
        display_value = row[3]
        description = row[4]

        # Get ids if we were given QR Codes.  Note, we could have multiple ids specified as the first element of
        # the csv.   This is a holdover from the old code.
        try:
            ids = item_id.split(",")
            if item_id_type == "qr_id":
                ids_from_qr_ids = [
                    str(item_api.get_item_by_qr_id(int(qr_id)).id) for qr_id in ids
             ]
                initialized_str = ","
                item_id = initialized_str.join(ids_from_qr_ids)
        except Exception as e:
            print(e)
            continue

        response_list = add_property_help(
            item_id, propname, unique_flag, tag, url, display_value, description, cli
        )
        for response in response_list:
            click.echo(response)


if __name__ == "__main__":
    add_property()
