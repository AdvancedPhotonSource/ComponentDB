# The top level makefile. Targets like "all" and "clean"
# are defined in the RULES file.

TOP = .
#SUBDIRS = irmis src
SUBDIRS = src

.PHONY: support db db-dev deploy-web-portal deploy-web-portal-dev
default:

prepare-dev-env: support db netbeans-config

netbeans-config:
	$(TOP)/sbin/cdb_prepare_netbeans_config.sh

support:
	$(TOP)/sbin/cdb_install_support.sh

db:
	$(TOP)/sbin/cdb_create_db.sh

deploy-web-portal: dist
	$(TOP)/sbin/cdb_deploy_web_portal.sh

db-dev:
	$(TOP)/sbin/cdb_create_db.sh cdb_dev

deploy-web-portal-dev: dist
	$(TOP)/sbin/cdb_deploy_web_portal.sh cdb_dev


include $(TOP)/tools/make/RULES_CDB

