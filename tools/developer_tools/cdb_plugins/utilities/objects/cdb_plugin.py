#!/usr/bin/env python
"""
Copyright (c) UChicago Argonne, LLC. All rights reserved.
See LICENSE file.
"""

import os, shutil

CDB_XHTML_PLUGIN_PATH = '%s/%s/web/views/plugins/private'
CDB_JAVA_PLUGIN_PATH = '%s/%s/src/java/gov/anl/aps/cdb/portal/plugins/support'
CDB_PYTHON_PLUGIN_PATH = '%s/%s/cdb_web_service/plugins'

CDB_WEB_SERVICE_CODE_PATH = 'src/python/cdb'
CDB_PORTAL_CODE_PATH = 'src/java/CdbWebPortal'


class CdbPlugin():
    def __init__(self, plugin_name, cdb_plugin_directory, cdb_dist_directory):
        self.plugin_name = plugin_name

        (self.xhtml_path, self.java_path, self.python_path) = self.generate_saved_paths(cdb_plugin_directory)
        (self.deploy_xhtml_path, self.deploy_java_path, self.deploy_python_path) = self.generate_deployed_paths(
            cdb_dist_directory)

    def has_xhtml(self):
        return self.__validate_path(self.xhtml_path)

    def has_java(self):
        return self.__validate_path(self.java_path)

    def has_python(self):
        return self.__validate_path(self.python_path)

    def has_deployed_python(self):
        return self.__validate_path(self.deploy_python_path)

    def has_deployed_java(self):
        return self.__validate_path(self.deploy_java_path)

    def has_deployed_xhtml(self):
        return self.__validate_path(self.deploy_xhtml_path)

    def copy_plugin_to_distribution(self):
        if self.has_deployed_java():
            shutil.rmtree(self.deploy_java_path)
        if self.has_deployed_xhtml():
            shutil.rmtree(self.deploy_xhtml_path)
        if self.has_deployed_python():
            shutil.rmtree(self.deploy_python_path)

        if self.has_python():
            shutil.copytree(self.python_path, self.deploy_python_path)
        if self.has_java():
            shutil.copytree(self.java_path, self.deploy_java_path)
        if self.has_xhtml():
            shutil.copytree(self.xhtml_path, self.deploy_xhtml_path)

    def save_plugin_to_saved_plugins_directory(self):
        if self.has_java():
            shutil.rmtree(self.java_path)
        if self.has_python():
            shutil.rmtree(self.python_path)
        if self.has_xhtml():
            shutil.rmtree(self.xhtml_path)

        if self.has_deployed_java():
            shutil.copytree(self.deploy_java_path, self.java_path)
        if self.has_deployed_xhtml():
            shutil.copytree(self.deploy_xhtml_path, self.xhtml_path)
        if self.has_deployed_python():
            shutil.copytree(self.deploy_python_path, self.python_path)

    def remove_plugin_from_distribution(self):
        if self.has_deployed_python():
            shutil.rmtree(self.deploy_python_path)
        if self.has_deployed_xhtml():
            shutil.rmtree(self.deploy_xhtml_path)
        if self.has_deployed_java():
            shutil.rmtree(self.deploy_java_path)

    def __validate_path(self, path):
        if path is not None:
            return os.path.exists(path)
        return False

    def generate_saved_paths(self, cdb_plugin_directory):
        plugin_xhtml_dir = '%s/%s/%s' % (cdb_plugin_directory, self.plugin_name, "xhtml")
        plugin_java_dir = '%s/%s/%s' % (cdb_plugin_directory, self.plugin_name, "java")
        plugin_python_dir = '%s/%s/%s' % (cdb_plugin_directory, self.plugin_name, "python")

        return (plugin_xhtml_dir, plugin_java_dir, plugin_python_dir)

    def generate_deployed_paths(self, cdb_dist_path):
        plugin_xhtml_dir = '%s/%s' % (CdbPlugin.get_xhtml_plugin_path(cdb_dist_path), self.plugin_name)
        plugin_java_dir = '%s/%s' % (CdbPlugin.get_java_plugin_path(cdb_dist_path), self.plugin_name)
        plugin_python_dir = '%s/%s' % (CdbPlugin.get_python_plugin_path(cdb_dist_path), self.plugin_name)

        return (plugin_xhtml_dir, plugin_java_dir, plugin_python_dir)

    @staticmethod
    def get_xhtml_plugin_path(CDB_DIST_PATH):
        return CDB_XHTML_PLUGIN_PATH % (CDB_DIST_PATH, CDB_PORTAL_CODE_PATH)

    @staticmethod
    def get_java_plugin_path(CDB_DIST_PATH):
        return CDB_JAVA_PLUGIN_PATH % (CDB_DIST_PATH, CDB_PORTAL_CODE_PATH)

    @staticmethod
    def get_python_plugin_path(CDB_DIST_PATH):
        return CDB_PYTHON_PLUGIN_PATH % (CDB_DIST_PATH, CDB_WEB_SERVICE_CODE_PATH)
