#!/usr/bin/env python3

import re
import click

from cdbApi import SimpleLocationInformation
from cdbApi import LogEntryEditInformation
from cdbApi import ItemStatusBasicObject
from cdbApi import ApiException

from CdbApiFactory import CdbApiFactory

from cdbCli.common.cli.cliBase import CliBase

from cdbCli.service.cli.CDBclickCmnds.getLocationIdByName import get_location_id_by_name
from cdbCli.service.cli.CDBclickCmnds.getCatalogItemsByName import get_catalog_items_by_name
from cdbCli.service.cli.CDBclickCmnds.setItemLogById import set_item_log_by_id
from cdbCli.service.cli.CDBclickCmnds.setItemStatusById import set_item_status_by_id
from cdbCli.service.cli.CDBclickCmnds.setLocationById import set_location_by_id
from cdbCli.service.cli.CDBclickCmnds.setQrIdById import set_qr_id_by_id
from cdbCli.service.cli.CDBclickCmnds.cdb_log_to_mqtt import cdb_log_to_mqtt
from cdbCli.service.cli.CDBclickCmnds.createLocation import create_location
from cdbCli.service.cli.CDBclickCmnds.getNamedItems import get_named_items
from cdbCli.service.cli.CDBclickCmnds.setItemLocation import set_item_location
from cdbCli.service.cli.CDBclickCmnds.setLocation import set_location
from cdbCli.service.cli.CDBclickCmnds.addDocumentProperty import add_http_property
from cdbCli.service.cli.CDBclickCmnds.updateParentLocation import update_parent_location
from cdbCli.service.cli.CDBclickCmnds.setMachineInstallStatuses import set_machine_install_statuses
from cdbCli.service.cli.CDBclickCmnds.updateItemDetails import update_item_details

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


@click.group(cls=AliasedGroup)
def entry_point():
    pass

def main():
    entry_point.add_command(get_location_id_by_name)
    entry_point.add_command(get_catalog_items_by_name)
    entry_point.add_command(set_item_log_by_id)
    entry_point.add_command(set_item_status_by_id)
    entry_point.add_command(set_location_by_id)
    entry_point.add_command(set_qr_id_by_id)
    entry_point.add_command(cdb_log_to_mqtt)
    entry_point.add_command(set_item_location)
    entry_point.add_command(create_location)
    entry_point.add_command(get_named_items)
    entry_point.add_command(set_location)
    entry_point.add_command(update_parent_location)
    entry_point.add_command(add_http_property)
    entry_point.add_command(set_machine_install_statuses)
    entry_point.add_command(update_item_details)
    entry_point()

    
if __name__ == "__main__":
    main()
