# Copyright (c) UChicago Argonne, LLC. All rights reserved.
# See LICENSE file.

TOP=../../..

all install: dist

clean clean-all clean-install distclean: pyc-clean build-clean

.PHONY: pyc-clean build-clean
pyc-clean:
	rm -f `find . -name '*.pyc'`


build-clean:
	rm -rf build

# For compiling from command line, we have to use generic build properties file
.PHONY: dist
dist: 
