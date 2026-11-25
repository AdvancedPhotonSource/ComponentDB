"""
Generic data models for MQTT message brokers.

Application-specific event models (CDB, BELY) build on EventModel.
"""

import re
from datetime import datetime
from typing import Annotated, Any, Dict

from pydantic import BaseModel, BeforeValidator, ConfigDict

# Jackson's default Date string ends in "+0000"; normalize to "+00:00" for ISO 8601 parsers.
_COMPACT_OFFSET = re.compile(r"([+-]\d{2})(\d{2})$")


def _normalize_java_datetime(value: Any) -> Any:
    if isinstance(value, str):
        return _COMPACT_OFFSET.sub(r"\1:\2", value.strip())
    return value


JavaDateTime = Annotated[datetime, BeforeValidator(_normalize_java_datetime)]
"""Datetime that accepts ISO 8601, Jackson "+0000" offsets and epoch milliseconds."""


class EventModel(BaseModel):
    """Base for event payload models: camelCase aliases, unknown fields ignored."""

    model_config = ConfigDict(populate_by_name=True, extra="ignore")


class MQTTMessage(BaseModel):
    """Wrapper for an MQTT message with topic and payload."""

    model_config = ConfigDict(arbitrary_types_allowed=True)

    topic: str
    payload: Dict[str, Any]
    raw_payload: str = ""
