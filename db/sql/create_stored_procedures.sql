--
-- Copyright (c) UChicago Argonne, LLC. All rights reserved.
-- See LICENSE file.
--

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

DROP PROCEDURE IF EXISTS search_cable_design_items;//
CREATE PROCEDURE `search_cable_design_items` (IN limit_row int, IN search_string VARCHAR(255)) 
BEGIN
	DECLARE cable_relationship_id INT;
	DECLARE cable_design_domain_id INT;

	SET cable_relationship_id = 4; 
	SET cable_design_domain_id = 9;

	SET search_string = CONCAT('%', search_string, '%'); 
	SELECT DISTINCT item.* from item 
	INNER JOIN v_item_self_element ise ON item.id = ise.item_id 
	INNER JOIN item_element ie ON ise.self_element_id = ie.id
	INNER JOIN entity_info ei ON ise.entity_info_id = ei.id
	INNER JOIN user_info owneru ON ei.owner_user_id = owneru.id
	INNER JOIN user_info creatoru ON ei.created_by_user_id = creatoru.id
	INNER JOIN user_info updateu ON ei.last_modified_by_user_id = updateu.id
	LEFT OUTER JOIN item derived_item ON derived_item.id = item.derived_from_item_id 
	LEFT OUTER JOIN item_element_relationship iercable on iercable.second_item_element_id = ise.self_element_id
	INNER JOIN v_item_extras icon on icon.self_element_id = iercable.first_item_element_id
	WHERE item.domain_id = cable_design_domain_id and iercable.relationship_type_id = cable_relationship_id
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
		OR icon.name LIKE search_string
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

DROP PROCEDURE IF EXISTS fetch_relationship_children_items;//
CREATE PROCEDURE `fetch_relationship_children_items` (IN item_id int, IN relationship_type_id int) 
BEGIN
	SELECT item.* 
	FROM item_element_relationship ier
	INNER JOIN v_item_self_element vitem1 on ier.first_item_element_id = vitem1.self_element_id
	INNER JOIN v_item_self_element vitem2 on ier.second_item_element_id = vitem2.self_element_id
	INNER JOIN item on vitem1.item_id = item.id	
	WHERE ier.relationship_type_id = relationship_type_id and vitem2.item_id = item_id;
END //

DROP PROCEDURE IF EXISTS fetch_relationship_parent_items;//
CREATE PROCEDURE `fetch_relationship_parent_items` (IN item_id int, IN relationship_type_id int) 
BEGIN
	SELECT item.* 
	FROM item_element_relationship ier
	INNER JOIN v_item_self_element vitem1 on ier.first_item_element_id = vitem1.self_element_id
	INNER JOIN v_item_self_element vitem2 on ier.second_item_element_id = vitem2.self_element_id
	INNER JOIN item on vitem2.item_id = item.id	
	WHERE ier.relationship_type_id = relationship_type_id and vitem1.item_id = item_id;
END //

DROP PROCEDURE IF EXISTS fetch_relationship_parent_property_values;//
CREATE PROCEDURE `fetch_relationship_parent_property_values` (IN item_id int, IN parent_item_id int, IN relationship_type_id int) 
BEGIN
	SELECT pv.*
	FROM item_element_relationship ier 
	INNER JOIN v_item_self_element vitem1 on ier.first_item_element_id = vitem1.self_element_id 
	INNER JOIN v_item_self_element vitem2 on ier.second_item_element_id = vitem2.self_element_id 
	LEFT OUTER JOIN item_element_relationship_property ierp on ier.id = ierp.item_element_relationship_id
	INNER JOIN property_value pv on pv.id = ierp.property_value_id 
	WHERE ier.relationship_type_id = relationship_type_id and vitem1.item_id = item_id and vitem2.item_id = parent_item_id;
END //

