#!/usr/bin/env python3

import sys
import re
import csv
import click

from rich import print

from cdbApi import ApiException
from cdbCli.common.cli import cliBase

from cdbCli.service.cli.cdbCliCmnds.setItemLogById import set_item_log_by_id_helper
from cdbApi.models.property_value import PropertyValue


################################################################################################
#                                                                                              #
#                           Add http handler property to item                                  #
#                                                                                              #
################################################################################################
def add_document_property_helper(
    item_api,
    prop_type_api,
    item_id,
    prop_name,
    unique_flag,
    tag,
    prop_value,
    display_value,
    description,
    add_log_to_item = False
):
    """This function adds a http property to a given item

    Args:
        item_api (object): Item Api object
        prop_type_api (object): Property Type Api object
        item_id (string): Item Id
        prop_name (string): Name of the property to add
        unique_flag (boolean): Whether or not to prevent duplicate tags
        tag (string): Tag for given property
        prop_value (string): Value of the property to add
        display_value (string): Value to be displated
        description (string): Description of the property
    """

    try:
        http_property_type = prop_type_api.get_property_type_by_name(prop_name)        

        # Check to see if property exists and must be unique
        if unique_flag:
            properties = item_api.get_properties_for_item(item_id)
            for prop in properties:
                if prop.property_type == http_property_type and prop.tag == tag:
                    print(
                        "Item Id: " + item_id + ", Tag " + prop.tag + " is already used"
                    )
                    return
        # Go ahead and add the property
        property_value = PropertyValue(
            property_type=http_property_type,
            tag=tag,
            value=prop_value,
            display_value=display_value,
            description=description,
        )
        item_api.add_item_property_value(item_id, property_value=property_value)
    except ApiException as e:
        p = r'"localizedMessage.*'
        matches = re.findall(p, e.body)
        if matches:
            error = (
                "ItemId: "
                + item_id
                + "Error: Unable to creating property: "
                + matches[0][:-2]
            )
            print(error)
    else:
        if add_log_to_item:
            log = "Item Id: " + item_id + "successfully uploaded with properties"
            set_item_log_by_id_helper(item_api=item_api, item_id=item_id, log_entry=log)


@click.command()
@click.option(
    "--input-file",
    help="Input csv file with id,new detail value, default is STDIN",
    type=click.File("r"),
    default=sys.stdin,
)
@click.option(
    "--item-id-type",
    default="id",
    type=click.Choice(["id", "qr_id"], case_sensitive=False),
    help="Allowed values are 'id'(default) or 'qr_id'",
)
@click.option(
    "--prop",
    default="web_documentation",
    type=click.Choice(["web_documentation", "related_cdb_item"], case_sensitive=False),
    help="Allowed values are web_documentation(default) or 'related_cdb_item' ",
)
@click.option(
    "--unique-flag/--no-unique-flag", default=True, help="Prevent duplicate tags [True]"
)
@cliBase.wrap_common_cli_click_options
@click.pass_obj
def add_document_property(cli, input_file, item_id_type, prop, unique_flag, add_log_to_item):
    """Adds a Property with an http link handler to a CDB Item.   Property Type
    is selected via the doc_type flag.   If the unique flag is true,
    then the property is not added if there is already a document propety
    with the same tag.

    \b
    Example (file): add-document-property --input-file filename.csv --item-id-type=qr_id
    Example (pipe): cat filename.csv | add-document-property
    Example (terminal): add-document-property
                        header
                        <Insert Item ID>,<Tag>,<Url>,<URL Display Value>,<Description>

    Input is either through a named file or through STDIN.   Default is STDIN
    The format of the input data is
    <Item ID>.<Tag>,<Url>,<URL Display Value>,<Description>
    where the ID is by the type specified on the commandline."""
    try:
        factory = cli.require_authenticated_api()
    except ApiException:
        click.echo("Unauthorized User/ Wrong Username or Password. Try again.")
        return

    item_api = factory.getItemApi()
    prop_type_api = factory.getPropertyTypeApi()
    
    if prop == "web_documentation":
        prop_name = "Documentation (Web)"        
    elif prop == "related_cdb_item":
        prop_name = "Related CDB Item"

    stdin_msg = "Entry per line: <Item_%s>.<Tag>,<Url>,<Display_Value>,<Description>" % item_id_type
    reader, stdin_tty_mode = cli.prepare_cli_input_csv_reader(input_file, stdin_msg)    

    # Parse lines of csv
    for row in reader:
        if row.__len__() == 0 and stdin_tty_mode:
            break
        item_id = row[0]
        tag = row[1]
        url = row[2]
        display_value = row[3]
        description = row[4]

        if item_id_type == "qr_id":
            item_id = str(item_api.get_item_by_qr_id(int(item_id)).id)

        add_document_property_helper(
            item_api,
            prop_type_api,
            item_id,
            prop_name,
            unique_flag,
            tag,
            url,
            display_value,
            description,
            add_log_to_item
        )


if __name__ == "__main__":
    add_document_property()
