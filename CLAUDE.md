# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Build & deploy are the developer's job

**Do not build or deploy the application.** The developer builds and deploys the
Java web portal themselves from NetBeans (Ant project → WAR → Payara). Never run
`make deploy-web-portal`, `make deploy-web-portal-dev`, `make undeploy-web-portal`,
`ant`, or a Maven package/deploy of `CdbWebPortal`, and do not restart Payara.

When a change needs to be built or deployed to be exercised, make the source change
and then say so — the developer will do the NetBeans build/deploy and report back.
The `make` targets below are documented for reference and for the developer's own
use, not for you to invoke.

## Environment bootstrap

Every shell that builds, deploys, or runs CDB **must first source `setup.sh`** from the repo root. It sets `CDB_ROOT_DIR`, `CDB_INSTALL_DIR`, `CDB_DATA_DIR`, `CDB_VAR_DIR`, `CDB_SUPPORT_DIR`, `CDB_GLASSFISH_DIR`, `CDB_PYTHON_DIR`, `PYTHONPATH`, and prepends `bin/`, Ant, Java, Payara, NetBeans-bundled Maven, the support Python, and MariaDB to `PATH`. Most `make` targets and `sbin/` scripts assume those variables exist; running them without sourcing first will fail in non-obvious ways.

The layout is intentionally **out-of-tree**: `CDB_ROOT_DIR` is the source checkout, but the dependency tree lives at `$CDB_INSTALL_DIR/support-<hostname>/` (Payara, Java, Ant, NetBeans, MariaDB, Python) created by `make support`. The deployed app, data, logs, and config live under `$CDB_INSTALL_DIR/{etc,var,data,backup}`. Do not commit or read anything from `support-*/` — it is host-specific.

## Common commands

All `make` targets must be run from the repo root after `source setup.sh`.

```sh
# First-time / dev environment setup
make support                       # downloads & builds Payara, Java, Python, etc. into support-<hostname>/
make support-mysql                 # optional bundled MariaDB
make support-netbeans              # optional NetBeans IDE bundle
make dev-config                    # interactive: writes LDAP/email config & per-host build properties

# Database (production "cdb" vs dev "cdb_dev" — every db/deploy target has a -dev twin)
make clean-db                      # drop & recreate cdb with seed data from db/sql/clean
make test-db                       # recreate cdb with fixtures from db/sql/test
make db                            # recreate cdb without populating (schema only)
make backup                        # dumps cdb to $CDB_INSTALL_DIR/backup/cdb/<YYYYMMDD>/
make clean-db-dev                  # same, against cdb_dev

# Build & deploy the Java web portal (WAR -> Payara)
# NOTE: developer-run only — done from NetBeans, not by Claude. See section above.
make configure-web-portal          # one-time: writes glassfish-resources.xml, etc.
make deploy-web-portal             # builds dist/CdbWebPortal.war via Ant and deploys to Payara
make undeploy-web-portal
make deploy-web-portal-dev         # deploys to dev domain, uses cdb_dev

# Plugins
make deploy-cdb-plugin             # interactive picker; see tools/developer_tools/cdb_plugins/plugins/

# Full test suite (interactive — prompts for DB root password)
make test                          # runs ./sbin/cdb_test.sh: backs up cdb, swaps in test DB,
                                   # runs Maven unit tests + pytest API tests + Selenium GUI tests,
                                   # then restores the backup
```

### Test targets in isolation

```sh
# Java unit tests (Arquillian + embedded Glassfish)
cd tools/developer_tools/code_testing/CdbWebPortalTest && mvn test
mvn test -Dtest=ClassName#methodName    # single test

# Python API tests — require the portal to be running on localhost:8080
cd tools/developer_tools/python-client && ./generatePyClient.sh http://localhost:8080/cdb
cd tools/developer_tools/python-client/test && pytest api_test.py
pytest api_test.py::TestClass::test_method    # single test

# Selenium GUI tests
cd tools/developer_tools/portal_testing/PythonSeleniumTest && pytest gui_test.py
```

### Server / log management

