"""
MQTT client for connecting to an MQTT broker and dispatching to handlers.
"""

import asyncio
import json
import logging
from typing import Any, Optional

import paho.mqtt.client as mqtt
from paho.mqtt.client import ReasonCodes
from paho.mqtt.client import Properties

from cdb_bely_message_broker_base.models import MQTTMessage
from cdb_bely_message_broker_base.plugin import PluginManager

logger = logging.getLogger(__name__)


class MQTTClient:
    """
    MQTT client for application events.

    Connects to an MQTT broker and routes messages to registered handlers.
    """

    def __init__(
        self,
        broker_host: str,
        broker_port: int = 1883,
        client_id: str = "mqtt-message-broker",
        username: Optional[str] = None,
        password: Optional[str] = None,
        plugin_manager: Optional[PluginManager] = None,
    ):
        """
        Initialize the MQTT client.

        Args:
            broker_host: MQTT broker hostname or IP.
            broker_port: MQTT broker port (default: 1883).
            client_id: MQTT client ID.
            username: Optional MQTT username.
            password: Optional MQTT password.
            plugin_manager: PluginManager instance for handling messages.
        """
        self.broker_host = broker_host
        self.broker_port = broker_port
        self.client_id = client_id
        self.username = username
        self.password = password
        self.plugin_manager = plugin_manager or PluginManager()
        self.logger = logging.getLogger(__name__)

        self.client = mqtt.Client(mqtt.CallbackAPIVersion.VERSION2, client_id)  # type: ignore[attr-defined, arg-type]
        self.client.on_connect = self._on_connect
        self.client.on_message = self._on_message
        self.client.on_disconnect = self._on_disconnect  # type: ignore[assignment]
        self.client.on_subscribe = self._on_subscribe

        if username and password:
            self.client.username_pw_set(username, password)

        self._subscribed_topics: set[str] = set()
        self._running = False
        self._loop: Optional[asyncio.AbstractEventLoop] = None

    def subscribe(self, topic: str) -> None:
        """
        Subscribe to an MQTT topic.

        Args:
            topic: The topic pattern to subscribe to.
        """
        self._subscribed_topics.add(topic)
        if self.client.is_connected():
            self.client.subscribe(topic)
            self.logger.info(f"Subscribed to topic: {topic}")

    # CallbackOnConnect_v2 = Callable[["Client", Any, ConnectFlags, ReasonCode, Union[Properties, None]], None]
    # Callable[[Client, Any, Optional[ReasonCodes], Optional[Properties]], object], None]")
    def _on_connect(
        self,
        client: mqtt.Client,
        userdata: Any,
        connect_flags: dict[str, int],
        reason_code: ReasonCodes,
        properties: Optional[Properties],
    ) -> None:
        """Handle MQTT connection."""
        if reason_code == 0:
            self.logger.info(f"Connected to MQTT broker at {self.broker_host}:{self.broker_port}")
            # Resubscribe to all topics
            for topic in self._subscribed_topics:
                client.subscribe(topic)
                self.logger.info(f"Subscribed to topic: {topic}")
        else:
            self.logger.error(f"Failed to connect to MQTT broker: {reason_code}")

    def _on_message(
        self,
        client: mqtt.Client,
        userdata: object,
        msg: mqtt.MQTTMessage,
    ) -> None:
        """Handle incoming MQTT message."""
        try:
            payload_str = msg.payload.decode("utf-8")
            payload_dict = json.loads(payload_str)

            message = MQTTMessage(
                topic=msg.topic,
                payload=payload_dict,
                raw_payload=payload_str,
            )

            # Schedule the async handler on the event loop
            if self._loop and self._loop.is_running():
                asyncio.run_coroutine_threadsafe(
                    self.plugin_manager.handle_message(message),
                    self._loop,
                )
            else:
                self.logger.warning("Event loop not running, cannot process message")

        except json.JSONDecodeError as e:
            self.logger.error(f"Failed to parse JSON payload from {msg.topic}: {e}")
        except Exception as e:
            self.logger.error(f"Error processing message from {msg.topic}: {e}")

    def _on_disconnect(
        self,
        client: mqtt.Client,
        userdata: Any,
        disconnect_flags: dict[str, int],
        reason_code: Optional[ReasonCodes],
        properties: Optional[Properties],
    ) -> None:
        """Handle MQTT disconnection."""
        if reason_code == 0:
            self.logger.info("Disconnected from MQTT broker")
        else:
            self.logger.warning(f"Unexpected disconnection from MQTT broker: {reason_code}")

    def _on_subscribe(
        self,
        client: mqtt.Client,
        userdata: object,
        mid: int,
        reason_code_list: list[mqtt.ReasonCodes],
        properties: mqtt.Properties,
    ) -> None:
        """Handle subscription confirmation."""
        for reason_code in reason_code_list:
            if reason_code == 0:
                self.logger.debug("Subscription successful")
            else:
                self.logger.warning(f"Subscription failed: {reason_code}")

    def connect(self) -> None:
        """Connect to the MQTT broker."""
        try:
            self.client.connect(self.broker_host, self.broker_port, keepalive=60)
            self.logger.info(f"Connecting to MQTT broker at {self.broker_host}:{self.broker_port}")
        except Exception as e:
            self.logger.error(f"Failed to connect to MQTT broker: {e}")
            raise

    def disconnect(self) -> None:
        """Disconnect from the MQTT broker."""
        self.client.disconnect()

    async def _run_mqtt_loop(self) -> None:
        """Run the MQTT loop in a separate thread."""
        loop = asyncio.get_event_loop()
        await loop.run_in_executor(None, self.client.loop_forever)

    def start(self) -> None:
        """Start the MQTT client (blocking)."""
        self._running = True
        # Create and set the event loop for async handler execution
        try:
            self._loop = asyncio.new_event_loop()
            asyncio.set_event_loop(self._loop)
        except RuntimeError:
            # Event loop already exists, use the current one
            self._loop = asyncio.get_event_loop()

        try:
            self.connect()
            # Run MQTT loop in executor to avoid blocking the event loop
            self._loop.run_until_complete(self._run_mqtt_loop())
        except KeyboardInterrupt:
            self.logger.info("Interrupted by user")
        finally:
            self.disconnect()
            self._running = False
            if self._loop and not self._loop.is_closed():
                self._loop.close()

    async def start_async(self) -> None:
        """Start the MQTT client (async)."""
        self._running = True
        self._loop = asyncio.get_event_loop()

        try:
            self.connect()
            # Run the MQTT loop in a separate thread
            await self._run_mqtt_loop()
        except KeyboardInterrupt:
            self.logger.info("Interrupted by user")
        finally:
            self.disconnect()
            self._running = False

    def is_connected(self) -> bool:
        """Check if the client is connected to the broker."""
        return self.client.is_connected()
