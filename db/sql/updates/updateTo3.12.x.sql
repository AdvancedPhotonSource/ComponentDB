--
-- Copyright (c) UChicago Argonne, LLC. All rights reserved.
-- See LICENSE file.
--

-- Execute by running `mysql CDB_DB_NAME --host=127.0.0.1 --user=cdb -p < updateTo3.12.x.sql`

delimiter //

DROP PROCEDURE IF EXISTS fetch_items_with_property_value;//
CREATE PROCEDURE `fetch_items_with_property_value` (IN property_value_id int) 
BEGIN
	SELECT item.* 
	FROM v_item_self_element v_item 
	INNER JOIN item_element_property iep ON iep.item_element_id = v_item.self_element_id 
	INNER JOIN item ON v_item.item_id = item.id
	WHERE iep.property_value_id = property_value_id;
END //

delimiter ;