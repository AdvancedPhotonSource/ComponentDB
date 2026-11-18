"""Tests for plugin system."""

from enum import Enum

import pytest
from pydantic import Field

from cdb_bely_message_broker_base.config import ConfigManager
from cdb_bely_message_broker_base.events import EventRegistry
from cdb_bely_message_broker_base.models import EventModel, MQTTMessage
from cdb_bely_message_broker_base.plugin import MQTTHandler, PluginManager


class MockHandler(MQTTHandler):
    """Test handler for testing."""

    def __init__(self, *args, **kwargs):
        super().__init__(*args, **kwargs)
        self.handled_messages = []

    @property
    def topic_pattern(self) -> str:
        return "test/topic"

    async def handle(self, message: MQTTMessage) -> None:
        self.handled_messages.append(message)


class WildcardHandler(MQTTHandler):
    """Handler with wildcard pattern."""

    def __init__(self, *args, **kwargs):
        super().__init__(*args, **kwargs)
        self.handled_messages = []

    @property
    def topic_pattern(self) -> str:
        return "test/+"

    async def handle(self, message: MQTTMessage) -> None:
        self.handled_messages.append(message)


class MultiLevelHandler(MQTTHandler):
    """Handler with multi-level wildcard."""

    def __init__(self, *args, **kwargs):
        super().__init__(*args, **kwargs)
        self.handled_messages = []

    @property
    def topic_pattern(self) -> str:
        return "test/#"

    async def handle(self, message: MQTTMessage) -> None:
        self.handled_messages.append(message)


def test_topic_matching():
    """Test MQTT topic pattern matching."""
    handler = MockHandler()

    assert handler.topic_matches("test/topic")
    assert not handler.topic_matches("test/other")
    assert not handler.topic_matches("other/topic")


def test_single_level_wildcard():
    """Test single-level wildcard matching."""
    handler = WildcardHandler()

    assert handler.topic_matches("test/topic")
    assert handler.topic_matches("test/other")
    assert not handler.topic_matches("test/topic/sub")
    assert not handler.topic_matches("other/topic")


def test_multi_level_wildcard():
    """Test multi-level wildcard matching."""
    handler = MultiLevelHandler()

    assert handler.topic_matches("test/topic")
    assert handler.topic_matches("test/topic/sub")
    assert handler.topic_matches("test/topic/sub/deep")
    assert not handler.topic_matches("other/topic")


@pytest.mark.asyncio
async def test_plugin_manager_registration():
    """Test handler registration."""
    manager = PluginManager()
    handler = MockHandler()

    manager.register_handler(handler)

    assert len(manager.handlers) == 1
    assert manager.handlers[0] is handler


@pytest.mark.asyncio
async def test_plugin_manager_message_routing():
    """Test message routing to handlers."""
    manager = PluginManager()
    handler = MockHandler()
    manager.register_handler(handler)

    message = MQTTMessage(topic="test/topic", payload={"test": "data"})
    await manager.handle_message(message)

    assert len(handler.handled_messages) == 1
    assert handler.handled_messages[0] is message


@pytest.mark.asyncio
async def test_plugin_manager_no_matching_handlers():
    """Test that no error occurs when no handlers match."""
    manager = PluginManager()
    handler = MockHandler()
    manager.register_handler(handler)

    message = MQTTMessage(topic="other/topic", payload={"test": "data"})
    # Should not raise
    await manager.handle_message(message)

    assert len(handler.handled_messages) == 0


@pytest.mark.asyncio
async def test_plugin_manager_multiple_handlers():
    """Test routing to multiple matching handlers."""
    manager = PluginManager()
    handler1 = MockHandler()
    handler2 = MockHandler()
    manager.register_handler(handler1)
    manager.register_handler(handler2)

    message = MQTTMessage(topic="test/topic", payload={"test": "data"})
    await manager.handle_message(message)

    assert len(handler1.handled_messages) == 1
    assert len(handler2.handled_messages) == 1


