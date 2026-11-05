#!/bin/bash

set -eo pipefail

TEST_DIR=$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)
CDB_ROOT_DIR=$(cd "$TEST_DIR/../../../.." && pwd)
REGISTRAR_DIR="$CDB_ROOT_DIR/src/java/CdbWebPortal/src/java/gov/anl/aps/cdb/portal/plugins/support"
REGISTRAR_FILE="$REGISTRAR_DIR/PluginRegistrar.java"

if [ -n "${CI:-}" ]; then
    mkdir -p "$REGISTRAR_DIR"
    cat > "$REGISTRAR_FILE" <<'EOF'
package gov.anl.aps.cdb.portal.plugins.support;

import gov.anl.aps.cdb.portal.plugins.CdbPluginManager;

public abstract class PluginRegistrar {

    public static void registerPlugins(CdbPluginManager cdbPluginManager) {
        // No plugins are installed in CI.
    }
}
EOF
else
    source "$CDB_ROOT_DIR/setup.sh"
    python "$CDB_ROOT_DIR/tools/developer_tools/cdb_plugins/update_plugin_generated_files.py"
fi
