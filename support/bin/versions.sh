#!/bin/bash

# Copyright (c) UChicago Argonne, LLC. All rights reserved.
# See LICENSE file.

# Single source of runtime versions for the support installers.
# Any value can be overridden from the environment, e.g. PAYARA_VERSION=7.2026.12 make support

# Eclipse Temurin JDK release (see https://adoptium.net); JAVA_FEATURE_VERSION must match JAVA_VERSION.
JAVA_FEATURE_VERSION=${JAVA_FEATURE_VERSION:-25}
JAVA_VERSION=${JAVA_VERSION:-25.0.5.1+2}

PAYARA_VERSION=${PAYARA_VERSION:-7.2026.11}
# Default 5900 clashes with VNC; keep clear of the 59xx range.
PAYARA_HAZELCAST_START_PORT=${PAYARA_HAZELCAST_START_PORT:-6900}
ANT_VERSION=${ANT_VERSION:-1.10.18}
NETBEANS_VERSION=${NETBEANS_VERSION:-32}