```sh
./etc/init.d/cdb-glassfish restart
./etc/init.d/cdb-mysqld start            # only if using bundled MariaDB
tail -f $CDB_SUPPORT_DIR/payara/$CDB_HOST_ARCH/glassfish/domains/production/logs/server.log
```

## High-level architecture

ComponentDB has **three deployable pieces** plus a generated client. They communicate through the database and through the portal's REST API.

### 1. Java web portal — `src/java/CdbWebPortal/`

A JSF (PrimeFaces) + JAX-RS application packaged as a WAR and deployed to **Payara 5**. Built with **Ant via the NetBeans project** (`build.xml` → `nbproject/build-impl.xml`); the developer runs this build and the deploy from NetBeans. The `make` target invokes `cdb-ant` from the support dir. Maven `pom-wip-jdk11.xml` exists but is not the build of record.

Source root: `src/java/CdbWebPortal/src/java/gov/anl/aps/cdb/` with these top-level packages:

- `portal/` — the JSF webapp.
  - `model/db/entities/` — JPA entities (EclipseLink). Item hierarchy is the core domain model: `Item` is the abstract parent; `ItemDomain*` subclasses (`ItemDomainCatalog`, `ItemDomainInventory`, `ItemDomainLocation`, `ItemDomainMachineDesign`, `ItemDomainCableCatalog`, `ItemDomainCableDesign`, `ItemDomainCableInventory`, `ItemDomainApp`, `ItemDomainMAARC`, `ItemDomainAppDeployment`) implement domain-specific behavior. Catalog/Inventory base classes are shared.
  - `model/db/beans/` — `@Stateless` EJB facades wrapping JPA queries.
  - `controllers/` — `@SessionScoped`/`@ViewScoped` JSF managed beans. Naming mirrors the entity: e.g. `ItemDomainCatalogController` ↔ `ItemDomainCatalog`. `CdbDomainEntityController` is the common base. `controllers/extensions/` adds cross-cutting helpers, `controllers/settings/` persists user UI prefs.
  - `import_export/` — wizard-driven spreadsheet (XLSX) import/export framework; `import_/objects/specs/` defines per-domain column specs and `handlers/` define value coercion.
  - `view/` — JSF beans and value objects backing views.
  - `plugins/` — `CdbPluginManager` discovers plugin JARs deployed under `support/`; see `tools/developer_tools/cdb_plugins/` for the plugin install tooling.
- `rest/` — JAX-RS endpoints exposed at `/cdb/api`. One `*Route` class per resource (`ItemRoute`, `MachineDesignItemRoute`, `CableDesignItemRoute`, `UsersRoute`, `AuthenticationRoute`, etc.) under `rest/routes/`. `rest/entities/` are the request/response DTOs; `rest/authentication/` issues JWTs; `rest/provider/` registers JAX-RS providers. The OpenAPI surface is declared in `src/java/openapi.yaml` and drives Python client generation — keep it in sync when adding/changing routes.
- `common/` — shared exceptions, constants, value objects used by both `portal/` and `rest/`.
- `api/`, `connectors/` — internal API surfaces and external system connectors.

**JSF views, templates, and static assets** live in `web/` (`web/views/`, `web/templates/`, `web/resources/`, `web/WEB-INF/`). `web/WEB-INF/glassfish-web.xml` and `src/java/cdb.portal.properties` are generated from `.template` files by `make dev-config` / `make configure-web-portal` — never edit the generated files; edit the templates.

### 2. Python CDB web service — `src/python/cdb/`

A CherryPy service that runs on port 10232 (`./sbin/cdbWebService.sh`), handling email notifications, async tasks, and a legacy SOAP/REST surface. Marked **deprecated** but still part of the distribution for legacy integrations. Layout follows the `common/` + `cdb_web_service/` split with `api/`, `cli/`, `impl/`, `service/`, `tasks/`, `plugins/`, `java_api/` (Python wrappers around the Java REST API). New features should go in the Java REST layer instead.

### 3. Python client + CLI — `tools/developer_tools/python-client/`

