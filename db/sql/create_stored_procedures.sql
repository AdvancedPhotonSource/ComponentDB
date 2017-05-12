--
-- Copyright (c) UChicago Argonne, LLC. All rights reserved.
-- See LICENSE file.
--

delimiter //

drop procedure if exists inventory_items_with_avaiable_connector;//
create procedure `inventory_items_with_avaiable_connector`  (IN connector_type_id int, IN is_male tinyint(1)) 
BEGIN  
    SET @query = 'select distinct item.* from item inner join v_item_domain_inventory_connector_status on inv_item_id = item.id where inv_connector_id is null';
    
    SET @query = CONCAT(@query, ' AND connector_type_id = ', connector_type_id);  
        
    SET @query = CONCAT(@query, ' AND is_male = ' , is_male); 

    PREPARE stmt FROM @query;
    EXECUTE stmt;
END //

drop procedure if exists available_connectors_for_inventory_item;//
create procedure `available_connectors_for_inventory_item` (IN item_id int, IN connector_type_id int, IN is_male tinyint(1)) 
BEGIN 
    SET @query = 'select connector.* from v_item_domain_inventory_connector_status ics inner join connector on ics.cat_connector_id = connector.id where ics.second_item_connector_id is null'; SET
    @query = CONCAT(@query, ' AND ics.inv_item_id =', item_id);
    IF (connector_type_id IS NOT NULL) 
    THEN 
        SET @query = CONCAT(@query, ' AND connector.connector_type_id =', connector_type_id); 
    END IF;
     
    IF (is_male IS NOT NULL) 
    THEN 
         SET @query = CONCAT(@query, ' AND connector.is_male =', is_male); 
    END IF;
    PREPARE stmt FROM @query; 
    EXECUTE stmt; 
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

drop procedure if exists is_item_attributes_unique//
create procedure `is_item_attributes_unique` 
(IN domain_id INT, 
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
		WHERE domain_id=domain_id
			AND name = item_name) AS item2
	WHERE derived_from_item_id = new_derived_from_item_id
		AND item_identifier1 = new_item_identifier1
		AND item_identifier2 = new_item_identifier2;

	IF ISNULL(existing_item_id)
	THEN
		SET unique_result = (row_count < 1);
	ELSEIF row_count = 1
	THEN 
		SET unique_result = (existing_item_id = record_found_id);
	ELSE
		SET unique_result = FALSE;
	END IF;
	
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



delimiter ;
