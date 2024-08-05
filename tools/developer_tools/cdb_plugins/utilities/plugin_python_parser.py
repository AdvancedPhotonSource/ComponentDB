#!/usr/bin/env python
"""
Copyright (c) UChicago Argonne, LLC. All rights reserved.
See LICENSE file.
"""

from .plugin_configuration import PluginConfiguration

CONFIGURATION_FILE_EXTENSION = "cfg"

class PluginPythonParser():

    def __init__(self, cdb_plugin_configuration_storage):
        self.plugin_configurator = PluginConfiguration(cdb_plugin_configuration_storage)

    def update_required_configuration_for_plugins(self, plugins):
        for cdb_plugin in plugins:
            if cdb_plugin.has_deployed_python():
                plugin_name = cdb_plugin.plugin_name
                plugin_deployed_path = cdb_plugin.deploy_python_path
                self.plugin_configurator.update_plugin_configuration(plugin_name, plugin_deployed_path, CONFIGURATION_FILE_EXTENSION)
