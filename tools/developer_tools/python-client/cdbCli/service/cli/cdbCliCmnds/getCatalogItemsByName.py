#!/usr/bin/env python3

import sys
import csv
import re
import click

from rich import print
from cdbApi import ApiException

from CdbApiFactory import CdbApiFactory

from cdbCli.common.cli.cliBase import CliBase


#############################################################################################
#                                                                                           #
#                        Get info about catalog items from name                             #
#                                                                                           #
#############################################################################################
def get_inventory_info_by_catalog_id(item_api, catalog_id, field):
    """
    This function prints info about a given catalog item's associated inventory items.

        :param item_api: Item Api object
        :param catalog_id: ID of the catalog item
        :param field: The name of the info field requested

    """

    derived_items = item_api.get_items_derived_from_item_by_item_id(catalog_id)
    fields_requested = field.split(",")

    for item in derived_items:
        csvwriter = csv.writer(sys.stdout)
        info_list = []
        for f in fields_requested:
            f = f.strip(" ")
            # Get info for inventory items

            if f == "status":
                status = item_api.get_item_status(item.id).value
                info = str(status)
            elif f == "location":
                location = item_api.get_item_location(item.id).location_string
                info = str(location)
            elif f == "id":
                info = str(item.id)
            elif f == "qr_id":
                qr_id = item.qr_id
                info = str(qr_id).zfill(9)
            elif f == "name":
                info = item.name
            elif f == "description":
                info = item.description
            elif f == "serial number":
                info = item.item_identifier1
            elif f == "alternate name":
                info = item.item_identifier2
            elif f == "technical system":
                info = [
                    item.derived_from_item.item_category_list[i].name
                    for i in range(len(item.derived_from_item.item_category_list))
                ]
            elif f == "function":
                info = [
                    item.derived_from_item.item_type_list[i].name
                    for i in range(len(item.derived_from_item.item_type_list))
                ]
            else:
                info = "Invalid Field"
            if type(info) != list:
                info_list.append(info)
            else:
                info_list = info_list + info

            for output_message in info_list:
                outputlist = []
                outputlist.append("InventoryItem")
                outputlist.append(str(catalog_id))
                outputlist.append(str(item.id))
                outputlist.append(str(item.name))
                outputlist.append(str(f))
                outputlist.append(output_message)
                csvwriter.writerow(outputlist)

        # Print info to user


def get_specific_inventory_info_by_catalog_id(item_api, catalog_id, field, inventory):
    """
    This function prints info about a given catalog item's associated inventory items.

        :param item_api: Item Api object
        :param catalog_id: ID of the catalog item
        :param field: The name of the info field requested
        :param inventory: The specific inventory names that are needed
    """

    derived_items = item_api.get_items_derived_from_item_by_item_id(catalog_id)

    # expression must be at beginning of name
    r = "^" + inventory.lower() + "$"

    # change SQL wildcard '?' to regex wildcard '.'
    if "?" in inventory:
        r = r.replace("?", ".")

    # change SQL wildcard '*' to regex wildcard '.*'
    if "*" in inventory:
        r = r.replace("*", ".*")

    fields_requested = field.split(",")

    for derived_item in derived_items:

        matches = re.findall(r, (derived_item.name).lower())

        if matches:
            csvwriter = csv.writer(sys.stdout)
            info_list = []
            for f in fields_requested:
                f = f.strip(" ").lower()

                # Get info to print
                if f == "status":
                    status = item_api.get_item_status(derived_item.id).value
                    info = str(status)
                elif f == "location":
                    location = item_api.get_item_location(
                        derived_item.id
                    ).location_string
                    info = str(location)
                elif f == "id":
                    info = str(derived_item.id)
                elif f == "qr_id":
                    qr_id = derived_item.qr_id
                    info = str(qr_id).zfill(9)
                elif f == "serial number":
                    info = derived_item.item_identifier1
                elif f == "alternate name":
                    info = derived_item.item_identifier2
                elif f == "technical system":
                    info = [
                        derived_item.derived_from_item.item_category_list[i].name
                        for i in range(
                            len(derived_item.derived_from_item.item_category_list)
                        )
                    ]
                elif f == "function":
                    info = [
                        derived_item.derived_from_item.item_type_list[i].name
                        for i in range(
                            len(derived_item.derived_from_item.item_type_list)
                        )
                    ]
                else:
                    info = "Invalid Field"
                if type(info) != list:
                    info_list.append(info)
                else:
                    info_list = info_list + info

                for output_message in info_list:
                    outputlist = []
                    outputlist.append("InventoryItem")
                    outputlist.append(str(catalog_id))
                    outputlist.append(str(derived_item.id))
                    outputlist.append(str(derived_item.name))
                    outputlist.append(str(f))
                    outputlist.append(output_message)
                    csvwriter.writerow(outputlist)


