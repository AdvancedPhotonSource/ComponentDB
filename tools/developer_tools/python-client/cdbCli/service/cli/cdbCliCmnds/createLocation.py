#!/usr/bin/env python3

import sys
import csv
import click

from rich import print
from cdbApi import ApiException

from cdbApi.models.new_location_information import NewLocationInformation

from cdbCli.common.cli.cliBase import CliBase


################################################################################################
#                                                                                              #
#                                 Set new location of item                                     #
#                                                                                              #
################################################################################################
def create_location_helper(
    item_api,
    parent_location_id,
    location_name,
    location_qr_id,
    location_type,
    location_description,
):
    """
    This function creates a new location

        :param item_api: Item Api object
        :param parent_location_id: The parent ID iunder which this location is created
        :param location_name: New Location's Name
        :param locaton_qr_id: QR Code of the location
        :param location_type: Type of the Location tion
        :param location_description: New location's description
    """

    try:
        new_location = NewLocationInformation(
            parent_location_id=parent_location_id,
            location_name=location_name,
            location_qr_id=location_qr_id,
            location_type=location_type,
            location_description=location_description,
        )
        result = item_api.create_location(new_location)
        print(result)
    except Exception as e:
        print("Error :", str(e))


@click.command()
@click.option(
    "--input-file",
    help="Input csv file with new location parameters, see help, default is STDIN",
    type=click.File("r"),
    default=sys.stdin,
)
@click.pass_obj
def create_location(cli, input_file):
    """Creates a new location with id, qr_id, name, type, and description

    \b
    Example (file): create-location --input-file filename.csv
    Example (pipe): cat filename.csv | create-location
    Example (terminal): create-location
                        header
                        <Insert Location ID>,<Location Name>,<qr_id>,<location type>,<location description>


    Create new location from csv on STDIN(default) or a file
    File has the format
    <Parent Location ID>,<location_name>,<location qr code>,<location_type>,<location description>"""
    
    try:
        factory = cli.require_authenticated_api()
    except ApiException:
        click.echo("Unauthorized User/ Wrong Username or Password. Try again.")
        return

    item_api = factory.getItemApi()

    stdin_msg = "Entry per line: <Parent_Location_ID>,<Location_Name>,<qr_id>,<location_type>,<location_description>"
    reader, stdin_tty_mode = cli.prepare_cli_input_csv_reader(input_file, stdin_msg)    

    # Parse lines of csv
    for row in reader:
        if row.__len__() == 0 and stdin_tty_mode:
            break
        location_id = row[0]
        location_name = row[1]
        location_qr_id = row[2]
        location_type = row[3]
        location_description = row[4]
        create_location_helper(
            item_api,
            location_id,
            location_name,
            location_qr_id,
            location_type,
            location_description,
        )


if __name__ == "__main__":
    create_location()
