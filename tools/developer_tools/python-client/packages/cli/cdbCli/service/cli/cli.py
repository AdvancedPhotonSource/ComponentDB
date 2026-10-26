#!/usr/bin/env python3

import click

from cdbCli.common.cli.cliBase import CliBase

from cdbCli.service.cli.cdbCliCmnds.addDocumentFile import add_document_file
from cdbCli.service.cli.cdbCliCmnds.addDocumentProperty import add_document_property
from cdbCli.service.cli.cdbCliCmnds.cdb_log_to_mqtt import cdb_log_to_mqtt
from cdbCli.service.cli.cdbCliCmnds.createLocation import create_location
from cdbCli.service.cli.cdbCliCmnds.getCatalogItemsByName import get_catalog_items_by_name
from cdbCli.service.cli.cdbCliCmnds.getLocationIdByName import get_location_id_by_name
from cdbCli.service.cli.cdbCliCmnds.getProperties import get_properties
from cdbCli.service.cli.cdbCliCmnds.info import cdb_info
from cdbCli.service.cli.cdbCliCmnds.search import cdb_search
from cdbCli.service.cli.cdbCliCmnds.setItemDetails import set_item_details
from cdbCli.service.cli.cdbCliCmnds.setItemLocation import set_item_location
from cdbCli.service.cli.cdbCliCmnds.setItemLogById import set_item_log_by_id
from cdbCli.service.cli.cdbCliCmnds.setItemStatusById import set_item_status_by_id
from cdbCli.service.cli.cdbCliCmnds.setMachineInstallState import set_machine_install_state
from cdbCli.service.cli.cdbCliCmnds.setParentLocation import set_parent_location
from cdbCli.service.cli.cdbCliCmnds.setPropertiesAndMetadata import set_properties
from cdbCli.service.cli.cdbCliCmnds.setQrIdById import set_qr_id_by_id
from cdbCli.service.cli.cdbCliCmnds.updateHierarchy import update_hierarchy
from cdbCli.service.cli.cdbCliCmnds.addProperty import add_property

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
@click.option("--dist", help="Change the CDB distribution (as provided in cdb.conf)")
@click.pass_context
def entry_point(ctx, dist=None):
    ctx.obj = CliBase(dist)

def main():
    entry_point.add_command(cdb_search)
    entry_point.add_command(cdb_info)
    entry_point.add_command(add_document_file)
    entry_point.add_command(add_document_property)
    entry_point.add_command(add_property)
    entry_point.add_command(cdb_log_to_mqtt)
    entry_point.add_command(create_location)
    entry_point.add_command(get_catalog_items_by_name)
    entry_point.add_command(get_location_id_by_name)
    entry_point.add_command(get_properties)
    entry_point.add_command(set_item_details)
    entry_point.add_command(set_item_location)
    entry_point.add_command(set_item_log_by_id)
    entry_point.add_command(set_item_status_by_id)
    entry_point.add_command(set_machine_install_state)
    entry_point.add_command(set_parent_location)
    entry_point.add_command(set_properties)
    entry_point.add_command(set_qr_id_by_id)
    entry_point.add_command(update_hierarchy)

    entry_point()

    
if __name__ == "__main__":
    main()
