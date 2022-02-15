#!/usr/bin/env python3

import re
import click

from cdbApi import SimpleLocationInformation
from cdbApi import LogEntryEditInformation
from cdbApi import ItemStatusBasicObject
from cdbApi import ApiException

from CdbApiFactory import CdbApiFactory

from cdbClick.common.cli.cliBase import CliBase
from cdbClick.service.cli.CDBclickCmnds.setItemLogById import set_item_log_by_id_help


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
    prop_type_api = factory.getPropertyTypeApi()

    status_prop = prop_type_api.get_inventory_status_property_type()

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


@click.command()
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
    cli = CliBase(dist)
    set_item_status_by_id_help(item_id, status, cli)


if __name__ == "__main__":
    set_item_status_by_id()


