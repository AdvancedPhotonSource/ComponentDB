"""
Copyright (c) UChicago Argonne, LLC. All rights reserved.
See LICENSE file.

Shared MQTT subscriber helpers for the API and Selenium integration tests.

Kept free of other ``test.*`` imports so it can be imported either as
``test.mqtt_test_support`` (API suite) or ``mqtt_test_support`` (Selenium suite,
with ``python-client/test`` on PYTHONPATH).
"""

import json
import os
import threading
import time
import unittest
import uuid

TOPIC_FILTER = "cdb/#"

TOPIC_ENTITY_ADD = "cdb/entity/add"
TOPIC_ENTITY_UPDATE = "cdb/entity/update"
TOPIC_ENTITY_DELETE = "cdb/entity/delete"
TOPIC_PROPERTY_ADD = "cdb/item/property/add"
TOPIC_PROPERTY_UPDATE = "cdb/item/property/update"
TOPIC_PROPERTY_DELETE = "cdb/item/property/delete"
TOPIC_MD_ADD = "cdb/machine-design/add"
TOPIC_MD_ASSIGNED_ITEM = "cdb/machine-design/assigned-item/update"
TOPIC_MD_MOVE = "cdb/machine-design/move"
TOPIC_MD_REPRESENTED_ELEMENT = "cdb/machine-design/represented-element/update"
TOPIC_MD_TRASH = "cdb/machine-design/trash"
TOPIC_MD_RESTORE = "cdb/machine-design/restore"
TOPIC_ITEM_LOCATION = "cdb/item/location/update"
TOPIC_INVENTORY_ASSEMBLY_ELEMENT = "cdb/inventory/assembly-element/update"

PUBLISH_FAILURE_HINT = (
    "The subscriber is connected, but the portal did not publish the expected event. "
    "Likely causes: the Payara connector resource 'cdb/MQTT/resource' is not configured "
    "(see sbin/cdb_create_mqtt_configuration.sh and sbin/cdb_configure_mqtt_service.sh); "
    "the connector points at a different broker than CDB_TEST_MQTT_HOST/CDB_TEST_MQTT_PORT; "
    "or the portal was not redeployed with the MQTT changes. Check the Payara server.log "
    "for 'MQTT is unavailable' or 'MQTT broker is unavailable' warnings."
)

_TRUE_VALUES = ("true", "1", "t", "y", "yes", "on")


def _env_flag(name, default):
    value = os.getenv(name)
    if value is None or value.strip() == "":
        return default
    return value.strip().lower() in _TRUE_VALUES


class MqttTestSettings:
    """Subscriber settings read from CDB_TEST_MQTT_* environment variables."""

    def __init__(self, enabled=True, host="localhost", port=1883,
                 username=None, password=None, timeout=10.0):
        self.enabled = enabled
        self.host = host
        self.port = port
        self.username = username
        self.password = password
        self.timeout = timeout

    @classmethod
    def from_env(cls):
        port_value = os.getenv("CDB_TEST_MQTT_PORT", "1883")
        timeout_value = os.getenv("CDB_TEST_MQTT_TIMEOUT", "10")
        try:
            port = int(port_value)
        except ValueError:
            raise ValueError("CDB_TEST_MQTT_PORT must be an integer, got %r" % port_value)
        try:
            timeout = float(timeout_value)
        except ValueError:
            raise ValueError("CDB_TEST_MQTT_TIMEOUT must be a number, got %r" % timeout_value)
        return cls(
            enabled=_env_flag("CDB_TEST_MQTT", True),
            host=os.getenv("CDB_TEST_MQTT_HOST") or "localhost",
            port=port,
            username=os.getenv("CDB_TEST_MQTT_USERNAME") or None,
            password=os.getenv("CDB_TEST_MQTT_PASSWORD") or None,
            timeout=timeout,
        )

    def describe(self):
        return "%s:%s" % (self.host, self.port)


class MqttUnavailable(Exception):
    """The broker could not be reached or the subscription was refused."""


