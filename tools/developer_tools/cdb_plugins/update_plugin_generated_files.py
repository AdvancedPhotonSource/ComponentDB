#!/usr/bin/env python
"""
Copyright (c) UChicago Argonne, LLC. All rights reserved.
See LICENSE file.
"""

from utilities.plugin_manager import PluginManager

# Storage directory is irrelevant for updating deployed plugins.
plugin_manager = PluginManager(use_default_storage_directory=True)

plugin_manager.update_auto_generated_files()

