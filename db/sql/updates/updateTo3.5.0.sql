--
-- Copyright (c) UChicago Argonne, LLC. All rights reserved.
-- See LICENSE file.
--

-- Execute by running `mysql CDB_DB_NAME --host=127.0.0.1 --user=cdb -p < updateTo3.5.0.sql`
-- After executing this file backup and rebuild db from backup to apply other changes.
-- NOTE: Update support to Glassfish 5.0


ALTER TABLE item_element ADD COLUMN contained_item_id2 int(11) AFTER contained_item_id;


