"""
MQTT client for ComponentDB events.
"""

from typing import Optional

from cdb_bely_message_broker_base import MQTTClient, PluginManager

from cdb_mqtt.plugin import CdbPluginManager

DEFAULT_CLIENT_ID = "cdb-mqtt-client"


class CdbMQTTClient(MQTTClient):
    """MQTTClient with CDB defaults (client id, CdbPluginManager)."""

    def __init__(
        self,
        broker_host: str,
        broker_port: int = 1883,
        client_id: str = DEFAULT_CLIENT_ID,
        username: Optional[str] = None,
        password: Optional[str] = None,
        plugin_manager: Optional[PluginManager] = None,
    ):
        super().__init__(
            broker_host=broker_host,
            broker_port=broker_port,
            client_id=client_id,
            username=username,
            password=password,
            plugin_manager=plugin_manager or CdbPluginManager(),
        )
