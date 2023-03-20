#!/usr/bin/env python
# coding: utf-8
import datetime
from mailbox import NotEmptyError
import re
import sys
import click
from datetime import date

import pandas as pd
import sqlite3
from rich import print
from rich.traceback import install


from CdbApiFactory import CdbApiFactory
from cdbCli.common.cli.cliBase import CliBase


@click.command()
@click.option(
    "--property", required=True, help="Scan the Inventory for items with this Property"
)
@click.option(
    "--itemnumbers/--no-itemnumbers",
    help="itemnumbers reads item numbers from input, otherwise scans entire database [d:--no-itemnumbers]",
    default=False,
)
@click.option("--dist", help="Change the CDB distribution (as provided in cdb.conf)")
@click.option(
    "--inputfile",
    help="Input for itemnumbers when --itemnumber selected, default is STDIN",
    type=click.File("r"),
    default=sys.stdin,
)
@click.option(
    "--printheader/--no-printheader",
    help="Output/suppress header line (default --printheader)",
    default=True,
)
@click.option(
    "--outputfile",
    help="Output csv file with item info and properties, default is STDOUT",
    type=click.File("r"),
    default=sys.stdout,
)
@click.option(
    "--item-id-type",
    default="id",
    type=click.Choice(["id", "qr_id"], case_sensitive=False),
    help="Allowed values are 'id'(default) or 'qr_id'",
)

@click.option(
    "--item-type",
    default="inventory",
    type=click.Choice(["catalog","inventory"],case_sensitive=False),
    help="Allowed cdb types are 'inventory'(default) or 'catalog' for full scan",
)
def get_properties(
    property, outputfile, inputfile, itemnumbers, item_id_type, item_type, printheader, dist=None):
    """Inspects CDB for inventory with the selected property name and outputs a
    CSV of the current values in CDB.  It can scan the CDB inventory for items
    that have the property or take selected id or qr ids from a file."""
    install(show_locals=True)
    resultDF = get_properties_helper(property, inputfile, item_id_type, item_type, itemnumbers, dist)
    resultDF.to_csv(outputfile, index=False, header=printheader)


def get_properties_helper(property, inputfile, item_id_type, item_type, itemnumbers, dist=None):
    """Takes a csv file of CDB Item, and Property Names and returns a
    dataframe of the current values in CDB or it can scan the CDB inventory for items
    that have the property and return that in the dataframe

    Args:
        property (str): _description_
        inputfile (file descripter): _description_
        itemnumbers (boolean): Take item number input from input file handler or scan CDB
        dist (str, optional): CDB Distribution. Defaults to None.

    Returns:
        Pandas Dataframe:  Dataframe with results
    """

    cli = CliBase(dist)
    factory = cli.require_api()
    itemApi = factory.getItemApi()
    cableCatalogApi = factory.getCableCatalogItemApi()
    propValueApi = factory.getPropertyValueApi()
    propname = property
    # FIXME: property_type_by_name seems broken.  Waiting for Darius
    # propTypeApi = factory.getPropertyTypeApi()
    # print("Property Name: ",propname)
    # property_id = propTypeApi.get_property_type_by_name(propname)
    # print("Property ID",property_id)
    list_of_property_dicts = []
    catalog_items = (
        itemApi.get_catalog_items() + cableCatalogApi.get_cable_catalog_item_list()
    )
    if not itemnumbers and item_type == "inventory":
        for cat_item in catalog_items:
            for inv_item in itemApi.get_items_derived_from_item_by_item_id(cat_item.id):
                property_dicts = screen_item_for_property(
                    itemApi, propValueApi, propname, inv_item
                )
                if len(property_dicts) > 0:
                    list_of_property_dicts = list_of_property_dicts + property_dicts
    elif not itemnumbers and item_type == "catalog":
        for cat_item in catalog_items:
            property_dicts = screen_item_for_property(
                itemApi, propValueApi, propname, cat_item
            )
            if len(property_dicts) > 0:
                list_of_property_dicts = list_of_property_dicts + property_dicts
    else:
        for itemnumberstr in inputfile:
            item_number = int(itemnumberstr.rstrip())
            if item_id_type == "qr_id":
                item = itemApi.get_item_by_qr_id(item_number)
            else:
                item = itemApi.get_item_by_id(item_number)
            print("Checking: ",item.id)
            property_dicts = screen_item_for_property(
                itemApi, propValueApi, propname, item
            )
            if len(property_dicts) > 0:
                list_of_property_dicts = list_of_property_dicts + property_dicts
    return pd.DataFrame(list_of_property_dicts)


def get_property_dictionary(item, prop, propname, itemApi, propValueApi):
    """Checks the property of the item to see if
    there is a match for the propname.
    name propname and then returns a list of dictionaries

    Args:
        inv_item (cdbApi.models.item.Item): Inventory Item
        prop (cdbApi.models.property_value.PropertyValue): property of the inv_item
        propname (str): name of the property to match
        itemApi (cdbApi.api.item_api.ItemApi): CDB API Object for items
        propValueApi (cdbApi.api.property_value_api.PropertyValueApi): CDB API Object for property values

    Returns:
        list: A list of dictionary objects containing item and property data
    """
    if prop.property_type.name == propname:
        entity_info = itemApi.get_item_entity_info(item.id)
        prop_result_dict = {}
        prop_result_dict["Item_Id"] = item.id
        try:
            prop_result_dict["Item_QrId"] = item.qr_id
        except:
            prop_result_dict["Item_QrId"] = "None"
        try:
            prop_result_dict["Item_Catalog_Name"] = item.derived_from_item.name
        except:
            prop_result_dict["Item_Catalog_Name"] = item.name
        prop_result_dict["Item_Name"] = item.name
        prop_result_dict["Item_Serial_No"] = item.item_identifier1
        prop_result_dict["Item_Entry_Owner"] = entity_info.owner_username
        prop_result_dict["Item_Entry_Group_Owner"] = entity_info.owner_user_group_name
        prop_result_dict["Prop_Name"] = propname
        prop_result_dict["Prop_Id"] = prop.id
        prop_result_dict["Prop_Value"] = prop.value
        prop_result_dict["Prop_Display_Value"] = prop.display_value
        prop_result_dict["Prop_Description"] = prop.description
        prop_result_dict["Prop_Tag"] = prop.tag
        prop_result_dict["Prop_Units"] = prop.units
        metadata = propValueApi.get_property_value_metadata(prop.id)
        for m in metadata:
            metadata_keystring = "Meta_" + m.metadata_key
            prop_result_dict[metadata_keystring] = m.metadata_value
        return_value = prop_result_dict
    else:
        return_value = None
    return return_value


def screen_item_for_property(itemApi, propValueApi, propname, item):
    """_summary_
    # TODO: Need to finish comments
        Args:
            itemApi (_type_): _description_
            propValueApi (_type_): _description_
            propname (_type_): _description_
            inv_item (_type_): _description_

        Returns:
            _type_: _description_
    """
    list_of_property_dicts = []
    properties = itemApi.get_properties_for_item(item.id)
    for prop in properties:
        prop_result_dict = get_property_dictionary(
            item, prop, propname, itemApi, propValueApi
        )
        if prop_result_dict != None:
            list_of_property_dicts.append(prop_result_dict)
    return list_of_property_dicts


if __name__ == "__main__":
    get_properties()
