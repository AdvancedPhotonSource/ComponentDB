#!/usr/bin/env python
"""
Copyright (c) UChicago Argonne, LLC. All rights reserved.
See LICENSE file.
"""

import os

ABOUT_PAGE_PLUGIN_DIALOGS_XHTML_FILENAME = 'aboutPagePluginDialogs.xhtml'
ABOUT_PAGE_PLUGIN_LINKS_XHTML_FILENAME = 'aboutPagePluginLinks.xhtml'
ITEM_DOMAIN_CATALOG_LIST_ACTION_BUTTONS_XHTML_FILENAME = 'itemDomainCatalogListActionButtons.xhtml'
ITEM_DOMAIN_CATALOG_LIST_DIALOGS_XHTML_FILENAME = 'itemDomainCatalogListDialogs.xhtml'
ITEM_DOMAIN_CATALOG_DETAIL_VIEW_SECTIONS = 'catalogDetailsViewSections.xhtml'
ITEM_DOMAIN_INVENTORY_DETAIL_VIEW_SECTIONS = 'inventoryDetailsViewSections.xhtml'
ITEM_DOMAIN_CABLE_CATALOG_DETAIL_VIEW_SECTIONS = 'cableCatalogDetailsViewSections.xhtml'
ITEM_DOMAIN_CABLE_INVENTORY_DETAIL_VIEW_SECTIONS = 'cableInventoryDetailsViewSections.xhtml'
ITEM_DOMAIN_MACHINE_DESIGN_DETAIL_VIEW_SECTIONS = 'itemMachineDesignDetailsViewSections.xhtml'
ITEM_DOMAIN_MACHINE_DESIGN_INVENTORY_DETAIL_VIEW_SECTIONS = 'itemMachineDesignInventoryDetailsViewSections.xhtml'
ITEM_DOMAIN_MACHINE_DESIGN_DELETED_ITEMS_DETAIL_VIEW_SECTIONS = 'itemMachineDesignDeletedItemsDetailsViewSections.xhtml'
PROPERTY_INFO_ACTION_DIALOGS_XHTML_FILENAME = 'propertyInfoActionDialogs.xhtml'
PROPERTY_EDIT_LIST_SUPPORT_FILENAME = 'propertyEditListSupport.xhtml'
PROPERTY_EDIT_OBJECT_SUPPORT_FILENAME = 'propertyEditObjectSupport.xhtml'
PROPERTY_EDIT_OBJECT_VALUES_FILENAME = 'propertyEditObjectValues.xhtml'
MULTI_EDIT_CATALOG_CONFIGURATION_FILENAME = 'catalogMultiEditConfigurations.xhtml'
MULTI_EDIT_CABLE_INVENTORY_CONFIGURATION_FILENAME = 'cableInventoryMultiEditConfigurations.xhtml'
MULTI_EDIT_INVENTORY_CONFIGURATION_FILENAME = 'inventoryMultiEditConfigurations.xhtml'
MULTI_EDIT_CATALOG_LIST_OBJECT_FILENAME = 'catalogMultiEditEditableListObjects.xhtml'
MULTI_EDIT_CABLE_INVENTORY_LIST_OBJECT_FILENAME = 'cableInventoryMultiEditEditableListObjects.xhtml'
MULTI_EDIT_INVENTORY_LIST_OBJECT_FILENAME = 'inventoryMultiEditEditableListObjects.xhtml'
MULTI_EDIT_CATALOG_LIST_SUPPORT_FILENAME = 'catalogMultiEditEditableListSupports.xhtml'
MULTI_EDIT_CABLE_INVENTORY_LIST_SUPPORT_FILENAME = 'cableInventoryMultiEditEditableListSupports.xhtml'
MULTI_EDIT_INVENTORY_LIST_SUPPORT_FILENAME = 'inventoryMultiEditEditableListSupports.xhtml'

