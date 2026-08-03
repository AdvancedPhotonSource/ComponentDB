--
-- Copyright (c) UChicago Argonne, LLC. All rights reserved.
-- See LICENSE file.
--

-- Execute by running `mysql CDB_DB_NAME -h 127.0.0.1 -u cdb -p < updateTo3.16.2.sql`

INSERT IGNORE INTO `setting_type` VALUES
(15018,'Search.Display.ItemDomainMachineDesignIOC','Display search result for IOC items.','true'),
(15019,'Search.Display.ItemDomainApp','Display search result for application items.','true'),
(15020,'Search.Display.PropertyValue','Display search result for property values.','true');

-- Add text property value (value/text/tag) matching to the item search procedures
-- and a dedicated IOC item search procedure (used to search markdown IOC instructions).

delimiter //

DROP PROCEDURE IF EXISTS search_ioc_items;//
CREATE PROCEDURE `search_ioc_items` (IN limit_row int, IN search_string VARCHAR(255))
BEGIN
	DECLARE ioc_entity_type_id INT;
	SET ioc_entity_type_id = 11;   -- EntityTypeName.IOC_ID

	SET search_string = CONCAT('%', search_string, '%');
	SELECT DISTINCT item.* from item
	INNER JOIN v_item_self_element ise ON item.id = ise.item_id
	INNER JOIN item_element ie ON ise.self_element_id = ie.id
	INNER JOIN entity_info ei ON ise.entity_info_id = ei.id
	INNER JOIN user_info owneru ON ei.owner_user_id = owneru.id
	INNER JOIN user_info creatoru ON ei.created_by_user_id = creatoru.id
	INNER JOIN user_info updateu ON ei.last_modified_by_user_id = updateu.id
	INNER JOIN item_entity_type iet ON iet.item_id = item.id AND iet.entity_type_id = ioc_entity_type_id
	WHERE (
		item.name LIKE search_string
		OR item.qr_id LIKE search_string
		OR item.item_identifier1 LIKE search_string
		OR item.item_identifier2 LIKE search_string
		OR ie.description LIKE search_string
		OR owneru.username LIKE search_string
		OR creatoru.username LIKE search_string
		OR updateu.username LIKE search_string
	)
	LIMIT limit_row;
END //

delimiter ;
