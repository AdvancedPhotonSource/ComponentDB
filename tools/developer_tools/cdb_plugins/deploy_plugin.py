#!/usr/bin/env python3
"""
Copyright (c) UChicago Argonne, LLC. All rights reserved.
See LICENSE file.
"""
#
# Adds the plugin to distribution and configures the plugin as necessary.
#
# Usage:
# $0 [[CDB_DB_NAME]]
#

from utilities.plugin_manager import PluginManager
import sys

cdb_db_name = sys.argv[1]
if cdb_db_name == None:
    cdb_db_name = 'cdb'


plugin_manager = PluginManager(cdb_db_name)

plugin_manager.prompt_deploy_plugin()
