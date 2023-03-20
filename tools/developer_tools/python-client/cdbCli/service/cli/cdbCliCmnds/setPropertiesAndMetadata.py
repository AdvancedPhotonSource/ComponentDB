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
@click.option("--dist", help="Change the CDB distribution (as provided in cdb.conf)")
@click.option(
    "--inputfile",
    help="Input csv file with item info and properties, default is STDOUT",
    type=click.File("r"),
    default=sys.stdin,
)
@click.option(
    "--changemeta",
    type=click.Choice(["yes","echo","no"]),
    default="echo",
)
@click.option(
    "--changeproperty",
    type=click.Choice(["yes","echo","no"]),
    default="echo",
)

def set_properties(inputfile, changemeta, changeproperty, dist=None):
    """Takes a headered csv file with change data for the properties.  The format is the same as
    what comes out of get_properties.  Execute that first and then edit the
    resulting csv file for the new property data."""
    
    install()
    cli = CliBase(dist)
    factory = cli.require_authenticated_api()
    itemApi = factory.getItemApi()
    cableCatalogApi = factory.getCableCatalogItemApi()
    propValueApi = factory.getPropertyValueApi()

    df = pd.read_csv(inputfile)
    for i,row in df.iterrows():
        item_dict = {}
        prop_dict = {}
        meta_dict = {}
        for itemkey in row.keys():
            if not pd.isnull(row[itemkey]) and "Item_" in itemkey:
                dictkey = itemkey.replace("Item_","")
                item_dict[dictkey.lower()] = row[itemkey]
            if not pd.isnull(row[itemkey]) and "Prop_" in itemkey:
                dictkey = itemkey.replace("Prop_","")
                prop_dict[dictkey.lower()] = row[itemkey]
            if not pd.isnull(row[itemkey]) and "Meta_" in itemkey:
                dictkey = itemkey.replace("Meta_","")
                meta_dict[dictkey.lower()] = row[itemkey]
    # We now have three dictionaries parsed if they pertain to the 
    # Item, Properties, and Metadata Properties
    # Lets do some echoing if we need it
    # First lets change the properties
    # Also, we scan the property listand look for the same ID
    # as in the prop ID.   The combination of the item id and property
    # id identify the property that we are going to change.
        properties = itemApi.get_properties_for_item(item_dict["id"])
        for prop in properties:
            if prop.id == int(prop_dict["id"]):
                if changeproperty == "yes":
                    if "value" in prop_dict.keys():
                        prop.value = prop_dict["value"]
                    if "description" in prop_dict.keys():
                        prop.description = prop_dict["description"]
                    if "units" in prop_dict.keys():
                        prop.units = prop_dict["units"]
                    if "tag" in prop_dict.keys():
                        prop.tag = prop_dict["tag"]
                    if "display_value" in prop_dict.keys():
                        prop.display_value = prop_dict["display_value"]
                    try:
                        result = itemApi.update_item_property_value(item_dict["id"],property_value=prop)
                    except Exception as e:
                        print(e)
                elif changeproperty  == "echo":
                    print(prop_dict)
                if changemeta == "yes":
                    metadata = propValueApi.get_property_value_metadata(prop.id)
                    for metadata_element in metadata:
                        # If the metadata key lowered is in our meta dictionary, then change
                        # the value and submit the key.
                        if metadata_element.metadata_key.lower() in meta_dict.keys():
                            metadata_element.metadata_value = str(meta_dict[metadata_element.metadata_key.lower()])
                            try:
                                result = itemApi.update_item_property_metadata(item_dict["id"],prop.id,property_metadata=metadata_element)
                            except Exception as e:
                                print(e)
                        elif changemeta == "echo":
                            print(meta_dict)


if __name__ == "__main__":
    set_properties()