- `cdbApi/` is **generated** from `openapi.yaml` by `./generatePyClient.sh <portal-url>` (uses openapi-generator). Do not hand-edit it — regenerate after changing the Java REST routes. `setup-api.py` packages it as `ComponentDB_API` for PyPI.
- `CdbApiFactory.py` is the hand-written entry point that hides authentication & client wiring.
- `cdbCli/` is the user-facing CLI (`cdbSearch`, `cdbInfo`, `cdb-cli`); see `docs/CLI.md` for usage and option reference. `cli-template/` holds skeletons for new commands.

### Database — `db/sql/`

MariaDB 10.5. Authoritative schema is the set of `create_cdb_tables.sql`, `create_stored_procedures.sql`, `create_triggers.sql`, `create_views.sql` in `db/sql/`, applied by `sbin/cdb_create_db.sh`. Initial reference data lives in `db/sql/clean/populate_*.sql` (used by `make clean-db`). Test fixtures in `db/sql/test/`.

**Releases bump the version in `etc/version` and add `db/sql/updates/updateTo<NEW_VERSION>.sql`** containing the idempotent (INSERT IGNORE / ALTER ... IF NOT EXISTS) migrations needed to bring an existing prod DB to the new schema. Header comments in each update file document how to run it. The existing version chain (e.g. `updateTo3.15.5.sql` → `updateTo3.17.0.sql`) is the template; do not retroactively edit older update scripts.

**Adding a setting:** `setting_type` ids are organized into per-type blocks with numeric gaps between blocks (e.g. `15xxx` Search, `16xxx` Source, `22xxx` MAARC) so each type has room for new entries. A new setting takes the next free id at the end of its type block — never reuse an id or insert one mid-block (the gap before the next block is the room reserved for this). Add the identical row to all seed copies (`db/sql/{clean,test,static}/populate_setting_type.sql`) and to the current release's `db/sql/updates/updateTo<VERSION>.sql`.

## Repo-specific conventions

- **Generated files that look like sources** are common: `cdb.portal.properties`, `web/WEB-INF/glassfish-web.xml`, `web/WEB-INF/web.xml`, `setup/glassfish-resources.xml`, `nbproject/private/private.properties`, and everything under `tools/developer_tools/python-client/cdbApi/`. They are produced from `*.template` files (or via openapi-generator) by `dev-config` / `configure-web-portal` / `dist` targets and are in `.gitignore`. Edit the template, regenerate, then commit only the template.
- **`cdb` vs `cdb_dev`**: every db, deploy, undeploy, and backup target has a `-dev` variant that targets a separate database name and Payara domain, so a developer can run both side-by-side. When adding new admin scripts, follow the same pattern (first arg is DB name, defaulting to `cdb`).
- **Single canonical version string**: update `etc/version`, `src/java/CdbWebPortal/src/java/openapi.yaml` (`info.version`), `tools/developer_tools/python-client/setup-api.py`, and `tools/developer_tools/python-client/setup-cli.py` together. Past release commits (`git log --grep "Prepare relase"`) show the full set of files that need bumping.
- **Plugins** under `tools/developer_tools/cdb_plugins/plugins/<name>/` extend both the Java portal (deployed via `make deploy-cdb-plugin` → `update_plugin_generated_files.py`) and the Python web service. `pluginTemplates/` is the scaffold.
- **REST changes touch three places**: the Java route, `openapi.yaml`, and (after `generatePyClient.sh`) the regenerated `cdbApi/` package. The Python API tests in `tools/developer_tools/python-client/test/api_test.py` are the integration check.

## Reference docs

- `README.md` — full deployment and upgrade walk-through (commands here are abbreviated).
- `docs/CLI.md` — `cdbSearch` / `cdbInfo` / `cdb-cli` reference.
- `docs/release-notes/<version>.md` — per-release notes; mirror this format for new releases.
- `docs/db/CdbSchema-v3.0-3.pdf` — ER diagram (somewhat dated but useful for the Item hierarchy).
- External developer guide: <https://confluence.aps.anl.gov/display/APSUCMS/Developer+Guide> (referenced from README).
