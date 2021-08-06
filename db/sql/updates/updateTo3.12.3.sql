--
-- Copyright (c) UChicago Argonne, LLC. All rights reserved.
-- See LICENSE file.
--

-- Execute by running `mysql CDB_DB_NAME --host=127.0.0.1 --user=cdb -p < updateTo3.12.3.sql`

INSERT INTO `setting_type` VALUES
(23076,'ItemDomainCableDesign.List.Display.ConnectedDevicesEndpoint1','Display ConnectedDevicesEndpoint1.','false'),
(23077,'ItemDomainCableDesign.List.FilterBy.ConnectedDevicesEndpoint1','FilterBy ConnectedDevicesEndpoint1.',''),
(23078,'ItemDomainCableDesign.List.Display.ConnectedDevicesEndpoint2','Display ConnectedDevicesEndpoint2.','false'),
(23079,'ItemDomainCableDesign.List.FilterBy.ConnectedDevicesEndpoint2','FilterBy ConnectedDevicesEndpoint2.','');

