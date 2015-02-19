# The top level makefile. Targets like "all" and "clean"
# are defined in the RULES file.

TOP = .
#SUBDIRS = irmis src
SUBDIRS = src

.PHONY: support db webapp-dev
default:

dev: support db netbeans-config

netbeans-config:
	$(TOP)/sbin/cdb_prepare_netbeans_config.sh

support:
	$(TOP)/sbin/cdb_install_support.sh

db:
	$(TOP)/sbin/cdb_create_db.sh

db-dev:
	$(TOP)/sbin/cdb_create_db.sh cdb_dev


include $(TOP)/tools/make/RULES_CDB

