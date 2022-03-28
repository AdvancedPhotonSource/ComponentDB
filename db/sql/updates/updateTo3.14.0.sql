--
-- Copyright (c) UChicago Argonne, LLC. All rights reserved.
-- See LICENSE file.
--

-- Execute by running `mysql CDB_DB_NAME --host=127.0.0.1 --user=cdb -p < updateTo3.13.3.sql`



INSERT INTO `setting_type` VALUES
(23080,'ItemDomainCableDesign.List.Display.LocationEndpoint1','Display LocationEndpoint1.','false'),
(23081,'ItemDomainCableDesign.List.FilterBy.LocationEndpoint1','FilterBy LocationEndpoint1.',''),
(23082,'ItemDomainCableDesign.List.Display.LocationEndpoint2','Display LocationEndpoint2.','false'),
(23083,'ItemDomainCableDesign.List.FilterBy.LocationEndpoint2','FilterBy LocationEndpoint2.','');