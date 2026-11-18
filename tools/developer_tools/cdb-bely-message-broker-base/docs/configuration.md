# Configuration

Examples use `PREFIX` for the application's `AppProfile.env_prefix` (`CDB`, `BELY`, …) and `<prog>` for its CLI name.

## `<prog> start` options

| Option | Env var | Default |
|---|---|---|
| `--broker-host` | `MQTT_BROKER_HOST` | `localhost` |
| `--broker-port` | `MQTT_BROKER_PORT` | `1883` |
| `--client-id` | `MQTT_CLIENT_ID` | `profile.default_client_id` |
| `--username` / `--password` | `MQTT_USERNAME` / `MQTT_PASSWORD` | none |
| `--topic`, `-t` (repeatable) | n/a | `profile.default_topic` |
| `--handlers-dir` | `PREFIX_HANDLERS_DIR` | none |
| `--api-url` | `PREFIX_API_URL` | falls back to `global.api_url`, then the public URL |
| `--api-key` | `PREFIX_API_KEY` | none |
| `--config` | `PREFIX_CONFIG` | none |
| `--lock-file` | `PREFIX_LOCK_FILE` | `profile.default_lock_file` |
| `--log-level` | `LOG_LEVEL` | `INFO` |
| `--env-file` | n/a | none (loads a `.env` file) |

Only one instance runs per lock file. To run several brokers on one host, give each its own `--lock-file`.

`<prog> list-handlers --handlers-dir DIR` lists the handlers that would be loaded.

## YAML config file

```yaml
global:
  cdb_url: https://cdb.example.com/cdb   # key = GlobalConfig.PUBLIC_URL_KEY
  api_url: http://localhost:8080/cdb     # optional, defaults to the public URL

handlers:
  MyHandler:          # handler class name
    output_dir: /tmp  # passed as MyHandler(output_dir="/tmp", ...)
```

- `global` becomes `handler.global_config`, an instance of the application's `GlobalConfig` subclass.
- `handlers.<ClassName>` entries are passed as keyword arguments to that handler's constructor.
