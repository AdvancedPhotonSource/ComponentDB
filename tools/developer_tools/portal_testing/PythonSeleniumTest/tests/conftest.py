"""
Copyright (c) UChicago Argonne, LLC. All rights reserved.
See LICENSE file.
"""


def pytest_configure(config):
    config.addinivalue_line(
        "markers",
        "mqtt: stateful MQTT verification; run serially via sbin/cdb_mqtt_test.sh, "
        "deselect from parallel runs with -m \"not mqtt\"")
