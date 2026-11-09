#!/bin/bash

# Copyright (c) UChicago Argonne, LLC. All rights reserved.
# See LICENSE file.

#
# Runs the MQTT integration tests against a deployed portal and a running broker.
#
# Prerequisites (not handled by this script):
#   - an MQTT broker reachable by both Payara and this shell
#   - Payara connector resource cdb/MQTT/resource configured
#     (sbin/cdb_create_mqtt_configuration.sh, sbin/cdb_configure_mqtt_service.sh)
#   - portal deployed at http://localhost:8080/cdb using the test database
#   - a Python environment with paho-mqtt (v2), pytest, the generated cdbApi
#     client and, for the UI tests, selenium
#
# This script never provisions a broker, reconfigures Payara, replaces the
# database, builds, deploys or restarts services.
#
# Environment:
#   CDB_TEST_MQTT=false        skip all MQTT tests
#   CDB_TEST_MQTT_HOST         broker host (default localhost)
#   CDB_TEST_MQTT_PORT         broker port (default 1883)
#   CDB_TEST_MQTT_USERNAME     optional broker username
#   CDB_TEST_MQTT_PASSWORD     optional broker password
#   CDB_TEST_MQTT_TIMEOUT      seconds to wait for connect/messages (default 10)
#   CDB_TEST_MQTT_SKIP_SELENIUM=true   run only the API MQTT tests
#
# Usage:
#
# $0 [extra pytest args]
#

MY_DIR=`dirname $0` && cd $MY_DIR && MY_DIR=`pwd`
CDB_DIST_DIR=`cd $MY_DIR/.. && pwd`

if [ -z "${CDB_ROOT_DIR}" ]; then
    if [ ! -f $CDB_DIST_DIR/setup.sh ]; then
        echo "Environment file $CDB_DIST_DIR/setup.sh does not exist."
        exit 2
    fi
    . $CDB_DIST_DIR/setup.sh > /dev/null
fi

API_CLIENT_PATH=$CDB_DIST_DIR/tools/developer_tools/python-client
SELENIUM_PATH=$CDB_DIST_DIR/tools/developer_tools/portal_testing/PythonSeleniumTest
SELENIUM_MQTT_MODULE=tests/test_mqtt_machine_design.py

# Shared helper (mqtt_test_support) lives in the API test directory.
export PYTHONPATH="$API_CLIENT_PATH/packages/api:$API_CLIENT_PATH/packages/cli:$API_CLIENT_PATH/test${PYTHONPATH:+:$PYTHONPATH}"

if ! command -v pytest > /dev/null; then
    echo "pytest is not on PATH; activate the test environment first."
    exit 2
fi

PYTHON_CMD=`command -v python || command -v python3`
if ! $PYTHON_CMD -c "import paho.mqtt" > /dev/null 2>&1; then
    echo "Warning: paho-mqtt is not importable; MQTT tests will be skipped."
fi

HEADER_TEXT='****************'
PRINTF_HEADER="\n\n$HEADER_TEXT Starting %s $HEADER_TEXT\n\n\n"

result=0

printf "$PRINTF_HEADER" "API MQTT Tests"
cd $API_CLIENT_PATH
pytest test/mqtt_test.py -rs "$@" || result=1

skip_selenium=`echo "$CDB_TEST_MQTT_SKIP_SELENIUM" | tr "[:upper:]" "[:lower:]"`
if [[ "$skip_selenium" == "true" ]]; then
    printf "\nSkipping Selenium MQTT tests (CDB_TEST_MQTT_SKIP_SELENIUM=true).\n"
elif [ -f $SELENIUM_PATH/$SELENIUM_MQTT_MODULE ]; then
    printf "$PRINTF_HEADER" "Selenium MQTT Tests"
    cd $SELENIUM_PATH
    # Serial on purpose: the UI test mutates shared machine-design state.
    pytest tests/ -m mqtt -p no:xdist -rs "$@" || result=1
fi

exit $result
