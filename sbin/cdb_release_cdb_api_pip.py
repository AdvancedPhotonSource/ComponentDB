#!/usr/bin/env python

"""
Copyright (c) UChicago Argonne, LLC. All rights reserved.
See LICENSE file.
"""

# Builds and publishes the ComponentDB python client (componentdb-api, componentdb-cli)
# to PyPI using uv.
#
# DEV NOTE: To publish a release
#   source setup.sh
#   ./sbin/cdb_release_cdb_api_pip.py
# or, via make:
#   make release-python-client
#
# Regenerating cdbApi requires a running portal (default http://localhost:8080/cdb).
# Publishing requires a PyPI API token, e.g. via UV_PUBLISH_TOKEN or ~/.pypirc.

import argparse
import glob
import os
import shutil
import subprocess

DIST_ROOT_DIRECTORY_ENV_KEY = "CDB_ROOT_DIR"
PYTHON_CLIENT_DIR = "tools/developer_tools/python-client"
DEFAULT_PORTAL_URL = "http://localhost:8080/cdb"

rootDir = os.getenv(DIST_ROOT_DIRECTORY_ENV_KEY)
if rootDir is None:
    raise EnvironmentError("Please run setup.sh from the root directory of the cdb distribution.")

clientDir = os.path.join(rootDir, PYTHON_CLIENT_DIR)


def run(args):
    print("+ %s" % " ".join(args))
    subprocess.run(args, check=True, cwd=clientDir)


def regenerate_client(portal_url):
    run(["./generatePyClient.sh", portal_url])
    generated = os.path.join(clientDir, "packages", "api", "cdbApi", "__init__.py")
    if not os.path.exists(generated):
        raise RuntimeError("generatePyClient.sh did not produce %s" % generated)


def build(dist_dir):
    if os.path.isdir(dist_dir):
        shutil.rmtree(dist_dir)
    run(["uv", "lock"])
    run(["uv", "build", "--all-packages", "--out-dir", dist_dir])
    artifacts = sorted(glob.glob(os.path.join(dist_dir, "*")))
    if not artifacts:
        raise RuntimeError("uv build produced no artifacts in %s" % dist_dir)
    return artifacts


def publish(artifacts, publish_url):
    args = ["uv", "publish"]
    if publish_url:
        args += ["--publish-url", publish_url]
    args += artifacts
    run(args)


def main():
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument(
        "--portal-url",
        default=DEFAULT_PORTAL_URL,
        help="running CDB portal used to regenerate cdbApi (default: %(default)s)",
    )
    parser.add_argument(
        "--skip-generate",
        action="store_true",
        help="skip regenerating cdbApi; use whatever is already in packages/api/cdbApi",
    )
    parser.add_argument(
        "--publish-url",
        default=None,
        help="alternate index, e.g. https://test.pypi.org/legacy/ for a dry run upload",
    )
    parser.add_argument("--dry-run", action="store_true", help="build only; do not upload")
    args = parser.parse_args()

    if not args.skip_generate:
        regenerate_client(args.portal_url)

    dist_dir = os.path.join(clientDir, "dist")
    artifacts = build(dist_dir)

    print("\nBuilt %d artifact(s):" % len(artifacts))
    for artifact in artifacts:
        print("  %s" % os.path.relpath(artifact, clientDir))

    if args.dry_run:
        print("\n--dry-run given, not publishing.")
        return

    response = input("\nPublish these to %s? [y/N]: " % (args.publish_url or "PyPI")).strip().lower()
    if response not in ("y", "yes"):
        print("Aborted, nothing published.")
        return

    publish(artifacts, args.publish_url)


if __name__ == "__main__":
    main()
