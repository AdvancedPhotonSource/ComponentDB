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
PROPERTY_INFO_ACTION_DIALOGS_XHTML_FILENAME = 'propertyInfoActionDialogs.xhtml'
PROPERTY_EDIT_LIST_SUPPORT_FILENAME = 'propertyEditListSupport.xhtml'
PROPERTY_EDIT_OBJECT_SUPPORT_FILENAME = 'propertyEditObjectSupport.xhtml'
PROPERTY_EDIT_OBJECT_VALUES_FILENAME = 'propertyEditObjectValues.xhtml'

PLUGIN_ABOUT_DIALOGS_PATH = 'about/dialogs.xhtml'
PLUGIN_ABOUT_LINKS_PATH = 'about/links.xhtml'
PLUGIN_ITEM_DOMAIN_CATALOG_LIST_DIALOGS_PATH = 'item/domainCatalogListDialogs.xhtml'
PLUGIN_ITEM_DOMAIN_CATALOG_LIST_ACTION_BUTTONS_PATH = 'item/domainCatalogListButtons.xhtml'
PLUGIN_PROPERTY_INFO_ACTION_DIALOGS_PATH = 'propertyValue/infoActionDialogs.xhtml'
PLUGIN_PROPERTY_EDIT_LIST_SUPPORT_PATH = 'propertyValue/editListSupport.xhtml'
PLUGIN_PROPERTY_EDIT_OBJECT_SUPPORT_PATH = 'propertyValue/editObjectSupport.xhtml'
PLUGIN_PROPERTY_EDIT_OBJECT_VALUES_PATH = 'propertyValue/editObjectValues.xhtml'

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
        property_info_action_dialogs = ''
        property_edit_list_support = ''
        property_edit_object_support = ''
        property_edit_object_values = ''

        for cdb_plugin in installed_plugins:
            plugin_name = cdb_plugin.plugin_name

            about_page_dialogs += self.generate_ui_inlcude(PLUGIN_ABOUT_DIALOGS_PATH, plugin_name)
            about_page_links += self.generate_ui_inlcude(PLUGIN_ABOUT_LINKS_PATH, plugin_name)

            item_domain_catalog_list_dialogs += self.generate_ui_inlcude(PLUGIN_ITEM_DOMAIN_CATALOG_LIST_DIALOGS_PATH, plugin_name)
            item_domain_catalog_list_action_buttons += self.generate_ui_inlcude(PLUGIN_ITEM_DOMAIN_CATALOG_LIST_ACTION_BUTTONS_PATH, plugin_name)

            property_info_action_dialogs += self.generate_ui_inlcude(PLUGIN_PROPERTY_INFO_ACTION_DIALOGS_PATH, plugin_name)
            property_edit_list_support += self.generate_ui_inlcude(PLUGIN_PROPERTY_EDIT_LIST_SUPPORT_PATH, plugin_name)
            property_edit_object_support += self.generate_ui_inlcude(PLUGIN_PROPERTY_EDIT_OBJECT_SUPPORT_PATH, plugin_name)
            property_edit_object_values += self.generate_ui_inlcude(PLUGIN_PROPERTY_EDIT_OBJECT_VALUES_PATH, plugin_name)

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
        result[PROPERTY_INFO_ACTION_DIALOGS_XHTML_FILENAME] = xhtml_template_file_content.replace(
            XHTML_TEMPLATE_UI_INCLUDE_KEY, property_info_action_dialogs)
        result[PROPERTY_EDIT_LIST_SUPPORT_FILENAME] = xhtml_template_file_content.replace(
            XHTML_TEMPLATE_UI_INCLUDE_KEY, property_edit_list_support)
        result[PROPERTY_EDIT_OBJECT_SUPPORT_FILENAME] = xhtml_template_file_content.replace(
            XHTML_TEMPLATE_UI_INCLUDE_KEY, property_edit_object_support)
        result[PROPERTY_EDIT_OBJECT_VALUES_FILENAME] = xhtml_template_file_content.replace(
            XHTML_TEMPLATE_UI_INCLUDE_KEY, property_edit_object_values)

        return result

    def generate_ui_inlcude(self, plugin_section_path, plugin_name):
        plugin_path = '%s/%s' % (self.CDB_XHTML_PLUGIN_PATH, plugin_name)
        full_path = '%s/%s' % (plugin_path, plugin_section_path)
        if os.path.exists(full_path):
            return UI_INCLUDE_SYNTAX % (plugin_name + '/' + plugin_section_path)
        return ''
