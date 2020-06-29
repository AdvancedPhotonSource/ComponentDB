#!/bin/bash

# Copyright (c) UChicago Argonne, LLC. All rights reserved.
# See LICENSE file.


currentDir=`pwd`
cd `dirname $0`/.. && topDir=`pwd`
binDir=$topDir/bin

$binDir/build_python.sh || exit 1
$binDir/install_setuptools.sh || exit 1
$binDir/install_click.sh || exit 1
$binDir/install_python_ldap.sh || exit 1
$binDir/install_pip.sh || exit 1
$binDir/install_sphinx.sh || exit 1
$binDir/install_twine.sh || exit 1
$binDir/build_cherrypy.sh || exit 1
$binDir/install_routes.sh || exit 1
$binDir/build_sqlalchemy.sh || exit 1
$binDir/build_mysql_python.sh || exit 1
$binDir/build_suds.sh || exit 1