DROP PROCEDURE IF EXISTS fetch_name_filter_for_relationship_hierarchy;//
CREATE PROCEDURE `fetch_name_filter_for_relationship_hierarchy` (IN domain_id int, IN entity_type_id int, IN relationship_type_id int, in name_pattern varchar(255)) 
BEGIN
	SELECT item.*
	FROM item
	LEFT OUTER JOIN item_entity_type AS iet ON iet.item_id = item.id
	WHERE item.domain_id = domain_id AND iet.entity_type_id = entity_type_id AND item.name LIKE name_pattern
	OR item.id 
	IN (
	SELECT DISTINCT vitem.id
	FROM item_element_relationship ier 
	INNER JOIN v_item_extras vitem ON ier.first_item_element_id = vitem.self_element_id 
	WHERE ier.relationship_type_id = relationship_type_id AND vitem.name LIKE name_pattern AND vitem.domain_id = domain_id);
END //

DROP PROCEDURE IF EXISTS fetch_location_item_for_locatable_item;//
CREATE PROCEDURE `fetch_location_item_for_locatable_item` (IN locatable_item_id int) 
BEGIN
    SET @query = 'SELECT loc_item.* ';
    SET @query = CONCAT(@query, 'FROM item inner join item_element ie on ie.parent_item_id = item.id inner join item_element_relationship ier on ier.first_item_element_id = ie.id inner join item_element loc_element on loc_element.id = ier.second_item_element_id inner join item loc_item on loc_item.id = loc_element.parent_item_id ');
    SET @query = CONCAT(@query, 'WHERE ie.Name is NULL AND ie.derived_from_item_element_id is NULL AND ier.relationship_type_id = 1 ');
    set @query = CONCAT(@query, "AND item.id = ", locatable_item_id);

    PREPARE stmt FROM @query;
    EXECUTE stmt;

END //

DROP PROCEDURE IF EXISTS fetch_inventory_assigned_to_machine_item_hierarchy;//
CREATE PROCEDURE `fetch_inventory_assigned_to_machine_item_hierarchy` (IN machine_item_id int) 
BEGIN
	WITH RECURSIVE machine as (
		SELECT * FROM v_machine_element ie WHERE ie.parent_item_id = machine_item_id
		UNION
		SELECT me.* FROM v_machine_element me, machine AS m WHERE me.parent_item_id = m.child_machine_id
	) SELECT item.* from machine inner join item on item.id = machine.assigned_item_id WHERE item.domain_id = 3; 
END //

DROP PROCEDURE IF EXISTS fetch_inventory_stored_in_location_hierarchy;//
CREATE PROCEDURE `fetch_inventory_stored_in_location_hierarchy` (IN location_item_id_input int)
BEGIN
	WITH RECURSIVE location AS ( 
		SELECT * 
		FROM v_item_hierarchy  
		WHERE parent_item_id = location_item_id_input 
		UNION 
		SELECT ih.*  
		FROM v_item_hierarchy ih, location l  
		WHERE ih.parent_item_id = l.child_item_id
	) 
	SELECT item.* 
	FROM v_inventory_located_by_relationship ilr 
	INNER JOIN item on item.id = ilr.inventory_item_id 
		WHERE location_item_id = location_item_id_input OR location_item_id in (select child_item_id from location);		
END //

DROP PROCEDURE IF EXISTS fetch_inventory_assigned_to_assembly_hierarchy;//
CREATE PROCEDURE `fetch_inventory_assigned_to_assembly_hierarchy` (IN assembly_item_id int)
BEGIN
	WITH RECURSIVE assembly AS (
		SELECT *
		FROM v_item_hierarchy
		WHERE parent_item_id = assembly_item_id
		UNION
		SELECT ih.*
		FROM v_item_hierarchy ih, assembly a
		WHERE ih.parent_item_id = a.child_item_id
	)
	SELECT item.*
	FROM assembly inner join item on assembly.child_item_id = item.id; 
END //

DROP PROCEDURE IF EXISTS fetch_items_with_property_value;//
CREATE PROCEDURE `fetch_items_with_property_value` (IN property_value_id int) 
BEGIN
	SELECT item.* 
	FROM v_item_self_element v_item 
	INNER JOIN item_element_property iep ON iep.item_element_id = v_item.self_element_id 
	INNER JOIN item ON v_item.item_id = item.id
	WHERE iep.property_value_id = property_value_id;