def get_catalog_items_by_name_helper(item_api, name, field, inventory):
    """
    This function prints info about catalog items given the catalog item name. Supports wildcard characters for names.

        :param item_api: Item Api object
        :param name: The name of the catalog item (wildcards * and ? are supported)
        :param field: The name of the info field requested
        :param inventory: The inventory items needed
    """

    catalog_items = item_api.get_catalog_items()

    field_list = [
        "id",
        "name",
        "qr_id",
        "description",
        "location",
        "status",
        "model number",
        "function",
        "technical system",
        "alternate name",
        "serial number",
    ]

    fields_requested = field.split(",")
    if field == "?":
        print("Valid fields:")
        print("---------------")
        for f in field_list:
            print(f)
        return

    # expression must be at beginning of name
    r = "^" + name.lower() + "$"

    # change SQL wildcard '?' to regex wildcard '.'
    if "?" in name:
        r = r.replace("?", ".")

    # change SQL wildcard '*' to regex wildcard '.*'
    if "*" in name:
        r = r.replace("*", ".*")

    csvwriter = csv.writer(sys.stdout)
    outputlist = ["ItemType", "DerivedFromItem", "ItemId", "Field", "FieldValue"]

    for catalog_item in catalog_items:

        matches = re.findall(r, (catalog_item.name).lower())

        if matches:
            csvwriter.writerow(outputlist)
            info_list = []
            for f in fields_requested:
                # Get info to print
                f = f.strip(" ").lower()
                if (
                    f == "status"
                    or f == "location"
                    or f == "serial number"
                    or f == "qr_id"
                ):
                    info = "info not available for catalog items"
                elif f == "id":
                    info = str(catalog_item.id)
                elif f == "name":
                    info = catalog_item.name
                elif f == "description":
                    info = catalog_item.description
                elif f == "model number":
                    info = catalog_item.item_identifier1
                elif f == "alternate name":
                    info = catalog_item.item_identifier2
                elif f == "technical system":
                    info = [
                        catalog_item.item_category_list[i].name
                        for i in range(len(catalog_item.item_category_list))
                    ]
                elif f == "function":
                    info = [
                        catalog_item.item_type_list[i].name
                        for i in range(len(catalog_item.item_type_list))
                    ]
                else:
                    info = "Invalid Field"
                if type(info) != list:
                    info_list.append(info)
                else:
                    info_list = info_list + info

                # Print info to user

                for output_message in info_list:
                    outputlist = []
                    outputlist.append("CatalogItem")
                    outputlist.append(str(catalog_item.id))
                    outputlist.append(str(catalog_item.id))
                    outputlist.append(str(catalog_item.name))
                    outputlist.append(str(f))
                    outputlist.append(output_message)
                    csvwriter.writerow(outputlist)

            if inventory:
                if inventory == "all":
                    get_inventory_info_by_catalog_id(item_api, catalog_item.id, field)
                else:
                    get_specific_inventory_info_by_catalog_id(
                        item_api, catalog_item.id, field, inventory
                    )


@click.command()
@click.option(
    "--name",
    required=True,
    prompt="Item Name",
    help="name of the item (use wildcards * and ?)",
)
@click.option(
    "--field", prompt="Field Name", help="field to be returned (use ? to get options)"
)
@click.option(
    "--inventory",
    help='Inventory items to be included by name (use "all" to get all inventory for that catalog item',
)
@click.pass_obj
def get_catalog_items_by_name(cli, name, field, inventory):
    """Gets given field(s) and inventory item(s) for given catalog item

    \b
    * For multi-word entries enclose in ""
    * Wildcards work for catalog names and inventory names
    * Wildcard (?): Use ? as any single character
    * Wildcard (*): Use * to capture any amount of characters

    \b
    Example: get-catalog-items-by-name --name "001 - CDB*" --field id --inventory "unit: 1*"
    Example: get-catalog-items-by-name --name "001 ? CDB Test Component" --field "qr_id, description"
    """    
    try:
        factory = cli.require_authenticated_api()
    except ApiException:
        print("Unauthorized User/ Wrong Username or Password. Try again.")
        return

    item_api = factory.getItemApi()

    get_catalog_items_by_name_helper(item_api, name, field, inventory)


if __name__ == "__main__":
    get_catalog_items_by_name()
