--
-- Copyright (c) UChicago Argonne, LLC. All rights reserved.
-- See LICENSE file.
--

-- Execute by running `mysql CDB_DB_NAME -h 127.0.0.1 -u cdb -p < updateTo3.16.2.sql`


INSERT IGNORE INTO `entity_type` VALUES (11,'IOC','Entity type used for marking a sub domain of ioc type items.');
INSERT IGNORE INTO `allowed_entity_type_domain` VALUES (6,11);
