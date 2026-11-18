# API Reference

## Events

### `EventSpec(topic, handler_method, model)`
One event: the exact topic, the handler method name, and the pydantic model for its payload. `spec.parse(message)` validates `message.payload` into `model`.

### `EventRegistry`
- `register(topic, model, handler_method) -> EventSpec`: register one event (duplicate topics raise `ValueError`).
- `EventRegistry.from_enum(enum_cls, model_map)`: builds a registry from an enum whose values are topics. The handler method for each member is `handle_<member_name_lower>`. Members missing from `model_map` are skipped.
- `from_topic(topic) -> EventSpec | None`, `parse(spec, message)`, `in`, `len()`, iteration.

### `handler_method_name(member) -> str`
`ITEM_LOG_ADD` → `handle_item_log_add`.

## Models

- `MQTTMessage(topic, payload: dict, raw_payload: str)`: a received message.
- `EventModel`: base for payload models (`populate_by_name=True`, `extra="ignore"`). Declare camelCase aliases with `Field(alias=...)`.
- `JavaDateTime`: a `datetime` type that accepts ISO 8601, Jackson's `+0000` offset, and epoch milliseconds.

## Handlers

### `MQTTHandler`
Class attributes (set once in the application handler base):
- `event_registry: EventRegistry | None`
- `default_topic_pattern: str` (default `"#"`)

Instance:
- `__init__(api_factory=None, global_config=None)`. Handler-specific config keys are also passed as keyword arguments. Constructor parameters a handler doesn't accept are filtered out.
- `topic_pattern`: property; override it to narrow the subscription.
- `async dispatch(message)`: calls the specific event method if implemented, otherwise `handle(message)`.
- `async handle(message)`: by default routes to the specific method, otherwise `handle_generic(message)`.
- `async handle_generic(message)`: no-op by default.
- `topic_matches(topic)`, `logger`, `api_factory`, `global_config`.

### `PluginManager(api_factory=None, config_manager=None, handler_base=MQTTHandler)`
- `load_handlers_from_directory(path)`: loads `*.py` modules and packages (directories with `__init__.py`). It registers every concrete strict subclass of `handler_base`, so the application base class itself is never registered.
- `register_handler(instance)`, `register_handler_class(cls)`, `get_handlers_for_topic(topic)`.
- `async handle_message(message)`: dispatches to every matching handler. Errors are logged per handler.

## Client

### `MQTTClient(broker_host, broker_port=1883, client_id=..., username=None, password=None, plugin_manager=None)`
- `subscribe(topic)` (topics are resubscribed on reconnect), `start()` (blocking), `await start_async()`, `connect()`, `disconnect()`, `is_connected()`.

## Configuration

- `GlobalConfig(config)`: `get(key, default)`, `public_url` (the value at `PUBLIC_URL_KEY`), and `api_url` (falls back to `public_url`). Applications subclass it and set `PUBLIC_URL_KEY`.
- `HandlerConfig(handler_name, config)`.
- `ConfigManager(global_config_class=GlobalConfig)`: `load_from_file(path)`, `load_from_dict(d)`, `get_global_config()`, `get_config(name)`.

## CLI

### `AppProfile`
Fields:
- `prog_name`, `version`, `description`
- `default_topic`, `default_client_id`, `env_prefix`, `default_lock_file`
- `handler_base`, `global_config_class`
- `api_factory_loader(api_url)`: may raise `ImportError` to disable API features.
- `client_class`, `plugin_manager_class`

### `build_cli(profile) -> click.Group`
Commands: `start`, `list-handlers`, `validate-config`. See [configuration.md](configuration.md).
