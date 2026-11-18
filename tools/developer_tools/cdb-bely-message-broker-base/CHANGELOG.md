# Changelog

All notable changes to the CDB/BELY MQTT Message Broker Base will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [1.0.0]

### Added
- Initial release, extracted from the BELY MQTT Framework (`bely_mqtt`).
- `EventRegistry`/`EventSpec`: applications map topics to typed handler methods and pydantic models.
- `MQTTHandler` with `event_registry`/`default_topic_pattern` class attributes and a single `dispatch()` path.
- `PluginManager(handler_base=...)` only discovers subclasses of the application handler base.
- `GlobalConfig.PUBLIC_URL_KEY`/`public_url`; `ConfigManager(global_config_class=...)`.
- `AppProfile` + `build_cli()` CLI factory (`start`, `list-handlers`) with a per-application env var prefix.
- `EventModel` base and `JavaDateTime` type for Jackson-serialized payloads.