def test_get_handlers_for_topic():
    """Test getting handlers for a specific topic."""
    manager = PluginManager()
    handler1 = MockHandler()
    handler2 = WildcardHandler()
    handler3 = MultiLevelHandler()

    manager.register_handler(handler1)
    manager.register_handler(handler2)
    manager.register_handler(handler3)

    handlers = manager.get_handlers_for_topic("test/topic")
    assert len(handlers) == 3

    handlers = manager.get_handlers_for_topic("test/other")
    assert len(handlers) == 2  # WildcardHandler and MultiLevelHandler

    handlers = manager.get_handlers_for_topic("other/topic")
    assert len(handlers) == 0


# --- Event registry dispatch -------------------------------------------------


class ToyEventType(Enum):
    ITEM_ADD = "toy/item/add"
    ITEM_DELETE = "toy/item/delete"
    UNMODELED = "toy/unmodeled"


class ToyItemEvent(EventModel):
    item_id: int = Field(alias="itemId")


TOY_REGISTRY = EventRegistry.from_enum(
    ToyEventType,
    {ToyEventType.ITEM_ADD: ToyItemEvent, ToyEventType.ITEM_DELETE: ToyItemEvent},
)


class ToyMQTTHandler(MQTTHandler):
    """Application handler base (like CdbMQTTHandler)."""

    default_topic_pattern = "toy/#"
    event_registry = TOY_REGISTRY


class ToyHandler(ToyMQTTHandler):
    def __init__(self, *args, **kwargs):
        super().__init__(*args, **kwargs)
        self.added = []
        self.generic = []

    async def handle_item_add(self, event: ToyItemEvent) -> None:
        self.added.append(event)

    async def handle_generic(self, message: MQTTMessage) -> None:
        self.generic.append(message)


class OverridingHandleHandler(ToyMQTTHandler):
    """Overrides handle(); specific methods must still take precedence."""

    def __init__(self, *args, **kwargs):
        super().__init__(*args, **kwargs)
        self.calls = []

    async def handle_item_add(self, event: ToyItemEvent) -> None:
        self.calls.append(("specific", event.item_id))

    async def handle(self, message: MQTTMessage) -> None:
        self.calls.append(("handle", message.topic))


def test_default_topic_pattern():
    assert MockHandler.default_topic_pattern == "#"
    assert ToyHandler().topic_pattern == "toy/#"


@pytest.mark.asyncio
async def test_specific_method_receives_typed_event():
    manager = PluginManager()
    handler = ToyHandler()
    manager.register_handler(handler)

    await manager.handle_message(MQTTMessage(topic="toy/item/add", payload={"itemId": 5}))

    assert len(handler.added) == 1
    assert isinstance(handler.added[0], ToyItemEvent)
    assert handler.added[0].item_id == 5
    assert handler.generic == []


@pytest.mark.asyncio
async def test_unimplemented_or_unregistered_falls_back_to_generic():
    handler = ToyHandler()

    await handler.dispatch(MQTTMessage(topic="toy/item/delete", payload={"itemId": 1}))
    await handler.dispatch(MQTTMessage(topic="toy/unmodeled", payload={}))
    await handler.dispatch(MQTTMessage(topic="toy/other", payload={}))

    assert handler.added == []
    assert [m.topic for m in handler.generic] == ["toy/item/delete", "toy/unmodeled", "toy/other"]


@pytest.mark.asyncio
async def test_specific_method_takes_precedence_over_overridden_handle():
    handler = OverridingHandleHandler()

    await handler.dispatch(MQTTMessage(topic="toy/item/add", payload={"itemId": 7}))
    await handler.dispatch(MQTTMessage(topic="toy/item/delete", payload={"itemId": 8}))

    assert handler.calls == [("specific", 7), ("handle", "toy/item/delete")]


@pytest.mark.asyncio
async def test_handle_routes_to_specific_method_when_called_directly():
    handler = ToyHandler()

    await handler.handle(MQTTMessage(topic="toy/item/add", payload={"itemId": 3}))

    assert [e.item_id for e in handler.added] == [3]


