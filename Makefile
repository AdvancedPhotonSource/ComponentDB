# The top level makefile. Targets like "all" and "clean"
# are defined in the RULES file.

TOP = .
#SUBDIRS = irmis src
SUBDIRS = src

.PHONY: support db db-dev deploy-web-portal deploy-web-portal-dev
default:

prepare-dev-env: support db netbeans-config

dev-config:
	$(TOP)/sbin/cdb_prepare_dev_config.sh

support:
	$(TOP)/sbin/cdb_install_support.sh

db:
	$(TOP)/sbin/cdb_create_db.sh

deploy-web-portal: dist
	$(TOP)/sbin/cdb_deploy_web_portal.sh

deploy-web-service: 
	$(TOP)/sbin/cdb_deploy_web_service.sh

undeploy-web-portal: 
	$(TOP)/sbin/cdb_undeploy_web_portal.sh

undeploy-web-service: 
	$(TOP)/sbin/cdb_undeploy_web_service.sh

db-dev:
	$(TOP)/sbin/cdb_create_db.sh cdb_dev

deploy-web-portal-dev: dist
	$(TOP)/sbin/cdb_deploy_web_portal.sh cdb_dev

deploy-web-service-dev: 
	$(TOP)/sbin/cdb_deploy_web_service.sh cdb_dev

undeploy-web-portal-dev: 
	$(TOP)/sbin/cdb_undeploy_web_portal.sh cdb_dev

undeploy-web-service-dev: 
	$(TOP)/sbin/cdb_undeploy_web_service.sh cdb_dev

include $(TOP)/tools/make/RULES_CDB

