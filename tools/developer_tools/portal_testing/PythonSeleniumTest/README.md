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

## MQTT tests

These tests are marked `mqtt` and build on `tests/mqtt_base.py`. Each one fires portal events
through the UI with the fewest steps needed, then checks the broker receives them:

| Test | UI action | Topics |
| --- | --- | --- |
| `test_mqtt_machine_design.py::test_machine_trash_and_restore` | create machine + child, Move to Trash, Put back | `cdb/entity/add`, `cdb/machine-design/trash`, `cdb/machine-design/restore` |
| `test_mqtt_machine_design.py::test_machine_assign_catalog` | assign catalog `Test Item Assembly` to the child | `cdb/machine-design/assigned-item/update` |
| `test_mqtt_catalog.py::test_catalog_add_property_delete` | create catalog item, add property, delete item | `cdb/entity/add`, `cdb/item/property/add`, `cdb/entity/delete` |

Trash and restore have no REST route, so they are covered only here. The other topics are also
covered through the API in `tools/developer_tools/python-client/test/mqtt_test.py`.

Everything the tests create is uniquely named (`MQTT ... <id>`). Machine hierarchies are left in
trash and catalog items are deleted. If cleanup fails, the test prints what to remove by hand.

They change shared state, so run them serially and never under `-n`. Additional requirements:

- `paho-mqtt` (v2) in the Selenium environment: `python -m pip install paho-mqtt`;
- the shared helper `tools/developer_tools/python-client/test/mqtt_test_support.py`, which is
  found automatically by relative path;
- the broker, Payara connector (`cdb/MQTT/resource`) and portal prerequisites, plus the
  `CDB_TEST_MQTT*` variables, described in `tools/developer_tools/python-client/README.md`.

They are skipped if `CDB_TEST_MQTT=false`, if paho is missing, or if the broker is unreachable.

```sh
sbin/cdb_mqtt_test.sh                       # API MQTT tests, then these (from repo root)

cd tools/developer_tools/portal_testing/PythonSeleniumTest
pytest tests/ -m mqtt -p no:xdist -rs       # all Selenium MQTT tests, headless
CDB_TEST_HEADLESS=false pytest tests/test_mqtt_machine_design.py -p no:xdist -rs  # watch it
```
