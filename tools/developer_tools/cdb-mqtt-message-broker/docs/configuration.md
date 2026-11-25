# Configuration

## `cdb-mqtt start`

| Option | Env var | Default |
|---|---|---|
| `--broker-host` | `MQTT_BROKER_HOST` | `localhost` |
| `--broker-port` | `MQTT_BROKER_PORT` | `1883` |
| `--client-id` | `MQTT_CLIENT_ID` | `cdb-mqtt-client` |
| `--username` / `--password` | `MQTT_USERNAME` / `MQTT_PASSWORD` | none |
| `--topic`, `-t` (repeatable) | n/a | `cdb/#` |
| `--handlers-dir` | `CDB_HANDLERS_DIR` | none |
| `--config` | `CDB_CONFIG` | none |
| `--api-url` | `CDB_API_URL` | `global.api_url`, then `global.cdb_url` |
| `--api-key` | `CDB_API_KEY` | none |
| `--lock-file` | `CDB_LOCK_FILE` | `/tmp/cdb-mqtt.lock` |
| `--log-level` | `LOG_LEVEL` | `INFO` |
| `--env-file` | n/a | none (loads a `.env` file) |

Only one instance runs per lock file. Give each additional instance its own `--lock-file` and `--client-id`.

Other commands:
- `cdb-mqtt list-handlers --handlers-dir DIR`
- `cdb-mqtt --version`

## YAML config

See [`config.example.yaml`](../config.example.yaml):

```yaml
global:
  cdb_url: https://cdb.example.com/cdb   # public portal URL for links
  api_url: http://localhost:8080/cdb     # optional internal API URL
handlers:
  MachineDesignAuditHandler:
    audit_file: /var/log/cdb-mqtt/machine-design-audit.jsonl
```

## systemd

[`examples/systemd/`](../examples/systemd) contains a user unit that reads every option from an environment file:

```sh
mkdir -p ~/.config/cdb-mqtt ~/.config/systemd/user
cp examples/systemd/cdb-mqtt.env ~/.config/cdb-mqtt/       # edit paths/credentials
cp examples/systemd/cdb-mqtt.service ~/.config/systemd/user/
systemctl --user daemon-reload && systemctl --user enable --now cdb-mqtt
journalctl --user -u cdb-mqtt -f
```
