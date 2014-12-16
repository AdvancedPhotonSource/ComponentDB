TOP=../../..
ANT=cdb-ant
ANT_ARGS=-Dlibs.CopyLibs.classpath=lib/org-netbeans-modules-java-j2seproject-copylibstask.jar 
BUILD_PROPERTIES_FILE=nbproject/private/private.properties
GENERIC_BUILD_PROPERTIES_FILE=$(BUILD_PROPERTIES_FILE).generic.build

all install: dist

clean clean-all clean-install distclean: ant-clean

.PHONY: ant-clean
ant-clean:
	$(ANT) $(ANT_ARGS)  clean

# For compiling from command line, we have to use generic build properties file
.PHONY: dist
dist: 
	mv $(BUILD_PROPERTIES_FILE) $(BUILD_PROPERTIES_FILE).orig
	cmd="cat $(GENERIC_BUILD_PROPERTIES_FILE) | sed 's?CDB_GLASSFISH_DIR?$${CDB_GLASSFISH_DIR}?g' > $(BUILD_PROPERTIES_FILE)" && eval $$cmd
	$(ANT) $(ANT_ARGS) dist 
	mv $(BUILD_PROPERTIES_FILE).orig $(BUILD_PROPERTIES_FILE)


