--
-- Copyright (c) UChicago Argonne, LLC. All rights reserved.
-- See LICENSE file.
--

-- Execute by running `mysql CDB_DB_NAME -h 127.0.0.1 -u cdb -p < updateTo3.16.2.sql`

INSERT IGNORE INTO `setting_type` VALUES
(23090,'ItemDomainCableDesign.List.Display.QrId','Display component qrId.','false'),
(23091,'ItemDomainCableDesign.Default.CablePrefix','Default cable prefix for cable design.',NULL),
(23092,'ItemDomainCableDesign.Default.Project','Default project for cable design.',NULL),
(23093,'ItemDomainCableDesign.Default.TechnicalSystem','Default technical system for cable design.',NULL);