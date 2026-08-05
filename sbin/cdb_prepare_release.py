#!/usr/bin/env python

"""
Copyright (c) UChicago Argonne, LLC. All rights reserved.
See LICENSE file.
"""

# Bumps the release version string across the repo in one pass. Locates
# occurrences by pattern (not a hardcoded file list) so it also repairs any
# files that drifted out of sync in a previous manual release.

import argparse
import glob
import os
import re
import sys

DIST_ROOT_DIRECTORY_ENV_KEY = "CDB_ROOT_DIR"
VERSION_FILE_PATH = "etc/version"
RELEASE_NOTES_DIR = "docs/release-notes"

rootDir = os.getenv(DIST_ROOT_DIRECTORY_ENV_KEY)
if rootDir is None:
    raise EnvironmentError("Please run setup.sh from the root directory of the cdb distribution.")

# Matches "3.17.0" as well as pre-release forms like "3.14.1.rc1".
VERSION_RE = r"\d+\.\d+\.\d+(?:\.[A-Za-z0-9]+)?"


def paths(pattern):
    """Resolve a repo-relative path or glob to a sorted list of repo-relative paths."""
    matches = glob.glob(os.path.join(rootDir, pattern), recursive=True)
    return sorted(os.path.relpath(p, rootDir) for p in matches)


# Each spec is a group of files sharing the same version marker pattern(s).
# Every pattern must contain a single named group "ver".
SPECS = [
    {
        "name": "etc/version",
        "paths": lambda: paths(VERSION_FILE_PATH),
        "patterns": [r"^(?P<ver>%s)\s*$" % VERSION_RE],
    },
    {
        "name": "openapi.yaml",
        "paths": lambda: paths("src/java/CdbWebPortal/src/java/openapi.yaml"),
        "patterns": [r"^(\s*version:\s*')(?P<ver>%s)(')" % VERSION_RE],
    },
    {
        "name": "web assets (cache-busting ?v=)",
        "paths": lambda: paths("src/java/CdbWebPortal/web/**/*.xhtml")
        + paths("src/java/CdbWebPortal/web/**/*.css"),
        "patterns": [r"(\?v=v)(?P<ver>%s)" % VERSION_RE],
        # Most files in this glob don't carry the marker; only warn if none do.
        "sparse": True,
    },
    {
        "name": "python-client API setup",
        "paths": lambda: paths("tools/developer_tools/python-client/setup-api.py"),
        "patterns": [r'(version=")(?P<ver>%s)(")' % VERSION_RE],
    },
    {
        "name": "python-client CLI setup",
        "paths": lambda: paths("tools/developer_tools/python-client/setup-cli.py"),
        "patterns": [
            r'(version=")(?P<ver>%s)(")' % VERSION_RE,
            r'(ComponentDB-API==)(?P<ver>%s)(")' % VERSION_RE,
        ],
    },
    {
        "name": "conda recipes",
        "paths": lambda: paths("tools/developer_tools/python-client/conda-recipe/API/meta.yaml")
        + paths("tools/developer_tools/python-client/conda-recipe/CLI/meta.yaml"),
        "patterns": [r'(set version = ")(?P<ver>%s)(")' % VERSION_RE],
    },
]

# Conda build numbers get reset to 0 for a fresh release.
CONDA_BUILD_NUMBER_RE = re.compile(r"^(\s*number:\s*)\d+\s*$", re.MULTILINE)

RELEASE_NOTES_STUB = "# General\n- \n# Bug Fixes\n- \n"


def read(path):
    with open(os.path.join(rootDir, path), "r") as f:
        return f.read()


def write(path, contents):
    with open(os.path.join(rootDir, path), "w") as f:
        f.write(contents)


def current_version():
    return read(VERSION_FILE_PATH).strip()


def bump_file(path, patterns, new_version):
    """Apply every pattern to a file's contents, returning (new_contents, old_versions_found)."""
    contents = read(path)
    old_versions = []

    def substitute(match):
        old_versions.append(match.group("ver"))
        # Replace only the "ver" span, keeping whatever surrounding literal
        # text the pattern captured (quotes, prefixes, etc.) untouched.
        span_start = match.start("ver") - match.start()
        span_end = match.end("ver") - match.start()
        return match.group(0)[:span_start] + new_version + match.group(0)[span_end:]

    for pattern in patterns:
        contents = re.sub(pattern, substitute, contents, flags=re.MULTILINE)

    return contents, old_versions


