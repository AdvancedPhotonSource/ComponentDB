"""Tests for BELY event models."""

from datetime import datetime

import pytest

from bely_mqtt.models import (
    CoreEvent,
    LogEntryAddEvent,
    LogEntryDeleteEvent,
    LogEntryReplyDeleteEvent,
    LogbookInfo,
)


@pytest.fixture
def sample_log_entry_add_payload():
    """Sample log entry add event payload."""
    return {
        "description": "log entry was added",
        "eventTimestamp": "2025-11-21T16:38:06.157+00:00",
        "parentLogDocumentInfo": {
            "name": "[2025/11/21/2] New Document",
            "id": 105,
            "lastModifiedOnDateTime": "2025-11-21T16:37:42.000+00:00",
            "createdByUsername": "logr",
            "lastModifiedByUsername": "logr",
            "enteredOnDateTime": "2025-11-21T16:37:42.000+00:00",
            "ownerUsername": "logr",
            "ownerUserGroupName": "LOGR_ADMIN",
        },
        "logInfo": {
            "id": 267,
            "lastModifiedOnDateTime": "2025-11-21T16:38:00.392+00:00",
            "lastModifiedByUsername": "logr",
            "enteredByUsername": "logr",
            "enteredOnDateTime": "2025-11-21T16:38:00.392+00:00",
        },
        "logbookList": [{"name": "studies-sr", "id": 7, "displayName": "SR"}],
        "textDiff": "New Log Entry Added",
        "entityName": "Log",
        "entityId": 267,
        "eventTriggedByUsername": "logr",
    }


@pytest.fixture
def sample_simple_event_payload():
    """Sample simple event payload."""
    return {
        "description": "Add action completed",
        "eventTimestamp": "2025-11-21T16:37:52.537+00:00",
        "entityName": "ItemDomainLogbook",
        "entityId": 105,
        "eventTriggedByUsername": "logr",
    }


def test_core_event_parsing(sample_simple_event_payload):
    """Test parsing of core event."""
    event = CoreEvent(**sample_simple_event_payload)

    assert event.description == "Add action completed"
    assert event.entity_name == "ItemDomainLogbook"
    assert event.entity_id == 105
    assert event.event_triggered_by_username == "logr"
    assert isinstance(event.event_timestamp, datetime)


def test_log_entry_add_event_parsing(sample_log_entry_add_payload):
    """Test parsing of log entry add event."""
    event = LogEntryAddEvent(**sample_log_entry_add_payload)

    assert event.description == "log entry was added"
    assert event.log_info.id == 267
    assert event.parent_log_document_info.id == 105
    assert event.parent_log_document_info.name == "[2025/11/21/2] New Document"
    assert len(event.logbook_list) == 1
    assert event.logbook_list[0].display_name == "SR"
    assert event.text_diff == "New Log Entry Added"
    assert event.event_triggered_by_username == "logr"


def test_logbook_info_parsing():
    """Test parsing of logbook info."""
    data = {"name": "studies-sr", "id": 7, "displayName": "SR"}
    logbook = LogbookInfo(**data)

    assert logbook.name == "studies-sr"
    assert logbook.id == 7
    assert logbook.display_name == "SR"


def test_event_timestamp_parsing(sample_simple_event_payload):
    """Test that event timestamp is properly parsed."""
    event = CoreEvent(**sample_simple_event_payload)

    assert isinstance(event.event_timestamp, datetime)
    assert event.event_timestamp.year == 2025
    assert event.event_timestamp.month == 11
    assert event.event_timestamp.day == 21


def test_alias_field_mapping(sample_simple_event_payload):
    """Test that aliased fields are properly mapped."""
    event = CoreEvent(**sample_simple_event_payload)

    # Check that snake_case attributes work
    assert event.event_triggered_by_username == "logr"
    assert event.entity_name == "ItemDomainLogbook"
    assert event.entity_id == 105


