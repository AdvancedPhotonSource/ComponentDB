# Configuration

The BELY MQTT Framework can be configured through command-line options, environment variables, or configuration files.

## Command-Line Options

```bash
bely-mqtt start [OPTIONS]
```

### MQTT Options

- `--broker-host TEXT` - MQTT broker hostname (default: localhost)
- `--broker-port INTEGER` - MQTT broker port (default: 1883)
- `--username TEXT` - MQTT username for authentication
- `--password TEXT` - MQTT password for authentication
- `--topic TEXT` - MQTT topic pattern to subscribe (default: bely/#)

### Handler Options

- `--handlers-dir PATH` - Directory containing handler files (default: ./handlers)
- `--config PATH` - YAML configuration file for handlers

### API Options

- `--api-url TEXT` - Internal BELY API URL for server-to-server API calls.
  Takes precedence over `api_url` in the config file, which in turn falls back
  to `bely_url`. Set when the broker runs on the same host as BELY and internal
  access is preferred over going through the public URL.
- `--api-key TEXT` - BELY API authentication key

### Logging Options

- `--log-level TEXT` - Logging level: DEBUG, INFO, WARNING, ERROR (default: INFO)

## Environment Variables

All command-line options can be set via environment variables:

```bash
export MQTT_BROKER_HOST=broker.example.com
export MQTT_BROKER_PORT=1883
export MQTT_CLIENT_ID=bely-mqtt-client
export MQTT_USERNAME=myuser
export MQTT_PASSWORD=mypass
export BELY_API_URL=https://api.bely.dev
export BELY_API_KEY=your-api-key
export BELY_HANDLERS_DIR=./handlers
export BELY_CONFIG=./config.yaml
export LOG_LEVEL=DEBUG
```

## Configuration Files

### Environment File (.env)

Create a `.env` file in your project root for environment variables:

```bash
# MQTT Configuration
MQTT_BROKER_HOST=localhost
MQTT_BROKER_PORT=1883
MQTT_CLIENT_ID=bely-mqtt-client
MQTT_USERNAME=
MQTT_PASSWORD=

# BELY API Configuration
BELY_API_URL=https://api.bely.dev
BELY_API_KEY=your-api-key-here

# Logging
LOG_LEVEL=INFO

# Handler Configuration
BELY_HANDLERS_DIR=./handlers
BELY_CONFIG=./config.yaml
```

### Handler Configuration (YAML)

Handlers can be configured via a YAML file to provide both global and handler-specific settings:

```yaml
# Global configuration shared across all handlers
global:
  # Public-facing URL for notification links (log entry permalinks, unsubscribe URLs).
  # Must be reachable by end-users who receive notifications.
  bely_url: https://bely.example.com/bely
  # (Optional) Internal API URL for broker-to-BELY server API calls.
  # Defaults to bely_url. Set when broker runs on the same host as BELY.
  # api_url: http://localhost:8080/bely
  # Add any other global parameters here
  shared_param: value

# Handler-specific configurations
handlers:
  # Configure the AdvancedLoggingHandler
  AdvancedLoggingHandler:
    logging_dir: /var/log/bely
    log_level: DEBUG
    rotate_logs: true
    max_size_mb: 100
  
  # Configure the NotificationHandler
  NotificationHandler:
    webhook_url: https://hooks.slack.com/services/YOUR/WEBHOOK
    enabled: true
    timeout: 30
  
  # Configure the AppriseSmartNotificationHandler
  AppriseSmartNotificationHandler:
    config_path: /path/to/apprise_notification_config.yaml
```

Use with: `--config config.yaml`

## SSL/TLS Configuration

For secure MQTT connections:

```bash
bely-mqtt start \
    --mqtt-host broker.example.com \
    --mqtt-port 8883 \
    --mqtt-tls \
    --mqtt-ca-cert /path/to/ca.crt \
    --mqtt-client-cert /path/to/client.crt \
    --mqtt-client-key /path/to/client.key
```

## Logging Configuration

### Log Levels

- `DEBUG` - Detailed information for debugging
- `INFO` - General informational messages
- `WARNING` - Warning messages for potentially harmful situations
- `ERROR` - Error messages for serious problems

### Log Format

The default log format includes:
- Timestamp
- Log level
- Handler name
- Message

Example:
```
2024-01-01 12:00:00 INFO [LogHandler] New log entry added: ID=123
```

## Production Configuration

For production deployments:

1. Use environment variables for sensitive data
2. Enable appropriate log level (INFO or WARNING)
3. Configure log rotation
4. Use SSL/TLS for MQTT connections
5. Set up monitoring and alerting

Example production command:

```bash
bely-mqtt start \
    --handlers-dir /opt/bely-handlers \
    --log-level INFO \
    --log-file /var/log/bely-mqtt/app.log \
    --mqtt-tls \
    --mqtt-ca-cert /etc/ssl/mqtt/ca.crt
```