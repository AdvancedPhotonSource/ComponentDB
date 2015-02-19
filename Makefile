# The top level makefile. Targets like "all" and "clean"
# are defined in the RULES file.

TOP = .
#SUBDIRS = irmis src
SUBDIRS = src

.PHONY: support db webapp-dev
default:

dev: support db portal-config

portal-config:
	$(TOP)/sbin/cdb_prepare_portal_config.sh

support:
	$(TOP)/sbin/cdb_install_support.sh

db:
	$(TOP)/sbin/cdb_create_db.sh

include $(TOP)/tools/make/RULES_CDB

