"""Tests for the configuration system."""

import pytest

from cdb_bely_message_broker_base.config import ConfigManager, GlobalConfig


class AppGlobalConfig(GlobalConfig):
    PUBLIC_URL_KEY = "app_url"


def test_public_url_and_api_url_fallback():
    config = AppGlobalConfig({"app_url": "https://public"})
    assert config.public_url == "https://public"
    assert config.api_url == "https://public"

    config = AppGlobalConfig({"app_url": "https://public", "api_url": "http://localhost"})
    assert config.api_url == "http://localhost"


def test_base_public_url_key():
    assert GlobalConfig({"url": "https://u"}).public_url == "https://u"
    assert GlobalConfig().api_url is None


def test_config_manager_uses_global_config_class(tmp_path):
    config_file = tmp_path / "config.yaml"
    config_file.write_text(
        "global:\n  app_url: https://public\nhandlers:\n  MyHandler:\n    param: 3\n"
    )
    manager = ConfigManager(global_config_class=AppGlobalConfig)
    manager.load_from_file(config_file)

    assert isinstance(manager.get_global_config(), AppGlobalConfig)
    assert manager.get_global_config().public_url == "https://public"
    assert manager.get_config("MyHandler").get("param") == 3
    assert manager.has_config("MyHandler")
    assert not manager.has_config("Other")


def test_config_manager_empty_file(tmp_path):
    config_file = tmp_path / "empty.yaml"
    config_file.write_text("")
    manager = ConfigManager()
    manager.load_from_file(config_file)

    assert manager.get_global_config() is None
    assert manager.configs == {}


def test_config_manager_missing_file(tmp_path):
    with pytest.raises(FileNotFoundError):
        ConfigManager().load_from_file(tmp_path / "missing.yaml")
