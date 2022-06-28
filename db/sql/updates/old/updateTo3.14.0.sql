--
-- Copyright (c) UChicago Argonne, LLC. All rights reserved.
-- See LICENSE file.
--

-- Execute by running `mysql CDB_DB_NAME --host=127.0.0.1 --user=cdb -p < updateTo3.13.3.sql`



INSERT IGNORE INTO `setting_type` VALUES
(23080,'ItemDomainCableDesign.List.Display.LocationEndpoint1','Display LocationEndpoint1.','false'),
(23081,'ItemDomainCableDesign.List.FilterBy.LocationEndpoint1','FilterBy LocationEndpoint1.',''),
(23082,'ItemDomainCableDesign.List.Display.LocationEndpoint2','Display LocationEndpoint2.','false'),
(23083,'ItemDomainCableDesign.List.FilterBy.LocationEndpoint2','FilterBy LocationEndpoint2.',''),
(23084,'ItemDomainCableDesign.List.Display.AssignedInventory','Display column for assigned inventory in cable design list.','true'),
(23085,'ItemDomainCableDesign.List.Display.InstallationStatus','Display column for installation status in cable  design list.','true'),
(23086,'ItemDomainCableDesign.List.FilterBy.AssignedInventory','Filter for components by assigned inventory.',NULL),
(23087,'ItemDomainCableDesign.List.FilterBy.InstallationStatus','Filter for components by installation status.',NULL);

DROP VIEW IF EXISTS v_inventory_located_by_relationship;
CREATE VIEW v_inventory_located_by_relationship
AS
SELECT item.id as inventory_item_id, item.name as inventory_item_name, loc_item.id as location_item_id, loc_item.name as location_item_name, ier.id as item_element_relationship_id
FROM v_item_extras item 
INNER JOIN item_element_relationship AS ier ON ier.first_item_element_id = item.self_element_id 
INNER JOIN v_item_extras AS loc_item ON loc_item.self_element_id = ier.second_item_element_id 
WHERE ier.relationship_type_id = 1 and  (item.domain_id = 3 or item.domain_id = 8)
AND item.id not in (
	SELECT item.id 
	FROM item, item_element ie 
	WHERE (ie.contained_item_id1 = item.id or ie.contained_item_id2 = item.id) 
	AND ie.is_housed = true 
	AND item.id is not NULL and (item.domain_id = 3 or item.domain_id = 8)
);
