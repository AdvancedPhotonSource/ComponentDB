# ComponentDB python client

A [uv workspace](https://docs.astral.sh/uv/concepts/projects/workspaces/) with two publishable
packages:

- `packages/api/` — **componentdb-api**: the generated `cdbApi` REST client plus the
  hand-written `CdbApiFactory` convenience wrapper.
- `packages/cli/` — **componentdb-cli**: the `cdbCli` command line tools (`cdb-cli`,
  `cdbSearch`, `cdbInfo`, `cdbHelp`), depending on `componentdb-api`.

`cdbApi/` is **generated** by `generatePyClient.sh` from the portal's OpenAPI spec and is
gitignored — regenerate it before building, testing, or running anything that imports it.

## Dev setup

```sh
source setup.sh                       # from the repo root; puts packages/{api,cli} on PYTHONPATH
cd tools/developer_tools/python-client
./generatePyClient.sh http://localhost:8080/cdb   # requires the portal running locally
```

At this point `import cdbApi`, `import CdbApiFactory`, and `import cdbCli` resolve directly
against this checkout via `PYTHONPATH` — no install required, matching the rest of the CDB dev
workflow (`sbin/cdb_test.sh` relies on this).

If you'd rather use uv directly (editable installs into a real virtualenv):

```sh
uv sync                               # installs both packages editable + dev deps (pytest)
uv run pytest test/
uv run cdb-cli --help
```

## Regenerating the client

```sh
./generatePyClient.sh <CDB_BASE_PATH>     # e.g. http://localhost:8080/cdb
```

Downloads `openapi-generator-cli` (once — cached for subsequent runs), generates a client
from `<CDB_BASE_PATH>/api/openapi.yaml`, and overwrites `packages/api/cdbApi/`. Run this
any time REST routes or `openapi.yaml` change.

The generator jar is cached in the first of: `$OPENAPI_GENERATOR_CACHE_DIR` (explicit
override), `$CDB_SUPPORT_DIR/src` (the repo's usual download cache, if `make support` has
been run), or a repo-local `support_bin/` here.

## Building & publishing to PyPI

```sh
uv build --all-packages --out-dir dist    # sdist + wheel for both packages, normalized names
uv publish dist/*                         # needs UV_PUBLISH_TOKEN or ~/.pypirc
```

Or use the wrapper script, which also regenerates `cdbApi` first and prompts before uploading:

```sh
./sbin/cdb_release_cdb_api_pip.py                                  # from repo root
./sbin/cdb_release_cdb_api_pip.py --dry-run                        # build only
./sbin/cdb_release_cdb_api_pip.py --publish-url https://test.pypi.org/legacy/  # TestPyPI
```

or `make release-python-client` from the repo root.

`make prepare-release` (`sbin/cdb_prepare_release.py`) bumps the version in both
`packages/*/pyproject.toml` (and everywhere else version strings live) and refreshes `uv.lock`.

## Building conda packages

```sh
./conda-build.sh http://localhost:8080/cdb
```

Regenerates the client, builds `conda-recipe/API` then `conda-recipe/CLI` (against the just-built
API package via the local `./build` channel), smoke-tests both in a throwaway env, and prints a
reminder to upload the resulting `cdb-api-cli-env.txt` with the c2 tool.

## Tests

```sh
uv run pytest test/                     # requires the portal running on localhost:8080
```

`sbin/cdb_test.sh` (invoked by `make test`) regenerates the client and runs this as part of the
full suite.

### MQTT integration tests

`test/mqtt_test.py` subscribes to `cdb/#` on a real broker, performs API mutations, and checks
the MQTT events the portal publishes. It needs `paho-mqtt` (v2) in the active environment. It is
not a declared dependency, so install it yourself (for example `uv pip install paho-mqtt` or
`pip install paho-mqtt`).

Prerequisites (developer-owned; nothing here provisions or reconfigures them):

- an MQTT broker reachable from both Payara and your shell;
- the Payara connector resource `cdb/MQTT/resource` pointing at that broker
  (`sbin/cdb_create_mqtt_configuration.sh`, then `sbin/cdb_configure_mqtt_service.sh`);
- the portal deployed with the MQTT changes and running on the test database (`make test-db`).

`make test` (`sbin/cdb_test.sh`) runs these with the API suite and runs the Selenium MQTT tests
serially after the parallel Selenium run. To run only the MQTT tests (API, then Selenium) from
the repo root:

```sh
source setup.sh            # optional if already sourced; the runner keeps an existing environment
sbin/cdb_mqtt_test.sh      # extra args are passed to pytest, e.g. -k location
```

Or run only the API module:

```sh
uv run pytest test/mqtt_test.py -rs
```

| Variable | Default | Purpose |
| --- | --- | --- |
| `CDB_TEST_MQTT` | `true` | `false` skips all MQTT tests |
| `CDB_TEST_MQTT_HOST` | `localhost` | broker host for the test subscriber |
| `CDB_TEST_MQTT_PORT` | `1883` | broker port |
| `CDB_TEST_MQTT_USERNAME` / `CDB_TEST_MQTT_PASSWORD` | unset | optional broker credentials |
| `CDB_TEST_MQTT_TIMEOUT` | `10` | seconds to wait for connect, subscribe and each event |
| `CDB_TEST_MQTT_SKIP_SELENIUM` | unset | `true` makes `sbin/cdb_mqtt_test.sh` run only the API module |

The subscriber does not read the connector's `$CDB_INSTALL_DIR/etc/<db>.mqtt.conf`. If the broker
requires authentication, export the same values, or another account allowed to subscribe to
`cdb/#`, in `CDB_TEST_MQTT_USERNAME`/`CDB_TEST_MQTT_PASSWORD`.

The tests change standard test-database records (inventory item 56, machines 93 and 112) and
restore them in `finally`. Placeholder machines, locations and properties they create are
uniquely named and removed afterwards. The broker round-trip check publishes one non-retained
message on `cdb/test-probe/<uuid>`.

The tests are skipped, with the reason shown by `-rs`, if `paho-mqtt` is missing or the subscriber
cannot connect. That keeps `make test` working on hosts without a broker. Once the subscriber is
connected, a missing event fails the test. The failure message lists the unmatched messages it
did capture, plus likely connector causes. The runner never builds, deploys, restarts services,
or replaces the database.
