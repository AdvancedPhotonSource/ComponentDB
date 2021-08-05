--
-- Copyright (c) UChicago Argonne, LLC. All rights reserved.
-- See LICENSE file.
--

CREATE VIEW v_item_connector_domain_inventory 
AS
SELECT item_connector.* 
FROM item 
INNER JOIN domain ON item.domain_id = domain.id 
INNER JOIN item_connector on item.id = item_connector.item_id 
WHERE domain.name='Inventory';

DROP VIEW IF EXISTS v_item_extras;
CREATE VIEW v_item_extras 
AS
SELECT item.*, ie.id AS self_element_id 
FROM item 
INNER JOIN item_element ie ON ie.parent_item_id = item.id 
WHERE ie.name IS NULL AND ie.derived_from_item_element_id IS NULL;

DROP VIEW IF EXISTS v_machine_element;
CREATE VIEW v_machine_element
AS
SELECT ie.id AS item_element_id, 
parent_item.id AS parent_item_id, parent_item.name as parent_item_name,
child_machine.id AS child_machine_id, child_machine.name as child_machine_name,
assigned_item.id AS assigned_item_id, assigned_item.name as assigned_item_name
FROM item_element ie 
INNER JOIN v_item_extras parent_item ON parent_item.id = ie.parent_item_id 
INNER JOIN v_item_extras child_machine ON child_machine.id = ie.contained_item_id1 
INNER JOIN item_element child_machine_element ON child_machine.self_element_id = child_machine_element.id 
LEFT OUTER JOIN v_item_extras assigned_item ON assigned_item.id = child_machine_element.contained_item_id2;

DROP VIEW IF EXISTS v_item_hierarchy;
CREATE VIEW v_item_hierarchy
AS
SELECT ie.id AS item_element_id, 
parent_item.id AS parent_item_id, parent_item.name as parent_item_name,
child_item.id AS child_item_id, child_item.name as child_item_name
FROM item_element ie 
INNER JOIN item parent_item ON parent_item.id = ie.parent_item_id 
INNER JOIN item child_item ON child_item.id = ie.contained_item_id1; 

DROP VIEW IF EXISTS v_inventory_located_by_relationship;
CREATE VIEW v_inventory_located_by_relationship
AS
SELECT item.id as inventory_item_id, item.name as inventory_item_name, loc_item.id as location_item_id, loc_item.name as location_item_name, ier.id as item_element_relationship_id
FROM v_item_extras item 
INNER JOIN item_element_relationship AS ier ON ier.first_item_element_id = item.self_element_id 
INNER JOIN v_item_extras AS loc_item ON loc_item.self_element_id = ier.second_item_element_id 
WHERE ier.relationship_type_id = 1 and item.domain_id = 3 
AND item.id not in (
	SELECT item.id 
	FROM item, item_element ie 
	WHERE (ie.contained_item_id1 = item.id or ie.contained_item_id2 = item.id) 
	AND ie.is_housed = true 
	AND item.id is not NULL and item.domain_id = 3
);

CREATE VIEW v_item_domain_inventory_connector_status 
AS 
SELECT 
inventoryItem.id as inv_item_id, 
invConnector.id as inv_item_connector_id, 
ier.id as item_element_relationship_id, 
ier.first_item_connector_id, 
ier.second_item_connector_id, 
invConnector.connector_id as inv_connector_id, 
conn.id as cat_connector_id, 
conn.connector_type_id, 
conn.is_male 
FROM item 
INNER JOIN item inventoryItem on item.id = inventoryItem.derived_from_item_id 
INNER JOIN item_connector on item.id = item_connector.item_id 
INNER JOIN connector conn on (item_connector.connector_id = conn.id) 
LEFT OUTER JOIN v_item_connector_domain_inventory invConnector on (invConnector.connector_id = conn.id and invConnector.item_id = inventoryItem.id) 
LEFT OUTER JOIN item_element_relationship ier on invConnector.id = ier.first_item_connector_id 
ORDER BY inventoryItem.id;

CREATE VIEW v_item_self_element
AS 
SELECT i.id as item_id, ie.id as self_element_id, ie.entity_info_id
FROM item i inner join item_element ie on i.id = ie.parent_item_id
WHERE ie.name is NULL and ie.derived_from_item_element_id is null;


