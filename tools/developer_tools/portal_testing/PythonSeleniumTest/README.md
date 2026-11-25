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

`make test` runs the suite in parallel with `-m "not mqtt"`, then runs the MQTT tests below
serially (`pytest tests/ -m mqtt -p no:xdist`). Set `CDB_TEST_MQTT=false` to skip them.

## MQTT tests

These tests are marked `mqtt` and build on `tests/mqtt_base.py`. Each one fires portal events
through the UI with the fewest steps needed, then checks the broker receives them:

| Test | UI action | Topics |
| --- | --- | --- |
| `test_mqtt_catalog.py::test_catalog_item_and_property_lifecycle` | create catalog item, add/edit/delete a property, delete the item | `cdb/entity/add`, `cdb/entity/update`, `cdb/item/property/add`, `/update`, `/delete`, `cdb/entity/delete` |
| `test_mqtt_catalog.py::test_catalog_item_log_lifecycle` | create catalog item, add a log, attach `AnlLogo.png`, edit and delete the log, delete the item | `cdb/item/log/add`, `cdb/item/log/attachment/add`, `cdb/item/log/update`, `/delete` |
| `test_mqtt_machine_design.py::test_machine_add_parent_and_child` | create machine + child placeholder | `cdb/machine-design/add` (top level has `parent: null`; no template fields) |
| `test_mqtt_machine_design.py::test_machine_add_from_template` | create machine, add child From Template `Template_Test {nn}` | `cdb/machine-design/add` with `sourceTemplate`, `templateAppliedToMachine`, `templateCreatedMachineCount` |
| `test_mqtt_machine_design.py::test_machine_trash_and_restore` | create machine + child, Move to Trash, Put back | `cdb/entity/add`, `cdb/machine-design/trash`, `/restore` |
| `test_mqtt_machine_design.py::test_machine_assign_link_and_move` | assign catalog `Test Item Assembly` to a machine, link its child to an assembly part, drag another machine onto it | `cdb/machine-design/assigned-item/update`, `/represented-element/update`, `/move` (and no `/add` for the move) |
| `test_mqtt_inventory.py::test_inventory_location_details` | edit location details in place on inventory item 56 | `cdb/item/location/update` |
| `test_mqtt_inventory.py::test_inventory_assembly_element` | change element `E1` of inventory item 56 from item 45 to 41 and back | `cdb/inventory/assembly-element/update` |

Together these cover all 18 topics. Trash and restore have no REST route, so they are covered
only here. The other 16 are also covered through the API in
`tools/developer_tools/python-client/test/mqtt_test.py`. Payloads of the newer topics are
described in `tools/developer_tools/python-client/README.md` ("MQTT event payloads"). Template instantiation has no REST
route, so the template fields of `cdb/machine-design/add` are covered only here.

Everything the tests create is uniquely named (`MQTT ... <id>`). Machine hierarchies are left in
trash and catalog items are deleted. Inventory item 56's location details and element `E1` are
restored in `finally`. If cleanup fails, the test prints what to remove by hand.

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
