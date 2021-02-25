--
-- Copyright (c) UChicago Argonne, LLC. All rights reserved.
-- See LICENSE file.
--

delimiter //

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
IN item_name VARCHAR(64), 
IN new_item_identifier1 VARCHAR(32), 
IN new_item_identifier2 VARCHAR(32), 
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
	IN item_identifier1 VARCHAR(32),
	IN item_identifier2 VARCHAR(32),
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
	item_name VARCHAR(64), 
	item_identifier1 VARCHAR(32), 
	item_identifier2 VARCHAR(32), 
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
