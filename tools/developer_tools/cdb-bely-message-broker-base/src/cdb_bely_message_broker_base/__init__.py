"""
CDB/BELY MQTT Message Broker Base - shared framework for handling MQTT events.

This framework provides:
- Pluggable handler system for MQTT topics
- Event registry mapping topics to typed handler methods and pydantic models
- Global and per-handler configuration
- MQTT client and CLI factory reused by the CDB and BELY message brokers
"""

from cdb_bely_message_broker_base.cli import AppProfile, build_cli
from cdb_bely_message_broker_base.config import ConfigManager, GlobalConfig, HandlerConfig
from cdb_bely_message_broker_base.events import EventRegistry, EventSpec, handler_method_name
from cdb_bely_message_broker_base.models import EventModel, JavaDateTime, MQTTMessage
from cdb_bely_message_broker_base.mqtt_client import MQTTClient
from cdb_bely_message_broker_base.plugin import MQTTHandler, PluginManager

__version__ = "1.0.0"

__all__ = [
    "AppProfile",
    "build_cli",
    "ConfigManager",
    "GlobalConfig",
    "HandlerConfig",
    "EventRegistry",
    "EventSpec",
    "handler_method_name",
    "EventModel",
    "JavaDateTime",
    "MQTTMessage",
    "MQTTClient",
    "MQTTHandler",
    "PluginManager",
]
