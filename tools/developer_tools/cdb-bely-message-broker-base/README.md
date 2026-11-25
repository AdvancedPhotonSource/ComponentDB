# CDB/BELY MQTT Message Broker Base

Shared Python framework for building pluggable MQTT message brokers. ComponentDB (`cdb-mqtt-message-broker`) and BELY (`bely-mqtt-framework`) both build on it. It is the subscriber-side counterpart of the Java `cdb-base-mqtt` publishing library.

## Features

- 🔌 **Pluggable Handlers**: drop handler modules or packages into a directory.
- 🗂 **Event Registry**: applications map topics to typed handler methods and pydantic models.
- 🎯 **Topic Matching**: supports MQTT wildcards (`+`, `#`).
- ⚙️ **Configuration**: a global section plus per-handler constructor arguments from YAML.
- 🧰 **CLI Factory**: `build_cli(AppProfile(...))` gives each application its own `start` / `list-handlers` CLI.
- 🚀 **Async Support**: handlers are `async` and run on an asyncio loop.

## Building an application broker

```python
from enum import Enum
from cdb_bely_message_broker_base import (
    AppProfile, EventModel, EventRegistry, GlobalConfig, MQTTHandler, build_cli,
)

class AppEventType(Enum):
    ITEM_ADD = "app/item/add"

class ItemAddEvent(EventModel):
    item_id: str

APP_EVENT_REGISTRY = EventRegistry.from_enum(AppEventType, {AppEventType.ITEM_ADD: ItemAddEvent})

class AppMQTTHandler(MQTTHandler):
    default_topic_pattern = "app/#"
    event_registry = APP_EVENT_REGISTRY

class AppGlobalConfig(GlobalConfig):
    PUBLIC_URL_KEY = "app_url"

cli = build_cli(AppProfile(
    prog_name="app-mqtt", version="1.0.0", description="App MQTT broker",
    default_topic="app/#", default_client_id="app-mqtt-client", env_prefix="APP",
    default_lock_file="/tmp/app-mqtt.lock", handler_base=AppMQTTHandler,
    global_config_class=AppGlobalConfig,
))
```

Handler plugins subclass `AppMQTTHandler` and implement `async def handle_item_add(self, event: ItemAddEvent)`.

## Development

```sh
make install-dev
make lint type-check test-cov
```

See [docs](./docs) for the API reference and configuration.
