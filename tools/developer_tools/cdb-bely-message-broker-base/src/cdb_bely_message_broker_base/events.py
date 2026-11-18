"""
Event registry for MQTT message brokers.

Applications (CDB, BELY) describe their topics and payload models with an
EventRegistry; the framework uses it to route messages to typed handler methods.
"""

from dataclasses import dataclass
from enum import Enum
from typing import Dict, Iterator, Mapping, Optional, Type, TypeVar

from pydantic import BaseModel

from cdb_bely_message_broker_base.models import MQTTMessage

E = TypeVar("E", bound=Enum)


def handler_method_name(event_type: Enum) -> str:
    """
    Get the handler method name for an event enum member.

    Examples:
        LOG_ENTRY_ADD -> "handle_log_entry_add"
        MACHINE_DESIGN_MOVE -> "handle_machine_design_move"
    """
    return f"handle_{event_type.name.lower()}"


@dataclass(frozen=True)
class EventSpec:
    """Describes one event: its exact topic, handler method name and payload model."""

    topic: str
    handler_method: str
    model: Type[BaseModel]

    def parse(self, message: MQTTMessage) -> BaseModel:
        """Parse the message payload into this event's model."""
        return self.model.model_validate(message.payload)


class EventRegistry:
    """
    Maps exact MQTT topics to EventSpecs.

    Example:
        class MyEventType(Enum):
            ITEM_ADD = "app/item/add"

        registry = EventRegistry.from_enum(MyEventType, {MyEventType.ITEM_ADD: ItemAddEvent})
        # messages on "app/item/add" are routed to handle_item_add(event: ItemAddEvent)
    """

    def __init__(self) -> None:
        self._specs: Dict[str, EventSpec] = {}

    def register(
        self,
        topic: str,
        model: Type[BaseModel],
        handler_method: str,
    ) -> EventSpec:
        """Register an event and return its spec."""
        if topic in self._specs:
            raise ValueError(f"Topic already registered: {topic}")
        spec = EventSpec(topic=topic, handler_method=handler_method, model=model)
        self._specs[topic] = spec
        return spec

    @classmethod
    def from_enum(
        cls,
        enum_cls: Type[E],
        model_map: Mapping[E, Type[BaseModel]],
    ) -> "EventRegistry":
        """
        Build a registry from an enum whose values are topics.

        Members without a model are skipped, so their messages fall back to the
        generic handler.
        """
        registry = cls()
        for member in enum_cls:
            model = model_map.get(member)
            if model is not None:
                registry.register(str(member.value), model, handler_method_name(member))
        return registry

    def from_topic(self, topic: str) -> Optional[EventSpec]:
        """Return the spec registered for an exact topic, if any."""
        return self._specs.get(topic)

    def parse(self, spec: EventSpec, message: MQTTMessage) -> BaseModel:
        """Parse a message into the spec's model."""
        return spec.parse(message)

    def __contains__(self, topic: object) -> bool:
        return topic in self._specs

    def __iter__(self) -> Iterator[EventSpec]:
        return iter(self._specs.values())

    def __len__(self) -> int:
        return len(self._specs)

    def __repr__(self) -> str:
        return f"EventRegistry({list(self._specs)})"