@pytest.fixture
def sample_log_entry_delete_payload():
    """Sample log entry delete event payload."""
    return {
        "description": "log entry was deleted",
        "eventTimestamp": "2025-11-21T16:40:00.000+00:00",
        "parentLogDocumentInfo": {
            "name": "[2025/11/21/2] New Document",
            "id": 105,
            "lastModifiedOnDateTime": "2025-11-21T16:38:20.000+00:00",
            "createdByUsername": "logr",
            "lastModifiedByUsername": "logr",
            "enteredOnDateTime": "2025-11-21T16:37:42.000+00:00",
            "ownerUsername": "logr",
            "ownerUserGroupName": "LOGR_ADMIN",
        },
        "logInfo": {
            "id": 267,
            "lastModifiedOnDateTime": "2025-11-21T16:40:00.000+00:00",
            "lastModifiedByUsername": "logr",
            "enteredByUsername": "logr",
            "enteredOnDateTime": "2025-11-21T16:38:00.000+00:00",
        },
        "logbookList": [{"name": "studies-sr", "id": 7, "displayName": "SR"}],
        "textDiff": "Log Entry Deleted",
        "entityName": "Log",
        "entityId": 267,
        "eventTriggedByUsername": "logr",
    }


@pytest.fixture
def sample_log_entry_reply_delete_payload():
    """Sample log entry reply delete event payload."""
    return {
        "description": "reply log entry was deleted",
        "eventTimestamp": "2025-11-21T16:41:00.000+00:00",
        "parentLogDocumentInfo": {
            "name": "[2025/11/21/2] New Document",
            "id": 105,
            "lastModifiedOnDateTime": "2025-11-21T16:40:00.000+00:00",
            "createdByUsername": "logr",
            "lastModifiedByUsername": "logr",
            "enteredOnDateTime": "2025-11-21T16:37:42.000+00:00",
            "ownerUsername": "logr",
            "ownerUserGroupName": "LOGR_ADMIN",
        },
        "logInfo": {
            "id": 268,
            "lastModifiedOnDateTime": "2025-11-21T16:41:00.000+00:00",
            "lastModifiedByUsername": "logr",
            "enteredByUsername": "logr",
            "enteredOnDateTime": "2025-11-21T16:38:16.000+00:00",
        },
        "logbookList": [{"name": "studies-sr", "id": 7, "displayName": "SR"}],
        "textDiff": "Reply Deleted",
        "parentLogInfo": {
            "id": 267,
            "lastModifiedOnDateTime": "2025-11-21T16:38:14.000+00:00",
            "lastModifiedByUsername": "logr",
            "enteredByUsername": "logr",
            "enteredOnDateTime": "2025-11-21T16:38:00.000+00:00",
        },
        "entityName": "Log",
        "entityId": 268,
        "eventTriggedByUsername": "logr",
    }


def test_log_entry_delete_event_parsing(sample_log_entry_delete_payload):
    """Test parsing of log entry delete event."""
    event = LogEntryDeleteEvent(**sample_log_entry_delete_payload)

    assert event.description == "log entry was deleted"
    assert event.log_info.id == 267
    assert event.parent_log_document_info.id == 105
    assert event.parent_log_document_info.name == "[2025/11/21/2] New Document"
    assert len(event.logbook_list) == 1
    assert event.logbook_list[0].display_name == "SR"
    assert event.text_diff == "Log Entry Deleted"
    assert event.entity_id == 267
    assert event.event_triggered_by_username == "logr"
    assert isinstance(event.event_timestamp, datetime)


def test_log_entry_reply_delete_event_parsing(sample_log_entry_reply_delete_payload):
    """Test parsing of log entry reply delete event."""
    event = LogEntryReplyDeleteEvent(**sample_log_entry_reply_delete_payload)

    assert event.description == "reply log entry was deleted"
    assert event.log_info.id == 268
    assert event.parent_log_info.id == 267
    assert event.parent_log_document_info.id == 105
    assert event.parent_log_document_info.name == "[2025/11/21/2] New Document"
    assert len(event.logbook_list) == 1
    assert event.logbook_list[0].display_name == "SR"
    assert event.text_diff == "Reply Deleted"
    assert event.entity_id == 268
    assert event.event_triggered_by_username == "logr"
    assert isinstance(event.event_timestamp, datetime)
