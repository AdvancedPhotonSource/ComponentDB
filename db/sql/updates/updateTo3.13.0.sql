--
-- Copyright (c) UChicago Argonne, LLC. All rights reserved.
-- See LICENSE file.
--

-- Execute by running `mysql CDB_DB_NAME --host=127.0.0.1 --user=cdb -p < updateTo3.13.0.sql`

SET @property_type_name = 'Control Interface'; 
INSERT INTO property_type(name, description, is_internal) VALUES (@property_type_name, 'Describes the interface to parent in the control hierarchy.', 1);

SET @property_type_id = (SELECT id FROM property_type WHERE name = @property_type_name);
INSERT INTO allowed_property_value (property_type_id, value) VALUES (@property_type_id, 'Direct Connection'); 

ALTER TABLE item_element ADD COLUMN `represents_item_element_id` int(11) unsigned DEFAULT NULL AFTER derived_from_item_element_id;
ALTER TABLE item_element_history ADD COLUMN `represents_item_element_id` int(11) unsigned DEFAULT NULL AFTER derived_from_item_element_id;

delimiter //

DROP PROCEDURE IF EXISTS search_items;//
CREATE PROCEDURE `search_items` (IN limit_row int, IN domain_id int, IN search_string VARCHAR(255)) 
BEGIN
	SET search_string = CONCAT('%', search_string, '%'); 
	SELECT item.* from item 
	INNER JOIN v_item_self_element ise ON item.id = ise.item_id 
	INNER JOIN item_element ie ON ise.self_element_id = ie.id
	INNER JOIN entity_info ei ON ise.entity_info_id = ei.id
	INNER JOIN user_info owneru ON ei.owner_user_id = owneru.id
	INNER JOIN user_info creatoru ON ei.created_by_user_id = creatoru.id
	INNER JOIN user_info updateu ON ei.last_modified_by_user_id = updateu.id
	LEFT OUTER JOIN item derived_item ON derived_item.id = item.derived_from_item_id 
	WHERE item.domain_id = domain_id
	AND (
		item.name LIKE search_string
		OR item.qr_id LIKE search_string
		OR item.item_identifier1 LIKE search_string
		OR item.item_identifier2 LIKE search_string
		OR ie.description LIKE search_string
		OR derived_item.name LIKE search_string
		OR owneru.username LIKE search_string
		OR creatoru.username LIKE search_string
		OR updateu.username LIKE search_string
	)
	LIMIT limit_row;
END //

DROP PROCEDURE IF EXISTS search_item_elements;//
CREATE PROCEDURE `search_item_elements` (IN limit_row int, IN search_string VARCHAR(255)) 
BEGIN
	SET search_string = CONCAT('%', search_string, '%'); 
	SELECT ie.* from item_element ie
	INNER JOIN item parent_item ON parent_item.id = ie.parent_item_id 
	LEFT OUTER JOIN item_element derived_ie ON derived_ie.id = ie.derived_from_item_element_id
	WHERE (
		ie.name LIKE search_string
		OR parent_item.name LIKE search_string
		OR derived_ie.name LIKE search_string
		OR ie.description LIKE search_string
	)
	LIMIT limit_row;
END //