# CDB Portal Selenium Tests

## Requirements

- Python 3.10+
- Chrome or Chromium
- `selenium`
- `pytest`
- `pytest-xdist`
- `pytest-rerunfailures`
- A running portal using the test database

Install the Python dependencies in the Selenium Conda environment:

```sh
python -m pip install selenium pytest pytest-xdist pytest-rerunfailures
```

## Run the full test suite

From the repository root:

```sh
source setup.sh
make test
```

The full suite runs in parallel with `-m "not mqtt"`, so the MQTT test below is excluded.

## MQTT trash/restore test

`tests/test_mqtt_machine_design.py` (marker `mqtt`) creates a uniquely named top-level machine
with one child, named `MQTT trash parent/child <id>`. It moves the machine to trash through the
context menu, then restores it from the deleted-items list. It checks that the broker receives
`cdb/machine-design/trash` and `cdb/machine-design/restore` for both machines, with the hierarchy
root, an affected count of 2, and the `cdb` user. At the end it moves the hierarchy back to
trash, so nothing disposable is left active. If that cleanup fails, it prints the machine name
so you can remove it by hand.

It changes shared state, so run it serially and never under `-n`. Additional requirements:

- `paho-mqtt` (v2) in the Selenium environment: `python -m pip install paho-mqtt`;
- the shared helper `tools/developer_tools/python-client/test/mqtt_test_support.py`, which is
  found automatically by relative path;
- the broker, Payara connector (`cdb/MQTT/resource`) and portal prerequisites, plus the
  `CDB_TEST_MQTT*` variables, described in `tools/developer_tools/python-client/README.md`.

The test is skipped if `CDB_TEST_MQTT=false`, if paho is missing, or if the broker is unreachable.

```sh
sbin/cdb_mqtt_test.sh                       # API MQTT tests, then this test (from repo root)

cd tools/developer_tools/portal_testing/PythonSeleniumTest
pytest tests/ -m mqtt -p no:xdist -rs       # this test only, headless
CDB_TEST_HEADLESS=false pytest tests/test_mqtt_machine_design.py -p no:xdist -rs  # watch it
```