@pytest.mark.asyncio
async def test_handler_error_does_not_stop_other_handlers():
    class FailingHandler(ToyMQTTHandler):
        async def handle_item_add(self, event: ToyItemEvent) -> None:
            raise RuntimeError("boom")

    manager = PluginManager()
    good = ToyHandler()
    manager.register_handler(FailingHandler())
    manager.register_handler(good)

    await manager.handle_message(MQTTMessage(topic="toy/item/add", payload={"itemId": 1}))

    assert len(good.added) == 1


@pytest.mark.asyncio
async def test_invalid_payload_is_logged_not_raised():
    manager = PluginManager()
    handler = ToyHandler()
    manager.register_handler(handler)

    await manager.handle_message(MQTTMessage(topic="toy/item/add", payload={"wrong": 1}))

    assert handler.added == []


# --- Handler discovery -------------------------------------------------------

HANDLER_MODULE = """
from cdb_bely_message_broker_base.plugin import MQTTHandler
from tests.test_plugin import ToyMQTTHandler


class FileToyHandler(ToyMQTTHandler):
    def __init__(self, api_factory=None, global_config=None, greeting="hi"):
        super().__init__(api_factory, global_config)
        self.greeting = greeting


class PlainHandler(MQTTHandler):
    pass
"""

PACKAGE_INIT = """
from tests.test_plugin import ToyMQTTHandler


class PackageToyHandler(ToyMQTTHandler):
    pass
"""


@pytest.fixture
def handlers_dir(tmp_path):
    (tmp_path / "file_handler.py").write_text(HANDLER_MODULE)
    (tmp_path / "_private.py").write_text("raise RuntimeError('must not be imported')")
    package = tmp_path / "toy_package_handler"
    package.mkdir()
    (package / "__init__.py").write_text(PACKAGE_INIT)
    return tmp_path


def test_load_handlers_filters_by_handler_base(handlers_dir):
    manager = PluginManager(handler_base=ToyMQTTHandler)
    manager.load_handlers_from_directory(handlers_dir)

    names = sorted(h.__class__.__name__ for h in manager.handlers)
    # ToyMQTTHandler itself (imported into the modules) and PlainHandler are skipped.
    assert names == ["FileToyHandler", "PackageToyHandler"]


def test_load_handlers_default_base_skips_only_mqtt_handler(handlers_dir):
    manager = PluginManager()
    manager.load_handlers_from_directory(handlers_dir)

    names = {h.__class__.__name__ for h in manager.handlers}
    assert "PlainHandler" in names
    assert "MQTTHandler" not in names


def test_load_handlers_passes_configuration(handlers_dir):
    config_manager = ConfigManager()
    config_manager.load_from_dict(
        {"global": {"url": "https://x"}, "handlers": {"FileToyHandler": {"greeting": "hello"}}}
    )
    manager = PluginManager(
        api_factory="api", config_manager=config_manager, handler_base=ToyMQTTHandler
    )
    manager.load_handlers_from_directory(handlers_dir)

    handler = next(h for h in manager.handlers if h.__class__.__name__ == "FileToyHandler")
    assert handler.greeting == "hello"
    assert handler.api_factory == "api"
    assert handler.global_config.public_url == "https://x"


def test_load_handlers_filters_unsupported_constructor_args():
    class NoArgsHandler(ToyMQTTHandler):
        def __init__(self):
            super().__init__()

    config_manager = ConfigManager()
    config_manager.load_from_dict({"global": {"a": 1}, "handlers": {"NoArgsHandler": {"x": 1}}})
    manager = PluginManager(config_manager=config_manager)
    manager.register_handler_class(NoArgsHandler)

    assert isinstance(manager.handlers[0], NoArgsHandler)


def test_missing_handlers_dir_is_ignored(tmp_path):
    manager = PluginManager()
    manager.load_handlers_from_directory(tmp_path / "missing")
    assert manager.handlers == []
