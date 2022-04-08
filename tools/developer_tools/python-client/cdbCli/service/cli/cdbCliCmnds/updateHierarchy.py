#!/usr/bin/env python3

import sys
import re
import csv
import click

from rich import print
from cdbApi import ApiException

from cdbCli.common.cli.cliBase import CliBase
from cdbCli.service.cli.cdbCliCmnds.setItemLogById import set_item_log_by_id_helper


################################################################################################
#                                                                                              #
#                            Update Assembly Part Name                                         #
#                                                                                              #
################################################################################################
def update_item_assembly_help(item_api, item_id, part_name, assigned_item_id):
    """
    This function updates fields on a CDB item

        :param item_api: Item Api object
        :param item_id: The ID of the inventory item assembly
        :param part_name: The part_name of the field for the assignment
        :param assigned_item_id: The id of the item to be assigned to the part_name
    """
    try:
        item = item_api.get_item_by_id(item_id)
        item_hierarchy = item_api.get_item_hierarchy_by_id(item_id)
        element_dict = {
            item_hierarchy.child_items[i]
            .element_name: item_hierarchy.child_items[i]
            .element_id
            for i in range(len(item_hierarchy.child_items))
        }
        item_hierarchy_after_assignment = item_api.update_contained_item(
            element_dict[part_name], assigned_item_id
        )
        element_dict_after = {
            item_hierarchy_after_assignment.child_items[i]
            .element_name: item_hierarchy_after_assignment.child_items[i]
            .element_id
            for i in range(len(item_hierarchy_after_assignment.child_items))
        }
        print(
            "ItemId: "
            + str(item_id)
            + ", New ("
            + part_name
            + "): "
            + str(element_dict_after[part_name])
        )
    except ApiException as e:
        p = r'"localizedMessage.*'
        matches = re.findall(p, e.body)
        if matches:
            print(
                "ItemId:,"
                + str(item_id)
                + "Error:Error updating assembly item: "
                + matches[0][:-2]
            )
    else:
        log = (
            "ItemId: "
            + str(item_id)
            + ", New ("
            + part_name
            + "): "
            + str(element_dict_after[part_name])
        )
        set_item_log_by_id_helper(item_api=item_api, item_id=item_id, log_entry=log)


@click.command()
@click.option(
    "--inputfile",
    help="Input csv file with id, part name, assigned id. default is STDIN",
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
    "--assigned-item-id-type",
    default="id",
    type=click.Choice(["id", "qr_id"], case_sensitive=False),
    help="Allowed values are 'id'(default) or 'qr_id'",
)
@click.option("--dist", help="Change the CDB distribution (as provided in cdb.conf)")
def update_hierarchy(inputfile, item_id_type, assigned_item_id_type, dist=None):
    """Updates item hierarchy (e.g. assemblies)

    \b
    Example (file): update-hierarchy --input-file=filename.csv --item-id-type=qr_id --assigned-id-type=qr_id
    Example (pipe): cat filename.csv | update-hierarchy
    Example (terminal): update-hierarchy --location-id-type=qr_id
                        header
                        <insert item id>,<insert part name>,<insert assigned item qr_id>

    Input is either through a named file or through STDIN.   Default is STDIN
    The format of the input data is
    <Item ID>,<Part Name>,<Item ID to Assign to Part>   where the ID is by the type specified by the commandline."""

    cli = CliBase(dist)
    try:
        factory = cli.require_authenticated_api()
    except ApiException:
        click.echo("Unauthorized User/ Wrong Username or Password. Try again.")
        return

    item_api = factory.getItemApi()

    reader = csv.reader(inputfile)

    # Remove header of input stream
    next(reader)

    # Parse data of csv
    for row in reader:
        item_id = row[0]
        part_name = row[1]
        assigned_item_id = row[2]

        # Get ids if we were given QR Codes.
        if item_id_type == "qr_id":
            item_id = str(item_api.get_item_by_qr_id(int(item_id)).id)
        if assigned_item_id_type == "qr_id":
            assigned_item_id = item_api.get_item_by_qr_id(int(assigned_item_id)).id

        update_item_assembly_help(item_api, item_id, part_name, assigned_item_id)


if __name__ == "__main__":
    update_hierarchy()
