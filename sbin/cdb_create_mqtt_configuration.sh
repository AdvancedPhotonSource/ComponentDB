#!/bin/bash

# Script to create MQTT configuration file for CDB service
# Usage: $0 [CDB_DB_NAME]

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

# Default configuration file location
CDB_DB_NAME=${1:-${CDB_DB_NAME:-cdb}}
MQTT_CONFIG_FILE=${CDB_INSTALL_DIR}/etc/${CDB_DB_NAME}.mqtt.conf

echo "==================================="
echo "MQTT Configuration Setup for CDB"
echo "==================================="
echo ""
echo "This script will help you create an MQTT configuration file."
echo "Configuration will be saved to: $MQTT_CONFIG_FILE"
echo ""

# Check if config file already exists
if [ -f "$MQTT_CONFIG_FILE" ]; then
    read -p "Configuration file already exists. Overwrite? (y/n): " -n 1 -r
    echo
    if [[ ! $REPLY =~ ^[Yy]$ ]]; then
        echo "Exiting without changes."
        exit 0
    fi
fi

echo ""
echo "Configuration Details:"
echo "- cleanSession: Whether client and server should remember state across reconnects"
echo "- automaticReconnect: Whether client will automatically reconnect if connection is lost"
echo "- filePersistance: Whether to use file persistence for un-acknowledged messages"
echo "- persistenceDirectory: Directory to use for file persistence"
echo "- connectionTimeout: Connection timeout value in seconds"
echo "- maxInflight: Maximum messages that can be sent without acknowledgements"
echo "- keepAliveInterval: Keep alive interval in seconds"
echo "- userName/password: Authentication credentials"
# Disable MDB only variables.
# echo "- topicFilter: Topic Filter (For MDBs only)"
# echo "- qos: Quality of Service for the subscription (For MDBs only)"
echo ""

# Ensure directory exists
mkdir -p $(dirname "$MQTT_CONFIG_FILE")

# Prompt for configuration values
read -p "MQTT Host [localhost]: " MQTT_HOST
MQTT_HOST=${MQTT_HOST:-localhost}

read -p "MQTT Port [1883]: " MQTT_PORT
MQTT_PORT=${MQTT_PORT:-1883}

read -p "MQTT Username (leave empty for no auth): " MQTT_USERNAME

if [ ! -z "$MQTT_USERNAME" ]; then
    read -s -p "MQTT Password: " MQTT_PASSWORD
    echo
fi

read -p "Clean Session (true/false) [false]: " CLEAN_SESSION
CLEAN_SESSION=${CLEAN_SESSION:-false}

read -p "Automatic Reconnect (true/false) [true]: " AUTOMATIC_RECONNECT
AUTOMATIC_RECONNECT=${AUTOMATIC_RECONNECT:-true}

read -p "File Persistance (true/false) [false]: " FILE_PERSISTANCE
FILE_PERSISTANCE=${FILE_PERSISTANCE:-false}

read -p "Persistence Directory [.]: " PERSISTENCE_DIRECTORY
PERSISTENCE_DIRECTORY=${PERSISTENCE_DIRECTORY:-.}

read -p "Connection Timeout (seconds) [30]: " CONNECTION_TIMEOUT
CONNECTION_TIMEOUT=${CONNECTION_TIMEOUT:-30}

read -p "Max Inflight [10]: " MAX_INFLIGHT
MAX_INFLIGHT=${MAX_INFLIGHT:-10}

read -p "Keep Alive Interval (seconds) [60]: " KEEP_ALIVE_INTERVAL
KEEP_ALIVE_INTERVAL=${KEEP_ALIVE_INTERVAL:-60}

# MDB only variables 
# read -p "Topic Filter (leave empty if not using MDB): " TOPIC_FILTER
# read -p "QoS (0/1/2) [0]: " QOS
# QOS=${QOS:-0}

# Write configuration file
cat > "$MQTT_CONFIG_FILE" << EOF
# MQTT Configuration for CDB Service
# Generated on $(date)

MQTT_HOST=$MQTT_HOST
MQTT_PORT=$MQTT_PORT
EOF

if [ ! -z "$MQTT_USERNAME" ]; then
    echo "MQTT_USERNAME=$MQTT_USERNAME" >> "$MQTT_CONFIG_FILE"
fi

if [ ! -z "$MQTT_PASSWORD" ]; then
    echo "MQTT_PASSWORD=$MQTT_PASSWORD" >> "$MQTT_CONFIG_FILE"
fi

cat >> "$MQTT_CONFIG_FILE" << EOF
MQTT_CLEAN_SESSION=$CLEAN_SESSION
MQTT_AUTOMATIC_RECONNECT=$AUTOMATIC_RECONNECT
MQTT_FILE_PERSISTANCE=$FILE_PERSISTANCE
MQTT_PERSISTENCE_DIRECTORY=$PERSISTENCE_DIRECTORY
MQTT_CONNECTION_TIMEOUT=$CONNECTION_TIMEOUT
MQTT_MAX_INFLIGHT=$MAX_INFLIGHT
MQTT_KEEP_ALIVE_INTERVAL=$KEEP_ALIVE_INTERVAL
EOF

if [ ! -z "$TOPIC_FILTER" ]; then
    echo "MQTT_TOPIC_FILTER=$TOPIC_FILTER" >> "$MQTT_CONFIG_FILE"
fi

if [ ! -z "$QOS" ]; then
    echo "MQTT_QOS=$QOS" >> "$MQTT_CONFIG_FILE"
fi

echo ""
echo "Configuration file created successfully at: $MQTT_CONFIG_FILE"
echo ""
echo "You can now run cdb_configure_mqtt_service.sh ${CDB_DB_NAME} to apply this configuration."