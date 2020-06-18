#!/usr/bin/env python3

import re
import click

from cdbApi import SimpleLocationInformation
from cdbApi import LogEntryEditInformation
from cdbApi import ItemStatusBasicObject
from cdbApi import ApiException
from cdbCli.common.cli.cliBase import CliBase


cli = CliBase()


@click.group()
def main():
    pass


##############################################################################################
#                                                                                            #
#                           Add log to item given the item's ID                              #
#                                                                                            #
##############################################################################################
def add_log_to_item_by_id(item_id, log_entry, effective_date=None):

    factory = cli.require_authenticated_api()
    itemApi = factory.getItemApi()

    log_entry_obj = LogEntryEditInformation(item_id=item_id, log_entry=log_entry, effective_date=effective_date)
    log = itemApi.add_log_entry_to_item(log_entry_edit_information=log_entry_obj)

    cli.print_cdb_obj(log)


@main.command()
@click.option('--item-id', required=True, prompt='Item ID', help='Id of item to fetch.')
@click.option('--log-entry', required=True, prompt='Log Entry', help='Log entry text')
@click.option('--effective-date', help='Effective date of log entry')
def add_log_to_item_by_id_cli(item_id, log_entry, effective_date=None):
    """Adds log to item with given ID"""
    add_log_to_item_by_id(item_id, log_entry, effective_date=None)


##############################################################################################
#                                                                                            #
#                              Update item status given item ID                              #
#                                                                                            #
##############################################################################################
def set_item_status_by_id(item_id, status):
    """
    This function updates the status of a given item.

    param item_id: ID of the inventory item. Multiple IDs can be given as a comma separated string
    param status: new status of the item. Only statuses from "statusDict.json" are allowed as parameters
    """

    factory = cli.require_authenticated_api()
    item_api = factory.getItemApi()
    prop_api = factory.getPropertyApi()

    status_prop = prop_api.get_inventory_status_property_type()

    status_list = [status.to_dict()['value'] for status in status_prop.sorted_allowed_property_value_list]

    try:
        item_status = ItemStatusBasicObject(status)
        for _id in item_id.split(','):
            try:
                result = item_api.update_item_status(item_id=_id.strip(' '), item_status_basic_object=item_status)
                log = "Status updated to " + status + " by CDB CLI"
                add_log_to_item_by_id(_id, log)

            except ApiException:
                click.echo("Error updating status")
                if status not in status_list:
                    click.echo("Please enter valid status from status list:")
                    for stat in status_list:
                        click.echo(stat)

    except KeyError:
        click.echo("Error: invalid status entered")


@main.command()
@click.option('--item-id', required=True, prompt='Item ID', help='Single item ID or list of item IDs', type=str)
@click.option('--status', required=True, prompt='Item Status', help='New Status of Item')
def set_item_status_by_id_cli(item_id, status):
    """Updates item status of item with the given ID and updates item log"""
    set_item_status_by_id(item_id, status)


#############################################################################################
#                                                                                           #
#                        Get info about catalog items from name                             #
#                                                                                           #
#############################################################################################
def get_inventory_info_by_catalog_id(catalog_id, field):
    """
    This function prints info about a given catalog item's associated inventory items.

    param catalog_id: ID of the catalog item
    param field: The name of the info field requested
    """

    factory = cli.require_api()
    item_api = factory.getItemApi()

    derived_items = item_api.get_items_derived_from_item_by_item_id(catalog_id)
    for item in derived_items:

        # Get info for inventory items
        if field == "status":
            status = (item_api.get_item_status(item.to_dict()['id'])).to_dict()['value']
            info = str(status)
        elif field == "location":
            location = (item_api.get_item_location(item.to_dict()['id'])).to_dict()['location_string']
            info = str(location)
        else:
            info = str((item.to_dict())[field])

        # Print info to user
        try:
            click.echo("\t" + (item.to_dict())['name'] + ':' + info)

        except KeyError:
            click.echo("Invalid field")
            break


