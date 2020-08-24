#!/usr/bin/env python3

import re
import click

from cdbApi import SimpleLocationInformation
from cdbApi import LogEntryEditInformation
from cdbApi import ItemStatusBasicObject
from cdbApi import ApiException

from CdbApiFactory import CdbApiFactory

from cdbCli.common.cli.cliBase import CliBase


class AliasedGroup(click.Group):

    def get_command(self, ctx, cmd_name):
        rv = click.Group.get_command(self, ctx, cmd_name)
        if rv is not None:
            return rv
        matches = [x for x in self.list_commands(ctx)
                   if x.startswith(cmd_name)]
        if not matches:
            return None
        elif len(matches) == 1:
            return click.Group.get_command(self, ctx, matches[0])
        ctx.fail('Too many matches: %s' % ', '.join(sorted(matches)))


@click.command(cls=AliasedGroup)
def main():
    pass


def set_cdb_dist(dist):
    """ Sets the address for the CDB distribution. Options are the sandbox, development, and production.

        :param dist: the cdb distribution
        :return cli: the CliBase object for the chose cdb distribution
    """

    cli = CliBase()

    if dist == "sandbox":
        cli.api_factory = CdbApiFactory("http://tinkerbox.aps.anl.gov:8080/cdb")
    elif dist == "dev":
        cli.api_factory = CdbApiFactory("https://cdb-dev.aps.anl.gov/cdb_dev")
    elif dist == "prod":
        cli.api_factory = CdbApiFactory("https://cdb.aps.anl.gov/cdb")
    elif dist == "local":
        cli.api_factory = CdbApiFactory("http://192.168.1.201:8080/cdb")

    return cli


#############################################################################################
#                                                                                           #
#                        Get info about catalog items from name                             #
#                                                                                           #
#############################################################################################
def get_inventory_info_by_catalog_id(catalog_id, field, cli):
    """
    This function prints info about a given catalog item's associated inventory items.

        :param catalog_id: ID of the catalog item
        :param field: The name of the info field requested
        :param cli: The necessary CliBase object
    """

    factory = cli.require_api()
    item_api = factory.getItemApi()

    derived_items = item_api.get_items_derived_from_item_by_item_id(catalog_id)
    fields_requested = field.split(",")

    for item in derived_items:

        info_list = []
        for f in fields_requested:
            f = f.strip(" ")

            # Get info for inventory items
            if f == "status":
                status = (item_api.get_item_status(item.to_dict()['id'])).to_dict()['value']
                info = str(status)
            if f == "location":
                location = (item_api.get_item_location(item.to_dict()['id'])).to_dict()['location_string']
                info = str(location)
            if f == "serial number":
                info = item.to_dict()['item_identifier1']
            else:
                try:
                    info = str(item.to_dict()[f])
                except KeyError:
                    info = "Invalid field"

            info_list.append(info)

        # Print info to user

        print_str = "\t" + item.to_dict()['name']

        for inf in info_list:
            if inf is None:
                inf = "None"
            print_str += " : " + inf

        try:
            click.echo(print_str)

        except KeyError:
            click.echo("Invalid field")
            break


def get_specific_inventory_info_by_catalog_id(catalog_id, field, inventory, cli):
    """
    This function prints info about a given catalog item's associated inventory items.

        :param catalog_id: ID of the catalog item
        :param field: The name of the info field requested
        :param inventory: The specific inventory names that are needed
        :param cli: The necessary CliBase object
    """

    factory = cli.require_api()
    item_api = factory.getItemApi()

    derived_items = item_api.get_items_derived_from_item_by_item_id(catalog_id)

    found = False

    # expression must be at beginning of name
    r = "^" + inventory.lower() + "$"

    # change SQL wildcard '?' to regex wildcard '.'
    if '?' in inventory:
        r = r.replace('?', '.')

    # change SQL wildcard '*' to regex wildcard '.*'
    if '*' in inventory:
        r = r.replace('*', '.*')

    fields_requested = field.split(",")

    for i in range(len(derived_items)):

        matches = re.findall(r, (derived_items[i].to_dict()['name']).lower())

        if matches:
            info_list = []
            for f in fields_requested:
                f = f.strip(" ").lower()

                # Get info to print
                if f == "status":
                    status = (item_api.get_item_status(derived_items[i].to_dict()['id'])).to_dict()['value']
                    info = str(status)
                if f == "location":
                    location = (item_api.get_item_location(derived_items[i].to_dict()['id'])).to_dict()['location_string']
                    info = str(location)
                if f == "serial number":
                    info = derived_items[0].to_dict()["itemIdentifier1"]
                else:
                    try:
                        info = str(derived_items[i].to_dict()[f])
                    except KeyError:
                        info = "Invalid Field"
                info_list.append(info)

            print_str = "\t" + derived_items[i].to_dict()['name']

            for inf in info_list:
                if inf is None:
                    inf = "None"
                print_str += " : " + inf

            # Print info to user
            try:
                click.echo(print_str)
            except KeyError:
                click.echo("Invalid Field")
                break

            found = True

    if not found:
        click.echo("invalid item name")


