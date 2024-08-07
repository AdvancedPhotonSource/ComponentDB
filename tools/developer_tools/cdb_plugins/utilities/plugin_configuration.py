#!/usr/bin/env python
"""
Copyright (c) UChicago Argonne, LLC. All rights reserved.
See LICENSE file.
"""

import os
import shutil


class PluginConfiguration():
    def __init__(self, cdb_plugin_configuration_storage):
        self.cdb_plugin_configuration_storage = cdb_plugin_configuration_storage

    def update_plugin_configuration(self, plugin_name, destination_path, configuration_extension):
        """
        Function verifies if a plugin has configuration in destination path. Generates it if needed & uses stored
        configuration (if exists). Updates configuration stored in the deployment path.

        :param plugin_name: Name of the plugin being checked
        :param destination_path: Deployment path of the plugin where a configuration file may exist
        :param configuration_extension: file extension of the destined configuration file
        """

        directory_listings = os.listdir(destination_path)
        for listing in directory_listings:
            ext_split = listing.split('.')
            ext = ext_split[-1]
            # Verify that file is not simply called the same as extension adn
            if ext_split.__len__() > 1 and ext.upper() == configuration_extension.upper():
                print('\n\nUpdating configuration for plugin: %s (%s)' % (plugin_name, listing))
                plugin_listing_path = '%s/%s' % (destination_path, listing)
                default_configuration = open(plugin_listing_path).read()
                plugin_configuration_directory = "%s/%s" % (self.cdb_plugin_configuration_storage, plugin_name)
                plugin_stored_configuration_file = "%s/%s" % (plugin_configuration_directory, listing)

                if os.path.exists(plugin_stored_configuration_file):
                    print("Using stored configuration file: %s" % plugin_stored_configuration_file)
                else:
                    print("\n\nNo configuration exists, follow prompts\n")
                    resulting_configuration = ''
                    for configuration_line in default_configuration.split('\n'):
                        if configuration_line.startswith('#'):
                            print('Configuration Comment: %s' % configuration_line[1:])
                            resulting_configuration += configuration_line
                        elif configuration_line.startswith('['):
                            print("Section: %s" % configuration_line)
                            resulting_configuration += configuration_line
                        else:
                            configuration_split = configuration_line.split('=')
                            if configuration_split.__len__() < 2:
                                # Configuration line invalid
                                continue
                            config_key = configuration_split[0]
                            config_default_value = ''
                            if configuration_split.__len__() == 2:
                                config_default_value = configuration_split[1]

                            config_value = input("%s [%s]: " % (config_key, config_default_value))
                            if config_value is None or config_value == '':
                                config_value = config_default_value
                            new_config_line = "%s=%s" % (config_key, config_value)

                            resulting_configuration += new_config_line

                        resulting_configuration += "\n"

                    # Verify that configuration path for pug-in exists
                    if not os.path.exists(plugin_configuration_directory):
                        os.mkdir(plugin_configuration_directory)

                    # Store entered configuration in configuration storage
                    config_file = open(plugin_stored_configuration_file, 'w')
                    config_file.write(resulting_configuration)
                    config_file.close()

                # Update the configuration stored.
                shutil.copyfile(plugin_stored_configuration_file, plugin_listing_path)
                print('New configuration stored in deployment: %s' % plugin_listing_path)

    @classmethod
    def backup_original_plugin_configurations(cls, plugin_storage_path, configuration_extension):
        """
        Backs up configuration files that are in the plugin storage.
        Used for the save process to avoid overriding default configuration.

        :param plugin_storage_path: The storage path of plugin where configuration will be found.
        :param configuration_extension: Extension of configuration files to be found in the plugin_storage_path
        :return The path to the directory where all the configuration files were stored.
        """
        configuration_backup_dir = None
        if os.path.exists(plugin_storage_path):
            directory_listings = os.listdir(plugin_storage_path)
            for listing in directory_listings:
                if listing.upper().endswith(configuration_extension.upper()):
                    configuration_storage_path = "%s/%s" % (plugin_storage_path, listing)
                    configuration_backup_dir = "/tmp%s" % configuration_storage_path
                    if not os.path.exists(configuration_backup_dir):
                        os.makedirs(configuration_backup_dir)
                    configuration_backup_path = "%s/%s" % (configuration_backup_dir, listing)
                    print("Storing a default plugin configuration file %s to %s: " % (listing, configuration_backup_path))
                    shutil.copyfile(configuration_storage_path, configuration_backup_path)

        return configuration_backup_dir


    @classmethod
    def restore_configuration(cls, configuration_backup_dir, plugin_storage_path):
        """
        Restores the configuration files that were backed up back into storage path.
        Used for the save process to avoid overriding default configuration.

        :param configuration_backup_dir: the path where all the backed up configuration files are stored.
        :param plugin_storage_path: The storage path of plugin where configuration will be found.
        """
        if configuration_backup_dir is not None:
            if os.path.exists(plugin_storage_path) and os.path.exists(configuration_backup_dir):
                directory_listings = os.listdir(configuration_backup_dir)
                for listing in directory_listings:
                    configuration_storage_path = "%s/%s" % (plugin_storage_path, listing)
                    configuration_backup_storage_path = "%s/%s" % (configuration_backup_dir, listing)
                    print("Reverting backup file %s to %s" % (listing, configuration_storage_path))
                    shutil.move(configuration_backup_storage_path, configuration_storage_path)

