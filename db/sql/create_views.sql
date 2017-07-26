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


