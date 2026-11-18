"""
Command-line interface factory for MQTT message brokers.

Applications describe themselves with an AppProfile and call ``build_cli`` to
get a click group with ``start`` and ``list-handlers`` commands.
"""

import logging
import sys
from dataclasses import dataclass
from pathlib import Path
from typing import Any, Callable, Optional, Type

import click
from dotenv import load_dotenv

from cdb_bely_message_broker_base.config import ConfigManager, GlobalConfig
from cdb_bely_message_broker_base.mqtt_client import MQTTClient
from cdb_bely_message_broker_base.plugin import MQTTHandler, PluginManager

logger = logging.getLogger(__name__)

LOG_FORMAT = "%(asctime)s - %(name)s - %(levelname)s - %(message)s"


@dataclass(frozen=True)
class AppProfile:
    """
    Application-specific CLI settings.

    Attributes:
        prog_name: CLI program name (e.g. "cdb-mqtt").
        version: Application version shown by --version.
        description: Help text for the CLI group.
        default_topic: Default subscription topic (e.g. "cdb/#").
        default_client_id: Default MQTT client ID.
        env_prefix: Prefix for app env vars: {PREFIX}_HANDLERS_DIR, _API_URL,
            _API_KEY, _CONFIG, _LOCK_FILE.
        default_lock_file: Default single-instance lock file path.
        handler_base: Base class handler plugins must subclass.
        global_config_class: GlobalConfig subclass for the "global" config section.
        api_factory_loader: Builds an API factory from an API URL; may raise ImportError.
        client_class: MQTTClient (sub)class to instantiate.
        plugin_manager_class: PluginManager (sub)class to instantiate.
    """

    prog_name: str
    version: str
    description: str
    default_topic: str
    default_client_id: str
    env_prefix: str
    default_lock_file: str
    handler_base: Type[MQTTHandler] = MQTTHandler
    global_config_class: Type[GlobalConfig] = GlobalConfig
    api_factory_loader: Optional[Callable[[str], Any]] = None
    client_class: Type[MQTTClient] = MQTTClient
    plugin_manager_class: Type[PluginManager] = PluginManager

    def env(self, name: str) -> str:
        """Return the prefixed env var name, e.g. env("CONFIG") -> "CDB_CONFIG"."""
        return f"{self.env_prefix}_{name}"


def _load_api_factory(profile: AppProfile, api_url: Optional[str]) -> Any:
    if not api_url or profile.api_factory_loader is None:
        return None
    try:
        api_factory = profile.api_factory_loader(api_url)
        logger.info(f"API factory initialized: {api_url}")
        return api_factory
    except ImportError as e:
        logger.warning(f"API factory not available ({e}). API features disabled.")
        return None


