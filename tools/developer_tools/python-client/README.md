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
uv run pytest test/api_test.py
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
uv run pytest test/api_test.py          # requires the portal running on localhost:8080
```

`sbin/cdb_test.sh` (invoked by `make test`) regenerates the client and runs this as part of the
full suite.
