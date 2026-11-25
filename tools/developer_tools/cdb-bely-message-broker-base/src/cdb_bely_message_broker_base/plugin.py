"""
Plugin system for MQTT handlers.

This module provides the base classes and interfaces for creating
pluggable handlers for MQTT topics.
"""

import importlib
import importlib.util
import inspect
import logging
import sys
from abc import ABC
from pathlib import Path
from types import ModuleType
from typing import Any, Callable, ClassVar, List, Optional, Tuple, Type

from pydantic import BaseModel

from cdb_bely_message_broker_base.config import GlobalConfig
from cdb_bely_message_broker_base.events import EventRegistry, EventSpec
from cdb_bely_message_broker_base.models import MQTTMessage

logger = logging.getLogger(__name__)


class MQTTHandler(ABC):
    """
    Base class for MQTT message handlers.

    Applications subclass this once to bind their event registry and default
    topic (e.g. ``CdbMQTTHandler``); handler plugins then subclass that.

    Handlers can implement either:
    1. Specific event handler methods named by the application's EventRegistry
       (e.g. ``handle_item_log_add(event)``), which receive parsed, typed events.
    2. The generic ``handle_generic()`` method for messages without a specific method.
    3. ``handle()`` to take over routing entirely for messages without a specific method.

    Example:
        class AppHandler(MQTTHandler):
            default_topic_pattern = "app/#"
            event_registry = APP_EVENT_REGISTRY

        class MyHandler(AppHandler):
            async def handle_item_add(self, event: ItemAddEvent) -> None:
                self.logger.info(f"New item: {event.item_id}")

            @property
            def topic_pattern(self) -> str:
                # Override to subscribe to specific topics only
                return "app/item/#"
    """

    event_registry: ClassVar[Optional[EventRegistry]] = None
    default_topic_pattern: ClassVar[str] = "#"

    def __init__(
        self,
        api_factory=None,
        global_config: Optional[GlobalConfig] = None,
    ):
        """
        Initialize the handler.

        Args:
            api_factory: Optional application API factory for querying the portal API.
            global_config: Optional global configuration shared across all handlers.
        """
        self.api_factory = api_factory
        self.global_config = global_config
        self.logger = logging.getLogger(self.__class__.__name__)

    @property
    def topic_pattern(self) -> str:
        """
        Return the MQTT topic pattern this handler subscribes to.

        Defaults to the class's ``default_topic_pattern``. Override to subscribe
        to specific topics only.

        Examples:
            - "app/#" - matches all application topics
            - "app/add" - matches exact topic
            - "app/+/add" - matches single level wildcard

        Returns:
            MQTT topic pattern string.
        """
        return self.default_topic_pattern

    async def dispatch(self, message: MQTTMessage) -> None:
        """
        Route a message: the specific event method if implemented, otherwise ``handle()``.

        Args:
            message: The MQTT message to dispatch.
        """
        if await self._dispatch_specific(message):
            return
        await self.handle(message)

    async def handle(self, message: MQTTMessage) -> None:
        """
        Handle an MQTT message.

        The default implementation routes to a specific event handler method if
        available, otherwise to ``handle_generic()``.

        Args:
            message: The MQTT message to handle.
        """
        if await self._dispatch_specific(message):
            return
        await self.handle_generic(message)

    async def handle_generic(self, message: MQTTMessage) -> None:
        """
        Generic handler for messages that don't match specific event types.

        Args:
            message: The MQTT message to handle.
        """
        # Default implementation does nothing
        pass

    def resolve_event_method(
        self, message: MQTTMessage
    ) -> Optional[Tuple[EventSpec, Callable[[Any], Any]]]:
        """Return the event spec and bound handler method for a message, if implemented."""
        if self.event_registry is None:
            return None
        spec = self.event_registry.from_topic(message.topic)
        if spec is None:
            return None
        method = getattr(self, spec.handler_method, None)
        if not callable(method):
            return None
        return spec, method

    async def _dispatch_specific(self, message: MQTTMessage) -> bool:
        resolved = self.resolve_event_method(message)
        if resolved is None:
            return False
        spec, method = resolved
        self.logger.debug(
            f"Routing {message.topic} to {self.__class__.__name__}.{spec.handler_method}"
        )
        await method(self._parse_event(spec, message))
        return True

    @staticmethod
    def _parse_event(spec: EventSpec, message: MQTTMessage) -> BaseModel:
        """Parse an MQTT message into the spec's event model."""
        return spec.parse(message)

    def topic_matches(self, topic: str) -> bool:
        """
        Check if a topic matches this handler's pattern.

        Args:
            topic: The topic to check.

        Returns:
            True if the topic matches the pattern, False otherwise.
        """
        return self._match_mqtt_pattern(self.topic_pattern, topic)

    @staticmethod
    def _match_mqtt_pattern(pattern: str, topic: str) -> bool:
        """
        Match an MQTT topic against a pattern.

        Supports:
            - # (multi-level wildcard, must be at end)
            - + (single-level wildcard)
            - exact matches

        Args:
            pattern: The MQTT pattern.
            topic: The topic to match.

        Returns:
            True if the topic matches the pattern.
        """
        pattern_parts = pattern.split("/")
        topic_parts = topic.split("/")

        for i, pattern_part in enumerate(pattern_parts):
            if pattern_part == "#":
                # Multi-level wildcard matches everything remaining
                return True
            elif pattern_part == "+":
                # Single-level wildcard matches one level
                if i >= len(topic_parts):
                    return False
            else:
                # Exact match required
                if i >= len(topic_parts) or topic_parts[i] != pattern_part:
                    return False

        # Check if we've consumed all topic parts
        return len(topic_parts) == len(pattern_parts)