def get_catalog_items_by_name(name, field, inventory):
    """
    This function prints info about catalog items given the catalog item name. Supports wildcard characters for names.

    param name: The name of the catalog item (wildcards * and ? are supported)
    param field: The name of the info field requested
    """

    factory = cli.require_api()
    item_api = factory.getItemApi()

    catalog_items = item_api.get_catalog_items()
    found = False

    # expression must be at beginning of name
    r = "^" + name.lower() + "$"

    # change SQL wildcard '?' to regex wildcard '.'
    if '?' in name:
        r = r.replace('?', '.')

    # change SQL wildcard '*' to regex wildcard '.*'
    if '*' in name:
        r = r.replace('*', '.*')

    for i in range(len(catalog_items)):

        matches = re.findall(r, (catalog_items[i]['name']).lower())

        if matches:

            # Get info to print
            if field == "status" or field == "location":
                info = "info not available for catalog items"
            else:
                info = str(catalog_items[i][field])
            click.echo("Catalog Item:")

            # Print info to user
            try:
                click.echo("\t" + catalog_items[i]['name'] + ":" + info)
            except KeyError:
                click.echo("invalid domain")

            if inventory:
                click.echo("Inventory Items:")
                get_inventory_info_by_catalog_id(catalog_items[i]['id'], field)

            found = True

    if not found:
        click.echo("invalid item name")


@main.command()
@click.option('--name', required=True, prompt='Item Name', help='name of the item (use wildcards * and ?)')
@click.option('--field', default='id', prompt='Domain Name', help='field to be returned')
@click.option('--inventory/--no-inventory', default=False, help='Flag for including inventory items')
def get_catalog_items_by_name_cli(name, field, inventory):
    """ Gets given domain for item of given name"""
    get_catalog_items_by_name(name, field, inventory)


##############################################################################################
#                                                                                            #
#                                        Set the QR ID                                       #
#                                                                                            #
##############################################################################################
def set_qr_id(item_id, qrid):
    """
    This function sets a new QR ID for a given item

    param item_id: The ID of the item
    param qrid: The new desired QR ID of the item
    """

    factory = cli.require_authenticated_api()
    item_api = factory.getItemApi()

    try:
        item = item_api.get_item_by_id(item_id)
        item.qr_id = qrid
        item_api.update_item_details(item=item)

    except ApiException:
        click.echo("Error setting QR ID")


@main.command()
@click.option('--item-id', required=True, prompt='Item ID', help='ID of the item (NOT the qr ID')
@click.option('--qr-id', required=True, prompt='QR ID', help='new QR ID', type=int)
def set_qr_id_cli(item_id, qr_id):
    """ Sets new qr ID for given item ID """
    set_qr_id(item_id, qr_id)


################################################################################################
#                                                                                              #
#                                 Set new location of item                                     #
#                                                                                              #
################################################################################################
def get_locatable_item_id_by_name(location_name):
    """
    This function gets a locatable item id from a given location name

    param location_name: The name of the location as a string
    """

    factory = cli.require_authenticated_api()
    item_api = factory.getItemApi()

    locations = item_api.get_items_by_domain("location")

    for i in range(len(locations)):

        if ((locations[i].to_dict())['name']).lower() == location_name.lower():
            return (locations[i].to_dict())['id']

    # if location name not found
    return -1


def set_location_by_id(item_id, location_id):
    """
    This function sets a new location for a given inventory item

    param item_id: The ID of the inventory item
    param location_id: The ID of the location item
    """

    factory = cli.require_authenticated_api()
    item_api = factory.getItemApi()

    location = SimpleLocationInformation(locatable_item_id=item_id, location_item_id=location_id)
    item_api.update_item_location(simple_location_information=location)

    log = "Location updated by CDB CLI"
    add_log_to_item_by_id(item_id, log)


@main.command()
@click.option('--item-id', required=True, prompt='Item ID',
              help='ID of inventory item - single id or comma separated list')
@click.option('--location', default=None, help='new location of the item')
@click.option('--location-id', default=None, help='ID of the new location item')
def set_location_cli(item_id, location, location_id):
    """Set new location for single or multiple items"""
    _ids = item_id.split(",")
    for _id in _ids:
        if location_id is None:
            try:
                location_id = get_locatable_item_id_by_name(location)
                set_location_by_id(_id.strip(" "), location_id)

            except ApiException:
                click.echo("Location Not Found")


if __name__ == "__main__":
    main()
