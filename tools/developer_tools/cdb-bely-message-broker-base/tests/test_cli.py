"""Tests for the CLI factory."""

from pathlib import Path
from typing import Any, Dict, List

import pytest
from click.testing import CliRunner

from cdb_bely_message_broker_base.cli import AppProfile, build_cli
from cdb_bely_message_broker_base.config import GlobalConfig
from cdb_bely_message_broker_base.mqtt_client import MQTTClient
from cdb_bely_message_broker_base.plugin import MQTTHandler


class AppHandler(MQTTHandler):
    default_topic_pattern = "app/#"


class AppGlobalConfig(GlobalConfig):
    PUBLIC_URL_KEY = "app_url"


class FakeClient(MQTTClient):
    """Records construction and subscriptions instead of connecting."""

    instances: List["FakeClient"] = []

    def __init__(self, **kwargs: Any) -> None:
        self.kwargs: Dict[str, Any] = kwargs
        self.plugin_manager = kwargs["plugin_manager"]
        self.topics: List[str] = []
        FakeClient.instances.append(self)

    def subscribe(self, topic: str) -> None:
        self.topics.append(topic)

    def start(self) -> None:
        pass


def make_profile(**overrides: Any) -> AppProfile:
    values: Dict[str, Any] = dict(
        prog_name="app-mqtt",
        version="9.9.9",
        description="App broker",
        default_topic="app/#",
        default_client_id="app-client",
        env_prefix="APP",
        default_lock_file="/tmp/unused.lock",
        handler_base=AppHandler,
        global_config_class=AppGlobalConfig,
        client_class=FakeClient,
    )
    values.update(overrides)
    return AppProfile(**values)


@pytest.fixture(autouse=True)
def reset_fake_client():
    FakeClient.instances = []


def test_profile_env():
    assert make_profile().env("CONFIG") == "APP_CONFIG"


def test_version():
    result = CliRunner().invoke(build_cli(make_profile()), ["--version"])
    assert result.exit_code == 0
    assert "app-mqtt" in result.output and "9.9.9" in result.output


def test_start_help_shows_prefixed_env_vars():
    result = CliRunner().invoke(build_cli(make_profile()), ["start", "--help"])
    assert result.exit_code == 0
    assert "--handlers-dir" in result.output


def test_start_uses_profile_defaults_and_env(tmp_path: Path):
    handlers_dir = tmp_path / "handlers"
    handlers_dir.mkdir()
    (handlers_dir / "h.py").write_text(
        "from tests.test_cli import AppHandler\n\nclass MyHandler(AppHandler):\n    pass\n"
    )
    config_file = tmp_path / "config.yaml"
    config_file.write_text("global:\n  app_url: https://public\n")
    loaded_urls: List[str] = []

    def loader(url: str) -> str:
        loaded_urls.append(url)
        return "factory"

    result = CliRunner().invoke(
        build_cli(make_profile(api_factory_loader=loader)),
        ["start"],
        env={
            "APP_HANDLERS_DIR": str(handlers_dir),
            "APP_CONFIG": str(config_file),
            "APP_LOCK_FILE": str(tmp_path / "app.lock"),
        },
    )

    assert result.exit_code == 0, result.output
    client = FakeClient.instances[0]
    assert client.kwargs["client_id"] == "app-client"
    assert client.topics == ["app/#"]
    # api_url falls back to the public URL from the config file
    assert loaded_urls == ["https://public"]
    manager = client.plugin_manager
    assert manager.handler_base is AppHandler
    assert [h.__class__.__name__ for h in manager.handlers] == ["MyHandler"]
    assert manager.handlers[0].api_factory == "factory"
    assert isinstance(manager.handlers[0].global_config, AppGlobalConfig)


def test_start_api_loader_import_error_disables_api(tmp_path: Path):
    def loader(url: str) -> Any:
        raise ImportError("not installed")

    result = CliRunner().invoke(
        build_cli(make_profile(api_factory_loader=loader)),
        ["start", "--api-url", "http://x", "--lock-file", str(tmp_path / "l"), "-t", "app/a"],
    )

    assert result.exit_code == 0, result.output
    client = FakeClient.instances[0]
    assert client.plugin_manager.api_factory is None
    assert client.topics == ["app/a"]


def test_list_handlers(tmp_path: Path):
    (tmp_path / "h.py").write_text(
        "from tests.test_cli import AppHandler\n\n"
        "class ListedHandler(AppHandler):\n    '''Does things.'''\n"
    )
    result = CliRunner().invoke(
        build_cli(make_profile()), ["list-handlers", "--handlers-dir", str(tmp_path)]
    )

    assert result.exit_code == 0
    assert "ListedHandler" in result.output
    assert "app/#" in result.output
    assert "Does things." in result.output


def test_list_handlers_none(tmp_path: Path):
    result = CliRunner().invoke(
        build_cli(make_profile()), ["list-handlers", "--handlers-dir", str(tmp_path)]
    )
    assert "No handlers found." in result.output