def build_cli(profile: AppProfile) -> click.Group:
    """Build the click CLI group for an application profile."""

    @click.group(help=profile.description)
    @click.version_option(version=profile.version, prog_name=profile.prog_name)
    def cli() -> None:
        pass

    @cli.command()
    @click.option(
        "--broker-host",
        default="localhost",
        envvar="MQTT_BROKER_HOST",
        help="MQTT broker hostname or IP address.",
    )
    @click.option(
        "--broker-port",
        default=1883,
        type=int,
        envvar="MQTT_BROKER_PORT",
        help="MQTT broker port.",
    )
    @click.option(
        "--client-id",
        default=profile.default_client_id,
        envvar="MQTT_CLIENT_ID",
        help="MQTT client ID.",
    )
    @click.option(
        "--username",
        default=None,
        envvar="MQTT_USERNAME",
        help="MQTT broker username.",
    )
    @click.option(
        "--password",
        default=None,
        envvar="MQTT_PASSWORD",
        help="MQTT broker password.",
    )
    @click.option(
        "--topic",
        "-t",
        multiple=True,
        default=[profile.default_topic],
        help="MQTT topic(s) to subscribe to. Can be specified multiple times.",
    )
    @click.option(
        "--handlers-dir",
        type=click.Path(exists=False, file_okay=False, dir_okay=True, path_type=Path),
        default=None,
        envvar=profile.env("HANDLERS_DIR"),
        help="Directory containing handler plugins.",
    )
    @click.option(
        "--api-url",
        default=None,
        envvar=profile.env("API_URL"),
        help="Portal API base URL for querying additional information.",
    )
    @click.option(
        "--api-key",
        default=None,
        envvar=profile.env("API_KEY"),
        help="Portal API key for authentication.",
    )
    @click.option(
        "--log-level",
        type=click.Choice(["DEBUG", "INFO", "WARNING", "ERROR", "CRITICAL"]),
        default="INFO",
        envvar="LOG_LEVEL",
        help="Logging level.",
    )
    @click.option(
        "--env-file",
        type=click.Path(exists=True, file_okay=True, dir_okay=False, path_type=Path),
        default=None,
        help="Path to .env file for environment variables.",
    )
    @click.option(
        "--config",
        type=click.Path(exists=True, file_okay=True, dir_okay=False, path_type=Path),
        default=None,
        envvar=profile.env("CONFIG"),
        help="Path to configuration file for handlers (YAML format).",
    )
    @click.option(
        "--lock-file",
        type=click.Path(file_okay=True, dir_okay=False, path_type=Path),
        default=profile.default_lock_file,
        envvar=profile.env("LOCK_FILE"),
        help="Path to lock file for single-instance enforcement.",
    )
    def start(
        broker_host: str,
        broker_port: int,
        client_id: str,
        username: Optional[str],
        password: Optional[str],
        topic: tuple[str, ...],
        handlers_dir: Optional[Path],
        api_url: Optional[str],
        api_key: Optional[str],
        log_level: str,
        env_file: Optional[Path],
        config: Optional[Path],
        lock_file: Path,
    ) -> None:
        """Start the MQTT client with registered handlers."""
        logging.basicConfig(level=logging.INFO, format=LOG_FORMAT)

        # Acquire single-instance lock
        import fcntl

        try:
            lock_fd = open(lock_file, "w")  # noqa: SIM115
            fcntl.flock(lock_fd, fcntl.LOCK_EX | fcntl.LOCK_NB)
        except BlockingIOError:
            logger.error(
                f"Another instance of {profile.prog_name} is already running "
                f"(lock file: {lock_file}). Use --lock-file to specify a different "
                "lock file if you need multiple instances."
            )
            sys.exit(1)

        # Keep lock_fd alive for the lifetime of the process
        start._lock_fd = lock_fd  # type: ignore[attr-defined]

        # Load environment variables from file if provided
        if env_file:
            load_dotenv(env_file)

        # Set logging level
        logging.getLogger().setLevel(log_level)

        logger.info(f"Starting {profile.prog_name} {profile.version}")
        logger.info(f"Broker: {broker_host}:{broker_port}")
        logger.info(f"Topics: {', '.join(topic)}")

        # Initialize configuration manager
        config_manager = None
        if config:
            config_manager = ConfigManager(global_config_class=profile.global_config_class)
            try:
                config_manager.load_from_file(config)
                logger.info(f"Loaded handler configuration from: {config}")
            except Exception as e:
                logger.error(f"Failed to load configuration file: {e}")
                sys.exit(1)

        # Fall back to api_url from config (which itself falls back to the public URL)
        if not api_url and config_manager and config_manager.global_config:
            api_url = config_manager.global_config.api_url
            if api_url:
                logger.info(f"Using api_url from config file: {api_url}")

        api_factory = _load_api_factory(profile, api_url)

        plugin_manager = profile.plugin_manager_class(
            api_factory=api_factory,
            config_manager=config_manager,
            handler_base=profile.handler_base,
        )

        if handlers_dir:
            logger.info(f"Loading handlers from: {handlers_dir}")
            plugin_manager.load_handlers_from_directory(handlers_dir)

        if not plugin_manager.handlers:
            logger.warning("No handlers registered. Messages will be received but not processed.")

        mqtt_client = profile.client_class(
            broker_host=broker_host,
            broker_port=broker_port,
            client_id=client_id,
            username=username,
            password=password,
            plugin_manager=plugin_manager,
        )

        for t in topic:
            mqtt_client.subscribe(t)

        try:
            mqtt_client.start()
        except KeyboardInterrupt:
            logger.info("Shutting down...")
            sys.exit(0)
        except Exception as e:
            logger.error(f"Fatal error: {e}", exc_info=True)
            sys.exit(1)

    @cli.command()
    @click.option(
        "--handlers-dir",
        type=click.Path(exists=True, file_okay=False, dir_okay=True, path_type=Path),
        required=True,
        help="Directory containing handler plugins.",
    )
    def list_handlers(handlers_dir: Path) -> None:
        """List all available handlers in a directory."""
        plugin_manager = profile.plugin_manager_class(handler_base=profile.handler_base)
        plugin_manager.load_handlers_from_directory(handlers_dir)

        if not plugin_manager.handlers:
            click.echo("No handlers found.")
            return

        click.echo(f"Found {len(plugin_manager.handlers)} handler(s):\n")
        for handler in plugin_manager.handlers:
            click.echo(f"  {handler.__class__.__name__}")
            click.echo(f"    Topic Pattern: {handler.topic_pattern}")
            if handler.__doc__:
                click.echo(f"    Description: {handler.__doc__.strip()}")
            click.echo()

    @cli.command()
    def validate_config() -> None:
        """Validate the current configuration."""
        click.echo("Configuration validation not yet implemented.")

    return cli
