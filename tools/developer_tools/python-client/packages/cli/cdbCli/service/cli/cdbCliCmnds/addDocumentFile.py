#!/usr/bin/env python
import sys
import csv
import click

from rich import print
from rich.traceback import install

from cdbApi import ApiException

from CdbApiFactory import CdbApiFactory
from cdbCli.common.cli import cliBase

from cdbCli.common.cli.cliBase import CliBase
from cdbCli.service.cli.cdbCliCmnds.setItemLogById import set_item_log_by_id_helper


def add_document_file_helper(
    item_api, item_id, property_tag, property_description, upload_filename, add_item_log=False
):
    """Helper function to upload a file to a given item id

    Args:
        item_api (object): Item Api object
        item_id (string): Item ID to have file uploaded to
        property_tag (string): Tag of the given document
        property_description (string): Description of the given document
        upload_filename (string): Path to file including filename
    """

    try:

        current_prop_ids = [
            prop.id for prop in item_api.get_properties_for_item(item_id)
        ]
        fileObject = CdbApiFactory.createFileUploadObject(upload_filename)
        item_api.upload_document_for_item(item_id, file_upload_object=fileObject)
        document_property = [
            prop
            for prop in item_api.get_properties_for_item(item_id)
            if prop.id not in current_prop_ids
        ][0]
    except Exception as e:
        print("File uploaded to Item ID :" + str(item_id) + " unsuccessfully")
    else:
        document_property.tag = property_tag
        document_property.description = property_description
        item_api.update_item_property_value(item_id, property_value=document_property)
        if add_item_log:
            log = "File uploaded to Item ID :" + str(item_id) + " successfully"
            set_item_log_by_id_helper(item_api=item_api, item_id=item_id, log_entry=log)


@click.command()
@click.option(
    "--input-file",
    help="Input csv file with id, tag, description, upload filename, default is STDIN",
    type=click.File("r"),
    default=sys.stdin,
)
@click.option(
    "--item-id-type",
    default="id",
    type=click.Choice(["id", "qr_id"], case_sensitive=False),
    help="Allowed values are 'id'(default) or 'qr_id'",
)
@cliBase.wrap_common_cli_click_options
@click.pass_obj
def add_document_file(cli, input_file, item_id_type, add_log_to_item):
    """Uploads a document to a Document Property of the item.

    \b
    Example (file): add-document-file --input-file filename.csv --item-id-type=qr_id
    Example (pipe): cat filename.csv | add-document-file
    Example (terminal): add-document-file
                        header
                        <Item ID>,<Property Tag>,<Property Description>,<Filename to upload>

    Input is either through a named file or through STDIN.   Default is STDIN
    The format of the input data is
    <Item ID>,<Property Tag>,<Property Description>,<Filename to upload>"""
    
    try:
        factory = cli.require_authenticated_api()
    except ApiException:
        click.echo("Unauthorized User/ Wrong Username or Password. Try again.")
        return

    item_api = factory.getItemApi()
    
    stdin_msg = "Entry per line: <Item_%s>,<Property_Tag>,<Property_Description>,<Filename_to_upload>" % item_id_type
    reader, stdin_tty_mode = cli.prepare_cli_input_csv_reader(input_file, stdin_msg)    

    # Parse lines of csv
    for row in reader:
        if row.__len__() == 0 and stdin_tty_mode:
            break

        item_id = row[0]
        property_tag = row[1]
        property_description = row[2]
        upload_filename = row[3]

        if item_id_type == "qr_id":
            item_id = str(item_api.get_item_by_qr_id(int(item_id)).id)

        add_document_file_helper(
            item_api, item_id, property_tag, property_description, upload_filename, add_log_to_item
        )


if __name__ == "__main__":
    add_document_file()
