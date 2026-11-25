# Changelog

All notable changes to the CDB MQTT Message Broker will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [1.0.0]

### Added
- Initial release, built on `cdb-bely-message-broker-base`.
- `CdbEventType` for every topic published by the ComponentDB portal (`CdbMqttTopic`).
- Pydantic models for all CDB event payloads and snapshots.
- `CdbMQTTHandler` base class with typed `handle_<event>` methods; default topic `cdb/#`.
- `cdb-mqtt` CLI (`start`, `list-handlers`) with `CDB_*` environment variables.
- Optional `CdbApiFactory` integration (`componentdb-api` extra).
- Example handlers, systemd unit and configuration.