def reset_conda_build_number(path):
    contents = read(path)
    new_contents, count = CONDA_BUILD_NUMBER_RE.subn(r"\g<1>0", contents)
    changed = new_contents != contents
    return new_contents, changed


def scaffold_release_notes(version):
    notes_path = os.path.join(RELEASE_NOTES_DIR, "%s.md" % version)
    if os.path.exists(os.path.join(rootDir, notes_path)):
        return notes_path, False
    return notes_path, True


def build_plan(new_version, base_version):
    """Compute every change without touching disk. Returns (file_changes, notes_path, notes_is_new)."""
    file_changes = []

    for spec in SPECS:
        spec_paths = spec["paths"]()
        if not spec_paths:
            print("WARNING: spec '%s' matched no files" % spec["name"], file=sys.stderr)
            continue

        sparse = spec.get("sparse", False)
        spec_hits = 0
        for path in spec_paths:
            new_contents, old_versions = bump_file(path, spec["patterns"], new_version)
            if not old_versions:
                if not sparse:
                    print(
                        "WARNING: %s matched no version markers for spec '%s'" % (path, spec["name"]),
                        file=sys.stderr,
                    )
                continue
            spec_hits += 1
            drift = any(v != base_version for v in old_versions)
            file_changes.append(
                {
                    "path": path,
                    "old_versions": old_versions,
                    "new_contents": new_contents,
                    "drift": drift,
                }
            )

        if sparse and spec_hits == 0:
            print("WARNING: spec '%s' matched no version markers in any file" % spec["name"], file=sys.stderr)

    conda_recipe_paths = paths("tools/developer_tools/python-client/conda-recipe/API/meta.yaml") + paths(
        "tools/developer_tools/python-client/conda-recipe/CLI/meta.yaml"
    )
    for path in conda_recipe_paths:
        new_contents, changed = reset_conda_build_number(path)
        if changed:
            file_changes.append(
                {
                    "path": path,
                    "old_versions": ["build number -> 0"],
                    "new_contents": new_contents,
                    "drift": False,
                    "build_number_reset": True,
                }
            )

    notes_path, notes_is_new = scaffold_release_notes(new_version)

    return file_changes, notes_path, notes_is_new


def print_plan(file_changes, notes_path, notes_is_new, new_version):
    print("Preparing release %s\n" % new_version)
    for change in file_changes:
        if change.get("build_number_reset"):
            print("  %-70s conda build number -> 0" % change["path"])
            continue
        old = ", ".join(sorted(set(change["old_versions"])))
        marker = "  (DRIFT)" if change["drift"] else ""
        print("  %-70s %s -> %s%s" % (change["path"], old, new_version, marker))
    if notes_is_new:
        print("  %-70s (new stub)" % notes_path)
    else:
        print("  %-70s (already exists, left alone)" % notes_path)
    print()


def main():
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("version", nargs="?", help="new release version, e.g. 3.18.0")
    parser.add_argument("--dry-run", action="store_true", help="print planned changes without writing")
    args = parser.parse_args()

    base_version = current_version()

    new_version = args.version
    if not new_version:
        new_version = input("Current version is %s. New version: " % base_version).strip()

    if not re.fullmatch(VERSION_RE, new_version):
        raise ValueError("Version '%s' does not look like <major>.<minor>.<patch>[.<suffix>]" % new_version)

    file_changes, notes_path, notes_is_new = build_plan(new_version, base_version)
    print_plan(file_changes, notes_path, notes_is_new, new_version)

    if args.dry_run:
        return

    response = input("Write these changes? [Y/n]: ").strip().lower()
    if response not in ("", "y", "yes"):
        print("Aborted, no files written.")
        return

    for change in file_changes:
        write(change["path"], change["new_contents"])
    if notes_is_new:
        write(notes_path, RELEASE_NOTES_STUB)

    print("Wrote %d file(s)." % (len(file_changes) + (1 if notes_is_new else 0)))


if __name__ == "__main__":
    main()
