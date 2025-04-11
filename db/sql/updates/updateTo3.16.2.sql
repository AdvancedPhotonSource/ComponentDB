--
-- Copyright (c) UChicago Argonne, LLC. All rights reserved.
-- See LICENSE file.
--

-- Execute by running `mysql CDB_DB_NAME -h 127.0.0.1 -u cdb -p < updateTo3.16.2.sql`

INSERT IGNORE INTO `setting_type` VALUES
(23090,'ItemDomainCableDesign.List.Display.QrId','Display component qrId.','false');