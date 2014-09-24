TOP=../../..

all install: deploy war

clean clean-all clean-install distclean: ant-clean

.PHONY: ant-clean
ant-clean:
	ant clean

.PHONY: war
war: 
	ant warfile

.PHONY: deploy
deploy: 
	ant deploy 
	cd deploy && tar xf irmisDeploy.tar && cd -
