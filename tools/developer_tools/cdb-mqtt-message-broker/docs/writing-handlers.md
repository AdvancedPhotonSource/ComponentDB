# Writing Handlers

## Basics

Subclass `CdbMQTTHandler` and implement the event methods you need. Each method receives a parsed pydantic model (see [event-reference.md](event-reference.md)).

```python
from cdb_mqtt import CdbMQTTHandler, CdbMachineMoveEvent, MQTTMessage

class MoveHandler(CdbMQTTHandler):
    async def handle_machine_design_move(self, event: CdbMachineMoveEvent) -> None:
        self.logger.info(f"{event.machine.description} -> {event.new_parent.description}")

    async def handle_generic(self, message: MQTTMessage) -> None:
        # Any matching message without a specific method (raw JSON payload)
        pass
```

Dispatch rules:
1. A message goes to every handler whose `topic_pattern` matches. The default is `cdb/#`.
2. If the handler implements the event's method, that method is called with the typed event.
3. Otherwise `handle(message)` is called; by default it forwards to `handle_generic(message)`.
4. An exception in one handler is logged and doesn't affect other handlers. This includes payloads that fail validation.

## Narrowing the subscription

```python
class MachineOnly(CdbMQTTHandler):
    @property
    def topic_pattern(self) -> str:
        return "cdb/machine-design/#"
```

The CLI `--topic` option sets what the client subscribes to on the broker. `topic_pattern` then filters which of those messages each handler receives.

## Configuration

Constructor keyword arguments come from the `handlers.<ClassName>` section of the YAML config. `global_config` is a `CdbGlobalConfig` built from the `global` section.

```python
from typing import Optional
from cdb_mqtt import CdbGlobalConfig, CdbMQTTHandler

class Notifier(CdbMQTTHandler):
    def __init__(self, api_factory=None, global_config: Optional[CdbGlobalConfig] = None,
                 webhook_url: Optional[str] = None):
        super().__init__(api_factory=api_factory, global_config=global_config)
        self.webhook_url = webhook_url
```

```yaml
global:
  cdb_url: https://cdb.example.com/cdb
handlers:
  Notifier:
    webhook_url: https://hooks.example.com/abc
```

Use `self.global_config.public_url` (or `cdb_url` on `CdbGlobalConfig`) to build portal links, for example `{cdb_url}/views/itemDomainMachineDesign/view.xhtml?id={machine.id}`.

## Querying the CDB API

When an API URL is configured (`--api-url`, `CDB_API_URL`, `global.api_url` or `global.cdb_url`) and `componentdb-api` is installed, `self.api_factory` is a `CdbApiFactory`:

```python
item = self.api_factory.getItemApi().get_item_by_id(int(event.item_id))
```

API calls are synchronous; keep them short, or run them with `asyncio.to_thread`.

## Packaging handlers

The handlers directory may contain:
- single modules (`my_handler.py`)
- packages (`my_handler/__init__.py` that imports the handler class)

Files and directories starting with `_` or `.` are skipped. Every concrete `CdbMQTTHandler` subclass found is registered. `CdbMQTTHandler` itself is never registered.

## Testing handlers

```python
import pytest
from cdb_mqtt import MQTTMessage
from my_handler import PropertyWatcher

@pytest.mark.asyncio
async def test_property_update(payload):
    handler = PropertyWatcher()
    await handler.dispatch(MQTTMessage(topic="cdb/item/property/update", payload=payload))
```

See `tests/fixtures/` for sample payloads of every event.
