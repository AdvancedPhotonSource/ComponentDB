# CDB/BELY MQTT Message Broker Base Documentation

- **[api-reference.md](api-reference.md)**: classes for building an application broker and writing handlers.
- **[configuration.md](configuration.md)**: CLI options, environment variables and the YAML config file.

## Key Concepts

1. **Application layer**: an application package (e.g. `cdb_mqtt`) defines its event enum, pydantic models, `EventRegistry`, a handler base class and an `AppProfile`.
2. **Handlers**: plugin classes that subclass the application handler base and implement `handle_<event>` methods.
3. **Dispatch**: each message goes to every handler whose `topic_pattern` matches. The handler gets it through its specific method if it implements one, otherwise through `handle()` → `handle_generic()`.
