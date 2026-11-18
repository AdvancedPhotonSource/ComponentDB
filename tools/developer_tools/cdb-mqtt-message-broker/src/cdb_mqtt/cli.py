"""
Command-line interface for the ComponentDB MQTT message broker (``cdb-mqtt``).
"""

from typing import Any

from cdb_bely_message_broker_base import AppProfile, build_cli

from cdb_mqtt import __version__
from cdb_mqtt.config import CdbGlobalConfig
from cdb_mqtt.mqtt_client import DEFAULT_CLIENT_ID, CdbMQTTClient
from cdb_mqtt.plugin import CdbMQTTHandler, CdbPluginManager


def load_cdb_api_factory(api_url: str) -> Any:
    """Build a CdbApiFactory; raises ImportError if componentdb-api is not installed."""
    from CdbApiFactory import CdbApiFactory

    return CdbApiFactory(api_url)


CDB_PROFILE = AppProfile(
    prog_name="cdb-mqtt",
    version=__version__,
    description="CDB MQTT Message Broker - Pluggable MQTT handlers for ComponentDB events.",
    default_topic="cdb/#",
    default_client_id=DEFAULT_CLIENT_ID,
    env_prefix="CDB",
    default_lock_file="/tmp/cdb-mqtt.lock",
    handler_base=CdbMQTTHandler,
    global_config_class=CdbGlobalConfig,
    api_factory_loader=load_cdb_api_factory,
    client_class=CdbMQTTClient,
    plugin_manager_class=CdbPluginManager,
)

cli = build_cli(CDB_PROFILE)


def main() -> None:
    """Entry point for the CLI."""
    cli()


if __name__ == "__main__":
    main()
