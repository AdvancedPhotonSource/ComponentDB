"""
Configuration for ComponentDB MQTT handlers.
"""

from typing import Optional

from cdb_bely_message_broker_base import ConfigManager, GlobalConfig, HandlerConfig


class CdbGlobalConfig(GlobalConfig):
    """
    Global configuration for CDB handlers.

    URL configuration fields:
        cdb_url: Public-facing CDB portal URL used for links in notifications.
        api_url: Internal URL for broker-to-portal API calls; defaults to cdb_url.
    """

    PUBLIC_URL_KEY = "cdb_url"

    @property
    def cdb_url(self) -> Optional[str]:
        """Public-facing CDB portal URL, if configured."""
        return self.public_url


class CdbConfigManager(ConfigManager):
    """ConfigManager that builds CdbGlobalConfig for the "global" section."""

    def __init__(self) -> None:
        super().__init__(global_config_class=CdbGlobalConfig)


__all__ = ["CdbGlobalConfig", "CdbConfigManager", "HandlerConfig"]