END //

drop procedure if exists items_with_write_permission_for_user;//
create procedure `items_with_write_permission_for_user` (IN user_id int, IN domain_id int)
BEGIN
    SET @query = 'SELECT i.* FROM item i inner join v_item_self_element ise on i.id = ise.item_id inner join entity_info ei on ise.entity_info_id = ei.id WHERE';
    SET @query = CONCAT(@query, ' i.domain_id =', domain_id);
    SET @query = CONCAT(@query, ' AND (ei.owner_user_id =', user_id);
    SET @query = CONCAT(@query, ' OR ', '(ei.owner_user_group_id in (select ug.id from user_group ug inner join user_user_group uug on uug.user_group_id = ug.id where uug.user_id = ', user_id, ')');
    SET @query = CONCAT(@query, ' AND ei.is_group_writeable = 1))');

    PREPARE stmt FROM @query;
    EXECUTE stmt;
END //

drop function if exists is_record_unique;//
CREATE FUNCTION `is_record_unique`
	(row_count INT,
	 existing_record_id INT,
	 found_record_id INT)
RETURNS BOOLEAN
BEGIN 
	# Check for new items.
	IF ISNULL(existing_record_id)
	THEN
		RETURN (row_count < 1);
	# One row was found, checks that its not same item. 
	ELSEIF row_count = 1
	THEN
		RETURN (existing_record_id = found_record_id);
	# Multiple rows or less than one row was found.
	ELSE
		RETURN (row_count < 1);
	END IF;
END//

drop procedure if exists is_item_attributes_unique//
create procedure `is_item_attributes_unique` 
(IN item_domain_id INT, 
IN item_name VARCHAR(128), 
IN new_item_identifier1 VARCHAR(128), 
IN new_item_identifier2 VARCHAR(128), 
IN new_derived_from_item_id INT,
IN existing_item_id INT,
OUT unique_result BOOLEAN)
BEGIN
	DECLARE row_count INT;
	DECLARE record_found_id INT;

	SELECT 
		IFNULL(new_item_identifier1, ''), 
		IFNULL(new_item_identifier2, ''),
		IFNULL(new_derived_from_item_id, -1)
	INTO
		new_item_identifier1,
		new_item_identifier2,
		new_derived_from_item_id;

	SELECT count(*), id
	INTO row_count, record_found_id
	FROM (
		SELECT	
			name,
			id,
			IFNULL(derived_from_item_id, -1) AS derived_from_item_id,
			IFNULL(item_identifier1, '') AS item_identifier1, 
			IFNULL(item_identifier2, '') AS item_identifier2 
		FROM item
		WHERE domain_id=item_domain_id
			AND name = item_name) AS item2
	WHERE derived_from_item_id = new_derived_from_item_id
		AND item_identifier1 = new_item_identifier1
		AND item_identifier2 = new_item_identifier2;

	SET unique_result = is_record_unique(row_count, existing_item_id, record_found_id);
END//

DROP procedure IF EXISTS is_item_attributes_valid//
CREATE procedure is_item_attributes_valid
	(IN domain_id INT,
	IN item_identifier1 VARCHAR(128),
	IN item_identifier2 VARCHAR(128),
	OUT error_message VARCHAR(128),
	OUT item_identifier1_label varchar(32),
	OUT item_identifier2_label varchar(32)
	)
BEGIN
	SET error_message = NULL;

	SELECT 
		d.item_identifier1_label, d.item_identifier2_label
	INTO
		item_identifier1_label, item_identifier2_label
	FROM domain d
	WHERE d.id = domain_id
	LIMIT 1;

	IF item_identifier1 = '' THEN
		SET item_identifier1 = NULL;
	END IF;
	IF item_identifier2 = '' THEN
		SET item_identifier2 = NULL;
	END IF;

	IF ISNULL(item_identifier1_label) AND NOT ISNULL(item_identifier1)
	THEN		
		SET error_message = 'Item_identifier1 cannot be specified for item of this domain';
	END IF;

	IF ISNULL(item_identifier2_label) AND NOT ISNULL(item_identifier2)
	THEN
		SET error_message = 'Item_identifier2 cannot be specified for item of this domain';
	END IF;	

