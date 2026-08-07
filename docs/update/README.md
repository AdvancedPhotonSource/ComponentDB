# Updating ComponentDB 
This is the generic upgrade procedure for any release. It applies whether you are
upgrading one release or skipping several. For what changed in a specific release see
`docs/release-notes/<VERSION>.md`. Older releases also have dedicated walkthroughs:
[v3.8.0](v3.8.0.md), [v3.9.x](v3.9.x.md), [v3.10.x](v3.10.x.md).

# Before You Start 
Know the version you are upgrading *from*. It is not recorded in the database — read it
off the old distribution directory name (`ComponentDB-<VERSION>`) or its `etc/version`.
Then read the release notes for every version between it and the target, in order.

# Update Instructions 
## Download 
~~~~
# Navigate to cdb installation directory
cd $CDB_INSTALL_DIR
# Download the release package to cdb install directory
wget https://github.com/AdvancedPhotonSource/ComponentDB/archive/v<VERSION>.tar.gz
tar -xvf v<VERSION>.tar.gz
rm v<VERSION>.tar.gz
cd ComponentDB-<VERSION>
# Every command below assumes this has been sourced.
source setup.sh
~~~~

## Update  
1. Back up the database, and copy the backup aside as the pre-change snapshot
~~~~
# It's preferred to make a backup before making any db updates.
make backup
# Backups are date-stamped to the day, so copy this one aside now -- otherwise a
# rebuild backup later in this procedure (if one turns out to be needed) will land
# in the same directory and overwrite it.
cp -r $CDB_INSTALL_DIR/backup/cdb/`date +%Y%m%d` $CDB_INSTALL_DIR/backup/cdb/`date +%Y%m%d`-pre-change
~~~~

2. Apply the SQL updates

   Not every release has an `updateTo<VERSION>.sql` script -- only those with schema or
   seed-data changes do. Check both `db/sql/updates/` and `db/sql/updates/old/` for the
   target version(s) -- older scripts get moved into `old/` over time as new releases
   ship, so there is no fixed version cutoff between the two; always check both
   directories rather than assuming where a given version's script lives.

   If you're skipping releases, there is no schema-version table anywhere in the
   product, so nothing tracks which update scripts have already run -- you must apply
   every `updateTo*.sql` strictly between your old version and the target version
   yourself, one at a time, in numeric order (not the order `ls` gives you, since e.g.
   `3.10.0` sorts before `3.2.0` lexically):
   ~~~~
   cd db/sql/updates
   export CDB_DB_NAME=cdb
   ls updateTo*.sql | sort -V   # confirm the order before running anything
   # Execute the update db script(s) for your version range, in order, e.g. going
   # from 3.15.6 to 3.18.0:
   mysql $CDB_DB_NAME --host=127.0.0.1 --user=cdb -p < updateTo3.16.0.sql
   mysql $CDB_DB_NAME --host=127.0.0.1 --user=cdb -p < updateTo3.16.2.sql
   mysql $CDB_DB_NAME --host=127.0.0.1 --user=cdb -p < updateTo3.17.0.sql
   mysql $CDB_DB_NAME --host=127.0.0.1 --user=cdb -p < updateTo3.18.0.sql
   cd ../../../ # Navigate back to release directory
   ~~~~
   Notes on the chain:
   - It's sparse by design -- there is no `updateTo3.16.1.sql` or `updateTo3.17.1.sql`.
     A missing version in the sequence is expected, not an error.
   - A few script headers name the wrong file in their "Execute by running" comment
     (a copy-paste leftover) -- always trust the filename you're running, not the text
     inside the header comment.
   - Scripts that use `delimiter //` (for stored procedures) must be piped to the
     `mysql`/`mariadb` client as shown above, not pasted into a tool that doesn't honor
     `delimiter`.
   - Scripts are written to be idempotent (`INSERT IGNORE` / `ALTER ... IF NOT EXISTS`),
     so re-running one you're unsure about is safe.
   - If an update script fails because of a trigger, drop the trigger and re-run the
     script -- triggers are recreated from scratch in the next step regardless:
   ~~~~
   mysql $CDB_DB_NAME --host=127.0.0.1 --user=cdb -p
   drop trigger <trigger_name>;
   ~~~~

   **Check whether a full rebuild is also required.** Older `updateTo*.sql` scripts
   only carried part of the delta, leaving changed table definitions, views, stored
   procedures, and triggers to be picked up separately from `db/sql/create_*.sql` --
   those scripts say so explicitly in a header comment (e.g. `### PLEASE REBUILD DB ###`
   in `old/updateTo3.9.0.sql`). Recent scripts are self-contained -- they carry the
   complete delta (`ALTER TABLE`, `CREATE PROCEDURE`, etc.) themselves -- and running
   them is sufficient on its own; skipping the rebuild below saves significant time on
   a large database. This is typically **not** run -- only uncomment it if a script you
   ran calls for a rebuild:
   ~~~~
   # make backup
   # mkdir -p ../db/cdb/
   # cp ../backup/cdb/`date +%Y%m%d`/populate* ../db/cdb
   # make db
   ~~~~
   `make db` drops and recreates the database before repopulating it from those
   `populate*` files -- this is expected; it's why the backup from step 1 lives in its
   own pre-change snapshot rather than being overwritten first.

3. Configure any plugins, see [plugin wiki](https://github.com/AdvancedPhotonSource/ComponentDB/wiki/Plugins "github plugin wiki").
~~~~
make deploy-cdb-plugin
~~~~
4. Configure and deploy the portal
~~~~
# Typically not required -- only needed if generated config templates changed
# (glassfish-resources.xml, etc).
# make configure-web-portal


make deploy-web-portal
~~~~
5. Verify -- Check the
   portal's About page to confirm it comes up and reports the new version.

# Rebuilding Support Software (rarely needed) 
Support software (Payara, Java, Python, etc. under `support-<hostname>/`) almost never
needs to be rebuilt for a routine update -- **skip this section unless the release
notes for a version you are passing through explicitly call out a Payara/Java/Python
support change.** If they don't, do not run this.
~~~~
# Remove or move the old support directory
rm -rf support-`hostname -s`
# Or move
mv support-`hostname -s` support-`hostname -s`-<OLD_VERSION>
# Install the new support
make support
~~~~
