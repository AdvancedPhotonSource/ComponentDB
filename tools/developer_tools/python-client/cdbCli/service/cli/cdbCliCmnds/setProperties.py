#!/usr/bin/env python
# coding: utf-8

import sys
import click

import pandas as pd
from rich import print
from rich.traceback import install


from CdbApiFactory import CdbApiFactory
from cdbCli.common.cli.cliBase import CliBase


@click.command()
@click.option(
    "--inputfile",
    help="Input csv file with item info and properties, default is STDOUT",
    type=click.File("r"),
    default=sys.stdin,
)
@click.option(
    "--changemeta",
    type=click.Choice(["yes", "echo", "no"]),
    default="echo",
)
@click.option(
    "--changeproperty",
    type=click.Choice(["yes", "echo", "no"]),
    default="echo",
)
@click.pass_obj
def set_properties_helper(cli, inputfile, changemeta, changeproperty):
    """Takes a headered csv file with change data for the properties"""

    install()    
    factory = cli.require_authenticated_api()
    itemApi = factory.getItemApi()
    cableCatalogApi = factory.getCableCatalogItemApi()
    propValueApi = factory.getPropertyValueApi()

    df = pd.read_csv(inputfile)
    for i, row in df.iterrows():
        item_dict = {}
        prop_dict = {}
        meta_dict = {}
        for itemkey in row.keys():
            if not pd.isnull(row[itemkey]) and "Item_" in itemkey:
                dictkey = itemkey.replace("Item_", "")
                item_dict[dictkey.lower()] = row[itemkey]
            if not pd.isnull(row[itemkey]) and "Prop_" in itemkey:
                dictkey = itemkey.replace("Prop_", "")
                prop_dict[dictkey.lower()] = row[itemkey]
            if not pd.isnull(row[itemkey]) and "Meta_" in itemkey:
                dictkey = itemkey.replace("Meta_", "")
                meta_dict[dictkey.lower()] = row[itemkey]
        # We now have three dictionaries parsed if they pertain to the
        # Item, Properties, and Metadata Properties
        # Lets do some echoing if we need it
        # First lets change the properties
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
                        prop.descripton = prop_dict["display_value"]
                    try:
                        result = itemApi.update_item_property_value(
                            item_dict["id"], property_value=prop
                        )
                    except Exception as e:
                        print(e)
                elif changeproperty == "echo":
                    print(prop_dict)
                if changemeta == "yes":
                    metadata = propValueApi.get_property_value_metadata(prop.id)
                    for metadata_element in metadata:
                        # If the metadata key lowered is in our meta dictionary, then change
                        # the value and submit the key.
                        if metadata_element.metadata_key.lower() in meta_dict.keys():
                            metadata_element.metadata_value = str(
                                meta_dict[metadata_element.metadata_key.lower()]
                            )
                            try:
                                result = itemApi.update_item_property_metadata(
                                    item_dict["id"],
                                    prop.id,
                                    property_metadata=metadata_element,
                                )
                            except Exception as e:
                                print(e)
                        elif changemeta == "echo":
                            print(meta_dict)


def set_properties(cli, property, inputfile, qr_id, itemnumbers):
    """Takes a csv file of CDB Item, and Property Names and returns a
    dataframe of the current values in CDB or it can scan the CDB inventory for items
    that have the property and return that in the dataframe

    Args:
        property (str): _description_
        inputfile (file descripter): _description_
        itemnumbers (boolean): Take item number input from input file handler or scan CDB        

    Returns:
        Pandas Dataframe:  Dataframe with results
    """
    
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
    if not itemnumbers:
        for cat_item in catalog_items:
            for inv_item in itemApi.get_items_derived_from_item_by_item_id(cat_item.id):
                property_dicts = screen_inventory_item_for_property(
                    itemApi, propValueApi, propname, inv_item
                )
                if len(property_dicts) > 0:
                    list_of_property_dicts = list_of_property_dicts + property_dicts
    else:
        for itemnumberstr in inputfile:
            item_number = int(itemnumberstr.rstrip())
            if qr_id == "qr_id":
                inv_item = itemApi.get_item_by_qr_id(item_number)
            else:
                inv_item = itemApi.get_item_by_id(item_number)
            property_dicts = screen_inventory_item_for_property(
                itemApi, propValueApi, propname, inv_item
            )
            if len(property_dicts) > 0:
                list_of_property_dicts = list_of_property_dicts + property_dicts
    return pd.DataFrame(list_of_property_dicts)


def get_property_dictionary(inv_item, prop, propname, itemApi, propValueApi):
    """Checks the property of the inventory item to see if
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
        entity_info = itemApi.get_item_entity_info(inv_item.id)
        prop_result_dict = {}
        prop_result_dict["Item_Id"] = inv_item.id
        prop_result_dict["Item_Catalog_Name"] = inv_item.derived_from_item.name
        prop_result_dict["Item_Name"] = inv_item.name
        prop_result_dict["Item_Serial_No"] = inv_item.item_identifier1
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


def screen_inventory_item_for_property(itemApi, propValueApi, propname, inv_item):
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
    properties = itemApi.get_properties_for_item(inv_item.id)
    for prop in properties:
        prop_result_dict = get_property_dictionary(
            inv_item, prop, propname, itemApi, propValueApi
        )
        if prop_result_dict != None:
            list_of_property_dicts.append(prop_result_dict)
    return list_of_property_dicts


if __name__ == "__main__":
    set_properties()
