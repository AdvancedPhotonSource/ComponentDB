"""Shared fixtures: sample payloads for every CDB topic."""

import json
from pathlib import Path
from typing import Any, Dict

import pytest

FIXTURES_DIR = Path(__file__).parent / "fixtures"
PACKAGE_DIR = Path(__file__).resolve().parents[1]
REPO_ROOT = Path(__file__).resolve().parents[4]
JAVA_MQTT_DIR = REPO_ROOT / "src/java/CdbWebPortal/src/java/gov/anl/aps/cdb/mqtt"
EXAMPLE_HANDLERS_DIR = PACKAGE_DIR / "examples" / "handlers"


def load_event_payloads() -> Dict[str, Dict[str, Any]]:
    with open(FIXTURES_DIR / "events.json") as f:
        return json.load(f)


@pytest.fixture
def event_payloads() -> Dict[str, Dict[str, Any]]:
    return load_event_payloads()
