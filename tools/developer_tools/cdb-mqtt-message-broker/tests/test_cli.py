"""Tests for the cdb-mqtt CLI."""

import sys

import pytest
from click.testing import CliRunner

from cdb_mqtt import CdbGlobalConfig, CdbMQTTClient, CdbMQTTHandler, CdbPluginManager, __version__
from cdb_mqtt.cli import CDB_PROFILE, cli, load_cdb_api_factory
from tests.conftest import EXAMPLE_HANDLERS_DIR


def test_profile():
    assert CDB_PROFILE.prog_name == "cdb-mqtt"
    assert CDB_PROFILE.default_topic == "cdb/#"
    assert CDB_PROFILE.default_client_id == "cdb-mqtt-client"
    assert CDB_PROFILE.default_lock_file == "/tmp/cdb-mqtt.lock"
    assert CDB_PROFILE.env("HANDLERS_DIR") == "CDB_HANDLERS_DIR"
    assert CDB_PROFILE.handler_base is CdbMQTTHandler
    assert CDB_PROFILE.global_config_class is CdbGlobalConfig
    assert CDB_PROFILE.client_class is CdbMQTTClient
    assert CDB_PROFILE.plugin_manager_class is CdbPluginManager


def test_version():
    result = CliRunner().invoke(cli, ["--version"])
    assert result.exit_code == 0
    assert "cdb-mqtt" in result.output and __version__ in result.output


def test_start_help():
    result = CliRunner().invoke(cli, ["start", "--help"])
    assert result.exit_code == 0
    assert "--handlers-dir" in result.output


def test_list_example_handlers():
    if not EXAMPLE_HANDLERS_DIR.exists():
        pytest.skip("examples not available")
    result = CliRunner().invoke(cli, ["list-handlers", "--handlers-dir", str(EXAMPLE_HANDLERS_DIR)])
    assert result.exit_code == 0
    assert "LoggingHandler" in result.output
    assert "MachineDesignAuditHandler" in result.output
    assert "cdb/machine-design/#" in result.output


def test_cdb_global_config():
    config = CdbGlobalConfig({"cdb_url": "https://cdb.test/cdb"})
    assert config.cdb_url == "https://cdb.test/cdb"
    assert config.api_url == "https://cdb.test/cdb"


def test_api_factory_loader_import_error(monkeypatch):
    monkeypatch.setitem(sys.modules, "CdbApiFactory", None)
    with pytest.raises(ImportError):
        load_cdb_api_factory("http://localhost:8080/cdb")
