#!/bin/bash

# Copyright (c) UChicago Argonne, LLC. All rights reserved.
# See LICENSE file.


#
# Script used for configuring MQTT connector for CDB webapp
# Deploys MQTT Resource Adapter and creates connection pool/resource
#
# Usage:
#
# $0 [CDB_DB_NAME] [mqtt_config_file]
#
# If no config file is specified, defaults to $CDB_INSTALL_DIR/etc/<CDB_DB_NAME>.mqtt.conf
#
# Sample MQTT configuration file contents (mqtt.conf):
# MQTT_HOST=localhost              # MQTT broker hostname (default: localhost)
# MQTT_PORT=1883                   # MQTT broker port (default: 1883)
# MQTT_USERNAME=admin              # MQTT username (optional)
# MQTT_PASSWORD=admin              # MQTT password (optional)
# MQTT_CLEAN_SESSION=true          # Clean session flag (optional)
# MQTT_QOS=1                       # Quality of Service level (optional)
# MQTT_KEEP_ALIVE_INTERVAL=60      # Keep alive interval in seconds (optional)
# MQTT_CONNECTION_TIMEOUT=30       # Connection timeout in seconds (optional)
# MQTT_MAX_INFLIGHT=10             # Maximum number of messages in flight (optional)
# MQTT_AUTOMATIC_RECONNECT=true    # Automatic reconnection on disconnect (optional)
# MQTT_FILE_PERSISTANCE=false      # Enable file-based message persistence (optional)
# MQTT_PERSISTENCE_DIRECTORY=.     # Directory for persistent message storage (optional)
# MQTT_TOPIC_FILTER                # MQTT topic filter for subscriptions (optional)

MY_DIR=`dirname $0` && cd $MY_DIR && MY_DIR=`pwd`
if [ -z "${CDB_ROOT_DIR}" ]; then
    CDB_ROOT_DIR=$MY_DIR/..
fi
CDB_ENV_FILE=${CDB_ROOT_DIR}/setup.sh
if [ ! -f ${CDB_ENV_FILE} ]; then
    echo "Environment file ${CDB_ENV_FILE} does not exist."
    exit 2
fi
. ${CDB_ENV_FILE} > /dev/null

# Constants
MQTT_POOL_NAME="cdb/MQTT/pool"
MQTT_RESOURCE_NAME="cdb/MQTT/resource"
MQTT_RAR_NAME="mqtt-rar-0.8.0"
MQTT_CONNECTOR_RAR_DEPLOYMENT_NAME="cdb-mqtt-rar-deployment"
MQTT_RAR_PATH=$CDB_ROOT_DIR/src/lib/${MQTT_RAR_NAME}.rar

# Look for the deployment-specific MQTT configuration file
CDB_DB_NAME=${1:-${CDB_DB_NAME:-cdb}}
if [ ! -z "$2" ]; then
    mqttConfigFile=$2
else
    mqttConfigFile=$CDB_INSTALL_DIR/etc/${CDB_DB_NAME}.mqtt.conf
fi

if [ -f $mqttConfigFile ]; then
    echo "Using MQTT config file: $mqttConfigFile"
    . $mqttConfigFile
else
    echo "Error: MQTT config file $mqttConfigFile not found."
    echo "You can create one using cdb_create_mqtt_configuration.sh ${CDB_DB_NAME}"
    exit 1
fi

CDB_HOST_ARCH=$(uname -sm | tr -s '[:upper:][:blank:]' '[:lower:][\-]')
GLASSFISH_DIR=$CDB_SUPPORT_DIR/payara/$CDB_HOST_ARCH

ASADMIN_CMD=$GLASSFISH_DIR/bin/asadmin

# MQTT Configuration defaults
MQTT_HOST=${MQTT_HOST:=localhost}
MQTT_PORT=${MQTT_PORT:=1883}

# Build properties string for connection pool
PROPERTIES="serverURIs=tcp\\://${MQTT_HOST}\\:${MQTT_PORT}"

if [ ! -z "$MQTT_USERNAME" ]; then
    PROPERTIES="${PROPERTIES}:userName=${MQTT_USERNAME}"
fi

if [ ! -z "$MQTT_PASSWORD" ]; then
    PROPERTIES="${PROPERTIES}:password=${MQTT_PASSWORD}"
fi

if [ ! -z "$MQTT_CLEAN_SESSION" ]; then
    PROPERTIES="${PROPERTIES}:cleanSession=${MQTT_CLEAN_SESSION}"