def get_catalog_items_by_name_help(name, field, inventory, cli):
    """
    This function prints info about catalog items given the catalog item name. Supports wildcard characters for names.

        :param name: The name of the catalog item (wildcards * and ? are supported)
        :param field: The name of the info field requested
        :param inventory: The inventory items needed
        :param cli: The necessary CliBase object
    """

    factory = cli.require_api()
    item_api = factory.getItemApi()

    catalog_items = item_api.get_catalog_items()

    found = False

    field_list = ["id", "name", "qr_id", "description", "location", "status", "model number", "function",
                  "technical system", "alternate name", "serial number"]

    fields_requested = field.split(",")

    if field == "?":
        click.echo("Valid fields:")
        click.echo("---------------")
        for f in field_list:
            click.echo(f)
        return

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
            info_list = []
            for f in fields_requested:
                # Get info to print
                f = f.strip(" ").lower()
                if f == "status" or f == "location" or f == "serial number":
                    info = "info not available for catalog items"
                elif f == "model number":
                    info = catalog_items[0]["itemIdentifier1"]
                elif f == "alternate name":
                    info = catalog_items[0]["itemIdentifier2"]
                elif f == "technical system":
                    info = catalog_items[0]["itemCategoryList"][0]['name']
                elif f == "function":
                    info = catalog_items[0]["itemTypeList"][0]['name']
                else:
                    try:
                        info = str(catalog_items[i][f])
                    except KeyError:
                        info = "Invalid Field"
                info_list.append(info)

            # Print info to user
            click.echo("Catalog Item:")

            print_str = "\t" + catalog_items[i]['name']

            for inf in info_list:
                if inf is None:
                    inf = "None"
                print_str += " : " + inf

            try:
                click.echo(print_str)
            except KeyError:
                click.echo("Invalid Field")

            if inventory:
                if inventory == "all":
                    click.echo("Inventory Items:")
                    get_inventory_info_by_catalog_id(catalog_items[i]['id'], field, cli)
                else:
                    click.echo("Inventory Items:")
                    get_specific_inventory_info_by_catalog_id(catalog_items[i]['id'], field, inventory, cli)

            found = True

    if not found:
        click.echo("invalid item name")


@main.command()
@click.option('--name', required=True, prompt='Item Name', help='name of the item (use wildcards * and ?)')
@click.option('--field', prompt='Field Name', help='field to be returned (use ? to get options)')
@click.option('--inventory', help='Inventory items to be included (use "all" to get all inventory for that catalog item')
@click.option('--dist', help='Change the CDB distribution (sandbox, dev, prod)')
def get_catalog_items_by_name(name, field, inventory, dist=None):
    """ Gets given field(s) and inventory item(s) for given catalog item

        \b
        * For multi-word entries enclose in ""
        * Wildcards work for catalog names and inventory names
        * Wildcard (?): Use ? as any single character
        * Wildcard (*): Use * to capture any amount of characters

        \b
        Example: get-catalog-items-by-name --name "001 - CDB*" --field id --inventory "unit: 1*"
        Example: get-catalog-items-by-name --name "001 ? CDB Test Component" --field "qr_id, description"
    """
    cli = set_cdb_dist(dist)
    get_catalog_items_by_name_help(name, field, inventory, cli)


################################################################################################
#                                                                                              #
#                                 Get location ID by name                                      #
#                                                                                              #
################################################################################################
def get_location_id_by_name_help(location_name, cli):
    """
    This function gets a locatable item id from a given location name

        :param location_name: The name of the location as a string
        :param cli: necessary CliBase object
    """

    factory = cli.require_api()
    item_api = factory.getItemApi()

    catalog = item_api.get_catalog_items()
    print(len(catalog))

    inventory = item_api.get_items_by_domain(domain_name="inventory")
    print(len(inventory))

    locations = item_api.get_items_by_domain("location")

    # expression must be at beginning of name
    r = "^" + location_name.lower() + "$"

    # change SQL wildcard '?' to regex wildcard '.'
    if '?' in location_name:
        r = r.replace('?', '.')

    # change SQL wildcard '*' to regex wildcard '.*'
    if '*' in location_name:
        r = r.replace('*', '.*')

    found = False
    click.echo("Location: ID")
    click.echo("--------------")
    for i in range(len(locations)):

        matches = re.findall(r, (locations[i].to_dict()['name']).lower())

        if matches:
            print_string = locations[i].to_dict()['name'] + ": " + str(locations[i].to_dict()['id'])
            click.echo(print_string)
            found = True
    if not found:
        click.echo("Location not found")


