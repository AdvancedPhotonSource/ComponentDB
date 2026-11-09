"""
Copyright (c) UChicago Argonne, LLC. All rights reserved.
See LICENSE file.

MQTT integration tests: perform API mutations against the deployed portal and
verify the events it publishes through Payara's cdb/MQTT/resource connector.

Skipped when CDB_TEST_MQTT=false, when paho-mqtt is missing, or when the broker
cannot be reached. See README.md ("MQTT integration tests").
"""

import unittest

from test.cdb_test_base import CdbTestBase
from test.mqtt_test_support import start_collector_or_skip


class MqttIntegrationTest(CdbTestBase):

    @classmethod
    def setUpClass(cls):
        super().setUpClass()
        cls.mqtt = start_collector_or_skip()

    @classmethod
    def tearDownClass(cls):
        cls.mqtt.stop()
        super().tearDownClass()

    def setUp(self):
        super().setUp()
        self.mqtt.drain()

    def test_broker_round_trip(self):
        payload = self.mqtt.probe()
        self.assertIn("probe", payload)


if __name__ == "__main__":
    unittest.main()
