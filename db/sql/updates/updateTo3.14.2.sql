--
-- Copyright (c) UChicago Argonne, LLC. All rights reserved.
-- See LICENSE file.
--

-- Execute by running `mysql CDB_DB_NAME --host=127.0.0.1 --user=cdb -p < updateTo3.14.2.sql`



INSERT IGNORE INTO `setting_type` VALUES
(15017,'Search.Display.ItemDomainMAARC', 'Display search result for MAARC items', 'false'),
(23088,'ItemDomainCableDesign.List.Display.TotalReqLengthDisplay','Display total required cable length.','false'),
(23089,'ItemDomainCableDesign.List.FilterBy.TotalReqLengthDisplay','Filter for total required cable length.',NULL);
