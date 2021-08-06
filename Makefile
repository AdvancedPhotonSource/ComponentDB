# Copyright (c) UChicago Argonne, LLC. All rights reserved.
# See LICENSE file.

# The top level makefile. Targets like "all" and "clean"
# are defined in the RULES file.

TOP = .
#SUBDIRS = irmis src
SUBDIRS = src

.PHONY: support support-mysql dev-config
.PHONY: db backup db-dev deploy-web-portal undeploy-web-portal deploy-web-service undeploy-web-service
.PHONY: db-dev backup-dev deploy-web-portal-dev undeploy-web-portal-dev deploy-web-service-dev undeploy-web-service-dev

default:

prepare-dev-env: support db dev-config

dev-config:
	$(TOP)/sbin/cdb_prepare_dev_config.sh

configuration:
	$(TOP)/sbin/cdb_create_configuration.sh

support:
	$(TOP)/sbin/cdb_install_support.sh

support-portal:
	$(TOP)/sbin/cdb_install_support_portal.sh

support-mysql:
	$(TOP)/sbin/cdb_install_support_mysql.sh && $(TOP)/sbin/cdb_deploy_mysqld.sh

support-netbeans:
	$(TOP)/sbin/cdb_install_support_netbeans.sh

clean-db:
	$(TOP)/sbin/cdb_create_db.sh cdb $(TOP)/db/sql/clean

test-db:
	$(TOP)/sbin/cdb_create_db.sh cdb $(TOP)/db/sql/test

test:
	$(TOP)/sbin/cdb_test.sh

db:
	$(TOP)/sbin/cdb_create_db.sh

backup:
	$(TOP)/sbin/cdb_backup_all.sh

configure-web-portal: dist
	$(TOP)/sbin/cdb_configure_web_portal.sh

deploy-cdb-plugin:
	$(TOP)/tools/developer_tools/cdb_plugins/deploy_plugin.py cdb

deploy-web-portal: dist
	$(TOP)/sbin/cdb_deploy_web_portal.sh

deploy-web-service:
	$(TOP)/sbin/cdb_deploy_web_service.sh

unconfigure-web-portal:
	$(TOP)/sbin/cdb_unconfigure_web_portal.sh

undeploy-web-portal:
	$(TOP)/sbin/cdb_undeploy_web_portal.sh

undeploy-web-service:
	$(TOP)/sbin/cdb_undeploy_web_service.sh

configuration-dev:
	$(TOP)/sbin/cdb_create_configuration.sh cdb_dev

db-dev:
	$(TOP)/sbin/cdb_create_db.sh cdb_dev

clean-db-dev:
	$(TOP)/sbin/cdb_create_db.sh cdb_dev $(TOP)/db/sql/clean

backup-dev:
	$(TOP)/sbin/cdb_backup_all.sh cdb_dev

deploy-cdb-plugin-dev:
	$(TOP)/tools/developer_tools/cdb_plugins/deploy_plugin.py cdb_dev

deploy-web-portal-dev: dist 
	$(TOP)/sbin/cdb_deploy_web_portal.sh cdb_dev Dev

configure-web-portal-dev: dist
	$(TOP)/sbin/cdb_configure_web_portal.sh cdb_dev

deploy-web-service-dev:
	$(TOP)/sbin/cdb_deploy_web_service.sh cdb_dev

unconfigure-web-portal-dev:
	$(TOP)/sbin/cdb_unconfigure_web_portal.sh cdb_dev

undeploy-web-portal-dev:
	$(TOP)/sbin/cdb_undeploy_web_portal.sh cdb_dev

undeploy-web-service-dev:
	$(TOP)/sbin/cdb_undeploy_web_service.sh cdb_dev

include $(TOP)/tools/make/RULES_CDB
