--
-- Copyright (c) UChicago Argonne, LLC. All rights reserved.
-- See LICENSE file.
--

-- Execute by running `mysql CDB_DB_NAME --host=127.0.0.1 --user=cdb -p < updateTo3.16.0.sql`

INSERT INTO property_type_handler (name, description) VALUES ('Markdown', 'Handler for properties containing markdown documentation');

ALTER TABLE property_value ADD COLUMN `text` text DEFAULT NULL AFTER `value`;  