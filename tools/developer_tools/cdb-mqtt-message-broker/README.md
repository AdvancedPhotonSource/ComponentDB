# CDB MQTT Message Broker

A pluggable Python framework for handling MQTT events published by ComponentDB (CDB). It is built on [`cdb-bely-message-broker-base`](../cdb-bely-message-broker-base).

## Features

- 🔌 **Pluggable Handlers**: drop handler modules into a directory.
- 📦 **Type-Safe Models**: a pydantic model for every CDB event (`cdb/#`).
- 🎯 **Typed Dispatch**: implement `handle_item_log_add(event)`, `handle_machine_design_move(event)`, …
- ⚙️ **Configuration**: a global `cdb_url` plus per-handler settings from YAML.
- 🔗 **API Integration**: optional `CdbApiFactory` (`componentdb-api`) for extra data.

## Quick start

```sh
make install-dev                      # installs the base from this checkout too
cdb-mqtt list-handlers --handlers-dir examples/handlers
cdb-mqtt start --broker-host localhost --handlers-dir examples/handlers \
    --config config.example.yaml --log-level DEBUG
```

```python
from cdb_mqtt import CdbMQTTHandler, CdbItemLogEvent

class LogEntryHandler(CdbMQTTHandler):
    async def handle_item_log_add(self, event: CdbItemLogEvent) -> None:
        self.logger.info(f"{event.triggered_by_username} logged on {event.item_description}")
```

## Documentation

- [Getting started](docs/getting-started.md)
- [Writing handlers](docs/writing-handlers.md)
- [Event reference](docs/event-reference.md)
- [Configuration](docs/configuration.md)

## Development

```sh
make install-dev
make lint type-check test-cov
```