END//

DROP FUNCTION IF EXISTS check_item//
CREATE FUNCTION check_item
	(domain_id INT, 
	item_name VARCHAR(128), 
	item_identifier1 VARCHAR(128), 
	item_identifier2 VARCHAR(128), 
	derived_from_item_id INT,
	existing_item_id INT)
RETURNS BOOLEAN
BEGIN
	DECLARE error_message varchar(128);  
	DECLARE unique_result BOOLEAN;
	DECLARE item_identifier1_label VARCHAR(32);
	DECLARE item_identifier2_label VARCHAR(32);
	
	CALL is_item_attributes_valid(domain_id, item_identifier1, item_identifier2, error_message, item_identifier1_label, item_identifier2_label);

	IF ISNULL(error_message)
	THEN
		CALL is_item_attributes_unique(domain_id, item_name, item_identifier1, item_identifier2, derived_from_item_id, existing_item_id, unique_result);

		IF NOT unique_result
		THEN
			set error_message = 'Item has nonunique attributes, please update one or more of the following: name';
			
			IF NOT ISNULL(item_identifier1_label) AND NOT ISNULL(item_identifier1) THEN
				set error_message = CONCAT(error_message, ', ', item_identifier1_label);
			END IF;

			IF NOT ISNULL(item_identifier2_label) AND NOT ISNULL(item_identifier2) THEN
				set error_message = CONCAT(error_message, ', ', item_identifier2_label);
			END IF;
			
			SIGNAL SQLSTATE '45000'
				SET MESSAGE_TEXT = error_message;
		END IF;
	END IF;

	IF NOT ISNULL(error_message)
	THEN
		SIGNAL SQLSTATE '45000'
			SET MESSAGE_TEXT = error_message;
	END IF;

	RETURN true;
END//

DROP FUNCTION IF EXISTS is_item_element_attributes_unique;//
CREATE FUNCTION is_item_element_attributes_unique
	(new_item_element_name VARCHAR(64),
	new_parent_item_id INT,
	new_derived_from_item_element_id INT,
	existing_item_element_id INT)
RETURNS BOOLEAN
BEGIN
	DECLARE row_count INT;
	DECLARE record_found_id INT;

	SELECT 
		IFNULL(new_item_element_name, ''),
		IFNULL(new_derived_from_item_element_id, -1)
	INTO
		new_item_element_name,
		new_derived_from_item_element_id;

	SELECT count(*), id
	INTO row_count, record_found_id
	FROM (
		SELECT	
			IFNULL(name, '') as name,
			id,		
			IFNULL(derived_from_item_element_id, -1) AS derived_from_item_element_id
		FROM item_element
		WHERE parent_item_id=new_parent_item_id
		) AS itemElement2
	WHERE derived_from_item_element_id = new_derived_from_item_element_id
		AND itemElement2.name = new_item_element_name;
	
	RETURN is_record_unique(row_count, existing_item_element_id, record_found_id);	
END//

DROP FUNCTION IF EXISTS check_item_element//
CREATE FUNCTION check_item_element
	(item_element_name VARCHAR(64),
	parent_item_id INT,
	derived_from_item_element_id INT,
	existing_item_element_id INT)
RETURNS BOOLEAN
BEGIN	
	DECLARE result BOOLEAN;
	SET result = is_item_element_attributes_unique(item_element_name, parent_item_id, derived_from_item_element_id, existing_item_element_id); 

	IF not result
	THEN 
		SIGNAL SQLSTATE '45000'
			SET MESSAGE_TEXT = 'Item element is not unque, please update: name, parent item, or derived from item element attribute.';
	END IF;

	RETURN true;
END//

delimiter ;
