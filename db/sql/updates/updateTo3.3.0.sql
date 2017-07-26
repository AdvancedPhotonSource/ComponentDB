--
-- Copyright (c) UChicago Argonne, LLC. All rights reserved.
-- See LICENSE file.
--

-- After executing this file backup and rebuild db from backup to apply other changes. 
-- Execute by running `mysql CDB_DB_NAME --host=127.0.0.1 --user=cdb -p < updateTo3.3.0.sql` 

ALTER TABLE property_type ADD prompt_description VARCHAR(256) AFTER description;


