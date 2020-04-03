--
-- Copyright (c) UChicago Argonne, LLC. All rights reserved.
-- See LICENSE file.
--

-- Execute by running `mysql CDB_DB_NAME --host=127.0.0.1 --user=cdb -p < updateTo3.9.0.sql`
-- After executing this file backup and rebuild db from backup to apply other changes.
-- NOTE: Update support 

# Enable part number as item_identifier1 for cable catalog domain
UPDATE `domain` SET item_identifier1_label = 'Part Number' where id = 7;

# Prepopulate list of categories for cable catalog. 
INSERT INTO `domain` VALUES
(9,'Cable Design', 'Item domain for managing cable design items', 'Alternate Name', 'UUID', NULL, 'Technical System');
INSERT INTO `item_category` (name, description, domain_id) (select name,description, 9 AS domain_id FROM item_category WHERE domain_id = 2);