@main.command()
@click.option('--location-name', required=True, prompt='Location Name:', help='Location Name (use wildcards ? and *)')
@click.option('--dist', help='Change the CDB distribution (sandbox, dev, prod)')
def get_location_id_by_name(location_name, dist=None):
    """Gets the corresponding ID for a location name

        \b
        * For multi-word entries enclose entry in ""
        * Wildcard (?): Use ? as any single character
        * Wildcard (*): Use * to capture any amount of characters

        \b
        Example: get-location-id-by-name --location-name 335*
        Example: get-location-id-by-name --location-name "335?C?shelf 9"
    """
    cli = set_cdb_dist(dist)
    get_location_id_by_name_help(location_name, cli)


##############################################################################################
#                                                                                            #
#                           Add log to item given the item's ID                              #
#                                                                                            #
##############################################################################################
def set_item_log_by_id_help(item_id, log_entry, cli, effective_date=None):
    """Helper function to set a log for an item in CDB

        :param item_id: item ID of the object which the log is being written for
        :param log_entry: the log entry to be written
        :param cli: necessary CliBase object
        :param effective_date: optional date of log"""

    try:
        factory = cli.require_authenticated_api()
        itemApi = factory.getItemApi()
    except ApiException:
        click.echo("Unauthorized User/ Wrong Username or Password. Try again.")
        return
    for _id in item_id.split(','):
        try:
            log_entry_obj = LogEntryEditInformation(item_id=_id, log_entry=log_entry, effective_date=effective_date)
            itemApi.add_log_entry_to_item(log_entry_edit_information=log_entry_obj)
        except ApiException as e:
            p = r'"localizedMessage.*'
            matches = re.findall(p, e.body)
            if matches:
                error = "Error uploading log entry: " + matches[0][:-2]
                click.echo(error)
            else:
                click.echo("Error uploading log entry")
            exit(1)
    click.echo("Log updated successfully")


@main.command()
@click.option('--item-id', required=True, prompt='Item ID', help='Id of item to fetch.')
@click.option('--log-entry', required=True, prompt='Log Entry', help='Log entry text')
@click.option('--effective-date', help='Effective date of log entry. Format: "yyy-mm-dd" Default to todays date')
@click.option('--dist', help='Change the CDB distribution (sandbox, dev, prod)')
def set_item_log_by_id(item_id, log_entry, dist=None, effective_date=None):
    """Adds a log entry to the item with the given ID

        \b
        Example (default date): add-log-to-item-by-id --item-id 2160 --log-entry "Test entry"
        Example (custom date): add-log-to-item-by-id --item-id 2160 --log-entry "Test entry" --effective-date 2020-06-22
        Example (multiple IDs): add-log-to-item-by-id --item-id "2160, 3604, 3605" --log-entry "Test entry"
    """
    cli = set_cdb_dist(dist)
    set_item_log_by_id_help(item_id, log_entry, cli, effective_date)


##############################################################################################
#                                                                                            #
#                              Update item status given item ID                              #
#                                                                                            #
##############################################################################################
def set_item_status_by_id_help(item_id, status, cli):
    """
    This function updates the status of a given item.

        :param item_id: ID of the inventory item. Multiple IDs can be given as a comma separated string
        :param status: new status of the item. Only statuses from "statusDict.json" are allowed as parameters
        :param cli: necessary CliBase object
    """

    factory = cli.require_authenticated_api()
    item_api = factory.getItemApi()
    prop_api = factory.getPropertyApi()

    status_prop = prop_api.get_inventory_status_property_type()

    status_list = [status.to_dict()['value'] for status in status_prop.sorted_allowed_property_value_list]

    if status == "?":
        click.echo("Status Options:")
        click.echo("----------------")
        for stat in status_list:
            click.echo(stat)
        return

    try:
        item_status = ItemStatusBasicObject(status=status)

        for _id in item_id.split(','):
            try:
                result = item_api.update_item_status(item_id=int(_id.strip(' ')), item_status_basic_object=item_status)
                log = "Status updated to " + status + " by CDB CLI"
                set_item_log_by_id_help(_id, log, cli, effective_date=None)
            except ApiException as e:
                p = r'"localizedMessage.*'
                matches = re.findall(p, e.body)
                if matches:
                    error = "Error updating status: " + matches[0][:-2]
                    click.echo(error)
                else:
                    click.echo("Error updating status")
                if status not in status_list:
                    click.echo("Please enter valid status from status list:")
                    for stat in status_list:
                        click.echo(stat)
                exit(1)

    except KeyError:
        click.echo("Error: invalid status entered")


