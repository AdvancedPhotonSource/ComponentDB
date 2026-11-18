"""Tests for plugin system."""

import pytest

from bely_mqtt.models import MQTTMessage
from bely_mqtt.plugin import MQTTHandler, PluginManager


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