PLUGIN_ABOUT_DIALOGS_PATH = 'about/dialogs.xhtml'
PLUGIN_ABOUT_LINKS_PATH = 'about/links.xhtml'
PLUGIN_ITEM_DOMAIN_CATALOG_LIST_DIALOGS_PATH = 'item/domainCatalogListDialogs.xhtml'
PLUGIN_ITEM_DOMAIN_CATALOG_LIST_ACTION_BUTTONS_PATH = 'item/domainCatalogListButtons.xhtml'
PLUGIN_ITEM_DETAIL_VIEW_SECTION_BASE_PATH = 'itemDetailsViewSections'
PLUGIN_ITEM_DOMAIN_CATALOG_VIEW_SECTION_PATH = '%s/catalogSections.xhtml' % PLUGIN_ITEM_DETAIL_VIEW_SECTION_BASE_PATH
PLUGIN_ITEM_DOMAIN_INVENTORY_VIEW_SECTION_PATH = '%s/inventorySections.xhtml' % PLUGIN_ITEM_DETAIL_VIEW_SECTION_BASE_PATH
PLUGIN_ITEM_DOMAIN_CABLE_CATALOG_VIEW_SECTION_PATH = '%s/cableCatalogSections.xhtml' % PLUGIN_ITEM_DETAIL_VIEW_SECTION_BASE_PATH
PLUGIN_ITEM_DOMAIN_CABLE_INVENTORY_VIEW_SECTION_PATH = '%s/cableInventorySections.xhtml' % PLUGIN_ITEM_DETAIL_VIEW_SECTION_BASE_PATH
PLUGIN_ITEM_DOMAIN_MACHINE_DESIGN_VIEW_SECTION_PATH = '%s/itemMachineDesignSections.xhtml' % PLUGIN_ITEM_DETAIL_VIEW_SECTION_BASE_PATH
PLUGIN_ITEM_DOMAIN_MACHINE_DESIGN_INVENTORY_VIEW_SECTION_PATH = '%s/itemMachineDesignInventorySections.xhtml' % PLUGIN_ITEM_DETAIL_VIEW_SECTION_BASE_PATH
PLUGIN_ITEM_DOMAIN_MACHINE_DESIGN_DELETED_ITEMS_VIEW_SECTION_PATH = '%s/itemMachineDesignDeletedItemsSections.xhtml' % PLUGIN_ITEM_DETAIL_VIEW_SECTION_BASE_PATH
PLUGIN_PROPERTY_INFO_ACTION_DIALOGS_PATH = 'propertyValue/infoActionDialogs.xhtml'
PLUGIN_PROPERTY_EDIT_LIST_SUPPORT_PATH = 'propertyValue/editListSupport.xhtml'
PLUGIN_PROPERTY_EDIT_OBJECT_SUPPORT_PATH = 'propertyValue/editObjectSupport.xhtml'
PLUGIN_PROPERTY_EDIT_OBJECT_VALUES_PATH = 'propertyValue/editObjectValues.xhtml'
PLUGIN_MULTI_EDIT_BASE_PATH = 'itemMultiEdit'
PLUGIN_MULTI_EDIT_CATALOG_CONFIGURATION_PATH = '%s/catalogConfiguration.xhtml' % PLUGIN_MULTI_EDIT_BASE_PATH
PLUGIN_MULTI_EDIT_CABLE_INVENTORY_CONFIGURATION_PATH = '%s/cableInventoryConfiguration.xhtml' % PLUGIN_MULTI_EDIT_BASE_PATH
PLUGIN_MULTI_EDIT_INVENTORY_CONFIGURATION_PATH = '%s/inventoryConfiguration.xhtml' % PLUGIN_MULTI_EDIT_BASE_PATH
PLUGIN_MULTI_EDIT_CATALOG_LIST_OBJECT_PATH = '%s/catalogEditableListObject.xhtml' % PLUGIN_MULTI_EDIT_BASE_PATH
PLUGIN_MULTI_EDIT_CABLE_INVENTORY_LIST_OBJECT_PATH = '%s/cableInventoryEditableListObject.xhtml' % PLUGIN_MULTI_EDIT_BASE_PATH
PLUGIN_MULTI_EDIT_INVENTORY_LIST_OBJECT_PATH = '%s/inventoryEditableListObject.xhtml' % PLUGIN_MULTI_EDIT_BASE_PATH
PLUGIN_MULTI_EDIT_CATALOG_LIST_SUPPORT_PATH = '%s/catalogEditableListSupport.xhtml' % PLUGIN_MULTI_EDIT_BASE_PATH
PLUGIN_MULTI_EDIT_CABLE_INVENTORY_LIST_SUPPORT_PATH = '%s/cableInventoryEditableListSupport.xhtml' % PLUGIN_MULTI_EDIT_BASE_PATH
PLUGIN_MULTI_EDIT_INVENTORY_LIST_SUPPORT_PATH = '%s/inventoryEditableListSupport.xhtml' % PLUGIN_MULTI_EDIT_BASE_PATH