@main.command()
@click.option('--item-id', required=True, prompt='Item ID', help='Single item ID or list of item IDs', type=str)
@click.option('--status', required=True, prompt='Item Status (? for options)', help='New Status of Item')
@click.option('--dist', help='Change the CDB distribution (sandbox, dev, prod)')
def set_item_status_by_id(item_id, status, dist=None):
    """Updates item status of item with the given ID and updates item log

        \b
        * For multi-word entries, enclose entry in ""
        * For list of IDs: enter as "ID1, ID2, ID3, ..."

        \b
        Example (single item): set-item-status-by-id --item-id 2160 --status "Ready For Use"
        Example (multi-item): set-item-status-by-id --item-id "2160, 3604, 3605" --status Spare
    """
    cli = set_cdb_dist(dist)
    set_item_status_by_id_help(item_id, status, cli)


################################################################################################
#                                                                                              #
#                                 Set new location of item                                     #
#                                                                                              #
################################################################################################
def set_location_by_id_help(item_id, location_id, cli):
    """
    This function sets a new location for a given inventory item

        :param item_id: The ID of the inventory item
        :param location_id: The ID of the location item
        :param cli: necessary CliBase object
    """

    factory = cli.require_authenticated_api()
    item_api = factory.getItemApi()

    _ids = item_id.split(",")
    for _id in _ids:
        location = SimpleLocationInformation(locatable_item_id=_id.strip(" "), location_item_id=location_id)
        try:
            item_api.update_item_location(simple_location_information=location)
        except ApiException as e:
            p = r'"localizedMessage.*'
            matches = re.findall(p, e.body)
            if matches:
                error = "Error updating location: " + matches[0][:-2]
                click.echo(error)
            exit(1)

    log = "Location updated by CDB CLI"
    set_item_log_by_id_help(item_id, log, cli, effective_date=None)


@main.command()
@click.option('--item-id', required=True, prompt='Item ID',
              help='ID of inventory item - single id or comma separated list')
@click.option('--location-id', required=True, prompt='Item Location ID', help='New location ID for item')
@click.option('--dist', help='Change the CDB distribution (sandbox, dev, prod)')
def set_location_by_id(item_id, location_id, dist=None):
    """Set new location for single or multiple items using location ID

        \b
        * For list of IDs: enter as "ID1, ID2, ID3, ..."

        \b
        Example (single item): set-location-by-id --item-id 2160 --location-id 6565
        Example (multi-item): set-location-by-id --item-id "2160, 3604, 3605" --location-id 6565"""
    cli = set_cdb_dist(dist)
    set_location_by_id_help(item_id, location_id, cli)


##############################################################################################
#                                                                                            #
#                             Set the QR ID of an inventory item                             #
#                                                                                            #
##############################################################################################
def set_qr_id_by_id_help(item_id, qrid, cli):
    """
    This function sets a new QR ID for a given item

        :param item_id: The ID of the item
        :param qrid: The new desired QR ID of the item
        :param cli: necessary CliBase object
    """

    factory = cli.require_authenticated_api()
    item_api = factory.getItemApi()

    try:
        item = item_api.get_item_by_id(item_id)
        item.qr_id = qrid
        item_api.update_item_details(item=item)
    except ApiException as e:
        p = r'"localizedMessage.*'
        matches = re.findall(p, e.body)
        if matches:
            error = "Error setting QR ID: " + matches[0][:-2]
            click.echo(error)
        else:
            click.echo("Error setting QR ID")
        exit(1)


@main.command()
@click.option('--item-id', required=True, prompt='Item ID', help='ID of the item (NOT the qr ID)')
@click.option('--qr-id', required=True, prompt='QR ID', help='new QR ID', type=int)
@click.option('--dist', help='Change the CDB distribution (sandbox, dev, prod)')
def set_qr_id_by_id(item_id, qr_id, dist=None):
    """ Sets new qr ID for given item ID

        \b
        Example: set-qr-id-by-id --item-id 2106 --qr-id 1359
    """
    cli = set_cdb_dist(dist)
    set_qr_id_by_id_help(item_id, qr_id, cli)


if __name__ == "__main__":
    main()
