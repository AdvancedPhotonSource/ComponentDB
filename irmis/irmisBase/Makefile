TOP=../..
DEPLOY_DIR=$(TOP)/../lib/java

all: deploy

clean clean-all clean-install distclean: ant-clean

.PHONY: ant-clean
ant-clean:
	ant clean

.PHONY: deploy
deploy: site.build.properties
	cd db && ant deploy && cd -
	cd apps && ant deploy && cd -
	cd apps/deploy && tar xvf irmisDeploy.tar && cd -


