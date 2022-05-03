#!/usr/bin/env python3

import re
import click

from rich import print
from cdbApi import ApiException


from cdbCli.common.cli.cliBase import CliBase


################################################################################################
#                                                                                              #
#                                 Get location ID by name                                      #
#                                                                                              #
################################################################################################
def get_location_id_by_name_helper(item_api, location_name):
    """
    This function gets a locatable item id from a given location name

        :param item_api: Item Api object
        :param location_name: The name of the location as a string
    """

    catalog = item_api.get_catalog_items()
    #    print(len(catalog))

    inventory = item_api.get_items_by_domain(domain_name="inventory")
    #    print(len(inventory))

    locations = item_api.get_items_by_domain("location")

    # expression must be at beginning of name
    r = "^" + location_name.lower() + "$"

    # change SQL wildcard '?' to regex wildcard '.'
    if "?" in location_name:
        r = r.replace("?", ".")

    # change SQL wildcard '*' to regex wildcard '.*'
    if "*" in location_name:
        r = r.replace("*", ".*")

    found = False
    click.echo("Location: ID")
    click.echo("--------------")
    for i in range(len(locations)):

        matches = re.findall(r, (locations[i].to_dict()["name"]).lower())

        if matches:
            print_string = (
                locations[i].to_dict()["name"]
                + ": "
                + str(locations[i].to_dict()["id"])
            )
            print(print_string)
            found = True
    if not found:
        print("Location not found")


@click.command()
@click.option(
    "--location-name",
    required=True,
    prompt="Location Name:",
    help="Location Name (use wildcards ? and *)",
)
@click.pass_obj
def get_location_id_by_name(cli, location_name):
    """Gets the corresponding ID for a location name

    \b
    * For multi-word entries enclose entry in ""
    * Wildcard (?): Use ? as any single character
    * Wildcard (*): Use * to capture any amount of characters


    Example: get-location-id-by-name --location-name 335*
    Example: get-location-id-by-name --location-name "335?C?shelf 9"
    """    
    try:
        factory = cli.require_authenticated_api()
    except ApiException:
        print("Unauthorized User/ Wrong Username or Password. Try again.")
        return
    item_api = factory.getItemApi()

    get_location_id_by_name_helper(item_api, location_name)


if __name__ == "__main__":
    get_location_id_by_name()
