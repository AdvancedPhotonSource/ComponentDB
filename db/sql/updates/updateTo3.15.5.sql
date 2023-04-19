--
-- Copyright (c) UChicago Argonne, LLC. All rights reserved.
-- See LICENSE file.
--

-- Execute by running `mysql CDB_DB_NAME --host=127.0.0.1 --user=cdb -p < updateTo3.15.5.sql`

delimiter //

DROP PROCEDURE IF EXISTS fetch_relationship_children_items;//
CREATE PROCEDURE `fetch_relationship_children_items` (IN item_id INT, IN relationship_type_id INT, IN parent_relationship_id INT) 
BEGIN
	IF(parent_relationship_id IS NULL) THEN
		SELECT item.*, ier.id as parent_relationship_id
		FROM item_element_relationship ier
		INNER JOIN v_item_self_element vitem1 on ier.first_item_element_id = vitem1.self_element_id
		INNER JOIN v_item_self_element vitem2 on ier.second_item_element_id = vitem2.self_element_id
		INNER JOIN item on vitem1.item_id = item.id	
		WHERE ier.relationship_type_id = relationship_type_id and vitem2.item_id = item_id;
	ELSE		
		SELECT item.*, ier.id as parent_relationship_id
		FROM item_element_relationship ier
		INNER JOIN v_item_self_element vitem1 on ier.first_item_element_id = vitem1.self_element_id
		INNER JOIN v_item_self_element vitem2 on ier.second_item_element_id = vitem2.self_element_id
		INNER JOIN item on vitem1.item_id = item.id	
		WHERE ier.relationship_type_id = relationship_type_id and vitem2.item_id = item_id
		AND (ier.relationship_id_for_parent = parent_relationship_id or ier.relationship_id_for_parent is null);
	END IF;	
END //

DROP PROCEDURE IF EXISTS fetch_relationship_parent_items;//
CREATE PROCEDURE `fetch_relationship_parent_items` (IN item_id int, IN relationship_type_id int, IN parent_relationship_id int) 
BEGIN   	
	IF (parent_relationship_id is NULL) THEN		
		SELECT item.*, ier.relationship_id_for_parent as parent_relationship_id
		FROM item_element_relationship ier
		INNER JOIN v_item_self_element vitem1 on ier.first_item_element_id = vitem1.self_element_id
		INNER JOIN v_item_self_element vitem2 on ier.second_item_element_id = vitem2.self_element_id
		INNER JOIN item on vitem2.item_id = item.id	
		WHERE ier.relationship_type_id = relationship_type_id 
		AND vitem1.item_id = item_id ;
	ELSE
		SELECT item.*, ier.relationship_id_for_parent as parent_relationship_id
		FROM item_element_relationship ier
		INNER JOIN v_item_self_element vitem1 on ier.first_item_element_id = vitem1.self_element_id
		INNER JOIN v_item_self_element vitem2 on ier.second_item_element_id = vitem2.self_element_id
		INNER JOIN item on vitem2.item_id = item.id	
		WHERE ier.relationship_type_id = relationship_type_id 
		AND vitem1.item_id = item_id
		AND ier.id = parent_relationship_id; 
	END IF; 
END //
