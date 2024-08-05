#!/usr/bin/env python3
"""
Copyright (c) UChicago Argonne, LLC. All rights reserved.
See LICENSE file.
"""
#
# Update required auto-generated files for plugins.
#
# Usage:
#
# $0 [[CDB_DB_NAME]] [[UPDATE_CONFIGURATION (0/1)]]
#

from utilities.plugin_manager import PluginManager
import sys

cdb_db_name = 'cdb'
if sys.argv.__len__() == 2:
    cdb_db_name = sys.argv[1]

# Storage directory is irrelevant for updating deployed plugins.
plugin_manager = PluginManager(cdb_db_name, use_default_storage_directory=True)

update_configuration = False
if sys.argv.__len__() == 3:
    input_update_configuration = int(sys.argv[2])
    if input_update_configuration is not None:
        if input_update_configuration == 1:
            update_configuration = True

plugin_manager.update_auto_generated_files(update_configuration)
