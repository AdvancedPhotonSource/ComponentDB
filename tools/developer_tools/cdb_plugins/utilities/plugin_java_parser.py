#!/usr/bin/env python
"""
Copyright (c) UChicago Argonne, LLC. All rights reserved.
See LICENSE file.
"""

import os

JAVA_PLUGIN_PACKAGE="gov.anl.aps.cdb.portal.plugins.support"

JAVA_TEMPLATE_IMPORT_KEY = "PYTHON_IMPORT_INSERT_MARKER"
JAVA_TEMPLATE_REGISTER_KEY = "PYTHON_REGISTER_INSERT_MARKER"
JAVA_TEMPLATE_AUTO_GEN_MESSAGE_KEY = "PYTHON_AUTO_GEN_MESSAGE"

PLUGIN_TEMPLATE_PATH = "../pluginTemplates"
PLUGIN_REGISTRAR_FILENAME = "PluginRegistrar.java"
PLUGIN_MANAGER_SUFFIX = "PluginManager.java"

PLUGIN_REGISTER_SYNTAX= "        cdbPluginManager.registerPlugin(new %s());\n"

class PluginJavaParser():
    def __init__(self, cdb_java_plugin_path):
        self.CDB_JAVA_PLUGIN_PATH = cdb_java_plugin_path

    def generate_plugin_registrar_file_contents(self, installed_plugins):
        java_imports = ""
        java_registers = ""
        auto_gen_message = "Recommended not to modify."

        for cdb_plugin in installed_plugins:
            plugin_name = cdb_plugin.plugin_name
            plugin_java_path = "%s/%s" % (self.CDB_JAVA_PLUGIN_PATH, plugin_name)
            if os.path.exists(plugin_java_path):
                directory_listings = os.listdir(plugin_java_path)
                plugin_manager_listings = []
                for directory_listing in directory_listings:
                    if directory_listing.endswith(PLUGIN_MANAGER_SUFFIX):
                        plugin_manager_listings.append(directory_listing)

                for plugin_manager_listing in plugin_manager_listings:
                    plugin_manager_name = plugin_manager_listing.split('.')[0]
                    java_imports += "\nimport " + JAVA_PLUGIN_PACKAGE + '.' + plugin_name + '.' + plugin_manager_name + ';'
                    java_registers += PLUGIN_REGISTER_SYNTAX % plugin_manager_name


        java_file_path = "%s/%s" % (PLUGIN_TEMPLATE_PATH, PLUGIN_REGISTRAR_FILENAME)
        dir = os.path.dirname(__file__)
        java_file_path = os.path.join(dir, java_file_path)
        javaFile = open(java_file_path, 'r')
        fileContents = javaFile.read()
        javaFile.close()

        fileContents = fileContents.replace(JAVA_TEMPLATE_IMPORT_KEY, java_imports)
        fileContents = fileContents.replace(JAVA_TEMPLATE_AUTO_GEN_MESSAGE_KEY, auto_gen_message)
        fileContents = fileContents.replace(JAVA_TEMPLATE_REGISTER_KEY, java_registers)

        return fileContents


