--
-- Copyright (c) UChicago Argonne, LLC. All rights reserved.
-- See LICENSE file.
--

-- Execute by running `mysql CDB_DB_NAME -h 127.0.0.1 -u cdb -p < updateTo3.16.2.sql`

INSERT IGNORE INTO `setting_type` VALUES
(15014,'Search.Display.ItemDomainMachineDesignIOC','Display search result for IOC items.','true'),
(15015,'Search.Display.ItemDomainApp','Display search result for application items.','true');