class CapturedMessage:

    def __init__(self, topic, raw, received_at):
        self.topic = topic
        self.raw = raw
        self.received_at = received_at
        self.decode_error = None
        try:
            self.payload = json.loads(raw.decode("utf-8"))
        except (UnicodeDecodeError, ValueError) as ex:
            self.payload = None
            self.decode_error = str(ex)

    def summary(self):
        if self.payload is None:
            return "%s <undecodable: %s>" % (self.topic, self.decode_error)
        text = json.dumps(self.payload, sort_keys=True)
        if len(text) > 300:
            text = text[:300] + "..."
        return "%s %s" % (self.topic, text)


def get_path(payload, path):
    """Return a nested value for a dotted path such as 'machine.id', or None."""
    value = payload
    for part in path.split("."):
        if not isinstance(value, dict):
            return None
        value = value.get(part)
    return value


def same_id(value, expected):
    """Compare ids that the events may serialize as strings."""
    if value is None or expected is None:
        return value is None and expected is None
    return str(value) == str(expected)


def id_at(path, expected):
    """Predicate matching payloads whose id at ``path`` equals ``expected``."""
    return lambda payload: same_id(get_path(payload, path), expected)


def all_of(*predicates):
    return lambda payload: all(p(payload) for p in predicates)


class MqttEventCollector:
    """Background subscriber that buffers decoded messages for bounded waits."""

    def __init__(self, settings, topic_filter=TOPIC_FILTER):
        self.settings = settings
        self.topic_filter = topic_filter
        self._messages = []
        self._lock = threading.Condition()
        self._connected = threading.Event()
        self._subscribed = threading.Event()
        self._failure = None
        self._client = None

    # -- lifecycle ---------------------------------------------------------

    def start(self):
        try:
            import paho.mqtt.client as mqtt
        except ImportError as ex:
            raise MqttUnavailable("paho-mqtt is not installed (%s)" % ex)

        client_id = "cdb-mqtt-test-%s" % uuid.uuid4().hex[:8]
        callback_api = getattr(mqtt, "CallbackAPIVersion", None)
        if callback_api is not None:
            client = mqtt.Client(callback_api.VERSION2, client_id=client_id)
        else:
            client = mqtt.Client(client_id=client_id)
        if self.settings.username:
            client.username_pw_set(self.settings.username, self.settings.password)

        client.on_connect = self._on_connect
        client.on_subscribe = self._on_subscribe
        client.on_message = self._on_message
        self._client = client

        try:
            client.connect(self.settings.host, self.settings.port, keepalive=30)
        except (OSError, ValueError) as ex:
            raise MqttUnavailable("cannot connect to broker %s: %s"
                                  % (self.settings.describe(), ex))

        client.loop_start()
        if not self._connected.wait(self.settings.timeout):
            self.stop()
            raise MqttUnavailable("no CONNACK from broker %s within %ss (%s)"
                                  % (self.settings.describe(), self.settings.timeout,
                                     self._failure or "timeout"))
        if self._failure:
            failure = self._failure
            self.stop()
            raise MqttUnavailable(failure)

        client.subscribe(self.topic_filter, qos=1)
        if not self._subscribed.wait(self.settings.timeout):
            self.stop()
            raise MqttUnavailable("no SUBACK for %s within %ss"
                                  % (self.topic_filter, self.settings.timeout))
        if self._failure:
            failure = self._failure
            self.stop()
            raise MqttUnavailable(failure)
        return self

    def stop(self):
        client = self._client
        self._client = None
        if client is not None:
            try:
                client.disconnect()
            finally:
                client.loop_stop()

    def probe(self, timeout=None):
        """Round-trip a non-retained message through the broker on our own subscription."""
        topic = "cdb/test-probe/%s" % uuid.uuid4().hex
        marker = uuid.uuid4().hex
        self._client.publish(topic, json.dumps({"probe": marker}), qos=1)
        message = self._wait(topic, lambda payload: payload.get("probe") == marker, timeout)
        if message is None:
            raise AssertionError("Probe message on %s was not echoed back by broker %s"
                                 % (topic, self.settings.describe()))
        return message.payload

    # -- paho callbacks (compatible with callback API v1 and v2) -----------

    def _on_connect(self, client, userdata, flags, reason_code, properties=None):
        if _is_failure(reason_code):
            self._failure = "broker %s refused connection: %s" % (
                self.settings.describe(), reason_code)
        self._connected.set()

    def _on_subscribe(self, client, userdata, mid, reason_codes, properties=None):
        codes = reason_codes if isinstance(reason_codes, (list, tuple)) else [reason_codes]
        for code in codes:
            if _is_failure(code, subscribe=True):
                self._failure = "broker refused subscription to %s: %s" % (
                    self.topic_filter, code)
        self._subscribed.set()

    def _on_message(self, client, userdata, message):
        captured = CapturedMessage(message.topic, message.payload, time.monotonic())
        with self._lock:
            self._messages.append(captured)
            self._lock.notify_all()

    # -- queue access ------------------------------------------------------

    def drain(self):
        """Discard and return everything captured so far."""
        with self._lock:
            drained = self._messages
            self._messages = []
        return drained

    def messages(self):
        with self._lock:
            return list(self._messages)

    def wait_for(self, topic, predicate=None, timeout=None, description=None):
        """Remove and return the first payload on ``topic`` matching ``predicate``.

        Fails the calling test with diagnostics if nothing matches in time.
        """
        message = self._wait(topic, predicate, timeout)
        if message is None:
            raise AssertionError(self._timeout_message(topic, description, timeout))
        return message.payload

    def assert_no_message(self, topic, predicate=None, within=1.0, description=None):
        """Fail if a matching message arrives within ``within`` seconds."""
        message = self._wait(topic, predicate, within)
        if message is not None:
            raise AssertionError("Unexpected MQTT message%s: %s" % (
                " (%s)" % description if description else "", message.summary()))

    def _wait(self, topic, predicate, timeout):
        timeout = self.settings.timeout if timeout is None else timeout
        deadline = time.monotonic() + timeout
        with self._lock:
            while True:
                for index, message in enumerate(self._messages):
                    if message.topic != topic or message.payload is None:
                        continue
                    if predicate is None or predicate(message.payload):
                        del self._messages[index]
                        return message
                remaining = deadline - time.monotonic()
                if remaining <= 0:
                    return None
                self._lock.wait(remaining)

    def _timeout_message(self, topic, description, timeout):
        timeout = self.settings.timeout if timeout is None else timeout
        received = self.messages()
        lines = ["No MQTT message on %s%s within %ss from broker %s." % (
            topic, " matching %s" % description if description else "",
            timeout, self.settings.describe())]
        if received:
            lines.append("Captured but unmatched messages (%d):" % len(received))
            lines.extend("  " + m.summary() for m in received[-10:])
        else:
            lines.append("No messages were captured on %s." % self.topic_filter)
        if any(m.topic == topic for m in received):
            # The topic is flowing, so this is a payload/correlation mismatch, not wiring.
            lines.append("Messages arrived on %s but none matched; compare the payloads above "
                         "with the test's expectations." % topic)
        else:
            lines.append(PUBLISH_FAILURE_HINT)
        return "\n".join(lines)


def _is_failure(code, subscribe=False):
    # paho v2 passes ReasonCode objects; v1 passes ints (connect) or granted QoS (subscribe).
    is_failure = getattr(code, "is_failure", None)
    if is_failure is not None:
        return bool(is_failure)
    if subscribe:
        return code == 0x80
    return code != 0


def start_collector_or_skip(settings=None):
    """Start a collector, or raise unittest.SkipTest when MQTT is disabled/unreachable."""
    settings = settings or MqttTestSettings.from_env()
    if not settings.enabled:
        raise unittest.SkipTest("MQTT tests disabled (CDB_TEST_MQTT=false)")
    try:
        return MqttEventCollector(settings).start()
    except MqttUnavailable as ex:
        raise unittest.SkipTest("MQTT broker unavailable, skipping: %s" % ex)