fi

if [ ! -z "$MQTT_KEEP_ALIVE_INTERVAL" ]; then
    PROPERTIES="${PROPERTIES}:keepAliveInterval=${MQTT_KEEP_ALIVE_INTERVAL}"
fi

if [ ! -z "$MQTT_CONNECTION_TIMEOUT" ]; then
    PROPERTIES="${PROPERTIES}:connectionTimeout=${MQTT_CONNECTION_TIMEOUT}"
fi

if [ ! -z "$MQTT_MAX_INFLIGHT" ]; then
    PROPERTIES="${PROPERTIES}:maxInflight=${MQTT_MAX_INFLIGHT}"
fi

if [ ! -z "$MQTT_AUTOMATIC_RECONNECT" ]; then
    PROPERTIES="${PROPERTIES}:automaticReconnect=${MQTT_AUTOMATIC_RECONNECT}"
fi

if [ ! -z "$MQTT_FILE_PERSISTANCE" ]; then
    PROPERTIES="${PROPERTIES}:filePersistance=${MQTT_FILE_PERSISTANCE}"
fi

if [ ! -z "$MQTT_PERSISTENCE_DIRECTORY" ]; then
    PROPERTIES="${PROPERTIES}:persistenceDirectory=${MQTT_PERSISTENCE_DIRECTORY}"
fi

if [ ! -z "$MQTT_QOS" ]; then
    PROPERTIES="${PROPERTIES}:qos=${MQTT_QOS}"
fi

if [ ! -z "$MQTT_TOPIC_FILTER" ]; then
    PROPERTIES="${PROPERTIES}:topicFilter=${MQTT_TOPIC_FILTER}"
fi

# Deploy MQTT RAR
echo "Deploying MQTT RAR"
if [ -f "$MQTT_RAR_PATH" ]; then
    # Check if already deployed and undeploy if needed
    $ASADMIN_CMD list-applications | grep -q ${MQTT_CONNECTOR_RAR_DEPLOYMENT_NAME} && {
        echo "Undeploying existing MQTT RAR"
        # Check if resource exists and delete it
        $ASADMIN_CMD list-connector-resources | grep -q ${MQTT_RESOURCE_NAME} && {
            echo "Deleting existing MQTT resource"
            $ASADMIN_CMD delete-connector-resource ${MQTT_RESOURCE_NAME} || exit 1
        }

        # Check if connection pool exists and delete it
        $ASADMIN_CMD list-connector-connection-pools | grep -q ${MQTT_POOL_NAME} && {
            echo "Deleting existing MQTT connection pool"
            $ASADMIN_CMD delete-connector-connection-pool ${MQTT_POOL_NAME} || exit 1
        }

        echo "Undeploying existing MQTT RAR"
        $ASADMIN_CMD undeploy ${MQTT_CONNECTOR_RAR_DEPLOYMENT_NAME} || exit 1
    }
    $ASADMIN_CMD deploy --name ${MQTT_CONNECTOR_RAR_DEPLOYMENT_NAME} $MQTT_RAR_PATH || exit 1
else
    echo "Warning: MQTT RAR file not found at $MQTT_RAR_PATH"
    exit 1
fi

# Create MQTT connection pool
echo "Creating MQTT connection pool ${MQTT_POOL_NAME}"
$ASADMIN_CMD create-connector-connection-pool \
    --raname ${MQTT_CONNECTOR_RAR_DEPLOYMENT_NAME} \
    --connectiondefinition fish.payara.cloud.connectors.mqtt.api.MQTTConnectionFactory \
    --property "${PROPERTIES}" \
    ${MQTT_POOL_NAME} || exit 1
# Create MQTT resource
echo "Creating MQTT resource ${MQTT_RESOURCE_NAME}"
$ASADMIN_CMD create-connector-resource \
    --poolname ${MQTT_POOL_NAME} \
    ${MQTT_RESOURCE_NAME} || exit 1

# The connector logs allocation failures with a full stack trace before returning null.
$ASADMIN_CMD set-log-levels fish.payara.cloud.connectors.mqtt.api.outbound.MQTTConnectionFactoryImpl=OFF || exit 1

# Test MQTT connection pool
echo "Testing MQTT connection pool"
$ASADMIN_CMD ping-connection-pool ${MQTT_POOL_NAME} || { echo "Warning: MQTT connection pool ping failed"; exit 1; }

echo "Restart or redeploy CDB."