class PluginManager:
    """
    Manages loading and executing MQTT handlers.

    Supports passing both global and handler-specific configuration to handlers
    during instantiation. Only subclasses of ``handler_base`` are discovered.
    """

    def __init__(
        self,
        api_factory=None,
        config_manager: Optional[Any] = None,
        handler_base: Type[MQTTHandler] = MQTTHandler,
    ):
        """
        Initialize the plugin manager.

        Args:
            api_factory: Optional application API factory to pass to handlers.
            config_manager: Optional ConfigManager for handler configuration.
            handler_base: Handler base class; only its strict subclasses are loaded.
        """
        self.api_factory = api_factory
        self.config_manager = config_manager
        self.handler_base = handler_base
        self.handlers: List[MQTTHandler] = []
        self.logger = logging.getLogger(__name__)

    def is_handler_class(self, obj: Any) -> bool:
        """Return True if obj is a concrete handler class to load."""
        return (
            inspect.isclass(obj)
            and issubclass(obj, self.handler_base)
            and obj is not self.handler_base
            and not inspect.isabstract(obj)
        )

    def register_handler(self, handler: MQTTHandler) -> None:
        """
        Register a handler.

        Args:
            handler: The handler instance to register.
        """
        self.handlers.append(handler)
        self.logger.info(
            f"Registered handler {handler.__class__.__name__} "
            f"for topic pattern: {handler.topic_pattern}"
        )

    def register_handler_class(self, handler_class: Type[MQTTHandler]) -> None:
        """
        Register a handler by class.

        Attempts to pass both global configuration and handler-specific
        configuration to the handler if available.

        Args:
            handler_class: The handler class to instantiate and register.
        """
        handler_name = handler_class.__name__

        # Get global configuration if available
        global_config = None
        if self.config_manager:
            global_config = self.config_manager.get_global_config()

        # Try to get handler-specific configuration
        handler_config = None
        if self.config_manager:
            handler_config = self.config_manager.get_config(handler_name)

        # Prepare constructor arguments
        constructor_args = {
            "api_factory": self.api_factory,
        }

        # Add global_config if the handler accepts it
        if global_config:
            constructor_args["global_config"] = global_config

        # Add handler-specific configuration if available
        if handler_config and handler_config.config:
            constructor_args.update(handler_config.config)

        # Instantiate handler with configuration
        try:
            # First try with all arguments
            handler = handler_class(**constructor_args)  # type: ignore[arg-type]
            if global_config:
                self.logger.debug(f"Instantiated {handler_name} with global configuration")
            if handler_config:
                self.logger.debug(
                    f"Instantiated {handler_name} with handler configuration: {handler_config.config}"
                )
        except TypeError as e:
            # Handler might not accept all parameters, try with just what it accepts
            self.logger.debug(
                f"Handler {handler_name} does not accept all configuration parameters: {e}"
            )

            sig = inspect.signature(handler_class.__init__)
            accepted_params = set(sig.parameters.keys()) - {"self"}

            # Filter constructor args to only what the handler accepts
            filtered_args = {k: v for k, v in constructor_args.items() if k in accepted_params}

            try:
                handler = handler_class(**filtered_args)  # type: ignore[arg-type]
                self.logger.debug(
                    f"Instantiated {handler_name} with filtered parameters: {filtered_args.keys()}"
                )
            except Exception as e2:
                # Fall back to minimal instantiation
                self.logger.warning(
                    f"Failed to instantiate {handler_name} with configuration, "
                    f"falling back to minimal instantiation: {e2}"
                )
                handler = handler_class()

        self.register_handler(handler)

    def _register_module_handlers(self, module: ModuleType, source: Path) -> None:
        for name, obj in inspect.getmembers(module):
            if self.is_handler_class(obj):
                self.register_handler_class(obj)
                self.logger.info(f"Loaded handler from {source}: {name}")

    def load_handlers_from_directory(self, directory: Path) -> None:
        """
        Dynamically load handlers from a directory.

        Looks for Python files and packages in the directory and imports any classes
        that inherit from ``handler_base``.

        Supports:
        - Single Python files (*.py)
        - Python packages (directories with __init__.py)

        Args:
            directory: Path to the directory containing handler modules.
        """
        directory = Path(directory)
        if not directory.exists():
            self.logger.warning(f"Handler directory does not exist: {directory}")
            return

        # Load handlers from Python files
        for py_file in directory.glob("*.py"):
            if py_file.name.startswith("_"):
                continue

            module_name = py_file.stem
            try:
                spec = importlib.util.spec_from_file_location(module_name, py_file)
                if spec is None or spec.loader is None:
                    continue

                module = importlib.util.module_from_spec(spec)
                spec.loader.exec_module(module)
                self._register_module_handlers(module, py_file)

            except Exception as e:
                self.logger.error(f"Failed to load handlers from {py_file}: {e}")

        # Load handlers from Python packages (directories with __init__.py)
        for item in directory.iterdir():
            if not item.is_dir() or item.name.startswith("_") or item.name.startswith("."):
                continue

            if not (item / "__init__.py").exists():
                continue

            try:
                # Add parent directory to sys.path temporarily
                old_path = sys.path.copy()
                sys.path.insert(0, str(directory))
                try:
                    module = importlib.import_module(item.name)
                    self._register_module_handlers(module, item)
                finally:
                    sys.path = old_path

            except Exception as e:
                self.logger.error(f"Failed to load handlers from package {item}: {e}")

    async def handle_message(self, message: MQTTMessage) -> None:
        """
        Route a message to all matching handlers.

        Each matching handler receives the message via ``MQTTHandler.dispatch``.
        Errors in one handler are logged and do not affect the others.

        Args:
            message: The MQTT message to handle.
        """
        matching_handlers = self.get_handlers_for_topic(message.topic)

        if not matching_handlers:
            self.logger.debug(f"No handlers found for topic: {message.topic}")
            return

        for handler in matching_handlers:
            try:
                await handler.dispatch(message)
            except Exception as e:
                self.logger.error(
                    f"Error in handler {handler.__class__.__name__}: {e}",
                    exc_info=True,
                )

    def get_handlers_for_topic(self, topic: str) -> List[MQTTHandler]:
        """
        Get all handlers that match a topic.

        Args:
            topic: The MQTT topic.

        Returns:
            List of matching handlers.
        """
        return [h for h in self.handlers if h.topic_matches(topic)]