UI_INCLUDE_SYNTAX = '   <ui:include src="%s"/>\n'

PLUGIN_TEMPLATE_PATH = "../pluginTemplates"
PLUGIN_XHTML_TEMPLATE_FILENAME = "xhtml_template.xhtml"

XHTML_TEMPLATE_UI_INCLUDE_KEY = "PYTHON_UI_INCLUDE_MARKER"


class PluginXhtmlParser():
    def __init__(self, cdb_xthml_plugin_path):
        self.CDB_XHTML_PLUGIN_PATH = cdb_xthml_plugin_path

    def generate_xhtml_files_contents(self, installed_plugins):
        about_page_dialogs = ''
        about_page_links = ''
        item_domain_catalog_list_dialogs = ''
        item_domain_catalog_list_action_buttons = ''
        item_domain_catalog_view_sections = ''
        item_domain_inventory_view_sections = ''
        item_domain_cable_catalog_view_sections = ''
        item_domain_cable_inventory_view_sections = ''
        item_domain_machine_design_view_sections = ''
        item_domain_machine_design_inventory_view_sections = ''
        item_domain_machine_design_deleted_items_view_sections = ''
        property_info_action_dialogs = ''
        property_edit_list_support = ''
        property_edit_object_support = ''
        property_edit_object_values = ''

        multi_edit_catalog_configurations = ''
        multi_edit_cable_inventory_configurations = ''
        multi_edit_inventory_configurations = ''
        multi_edit_catalog_list_objects = ''
        multi_edit_cable_inventory_list_objects = ''
        multi_edit_inventory_list_objects = ''
        multi_edit_catalog_list_supports = ''
        multi_edit_cable_inventory_list_supports = ''
        multi_edit_inventory_list_supports = ''

        for cdb_plugin in installed_plugins:
            plugin_name = cdb_plugin.plugin_name

            about_page_dialogs += self.generate_ui_inlcude(PLUGIN_ABOUT_DIALOGS_PATH, plugin_name)
            about_page_links += self.generate_ui_inlcude(PLUGIN_ABOUT_LINKS_PATH, plugin_name)

            item_domain_catalog_list_dialogs += self.generate_ui_inlcude(PLUGIN_ITEM_DOMAIN_CATALOG_LIST_DIALOGS_PATH, plugin_name)
            item_domain_catalog_list_action_buttons += self.generate_ui_inlcude(PLUGIN_ITEM_DOMAIN_CATALOG_LIST_ACTION_BUTTONS_PATH, plugin_name)

            item_domain_catalog_view_sections += self.generate_ui_inlcude(PLUGIN_ITEM_DOMAIN_CATALOG_VIEW_SECTION_PATH, plugin_name)
            item_domain_inventory_view_sections += self.generate_ui_inlcude(PLUGIN_ITEM_DOMAIN_INVENTORY_VIEW_SECTION_PATH, plugin_name)

            item_domain_cable_catalog_view_sections += self.generate_ui_inlcude(PLUGIN_ITEM_DOMAIN_CABLE_CATALOG_VIEW_SECTION_PATH, plugin_name)
            item_domain_cable_inventory_view_sections += self.generate_ui_inlcude(PLUGIN_ITEM_DOMAIN_CABLE_INVENTORY_VIEW_SECTION_PATH, plugin_name)

            item_domain_machine_design_view_sections += self.generate_ui_inlcude(PLUGIN_ITEM_DOMAIN_MACHINE_DESIGN_VIEW_SECTION_PATH, plugin_name)
            item_domain_machine_design_inventory_view_sections += self.generate_ui_inlcude(PLUGIN_ITEM_DOMAIN_MACHINE_DESIGN_INVENTORY_VIEW_SECTION_PATH, plugin_name)
            item_domain_machine_design_deleted_items_view_sections += self.generate_ui_inlcude(PLUGIN_ITEM_DOMAIN_MACHINE_DESIGN_DELETED_ITEMS_VIEW_SECTION_PATH, plugin_name)

            property_info_action_dialogs += self.generate_ui_inlcude(PLUGIN_PROPERTY_INFO_ACTION_DIALOGS_PATH, plugin_name)
            property_edit_list_support += self.generate_ui_inlcude(PLUGIN_PROPERTY_EDIT_LIST_SUPPORT_PATH, plugin_name)
            property_edit_object_support += self.generate_ui_inlcude(PLUGIN_PROPERTY_EDIT_OBJECT_SUPPORT_PATH, plugin_name)
            property_edit_object_values += self.generate_ui_inlcude(PLUGIN_PROPERTY_EDIT_OBJECT_VALUES_PATH, plugin_name)

            multi_edit_catalog_configurations += self.generate_ui_inlcude(PLUGIN_MULTI_EDIT_CATALOG_CONFIGURATION_PATH, plugin_name)
            multi_edit_cable_inventory_configurations += self.generate_ui_inlcude(PLUGIN_MULTI_EDIT_CABLE_INVENTORY_CONFIGURATION_PATH, plugin_name)
            multi_edit_inventory_configurations += self.generate_ui_inlcude(PLUGIN_MULTI_EDIT_INVENTORY_CONFIGURATION_PATH, plugin_name)
            multi_edit_catalog_list_objects += self.generate_ui_inlcude(PLUGIN_MULTI_EDIT_CATALOG_LIST_OBJECT_PATH, plugin_name)
            multi_edit_cable_inventory_list_objects += self.generate_ui_inlcude(PLUGIN_MULTI_EDIT_CABLE_INVENTORY_LIST_OBJECT_PATH, plugin_name)
            multi_edit_inventory_list_objects += self.generate_ui_inlcude(PLUGIN_MULTI_EDIT_INVENTORY_LIST_OBJECT_PATH, plugin_name)
            multi_edit_catalog_list_supports += self.generate_ui_inlcude(PLUGIN_MULTI_EDIT_CATALOG_LIST_SUPPORT_PATH, plugin_name)
            multi_edit_cable_inventory_list_supports += self.generate_ui_inlcude(PLUGIN_MULTI_EDIT_CABLE_INVENTORY_LIST_SUPPORT_PATH, plugin_name)
            multi_edit_inventory_list_supports += self.generate_ui_inlcude(PLUGIN_MULTI_EDIT_INVENTORY_LIST_SUPPORT_PATH, plugin_name)


        xhtml_template_file_path = "%s/%s" % (PLUGIN_TEMPLATE_PATH, PLUGIN_XHTML_TEMPLATE_FILENAME)
        dir = os.path.dirname(__file__)
        xhtml_template_file_path = os.path.join(dir, xhtml_template_file_path)
        xhtml_template_file = open(xhtml_template_file_path, 'r')
        xhtml_template_file_content = xhtml_template_file.read()
        xhtml_template_file.close()

        result = {}
        result[ABOUT_PAGE_PLUGIN_DIALOGS_XHTML_FILENAME] = xhtml_template_file_content.replace(
            XHTML_TEMPLATE_UI_INCLUDE_KEY, about_page_dialogs)
        result[ABOUT_PAGE_PLUGIN_LINKS_XHTML_FILENAME] = xhtml_template_file_content.replace(
            XHTML_TEMPLATE_UI_INCLUDE_KEY, about_page_links)
        result[ITEM_DOMAIN_CATALOG_LIST_ACTION_BUTTONS_XHTML_FILENAME] = xhtml_template_file_content.replace(
            XHTML_TEMPLATE_UI_INCLUDE_KEY, item_domain_catalog_list_action_buttons)
        result[ITEM_DOMAIN_CATALOG_LIST_DIALOGS_XHTML_FILENAME] = xhtml_template_file_content.replace(
            XHTML_TEMPLATE_UI_INCLUDE_KEY, item_domain_catalog_list_dialogs)
        result[ITEM_DOMAIN_CATALOG_DETAIL_VIEW_SECTIONS] = xhtml_template_file_content.replace(
            XHTML_TEMPLATE_UI_INCLUDE_KEY, item_domain_catalog_view_sections)
        result[ITEM_DOMAIN_INVENTORY_DETAIL_VIEW_SECTIONS] = xhtml_template_file_content.replace(
            XHTML_TEMPLATE_UI_INCLUDE_KEY, item_domain_inventory_view_sections)
        result[ITEM_DOMAIN_CABLE_CATALOG_DETAIL_VIEW_SECTIONS] = xhtml_template_file_content.replace(
            XHTML_TEMPLATE_UI_INCLUDE_KEY, item_domain_cable_catalog_view_sections)
        result[ITEM_DOMAIN_CABLE_INVENTORY_DETAIL_VIEW_SECTIONS] = xhtml_template_file_content.replace(
            XHTML_TEMPLATE_UI_INCLUDE_KEY, item_domain_cable_inventory_view_sections)
        result[ITEM_DOMAIN_MACHINE_DESIGN_DETAIL_VIEW_SECTIONS] = xhtml_template_file_content.replace(
            XHTML_TEMPLATE_UI_INCLUDE_KEY, item_domain_machine_design_view_sections)
        result[ITEM_DOMAIN_MACHINE_DESIGN_INVENTORY_DETAIL_VIEW_SECTIONS] = xhtml_template_file_content.replace(
            XHTML_TEMPLATE_UI_INCLUDE_KEY, item_domain_machine_design_inventory_view_sections)
        result[ITEM_DOMAIN_MACHINE_DESIGN_DELETED_ITEMS_DETAIL_VIEW_SECTIONS] = xhtml_template_file_content.replace(
            XHTML_TEMPLATE_UI_INCLUDE_KEY, item_domain_machine_design_deleted_items_view_sections)
        result[PROPERTY_INFO_ACTION_DIALOGS_XHTML_FILENAME] = xhtml_template_file_content.replace(
            XHTML_TEMPLATE_UI_INCLUDE_KEY, property_info_action_dialogs)
        result[PROPERTY_EDIT_LIST_SUPPORT_FILENAME] = xhtml_template_file_content.replace(
            XHTML_TEMPLATE_UI_INCLUDE_KEY, property_edit_list_support)
        result[PROPERTY_EDIT_OBJECT_SUPPORT_FILENAME] = xhtml_template_file_content.replace(
            XHTML_TEMPLATE_UI_INCLUDE_KEY, property_edit_object_support)
        result[PROPERTY_EDIT_OBJECT_VALUES_FILENAME] = xhtml_template_file_content.replace(
            XHTML_TEMPLATE_UI_INCLUDE_KEY, property_edit_object_values)
        result[MULTI_EDIT_CATALOG_CONFIGURATION_FILENAME] = xhtml_template_file_content.replace(
            XHTML_TEMPLATE_UI_INCLUDE_KEY, multi_edit_catalog_configurations)
        result[MULTI_EDIT_CABLE_INVENTORY_CONFIGURATION_FILENAME] = xhtml_template_file_content.replace(
            XHTML_TEMPLATE_UI_INCLUDE_KEY, multi_edit_cable_inventory_configurations)
        result[MULTI_EDIT_INVENTORY_CONFIGURATION_FILENAME] = xhtml_template_file_content.replace(
            XHTML_TEMPLATE_UI_INCLUDE_KEY, multi_edit_inventory_configurations)
        result[MULTI_EDIT_CATALOG_LIST_OBJECT_FILENAME] = xhtml_template_file_content.replace(
            XHTML_TEMPLATE_UI_INCLUDE_KEY, multi_edit_catalog_list_objects)
        result[MULTI_EDIT_CABLE_INVENTORY_LIST_OBJECT_FILENAME] = xhtml_template_file_content.replace(
            XHTML_TEMPLATE_UI_INCLUDE_KEY, multi_edit_cable_inventory_list_objects)
        result[MULTI_EDIT_INVENTORY_LIST_OBJECT_FILENAME] = xhtml_template_file_content.replace(
            XHTML_TEMPLATE_UI_INCLUDE_KEY, multi_edit_inventory_list_objects)
        result[MULTI_EDIT_CATALOG_LIST_SUPPORT_FILENAME] = xhtml_template_file_content.replace(
            XHTML_TEMPLATE_UI_INCLUDE_KEY, multi_edit_catalog_list_supports)
        result[MULTI_EDIT_CABLE_INVENTORY_LIST_SUPPORT_FILENAME] = xhtml_template_file_content.replace(
            XHTML_TEMPLATE_UI_INCLUDE_KEY, multi_edit_cable_inventory_list_supports)
        result[MULTI_EDIT_INVENTORY_LIST_SUPPORT_FILENAME] = xhtml_template_file_content.replace(
            XHTML_TEMPLATE_UI_INCLUDE_KEY, multi_edit_inventory_list_supports)

        return result

    def generate_ui_inlcude(self, plugin_section_path, plugin_name):
        plugin_path = '%s/%s' % (self.CDB_XHTML_PLUGIN_PATH, plugin_name)
        full_path = '%s/%s' % (plugin_path, plugin_section_path)
        if os.path.exists(full_path):
            return UI_INCLUDE_SYNTAX % (plugin_name + '/' + plugin_section_path)
        return ''
