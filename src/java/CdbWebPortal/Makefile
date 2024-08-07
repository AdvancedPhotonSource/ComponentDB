# Copyright (c) UChicago Argonne, LLC. All rights reserved.
# See LICENSE file.

TOP=../../..
ANT=cdb-ant
ANT_ARGS=-Dlibs.CopyLibs.classpath=lib/org-netbeans-modules-java-j2seproject-copylibstask.jar 
BUILD_PROPERTIES_FILE=nbproject/private/private.properties
GENERIC_BUILD_PROPERTIES_FILE=$(BUILD_PROPERTIES_FILE).generic.build
GLASSFISH_WEB_XML_FILE=web/WEB-INF/glassfish-web.xml
CDB_PORTAL_PROPERTIES_FILE=src/java/cdb.portal.properties

all install: dist

clean clean-all clean-install distclean: ant-clean

.PHONY: ant-clean
ant-clean:
	$(ANT) $(ANT_ARGS)  clean

# For compiling from command line, we have to use generic build properties file
.PHONY: dist
dist: 
	python3 $(TOP)/tools/developer_tools/cdb_plugins/update_plugin_generated_files.py cdb 0
	if [ -f $(BUILD_PROPERTIES_FILE) ]; then mv $(BUILD_PROPERTIES_FILE) $(BUILD_PROPERTIES_FILE).orig; fi
	cmd="cat $(GENERIC_BUILD_PROPERTIES_FILE) | sed 's?CDB_GLASSFISH_DIR?$${CDB_GLASSFISH_DIR}?g' > $(BUILD_PROPERTIES_FILE)" && eval $$cmd
	cmd="cat $(GLASSFISH_WEB_XML_FILE).template | sed 's?CDB_DATA_DIR?/cdb?g' > $(GLASSFISH_WEB_XML_FILE)" && eval $$cmd
	cmd="cat $(CDB_PORTAL_PROPERTIES_FILE).template | sed 's?CDB_DATA_DIR?/cdb?g' > $(CDB_PORTAL_PROPERTIES_FILE)" && eval $$cmd
	$(ANT) $(ANT_ARGS) dist 
	if [ -f $(BUILD_PROPERTIES_FILE).orig ]; then mv $(BUILD_PROPERTIES_FILE).orig $(BUILD_PROPERTIES_FILE); fi
