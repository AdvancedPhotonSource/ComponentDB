#!/usr/bin/env python3

import re
import click

from cdbApi import SimpleLocationInformation
from cdbApi import LogEntryEditInformation
from cdbApi import ItemStatusBasicObject
from cdbApi import ApiException

from CdbApiFactory import CdbApiFactory

from cdbClick.common.cli.cliBase import CliBase



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
#    print(len(catalog))

    inventory = item_api.get_items_by_domain(domain_name="inventory")
#    print(len(inventory))

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


@click.command()
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
#    cli = set_cdb_dist(dist)
    cli = CliBase(dist)
    get_location_id_by_name_help(location_name, cli)



if __name__ == "__main__":
    get_location_id_by_name()
