# Getting Started

## Prerequisites

- Python 3.10+
- A ComponentDB portal configured to publish MQTT events (`make configure-mqtt`), and access to its broker.

## Install

From this checkout:

```sh
cd tools/developer_tools/cdb-mqtt-message-broker
make install            # pip install -e ../cdb-bely-message-broker-base && pip install -e .
# or, with uv (resolves the base through [tool.uv.sources]):
uv sync
```

Install the optional API client with `pip install -e ".[api]"`.

## Run with the example handlers

```sh
cdb-mqtt start \
    --broker-host localhost \
    --handlers-dir examples/handlers \
    --config config.example.yaml \
    --log-level DEBUG
```

Make a change in the portal (for example, add a log entry to an item). `LoggingHandler` logs it.

## Your first handler

Create `handlers/my_handler.py`:

```python
from cdb_mqtt import CdbMQTTHandler, CdbItemPropertyEvent

class PropertyWatcher(CdbMQTTHandler):
    """Logs property value changes."""

    async def handle_item_property_update(self, event: CdbItemPropertyEvent) -> None:
        old = event.old_property.value if event.old_property else None
        new = event.new_property.value if event.new_property else None
        self.logger.info(f"{event.item_description}: {old!r} -> {new!r}")
```

Then run `cdb-mqtt start --handlers-dir handlers`.

Next, read [writing-handlers.md](writing-handlers.md) and [event-reference.md](event-reference.md).
