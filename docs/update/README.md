# Updating ComponentDB 
Generic upgrade procedure for any release -- one version or several at once. What
changed in a given release lives in `docs/release-notes/<VERSION>.md`; older releases
also have dedicated walkthroughs: [v3.8.0](v3.8.0.md), [v3.9.x](v3.9.x.md),
[v3.10.x](v3.10.x.md).

# Before You Start 
The installed version isn't recorded anywhere in the database -- read it off the old
distribution directory name (`ComponentDB-<VERSION>`) or its `etc/version`. Then skim
the release notes for every version between it and your target, in order.

# Update Instructions 
## Download 
```sh
cd $CDB_INSTALL_DIR
wget https://github.com/AdvancedPhotonSource/ComponentDB/archive/v<VERSION>.tar.gz
tar -xvf v<VERSION>.tar.gz
rm v<VERSION>.tar.gz
cd ComponentDB-<VERSION>
source setup.sh   # everything below assumes this
```

## Update  
1. Back up the database, and stash the backup as a pre-change snapshot
```sh
make backup
# Backups are stamped by day, so a later backup in this same procedure would land in
# the same directory and overwrite this one -- copy it aside now.
cp -r $CDB_INSTALL_DIR/backup/cdb/`date +%Y%m%d` $CDB_INSTALL_DIR/backup/cdb/`date +%Y%m%d`-pre-change
```

2. Apply the SQL updates

Only releases with schema or seed-data changes ship an `updateTo<VERSION>.sql` --
check both `db/sql/updates/` and `db/sql/updates/old/` (scripts migrate into `old/`
over time, so there's no fixed cutoff between the two).

Nothing tracks which scripts have already run -- there's no schema-version table
anywhere in the product. Skipping releases means applying every `updateTo*.sql`
between your old version and the target yourself, one at a time, in numeric order
(not `ls` order -- `3.10.0` sorts before `3.2.0` lexically):
```sh
cd db/sql/updates
export CDB_DB_NAME=cdb
ls updateTo*.sql | sort -V   # confirm the order before running anything
# e.g. 3.15.6 -> 3.18.0:
mysql $CDB_DB_NAME --host=127.0.0.1 --user=cdb -p < updateTo3.16.0.sql
mysql $CDB_DB_NAME --host=127.0.0.1 --user=cdb -p < updateTo3.16.2.sql
mysql $CDB_DB_NAME --host=127.0.0.1 --user=cdb -p < updateTo3.17.0.sql
mysql $CDB_DB_NAME --host=127.0.0.1 --user=cdb -p < updateTo3.18.0.sql
cd ../../../
```
Gotchas:
- The chain is sparse by design -- there's no `updateTo3.16.1.sql` or `3.17.1.sql`.
  A gap in the sequence is normal, not a sign something's missing.
- A few headers name the wrong file in their "Execute by running" comment (copy-paste
  leftovers) -- trust the filename you're running, not the comment.
- Scripts using `delimiter //` must be piped to the client as shown, not pasted into
  a tool that ignores `delimiter`.
- Scripts are idempotent (`INSERT IGNORE` / `ALTER ... IF NOT EXISTS`) -- re-running
  one you're unsure about is safe.
- If a script fails on a trigger, drop the trigger and re-run it -- triggers get
  recreated from scratch if you rebuild below:
~~~~
mysql $CDB_DB_NAME --host=127.0.0.1 --user=cdb -p
drop trigger <trigger_name>;
~~~~

**Rebuild only if a script asks for it.** Older `updateTo*.sql` scripts carried only
part of the delta and said so explicitly (e.g. `### PLEASE REBUILD DB ###` in
`old/updateTo3.9.0.sql`) -- the rest (table/view/procedure/trigger changes) lived in
`db/sql/create_*.sql` and only reached the database via a full rebuild. Recent scripts
are self-contained, so this step is normally skipped -- worth knowing, since a rebuild
can take a while on a large database:
~~~~
# make backup
# mkdir -p ../db/cdb/
# cp ../backup/cdb/`date +%Y%m%d`/populate* ../db/cdb
# make db
~~~~
`make db` drops and recreates the database, repopulating it from those `populate*`
files -- exactly why step 1's backup lives safely off to the side first.

3. Configure any plugins, see [plugin wiki](https://github.com/AdvancedPhotonSource/ComponentDB/wiki/Plugins "github plugin wiki").
```sh
make deploy-cdb-plugin
```
4. Deploy the portal
```sh
# make configure-web-portal   # only if generated config templates changed -- rare
make deploy-web-portal
```
5. Verify -- `make deploy-web-portal` already restarts Glassfish, so just load the
   portal's About page and confirm it reports the new version.

# Rebuilding Support Software (rarely needed) 
Support software (Payara, Java, Python, etc. under `support-<hostname>/`) almost never
needs rebuilding for a routine update. Skip this unless the release notes for a
version you're passing through explicitly call out a Payara/Java/Python change.
~~~~
rm -rf support-`hostname -s`
# or, to keep the old one around:
mv support-`hostname -s` support-`hostname -s`-<OLD_VERSION>
make support
~~~~
