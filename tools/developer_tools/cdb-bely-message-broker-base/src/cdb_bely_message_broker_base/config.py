"""
Configuration system for MQTT handlers.

This module provides a configuration system that allows handlers to receive
configuration parameters when they are instantiated.
"""

import logging
from pathlib import Path
from typing import Any, ClassVar, Dict, Optional, Type, cast

import yaml

logger = logging.getLogger(__name__)


class GlobalConfig:
    """
    Global configuration shared across all handlers.

    Stores configuration parameters that will be available to all handlers.

    URL configuration fields:
        <PUBLIC_URL_KEY>: The public-facing portal URL that end-users access in
                  their browser (e.g. ``cdb_url``, ``bely_url``). Used to generate
                  clickable links in notifications.
        api_url:  The internal URL used by the broker for server-to-server API
                  calls to the portal. Defaults to the public URL if not set.
                  Set this to localhost or an internal hostname when the broker
                  runs on the same machine as the portal. Never used in
                  user-facing notification content.

    Applications subclass this and set ``PUBLIC_URL_KEY``.
    """

    PUBLIC_URL_KEY: ClassVar[str] = "url"

    def __init__(self, config: Optional[Dict[str, Any]] = None):
        """
        Initialize global configuration.

        Args:
            config: Dictionary of global configuration parameters.
        """
        self.config = config or {}

    def get(self, key: str, default: Any = None) -> Any:
        """
        Get a configuration value.

        Args:
            key: Configuration key.
            default: Default value if key not found.

        Returns:
            Configuration value or default.
        """
        return self.config.get(key, default)

    @property
    def public_url(self) -> Optional[str]:
        """
        Get the public-facing portal URL (the ``PUBLIC_URL_KEY`` entry).

        Returns:
            Public URL if configured, None otherwise.
        """
        return cast(Optional[str], self.config.get(self.PUBLIC_URL_KEY))

    @property
    def api_url(self) -> Optional[str]:
        """
        Get the portal API URL for internal server-to-server API calls.

        Defaults to the public URL if not explicitly configured.

        Returns:
            API URL if configured, otherwise falls back to public_url.
            Returns None if neither is configured.
        """
        return cast(Optional[str], self.config.get("api_url")) or self.public_url

    def __repr__(self) -> str:
        """Return string representation."""
        return f"{self.__class__.__name__}({self.config})"


class HandlerConfig:
    """
    Configuration for a handler.

    Stores configuration parameters that will be passed to handler constructors.
    """

    def __init__(self, handler_name: str, config: Optional[Dict[str, Any]] = None):
        """
        Initialize handler configuration.

        Args:
            handler_name: Name of the handler class.
            config: Dictionary of configuration parameters.
        """
        self.handler_name = handler_name
        self.config = config or {}

    def get(self, key: str, default: Any = None) -> Any:
        """
        Get a configuration value.

        Args:
            key: Configuration key.
            default: Default value if key not found.

        Returns:
            Configuration value or default.
        """
        return self.config.get(key, default)

    def __repr__(self) -> str:
        """Return string representation."""
        return f"HandlerConfig({self.handler_name}, {self.config})"


class ConfigManager:
    """
    Manages configuration for handlers.

    Loads configuration from files or dictionaries and provides it to handlers.
    Supports both global configuration (shared across all handlers) and
    handler-specific configuration.
    """

    def __init__(self, global_config_class: Type[GlobalConfig] = GlobalConfig):
        """
        Initialize the configuration manager.

        Args:
            global_config_class: Application GlobalConfig subclass to instantiate.
        """
        self.global_config_class = global_config_class
        self.global_config: Optional[GlobalConfig] = None
        self.configs: Dict[str, HandlerConfig] = {}
        self.logger = logging.getLogger(__name__)

    def load_from_file(self, config_file: Path) -> None:
        """
        Load configuration from a YAML file.

        File format:
            global:
              shared_param: value
              another_param: value
              cdb_url: https://cdb.example.com  # public URL (key set by PUBLIC_URL_KEY)
              api_url: http://localhost:8080     # optional; internal API URL, defaults to public URL

            handlers:
              MyHandler:
                param1: value1
                param2: value2

        Args:
            config_file: Path to the YAML configuration file.

        Raises:
            FileNotFoundError: If the configuration file doesn't exist.
            yaml.YAMLError: If the file is not valid YAML.
        """
        config_file = Path(config_file)
        if not config_file.exists():
            raise FileNotFoundError(f"Configuration file not found: {config_file}")

        try:
            with open(config_file) as f:
                data = yaml.safe_load(f) or {}

            # Load global configuration if present
            if "global" in data:
                self.set_global_config(data["global"])
                self.logger.info(f"Loaded global configuration: {data['global']}")

            # Load handler-specific configurations
            handlers_config = data.get("handlers", {})
            for handler_name, config in handlers_config.items():
                self.set_config(handler_name, config)

            self.logger.info(f"Loaded configuration from {config_file}")
        except yaml.YAMLError as e:
            self.logger.error(f"Invalid YAML in configuration file: {e}")
            raise

    def load_from_dict(self, config_dict: Dict[str, Any]) -> None:
        """
        Load configuration from a dictionary.

        Args:
            config_dict: Dictionary with optional "global" and "handlers" keys.
        """
        # Load global configuration if present
        if "global" in config_dict:
            self.set_global_config(config_dict["global"])

        # Load handler-specific configurations
        handlers_config = config_dict.get("handlers", {})
        for handler_name, config in handlers_config.items():
            self.set_config(handler_name, config)

    def set_global_config(self, config: Dict[str, Any]) -> None:
        """
        Set global configuration.

        Args:
            config: Global configuration dictionary.
        """
        self.global_config = self.global_config_class(config)
        self.logger.debug(f"Set global configuration: {config}")

    def get_global_config(self) -> Optional[GlobalConfig]:
        """
        Get global configuration.

        Returns:
            GlobalConfig if set, None otherwise.
        """
        return self.global_config

    def set_config(self, handler_name: str, config: Dict[str, Any]) -> None:
        """
        Set configuration for a handler.

        Args:
            handler_name: Name of the handler class.
            config: Configuration dictionary.
        """
        self.configs[handler_name] = HandlerConfig(handler_name, config)
        self.logger.debug(f"Set configuration for {handler_name}: {config}")

    def get_config(self, handler_name: str) -> Optional[HandlerConfig]:
        """
        Get configuration for a handler.

        Args:
            handler_name: Name of the handler class.

        Returns:
            HandlerConfig if found, None otherwise.
        """
        return self.configs.get(handler_name)

    def has_config(self, handler_name: str) -> bool:
        """
        Check if configuration exists for a handler.

        Args:
            handler_name: Name of the handler class.

        Returns:
            True if configuration exists.
        """
        return handler_name in self.configs

    def __repr__(self) -> str:
        """Return string representation."""
        return f"ConfigManager(global={self.global_config}, handlers={self.configs})"
