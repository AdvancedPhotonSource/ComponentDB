#!/usr/bin/env python

"""
Copyright (c) UChicago Argonne, LLC. All rights reserved.
See LICENSE file.

Shared base for Selenium MQTT tests. Subclasses must be marked ``@pytest.mark.mqtt``
so the parallel suite deselects them; run serially via sbin/cdb_mqtt_test.sh.
"""

import os
import sys
import unittest

from tests.base import CdbPortalTestCase

# Shared subscriber helper lives with the API tests.
SUPPORT_DIR = os.path.abspath(os.path.join(
    os.path.dirname(__file__), '..', '..', '..', 'python-client', 'test'))
if os.path.isdir(SUPPORT_DIR) and SUPPORT_DIR not in sys.path:
    sys.path.append(SUPPORT_DIR)

try:
    import mqtt_test_support
except ImportError:
    mqtt_test_support = None

ADMIN_USERNAME = 'cdb'
UI_EVENT_TIMEOUT = 30


def described_as(path, name):
    """Match snapshots by name; item descriptions look like '<name> [Item Id: N]'."""
    prefix = '%s [Item Id:' % name
    return lambda payload: str(
        mqtt_test_support.get_path(payload, path) or '').startswith(prefix)


class MqttPortalTestCase(CdbPortalTestCase):

    @classmethod
    def setUpClass(cls):
        super().setUpClass()
        if mqtt_test_support is None:
            raise unittest.SkipTest(
                'mqtt_test_support not importable; expected it in %s' % SUPPORT_DIR)
        # Connect before the browser starts so an unavailable broker skips cheaply.
        cls.mqtt = mqtt_test_support.start_collector_or_skip()

    @classmethod
    def tearDownClass(cls):
        cls.mqtt.stop()
        super().tearDownClass()

    def wait_for_ui_event(self, topic, predicate, description):
        return self.mqtt.wait_for(
            topic, predicate,
            timeout=max(UI_EVENT_TIMEOUT, self.mqtt.settings.timeout),
            description=description)

    def assert_actor(self, event):
        self.assertEqual(event.get('triggeredByUsername'), ADMIN_USERNAME)
        self.assertIn('eventTimestamp', event)
        self.assertNotIn('topic', event)
