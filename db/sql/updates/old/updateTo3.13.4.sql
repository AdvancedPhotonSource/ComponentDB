--
-- Copyright (c) UChicago Argonne, LLC. All rights reserved.
-- See LICENSE file.
--

-- Execute by running `mysql CDB_DB_NAME --host=127.0.0.1 --user=cdb -p < updateTo3.13.3.sql`

UPDATE domain SET item_identifier1_label = "Serial Number" WHERE name = "Cable Inventory";
